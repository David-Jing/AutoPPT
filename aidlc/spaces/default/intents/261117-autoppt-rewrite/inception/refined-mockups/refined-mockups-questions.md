# Refined Mockups Questions

Sources for these questions: the approved rough mockups (`wireframes.md`, `user-flow.md`), the approved user stories (`stories.md`), the approved requirements (`requirements.md`) and the affirmed team practices (`team-practices.md`). The refined mockups turn each rough sketch into a detailed screen with exact states, messages and controls. Most design decisions are already made, so they are listed once in Q1 for you to confirm, and only the real gaps are asked after it.

## Q1. What your documents already answer

Context: the earlier stages already settle which screens exist, how they behave and the quality targets, so they are listed here once for you to confirm instead of being asked one by one.

- Screens, one for each group of stories: the start-up screen with its three states (checking, no connection, new version available); the four-step first-start wizard; the main window with its connection-lost cover; an editor for each slide type (Opening Passage, Offering, Bible Passage, Chinese Monthly Scripture, Hymn, Announcements and Supplications, Catechism, Spiritual Wisdom, Header and Sermon Header, Next Week's Schedule, Static, Title-only); the make-slides progress view; the error dialog; Settings with the app version; the three hymn confirmations and the "someone else saved this hymn first" message (wireframes.md S0 to S8; stories.md).
- Interaction patterns: a master-detail main window with a drag handle on each row, a one-screen-at-a-time wizard with Back and Next, a cover for a lost connection, a confirmation dialog for each hymn action with Cancel as the default button and Escape cancelling, and a progress view with one status for each row (user-flow.md; FR3, FR7, FR12).
- States for each screen: normal, row warnings, empty (no slides, no hymns), In progress, Done, Problem, Skipped, To do in Google Slides, connection lost, and new version available (wireframes.md; requirements.md).
- Design system: Java Swing with FlatLaf, with a light and a dark theme chosen in Settings (design document D1, F10; team-practices.md).
- Accessibility: every screen is fully usable by keyboard alone, and text keeps a contrast ratio of at least 4.5:1 in both themes. Screen-reader support is not part of the first version. The heading, region and focus rules in the notes are proposals to confirm in the mockups (rough-mockups-questions.md Q2, Q5; NFR8).
- Wording: plain words only, with "PPT" or "slides" for the output, and no "GitHub", "repo", "commit", "API" or "token" in the UI (project rules; FR15.7).
- Template changes: the Opening Passage has no title and one text box, the Offering is a passage slide with a fixed "OFFERING" heading, and there is no reference box (requirements.md FR5.1, FR5.12).

A. All correct
B. Some need changing (say which and how)
X. Other (please specify)

[Answer]: A

## Q2. [Designer] How big should the main window be, and what happens when it is made smaller?

Context: the main window is a list on the left and settings on the right, and the Hymn editor needs room for lyrics. Operators use ordinary laptops and desktop screens, so a stated size lets the layout be checked on both. The design system is Swing, which resizes freely unless a minimum is set.

A. Opens at 1100 by 700 pixels, can be resized, and has a minimum of 900 by 600; below the minimum the window stops shrinking
B. Opens maximised on each start, can be resized, and has a minimum of 900 by 600
C. Opens at 1280 by 800 and has no minimum
D. Not yet defined
X. Other (please specify)

[Answer]: A — opens at 1100 by 700 pixels, can be resized, minimum 900 by 600; the window also remembers its last size and position between launches.

## Q3. [Designer] Where should the make-slides progress view appear?

Context: the progress view lists each row as Done, Problem, Skipped, To do in Google Slides or In progress, and it stays until the run ends. The rough mockup sketches it as its own dialog. Whether it covers the main window or sits inside it decides whether the operator can keep editing while a run is going, which the requirements do not allow because the run uses the current service.

A. A dialog over the main window that blocks the window behind it until the run ends, then offers Open in browser, Copy link and Close
B. A panel inside the main window, replacing the settings panel, while the slide list stays visible but locked
C. Not yet defined
X. Other (please specify)

[Answer]: A — a dialog over the main window that blocks it until the run ends, then offers Open in browser, Copy link and Close.

## Q4. [Designer] How should the hymn lyrics editor work?

Context: a hymn is a list of blocks such as verse 1, chorus and verse 2, written in the shared library as text with `[Verse 1]`-style headers. The Operator edits the lyrics, and "New hymn…" splits pasted text on blank lines and headers. The editor can show the lyrics as one text area, or as one box for each block. This affects how an operator reorders or deletes a block.

A. One text area with the headers typed in, as in the rough mockup, plus a small hint about the header format
B. A list of blocks, one box for each, each with its own header, move up, move down and remove buttons
C. Not yet defined
X. Other (please specify)

[Answer]: A — one text area with `[Verse 1]`-style headers typed in, plus a small hint about the header format.

## Q5. [Designer] How should colour and emphasis be used beyond the theme's own?

Context: FlatLaf gives a light and a dark theme with an accent colour. The rough mockups use symbols (a warning sign for a row that cannot be made, a check for done, a dot for "Only on this computer"). Contrast must be at least 4.5:1, and meaning must never depend on colour alone, so a statement of how colour is used keeps the mockups consistent.

A. Use the theme's default accent colour, and mark every state with both a symbol and a word as well as any colour
B. Use a custom accent colour chosen by you (give it), and mark every state with both a symbol and a word as well as any colour
C. Not yet defined
X. Other (please specify)

[Answer]: A — the theme's default accent colour; every state is shown with both a symbol and a word as well as any colour.

## Q6. [Designer] What changes follow from the review? (follow-up from the review)

Context: your change request answered each review point, and this records them as the answers the mockups cite. You confirmed these answers yourself.

A. Use the answer below as written
X. Other (please specify)

[Answer]: X. Other (please specify): (1) Draw three more screens. "AutoPPT is already open" has OK only, Enter and Escape close it, and then the second copy exits. Sign in again (after Sign out of Google, or after an invalid sign-in) returns to the wizard's sign-in step with "Please sign in with Google again." Replace setup file shows "Setup file replaced", or the plain reason it failed. (2) Replace the web ideas (headings and regions) in the accessibility checklist with Swing checks: tab order, initial focus, default button, and where focus returns after a dialog closes. (3) Dialog sizes are starting values, finalised in code. One decided Escape rule: Escape cancels or closes any dialog, except the no-connection screen (does nothing), the new-version screen (means Continue) and the connection-lost cover (does nothing). The progress dialog ignores Escape during a run, and after the run Close is the default button and Escape closes it. (4) Skipped appears only after a stopped run. Draw the stopped-run state with the half-built PPT message and a Close button, and the template-not-shared message (FR12.6). (5) Fix the citations for the Escape rows. (6) The theme applies at once. (7) Show visible Move up and Move down buttons next to Add and Remove. Keep the `?` help button instead of a hover tooltip, because a tooltip cannot be reached by keyboard, and update FR5.2 and AC5.2.2 to match. (8) Draw the "A newer shared version is available" badge and the New hymn dialog. Save for everyone and Discard my changes are enabled only when the hymn has edits. Delete for everyone is enabled when a shared hymn is chosen. (9) Closing the window during the start-up check quits the app, and "no menu" is removed. Do not approve the stage; the developer reviews the revised files before approving.

## Q7. [Designer] What changes follow from the second review round? (follow-up from the review)

Context: your change request answered the open Escape question and added a feature. This records them as the answers the mockups cite. You confirmed these answers yourself.

[Answer]: X. Other (please specify): (1) On every first-start wizard step, including the sign-in-again step, Escape does nothing. The wizard is a full window: Back goes back, and closing the window quits. This is decided. (2) New feature "Reload all hymns" in Settings (design document F3 and F10, catalog resynced). It discards this computer's copy of the shared library and every unsaved hymn edit, including edits kept in the current service's rows, then downloads every hymn again from the shared library. It first asks "Replace every hymn on this computer with the shared version? Changes you haven't saved for everyone will be lost." with Cancel as the default button and Escape cancelling. It never changes the shared library. It is a new requirement (FR14.5) and story (US14.3), drawn in M6 Settings and in its confirmation in M7. Do not ask to reconfirm the summary. Run the review and stop at the approval gate. Do not approve.

## Consolidated Summary Confirmation

Does this all look correct before I generate the artifact?

- Looks correct
- Request changes

[Answer]: Looks correct
