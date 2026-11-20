# Requirements: AutoPPT Rewrite

Inputs: `intent-statement.md` (intent-capture), `scope-document.md` and `intent-backlog.md` (scope-definition), `team-practices.md` (practices-discovery), `requirements-analysis-questions.md` (Q1 to Q6), and `high-level-design.md` v0.5 (design document), which states the behaviour. Terms: "PPT" and "slides" name the generated output, never "deck". Each requirement has a pass or fail check in its own words. Section references like F3 or V2 are the design document's.

## Intent Analysis

- **Goal.** A non-technical operator builds the weekly Sunday worship PPT for FCNABC's English congregation in under 10 minutes from the church's Google Slides template, with no touch-up on 4 consecutive Sundays after launch, replacing the legacy Python app (intent-statement.md Success Metrics; Q1).
- **Type.** A rewrite of an existing product as a new desktop app, with new features (Opening Passage, Offering as a passage slide, Spiritual Wisdom, Catechism, any number of hymns, schedule roles, hymn deletion, a start-up update check).
- **Scope.** Several components: a Swing desktop app, a Google Slides and Drive writer, an ESV API client, and a shared hymn library kept in a GitHub repository.
- **Complexity.** Complex. The text-fitting logic (the paginator) is the main technical risk, and three spikes (S1 to S3) run first (scope-document.md Sequencing; feasibility-assessment.md).

## Deployment Shape (corner sweep)

A single-user, local desktop app with no accounts of its own. Each operator signs in with a personal Google account. The app shares one ESV key and one shared-library token across all operators, through a setup file. The identity, tenant and personal-data rows of the corner checklist therefore reduce to: no multi-tenant data, and no personal data beyond the user's name and Google refresh token held on that user's own computer (high-level-design.md D7, D10, D25).

## Functional Requirements

### FR1. Start-up and internet connection (F0)

- **FR1.1** Every launch shows a start-up screen with the app name, the app version and one status line at a time. The three lines show in this order: "Checking your internet connection…", "Checking for updates…", "Getting ready…", except on first start, as FR1.4 describes. Check: launch the app and observe the three lines in order, and on a first start observe the first two only.
- **FR1.2** The internet check is one HTTPS request to a Google endpoint with a 5-second timeout. A captive portal or blocked network counts as not connected. Check: with the network blocked or a captive portal in the way, the app reports no connection within 5 seconds.
- **FR1.3** With no connection at start-up, the screen stops at the first status line and shows "You're not connected to the internet. Connect, then choose Try again." with Try again (the default button) and Quit. Escape does nothing. Nothing else opens, and the app re-checks every 5 seconds by itself. Check: start offline, see the message, restore the network, and see start-up carry on without a click.
- **FR1.4** On first start, or when the saved keys are missing or replaced, "Checking for updates…" checks only the app version, the shared library is loaded in the last wizard step, and "Getting ready…" is skipped. Otherwise "Checking for updates…" also refreshes the shared library, and "Getting ready…" loads the saved keys. The library refresh has a 5-second timeout. If it fails or times out, the app uses the cached copy of the library and shows "Couldn't check for hymn updates", and start-up carries on. If it fails and no cached copy exists (for example after an interrupted first start), the main window does not open, and the app shows "Couldn't get the shared hymn library. Check your connection and choose Try again." Check: observe the status lines in both cases, block the shared library to see the cached copy used with that message, and block it with the cache removed to see the main window stay closed with the Try again message.
- **FR1.5** After start-up the app opens the first-start wizard (first start only) or the main window. Check: both paths.
- **FR1.6** When the connection is lost while the app is open, the main window or the wizard is covered by the same message, with Try again and Quit and Escape doing nothing. Typed work stays, and the cover closes by itself when the connection returns. A lost connection during Make slides is a stopped run (FR12.3): the rows not yet done show Skipped, the run reports "The internet connection was lost", and the stopped-run rule (FR12.5) applies. Any other action that was running, such as a hymn save, ends as Problem with "The internet connection was lost". The wizard stays on the same step and keeps what was typed. Check: disconnect during each of main window, a run and the wizard.
- **FR1.7** There is no offline mode: no part of the app other than the cover is usable while not connected. Check: with the cover showing, no editor takes input.

### FR2. First-start wizard (F8)

- **FR2.1** The setup screens appear only on first start, or when the saved keys are missing or replaced. Every other start opens the main window directly. Check: complete setup, restart, and see no wizard.
- **FR2.2** The wizard has four steps, one per screen, with Back, Next and a "Step n of 4" marker: setup file, your name, sign in with Google, shared library ready. The user never sees all steps on one screen. Check: step through and count screens.
- **FR2.3** Step 1 accepts the setup file by drag and drop or Browse. The file holds the Google OAuth client ID and secret, the ESV API key and the shared-library token with its repository name. Check: a valid file is accepted.
- **FR2.4** Step 1 checks the ESV key and the shared-library token with one inexpensive request each, and names the one that failed in plain words. Next stays disabled until the file is accepted. Check: a file with a wrong ESV key, and a file with a wrong shared-library token, each name the failing part.
- **FR2.5** Accepted keys are saved to `secrets.json` in the OS app-data folder. On macOS the file mode is 0600. On Windows its access list is limited to the current user. The app then offers "Delete the setup file now". Check: file permissions after setup.
- **FR2.6** Step 2 takes the user's name. Next stays disabled until a name is typed. The name is used in hymn save messages and in the "Hi [name]" greeting. Check: an empty name blocks Next.
- **FR2.7** Step 3 opens the system browser for Google sign-in (a loopback redirect). The refresh token is saved in `secrets.json`. A client that Google rejects shows "This setup file doesn't work with Google sign-in. Ask the developer for a new setup file." Check: sign in successfully, and with a deleted client.
- **FR2.8** Step 4 downloads the shared library and shows a progress bar. Finish stays disabled until the download is done, then opens the main window. Check: Finish is disabled mid-download.
- **FR2.9** Settings can replace the setup file later (FR14.2). The new file goes through the step 1 checks. Check: replace with a valid file and with a bad one.

### FR3. Service builder (F1)

- **FR3.1** The main window lists the service's slides in generation order on the left, and shows the selected slide's settings on the right. The top bar greets the user "Hi [name]", using the name from first start. Check: the greeting shows the saved name, and shows the new name after a change in Settings.
- **FR3.2** The service date defaults to the coming Sunday: today if today is a Sunday, otherwise the next Sunday. All dates use the computer's local time zone. Check: with fixed dates (a Sunday, a Saturday, a Monday), including a month end and a year end, and with the computer's time zone set to two different zones near midnight.
- **FR3.3** Choosing an order (Normal or Holy Communion) fills the list with that preset's default slides, and the Normal preset includes the Offering (FR5.12). "New service" starts again from the chosen preset with empty settings, and nothing is copied from earlier services or PPTs. Check: create two services and compare.
- **FR3.4** The user can reorder slides by dragging the handle, or with Move up and Move down (Alt+Up, Alt+Down). Check: both ways.
- **FR3.5** "+ Add slide" lists the slide types from the configuration, and any type can be added more than once. Remove removes a row from this service only. Check: add the same type twice, then remove one.
- **FR3.6** Each row shows a short summary. A row that cannot be made shows a warning with a plain reason ("Choose a hymn", "We couldn't find 'Jhon 3:16'"). Check: both reasons.
- **FR3.7** The service autosaves locally, so closing the app loses nothing. The service file is written so that an interrupted write never replaces the last good copy. Check: kill the app mid-edit and reopen; kill it mid-write and see the previous file intact.
- **FR3.8** An empty service shows "This service has no slides yet." with a hint. A hymn list that is empty shows "No hymns yet" with a hint to check for updates or add one. Check: both empty states.
- **FR3.9** Make slides is the only action that creates a PPT, and its button is disabled while a run is in progress. Check: a second click during a run does nothing.
- **FR3.10** Only one copy of the app may run on a computer. A second launch shows "AutoPPT is already open" and exits, so two copies never write the same service file or `secrets.json`. The check uses an operating-system file lock that is released when the process ends, so a crash never blocks the next launch. Check: launch twice and see the message and the second copy exit, then kill the app and relaunch it and see it start.

### FR4. Slide types and their settings (F1)

- **FR4.1** The app offers these slide types: Opening Passage, Offering, Bible Passage (Call to Worship, Monthly Scripture, Memorization this and next week, Prayer of Confession, Sermon Passage), Chinese Monthly Scripture, Hymn, Announcements, Supplications, Catechism, Spiritual Wisdom, Header, Sermon Header, Next Week's Schedule, Static (Lord's Prayer, Creed, Holy Communion, Doxology, Benediction) and Title-only. Check: each type can be added and made.
- **FR4.2** Static slides have no settings and are copied as they are. Title-only has a title and an empty body, finished by hand. Supplications uses the same editor as Announcements. Check: each behaves as stated.
- **FR4.3** Header and Sermon Header take a title, and Sermon Header also takes a speaker. The date fills in automatically, in the form "September 27th, 2026", with the ordinal suffix raised. The suffix is "th" for 11 to 13 and any number ending 0 or 4 to 9, otherwise "st" for 1, "nd" for 2 and "rd" for 3. Check: 1, 2, 3, 4, 11, 12, 13, 21, 22, 23 and 31.
- **FR4.4** Next week's rows (Memorization next week, Catechism next week) are entered each week like any other row. Check: they are empty in a new service.

### FR5. Bible passages (F4, rules V1 to V7)

- **FR5.1** The Opening Passage takes a passage (one or more verses) and has no title. The template slide has one `{Text}` box only (28 pt, left): the passage goes in it, and the reference ("—John 8:12") is the last paragraph of the same box, placed toward the right (V6). When a long passage spans several copies of the slide, the reference is on the last slide only. There is no separate reference box. Check: a short and a long passage, and no title field on the row.
- **FR5.2** A Bible Passage row takes a passage, an optional title, and a "show the reference after the passage" toggle. A `?` help button next to the passage field opens a pop-up that lists what works ("Genesis 1:1-10", "John 3:16, 18", "Psalm 23; Romans 8:28") and what does not (Chinese book names, other translations). It is a button and not a hover tooltip, because a tooltip cannot be reached by keyboard. Check: press the `?` button with the keyboard and read the pop-up text, and the toggle's effect.
- **FR5.3** Passage text comes from the ESV API `/v3/passage/text/`, and the app sends the reference to the API instead of parsing it. The passage field splits on a semicolon only, each piece is sent as one query, and its canonical reference becomes the reference line. Check: a field with two passages makes two references.
- **FR5.4** Request options: headings, footnotes, passage references, short copyright and Selahs off, and verse numbers on. Check: the request parameters.
- **FR5.5** Each verse is one paragraph, flattened (no poetry line breaks), with a gap between verses (V1). Check: Psalm 23 has no poetry line breaks.
- **FR5.6** A verse that does not fit on the slide starts on the next slide. A verse is split only if it alone is taller than a slide, and then at a line boundary (V2). Check: one very long verse, and a passage whose last verse does not fit.
- **FR5.7** Slides of one passage are balanced, and the title repeats on each slide (V3). Balanced is a soft requirement. The paginator aims for slides of one passage, hymn or text item that differ by at most 2 lines. A larger difference is allowed when the keep-together rules (whole verses, whole hymn blocks, sentence boundaries) make 2 impossible. The testable part is that, among the valid break points, the paginator picks the split with the smallest line-count difference. Check: given a passage and its valid break points, the chosen split has the smallest line-count difference among them, and when a split with a difference of at most 2 exists, it is chosen.
- **FR5.8** Verse numbers are superscript (V4) and continuation lines line up with the start of the first line's text (V5). The hanging-indent method is decided by spike S2. Check: the rendered slide.
- **FR5.9** The reference line ("—Romans 8:13-14") follows the last verse as its own paragraph in the same text box, placed toward the right, and appears on the last slide only (V6). It is optional for a Bible Passage row and always shown for the Opening Passage and Offering. Several passages in one field each start a new slide (V7). Check: both, and a passage over several slides shows the reference on the last slide only.
- **FR5.10** A reference the ESV API cannot find shows a plain error on the row ("We couldn't find 'Jhon 3:16'"), and the row is left out of the run. An empty reference field is flagged the same way. Check: a misspelt and an empty reference.
- **FR5.11** The app respects the ESV limits: 500 verses per query or half a book, 5,000 queries a day, 1,000 an hour and 60 a minute. When a limit is reached, the row shows a plain reason and the run continues. Passage text is kept in memory for the session only, never stored. Check: a request over 500 verses reports a plain reason, and no passage text is on disk.

- **FR5.12** The Offering is a passage slide type, not a static slide (template slide 14, tag `autoppt:offering`). The user enters any passage in the same field, with the same `;` rule and the same `?` help button as a Bible Passage. The text goes in the `{Text}` box (one font size) under the template's fixed "OFFERING" heading, with the reference line after it (V6). A long passage continues onto more copies of slide 14. The Normal preset includes the Offering. Check: add an Offering row with one passage and with a long passage, see the heading, the text and the reference on the last slide only, and see the row in a new Normal service.

### FR6. Hymns: display rules (rules H1 to H6)

- **FR6.1** Blocks that share a slide are separated by a gap. A block that does not fit moves to the next slide, and a block taller than a slide is split evenly (H1, H2). Check: a tall block and a short block.
- **FR6.2** Lines are split so their lengths on a slide stay similar, and text is never left to soft-wrap (H3). A line splits at a comma, semicolon, exclamation mark or full stop between 40% and 80% of its length, and otherwise at the space nearest the middle. The slides of one hymn are balanced as FR5.7 says, which is a soft requirement that respects whole hymn blocks. Check: long lines, and that the split of a hymn into slides has the smallest line-count difference among its valid break points.
- **FR6.3** Repeats collapse to "(x2)", and adjacent short blocks merge when they fit (H4). Check: a hymn with a repeated block.
- **FR6.4** Slide titles read "TITLE (n/N)", where N is the total slides for that hymn: (1/5) to (5/5) for a five-slide hymn (H5). Long titles are measured and use a second size, or the body moves down. Check: a one-slide, a five-slide and a long-title hymn.
- **FR6.5** Each hymn gets one background chosen at random and shared by all its slides. No background repeats within a service until every one in the pool has been used, and then picks start again from the full pool (H6). Check: a service with more hymns than backgrounds.
- **FR6.6** A hymn with an `arrangement` in its file shows its blocks in that order, and otherwise in file order. Check: a hymn with and without an arrangement.
- **FR6.7** No CCLI numbers, copyright lines, song credits or credits slide appear on hymn slides. Check: a made PPT.

### FR7. Hymn editing and sharing (F3)

- **FR7.1** Choosing a hymn from a searchable dropdown (title, alternate titles, first line) fills the lyrics editor with the shared version. Edits apply to this service immediately, shared or not, and a locally edited hymn shows "Only on this computer". Check: search each way, edit, see the badge.
- **FR7.2** "Save for everyone", "Discard my changes" and "Delete for everyone" each show a confirmation first, naming the hymn and the effect. Cancel is the default button, Escape cancels, and Cancel changes nothing. Check: each of the three, cancelled and confirmed.
- **FR7.3** Confirmed "Save for everyone" writes the hymn to the shared library with a message such as "Update hymn: Love Incarnate (by Grace)". The write uses the last-known version of the file, so the first save wins. Check: save, and see the commit message.
- **FR7.4** A save based on an older copy, or a new hymn whose id already exists, is refused with "Someone else saved this hymn first. Choose Check for updates, then make your change again." The user's typed text stays in the service's row. Check: two clients, one saves first.
- **FR7.5** "Discard my changes" returns the hymn to the shared version and affects only unsaved local edits. Users cannot restore older versions. Check: edit, discard, compare.
- **FR7.6** "New hymn…" is the last dropdown entry. It takes a title and pasted lyrics, auto-split into blocks on blank lines and `[Chorus]`-style headers. The id is a lowercase ASCII slug of the title (`love-incarnate`), and a number is added when that id exists (`love-incarnate-2`). If the title yields no usable letters (for example a Chinese title), the id is "hymn-" plus a short random id. Clashes are checked case-insensitively. Check: a duplicate title, a title that differs only by case, and a Chinese title.
- **FR7.7** Confirmed "Delete for everyone" removes the hymn from the shared library, using the last-known version of the file, so the first-save-wins rule applies: if someone changed the hymn first, the delete is refused with the same "Check for updates" message. A service row already using the hymn keeps its lyrics for that service. Users cannot undo a delete, and the developer can restore it from history. Check: delete, delete after a change by someone else, and a service row using the deleted hymn.
- **FR7.8** The shared library refreshes on start (except first start) and on "Check for updates". The local copy is a cache for fast start-up. Saving, deleting and making slides need a connection. A refresh of changed files completes within 5 seconds (NFR3). Local hymn edits ("Only on this computer") are saved with the service row and survive closing the app. "New service" clears them. If Check for updates brings a newer shared version of a hymn that has a local edit, the local edit is kept and a "newer shared version available" badge appears on that hymn. Check: change a hymn elsewhere and press Check for updates; edit a hymn, close and reopen the app, and see the edit and the badge; start a new service and see the edit gone.
- **FR7.9** Files carry a `schemaVersion`. An older app refuses files newer than it understands and tells the user to update. Check: a file with a higher version.
- **FR7.10** A hymn file is read with UTF-8 and tolerates a byte-order mark and CRLF line ends. A malformed hymn file is skipped and reported, and every valid hymn still loads. Check: one bad file among good ones.

### FR8. Hymn migration (F3)

- **FR8.1** A one-time developer tool converts the 116 hymns of the legacy `HymnDatabase.db` into one file each in the shared library (id, title, language, aliases, optional arrangement, update fields, and lyric blocks). Check: 116 files, and a spot check of five hymns against the legacy database.
- **FR8.2** A hymn that cannot be converted is listed by the tool and does not stop the others. Check: a deliberately broken row.

### FR9. Text blocks (F4a, F5, F5a, rules T1 to T3)

- **FR9.1** Announcements and Supplications are ordered lists of text blocks, one per item, with add, remove and reorder, a title, and buttons for bold, italic, bullet list, numbered list and link. Check: each button.
- **FR9.2** Formatting is stored as CommonMark. Underline is not offered. Anything the app cannot render is shown as plain text with a warning. Check: a table in the text shows a warning.
- **FR9.3** An announcement takes one slide, and a long one continues on another slide with the same title, split at a sentence boundary and otherwise at a word (T1). Sentence boundaries set the valid break points, and the smallest-line-difference rule of FR5.7 picks among them, the same pattern as FR5.6 and FR5.7. Supplications are numbered items on as few slides as possible, and the slides of one item are balanced as FR5.7 says, which is a soft requirement that respects sentence boundaries. Check: a long announcement split at sentence boundaries has the smallest line-count difference among its valid break points, and a long list of supplications.
- **FR9.4** Catechism takes a title and a body written by the user in the same block editor, with two rows: this week and next week. Overflow continues on the next slide (T1). Check: a long body.
- **FR9.5** Spiritual Wisdom takes a title (default "SPIRITUAL WISDOM AND PROFOUND SAYINGS"), a quote of several paragraphs and a source. Paragraphs are separated by a gap and continue onto more slides when needed. The source line goes after the last paragraph on the last slide, prefixed with "— " and placed toward the right (T3). Check: a long quote.
- **FR9.6** Lists use the slides' own bullets and numbering (T2), and paragraphs are separated by a gap (T1). Check: a bulleted and a numbered announcement.
- **FR9.7** Text with very long unbroken words, emoji, combining marks and right-to-left text is stored and shown without breaking the layout. Check: one very long word on a slide.
- **FR9.8** Nothing for these types is stored centrally, and Google Sheets is not used. Check: no network call besides Slides and Drive when making them.

### FR10. Next Week's Schedule (F7)

- **FR10.1** The schedule editor is built from the template: one name field per role placeholder, labelled with the left-column text. A new role in the template becomes a new field with no code change. Check: add a role to a template copy.
- **FR10.2** The date is the Sunday after the service date, with the raised ordinal (FR4.3). For a service date that is not a Sunday, it is the next Sunday after that date. All dates use the computer's local time zone. Fields suggest names used before. Check: the date for a Sunday, a Saturday and a Monday service date.
- **FR10.3** An empty field prints "N/A". Values are written with a replace that keeps the cell formatting. Check: an empty role in a made PPT.

### FR11. Chinese Monthly Scripture (F12)

- **FR11.1** The row takes a title only (default 本月金句) and the slide is made with that title and an empty body. The panel says "This slide is made with an empty body. Add the Chinese verse picture in Google Slides after making the slides." Check: the made slide.
- **FR11.2** The progress view and the end-of-run summary list it as "To do in Google Slides: add the Chinese verse picture to slide N", and a reminder is placed in that slide's speaker notes. Check: the summary and the notes.

### FR12. Make slides (F2)

- **FR12.1** Make slides builds a new PPT from the template every time and opens it in the default browser. The link can also be copied. Check: two runs make two PPTs.
- **FR12.2** The steps are: copy the template into the user's Drive and mark the copy as a PPT made by the app; duplicate the slide with each row's tag, in list order (a row of several slides gets one duplicate each); fill placeholders and styles in batches; clear speaker notes on every generated slide except the F12 reminder; put the slides in list order; delete every original template slide, leaving only the service. Check: the made PPT has only the service slides in order and no leftover template slides.
- **FR12.3** The progress view shows each row as Done, Problem (plain reason), Skipped, To do in Google Slides, or In progress for the row being made. The count equals the finished rows over all rows. A run STOPS when it cannot go on: a lost connection, a sign-in failure, a template copy failure, or a full Google Drive. The rows not yet done then show Skipped, and the stopped-run rule (FR12.5) applies. A run FINISHES when every row was attempted. Individual rows may then be Problem, their slides are left out, and one failed row does not stop the run. Check: cut the connection mid-run (the rest show Skipped and the run is stopped), and run with one bad row (the run finishes with that row as Problem).
- **FR12.4** "Delete last week's slides when I make new ones" is on by default and can be turned off. Last week's PPT is moved to the Trash only when the run FINISHED with no Problem rows (FR12.3). After such a run, the app finds the user's most recent earlier PPT made by the app, owned by the user, and not the one just made, and moves only that one to the Drive Trash. A stopped run, or a run that finished with any Problem row, moves nothing, so the operator is never left with an incomplete PPT and no old one. PPTs shared by others are never touched. The summary names it: "Last week's slides ('…') were moved to the Trash. You can restore them from there for 30 days." Check: two earlier PPTs, only the newest moves; a shared one is untouched; with the option off, nothing moves.
- **FR12.5** When a run stops partway, such as a lost connection, the app tries to move the half-built PPT to the Trash. If it cannot, it leaves the file, tells the operator it is incomplete and where it is, remembers its id locally, and moves it to the Trash on the next successful run. A stopped run never deletes last week's PPT, and a half-built PPT is never treated as last week's. Check: cut the connection mid-run, then run again.
- **FR12.6** Before the first run, and when a copy fails, the app checks it can read the template. A 403 or 404 shows "Ask the developer to share the slide template with your Google account and allow copying." Check: a template that is not shared.
- **FR12.7** A made PPT matches the intended layout of the reference PPTs, not their manual mistakes (the missing Spiritual Wisdom slide, and hymn titles that read "(1/1)" over five slides). The measurable layout rules are FR5.7 and FR6.2 (balanced is a soft requirement: the paginator picks the valid split with the smallest line-count difference and aims for at most 2 lines; hymn line splitting keeps its 40% to 80% rule), and overall visual judgement stays with the developer. Check: compare a made PPT with the reference PPTs; the developer judges the layout.
- **FR12.8** On a Google rate-limit response (429) or a server error (5xx), the app retries up to 3 times with increasing waits, and then marks that row Problem with a plain reason. A response that says the Google Drive is full stops the run with "Your Google Drive is full", and the stopped-run rule (FR12.5) applies. Check: simulate a 429 that clears on the second try, a 429 that does not clear (the row ends as Problem after 3 retries), and a Drive-full response (the run stops with that message).

### FR13. Template and configuration (F6)

- **FR13.1** Template slides are found by a tag in their speaker notes, so moving slides needs no code change. Tags are unique, except the hymn-background tag, which marks a pool. The app uses the developer's template v3 (Google Slides id `1Njchznjkd2toS1DqIvAStjP0w4gyMNq9S8j1q8v9U_E`, 34 slides, set in the app configuration). The `autoppt:` tags are not in the template's speaker notes yet: the developer adds them, and "Check template" reports every missing tag (FR13.4). Check: shuffle the template and make a PPT, and run "Check template" on the untagged template to see every tag reported missing.
- **FR13.2** The template carries the placeholders `{Title}` and `{Text}`, and the schedule roles, and the app keeps them as they are. The Opening Passage slide and the Offering slide have a `{Text}` box only. There is no `{Reference}` placeholder, because the reference is the last paragraph of `{Text}` (FR5.1, FR5.12). Check: the "Check template" run.
- **FR13.3** The configuration (app, slide types, presets, layout values and fonts) lives in the shared library and is maintained by the developer. The slide types and the template are read-only in the app. New slide types cannot be created in the app. Check: no editor for them exists.
- **FR13.4** A developer-only "Check template" command confirms: every tag in the slide types exists, and is unique except the hymn-background pool; each tagged slide has the placeholders its layout kind needs; there is at least one hymn background; every font used in a placeholder box has a measurement font, and a font without one is an error. The font check looks at fonts in placeholder boxes only: fonts in fixed static text, which is never measured, are not checked. Check: a template copy with a missing tag, a missing placeholder and an unknown font in a placeholder box (an error), and a different font in a static text box (not reported).
- **FR13.5** Template edits are made on a copy. Once "Check template" passes, the copy's id replaces the old one in the app configuration. Check: the id swap.
- **FR13.6** The configuration lists templates with a language so a Chinese template can be added later, and all text is in resource files so Chinese can be added later. There is no language selector in the first version. Check: no hard-coded UI strings.

### FR14. Settings (F10)

- **FR14.1** Settings holds: delete last week's slides (on or off), your name, theme (light or dark), replace setup file, sign out of Google, and reload all hymns (FR14.5). Check: each control.
- **FR14.2** Replace setup file follows FR2.9, and Sign out of Google removes the refresh token, after which the app asks to sign in again. Check: sign out, then make slides.
- **FR14.3** Changing the name updates the greeting. Check: change the name.
- **FR14.4** The app version, for example "AutoPPT 1.0.0", shows at the bottom of Settings, on the start-up screen and in "Copy details for support". Check: the version in all three places.
- **FR14.5** Settings has a "Reload all hymns" button. It first asks "Replace every hymn on this computer with the shared version? Changes you haven't saved for everyone will be lost." with Cancel as the default button and Escape cancelling. Cancel changes nothing. When confirmed, it discards this computer's copy of the shared library and every unsaved hymn edit, including edits kept in the current service's rows, and downloads every hymn again from the shared library. The download goes into a temporary copy first, and only when every hymn has arrived does it replace the local copy and clear the unsaved edits. While it downloads, the same blocking progress as other downloads shows. If the download fails, nothing changes and the app shows "Couldn't reload the hymns. Nothing was changed." It never changes the shared library. Check: edit a hymn without saving, reload and see the edit gone from the hymn and from the service row; cancel and see nothing change; check the shared library is unchanged; cut the connection mid-download and see the message with every hymn and edit as it was.

### FR15. Errors (F9)

- **FR15.1** A local rolling log file sits in the OS app-data folder, and nothing is sent anywhere. Check: no outgoing request carries log data.
- **FR15.2** Problems with what the user typed show on the row before Make slides. Runtime problems open a dialog with a plain summary and what to try, "Copy details for support" and "Open log folder". Check: each part.
- **FR15.3** "Copy details for support" copies the version, operating system, Java version, error code, stack trace and the affected row. Check: the copied text.
- **FR15.4** One redactor runs before anything reaches the log file or the details text. It removes authorization header values, the OAuth client secret, refresh and access tokens, the `key`, `token`, `code` and `client_secret` values, and any value read from the setup file. Passage text is not logged. Check: a unit test with known secrets finds none in the log or details.
- **FR15.5** Each key failure maps to one plain message: an ESV key or shared-library token rejected (401 or 403) shows "Ask the developer for a new setup file."; a Google sign-in that is no longer valid asks the user to sign in again; a disabled or deleted Google client shows "Ask the developer for a new setup file." Check: each case.
- **FR15.6** A lost connection is reported as "The internet connection was lost" rather than a generic error (FR1.6). Check: cut the connection in a run.
- **FR15.7** The UI never says "GitHub", "repo", "commit", "API" or "token". It uses "shared library", "Check for updates", "Save for everyone", "Delete for everyone", "Discard my changes", "Setup file", "Sign in with Google", "Slide" and "Slide type". Check: a text search of the UI strings.

### FR16. Distribution and update check (F11)

- **FR16.1** The app ships as a zipped self-contained app image for each of Windows x64, macOS Apple Silicon and macOS Intel, with a bundled Java runtime. There is no installer, no `.msi`, no `.dmg` and no code signing. The user unzips it once and double-clicks it. Check: run each zip on a clean machine.
- **FR16.2** Settings and secrets live in the OS app-data folder, not next to the app, so replacing the folder with a new version keeps them. Check: replace the folder and see settings kept.
- **FR16.3** During "Checking for updates…" the app makes one unauthenticated request to GitHub's public latest-release endpoint for AutoPPT, with a 5-second timeout. Only the latest published release counts, and "newer" means a higher major.minor.patch version. Check: a newer, an equal, an older and a pre-release version.
- **FR16.4** If the release is newer, the start-up screen shows "A new version of AutoPPT is available (x.y.z)" with Download and Continue. Continue is the default button and Escape means Continue. Download opens the zip for this computer in the browser, and the screen stays open. Check: both buttons.
- **FR16.5** If the check fails or times out, or the release is not newer, start-up carries on silently with no message. The app never downloads or replaces itself. Check: a failed request shows no screen, and no file is written to the app folder.
- **FR16.6** A short first-run guide covers the Windows SmartScreen warning ("More info", then "Run anyway") and the macOS steps (open once, then Privacy and Security, then "Open Anyway"). Check: the guide exists and covers both.

### FR17. Start-of-work: the first slice and the spikes

- **FR17.1** Spikes S1 to S3 run before the rest of the build: text measurement against what Slides renders, the hanging indent with a superscript number, and Google sign-in as an unverified app. If a spike fails, its documented fallback is used (scope-document.md Sequencing; feasibility-questions.md Q7). Check: each spike has a recorded pass or fail.
- **FR17.2** The first slice built is the walking skeleton: the start-up screen and internet check (FR1), the setup file and Google sign-in (FR2.3 to FR2.7), the template reader (FR13.1), and one Bible passage slide written into a real PPT (FR5, FR12). Check: the slice makes a real PPT end to end.

## Non-Functional Requirements

- **NFR1** Speed of making slides: at most 2 minutes for a PPT of up to 60 slides on a normal home internet connection. This is a provisional target, which the developer may raise after measuring real runs (spike S1 and the walking skeleton) (Q2). Check: time a 60-slide build.
- **NFR2** Start-up speed: the main window opens within 10 seconds of launch when the connection works (Q3). The 10 seconds covers all start-up steps: the internet check, the version check, the library refresh and loading the saved keys. It applies when each check answers normally. A timeout is the failure path, with its own message and behaviour: a library refresh has its own 5-second timeout (FR1.4), and the internet and version checks have theirs (FR1.2, FR16.3). Check: time start-up on both operating systems with every check answering normally, and separately test each timeout path for its message.
- **NFR3** Size: the app handles a PPT of up to 100 slides and a shared library of up to 500 hymns (Q4), and these targets are provisional, which the developer may raise after measurement like NFR1 (Q7): (a) a PPT of up to 100 slides is made within 3.5 minutes on a normal home internet connection; (b) a refresh of changed shared-library files completes within 5 seconds; (c) the first full download of 500 hymns completes within 30 seconds. Check: time a 100-slide build, a refresh of changed files, and a first download of a 500-hymn library.
- **NFR4** Operating systems: Windows 10 and 11 (x64), and macOS 12 or later on Apple Silicon and Intel (Q6). Check: a run on a Windows 10, a Windows 11, a macOS 12 and a newer macOS machine, on both chip types for macOS.
- **NFR5** Ease of use: a non-technical operator builds the Sunday PPT in under 10 minutes, with only the Chinese Monthly Scripture picture added by hand, checked by the developer timing one operator's first full build (intent-statement.md Success Metrics). Check: that timing.
- **NFR6** Quality of slides: verse, hymn and text slides need no touch-up on 4 consecutive Sundays after launch (intent-statement.md Success Metrics). Check: four Sundays of use.
- **NFR7** Retirement of the legacy app: operators stop using it, checked after 4 consecutive Sundays on the new app (intent-statement.md Success Metrics). Check: that review.
- **NFR8** Keyboard and contrast: every screen is fully usable by keyboard alone, and text keeps a contrast ratio of at least 4.5:1 in both the light and dark themes. Screen-reader support is not part of the first version (rough-mockups-questions.md Q2, Q5). Check: a keyboard-only walk-through and a contrast measure of each theme.
- **NFR9** Secrets: keys and the Google refresh token are stored only in `secrets.json`, readable only by the user, with no OS keychain (design document D25). Secrets never reach logs or support details (project rules; FR15.4). Check: file permissions and a log search after a full run.
- **NFR10** Privacy and network use: no remote logging, no data sent anywhere except Google, the shared library, the ESV API and the update check, and no cloud hosting (constraint-register.md C-T4). Check: a network capture of a full run.
- **NFR11** Reliability: no accepted typed work is lost to a crash, a lost connection or a full disk. The service file and `secrets.json` are written by writing a temporary file and then renaming it, and a full disk fails with a clear message while the previous good file survives. Check: kill and fill-disk tests.
- **NFR12** Compatibility of data: an older app refuses newer configuration or hymn files with an update message (FR7.9), and a newer app reads older ones. Check: both directions.
- **NFR13** Testing: the `core` module (layout, pagination and other pure logic) has an 80% line-coverage floor enforced in the automated build, with no floor on the Swing UI or the Google, ESV and library adapters. Network clients are tested against fakes. A manual check of one real PPT built in Google Drive runs before each release (team-practices.md). Check: the build fails when line coverage on `core` is below 80%.
- **NFR14** Code style: Java 21 with the formatter Spotless using google-java-format enforced in the build, Guice 7 constructor injection only, and no Checkstyle (team-practices.md; constraint-register.md C-T1). Check: the build fails on a Spotless violation.
- **NFR15** Fonts and licensing: placeholder boxes in the template use only Century Gothic, measured with TeX Gyre Adventor, which is the only font bundled. Arial appears only in fixed static text, which is never measured, and Times New Roman is not used. Each bundled font's licence file ships next to it (constraint-register.md C-T5, C-R4). Check: the zip holds only TeX Gyre Adventor and its licence file, and "Check template" flags an unmeasurable font in a placeholder box.
- **NFR16** Free services: the app uses only free services and free fonts (constraint-register.md C-T8). Check: no paid dependency in the build.
- **NFR17** Release safety: each release is published as a draft first, checked, then published by hand, so users see the update prompt only after the check. Releases are tagged `vMAJOR.MINOR.PATCH` (team-practices.md). Check: the release process record.
- **NFR18** Wording: artifacts and the UI say "PPT" or "slides", never "deck" (project rules). Check: a text search of UI strings and generated PPT text for the word "deck", which finds none. The search does not cover project documents, which quote the word in this rule.

## Constraints

- Java 21, Gradle 9.x, Swing with FlatLaf, and Google Guice 7 with constructor injection only (constraint-register.md C-T1).
- Windows and macOS only, as a self-contained app with no installer and no cloud hosting (C-T2 to C-T4).
- Free services and free fonts only (C-T8).
- The ESV conditions of use ask for "ESV" with each quotation and forbid sharing a key. Both are accepted risks (constraint-register.md C-R2).
- Google's unverified-app cap of 100 users applies, well above fewer than 10 operators (C-R5).
- The developer alone decides scope and priority (C-O1). There is no fixed launch date (C-O2).

## Assumptions

- The church's Google Slides template (v3, id `1Njchznjkd2toS1DqIvAStjP0w4gyMNq9S8j1q8v9U_E`) follows the design document's tags and placeholders once the developer adds the `autoppt:` tags, and is shared with each operator with copying allowed. Reason: the app cannot make slides without it, and FR12.6 and FR13.4 report when it is not.
- TeX Gyre Adventor widths match Century Gothic closely enough. Reason: this stays a hypothesis until spike S1 (high-level-design.md D20).
- The operators' computers have an internet connection and a default web browser. Reason: the app requires the connection (FR1) and opens the PPT and Google sign-in in the browser.
- The GitHub latest-release endpoint stays public and unauthenticated. Reason: the update check reads only that endpoint (FR16.3).
- The 4 Oct reference PPT's missing Spiritual Wisdom slide and "(1/1)" hymn titles are manual mistakes (high-level-design.md section 2.2). Reason: FR12.7 asks the app to follow the intended layout.

## Out of Scope

- Presenting or editing slides in the app, previewing slides, generating the Chinese pinyin picture, creating slide types or editing the template in the app.
- Built-out Chinese support, and the Chinese language selector (the groundwork marked "now" in the design document stays).
- Restoring older hymn versions, merging concurrent edits, remote logging, Google Sheets input, and code signing.
- An installer, and any automatic download or replacement of the app by itself.
- Screen-reader support.

## Open Questions

- Whether a PPT must be openable offline after it is made. This is a Google Slides matter, not the app's, so no requirement is written (high-level-design.md F0).
- The exact line-spacing, safety margin and inner-padding values are set by spike S1 and held in the layout configuration.
- The exact Gradle module boundaries are decided in units generation. The module names core, google, esv, library and app are the design document's suggestion (team-practices.md).
