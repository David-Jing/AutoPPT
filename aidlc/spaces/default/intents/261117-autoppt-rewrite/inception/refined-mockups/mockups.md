# Refined Mockups: AutoPPT Rewrite

Inputs: `wireframes.md` and `user-flow.md` (the rough mockups), `stories.md` (user stories), `requirements.md`, `team-practices.md`, and `refined-mockups-questions.md` (Q1 to Q7). Each screen below says which stories it serves (US ids) and which requirements it follows (FR and NFR ids). Wording follows the project rules: "PPT" or "slides" for the output, and no "GitHub", "repo", "commit", "API" or "token" on any screen. The Swing and FlatLaf mapping is in `design-system-mapping.md`, the component behaviour in `interaction-spec.md`, and the accessibility checks in `accessibility-checklist.md`.

## Decided here (from Q1 to Q7)

- **Main window size (Q2):** opens at 1100 by 700 pixels, can be resized, has a minimum of 900 by 600, and remembers its last size and position between launches.
- **Progress view (Q3):** a dialog over the main window that blocks it until the run ends, then offers Open in browser, Copy link and Close.
- **Lyrics editor (Q4):** one text area with `[Verse 1]`-style headers typed in, plus a small hint about the header format.
- **Colour (Q5):** the theme's default accent colour. Every state is shown with both a symbol and a word as well as any colour.
- **States, patterns and wording (Q1):** as listed in the questions file and confirmed.
- **Escape (Q6):** Escape cancels or closes any dialog, except the no-connection screen M0b (does nothing), the new-version screen M0c (means Continue) and the connection-lost cover M2d (does nothing). The progress dialog ignores Escape during a run, and after the run Close is the default button and Escape closes it. First-start wizard steps are not dialogs: Escape does nothing there (Q7).
- **Wizard Escape (Q7):** on every first-start wizard step, including the sign-in-again step, Escape does nothing. The wizard is a full window: Back goes back, and closing the window quits the app.
- **Reload all hymns (Q7):** a Settings button that, after a confirmation with Cancel as the default and Escape cancelling, downloads every hymn into a temporary copy. Only when every hymn has arrived does it replace this computer's copy of the shared library and discard every unsaved hymn edit (including edits kept in the current service's rows). If it fails, nothing changes and the app says "Couldn't reload the hymns. Nothing was changed." It never changes the shared library (FR14.5).
- **Theme (Q6):** the theme applies at once when it is chosen in Settings.
- **Hymn buttons (Q6):** Save for everyone and Discard my changes are enabled only when the hymn has edits. Delete for everyone is enabled when a shared hymn is chosen.
- **Row buttons (Q6):** Move up and Move down are visible buttons next to Add and Remove, as well as Alt+Up and Alt+Down.
- **Passage help (Q6):** a `?` help button replaces a hover tooltip, because a tooltip cannot be reached by keyboard.
- **Closing during start-up (Q6):** closing the window during the start-up check quits the app.
- **Extra screens (Q6):** "AutoPPT is already open", the sign-in-again step, and the result of Replace setup file.

## Proposed here (starting values, finalised in code)

- **Dialog and window sizes:** the sizes named in this file (start-up 420 by 240, confirmations 440 by 200, Settings 480 by 400, progress 520 by 420, wizard 560 by 420, and the 320-pixel list width) are starting values. They are finalised in code. The main window's 1100 by 700 start and 900 by 600 minimum are decided (Q2).

## Conventions

- A box line `┌ ─ ┐` is a window or dialog. `[ Text ]` is a button, with `[ Text ]*` marking the default button. `[____]` is a text field. `▾` is a drop-down. `☑` and `☐` are check boxes, `(•)` and `( )` are radio buttons. `≡` is a drag handle.
- State symbols always come with a word: `✔ Done`, `⚠ Problem`, `– Skipped`, `✎ To do in Google Slides`, `⟳ In progress`, and `● Only on this computer`.
- Keyboard: every control is reachable by Tab, Shift+Tab goes back, Enter presses the default button, and Space toggles a check box or radio button (NFR8).

## M0. Start-up screen (stories US1.1, US1.2, US16.2; FR1, FR16)

Size (starting value): a small fixed-size window of 420 by 240 pixels, centred on the screen. The app name, the version and one status line always show.

### M0a. Checking

```
┌ AutoPPT ────────────────────────────────┐
│                                         │
│                AutoPPT                  │
│              Version 1.0.0              │
│                                         │
│   Checking your internet connection…    │
│                                         │
└─────────────────────────────────────────┘
```

- The status line changes in this order: "Checking your internet connection…", "Checking for updates…", "Getting ready…". On first start, or when the saved keys are missing or replaced, "Getting ready…" is skipped and the library is loaded in wizard step 4 (FR1.4).
- No control takes focus while it checks. Closing the window during the check quits the app (Q6).

### M0b. No connection

```
┌ AutoPPT ────────────────────────────────┐
│                AutoPPT                  │
│              Version 1.0.0              │
│                                         │
│   ⚠ You're not connected to the         │
│     internet. Connect, then choose      │
│     Try again.                          │
│                                         │
│         [ Try again ]*   [ Quit ]       │
└─────────────────────────────────────────┘
```

- Try again is the default button and has focus. Escape does nothing. The app also re-checks by itself every 5 seconds (FR1.3).

### M0c. A new version is available

```
┌ AutoPPT ────────────────────────────────┐
│                AutoPPT                  │
│              Version 1.0.0              │
│                                         │
│   A new version of AutoPPT is           │
│   available (1.1.0)                     │
│                                         │
│       [ Download ]    [ Continue ]*     │
└─────────────────────────────────────────┘
```

- Continue is the default button and has focus. Escape means Continue. Download opens the zip for this computer in the web browser and the screen stays open (FR16.4).
- If the version check fails or times out, or the release is not newer, this screen does not appear and start-up carries on silently (FR16.5).

### M0d. Library not available

```
┌ AutoPPT ────────────────────────────────┐
│                AutoPPT                  │
│              Version 1.0.0              │
│                                         │
│   ⚠ Couldn't get the shared hymn        │
│     library. Check your connection      │
│     and choose Try again.               │
│                                         │
│         [ Try again ]*   [ Quit ]       │
└─────────────────────────────────────────┘
```

- This shows when the library cannot be refreshed and no cached copy exists. The main window does not open (FR1.4, AC1.1.4). When a cached copy exists, the app instead uses it, opens the main window and shows a one-line "Couldn't check for hymn updates" note in the bottom bar (FR1.4).

## M1. First-start wizard (stories US2.1 to US2.3; FR2)

Size (starting value): a fixed 560 by 420 pixel window, centred. One step per screen, with a "Step n of 4" marker at the top, Back at the bottom left of the button row, and Next (or Finish) at the right as the default button. Escape does nothing on any step (decided, Q7). Back goes back, and closing the window quits the app.

### M1a. Step 1 of 4: Setup file

```
┌ AutoPPT: Welcome ───────────────────────────────────────┐
│ Step 1 of 4   Setup file                                │
│                                                         │
│ ┌─────────────────────────────────────────────────────┐ │
│ │      Drop your setup file here    [ Browse… ]       │ │
│ └─────────────────────────────────────────────────────┘ │
│                                                         │
│ ✔ Scripture text is ready                               │
│ ✔ Shared library access is ready                        │
│                                                         │
│ [ Delete the setup file now ]                           │
│                                                         │
│                           [ Back ]off   [ Next ▸ ]*off  │
└─────────────────────────────────────────────────────────┘
```

- The two checks show only after a file is dropped in. A failed check shows `⚠` and names the part in plain words, for example "⚠ The scripture key doesn't work. Ask the developer for a new setup file." (AC2.1.3). Back is off on step 1, and Next is off until the file is accepted. "Delete the setup file now" shows only after the file is accepted.

### M1b. Step 2 of 4: Your name

```
┌ AutoPPT: Welcome ───────────────────────────────────────┐
│ Step 2 of 4   Your name                                 │
│                                                         │
│ What should we call you?                                │
│ [______________________]                                │
│ Used when you save a hymn for everyone, and to greet    │
│ you.                                                    │
│                                                         │
│                           [ Back ]      [ Next ▸ ]*off  │
└─────────────────────────────────────────────────────────┘
```

- The name field has focus. Next is off until a name is typed (AC2.2.1).

### M1c. Step 3 of 4: Sign in with Google

```
┌ AutoPPT: Welcome ───────────────────────────────────────┐
│ Step 3 of 4   Sign in with Google                       │
│                                                         │
│ [ Sign in with Google ]*  (opens your web browser)      │
│ ✔ Signed in as grace@example.com   (after sign-in)      │
│                                                         │
│                           [ Back ]      [ Next ▸ ]off   │
└─────────────────────────────────────────────────────────┘
```

- Sign in with Google has the initial focus and is the default button. Next stays off until sign-in succeeds, and then becomes the default button (decided, R-10 fix).
- A failure caused by the setup file shows "⚠ This setup file doesn't work with Google sign-in. Ask the developer for a new setup file." (AC2.2.3).

### M1d. Step 4 of 4: Shared library

```
┌ AutoPPT: Welcome ───────────────────────────────────────┐
│ Step 4 of 4   Shared library                            │
│                                                         │
│ Downloading the shared library…                         │
│ ▓▓▓▓▓▓░░░░░░░░░░  38%                                   │
│ ✔ Shared library is ready   (after the download)        │
│                                                         │
│                          [ Back ]     [ Finish ▸ ]*off  │
└─────────────────────────────────────────────────────────┘
```

- Finish is off until the download is done, then opens the main window (AC2.3.2).

## M2. Main window (stories US3.1 to US3.5, US4.1; FR3, FR4)

Size: opens at 1100 by 700 pixels, resizes freely down to 900 by 600, and remembers its size and position (Q2). The left list is 320 pixels wide and the settings panel takes the rest. The right panel scrolls when a window is small.

```
┌ AutoPPT ─────────────────────────────────────────────────────────────────────────┐
│ Hi Grace    Sunday 22 Nov 2026 ▾    Order: Normal ▾    [ New service ]  [ Make slides ▸ ]* │
├────────────────────────────────┬─────────────────────────────────────────────────┤
│ ≡ Opening Passage   John 8:12  │ Hymn                                            │
│ ≡ Offering          Ps 96:8-9  │                                                 │
│ ≡ Sunday Service Header        │ Choose a hymn                                   │
│ ≡ Monthly Scripture            │ [ Love Incarnate                            ▾ ] │
│ ≡ Chinese Monthly Scripture ✎  │                                                 │
│ ≡ Announcements            (5) │ Lyrics                ● Only on this computer   │
│ ≡ Catechism                    │ ┌─────────────────────────────────────────────┐ │
│ ≡ Spiritual Wisdom             │ │ [Verse 1]                                   │ │
│ ≡ Worship Header               │ │ Love incarnate, Love divine …               │ │
│▶≡ Hymn: Love Incarnate         │ │ [Chorus]                                    │ │
│ ⚠ Hymn: (none chosen)          │ │ …                                           │ │
│ ≡ Next Week's Schedule         │ └─────────────────────────────────────────────┘ │
│                                │ Put each part under a header such as [Verse 1]  │
│                                │ or [Chorus].                                    │
│ [ + Add slide ▾ ] [ Remove ]   │ [ Discard my changes ] [ Save for everyone ]    │
│ [ Move up ] [ Move down ]      │ [ Delete for everyone ]                         │
├────────────────────────────────┴─────────────────────────────────────────────────┤
│ Check for updates     Settings     Signed in as grace@example.com                │
└──────────────────────────────────────────────────────────────────────────────────┘
```

- **Top bar:** "Hi [name]" first, then the service date (a drop-down date picker defaulting to the coming Sunday), the order drop-down (Normal, Holy Communion), New service, and Make slides as the default button (FR3.1, FR3.2).
- **List rows:** a drag handle, the slide type, and a short summary. A row that cannot be made shows `⚠` and its plain reason in the right panel (FR3.6). The selected row is marked `▶` and highlighted with the theme accent plus the symbol.
- **Bottom bar:** Check for updates, Settings, and the signed-in account. The note "Couldn't check for hymn updates" shows here when the start-up refresh failed (FR1.4).
- **Offering row:** the Normal order includes it (FR3.3).
- **Reorder:** drag the handle, use the visible Move up and Move down buttons, or select a row and press Alt+Up or Alt+Down (FR3.4; Q6). The buttons sit under the list beside Add and Remove, as in the layout below.

### M2a. Row warnings

```
│ ⚠ Hymn: (none chosen)          │ ⚠ Choose a hymn                                 │
│ ⚠ Sermon Passage    Jhon 3:16  │ ⚠ We couldn't find 'Jhon 3:16'                  │
```

- Make slides stays on. A row that cannot be made is left out of the run (FR12.3).

### M2b. Empty service

```
│                                │ This service has no slides yet.                 │
│ (no rows)                      │ Choose an order above to fill in the usual      │
│ [ + Add slide ▾ ]              │ slides, or add slides one at a time.            │
```

### M2c. No hymns yet

```
│ Hymn                           │ No hymns yet.                                   │
│                                │ Choose "Check for updates" or add one with      │
│                                │ "New hymn…".                                    │
│                                │ [ Love Incarnate                            ▾ ] │
```

### M2d. Connection lost

```
┌ AutoPPT ─────────────────────────────────────────────────────────────────────────┐
│ Hi Grace    Sunday 22 Nov 2026 ▾                    (the window sits behind)     │
│ ┌──────────────────────────────────────────────────────────────────────────────┐ │
│ │ ⚠ You're not connected to the internet. Connect, then choose Try again.      │ │
│ │                                          [ Try again ]*   [ Quit ]           │ │
│ └──────────────────────────────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────────────────────────────┘
```

- The cover blocks every control behind it. Try again is the default button and has focus. Escape does nothing. The cover closes by itself when the connection returns, and typed work stays (FR1.6).

## M3. Editors for each slide type (stories US4.1, US4.2, US5.1 to US5.7, US9.1 to US9.5, US10.1, US10.2, US11.1)

Each editor fills the right panel. Every field has a visible label above it, and the first field has focus when its row is selected.

### M3a. Opening Passage

```
│ Opening Passage                                                       │
│ Passage                                                               │
│ [ John 8:12                                                       ?]  │
│ The reference is added after the passage.                             │
```

- No title field (FR5.1).

### M3b. Offering

```
│ Offering                                                              │
│ Passage                                                               │
│ [ Psalm 96:8-9                                                    ?]  │
│ The text goes under the OFFERING heading, with the reference after it.│
```

- No title field, because the template's heading is fixed (FR5.12).

### M3c. Bible Passage

```
│ Monthly Scripture                                                     │
│ Title (optional)                                                      │
│ [                                                                  ]  │
│ Passage                                                               │
│ [ Romans 8:13-14                                                  ?]  │
│ ☐ Show the reference after the passage                                │
```

- The `?` help button next to a passage field opens a small pop-up: "What works: Genesis 1:1-10, John 3:16, 18, Psalm 23; Romans 8:28. What doesn't: Chinese book names, other translations. Separate passages with a semicolon." (FR5.2). This replaces the hover tooltip that the requirement first described, because a tooltip cannot be reached by keyboard (decided, Q6). Keyboard users press the `?` button with Enter or Space, and Escape closes the pop-up.
- An error shows under the field: "⚠ We couldn't find 'Jhon 3:16'" (FR5.10).

### M3d. Chinese Monthly Scripture and Title-only

```
│ Chinese Monthly Scripture                                             │
│ Title                                                                 │
│ [ 本月金句                                                         ]  │
│ This slide is made with an empty body. Add the Chinese verse picture  │
│ in Google Slides after making the slides.                             │
```

- Title-only has the same single title field and the note "This slide is made with a title and an empty body. Finish it in Google Slides after making the slides." (FR4.2, FR11.1).

### M3e. Hymn

Shown in M2: the drop-down searches title, other titles and first line, with "New hymn…" as its last entry. The lyrics are one text area with a small hint under it about the `[Verse 1]` header format (Q4). The three buttons are Discard my changes, Save for everyone and Delete for everyone.

- **Button rules (decided, Q6):** Save for everyone and Discard my changes are on only when the hymn has edits. Delete for everyone is on when a shared hymn is chosen.

A hymn that has a local edit and a newer shared version shows a badge beside the lyrics, and keeps the local edit (FR7.8, AC7.6.2):

```
│ Lyrics     ● Only on this computer      ⚠ A newer shared version is available │
│ ┌─────────────────────────────────────────────────────────────────────────┐ │
│ │ [Verse 1]                                                               │ │
│ │ Love incarnate, Love divine …   (your edit, kept)                       │ │
│ └─────────────────────────────────────────────────────────────────────────┘ │
```

"New hymn…" opens this dialog:

```
┌ New hymn ───────────────────────────────────────────────────┐
│ Title                                                       │
│ [ Love Incarnate                                        ]   │
│ Lyrics                                                      │
│ [ Paste the lyrics here. Leave a blank line between parts,  │
│   or put each part under a header such as [Chorus].       ] │
│                                                             │
│                        [ Cancel ]      [ Add hymn ]*off     │
└─────────────────────────────────────────────────────────────┘
```

- Add hymn is off until a title and lyrics are typed, then it is the default button. Escape cancels. The lyrics split into blocks on blank lines and `[Chorus]`-style headers, and the id is made from the title (FR7.6).

### M3f. Announcements and Supplications

```
│ Announcements                                                         │
│ Title  [ ANNOUNCEMENTS                                            ]   │
│ ┌ Item 1 ─────────────────────────────────────────────────────────┐   │
│ │ [B] [I] [•] [1.] [Link]                       [↑] [↓] [Remove]  │   │
│ │ Youth retreat sign-up closes Friday.                            │   │
│ └─────────────────────────────────────────────────────────────────┘   │
│ [ + Add item ]                                                        │
```

- Supplications uses the same editor with its own default title (FR4.2, FR9.1).

### M3g. Catechism

```
│ Catechism                                                             │
│ Title  [ THE WESTMINSTER SHORTER CATECHISM Q&A #71                ]   │
│ Body   [B] [I] [•] [1.] [Link]                                        │
│        [ Q71: What is required in the seventh commandment? …      ]   │
```

### M3h. Spiritual Wisdom

```
│ Spiritual Wisdom                                                      │
│ Title   [ SPIRITUAL WISDOM AND PROFOUND SAYINGS                    ]  │
│ Quote   [B] [I] [•] [1.] [Link]                                       │
│         [ several paragraphs …                                     ]  │
│ Source  [ Imitation of Christ by Thomas Kempis                     ]  │
```

### M3i. Header and Sermon Header

```
│ Sermon Header                                                         │
│ Title    [                    ]     Speaker  [                    ]   │
│ The date fills in automatically.                                      │
```

- The Header has the title only.

### M3j. Next Week's Schedule

```
│ Next Week's Schedule                                                  │
│ Date: Sunday 29 Nov 2026 (set from the service date)                  │
│ Speaker      [                ▾ ]   Leader       [                ▾ ] │
│ Accompanist  [                ▾ ]   Audio        [                ▾ ] │
│ Streaming    [                ▾ ]   Announcer    [                ▾ ] │
│ Usher        [                ▾ ]   Offering     [                ▾ ] │
│ Welcome      [                ▾ ]                                     │
│ One field for each role in the template. An empty field prints N/A.   │
```

- Each field is an editable drop-down that suggests names used before (FR10.1, FR10.2). The roles follow the template, so a new role adds a field with no code change.

### M3k. Static

```
│ Lord's Prayer                                                         │
│ This slide is copied as-is.                                           │
```

- Static slides are the Lord's Prayer, the Creed, Holy Communion, the Doxology and the Benediction. The Offering is not static (FR4.2).

## M4. Make-slides progress dialog (stories US12.1 to US12.5; FR12)

A modal dialog over the main window, 520 by 420 pixels as a starting value. It blocks the window behind it until the run ends (Q3). The dialog ignores Escape during a run. After the run, Close is the default button and Escape closes it (decided, Q6). A row is Skipped only after a stopped run (FR12.3).

### M4a. A run in progress

```
┌ Making your slides ─────────────────────────────────────────┐
│ ✔ Done          Opening Passage                             │
│ ✔ Done          Offering                                    │
│ ⚠ Problem       Sermon Passage: We couldn't find 'Jhon 3:16'│
│ ✎ To do in Google Slides   Chinese Monthly Scripture:       │
│                 add the picture to slide 5                  │
│ ⟳ In progress   Hymn: Love Incarnate                        │
│   Waiting       Next Week's Schedule                        │
│ ▓▓▓▓▓▓▓▓▓░░░░░  4 of 6 rows finished                        │
│                                                             │
│ [ Open in browser ]off  [ Copy link ]off      [ Close ]*off │
└─────────────────────────────────────────────────────────────┘
```

- While the run goes, the three buttons are off, and Escape does nothing. A row that cannot be made is Problem and the run goes on.

### M4b. A finished run

```
┌ Making your slides ─────────────────────────────────────────┐
│ ✔ Done          Opening Passage                             │
│ ✔ Done          Offering                                    │
│ ⚠ Problem       Sermon Passage: We couldn't find 'Jhon 3:16'│
│ ✎ To do in Google Slides   Chinese Monthly Scripture:       │
│                 add the picture to slide 5                  │
│ ✔ Done          Hymn: Love Incarnate                        │
│ ✔ Done          Next Week's Schedule                        │
│ ▓▓▓▓▓▓▓▓▓▓▓▓▓▓  6 of 6 rows finished                        │
│ Last week's slides were kept, because a row had a problem.  │
│                                                             │
│ [ Open in browser ]   [ Copy link ]            [ Close ]*   │
└─────────────────────────────────────────────────────────────┘
```

- When every row was attempted, the count reads "6 of 6 rows finished", the buttons turn on, Close is the default button, and the PPT opens in the default browser (FR12.1).
- Last week's PPT is moved to the Trash only when no row ended as Problem, and then the dialog says: "Last week's slides ('Sunday Worship …') were moved to the Trash. You can restore them from there for 30 days." (FR12.4). With a Problem row, as above, it says the earlier slides were kept.

### M4c. A stopped run

```
┌ Making your slides ─────────────────────────────────────────┐
│ ⚠ The run stopped: The internet connection was lost.        │
│ ✔ Done          Opening Passage                             │
│ ✔ Done          Offering                                    │
│ – Skipped       Monthly Scripture                           │
│ – Skipped       Hymn: Love Incarnate                        │
│ – Skipped       Next Week's Schedule                        │
│ The unfinished PPT was left in your Google Drive. It is     │
│ incomplete, and it will be moved to the Trash the next time │
│ slides are made successfully. Last week's slides were kept. │
│                                                             │
│ [ Open in browser ]off  [ Copy link ]off      [ Close ]*    │
└─────────────────────────────────────────────────────────────┘
```

- The run stopped because it could not go on. The rows not yet done show Skipped (FR12.3). Open in browser and Copy link are off, because the PPT is incomplete. Close is on and the default button.
- If the app can move the unfinished PPT to the Trash, the line reads "The unfinished PPT was moved to the Trash." If it cannot, it says where the file is and that it is incomplete, as above (FR12.5). Last week's PPT is never deleted by a stopped run (FR12.4).
- Other stop reasons use the same layout with their own first line: "⚠ The run stopped: Please sign in with Google again.", "⚠ The run stopped: Your Google Drive is full.", and the template message below.

### M4d. Template not shared

```
│ ⚠ The run stopped: Ask the developer to share the slide template    │
│   with your Google account and allow copying.                       │
```

- This is the reason shown at the top of a stopped run when the template cannot be copied (FR12.6). All rows show Skipped, and nothing is left in Drive.

## M5. Error dialog (story US15.1; FR15)

```
┌ Something went wrong ───────────────────────────────────────┐
│ ⚠ We couldn't reach the shared library.                     │
│ Check your internet connection and try again.               │
│                                                             │
│ [ Copy details for support ]  [ Open log folder ]  [ OK ]* │
└─────────────────────────────────────────────────────────────┘
```

- OK is the default button, and Escape closes the dialog. A lost connection reads "The internet connection was lost" (FR15.6). "Copy details for support" copies the version, operating system, Java version, error code, stack trace and the affected row, with every secret removed (FR15.3, FR15.4).

## M6. Settings (stories US14.1, US14.2, US14.3; FR14)

```
┌ Settings ───────────────────────────────────────────────────┐
│ ☑ Delete last week's slides when I make new ones            │
│                                                             │
│ Your name   [ Grace                                     ]   │
│                                                             │
│ Theme       (•) Light    ( ) Dark                           │
│                                                             │
│ [ Replace setup file ]     [ Sign out of Google ]           │
│                                                             │
│ [ Reload all hymns ]                                        │
│ Downloads every hymn again. Unsaved hymn changes are lost.  │
│                                                             │
│ ─────────────────────────────────────────────────────────── │
│ AutoPPT 1.0.0                                  [ Close ]*   │
└─────────────────────────────────────────────────────────────┘
```

- A modal dialog, 480 by 400 pixels as a starting value. There is no language selector in the first version (FR13.6). Changing the name updates the greeting when Settings closes. Changing the theme applies at once (decided, Q6). Escape closes the dialog (Q6).
- Replace setup file and Sign out of Google lead to the screens in M8. Reload all hymns opens the confirmation in M7 (FR14.5; Q7). The size is now 480 by 400 as a starting value, to fit the extra button and its line.

## M7. Hymn confirmations and the conflict message (stories US7.2, US7.3, US7.5, US14.3)

Each confirmation is a modal dialog, 440 by 200 pixels as a starting value, with Cancel as the default button and Escape cancelling (Q1, Q6; FR7.2).

```
┌ Save for everyone? ─────────────────────────────────────────┐
│ Save your changes to 'Love Incarnate' for everyone?         │
│ Everyone will see your version the next time they check     │
│ for updates.                                                │
│                       [ Cancel ]*   [ Save for everyone ]   │
└─────────────────────────────────────────────────────────────┘
┌ Discard my changes? ────────────────────────────────────────┐
│ Discard your changes to 'Love Incarnate'?                   │
│ Your unsaved edits on this computer will be lost, and the   │
│ shared version will be used again. This can't be undone.    │
│                       [ Cancel ]*   [ Discard my changes ]  │
└─────────────────────────────────────────────────────────────┘
┌ Delete for everyone? ───────────────────────────────────────┐
│ Delete 'Love Incarnate' for everyone?                       │
│ It will be removed from the shared library for all users.   │
│ You can't undo this. A service already using the hymn keeps │
│ its lyrics.                                                 │
│                       [ Cancel ]*   [ Delete for everyone ] │
└─────────────────────────────────────────────────────────────┘
┌ Hymn not saved ─────────────────────────────────────────────┐
│ ⚠ Someone else saved this hymn first. Choose Check for      │
│   updates, then make your change again.                     │
│                       [ Check for updates ]*   [ Close ]    │
└─────────────────────────────────────────────────────────────┘
┌ Reload all hymns? ──────────────────────────────────────────┐
│ Replace every hymn on this computer with the shared         │
│ version? Changes you haven't saved for everyone will be     │
│ lost.                                                       │
│                       [ Cancel ]*   [ Reload all hymns ]    │
└─────────────────────────────────────────────────────────────┘
```

- Confirming opens the Reload progress dialog below, and a failure opens the failure message below it.

```
┌ Reloading the hymns ────────────────────────────────────────┐
│ Downloading every hymn from the shared library…             │
│ ▓▓▓▓▓▓░░░░░░░░░░  38%                                       │
│                                                             │
│                                                [ Close ]off │
└─────────────────────────────────────────────────────────────┘
┌ Reload all hymns ───────────────────────────────────────────┐
│ ⚠ Couldn't reload the hymns. Nothing was changed.           │
│                                                             │
│                                          [ OK ]*            │
└─────────────────────────────────────────────────────────────┘
```

- The hymns download into a temporary copy. Only when every hymn has arrived does the app replace the local copy and clear the unsaved edits. The progress dialog is blocking like the Make slides progress: Escape does nothing and Close is off. On failure, including a lost connection, nothing changes: every hymn and unsaved edit stays as it was. The failure message has OK as the default, and Escape closes it (decided, R-11 fix; FR14.5). The sizes are starting values.
- The Reload all hymns confirmation opens from Settings (M6), with Cancel as the default button and Escape cancelling (Q7; FR14.5). The question text is exactly as shown. Confirming downloads every hymn into a temporary copy. Only when every hymn has arrived are the local copy of the shared library and every unsaved hymn edit, including edits kept in the current service's rows, replaced and discarded. It never changes the shared library. Settings stays open behind it.
- The hymn name in each confirmation is the real hymn title. The conflict message shows after a refused save or delete, and the typed text stays in the row (FR7.4, FR7.7).

## M8. Second copy, sign in again, and replace setup file (stories US3.5, US14.1, US15.3, US2.3; FR3.10, FR14.2, FR15.5, FR2.9)

### M8a. AutoPPT is already open

```
┌ AutoPPT ────────────────────────────────────────────────────┐
│ AutoPPT is already open.                                    │
│                                                             │
│                                          [ OK ]*            │
└─────────────────────────────────────────────────────────────┘
```

- This shows when a second copy is launched. OK is the only button and the default. Enter and Escape both close it, and then the second copy exits (decided, Q6; FR3.10). The first copy is not changed. The size is a starting value (360 by 140).

### M8b. Sign in again

After "Sign out of Google", or after a Google sign-in that is no longer valid, the app returns to the wizard's sign-in step (M1c) with this line above the button:

```
┌ AutoPPT: Welcome ───────────────────────────────────────┐
│ Step 3 of 4   Sign in with Google                       │
│                                                         │
│ ⚠ Please sign in with Google again.                     │
│ [ Sign in with Google ]*  (opens your web browser)      │
│                                                         │
│                           [ Back ]off   [ Next ▸ ]off   │
└─────────────────────────────────────────────────────────┘
```

- Escape does nothing on this step, as on every wizard step (decided, Q7). The wizard shows only this step, because the setup file and name are already saved. It matches step 3 (M1c): Sign in with Google has the initial focus and is the default button, and Next stays off until sign-in succeeds, then becomes the default; after that the app returns to the main window with the service as it was (decided, Q6; FR14.2, FR15.5). A run that was stopped by a sign-in failure is stopped as in M4c ("⚠ The run stopped: Please sign in with Google again.").

### M8c. Replace setup file result

After the user picks a new setup file in Settings, the new file goes through the step 1 checks (M1a) and the result shows in a small dialog:

```
┌ Replace setup file ─────────────────────────────────────────┐
│ ✔ Setup file replaced.                                      │
│                                          [ OK ]*            │
└─────────────────────────────────────────────────────────────┘
┌ Replace setup file ─────────────────────────────────────────┐
│ ⚠ The scripture key doesn't work. Ask the developer for a   │
│   new setup file.                                           │
│                                          [ OK ]*            │
└─────────────────────────────────────────────────────────────┘
```

- On success the text is "Setup file replaced". On failure the dialog shows the plain reason from the step 1 checks, such as the part that failed, and the old setup file stays in use (decided, Q6; FR2.9, FR2.4). OK is the default button and Escape closes it.

## Responsive behaviour (window size only)

- The app is a desktop window with no phone or tablet layout (wireframes.md; design document D1). The main window's smallest size is 900 by 600. At that size the list stays at 320 pixels wide, the right panel scrolls vertically, and no control is hidden. Dialogs keep their starting sizes, which are finalised in code, and centre over the main window.

## Assumptions & Open Questions

- None open on Escape. The wizard rule is decided (Q7): on every first-start wizard step, including the sign-in-again step, Escape does nothing.
- None open. The Reload all hymns download and failure states are decided (R-11 fix): temporary copy first, blocking progress, and "Couldn't reload the hymns. Nothing was changed." on failure.
