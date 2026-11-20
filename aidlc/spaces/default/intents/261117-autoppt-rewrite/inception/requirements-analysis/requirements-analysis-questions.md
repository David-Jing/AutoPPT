# Requirements Analysis Questions

Sources for these questions: the confirmed intent statement (`intent-statement.md`), the approved scope document (`scope-document.md`) and the affirmed team practices (`team-practices.md`), plus the high-level design document (`high-level-design.md`, which the requirements follow closely). Many topics are already settled in these, so they are listed once in Q1 for you to confirm, and only the gaps are asked after it.

## Q1. What your documents already answer

Context: the design document already states the behaviour, the rules and most of the quality targets, so they are listed here once for you to confirm instead of being asked one by one.

- Functional requirements: one group for each first-version feature in the approved scope (F0 start-up and internet requirement, F1 service builder, F2 make slides, F3 hymn editing, sharing and deletion, F4 Bible passages, F4a catechism, F5 announcements and supplications, F5a Spiritual Wisdom, F6 template and configuration, F7 Next Week's Schedule, F8 first-start wizard, F9 errors, F10 settings, F11 distribution and update check, F12 Chinese Monthly Scripture), written from the design document's behaviour and its formatting rules for passages (V1 to V7), hymns (H1 to H6) and text blocks (T1 to T3) (high-level-design.md section 6 and 7.3; scope-document.md).
- Quality and platform requirements already stated:
  - a non-technical operator builds the PPT in under 10 minutes, and slides need no touch-up on 4 consecutive Sundays (intent-statement.md);
  - the app runs on Windows x64, macOS Apple Silicon and macOS Intel, as a self-contained app with no installer and no cloud hosting (constraint-register.md C-T2 to C-T4);
  - the internet is required for the whole app, with no offline mode (high-level-design.md F0);
  - keyboard-only use of every screen, and text contrast of at least 4.5:1 in both themes (rough-mockups-questions.md Q2, Q5);
  - passage text is kept in memory only, within the ESV limits (high-level-design.md F4);
  - keys and the Google refresh token sit in a file readable only by the user, and a redactor keeps them out of logs and support details (high-level-design.md D25, F9; team-practices.md).
- Constraints: the stack and tools in the constraint register, free services and free fonts only, no remote logging, and the developer alone decides scope (constraint-register.md; intent-statement.md).
- Out of scope: the intent statement's non-goals, plus the Chinese language selector and Chinese support beyond the groundwork marked "now" (scope-document.md).

A. All correct
B. Some need changing (say which and how)
X. Other (please specify)

[Answer]: A

## Q2. [Product owner] How long may "Make slides" take for a typical PPT?

Context: the 10-minute goal covers the whole job of entering a week's details and getting a PPT. The reference PPTs have 58 and 60 slides, and the app builds them through many Google Slides requests. A time limit for the build step alone lets a tester check it.

A. At most 60 seconds for a PPT of up to 60 slides, on a normal home internet connection
B. At most 2 minutes for a PPT of up to 60 slides, on a normal home internet connection
C. At most 5 minutes for a PPT of up to 60 slides, on a normal home internet connection
D. No limit beyond the 10-minute whole-job goal
E. Not yet defined
X. Other (please specify)

[Answer]: B — at most 2 minutes for a PPT of up to 60 slides on a normal home connection, as a provisional target: the developer may raise it after measuring real runs (spike S1 and the walking skeleton).

## Q3. [Product owner] How quickly should the app be ready to use at start-up?

Context: every launch shows the start-up screen while it checks the connection, the app version and the shared library, so a time limit tells a tester when start-up is too slow. The checks have a 5-second timeout each.

A. The main window opens within 10 seconds of launch, when the connection is working
B. The main window opens within 20 seconds of launch, when the connection is working
C. No limit beyond the 5-second timeout on each check
D. Not yet defined
X. Other (please specify)

[Answer]: A

## Q4. [Product owner] How large should a service and the hymn library be able to grow?

Context: the reference PPTs have 58 and 60 slides, and the legacy hymn database has 116 hymns. A stated size tells testers and the paginator what to be checked against.

A. Up to 100 slides in one PPT, and up to 500 hymns in the shared library
B. Up to 150 slides in one PPT, and up to 1,000 hymns in the shared library
C. No stated limit; handle the reference PPTs and the 116 hymns well
D. Not yet defined
X. Other (please specify)

[Answer]: A

## Q5. [Product owner] If "Make slides" stops partway, what happens to the half-built PPT in Google Drive?

Context: a run can stop partway, for example when the connection is lost or Google refuses a request. The new PPT is already a file in the operator's Drive by then. Whether it stays decides what the operator sees next, and "Delete last week's slides" must not remove the wrong PPT.

A. Leave the half-built PPT in Drive, tell the operator where it is and that it is incomplete, and do not delete last week's PPT
B. Move the half-built PPT to the Drive Trash automatically, and do not delete last week's PPT
C. Leave it and tell the operator, and treat it as last week's PPT the next time (it can be deleted then)
D. Not yet defined
X. Other (please specify)

[Answer]: X. Other (please specify): when a run stops partway, the app tries to move the half-built PPT to the Drive Trash. If it cannot (for example the connection is lost), it leaves the file, tells the operator it is incomplete and where it is, remembers its id locally, and moves it to the Trash on the next successful run. A failed or stopped run never deletes last week's PPT, and a half-built PPT is never treated as last week's PPT.

## Q6. [Product owner] Which operating system versions must the app run on?

Context: the app is built with Java 21 and bundles its own Java, so it needs only a recent enough operating system. The operators' own computers are a fact about your world, so the requirements need a named minimum for testers.

A. Windows 10 and 11 (x64), and macOS 12 or later on Apple Silicon and Intel
B. Windows 11 (x64), and macOS 13 or later on Apple Silicon and Intel
C. Whatever the bundled Java 21 supports, with no stated minimum
D. Not yet defined
X. Other (please specify)

[Answer]: A

## Q7. [Product owner] What changes follow from the review? (follow-up from the review)

Context: your change request answered each review point (the design document F2 was updated for the first), and this records them as the answers the requirements cite. You confirmed these answers yourself.

A. Use the answer below as written
X. Other (please specify)

[Answer]: X. Other (please specify): (1) A run STOPS when it cannot go on: a lost connection, a sign-in failure, a template copy failure, or a full Google Drive. The rows not yet done show Skipped, and the stopped-run rule applies. A run FINISHES when every row was attempted, and individual rows may be Problem. Last week's PPT is moved to the Trash only when the run finished with no Problem rows. A lost connection during Make slides is a stopped run. (2) Provisional targets, which may be raised after measurement like the 2-minute target: up to 100 slides within 3.5 minutes on a normal home connection; a shared-library refresh of changed files within 5 seconds; the first full download of 500 hymns within 30 seconds. (3) The three start-up status lines show in order, except on first start. (4) The 10-second start-up covers all start-up steps. The library refresh has a 5-second timeout. If it fails or times out, the app uses the cached copy and shows "Couldn't check for hymn updates". (5) Balanced is a soft requirement. The paginator aims for slides of one passage, hymn or text item differing by at most 2 lines, but a larger difference is allowed when the keep-together rules (whole verses, whole hymn blocks, sentence boundaries) make that impossible. The testable part is that, among the valid break points, the paginator picks the split with the smallest line-count difference. Hymn line splitting keeps its 40% to 80% rule. Overall visual judgement stays with the developer. (6) On a Google rate-limit (429) or server error (5xx), retry up to 3 times with increasing waits, then mark the row Problem. A Drive-full response stops the run with "Your Google Drive is full". (7) Local hymn edits are saved with the service row and survive closing the app. "New service" clears them. If Check for updates brings a newer shared version of a locally edited hymn, the local edit is kept and a "newer shared version available" badge appears. (8) Hymn ids are lowercase ASCII slugs from the title. If the title yields no usable letters, the id is "hymn-" plus a short random id. Clashes are checked case-insensitively. (9) Only one copy of the app may run. A second launch shows "AutoPPT is already open" and exits. (10) For a non-Sunday service date, the schedule date is the next Sunday after it. All dates use the computer's local time zone. (11) The "deck" search covers UI strings and generated PPT text only. Do not approve the stage; the developer reviews the revised files before approving.

## Consolidated Summary Confirmation

Does this all look correct before I generate the artifact?

- Looks correct
- Request changes

[Answer]: Looks correct
