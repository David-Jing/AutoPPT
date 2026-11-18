# AutoPPT: High-Level Design (Draft v0.5)

Status: draft for review. Input to AI-DLC Inception (requirements, user stories, application design).
Legacy reference: [David-Jing/AutoPPTMaker](https://github.com/David-Jing/AutoPPTMaker) (Python, v2.4.0).
App repo: `AutoPPT` (this repo, public). Data repo: `AutoPPTData` (`/Users/david/Documents/AutoPPTData`, private).
Reference decks: "Sunday Worship Slides September 27th 2026 – Regular" (60 slides) and "October 4th 2026 – Regular" (58 slides).

---

## 0. Decisions

| # | Decision |
|---|---|
| D1 | Java 21 (LTS), Gradle 9.x, Swing + FlatLaf. Windows and macOS. Java 21 rather than 25 because Guice 7 runs on it without warnings or workarounds (§9.1). |
| D2 | One output: the **Regular** template. Projected and Stream are deprecated. |
| D3 | Scripture always uses the multi-slide layout. Passages are flattened into running text (no poetry line breaks). |
| D4 | Next Week's Schedule takes a name for each role, as well as the date. An empty role prints "N/A". |
| D5 | Users are not technical. The UI never says "GitHub", "repo", "commit", "API" or "token" (§5). |
| D6 | English Bible text comes from the **ESV API**. No "(ESV)" mark is shown. **Accepted risk:** the [ESV conditions of use](https://api.esv.org/) require "ESV" with each quotation, so Crossway could revoke the key. |
| D7 | The developer hands each user a **setup file** containing every key. The app asks for it on first start. All users share one ESV key. **Accepted risk:** the ESV conditions forbid sharing a key, and revoking it stops scripture for every user at once. |
| D8 | Hymns and config live in the separate private repo `AutoPPTData`. |
| D9 | Announcements, supplications, catechism and Spiritual Wisdom are written by the user in the app (title + Markdown text). Nothing for them is stored centrally. Google Sheets is not used. |
| D10 | Users sign in with personal Gmail accounts. |
| D11 | No CCLI numbers, copyright lines or other song credits on hymn slides, and no credits slide. |
| D12 | "Delete last week's slides" is on by default and can be turned off. It only removes the user's previous deck, never the one just made (F2). |
| D13 | Hymn "Discard my changes" only discards unsaved local edits. Users cannot restore older versions. |
| D14 | Chinese defaults to **Traditional**: Chinese UI text, plus a separate Chinese template (possibly bilingual). Designed now, built later (§11). |
| D15 | No preview in the app. After generation the deck opens in the browser for checking. |
| D16 | No installer: each platform ships as an unsigned, zipped app that runs on double-click, with Java bundled. macOS builds for both Apple Silicon and Intel (F11). |
| D17 | Built-in Google Slides features are used wherever they meet a formatting rule. Only the gaps get custom code (§7). |
| D18 | No remote logging. Errors are shown to the user, who contacts the developer. |
| D19 | The template and slide types are maintained by the developer and are read-only in the app. |
| D20 | The template may only use fonts the app can measure (§7.4). Century Gothic stays, measured with TeX Gyre Adventor, a free font expected to have matching widths; spike S1 confirms it. |
| D21 | The Chinese Monthly Scripture picture is not generated. The app makes the slide with its title and an empty body, and tells the user to add the picture after generation. |
| D22 | Dependency injection uses Google Guice 7, with constructor injection only and no method interception (§9.1). |
| D23 | Each service is built from the template and its own inputs only. Nothing carries over from earlier services or decks; the user enters every week's settings, including "next week" rows. |
| D24 | Hymn saves: the first save wins. A later save based on an older copy is refused, and the user is told to check for updates (§8.3). |
| D25 | Keys and the Google refresh token are saved in a file in the app-data folder, readable only by the user. No OS keychain is used. |
| D26 | The legacy Python app is a reference, not a specification. Its business logic and heuristics (pagination, line splitting, lyric cleanup, ordering) may be changed or replaced wherever a better approach meets the same goals. The rules in this doc and the reference decks define the expected result, not the legacy code. |

---

## 1. Purpose

AutoPPT builds the weekly Sunday worship deck for FCNABC's English congregation. The operator enters the week's details, and the app produces a finished Google Slides deck from the church's template. The details include:
- passages, hymns, announcements and catechism;
- the sermon title and speaker;
- next week's roster.

The rewrite replaces the legacy Python console app, which was driven by a hand-edited `.ini` file.

---

## 2. Legacy behaviour and the target deck

### 2.1 Kept, changed, dropped

| Legacy behaviour | New design |
|---|---|
| Three output variants | **Changed:** Regular only (D2) |
| Ordering presets `0` Normal and `1` Holy Communion | **Kept** as starting points. Users can then reorder, add and remove slides (F1). |
| Fixed Hymn 1–4 slots | **Changed:** any number of hymns |
| Title + date headers with a superscript ordinal ("September 27th, 2026", with "th" raised) | **Kept** (§7.1) |
| Single-slide scripture (Monthly Scripture) | **Changed:** multi-slide only (D3) |
| ESV API, key hard-coded | **Kept** the API; the key moves into the setup file |
| Hymns in SQLite on Google Drive | **Changed:** one file per hymn in `AutoPPTData` |
| Announcements and supplications from a Google Sheet | **Changed:** written in the app (D9) |
| Catechism slide with a title only | **Changed:** user-written title plus Q&A body (F4a) |
| Static slides copied from the template | **Kept** |
| Schedule fills only `{Date}` | **Changed:** every role is filled (F7) |
| Random hymn backgrounds | **Kept** |
| Template slides found by numeric index | **Changed:** found by a tag (F6) |
| Style values set by code from `.ini` files | **Changed:** the template supplies the styles (§7) |
| Text measured with Times-Roman metrics, although the template is in Century Gothic | **Changed:** measured with a font whose widths match the template's font (§7.4) |
| Generated deck IDs kept in `SlideIDList.txt` and deleted next run | **Changed:** the previous deck is found through a Drive file property and moved to Trash; optional (D12) |
| Logging to a Google Sheet | **Dropped** |

### 2.2 Slides added by hand today (now generated)

| Deck slide | Content | New slide type |
|---|---|---|
| 0 | Optional title ("PLEASE KEEP SILENT AND PREPARE YOUR HEARTS FOR WORSHIP"), a passage with verse numbers ("12 Again Jesus spoke…"), and the reference in its own box ("John 8:12") | **Opening Passage** (F1). Chosen by hand; can be several verses. |
| 3 | Chinese Monthly Scripture: a pinyin picture pasted from christunite.com (4 Oct), or plain Chinese text under 本月金句 (27 Sep) | **Chinese Monthly Scripture** (F12): the title only, with a reminder to add the content after generation |
| 11–14 | Catechism title plus Q&A body ("Q71: What is required…" / answer) | **Catechism** (F4a): the user writes the title and body |
| 15 | "SPIRITUAL WISDOM AND PROFOUND SAYINGS": quote paragraphs, then the source ("— Imitation of Christ by Thomas Kempis") pushed to the right. It can run over several slides. It is missing from the 4 Oct deck by mistake. | **Spiritual Wisdom** (F5a) |

Other conventions taken from the decks:
- long announcements continue on a second slide at a sentence boundary;
- Psalm poetry is flattened (D3);
- empty schedule roles print "N/A";
- a hymn's slide count covers all its slides. In the 4 Oct deck, "HE WILL HOLD ME FAST (1/1)" on five slides was a manual error; it should read (1/5)…(5/5) (H5).

### 2.3 Regular template slides after the developer's update

The developer prepends the Opening Passage template as slide 0, and every other slide moves down by one. The app finds slides by tag (F6), so the shift needs no code change.

| Slides | Purpose | Tag | Required placeholders |
|---|---|---|---|
| 0 | Opening Passage (new) | `autoppt:opening-passage` | `{Title}`, `{Text}`, `{Reference}` |
| 1–5, 13 | Title + text layouts used by headers, scripture, announcements, catechism, Spiritual Wisdom, Chinese Monthly Scripture and others | `autoppt:title-text …` (one tag per layout) | `{Title}`, `{Text}` |
| 6–7, 8–10, 11–12, 14, 15, 16 | Static: Lord's Prayer, Holy Communion, Apostles' Creed, Offering, Doxology, Benediction | `autoppt:static <name>` | none |
| 17 | Next Week's Schedule table | `autoppt:schedule` | `{Date}` and the role placeholders (F7) |
| 18–33 | Hymn backgrounds | `autoppt:hymn-background` | `{Title}`, `{Text}`, styled like the title + text layouts |

Every hymn background carries its own styled `{Title}` and `{Text}` boxes, so hymn styles come from the template too (D17). Tags are unique, except `autoppt:hymn-background`, which marks a pool.

---

## 3. Goals and non-goals

**Goals**
- G1. A non-technical operator builds the Sunday deck in under 10 minutes. Only the Chinese Monthly Scripture picture is added by hand.
- G2. Verse, hymn and text slides follow §7 and need no touch-up in most weeks.
- G3. Users can edit and add hymns and share them with everyone from inside the app.
- G4. Template changes (moving slides, new backgrounds, new schedule roles) need no code change.
- G5. One codebase builds a ready-to-run app for Windows and macOS.
- G6. The design allows a Traditional-Chinese UI and template to be added without restructuring.

**Non-goals (v1)**
- Presenting, or editing slides after generation (done in Google Slides).
- Previewing slides in the app.
- Generating the Chinese pinyin picture.
- Creating slide types or editing the template in the app.
- Built-out Chinese support (§11 is the design only).
- Restoring older hymn versions, merging concurrent edits, remote logging, Google Sheets input, code signing.

---

## 4. Users and roles

| Role | Does | Where |
|---|---|---|
| Operator | Builds the weekly service, makes the slides, and adds the Chinese scripture picture | App and Google Slides (fewer than 10 people) |
| Hymn editor | Edits and adds hymns and shares them | App; the same people as Operators |
| Developer | Maintains the template, slide types, presets, measurement fonts and setup files; fixes reported errors | Google Slides, `AutoPPTData`, this repo |

---

## 5. Plain-language vocabulary

| Internal | Shown to users |
|---|---|
| Data repo / GitHub | "shared library" |
| Pull / sync | "Check for updates" (also runs automatically on start) |
| Commit + push | "Save for everyone" |
| Delete file + push | "Delete for everyone" |
| Revert local edits | "Discard my changes" |
| Edited locally, not pushed | "Only on this computer" badge |
| Push rejected (sha mismatch or file exists) | "Someone else saved this hymn first. Choose Check for updates, then make your change again." |
| Key file | "Setup file" |
| OAuth sign-in | "Sign in with Google" |
| Section / section type | "Slide" / "Slide type" ("Add slide → Hymn") |
| Error details | "Copy details for support" |

---

## 6. Features

Priority: **M** = MVP, **L** = later.

### F0. Start-up and internet connection (M)

An internet connection is required to use the app. Google Slides, the ESV API and the shared library are all online, so there is no offline mode.

- **Start-up screen.** Every launch opens a small start-up screen with the app name, the version, and one status line at a time:
  1. "Checking your internet connection…"
  2. "Checking for updates…": a newer app version (F11), and, except on first start, the shared library. On first start there are no keys yet, so the shared library is loaded in the wizard's last step (F8).
  3. "Getting ready…" (loading the saved keys; skipped on first start)

  It then opens the first-start wizard (F8) on first start, or the main window otherwise.
- **No connection at start-up.** The start-up screen stops at step 1 and shows "You're not connected to the internet. Connect, then choose Try again." with **Try again** (the default button) and **Quit**; Escape does nothing. Nothing else opens until the check passes, and the app re-checks by itself every 5 seconds.
- **Connection lost while the app is open.** The main window is covered by the same message. Typed work stays where it is (the service autosaves locally, F1). Any action that was running (making slides, a hymn save) reports Problem with "The internet connection was lost" instead of a generic error. When the connection returns, the cover closes by itself.
- **During the first-start wizard,** a lost connection shows the same cover; the wizard stays on the same step and keeps what was typed.
- **How it checks.** A short HTTPS request to a Google endpoint with a 5-second timeout; while not connected, it re-checks every 5 seconds. A captive portal or blocked network counts as "not connected".

### F1. Service builder (M)

The main window is a master–detail view. The left side lists the service's slides in the order they will be generated. Selecting one shows its settings on the right.

```
┌ AutoPPT ─────────────────────────────────────────────────────────────────────┐
│ Hi Grace   Sunday 22 Nov 2026 ▾   Order: Normal ▾             [Make slides ▸]│
├──────────────────────────────┬───────────────────────────────────────────────┤
│ ≡ Opening Passage  John 8:12 │ Hymn                                          │
│ ≡ Sunday Service Header      │ ┌───────────────────────────────────────────┐ │
│ ≡ Monthly Scripture          │ │ Love Incarnate                         ▾  │ │
│ ≡ Chinese Monthly Scripture ✎│ └───────────────────────────────────────────┘ │
│ ≡ Announcements          (5) │ Lyrics              ● Only on this computer   │
│ ≡ Catechism                  │ ┌───────────────────────────────────────────┐ │
│ ≡ Spiritual Wisdom           │ │ [Verse 1]                                 │ │
│ ≡ Worship Header             │ │ Love incarnate, Love divine …             │ │
│▶≡ Hymn: Love Incarnate       │ └───────────────────────────────────────────┘ │
│ ⚠ Hymn: (none chosen)        │ [Discard my changes]   [Save for everyone]    │
│ ≡ Next Week's Schedule       │                                               │
│ [+ Add slide ▾]  [Remove]    │                                               │
└──────────────────────────────┴───────────────────────────────────────────────┘
```
(✎ = the user finishes this slide in Google Slides.)

- **Greeting.** The top bar greets the user by the name entered at first start ("Hi Grace"). The name can be changed in Settings (F10).
- **Service.** The date defaults to the coming Sunday: today if today is Sunday, otherwise the next Sunday. Choosing an order (Normal, Holy Communion) fills the list with that preset's default slides.
- **Reorder.** Drag a row by its ≡ handle, or use Move up / Move down (Alt+↑/↓).
- **Add.** "+ Add slide" lists the slide types from config. Any type can appear more than once.
- **Remove.** Removes a row from this service only. New slide types cannot be created (D19).
- **Row status.** Each row shows a short summary. A row that cannot be generated shows ⚠ with a plain reason ("Choose a hymn", "We couldn't find 'Jhon 3:16'").
- **Autosave.** The service being edited is saved locally, so closing the app loses nothing. "New service" starts again from the chosen preset with empty settings. Nothing is copied from earlier services or decks (D23).
- **Settings for each slide type:**

| Slide type | Settings |
|---|---|
| Opening Passage | Passage field (one or more verses) and an optional title. Text goes in the body and the reference in its own box. Long passages continue onto more copies of the same layout; the reference box is filled on the last slide only and emptied on the others. |
| Bible Passage (Call to Worship, Monthly Scripture, Memorization this/next week, Prayer of Confession, Sermon Passage) | Passage field, optional title, and a "show the reference after the passage" toggle. Shows the passage text from the ESV API, or a plain error. Separate passages are split with `;` only (F4). A help tooltip on the field shows what works ("Genesis 1:1-10", "John 3:16, 18", "Psalm 23; Romans 8:28") and what doesn't (Chinese book names, other translations). |
| Chinese Monthly Scripture | Title only (default 本月金句). Note in the panel: "This slide is made with an empty body. Add the Chinese verse picture in Google Slides after making the slides." (F12) |
| Hymn | Searchable dropdown of shared hymns (title, alternate titles, first line). Selecting one fills the lyrics editor (F3). |
| Announcements / Supplications | An ordered list of text blocks, one per item, with add, remove and reorder; a title; formatting buttons for bold, italic, list and link (F5). |
| Next Week rows (Memorization next week, Catechism next week) | Entered each week like any other row. |
| Catechism | Title and body, written by the user (F4a). |
| Spiritual Wisdom | Title, quote text (several paragraphs) and source line (F5a). |
| Header / Sermon Header | Title (plus speaker). The date fills in automatically. |
| Next Week's Schedule | The date, plus one name field per role in the template (F7). |
| Static (Lord's Prayer, Creed, Holy Communion, Offering, Doxology, Benediction) | No settings: "This slide is copied as-is." |

### F2. Make slides (M)
- "Make slides" builds a new deck from the template every time, then opens it in the default browser (D15). The link can also be copied. The button is disabled while a run is in progress.
- **Assembly order.**
  1. Drive `files.copy` copies the template into the user's Drive and sets `appProperties.autoppt=deck` on the copy.
  2. For each row in list order, the slide with the row's tag is duplicated (`duplicateObject`). A row that spans several slides gets one duplicate per slide. Hymns use one background per hymn (H6).
  3. Placeholders and styles are filled with one `batchUpdate` per batch of slides.
  4. Speaker notes on every generated slide are cleared, so tags don't show in Presenter View. Only the F12 reminder is written back.
  5. `updateSlidesPosition` puts the generated slides in list order.
  6. Every original template slide (layouts, static slides and hymn backgrounds) is deleted, leaving only the service.
- The progress view shows each row as Done, Problem (plain reason), Skipped, or **To do in Google Slides**. The last one is used for the Chinese Monthly Scripture, with the slide number. One failed row does not stop the run. Its slides are left out and the row shows the reason.
- **"Delete last week's slides when I make new ones"** (D12), on by default.
  - After the new deck is made, the app looks for the user's most recent earlier deck: owned by the user, with `appProperties.autoppt=deck`, and not the deck just made. Only that one deck is moved to the Drive Trash. Decks shared by others are never touched.
  - The end-of-run summary names it: "Last week's slides ('…') were moved to the Trash. You can restore them from there for 30 days."
- **Template access.** Before the first run, and when a copy fails, the app checks it can read the template. A 403/404 maps to "Ask the developer to share the slide template with your Google account and allow copying."

### F3. Hymn editing and sharing (M)
- Choosing a hymn fills the editor with the shared version. Edits apply to this service immediately, shared or not.
- **Confirmations.** "Save for everyone", "Discard my changes" and "Delete for everyone" each ask first, naming the hymn and what will happen (for example "Save your changes to 'Love Incarnate' for everyone?"). **Cancel** is the default button and Escape cancels, so a stray Enter never saves, discards or deletes. Cancel leaves everything as it was.
- **Save for everyone.** Writes the hymn to `AutoPPTData` with a message such as "Update hymn: Love Incarnate (by Grace)".
- **Discard my changes.** Returns to the shared version (D13).
- **New hymn….** The last dropdown entry. Takes a title and pasted lyrics, auto-split into blocks on blank lines and `[Chorus]`-style headers.
- **Updates.** The shared library is refreshed on start and on "Check for updates". The local copy is a cache that makes start-up fast; the app does not run offline (F0).
- **Conflict.** The first save wins (D24). A later save based on an older copy, or a new hymn whose id already exists, is refused with "Someone else saved this hymn first. Choose Check for updates, then make your change again." (§8.3).
- **New hymn id.** Derived from the title (`love-incarnate`). If that id already exists in the library, a number is added (`love-incarnate-2`).
- **Migration.** A one-time developer tool converts the legacy `HymnDatabase.db` (116 hymns).
- **Delete for everyone.** Removes a hymn from the shared library after a confirmation ("Delete 'Love Incarnate' for everyone?"). It is a Contents API `DELETE` with the last-known `sha`, so the first-save-wins rule (D24) applies: if someone changed the hymn first, the delete is refused with the same "Check for updates" message. A service row already using the hymn keeps its lyrics for that service. Users cannot undo a delete (D13); the developer can restore it from history.

### F4. Bible passages (M)
- Text comes from the ESV API `/v3/passage/text/`. The API parses references itself (`John 1:1`, `jn11.35`, `Genesis 1-3`, `John 3:16, 18`) and returns the canonical reference, so the app does not build an English reference parser.
- The passage field is split on `;` only. Each piece is sent as one `q`, and its `passage_meta[].canonical` becomes the reference line. Commas stay inside a piece, where the ESV API reads them as a verse list.
- Request options:
  - headings, footnotes, passage references, short copyright and Selahs off;
  - verse numbers on;
  - `indent-poetry` off and `line-length=0`. The app collapses the remaining line breaks within a verse (D3).
- **ESV limits:**
  - 500 verses per query, or half a book;
  - 5,000 queries a day, 1,000 an hour, 60 a minute;
  - no more than 500 verses stored locally ([ESV API](https://api.esv.org/)).

  So passage text is kept in memory for the session only. The service file stores references, not text.

### F4a. Catechism (M)
- The user writes the full title (e.g. "THE WESTMINSTER SHORTER CATECHISM Q&A #71") and the body, in the same block editor as announcements. For example:
  - "Q71: What is required in the seventh commandment?"
  - then the answer.
- Nothing is stored centrally, so any catechism can be used.
- There are two slide rows: Catechism (this week) and Catechism (next week). Both are written each week (D23).
- Overflow continues on the next slide (T1).

### F5. Announcements and supplications (M)
- Ordered text blocks, one per item. Supported formatting: bold, italic, bullet and numbered lists, line breaks and links, stored as CommonMark. Underline is not offered, because CommonMark has no syntax for it. Anything else is shown as plain text, with a warning.
- **Announcements**: one per slide. A long one continues onto another slide with the same title, split at the last sentence that fits, otherwise at a word.
- **Supplications**: numbered items on as few slides as possible.

### F5a. Spiritual Wisdom (M)
- Fields: title (default "SPIRITUAL WISDOM AND PROFOUND SAYINGS"), quote (several paragraphs, same editor as announcements) and source ("Imitation of Christ by Thomas Kempis").
- Quote paragraphs are separated by a gap and continue onto more slides when needed.
- The source line goes after the last paragraph on the last slide, prefixed with "— ", placed toward the right (§7.1). Today this is done with five tab characters.

### F6. Template and configuration (M; developer-maintained)
- `config/app.yaml` names the template presentation ID. It holds a `templates` list so a Chinese template can be added later (§11).
- Template slides are found by a **tag** in their speaker notes (§2.3). The app clears notes in the generated deck (F2), so tags never reach Presenter View. The placeholders `{Title}`, `{Text}`, `{Reference}` and the schedule roles stay as they are.
- `slide-types.yaml` defines each slide type: its template tag, layout kind, default title and editor. Layout kinds: passage, hymn, text-blocks, quote, header, static, schedule, title-only. "+ Add slide" is built from this file.
- `presets/*.yaml` lists the default slides for each order. Normal includes Opening Passage, Chinese Monthly Scripture, the catechism rows and Spiritual Wisdom.
- `layout.yaml` holds values the template cannot express: the gap between verses, the measurement safety margin, the "N/A" text and the source-line indent.
- `fonts.yaml` maps each template font to its measurement font (§7.4).
- A developer-only "Check template" command confirms that:
  - every tag in `slide-types.yaml` exists, and is unique except the hymn-background pool;
  - each tagged slide has the placeholders its layout kind needs (§2.3);
  - there is at least one hymn background;
  - **every font used in the template has a measurement font**. A font without one is an error.
- Template edits are made on a copy. Once "Check template" passes, the copy's id replaces the old one in `app.yaml`, so no one generates from a half-edited template.

### F7. Next Week's Schedule (M)
- The table has role labels in the left column and placeholders in the right: `{Speaker}`, `{Leader}`, `{Accompanist}`, `{Audio}`, `{Streaming}`, `{Announcer}`, `{UsherAltar}`, `{Offering}`, `{Welcome}`.
- The editor is **built from the template**: one name field per placeholder, labelled with the left-column text. A new role in the template becomes a new field automatically.
- `{Date}` is the Sunday after the service date, with the superscript ordinal (§7.1).
- Fields suggest names used before. An empty field prints "N/A".
- Values are written with `replaceAllText`, which keeps the cell formatting.

### F8. First start and setup file (M)
The setup screens appear only on first start, or when the saved keys are missing or replaced. They are a step-by-step wizard: one step per screen, with Back and Next and a "Step n of N" marker (setup file, your name, sign in with Google, getting the shared library ready). Checking and saving the keys happen within the setup-file step. Later starts open the main window directly.

1. "Drop your setup file here" (drag and drop or Browse). The file (e.g. `fcnabc.autoppt`) holds:
   - the Google OAuth client ID and secret;
   - the ESV API key;
   - the shared-library token and its repo name.
2. The app checks the ESV key and the shared-library token with a cheap call each, and names the one that failed, in plain words. The Google client can only be checked by signing in (step 5); a failure there maps to "This setup file doesn't work with Google sign-in. Ask the developer for a new one."
3. Keys are saved to `secrets.json` in the app-data folder (D25). On macOS the file is set to `0600`; on Windows its ACL is limited to the current user. The app then offers "Delete the setup file now". Settings → "Replace setup file" accepts a new one.
4. The user enters their name, used in hymn save messages.
5. "Sign in with Google" opens the browser (loopback redirect). The refresh token is saved in the same `secrets.json`.
6. The app downloads the shared library.

### F9. Errors (M)
- A local rolling log file sits in the OS app-data folder. Nothing is sent anywhere.
- Problems with what the user typed show on the row (⚠) before "Make slides".
- Runtime problems open a dialog with:
  - a plain summary and what to try;
  - "Copy details for support": version, OS, Java version, error code, stack trace and the affected row;
  - "Open log folder".
- **Redaction.** One redactor runs before anything reaches the log file or the details text. It removes:
  - `Authorization` header values (`Token …` for ESV, `Bearer …` for the shared library);
  - the OAuth client secret, refresh and access tokens (`1//…`, `ya29.…`), and `key`, `token`, `code` and `client_secret` query or form values;
  - any value read from the setup file.

  Passage text is not logged.
- **Keys that stop working.** No key is set to expire (§10), but any can be revoked or disabled. The app maps each case to one plain message instead of a generic error:
  - ESV key or shared-library token rejected (401/403): "Ask the developer for a new setup file."
  - Google sign-in no longer valid (`invalid_grant`): the app asks the user to sign in with Google again.
  - Google OAuth client disabled or deleted (`invalid_client` / `disabled_client`): "Ask the developer for a new setup file."

### F10. Settings (M)
- Delete last week's slides (on/off).
- Replace setup file; Sign out of Google; Your name.
- Theme (light / dark).
- Language: the selector is hidden until §11 is built.
- **About:** the app version (e.g. "AutoPPT 1.0.0"), shown at the bottom of Settings. The same version appears on the start-up screen and in "Copy details for support" (F9).

### F11. Distribution and update check (M)
- `jpackage --type app-image` (JDK 21) builds a ready-to-run app with a bundled, trimmed Java runtime. Nothing is installed:
  - **Windows:** an `AutoPPT/` folder holding `AutoPPT.exe`, `app/` and `runtime/`, shipped as `AutoPPT-<version>-windows-x64.zip`. The user unzips it once and double-clicks `AutoPPT.exe` (a desktop shortcut works; the `.exe` alone does not run without its folder).
  - **macOS:** `AutoPPT.app`, shipped as `AutoPPT-<version>-macos-<arm64|x64>.zip`. The user unzips it and double-clicks the app.
- No `.msi` or `.dmg`, so no WiX Toolset is needed. Settings and secrets live in the OS app-data folder, not next to the app, so replacing the folder with a new version keeps them.
- `jpackage` cannot cross-compile, so each target runs on its own GitHub Actions runner:

  | Target | Runner |
  |---|---|
  | Windows x64 | `windows-latest` |
  | macOS Apple Silicon | `macos-latest` |
  | macOS Intel | `macos-15-intel` |

  `macos-15-intel` is GitHub's last x86_64 image and is available until August 2027 ([actions/runner-images#13045](https://github.com/actions/runner-images/issues/13045)). After that, Intel builds stop and Apple Silicon remains (D16).
- Unsigned. A short "first run" guide covers Windows SmartScreen ("More info → Run anyway") and macOS (open once, then Privacy & Security → "Open Anyway").
- **Update check.** During start-up (F0) the app makes one unauthenticated request to GitHub's public "latest release" endpoint for `AutoPPT` and compares its version tag with its own. If the release is newer, the start-up screen shows "A new version of AutoPPT is available (1.1.0)" with **Download** and **Continue**. Download opens the release's zip for this computer (Windows, Apple Silicon or Intel Mac, chosen from `os.name` and `os.arch`) in the browser; the user unzips it and replaces the old app by hand. Continue opens the app as usual. If the check fails or takes longer than 5 seconds, start-up carries on silently. Only the latest published release counts (the endpoint already ignores drafts and pre-releases), and "newer" means a higher `major.minor.patch` version. On this screen **Continue** is the default button and Escape means Continue.
- The app never downloads or replaces itself.

### F12. Chinese Monthly Scripture (M)
- The app creates the slide from a title + text layout. The title is set (default 本月金句) and the body is left empty (D21).
- The progress view and the end-of-run summary list it as "To do in Google Slides: add the Chinese verse picture to slide N", and a reminder is placed in the slide's speaker notes.
- The layout kind is **title-only**, so the developer can reuse it for any other slide that is finished by hand.

---

## 7. Formatting rules: built-in vs. custom

### 7.1 Built into Google Slides (use it, don't rebuild it)

| Need | Built-in feature | Replaces legacy code |
|---|---|---|
| Fonts, sizes, bold, colour, alignment and line spacing | Template formatting. `replaceAllText` keeps the placeholder's style, and new paragraphs inherit it. | Every `*TextSize`, `*Bolded` and `*Alignment` `.ini` key, plus the style calls |
| Gaps between verses, hymn blocks, items and quote paragraphs | `updateParagraphStyle` `spaceAbove` / `spaceBelow` | Blank-line spacing |
| Superscript verse numbers and date ordinals | `updateTextStyle` `baselineOffset: SUPERSCRIPT` on plain characters. Unicode superscript letters such as `ᵗʰ` are not used: the template font may not have them, and a fallback font would break measurement. The suffix is "th" for 11–13 and any number ending 0 or 4–9, otherwise "st" (1), "nd" (2) or "rd" (3): 1st, 2nd, 3rd, 4th, 11th, 12th, 13th, 21st, 22nd, 23rd. | Kept, simplified |
| Hanging indent so the verse number stands out | `updateParagraphStyle` `indentStart` + `indentFirstLine` | Superscripted-space padding |
| Reference and source lines toward the right ("—Hebrews 11:6", "— Imitation of Christ…") | `updateParagraphStyle` `alignment: END`, or a large `indentStart` from `layout.yaml` for the current look | Computed padding spaces; tab characters |
| Numbered supplications, bullets in announcements | `createParagraphBullets` (`NUMBERED_DIGIT_PERIOD`, `BULLET_DISC_CIRCLE_SQUARE`) | `ListMaker` numbering and indent |
| Bold, italic and links | `updateTextStyle` | n/a |
| Copying, duplicating, reordering and deleting slides | Drive `files.copy`; Slides `duplicateObject`, `updateSlidesPosition`, `deleteObject` | Kept |
| Schedule table | `replaceAllText` limited to the schedule slide | `setTextInTable` and its style calls |
| Reminder for slides finished by hand | Speaker notes text | n/a |
| Finding the previous deck | Drive `appProperties` + `files.list` (owned by the user, newest first); move to Trash | `SlideIDList.txt` |
| Parsing English Bible references | ESV API `q` parsing and `canonical` | n/a |

### 7.2 No built-in; custom code

| Need | Why no built-in fits | Custom piece |
|---|---|---|
| **Overflow to the next slide** (passages, hymns, announcements, catechism, quotes) | "Shrink on overflow" is reset to off by any API text change ([Apps Script Autofit](https://developers.google.com/apps-script/reference/slides/autofit)). The API never reports line counts or overflow. | **Paginator**: measures text locally with the measurement font (§7.4), with kerning on (`TextAttribute.KERNING`). It reads `lineSpacing`, `spaceAbove` and `spaceBelow` from the template, and measures against the text-box size minus Slides' fixed inner padding, which the API does not expose (S1 measures it). It then picks the slide breaks. |
| **Balanced slides** (14 lines with room for 10 → 7 + 7) | none | Part of the paginator (starting from the legacy heuristic; may be improved, D26) |
| **Uniform hymn line length** | Slides only soft-wraps | Line splitter: split at `,` `;` `!` `.` in the 40–80% range, otherwise at the space nearest the middle |
| **Hymn repeats** ("(x2)") and merging short blocks | none | `LyricsCleaner`, based on the legacy `HymnMaker` (D26) |
| **Long hymn titles** | none | Measure the title; use a second size, or move the body down (legacy rule; may be improved, D26) |
| **Markdown → styled text** | Slides has no Markdown import | commonmark-java parses it; the result maps onto §7.1 requests |

### 7.3 Rules

**Passages** (Opening Passage, all Bible Passage types)
- V1. Each verse is one paragraph, flattened (D3). A gap separates verses.
- V2. A verse that does not fit starts on the next slide. A verse is only split if it alone is taller than a slide, and then at a line boundary.
- V3. Slides are balanced. The title repeats on each slide.
- V4. Verse numbers are superscript.
- V5. Hanging indent: continuation lines line up with the start of the first line's text. Spike S2 decides between a tab and spacing after the number.
- V6. The optional reference line ("—Romans 8:13-14") follows the last verse, placed toward the right. The Opening Passage puts its reference in its own box instead.
- V7. Several passages in one field each start a new slide.

**Hymns**
- H1. A gap separates blocks that share a slide.
- H2. A block that does not fit moves to the next slide. A block taller than a slide is split evenly.
- H3. Lines are split so their lengths on a slide stay similar. Text is never left to soft-wrap.
- H4. Repeats collapse to "(x2)". Adjacent short blocks merge when they fit.
- H5. Slide titles read "TITLE (n/N)", where N is the total number of slides for that hymn: (1/5), (2/5) … (5/5). Long titles are handled as in §7.2.
- H6. Each hymn gets one background, chosen at random and shared by all its slides. No background repeats within a service until every one in the pool has been used; after that, picks start again from the full pool.

**Text blocks** (announcements, supplications, catechism, Spiritual Wisdom)
- T1. A gap separates paragraphs. Overflow continues on the next slide at a sentence boundary, otherwise at a word, and the title repeats.
- T2. Lists use Google's bullets and numbering.
- T3. The Spiritual Wisdom source line comes last, placed toward the right (F5a).

### 7.4 Fonts: what can be measured

The paginator is only as accurate as its font metrics. A font is **measurable** when both of these hold:
1. Google Slides offers it, so the template can use it.
2. The app can legally bundle either the same font file or a free font designed to the **same advance widths**. A clone's match is a hypothesis until S1 confirms it against Slides.

The measurement font is bundled in the app (`app/src/main/resources/fonts/`) and selected through `fonts.yaml`. The OS's installed fonts are never used, because they differ between machines. Only fonts the template uses today are bundled; the table below is the rule for adding more.

| Template font (in Slides) | Measured with | Licence | Notes |
|---|---|---|---|
| **Century Gothic** (current; bundled) | **TeX Gyre Adventor** (regular, bold, italic, bold italic) | GUST Font License (free; redistribution allowed) | Monotype drew Century Gothic to match the widths of ITC Avant Garde Gothic, so documents could switch between them with no change to copyfit ([Wikipedia](https://en.wikipedia.org/wiki/Century_Gothic)). TeX Gyre Adventor is based on URW Gothic L, the standard Avant Garde substitute ([CTAN](https://ctan.org/pkg/tex-gyre-adventor)). Bold mapping (Century Gothic Bold ↔ Adventor Bold, which is Avant Garde Demi) and kerning are checked in S1. |
| Times New Roman (bundled) | Tinos (or Liberation Serif) | Apache 2.0 / OFL | Metric-compatible clone; the template uses it in a few places |
| Arial (not bundled) | Arimo (or Liberation Sans) | Apache 2.0 / OFL | Metric-compatible clone; bundle if the template starts using it |
| Calibri / Cambria / Georgia (not bundled) | Carlito / Caladea / Gelasio | OFL | Metric-compatible clones; bundle if the template starts using them |
| Any Google Fonts family (e.g. Questrial, Montserrat, Poppins) | The same file from Google Fonts | OFL / Apache 2.0 | Exact match; the safest choice for new slide types |
| **Noto Sans TC / Noto Serif TC** (Chinese template, later) | The same file | OFL | Traditional Chinese (§11) |
| Other commercial fonts without a free metric clone | – | – | **Not allowed** in the template. "Check template" flags them (F6). |

**Fallback.** If S1 shows Adventor drifting too far from Century Gothic in Slides, the template's text boxes switch to a Google Fonts family with a similar look, such as Questrial or Didact Gothic. Those are measured with their exact files.

---

## 8. Data

### 8.1 `AutoPPTData` layout

```
AutoPPTData/                          (private)
  hymns/<id>.md                       # one file per hymn
  config/
    app.yaml                          # schemaVersion, templates[{id, language, presentationId}]
    slide-types.yaml                  # tag, layout kind, editor, default titles
    presets/normal.yaml
    presets/holy-communion.yaml
    layout.yaml                       # verse gap, safety margin, "N/A", source-line indent
    fonts.yaml                        # template font → bundled measurement font (§7.4)
  README.md                           # for the developer
```

Files carry `schemaVersion`. An older app refuses files newer than it understands and tells the user to update.

### 8.2 Hymn file

```markdown
---
id: love-incarnate          # file name = <id>.md, never changes
title: Love Incarnate
language: en                # en | zh-Hant later (§11)
aliases: []
arrangement: [v1, c, v2, c] # optional; when absent, blocks appear in file order
updated: 2026-11-16
updatedBy: Grace
---
[v1]
Love incarnate, Love divine
...

[c]
...
```

### 8.3 Local files and sync
- **Service file** (JSON, app-data folder, never shared). It holds the date, the preset, and ordered `{id, type, settings}` rows. Row settings can be:
  - a hymn id plus local edits;
  - a reference;
  - text blocks (announcements, supplications, catechism);
  - a quote and source;
  - schedule names;
  - a title.
- **Library cache**: a copy of `hymns/` and `config/` with their blob SHAs.
- **Name suggestions** for the schedule.
- **Read**: the GitHub Trees API lists `AutoPPTData`, and only files whose SHA changed are downloaded.
- **Write**: a Contents API `PUT` with the last-known `sha` (none for a new hymn). The first save wins (D24). GitHub rejects a stale update (409) or a new file whose name already exists (422), and both map to the same plain message (§5). The user's text stays in this service's row, so nothing typed is lost. No git tooling is needed.

---

## 9. Architecture

```
┌──────────────────────────── app (Swing + FlatLaf) ───────────────────────────┐
│ SlideListView · Editors (Passage, Hymn, TextBlocks, Quote, Schedule,         │
│   Header, TitleOnly) · MakeSlidesView · ErrorDialog                          │
│ SetupWizard · Settings · i18n ResourceBundles                                │
└───────────────┬──────────────────────────────────────────────────────────────┘
                │ view models; background work on SwingWorker
┌───────────────▼────────────── core (pure Java, unit-tested) ──────────────────┐
│ Service, SlideRow · SlideTypeRegistry · Presets                             │
│ Paginator + TextMeasurer (bundled fonts via fonts.yaml) + LineBreaker       │
│ PassageLayout · HymnLayout (LyricsCleaner, LineSplitter) · TextBlockLayout  │
│ DeckPlan ← slides {templateTag, replacements, styled ranges, notes}        │
│ ErrorModel (user message, support details, redaction)                       │
└──────┬─────────────────────────┬──────────────────────────┬──────────────────┘
┌──────▼──────── google ───────┐ ┌───────▼──── esv ────────┐ ┌───▼──── library ─────┐
│ GoogleAuth (loopback)        │ │ EsvClient (session cache │ │ LibrarySync (trees,  │
│ TemplateReader (tags, boxes, │ │  ≤500 verses)            │ │  blobs, cache)       │
│  fonts in use)               │ └──────────────────────────┘ │ HymnStore (PUT+sha)  │
│ DeckWriter (batchUpdate)     │                              │ ReleaseChecker       │
│ DeckCleaner (Trash)          │  secrets: SetupFile →        └──────────────────────┘
└──────────────────────────────┘    secrets.json (user-only)
```

- `core` has no network or Swing dependencies. Layout rules (§7.3) become unit tests, with cases from the two reference decks. Tests check the rules' intended result, not exact legacy output (D26).
- `DeckPlan` is the seam: layout produces it, and `DeckWriter` turns it into a `batchUpdate`.
- `TextMeasurer` uses AWT `TextLayout` / `LineBreakMeasurer` with fonts loaded through `Font.createFont` from bundled resources.
- Gradle (Kotlin DSL) with the wrapper pinned to a 9.x release and a Java 21 toolchain. Modules: `core`, `google`, `esv`, `library`, `app`.
- Libraries:
  - FlatLaf;
  - Google Guice 7 (§9.1);
  - Google API clients for Slides and Drive, plus `google-oauth-client-jetty`;
  - Jackson (JSON / YAML);
  - commonmark-java;
  - SLF4J + Logback (file only);
  - `java.net.http`.

### 9.1 Dependency injection (Guice)

- **Wiring.**
  - Each Gradle module ships one Guice module: `CoreModule`, `GoogleModule`, `EsvModule`, `LibraryModule`, `AppModule`.
  - `app` builds the injector once at startup, in `Main`, and the Swing frame, views and services come from it.
  - `core` uses only `jakarta.inject` annotations (`@Inject`, `@Singleton`), so it does not depend on Guice itself.
- **Seams that get bindings.** These are the parts tests swap for fakes:
  - Google, ESV and library clients;
  - `TextMeasurer`;
  - `LineBreaker` (Chinese later);
  - `BibleSource` (§11);
  - `SecretStore` (the `secrets.json` file);
  - `Clock`, so date logic such as "the coming Sunday" can be tested.

  Unit tests in `core` construct classes directly. Integration tests build the injector with `Modules.override(...)` to replace network clients.
- **Swing threading.** Guice creates objects on the main thread before `SwingUtilities.invokeLater`. Views are constructed on the EDT through `Provider<View>`.
- **Java 21 compatibility.** Guice 7.0.0 (May 2023, latest release) bundles ASM 9.5, which reads Java 21 class files. Tested on this machine with Temurin 21.0.13 and Guice 7.0.0 with its default dependencies (Guava 31.0.1-jre):

  | Check | Result |
  |---|---|
  | Constructor injection, singletons, records and sealed types | Work |
  | Runtime warnings | None. No JVM flags or dependency pins needed. |
  | Configuration-error messages | Correct, with source line numbers. |

  The same test on JDK 25 printed `sun.misc.Unsafe` deprecation warnings from Guava and Guice and needed workarounds, which is why D1 picks Java 21. Moving to a later LTS is gated on a Guice release that supports it. The fallback is Dagger 2 (compile-time injection, no runtime bytecode), with the same module boundaries.

---

## 10. Integrations and security

**Google**
- Scopes: `presentations` and `drive`. `drive` is a *restricted* scope. `drive.file` cannot copy a template the app did not create, even one shared by link.
- The OAuth client is a *Desktop* client using a loopback redirect on `127.0.0.1`.
- With personal Gmail accounts (D10), the OAuth app must be *External*. In *Testing* status, refresh tokens expire after 7 days. So the app is set to *In production* without verification:
  - users see a one-time "Google hasn't verified this app" screen;
  - unverified apps, including those asking for restricted scopes, are capped at 100 users for the life of the project, well above fewer than 10;
  - tokens then last.

  Source: [Google OAuth verification](https://developers.google.com/identity/protocols/oauth2/production-readiness/overview), [unverified apps](https://support.google.com/cloud/answer/13464323). Spike S3 confirms this.
- **Fallback if S3 fails** (sign-in blocked instead of warned, or tokens still expire): drop `drive` for `drive.file` plus the Google Picker. Each user picks the church template once, which grants the app access to it; copies, Trash and `appProperties` then work on files the app created. Full Google verification of the restricted scope is not pursued, because it needs a paid security assessment.
- The template is shared with each user as a viewer, with **"Viewers and commenters can see the option to download, print, and copy"** left on (`copyRequiresWriterPermission=false`); otherwise `files.copy` fails (F2). Decks are created in each user's own Drive.

**ESV.** The key is in the setup file, and usage stays within the F4 limits. ESV keys have no expiry date.

**Expiry of each key** (all configured not to expire):

| Key | Expiry | Can still stop working when |
|---|---|---|
| Shared-library token (GitHub fine-grained PAT) | Set to **No expiration** at creation | The developer revokes it, or the token owner loses access to `AutoPPTData` |
| ESV API key | None | Crossway revokes it (D6, D7) |
| Google OAuth client ID and secret | None | The developer deletes or resets it, or Google deletes it after 6 months with no sign-ins or token use. Weekly use keeps it active. |
| Google refresh token (per user) | None once the app is *In production* (7 days in *Testing*) | The user revokes access or signs out; it goes 6 months unused; or the user signs in more than 100 times with the same client, which drops the oldest token. |

**Shared library**
- A fine-grained personal access token limited to `AutoPPTData` with *Contents: read/write* only, created with **No expiration** ([GitHub credential types](https://docs.github.com/en/organizations/managing-programmatic-access-to-your-organization/github-credential-types)). GitHub cannot limit a token to one folder, so it can write anything in `AutoPPTData`, including `config/`. The app itself only writes `hymns/`.
- Users need no GitHub accounts. Commits are made by the token owner, with the user's typed name in the message; that name is not verified. Git history is the undo for a bad write.

**Setup file**
- Whoever holds the file can:
  - change anything in `AutoPPTData` (recoverable from history);
  - use the shared ESV key and its quota (D7);
  - start a Google sign-in as this app, without access to anyone else's Drive.
- Mitigations: a token limited to one repo, the developer can revoke and reissue, and the app offers to delete the file after import (F8). The developer hands the file over in person or through an encrypted channel, not plain email.
- After import the same secrets, plus the user's Google refresh token, sit unencrypted in `secrets.json` (D25). Anyone who can log in as that user on that computer can read them. Accepted for fewer than 10 users; "Sign out of Google" and "Replace setup file" rewrite the file.
- Because nothing expires, a leaked file stays usable until the developer revokes its keys. Revoking the shared-library token stops "Save for everyone" and updates for every user until each gets a new setup file.
- Never committed: add `*.autoppt` to both repos' `.gitignore`.

**Bundled fonts.** Each font's licence file ships next to it in the app zip, as the GUST, OFL and Apache licences require.

---

## 11. Chinese support: design considerations (built later)

Expected scope (D14): Traditional-Chinese UI text, plus a separate Chinese template, possibly bilingual. v1 builds only the items marked **now**.

| Area | Consideration | v1 |
|---|---|---|
| UI text | Every string in UTF-8 `ResourceBundle`s; `messages_zh_TW` added later. The FlatLaf font falls back to a CJK font on both OSes. | **now**: no hard-coded strings |
| Templates | `app.yaml` lists templates with a `language`. Slide types and presets can be template-specific. | **now**: list-shaped config |
| Bible text | ESV is English-only and may not be translated. Chinese text needs another source, e.g. the public-domain 和合本 (CUV) in Traditional script, stored locally. A `BibleSource` interface lets ESV (remote) and CUV (local) coexist, and bilingual slides pair verses by number. Chinese references (約翰福音 3:16 / 約 3:16) need a local parser. | **now**: `BibleSource` interface |
| Hymns | A `language` field. Bilingual hymns need paired lines or blocks; the format is decided with the requirements. | **now**: field only |
| Line breaking | `BreakIterator` plus Chinese punctuation rules (no line starting with `，。；：！？、）」』`). The hymn splitter adds `，；！。`. | **now**: `LineBreaker` interface |
| Fonts | Noto Sans TC / Noto Serif TC, bundled and measured with the exact file (§7.4). | later |
| Dates | "2026年11月22日", with no ordinal superscript. | later |

---

## 12. Risks and spikes

| # | Risk | Spike (do first) |
|---|---|---|
| S1 | Measurement drifts from what Slides renders, so slides overflow or are under-filled. Sources: Century Gothic vs. TeX Gyre Adventor widths (regular and bold) and kerning; Times New Roman vs. Tinos; line spacing; the text box's inner padding. | Write sample verses, hymns, titles and announcements through the API into a test copy of the template. Compare Slides' line breaks and text heights (via the exported PDF) with the paginator's predictions. Derive the inner padding, check the line-spacing model, and only then set the safety margin in `layout.yaml`. If Adventor drifts too far, use the §7.4 fallback. |
| S2 | The hanging indent with a superscript number renders unevenly. | One API-built slide testing `indentStart` / `indentFirstLine` with a tab vs. spaces after the number, plus `alignment: END` for the source line. |
| S3 | Restricted `drive` scope in an unverified production app behaves differently than expected. | Sign in with a Gmail account that is not listed as a test user. Pass if the "unverified app" warning can be clicked through (fail if access is blocked) and the app still works on day 8. On failure, use the §10 fallback. |
| S4 | Users are confused by the unsigned-app warnings on first run. | First-run guide with screenshots. |

---

## 13. Open questions

None at this point.

---

## 14. Next steps (AI-DLC)

1. Review this draft.
2. Run spikes S1–S3. They confirm measured pagination with bundled fonts, the indent and alignment built-ins, and the sign-in model.
3. AI-DLC Inception: requirements and user stories from §6–§7, application design from §8–§9, then a delivery plan. Suggested units:
   - `core-layout` (paginator, fonts, passage, hymn, text blocks);
   - `esv`;
   - `library` (hymn sync and save);
   - `google` (template reader, deck writer, cleanup);
   - `app-shell` (slide list, editors, setup wizard, errors, i18n bundles);
   - `packaging`.
