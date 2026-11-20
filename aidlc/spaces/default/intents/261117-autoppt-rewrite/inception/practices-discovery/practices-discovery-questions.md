# Practices Discovery Questions

These questions settle how you work, one area at a time. The release engineer's draft suggests an answer to each, taken from the framework defaults and your design document. Nothing in the draft is a confirmed fact until you answer here. The draft is in `team-practices.md` in this folder.

## Q1. [Developer] How do you want to handle branches and merging?

Context: you are the only developer, and the draft suggests trunk-based work on one `main` branch for each of the two repositories (the app, and the shared hymn data), with version tags for releases. The tags matter because the app's start-up update check compares release versions.

A. Work on `main`; commit straight to it for small changes, use short-lived topic branches that merge by squash for bigger ones; tag releases `vMAJOR.MINOR.PATCH`
B. Always use a topic branch and merge it into `main` by squash, even for small changes; tag releases `vMAJOR.MINOR.PATCH`
C. Not yet defined
X. Other (please specify)

[Answer]: A

## Q2. [Developer] Should the first thing built be a thin end-to-end slice?

Context: build a thin end-to-end slice first? A walking skeleton is a minimal version that runs the whole way through, built first to prove the pieces connect before the real features go in. Your approved scope already sequences this: after the three spikes, the first slice is the start-up screen and internet check, the setup file and Google sign-in, the template reader, and one Bible passage slide written into a real PPT.

A. Yes, build that slice first, as the scope says
B. No, build the features one at a time without a thin slice first
C. Not yet defined
X. Other (please specify)

[Answer]: A

## Q3. [Developer] When do you write tests: before the code, after it, or a mix?

Context: the framework default is to write each layer first and then its tests (test-after). The paginator, which decides where text breaks across slides, is pure logic and suits tests written first. Your approved scope adds an 80% line-coverage floor for this kind of project unless you set another.

A. Test-after everywhere: write each layer, then its tests
B. Test-first (TDD) for the paginator and other pure logic, test-after for the rest
C. Test-first (TDD) everywhere
D. Not yet defined
X. Other (please specify)

[Answer]: B

## Q4. [Developer] Do you want the 80% line-coverage floor, and a manual check before each release?

Context: the floor is a framework default for this kind of project, and it must pass in the automated build before merge. The output of this app is a visual slide PPT, so the draft also suggests a manual check of one real PPT build in Google Drive before each release.

A. Yes to both: an 80% line-coverage floor, and a manual PPT check before each release
B. 80% floor only, with no manual release check
C. Manual release check only, with no coverage floor
D. Neither
E. Not yet defined
X. Other (please specify)

[Answer]: X. Other (please specify): an 80% line-coverage floor on the `core` module only (layout, pagination and other pure logic), enforced in the automated build; no coverage floor on the Swing UI or the Google, ESV and shared-library adapters. Plus a manual check of one real PPT built in Google Drive before each release.

## Q5. [Developer] How should releases be built and rolled back?

Context: the draft suggests GitHub Actions builds on Windows x64, macOS Apple Silicon and macOS Intel runners. A pushed version tag builds the zipped app for each platform and publishes a GitHub release. There is no staging copy, and the gate before the church relies on it is your 4 Sundays of use alongside the legacy app. The app only reads the latest published release.

A. As drafted: automated build on every push to `main`, tag-triggered zipped releases, no staging, and rollback by marking an earlier release as the latest (or deleting the bad one)
B. As drafted, but publish each release as a draft first and publish it by hand once you have checked it
C. Not yet defined
X. Other (please specify)

[Answer]: B — publish each release as a draft first, check the zips, then publish it by hand (GitHub's latest-release endpoint ignores drafts, so users only see the update prompt after the check).

## Q6. [Developer] Which formatter and linter should the automated build enforce?

Context: the draft suggests a Gradle-run formatter and linter checked in the build, for example Spotless for formatting and Checkstyle for rules. Naming is the usual Java style (camelCase members, PascalCase types). Your design document already fixes Java 21, Swing, FlatLaf and Guice with constructor injection only, and no secrets in code or logs.

A. Spotless (formatter) and Checkstyle (linter), both enforced in the build
B. Spotless only
C. No formatter or linter in the build
D. Not yet defined
X. Other (please specify)

[Answer]: B — Spotless only, using google-java-format, enforced in the build.

## Q7. [Developer] Are there any hard rules that must always, or must never, apply?

Context: a hard rule is recorded as a permanent project rule, shown as ALWAYS or NEVER. The draft has none, because the inputs state no hard constraints. A rule you name here would apply to every later stage. If you have none, choose A.

A. None
X. Other (please specify, for example "ALWAYS ..." or "NEVER ...")

[Answer]: X. Other (please specify): NEVER write keys, tokens or other setup-file secrets to logs or support details. NEVER show technical terms (GitHub, repo, commit, API, token) in the UI. ALWAYS call the generated output "PPT" or "slides", never "deck".

## Consolidated Summary Confirmation

Does this all look correct before I generate the artifact?

- Looks correct
- Request changes

[Answer]: Looks correct
