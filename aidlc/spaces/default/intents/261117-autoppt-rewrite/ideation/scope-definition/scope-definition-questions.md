# Scope Definition Questions

Sources for these questions: the confirmed intent statement (`intent-statement.md`), the feasibility assessment and constraint register, and the high-level design draft (`high-level-design.md`, which marks each feature M for the first version or L for later). Launch timing was already answered in Feasibility (no fixed date), so it is not asked again.

## Q1. What your documents already answer

Context: the design document already sorts features into the first version (M) and later (L), and the intent statement already excludes the "later" features, so the split is listed here once for you to confirm.

- In the first version (M): start-up screen, internet requirement and connection-lost cover (F0, added in the design document); service builder with reordering and presets (F1); make slides (F2); hymn editing and sharing, including the one-time migration of the 116 legacy hymns and "Delete for everyone" (F3, updated in the design document); Bible passages from the ESV API (F4); catechism (F4a); announcements and supplications (F5); Spiritual Wisdom (F5a); template and configuration kept by the developer (F6); next week's schedule (F7); first start with the setup file (F8); error handling (F9); settings (F10); zipped app images for Windows and macOS, with a start-up check for a newer version that never downloads or replaces the app itself (F11, "Distribution and update check"); Chinese Monthly Scripture slide with an empty body (F12) (high-level-design.md section 6).
- Later (L): the Chinese language selector and Chinese support beyond the groundwork items marked "now" (F10, section 11) (high-level-design.md sections 6 and 11).
- Dependencies between capabilities: sign-in and the setup file (F8) come before making slides (F2); the template reader and tags (F6) come before making slides (F2); the paginator measures text for passages, hymns and text blocks (section 7); hymn editing (F3) needs the shared library (section 8) (high-level-design.md sections 6 to 9).
- Out of the first version: the non-goals in the intent statement (presenting or editing slides in the app, previewing, generating the Chinese pinyin picture, creating slide types or editing the template in the app, restoring older hymn versions, merging concurrent edits, remote logging, Google Sheets input, code signing) (intent-statement.md, Initial Scope Signal).

A. All correct
B. Some need changing (say which and how)
X. Other (please specify)

[Answer]: A

## Q2. [Product owner] In what order should the work be sequenced?

Context: the feasibility assessment says three spikes (S1 to S3) run first. This asks how to order everything after them, which shapes the backlog order.

A. Risk-first: the spikes, then the paginator and slide building, then the editors and sharing
B. Value-first: build the slide types operators use every week first, then the rarer ones
C. Dependency-first: sign-in, setup file and template reader, then slide building, then the editors
D. Not yet defined
X. Other (please specify)

[Answer]: X. Other (please specify): risk-first, then a thin end-to-end path, then breadth. Order: (1) spikes S1-S3; (2) a thin end-to-end path — setup file, Google sign-in, template reader, and one Bible passage slide written into a real PPT (the walking skeleton); (3) the paginator and each remaining slide type; (4) the editors, hymn sharing and migration.

## Q3. [Product owner] What is the smallest first release that still counts as useful?

Context: all of F1 to F12 are marked M, but the 10-minute goal only needs a working path from entering a week's details to a finished PPT. Naming the smallest useful slice tells the backlog what must exist before the rest.

A. Everything marked M must be present before the first real Sunday use
B. The service builder, make slides, Bible passages, hymns (with migration), announcements, schedule, sign-in and setup file; the rest follows in the first weeks
C. Not yet defined
X. Other (please specify)

[Answer]: A

## Q4. [Product owner] Must the 116 legacy hymns be migrated before the first real use?

Context: the design document has a one-time developer tool that converts the legacy hymn database. Without it, operators would have no hymns to pick.

A. Yes, the migration is must-have for the first release
B. No, hymns can be added by hand at first and the migration comes later
C. Not yet defined
X. Other (please specify)

[Answer]: A

## Q5. [Product owner] Which capabilities are nice-to-have within the first version, if time runs short?

Context: the feature list marks everything M, but if something had to slip, the backlog should show what goes first.

A. Nothing; all of it is must-have
B. Settings such as the theme (F10) and the local log folder shortcut (F9)
C. The Spiritual Wisdom and catechism slide types (F5a, F4a)
D. Not yet defined
X. Other (please specify)

[Answer]: A

## Consolidated Summary Confirmation

Does this all look correct before I generate the artifact?

- Looks correct
- Request changes

[Answer]: Looks correct
