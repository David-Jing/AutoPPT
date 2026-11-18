# Scope Document: AutoPPT Rewrite

Inputs: `intent-statement.md`, `feasibility-assessment.md`, `constraint-register.md`, `scope-definition-questions.md` (Q1 to Q5), and `high-level-design.md` v0.5.

## Scope Boundary

### In the first version

Everything the design document marks M (Q1, Q3). Every feature must be present before the first real Sunday use (Q3).

| Feature | Capability | Source |
|---------|------------|--------|
| F0 | Start-up and internet connection: a start-up screen on every launch, internet required for the whole app with no offline mode, a stop screen with Try again and Quit when not connected, and a connection-lost cover over the main window and the first-start wizard. Built in proto-Unit 1, with the thin end-to-end path | Q1; rough-mockups-questions.md Q9; design document F0; intent-backlog.md proto-Unit 1 |
| F1 | Service builder: ordered slide list, presets, reorder, add and remove rows, autosave, and the "Hi [name]" greeting in the main window's top bar. The greeting is built in proto-Unit 5, with the service builder | Q1; rough-mockups-questions.md Q9; design document F1; intent-backlog.md proto-Unit 5 |
| F2 | Make slides: build the PPT from the church template in the user's Google Drive and open it in the browser. This covers the full assembly order, removal of leftover template slides, clearing speaker notes, a progress view where a failed row does not stop the run, the template access check, and "Delete last week's slides" | Q1 |
| F3 | Hymn editing and sharing, including the one-time migration of the 116 legacy hymns and "Delete for everyone" (a confirmation, then a Contents API `DELETE` with the last-known `sha` under the first-save-wins rule; service rows already using the hymn keep their lyrics; users cannot undo it) | Q1, Q4; design document F3 |
| F4 | Bible passages from the ESV API | Q1 |
| F4a | Catechism slides | Q1 |
| F5 | Announcements and supplications | Q1 |
| F5a | Spiritual Wisdom slides | Q1 |
| F6 | Template and configuration, maintained by the developer | Q1 |
| F7 | Next Week's Schedule | Q1 |
| F8 | First start as a step-by-step wizard: setup file, your name, Google sign-in, and the shared library loaded in the last step. The setup file and Google sign-in steps are built in proto-Unit 1; the name step is built in proto-Unit 5, with the service builder, because its name feeds the "Hi [name]" greeting | Q1; rough-mockups-questions.md Q9; design document F8; intent-backlog.md proto-Units 1 and 5 |
| F9 | Error handling | Q1 |
| F10 | Settings | Q1 |
| F11 | Distribution and update check: a zipped self-contained app image per platform (Windows x64, macOS Apple Silicon, macOS Intel), with no installer. On start-up the app makes one request to GitHub's public latest-release endpoint, with a 5-second timeout. Only the latest published release counts, and "newer" means a higher major.minor.patch version. If the release is newer, the start-up screen shows "A new version of AutoPPT is available (x.y.z)" with Download (opens the zip for this computer in the browser) and Continue. If the check fails, start-up carries on silently. The app never downloads or replaces itself | Q1; constraint-register.md C-T3; design document F11 |
| F12 | Chinese Monthly Scripture slide with an empty body | Q1 |

Migrating the 116 legacy hymns is must-have for the first release (Q4). Nothing in the first version is marked nice-to-have (Q5).

### Out of the first version

| Item | Source |
|------|--------|
| The Chinese language selector and Chinese support beyond the groundwork marked "now" (F10, design section 11) | Q1 |
| Presenting or editing slides in the app, previewing slides, generating the Chinese pinyin picture, creating slide types or editing the template in the app | Q1; intent-statement.md Initial Scope Signal |
| Restoring older hymn versions, merging concurrent edits, remote logging, Google Sheets input, code signing | Q1; intent-statement.md Initial Scope Signal |

## Sequencing

The order is risk-first, then a thin end-to-end path, then breadth (Q2):

1. Spikes S1 to S3, as feasibility-assessment.md requires (Q2).
2. A thin end-to-end path, the walking skeleton: setup file, Google sign-in, template reader, and one Bible passage slide written into a real PPT (Q2), together with F0, the start-up screen, the internet requirement and the connection-lost cover (rough-mockups-questions.md Q9; proto-Unit 1 in intent-backlog.md).
3. The paginator and each remaining slide type (Q2).
4. The editors, hymn sharing and migration (Q2), together with the service builder, which carries the first-start name step and the "Hi [name]" greeting (rough-mockups-questions.md Q9; proto-Unit 5 in intent-backlog.md).

If a spike fails, the documented fallback in feasibility-assessment.md is used (feasibility-questions.md Q7).

## Dependencies Between Capabilities

- Sign-in and the setup file (F8) come before making slides (F2) (Q1).
- The template reader and tags (F6) come before making slides (F2) (Q1).
- The paginator measures text for passages, hymns and text blocks (Q1).
- Hymn editing (F3) needs the shared library (Q1).

## Constraints Carried In

The first version honours the constraints in constraint-register.md, including the self-contained executable with no installer and no cloud hosting, free services and free fonts only, and no fixed launch date (constraint-register.md C-T3, C-T4, C-T8, C-O2).

## Scope Against Timeline

There is no fixed launch date, so the full M set fits the timeline by definition (feasibility-questions.md Q4). The legacy app stays usable until operators have used the new app on 4 consecutive Sundays (intent-statement.md Success Metrics).

## Assumptions & Open Questions

None.
