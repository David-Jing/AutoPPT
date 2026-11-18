# Team Practices (Lead Draft)

Every item below is a suggested default for the team to confirm in the interview. None is a confirmed fact. The team is one developer, who alone decides scope and priority (C-O1).

## Way of Working

- **Integration branch**: main
- Suggested default: trunk-based development on `main`. Short-lived topic branches are optional for a solo developer and merge by squash. Commits may go straight to `main` for small changes.
- Suggested default: two repositories, AutoPPT (the app) and AutoPPTData (shared data), each with its own `main`.
- Suggested default: no review gate other than the developer's own check. Self-review the diff before merging.
- Suggested default: version tags (`vMAJOR.MINOR.PATCH`) on `main` mark releases, because the start-up update check compares major.minor.patch.

## Walking Skeleton

- Suggested default: build a thin end-to-end slice first. It is a minimal version that runs the whole way through, built first to prove the pieces connect before the real features go in.
- Suggested default: the slice is proto-Unit 1 of the backlog: start-up screen and internet check (F0), setup file and Google sign-in (F8), template reader (F6), and one Bible passage slide written into a real PPT (F4).
- Suggested default: the spikes S1 to S3 (text measurement, hanging indent, Google sign-in) run before the slice, as the scope sequencing requires.

## Testing Posture

- **Methodology**: test-after
- **Methodology evidence**: suggested default only. The org default is test-after, and nothing in the inputs states a team preference. The team may prefer tdd for the paginator, which is pure logic.
- **Ordering**: Suggested default: implement each testable layer first, then write and run that layer's tests before moving to the next layer.
- Suggested default: JUnit-style unit tests for pure logic (paginator, template tags, hymn migration, version comparison). Network clients (Google, GitHub, ESV) are tested against fakes, with the real services used only in the spikes and a manual check.
- Suggested default: the org scopes add an 80% line-coverage floor and CI execution before merge. The team decides whether to adopt it.
- Suggested default: a manual check of one real PPT build in Google Drive before each release, since the output is a visual slide deck.

## Deployment

- Suggested default: GitHub Actions builds on Windows x64, macOS Apple Silicon and macOS Intel runners, using Java 21 and Gradle 9.x.
- Suggested default: CI on every push to `main` runs build and tests. A pushed version tag builds the zipped self-contained app image for each platform and publishes a GitHub release. The app only reads the latest-release endpoint.
- Suggested default: no installer, no code signing, no cloud hosting (C-T3, C-T4). Release is the zip on a GitHub release.
- Suggested default: no staging environment. The legacy app stays usable until operators have used the new one on 4 consecutive Sundays, which acts as the production gate.
- Suggested default: rollback means marking an earlier release as latest or deleting the bad release, since the update check reads only the latest published release.

## Code Style

- Suggested default: Java 21, Swing with FlatLaf, Guice 7 with constructor injection only (C-T1).
- Suggested default: use a Gradle-run formatter and linter (for example Spotless and Checkstyle) enforced in CI. The tool choice is for the team to confirm.
- Suggested default: language-idiomatic naming (camelCase members, PascalCase types).
- Suggested default: the UI and artifacts say "PPT" or "slides", and the UI never says "GitHub", "repo", "commit", "API" or "token" (C-O4, C-O5).
- Suggested default: secrets never go in code or logs. The redactor keeps them out of logs (C-R3).
