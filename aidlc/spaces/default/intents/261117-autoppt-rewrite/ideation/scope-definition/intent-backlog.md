# Intent Backlog: AutoPPT Rewrite

Inputs: `intent-statement.md`, `feasibility-assessment.md`, `constraint-register.md`, `scope-definition-questions.md` (Q1 to Q5), and `high-level-design.md` v0.5.

## Prioritisation

MoSCoW: every first-version feature is Must, because everything marked M must be present before the first real Sunday use and nothing is nice-to-have (Q3, Q5). Items marked later are Won't (this version) (Q1). Order within Must follows the sequencing in Q2: risk-first, then a thin end-to-end path, then breadth.

## Backlog (proto-Units)

| Order | Proto-Unit | Capabilities | MoSCoW | Phase (Q2) | Source |
|-------|------------|--------------|--------|------------|--------|
| 0 | Spikes S1 to S3 | Text-measurement check, hanging-indent check, Google sign-in check | Must | 1. Spikes | Q2; feasibility-assessment.md |
| 1 | Thin end-to-end path | F8 setup file and Google sign-in, F6 template reader, one Bible passage slide (F4, and only the thin test slice of F2; the rest of F2 is in proto-Unit 5) written into a real PPT; and F0 start-up and internet connection (the start-up screen on every launch, internet required with no offline mode, the no-connection stop screen with Try again and Quit, and the connection-lost cover) | Must | 2. Walking skeleton | Q2; design document F0 |
| 2 | Core layout | Paginator, fonts, passage, hymn and text-block layout rules | Must | 3. Breadth | Q2; Q1 |
| 3 | Bible passages | F4 ESV client and passage slide types, F12 Chinese Monthly Scripture | Must | 3. Breadth | Q2; Q1 |
| 4 | Text slide types | F4a catechism, F5 announcements and supplications, F5a Spiritual Wisdom, F7 Next Week's Schedule | Must | 3. Breadth | Q2; Q1 |
| 5 | Service builder | F1 slide list, presets, reorder, autosave, and the "Hi [name]" greeting in the top bar; the first-start name step (F8) whose name feeds the greeting; F10 settings, including changing the name and the app version (About); F9 error handling; the rest of F2 Make slides: full assembly order and removal of leftover template slides, clearing speaker notes, the progress view where a failed row does not stop the run, the template access check, and "Delete last week's slides" | Must | 4. Editors | Q2; Q1; design document F2 |
| 6 | Hymns and sharing | F3 hymn editor, shared library, Save for everyone, the one-time migration of the 116 legacy hymns, and "Delete for everyone" (confirmation, then a Contents API `DELETE` with the last-known `sha` under the first-save-wins rule; service rows already using the hymn keep their lyrics; users cannot undo it) | Must | 4. Editors | Q2; Q4; design document F3 |
| 7 | Packaging | F11 "Distribution and update check": zipped app images for Windows x64, macOS Apple Silicon and macOS Intel, and the start-up check for a newer version (one request to GitHub's public latest-release endpoint; the request has a 5-second timeout, and only the latest published release counts; "newer" means a higher major.minor.patch version; if newer, a start-up state with Download and Continue; if the check fails or times out, start-up carries on silently; the app never downloads or replaces itself) | Must | 4. Editors | Q1; constraint-register.md C-T3 |

These proto-Units are a starting point; Units Generation decides the final unit boundaries.

## Deferred (Won't, this version)

| Item | Source |
|------|--------|
| Chinese language selector and Chinese support beyond the groundwork marked "now" | Q1 |

## Value Stream Map

| Step | What happens | Capability |
|------|--------------|------------|
| 1. First start | On every launch a start-up screen checks the connection and the app version. On first start, the operator then drops in the setup file, enters their name, signs in with Google and waits for the shared library | F0, F8, F11 |
| 2. Plan the service | The operator picks the date and order, then adds, removes and reorders slide rows | F1 |
| 3. Fill in the week | The operator enters passages, picks and edits hymns, writes announcements, catechism and Spiritual Wisdom, and fills the next week's schedule | F3, F4, F4a, F5, F5a, F7 |
| 4. Make slides | The app builds the PPT from the church template in the operator's Drive and opens it in the browser | F2 |
| 5. Finish by hand | The operator adds the Chinese Monthly Scripture picture in Google Slides | F12 |
| 6. Outcome | A finished Sunday PPT built in under 10 minutes with no touch-up (4 consecutive Sundays after launch) | intent-statement.md Success Metrics |

Sources: Q1 for the capabilities; intent-statement.md for the outcome.

## Assumptions & Open Questions

None.
