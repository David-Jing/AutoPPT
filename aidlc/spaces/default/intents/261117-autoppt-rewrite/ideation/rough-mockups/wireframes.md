# Wireframes: AutoPPT Rewrite

Inputs: `intent-statement.md`, `scope-document.md`, `intent-backlog.md`, `rough-mockups-questions.md` (Q1 to Q9), and `high-level-design.md` (the F1 main-window sketch and the screen descriptions in F1 to F12). Wording follows the plain-language vocabulary (design document section 5), and this document says "PPT" or "slides" (Q1).

These are low-fidelity sketches. Layout, colour and spacing are decided later; the sketches fix which controls exist, where they sit and what each state says.

## Conventions

- `≡` is a row's drag handle. `⚠` marks a row that cannot be made. `✎` marks a slide the user finishes in Google Slides. `▾` is a dropdown. `[ ]` is a button.
- Accessibility (Q2): every screen is fully usable by keyboard alone, and text keeps a contrast ratio of at least 4.5:1 (WCAG AA) in both the light and dark themes (Q5). Screen-reader support is not part of the first version (Q2). Each screen has a one-line note with its heading level, main regions and keyboard entry point. The heading levels, regions and focus-start rules in those notes are proposed, not decided (Q5).
- Form factor: a desktop window for Windows and macOS (Q1). The app has no preview of the slides (Q1).

## Screen index

| Screen | Sketched in | Source |
|--------|-------------|--------|
| Start-up screen on every launch (status lines), the no-connection stop screen and the "new version available" state | S0a, S0b, S0c | Q7, Q8 |
| First start (step-by-step wizard, one screen per step) | S1a to S1d | Q1, Q4, Q6 |
| Main window (service builder), with the "Hi [name]" greeting, and its connection-lost cover | S2, with its states in S2a to S2d | Q1, Q3, Q6, Q7 |
| Editor for each slide type | S3a to S3h, including S3d2 (Title-only); Supplications uses the S3d editor | Q4, Q5 |
| Make-slides progress view | S4 | Q3, Q4 |
| Error dialog | S5 | Q3, Q4 |
| Settings | S6 | Q4 |
| Hymn confirmations: save (S7a), discard (S7b) and delete (S7c) | S7a to S7c | Q4, Q6 |
| "Someone else saved this hymn first" message | S8 | Q4 |

## S0. Start-up screen and internet connection

An internet connection is required to use the app. Google Slides, the ESV API and the shared library are all online, so there is no offline mode (Q7; design document F0). Every launch opens a small start-up screen first. It shows the app name, the version, and one status line at a time (Q7).

### S0a. Start-up screen (one status line at a time)

```
┌ AutoPPT ──────────────────────────────┐
│                                       │
│               AutoPPT                 │
│               Version 1.0.0           │
│                                       │
│   Checking your internet connection…  │
│                                       │
└───────────────────────────────────────┘
```

The status line changes in this order, with the rest of the screen unchanged (Q7; design document F0):

1. "Checking your internet connection…"
2. "Checking for updates…" (a newer app version, and, except on first start, the shared library)
3. "Getting ready…" (loading the saved keys)

On first start, or when the saved keys are missing or replaced, there are no keys yet. The start-up screen then checks only the connection and the app version, the shared library is loaded in wizard step 4 (S1d), and "Getting ready…" is skipped (Q8). The screen then opens the first-start wizard (S1a), or the main window (S2) otherwise (Q6, Q7, Q8).

The internet check is a short request with a 5-second timeout, and while the app is not connected it re-checks every 5 seconds. A captive portal or blocked network counts as not connected (Q8; design document F0).

### S0b. No connection at start-up

```
┌ AutoPPT ──────────────────────────────┐
│               AutoPPT                 │
│               Version 1.0.0           │
│                                       │
│ You're not connected to the internet. │
│ Connect, then choose Try again.       │
│                                       │
│            [ Try again ]  [ Quit ]    │
└───────────────────────────────────────┘
```

- The start-up screen stops at step 1 and shows this message with Try again and Quit. Nothing else opens until the check passes, and the app re-checks by itself every 5 seconds while it is not connected (Q8; design document F0). The check has a 5-second timeout (Q8). When a re-check passes, the screen carries on to "Checking for updates…".
- This replaces the earlier offline state of the first-start wizard (Q7).
- Decided (Q9): Try again is the default button, and Escape does nothing.
- Accessibility (proposed): h1 is the app name; one main region; keyboard entry point is Try again, and Quit is the next Tab stop.

### S0c. A newer version is available

```
┌ AutoPPT ──────────────────────────────┐
│               AutoPPT                 │
│               Version 1.0.0           │
│                                       │
│ A new version of AutoPPT is available │
│ (1.1.0)                               │
│                                       │
│            [ Download ]  [ Continue ] │
└───────────────────────────────────────┘
```

- During "Checking for updates…" the app makes one request to GitHub's public latest-release endpoint for AutoPPT and compares the release's version with its own. If the release is newer, the start-up screen stops here with "A new version of AutoPPT is available (x.y.z)", where x.y.z is the new version, for example 1.1.0 (Q8; design document F11).
- Only the latest published release counts, because GitHub's latest-release endpoint already leaves out drafts and pre-releases. "Newer" means a higher major.minor.patch version (Q9; design document F11).
- This request uses the same 5-second timeout as the connection check. On a failure or a timeout, or when the release is not newer, this screen does not appear, and start-up carries on silently with no message (Q9).
- Download opens the zip for this computer (Windows x64, macOS Apple Silicon or macOS Intel) in the web browser. The screen stays open after Download is chosen, so the user can still choose Continue (Q8). Continue carries on to the next start-up step. The screen waits for the user to choose.
- The app never downloads or replaces itself (Q8; design document F11).
- Decided (Q9): Continue is the default button, and Escape means Continue.
- Accessibility (proposed): h1 is the app name; one main region; keyboard entry point is Continue (the default button), and Download is the next Tab stop.

## S1. First start

First start is a step-by-step wizard: one step per screen, with Back and Next and a "Step n of N" marker, and the user never sees all the steps on one screen (Q6; design document F8). The wizard appears only on first start, or when the saved keys are missing or replaced. Every later start opens the main window directly (Q6). The four steps are the setup file, your name, sign in with Google, and the shared library ready (Q6).

### S1a. Step 1 of 4: Setup file

```
┌ AutoPPT: Welcome ─────────────────────────────────────────────┐
│ Step 1 of 4   Setup file                                      │
│ ┌───────────────────────────────────────────────────────────┐ │
│ │   Drop your setup file here        [ Browse… ]            │ │
│ └───────────────────────────────────────────────────────────┘ │
│ ✔ Scripture text is ready                                     │
│ ✔ Shared library access is ready                              │
│ [ Delete the setup file now ]                                 │
│                                          [ Back ]  [ Next ▸ ] │
└───────────────────────────────────────────────────────────────┘
```

- Back is disabled on the first step. Next stays disabled until the setup file is accepted. The app checks the keys in this step and names the one that failed in plain words (Q6; design document F8).

### S1b. Step 2 of 4: Your name

```
┌ AutoPPT: Welcome ─────────────────────────────────────────────┐
│ Step 2 of 4   Your name                                       │
│ What should we call you?                                      │
│ [______________]                                              │
│ Used when you save a hymn for everyone, and to greet you.     │
│                                          [ Back ]  [ Next ▸ ] │
└───────────────────────────────────────────────────────────────┘
```

- Next stays disabled until a name is typed. The name appears in the main window's top bar as "Hi [name]" and can be changed in Settings (Q6).

### S1c. Step 3 of 4: Sign in with Google

```
┌ AutoPPT: Welcome ─────────────────────────────────────────────┐
│ Step 3 of 4   Sign in with Google                             │
│ [ Sign in with Google ]   (opens your web browser)            │
│ ✔ Signed in as grace@…   (after sign-in)                      │
│                                          [ Back ]  [ Next ▸ ] │
└───────────────────────────────────────────────────────────────┘
```

- Next stays disabled until sign-in succeeds. A sign-in that fails because of the setup file says "This setup file doesn't work with Google sign-in. Ask the developer for a new setup file." (design document F8).

### S1d. Step 4 of 4: Shared library ready

```
┌ AutoPPT: Welcome ─────────────────────────────────────────────┐
│ Step 4 of 4   Shared library                                  │
│ Downloading the shared library…  ▓▓▓▓░░░░                     │
│ ✔ Shared library is ready   (after the download)              │
│                                         [ Back ]  [ Finish ▸ ]│
└───────────────────────────────────────────────────────────────┘
```

- This is where the shared library is loaded on first start. The start-up screen does not load it, because there are no keys yet (Q8; design document F0 and F8).
- Finish opens the main window and stays disabled until the download is done (Q6).

There is no separate offline state for the wizard. The wizard opens only after the start-up screen has confirmed an internet connection (S0), and a connection lost while the wizard is open shows the same cover as in the main window (S2d). The wizard stays on the same step and keeps what was typed or dropped in, and the cover closes by itself when the connection returns (Q7, Q8; design document F0).

### S1 notes

- Failure states: a setup file that fails a check names which part failed in plain words (for example "The scripture key doesn't work. Ask the developer for a new setup file.") (design document F8).
- Accessibility (proposed): h1 "Welcome"; each step is a separate main region headed by its "Step n of 4" marker; keyboard entry point is Browse in step 1, the name field in step 2, the sign-in button in step 3 and Finish in step 4.

## S2. Main window (service builder)

```
┌ AutoPPT ─────────────────────────────────────────────────────────────────────┐
│ Hi Grace   Sunday 22 Nov 2026 ▾   Order: Normal ▾ [New service] [Make slides ▸]│
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
│ ⚠ Hymn: (none chosen)        │ [Discard my changes] [Save for everyone]      │
│ ≡ Next Week's Schedule       │ [Delete for everyone]                         │
│ [+ Add slide ▾]  [Remove]    │                                               │
├──────────────────────────────┴───────────────────────────────────────────────┤
│ Check for updates      Settings      Signed in as grace@…                    │
└──────────────────────────────────────────────────────────────────────────────┘
```

- This is the design document's F1 sketch, with the bottom bar and "Delete for everyone" added for the first version. The top bar greets the user with "Hi [name]", using the name entered at first start (S1b), and the name can be changed in Settings (S6) (Q6; design document F1). The main window opens only after the start-up screen has confirmed an internet connection (S0a). The `Order` dropdown offers Normal and Holy Communion. The date defaults to the coming Sunday (today if today is a Sunday).
- Reorder by dragging the ≡ handle, or with Move up and Move down (Alt+Up and Alt+Down). Each row shows a short summary; a row that cannot be made shows ⚠ and a plain reason.
- Accessibility: h1 is the window title with the service date; the slide list is the navigation region and the settings panel is the main region; keyboard entry point is the slide list, with Tab moving to the settings panel and the Make slides button.

### S2a. Row warnings

```
│ ⚠ Hymn: (none chosen)        │  Choose a hymn                                │
│ ⚠ Sermon Passage  Jhon 3:16  │  We couldn't find 'Jhon 3:16'                 │
```

- The plain reason shows beside the row in the panel, and the "Make slides" button stays enabled so a row that cannot be made is simply left out of the run (design document F2).

### S2b. Empty state: a new service

```
│                              │ This service has no slides yet.               │
│ (no rows)                    │ Choose an order above to fill in the usual    │
│ [+ Add slide ▾]              │ slides, or add slides one at a time.          │
```

- "New service" starts again from the chosen order with empty settings. Nothing is copied from earlier services (design document F1).

### S2c. Empty state: no hymns yet

```
│ Hymn                         │ ┌───────────────────────────────────────────┐ │
│                              │ │ No hymns yet. Choose "Check for updates"  │ │
│                              │ │ or add one with "New hymn…"          ▾    │ │
│                              │ └───────────────────────────────────────────┘ │
```

### S2d. Connection lost while the app is open

```
┌ AutoPPT ─────────────────────────────────────────────────────────────────────┐
│ Hi Grace   Sunday 22 Nov 2026 ▾   (the main window sits behind the cover)    │
│ ┌──────────────────────────────────────────────────────────────────────────┐ │
│ │ You're not connected to the internet. Connect, then choose Try again.    │ │
│ │                                          [ Try again ]  [ Quit ]         │ │
│ └──────────────────────────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────────────────────────┘
```

- The main window is covered by the same message as the start-up screen (S0b). Typed work stays where it is, because the service autosaves locally (Q7; design document F0 and F1).
- Any action that was running (making slides, a hymn save) reports Problem with "The internet connection was lost", in the progress view (S4) or the error dialog (S5), instead of a generic error (Q7; design document F0).
- The app re-checks by itself every 5 seconds while it is not connected, and the cover closes by itself when the connection returns (Q7, Q8; design document F0). Try again re-checks at once, and Quit closes the app (Q7).
- The same cover shows over the first-start wizard. The wizard stays on the same step and keeps what was typed (Q8).
- There is no offline mode, so no part of the main window stays usable while it is covered (Q7).
- Escape does nothing on this cover, as on the no-connection screen S0b (decided at approval).
- Accessibility (proposed): the cover is a modal region over the main window; keyboard entry point is Try again, and the controls behind the cover cannot take focus.

## S3. Editors for each slide type

Each editor fills the right-hand panel when its row is selected (design document F1 settings table).

### S3a. Opening Passage and Bible Passage

```
│ Opening Passage                                            │
│ Title (optional)  [PLEASE KEEP SILENT AND PREPARE YOUR …]  │
│ Passage           [John 8:12                           ?]  │
│ ☐ Show the reference after the passage   (Bible Passage only)
│ ⚠ We couldn't find 'Jhon 3:16'                             │
```

- The `?` tooltip lists what works ("Genesis 1:1-10", "John 3:16, 18", "Psalm 23; Romans 8:28") and what does not (Chinese book names, other translations). Separate passages are split with a semicolon only.

### S3b. Chinese Monthly Scripture (title only)

```
│ Chinese Monthly Scripture                                  │
│ Title   [本月金句]                                          │
│ This slide is made with an empty body. Add the Chinese     │
│ verse picture in Google Slides after making the slides.    │
```

### S3c. Hymn

```
│ Hymn                                                       │
│ [ Love Incarnate                                       ▾ ] │
│   search: title, other titles, first line                  │
│   New hymn…  (last entry)                                  │
│ Lyrics               ● Only on this computer               │
│ [ [Verse 1] Love incarnate, Love divine …                ] │
│ [Discard my changes] [Save for everyone] [Delete for everyone]
```

- "New hymn…" asks for a title and pasted lyrics, which are split into blocks on blank lines and headers such as `[Chorus]`.

### S3d. Announcements and Supplications

```
│ Announcements                                              │
│ Title  [ANNOUNCEMENTS]                                     │
│ ┌ 1 ──────────────────────── [B][I][•][1.][link] [↑][↓][x]┐│
│ │ Youth retreat sign-up closes Friday.                    ││
│ └─────────────────────────────────────────────────────────┘│
│ [+ Add item]                                               │
```

Supplications uses the same editor as Announcements (Q5): the same ordered list of text blocks with the same formatting buttons. Only the default title differs.

### S3d2. Title-only

```
│ Title-only slide                                           │
│ Title   [                                              ]   │
│ This slide is made with a title and an empty body.         │
│ Finish it in Google Slides after making the slides.        │
```

- A Title-only row reuses the title field of the Chinese Monthly Scripture editor (S3b) and has no other settings (design document F6 and F12; Q5).

### S3e. Catechism

```
│ Catechism                                                  │
│ Title  [THE WESTMINSTER SHORTER CATECHISM Q&A #71]         │
│ Body   [B][I][•][1.][link]                                 │
│        [Q71: What is required in the seventh commandment?] │
```

### S3f. Spiritual Wisdom (quote)

```
│ Spiritual Wisdom                                           │
│ Title   [SPIRITUAL WISDOM AND PROFOUND SAYINGS]            │
│ Quote   [several paragraphs…………………………]                │
│ Source  [Imitation of Christ by Thomas Kempis]             │
```

### S3g. Header and Sermon Header

```
│ Sermon Header                                              │
│ Title    [          ]    Speaker  [          ]             │
│ The date fills in automatically.                           │
```

### S3h. Next Week's Schedule, Static and Title-only rows

```
│ Next Week's Schedule                                       │
│ Date  Sunday 29 Nov 2026 (set from the service date)       │
│ Speaker [      ]  Leader [      ]  Accompanist [      ]    │
│ Audio   [      ]  Streaming [   ]  Announcer [      ]      │
│ (one name field per role in the template; empty prints N/A)│
│                                                            │
│ Static slides (Lord's Prayer, Creed, Holy Communion, …):   │
│ "This slide is copied as-is."                              │
```

- Accessibility (proposed) for S3a to S3h, including S3d2: the panel title is the h2; each field has a visible label; keyboard entry point is the first field; the formatting buttons and the reorder and remove buttons on a text block are reachable by Tab.

## S4. Make-slides progress view

```
┌ Making your slides ─────────────────────────────────────────┐
│ ✔ Opening Passage              Done                         │
│ ✔ Sunday Service Header        Done                         │
│ ⚠ Sermon Passage               Problem: We couldn't find …  │
│ – Supplications                Skipped                      │
│ ✎ Chinese Monthly Scripture    To do in Google Slides:      │
│                                add the picture to slide 4  │
│ ⟳ Hymn: Love Incarnate         In progress                  │
│ ▓▓▓▓▓▓▓▓░░ 5 of 6 rows finished                             │
│ [ Open in browser ] off  [ Copy link ] off     [ Close ] off │
└─────────────────────────────────────────────────────────────┘
```

When the run ends (all 6 rows finished), the count reads "6 of 6 rows finished", the buttons turn on, and this line appears under the rows: "Last week's slides ('Sunday Worship …') were moved to the Trash. You can restore them from there for 30 days."

- If the internet connection is lost during a run, the covered window (S2d) shows over this view, and the row being made ends as Problem with "The internet connection was lost" (Q7; design document F0). The run reports that reason instead of a generic error.
- The five row states are Done, Problem (with a plain reason), Skipped, To do in Google Slides and In progress (the row being made right now, shown with ⟳). A run has one In progress row at a time (Q5). The count is the number of rows no longer waiting or In progress, out of all rows drawn. One failed row does not stop the run, and its slides are left out. The PPT opens in the default browser when the run ends. The Make slides button is disabled while a run is in progress. The Trash line shows only when "Delete last week's slides" is on and an earlier PPT was found (design document F2).
- Accessibility (proposed): h1 "Making your slides"; the row list is the main region; keyboard entry point is Open in browser once the run ends.

## S5. Error dialog

```
┌ Something went wrong ───────────────────────────────────────┐
│ We couldn't reach the shared library.                       │
│ Check your internet connection and try again.               │
│ [ Copy details for support ]  [ Open log folder ]  [ OK ]   │
└─────────────────────────────────────────────────────────────┘
```

- A lost connection is reported here as "The internet connection was lost" rather than a generic error (Q7; design document F0).
- The dialog gives a plain summary and what to try. "Copy details for support" copies the version, operating system, Java version, error code, stack trace and the affected row. Nothing is sent anywhere (design document F9).
- Accessibility: h1 is the dialog title; focus starts on OK; Escape closes the dialog.

## S6. Settings

```
┌ Settings ───────────────────────────────────────────────────┐
│ ☑ Delete last week's slides when I make new ones            │
│ Your name        [Grace          ]                          │
│ Theme            ( ) Light  ( ) Dark                        │
│ [ Replace setup file ]   [ Sign out of Google ]             │
│                                                   [ Close ] │
│ ─────────────────────────────────────────────────────────── │
│ AutoPPT 1.0.0                                               │
└─────────────────────────────────────────────────────────────┘
```

- About: the app version, for example "AutoPPT 1.0.0", shows at the bottom of Settings. The same version appears on the start-up screen (S0a) and in "Copy details for support" (S5) (Q7; design document F10).

- The name in this screen is the one entered at first start (S1b) and is shown in the main window's top bar as "Hi [name]"; changing it here updates the greeting (Q6).
- There is no language selector in the first version (design document F10; scope-document.md lists it as out of scope).
- Accessibility: h1 "Settings"; one main region; keyboard entry point is the first checkbox.

## S7. Hymn confirmations

"Save for everyone", "Discard my changes" and "Delete for everyone" each ask first, naming the hymn and what will happen. Cancel leaves everything as it was and changes nothing (Q6; design document F3). Decided (Q8): on all three, Cancel is the default button and Escape cancels. None of them says "GitHub" or "commit" (design document D5).

### S7a. Save for everyone confirmation

```
┌ Save for everyone? ─────────────────────────────────────────┐
│ Save your changes to 'Love Incarnate' for everyone?         │
│ Everyone will see your version the next time they check     │
│ for updates.                                                │
│                          [ Cancel ]  [ Save for everyone ]  │
└─────────────────────────────────────────────────────────────┘
```

- Confirm saves the hymn to the shared library. If the hymn changed first, S8 shows instead.

### S7b. Discard my changes confirmation

```
┌ Discard my changes? ────────────────────────────────────────┐
│ Discard your changes to 'Love Incarnate'?                   │
│ Your unsaved edits on this computer will be lost, and the   │
│ shared version will be used again. This can't be undone.    │
│                          [ Cancel ]  [ Discard my changes ] │
└─────────────────────────────────────────────────────────────┘
```

- Confirm returns the hymn to the shared version. Discard only affects unsaved local edits (design document D13).

### S7c. Delete for everyone confirmation

```
┌ Delete for everyone? ───────────────────────────────────────┐
│ Delete 'Love Incarnate' for everyone?                       │
│ It will be removed from the shared library for all users.   │
│ You can't undo this. A service already using the hymn keeps │
│ its lyrics.                                                 │
│                          [ Cancel ]  [ Delete for everyone ]│
└─────────────────────────────────────────────────────────────┘
```

- Cancel is the default button, and the dialog never says "GitHub" or "commit" (design document F3, D5). Users cannot undo a delete; the developer can restore it from history (design document F3).
- Accessibility: h1 is the dialog title; focus starts on Cancel; Escape cancels.

## S8. "Someone else saved this hymn first"

```
┌ Hymn not saved ─────────────────────────────────────────────┐
│ Someone else saved this hymn first. Choose Check for        │
│ updates, then make your change again.                       │
│                       [ Check for updates ]  [ Close ]      │
└─────────────────────────────────────────────────────────────┘
```

- This message is shown when a save or a delete is refused because the hymn changed first, or a new hymn's id already exists. The user's typed text stays in the service's row, so nothing typed is lost (design document F3 and section 8.3).
- Accessibility: h1 is the dialog title; focus starts on Check for updates.

## Assumptions & Open Questions

None.
