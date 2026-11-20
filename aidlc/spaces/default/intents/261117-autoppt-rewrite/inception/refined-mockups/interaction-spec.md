# Interaction Specification: AutoPPT Rewrite

Inputs: `wireframes.md` and `user-flow.md` (rough mockups), `mockups.md` (the screens), `stories.md`, `requirements.md`, `team-practices.md`, and `refined-mockups-questions.md` (Q1 to Q7). Components follow the format of the framework's component specification template. The Swing and FlatLaf classes are in `design-system-mapping.md`, and the accessibility checks are in `accessibility-checklist.md`.

## Global Rules

| Rule | Behaviour | Source |
|---|---|---|
| Window size | The main window opens at 1100 by 700 pixels, resizes freely, has a minimum of 900 by 600, and remembers its size and position between launches. | Q2 |
| Dialog sizes | The sizes in `mockups.md` are starting values, finalised in code. | Q6 |
| Modal dialogs | A dialog blocks the window behind it. Its default button has focus, and Enter presses it. | FR3.9, Q3 |
| Escape (one rule, decided) | Escape cancels or closes any dialog, except the no-connection screen (does nothing), the new-version screen (means Continue) and the connection-lost cover (does nothing). The progress dialog ignores Escape during a run, and after the run Close is the default button and Escape closes it. First-start wizard steps are not dialogs: Escape does nothing on every step, including sign in again. | Q6; Q7 |
| Theme | Choosing a theme in Settings applies at once. | Q6 |
| Closing during start-up | Closing the window during the start-up check quits the app. | Q6 |
| State words | Every state shows a symbol and a word as well as any colour. | Q5; NFR8 |
| Keyboard | Every control is reachable by Tab and Shift+Tab, Space toggles a check box or radio button, and Alt+Up and Alt+Down move the selected slide row. | NFR8; FR3.4 |
| Plain words | No screen says "GitHub", "repo", "commit", "API" or "token". | FR15.7 |
| Connection lost | A cover blocks the whole window and closes by itself when the connection returns. Typed work stays. | FR1.6 |

### Default button and Escape for each dialog

| Dialog | Default button (focused) | Escape | Source |
|---|---|---|---|
| Start-up: no connection (M0b) | Try again | Does nothing | FR1.3; the Escape rule (Q6) |
| Start-up: new version (M0c) | Continue | Means Continue | FR16.4; the Escape rule (Q6) |
| Start-up: library not available (M0d) | Try again | Closes the screen, which quits the app, as the Escape rule and the closing-during-start-up rule say | the Escape rule (Q6); the default button is proposed |
| Connection-lost cover (M2d) | Try again | Does nothing | FR1.6; the Escape rule (Q6) |
| Wizard steps (M1) | Step 1: Next. Step 2: Next. Step 3: Sign in with Google, then Next once sign-in succeeds. Step 4: Finish. A default button that is off is not pressed by Enter; the first on button of the step is the default | Does nothing. Back goes back, and closing the window quits the app | Q7; the default button is proposed |
| Save, discard, delete confirmations (M7) | Cancel | Cancels | FR7.2; the Escape rule (Q6) |
| Hymn not saved (M7) | Check for updates | Closes | the Escape rule (Q6); the default button is proposed |
| Progress dialog (M4) | Close (off until the run ends) | Does nothing while a run goes, closes after it ends | Q6 |
| Error dialog (M5) | OK | Closes | the Escape rule (Q6) |
| Settings (M6) | Close | Closes | the Escape rule (Q6) |
| Reload all hymns confirmation (M7) | Cancel | Cancels | FR14.5; Q7 |
| Reload all hymns progress (M7) | Close (off until the download ends) | Does nothing | FR14.5; R-11 fix |
| Reload all hymns failure (M7) | OK | Closes | FR14.5; R-11 fix |
| New hymn (M3e) | Add hymn (off until a title and lyrics are typed) | Cancels | the Escape rule (Q6) |
| AutoPPT is already open (M8a) | OK | Closes, then the second copy exits | Q6 |
| Sign in again (M8b) | Sign in with Google (also the initial focus); Next stays off until sign-in succeeds, then becomes the default | Does nothing (it is a wizard step) | Q7; the same as wizard step 3 (M1c); the default button is proposed |
| Replace setup file result (M8c) | OK | Closes | the Escape rule (Q6) |

A hymn confirmation, the conflict message and the other dialogs are modal. The start-up screens and the connection-lost cover are not closed by Escape, as the rule says.

## Component Specifications

### Slide list

| Field | Value |
|---|---|
| Component | Slide list |
| Description | The ordered rows of the service, on the left of the main window. |
| Category | navigation |

States:

| State | Description | Trigger |
|---|---|---|
| default | Rows in order, each with a handle, a type and a summary. | A service is open |
| selected | One row highlighted with the accent colour and the `▶` mark. Its settings show on the right. | Click, or arrow keys |
| warning | A `⚠` and the word of the reason beside the row's summary. | A row cannot be made |
| dragging | The row follows the pointer, with a line showing where it will drop. | Drag the handle |
| empty | The text "(no rows)" and the add button. | A new service with no rows |
| locked | Greyed and not responding. | A modal dialog or the cover is open |

Behaviour: arrow keys move the selection. The visible Move up and Move down buttons, next to Add and Remove, and Alt+Up and Alt+Down all move the selected row up or down (decided, Q6). Delete or the Remove button removes it from this service only, and Enter moves focus to the settings panel. "+ Add slide" opens a menu of the slide types, and any type can be added more than once (FR3.4, FR3.5).

Accessibility: keyboard entry point is the list. The selected row is shown by highlight and the `▶` mark, not by colour alone.

### Settings panel

| Field | Value |
|---|---|
| Component | Settings panel |
| Description | The editor for the selected row, on the right of the main window. |
| Category | input |

Behaviour: it shows the editor for the row's type (mockups.md M3). It scrolls when the window is small. Its first field takes focus when a row is selected by the keyboard, and keeps focus where it is when a row is selected by the pointer. Edits are saved locally as they are typed (FR3.7).

### Passage field

| Field | Value |
|---|---|
| Component | Passage field |
| Description | A text field for one or more references, used by the Opening Passage, the Offering and every Bible Passage. |
| Category | input |

States: default; focus (accent outline); error (`⚠` and "We couldn't find 'Jhon 3:16'" under the field); disabled (greyed while a run is going or the cover shows).

Behaviour: separate passages with a semicolon only. The `?` help button opens a pop-up with what works and what does not (FR5.2). It replaces a hover tooltip, because a tooltip cannot be reached by keyboard (decided, Q6). A check of the reference happens when the field loses focus and again when slides are made. An empty field shows a warning on the row (FR5.10). The pop-up closes with Escape or by clicking away.

### Hymn picker and lyrics area

| Field | Value |
|---|---|
| Component | Hymn picker and lyrics area |
| Description | A searchable drop-down of shared hymns, and one text area for the lyrics. |
| Category | input |

States: default; empty (no hymns yet, with a hint); edited (the lyrics differ from the shared copy, shown as `● Only on this computer`); newer shared version available (a badge `⚠ A newer shared version is available` beside the lyrics, with the local edit kept, FR7.8); disabled.

Behaviour: typing in the drop-down filters by title, other titles and first line. "New hymn…" is the last entry and opens the New hymn dialog (a title field and a lyrics area, with Cancel and Add hymn). The lyrics area is one text area with `[Verse 1]`-style headers typed in and a small hint under it about the header format (Q4). The three buttons are Discard my changes, Save for everyone and Delete for everyone, and each opens its confirmation (M7). Save for everyone and Discard my changes are on only when the hymn has edits. Delete for everyone is on when a shared hymn is chosen (decided, Q6). Edits stay with the service row and survive closing the app, and "New service" clears them (FR7.1, FR7.8).

### Text block editor

| Field | Value |
|---|---|
| Component | Text block editor |
| Description | An ordered list of text blocks with simple formatting, used by Announcements, Supplications, Catechism and Spiritual Wisdom. |
| Category | input |

Behaviour: each block has the buttons Bold, Italic, Bullet list, Numbered list and Link, plus Up, Down and Remove for the block. "+ Add item" adds a block and gives it focus. Formatting is stored as CommonMark. Underline is not offered. Text the app cannot render shows as plain text with a `⚠` warning (FR9.1, FR9.2). The Catechism and Spiritual Wisdom editors use one block with the same buttons.

### Schedule name field

| Field | Value |
|---|---|
| Component | Schedule name field |
| Description | An editable drop-down for a role's name, with suggestions. |
| Category | input |

Behaviour: one for each role in the template, labelled with the template's own text. It suggests names used before. An empty field prints "N/A" in the PPT (FR10.1 to FR10.3).

### Confirmation dialog

| Field | Value |
|---|---|
| Component | Confirmation dialog |
| Description | A modal question before a hymn action runs. |
| Category | feedback |

Behaviour: a title, a sentence naming the hymn and the effect, and two buttons. Cancel is the default button and has focus, and Escape cancels. Cancel changes nothing. Confirming runs the action, and if someone else changed the hymn first, the conflict message shows (FR7.2, FR7.4).

### Progress row

| Field | Value |
|---|---|
| Component | Progress row |
| Description | One row of the progress dialog, with a status symbol, a word and the row's name. |
| Category | feedback |

States: waiting (no symbol, grey word "Waiting"), In progress `⟳`, Done `✔`, Problem `⚠` with a plain reason, Skipped `–`, To do in Google Slides `✎` with the slide number.

Behaviour: one row is In progress at a time. The count is the rows no longer waiting or in progress, over all rows. A run STOPS when the connection is lost, sign-in fails, the template copy fails (including the template not being shared, FR12.6) or the Drive is full, and the rows not yet done show Skipped. A run FINISHES when every row was attempted (FR12.3). After a stopped run, Open in browser and Copy link are off, Close is on and the default button, and the dialog says what happened to the half-built PPT (FR12.5). Escape is ignored during a run and closes the dialog after it (decided, Q6).

### Connection cover

| Field | Value |
|---|---|
| Component | Connection cover |
| Description | A message that blocks the whole window while there is no internet. |
| Category | feedback |

Behaviour: it appears within 5 seconds of a lost connection, shows the same message as start-up, and re-checks every 5 seconds. Try again re-checks at once. Quit closes the app. Escape does nothing. When the connection returns, the cover closes by itself and the window is as it was, with the wizard on the same step (FR1.6).

### Start-up screen

| Field | Value |
|---|---|
| Component | Start-up screen |
| Description | A small window shown on every launch with the app name, the version and one status line. |
| Category | feedback |

States: checking (the three status lines in order); no connection (M0b); new version available (M0c); library not available (M0d).

Behaviour: the version check uses a 5-second timeout, and a failure carries on silently. The whole start-up should finish within 10 seconds when every check answers normally (NFR2). Download opens the browser and the screen stays open.

### Wizard step

| Field | Value |
|---|---|
| Component | Wizard step |
| Description | One of four screens of the first-start setup. |
| Category | navigation |

Behaviour: a "Step n of 4" marker, Back at the left and Next or Finish at the right. Next is off until the step is done. Back keeps what was typed. A lost connection shows the connection cover and the wizard stays on the same step (FR2.2, FR1.6).

## Interaction Flows

| Flow | Steps | Source |
|---|---|---|
| Make a PPT | Fill in rows, press Make slides, the progress dialog blocks the window, the PPT opens in the browser when the run ends, press Close. | US12.1, US12.2 |
| Save a hymn | Edit the lyrics, choose Save for everyone, confirm, and see it saved or see the conflict message. | US7.2 |
| Delete a hymn | Choose Delete for everyone, confirm, and see it removed or see the conflict message. | US7.5 |
| Recover from no internet | See the cover, connect, and the cover closes by itself. | US1.3 |
| Reload all hymns | Open Settings, choose Reload all hymns, confirm (Cancel is the default and Escape cancels), and the hymns download into a temporary copy under a blocking progress dialog. When every hymn has arrived, the local copy is replaced and unsaved hymn edits are discarded. On failure nothing changes and "Couldn't reload the hymns. Nothing was changed." shows. The shared library is never changed. | US14.3 |
| Replace the setup file | Open Settings, choose Replace setup file, and the new file goes through the step 1 checks. | US2.3, US14.1 |

## Responsive Behaviour

The app is a desktop window only. The main window shrinks to 900 by 600, where the list stays 320 pixels wide and the right panel scrolls. Dialogs keep fixed sizes (mockups.md).

## Assumptions & Open Questions

None.
