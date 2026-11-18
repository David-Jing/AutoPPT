# Rough Mockups Questions

Sources for these questions: the confirmed intent statement (`intent-statement.md`), the scope document and intent backlog from Scope Definition, and the high-level design draft (`high-level-design.md`), which already sketches the main window and describes each screen's behaviour.

## Q1. What your documents already answer

Context: the design document already settles the main screens, the core flow and the interface rules, so they are listed here once for you to confirm instead of being asked one by one.

- Entry points and key screens: first start (setup file drop, name, Google sign-in); the main window, a master-detail view with the slide list on the left and the selected slide's settings on the right; the make-slides progress view; the error dialog; and settings (high-level-design.md F1, F2, F8, F9, F10).
- Core flow (happy path): drop in the setup file, enter your name and sign in with Google; choose the service date and order; fill in each slide row; press "Make slides"; check the PPT that opens in the browser; add the Chinese Monthly Scripture picture in Google Slides (high-level-design.md F1, F2, F8, F12).
- Information hierarchy: the slide list shows each row's short summary and a warning with a plain reason when a row cannot be made; each slide type has its own settings panel (high-level-design.md F1).
- Wording: the interface never says "GitHub", "repo", "commit", "API" or "token", and uses the plain-language vocabulary in section 5, such as "Check for updates", "Save for everyone", "Delete for everyone", "Discard my changes" and "Setup file" (high-level-design.md D5, section 5). Artifacts and the interface say "PPT" or "slides" (intent-statement.md).
- Form factor: a desktop app for Windows and macOS, with a light and a dark theme and no preview inside the app (high-level-design.md D1, D15, F10).
- Existing design system: Swing with FlatLaf (high-level-design.md D1).
- Language: English text only in the first version, with all text kept in resource files so Traditional Chinese can be added later (high-level-design.md section 11).

A. All correct
B. Some need changing (say which and how)
X. Other (please specify)

[Answer]: A

## Q2. [Designer] What accessibility level should the screens meet?

Context: the design document sets no accessibility target. It sets the level of keyboard use, contrast and screen-reader support that the mockups and later UI tests must show. The only keyboard rule it names today is Alt+Up and Alt+Down to move a slide row.

A. Keyboard-only use of every screen, plus readable contrast in both themes
B. Keyboard-only use, readable contrast, and screen-reader labels on every control
C. No specific accessibility target for the first version
D. Not yet defined
X. Other (please specify)

[Answer]: A

## Q3. [Designer] Which screen states must the mockups show?

Context: besides the normal view, a screen can show an empty, loading, error or partial state. The design document names several, such as a warning on a row that cannot be made, "Only on this computer" on an edited hymn, and a failed row that does not stop the run.

A. Normal, row warnings, the make-slides progress states (Done, Problem, Skipped, To do in Google Slides), and the error dialog
B. Everything in A, plus empty states (a new service with no rows, no hymns yet) and what happens without an internet connection
C. Only the normal view of each screen
D. Not yet defined
X. Other (please specify)

[Answer]: B. Updated by Q7: there is no offline mode. Internet is a prerequisite for the whole app, so the earlier offline state (hymns browsable and actions disabled) is removed. Without a connection, the start-up screen stops with "You're not connected to the internet. Connect, then choose Try again.", and a connection lost while the app is open is covered by the same message.

## Q4. [Designer] Which screens do you want sketched in this stage, beyond the main window the design document already shows?

Context: the main window is already sketched in the design document (F1). The rough mockups add the rest, and more screens mean more to review.

A. First start, the make-slides progress view, the error dialog and settings
B. Everything in A, plus an editor for each slide type (passage, hymn, text blocks, quote, schedule, header, title-only)
C. Everything in B, plus the hymn delete confirmation and the "someone else saved this hymn first" message
D. Not yet defined
X. Other (please specify)

[Answer]: C

## Q5. [Designer] What changes follow from the review? (follow-up from the review)

Context: your change request answered each review point, and this records them as the answers the wireframes cite.

A. Use the answer below as written
X. Other (please specify)

[Answer]: X. Other (please specify): (1) Screen-reader support is not part of the first version, because Q2 is A (keyboard and contrast only), so no screen-reader wording stays. (2) Contrast target: at least 4.5:1 for text (WCAG AA) in both the light and dark themes. The heading-level, region and focus-start rules in the accessibility notes are proposed, not decided. (3) Add a short Title-only editor sketch, and Supplications uses the same editor as Announcements. (4) The progress count matches the rows drawn, and the row being made shows an In progress state. (5) First start has its steps in order so the shared-library check sits with its step. (The offline state first added here, "You need an internet connection to finish setup", is replaced by the start-up stop screen in Q7.) (6) Your name, Replace setup file and Sign out of Google in Settings come from design document F10 and stay.

## Q6. [Designer] What changes follow from your review of the revised mockups? (follow-up)

Context: your second change request updated the design document (F1, F3 and F8), and this records its three points as the answers the wireframes cite.

A. Use the answer below as written
X. Other (please specify)

[Answer]: X. Other (please specify): (1) First start: the setup screens appear only on first start, or when saved keys are missing or replaced, and later starts open the main window directly. They are a step-by-step wizard, one step per screen, with Back and Next and a "Step n of N" marker (setup file, your name, sign in with Google, shared library ready). The user never sees all steps on one screen. Sketch each step screen. (The wizard's offline state asked for here is replaced by the start-up stop screen in Q7.) (2) "Save for everyone", "Discard my changes" and "Delete for everyone" each show a confirmation prompt naming the hymn and the effect before they run, and Cancel changes nothing. Sketch the save and discard confirmations next to the delete one, and update the hymn flow. (3) The main window top bar greets the user by name, "Hi [name]", using the name from first start, changeable in Settings. Do not approve the stage; the developer reviews the revised files before approving.

## Q7. [Designer] How does the app behave without an internet connection? (follow-up)

Context: your third change request updated the design document (new F0 "Start-up and internet connection", the F3 Updates wording, and the F10 About line), and this records its five points as the answers the wireframes cite. It replaces the earlier offline behaviour in Q3 and the first-start offline state in Q5.

A. Use the answer below as written
X. Other (please specify)

[Answer]: X. Other (please specify): (1) Internet is a prerequisite for the whole app, not just setup. There is no offline mode. This replaces the earlier offline behaviour (Q3, and the note that Discard stays enabled offline): the offline main-window state where hymns can be browsed and actions are disabled is removed. (2) A start-up screen shows on every launch with the app name, the version, and one status line at a time: "Checking your internet connection…", "Checking for updates…", "Getting ready…". It then opens the first-start wizard (first start only) or the main window. (3) With no connection at start-up, the screen stops and shows "You're not connected to the internet. Connect, then choose Try again." with Try again and Quit. Nothing else opens, and it re-checks by itself. This replaces the wizard's offline state. (4) With the connection lost while the app is open, the main window is covered by the same message. Typed work stays (autosave). A running action reports Problem with "The internet connection was lost". The cover closes when the connection returns. (5) Settings shows the app version at the bottom (About), for example "AutoPPT 1.0.0". Do not approve the stage; the developer reviews the revised files before approving.

## Q8. [Designer] What changes follow from the second review? (follow-up from the review)

Context: your change request updated the design document (F0, F3 and F11) and answered each review point, and this records them as the answers the wireframes and the scope documents cite. It refines the start-up lines in Q7.

A. Use the answer below as written
X. Other (please specify)

[Answer]: X. Other (please specify): (1) Auto-update, meaning the app downloading and replacing itself, is dropped entirely, and no intent artifact lists it as deferred or later. What stays, as a Must in F11 "Distribution and update check": on start-up the app makes one request to GitHub's public latest-release endpoint. If the release is newer, the start-up screen shows "A new version of AutoPPT is available (x.y.z)" with Download, which opens the zip for this computer in the browser, and Continue. If the check fails, start-up carries on silently. The app never downloads or replaces itself. The start-up state is sketched. (2) F0 (start-up screen, internet required, connection-lost cover) belongs to proto-Unit 1, and the name step plus the "Hi [name]" greeting belong to proto-Unit 5, in the scope document and the intent backlog. (3) On first start the start-up screen checks only the connection and the app version. The shared library is loaded in wizard step 4, and "Getting ready" is skipped on first start. (4) A lost connection during the wizard shows the same cover, and the wizard stays on the same step and keeps typed input. (5) The connection check has a 5-second timeout, and the app re-checks every 5 seconds while it is not connected. (6) In the hymn save, discard and delete confirmations, Cancel is the default button and Escape cancels, and this is decided, not proposed. Do not approve the stage; the developer reviews the revised files before approving.

## Q9. [Designer] What changes follow from the third review? (follow-up from the review)

Context: your change request updated the design document (F0 and F11) and answered each review point, and this records them as the answers the wireframes and the scope document cite. You confirmed these answers yourself, so the summary was not asked again.

A. Use the answer below as written
X. Other (please specify)

[Answer]: X. Other (please specify): (1) The scope document also carries F0 (start-up screen, internet required, connection-lost cover) and the first-start name step plus the "Hi [name]" greeting, in its F0, F1 and F8 rows and its Sequencing section: F0 with the thin end-to-end path (proto-Unit 1), and the name step and greeting with the service builder (proto-Unit 5). (2) The sentence "Auto-update is not part of the first version" is deleted from the new-version start-up state; "The app never downloads or replaces itself" stays. (3) The version check uses the same 5-second timeout as the connection check. On a failure or a timeout, start-up carries on silently. Only the latest published release counts, because GitHub's latest-release endpoint already excludes drafts and pre-releases. "Newer" means a higher major.minor.patch version. (4) On the no-connection screen, Try again is the default button and Escape does nothing. On the new-version screen, Continue is the default button and Escape means Continue. (5) The no-connection screen comes before the new-version screen in the wireframes. The error paths gain two rows: a new version available is not an error (Download or Continue), and a failed version check is silent. Do not approve the stage; the developer reviews the revised files before approving.

## Consolidated Summary Confirmation

Does this all look correct before I generate the artifact?

- Looks correct
- Request changes

[Answer]: Looks correct
