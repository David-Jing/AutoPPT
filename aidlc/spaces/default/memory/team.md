# Team-Level Rules

> This team's affirmed practices and corrections. Loaded after `org.md` as
> strict-additive guidance.
> Populated by the practices-discovery affirmation gate. Edit at the gate,
> not directly.

## Way of Working

- **Integration branch**: main
- We work on `main` in each of the two repositories, AutoPPT (the app) and AutoPPTData (shared data). Small changes are committed straight to `main`. Bigger changes use short-lived topic branches that merge by squash.
- We tag releases `vMAJOR.MINOR.PATCH` on `main`, because the start-up update check compares major.minor.patch.

## Walking Skeleton

- We build a thin end-to-end slice first. It is a minimal version that runs the whole way through, built first to prove the pieces connect before the real features go in.
- The slice is the first slice in the approved scope: start-up screen and internet check (F0), setup file and Google sign-in (F8), template reader (F6), and one Bible passage slide written into a real PPT (F4). It comes after the spikes S1 to S3.

## Testing Posture

- **Methodology**: custom
- **Methodology evidence**: the interview (Q3) chose test-first (TDD) for the paginator and other pure logic, and test-after for the rest, so no single methodology fits.
- These test and style practices apply to the AutoPPT repository only. The AutoPPTData repository holds only hymn Markdown and YAML config, with no code.
- "core" is the Gradle module named in the design document, section 9 (the modules are core, google, esv, library and app). It holds the pure layout and pagination logic.
- **Ordering**: Write each test before the code for the paginator and other pure logic in the `core` module, and for every other layer implement it first and then write and run its tests.
- We enforce an 80% line-coverage floor on the `core` module only (layout, pagination and other pure logic), checked in the automated build before merge. There is no coverage floor on the Swing UI or on the Google, ESV and shared-library adapters.
- Network clients (Google, GitHub, ESV) are tested against fakes. The real services are used only in the spikes and the manual check.
- Before each release we manually check one real PPT built in Google Drive.

## Guard Policy

<!-- Affirmed by the team. Mode: strict, relaxed, or off. Strict here holds for every intent, whatever one person asks in chat; relaxed or off here applies to every intent whose policy came from its scope, unless project.md or the person sets another; changing this line changes it. A section under the retired Change Control heading, written by an earlier release, is still read. -->

## Deployment

- GitHub Actions builds on Windows x64, macOS Apple Silicon and macOS Intel runners, using Java 21 and Gradle 9.x. Every push to `main` runs build and tests.
- A pushed version tag builds the zipped self-contained app image for each platform and creates a draft GitHub release. We check the zips, then publish the release by hand. GitHub's latest-release endpoint ignores drafts, so users see the update prompt only after our check.
- There is no installer, no code signing and no cloud hosting (C-T3, C-T4). A release is the zip on a GitHub release.
- There is no staging environment. The legacy app stays usable until operators have used the new one on 4 consecutive Sundays, which is the production gate.
- Rollback means marking an earlier release as latest, or deleting the bad release, since the app reads only the latest published release.

## Code Style

- Java 21, Swing with FlatLaf, Guice 7 with constructor injection only (C-T1).
- Spotless with google-java-format, enforced in the Gradle build. We use no Checkstyle.
- Naming follows Java convention: camelCase members, PascalCase types.
## Forbidden

<!-- Team-specific forbidden patterns -->

## Mandated

<!-- Team-specific mandates -->

## Corrections

<!-- Self-learning loop appends here. -->
