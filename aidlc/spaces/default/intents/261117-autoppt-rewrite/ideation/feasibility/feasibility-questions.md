# Feasibility & Constraints Questions

Sources for these questions: the confirmed intent statement (`intent-statement.md`, including its Constraints section) and the high-level design draft (`high-level-design.md`, v0.5).

## Q1. What your documents already answer

Context: the design document already settles several feasibility topics, so they are listed here once for you to confirm instead of being asked one by one.

- Existing systems to integrate with: Google Slides and Drive (the PPT is built from the church's template in each user's Google Drive), the ESV API (English Bible text), and the private GitHub repo `AutoPPTData` (shared hymns and config, read and written through GitHub's API with one shared token) (high-level-design.md §0 D6-D10, §8, §10).
- Tech stack and skills: Java 21, Gradle 9.x, Swing with FlatLaf, Google Guice 7, Google API clients, Jackson, commonmark-java, SLF4J with Logback; Java 21 was chosen over 25 because Guice 7 runs on it without warnings (high-level-design.md §0 D1 and D22, §9).
- Regulatory and data constraints: no regulated data is mentioned. The sensitive items are the shared ESV key, the shared-library token, the Google OAuth client secret and each user's Google refresh token, stored unencrypted in a user-only file in the app-data folder; the ESV terms are an accepted risk (high-level-design.md §0 D6, D7 and D25, §10).
- Known technical risks to test first: measuring text so slides do not overflow (S1), the hanging indent with superscript verse numbers (S2), and Google's unverified-app sign-in with the `drive` scope (S3) (high-level-design.md §10 and §12).
- Cloud use: none beyond Google, GitHub and ESV. The intent statement's constraint says no cloud hosting.

A. All correct
B. Some need changing (say which and how)
X. Other (please specify)

[Answer]: A

## Q2. [Developer] Which distribution form governs: your intent statement or the design document?

Context: your intent statement says the app is a self-contained executable with no formal installer needed. The design document (feature F11) plans `jpackage` installers: a Windows `.msi` (needs the WiX Toolset) and macOS `.dmg` files for Apple Silicon and Intel, built on three GitHub Actions runners. The Intel runner image is available only until August 2027.

A. Intent statement governs: ship a self-contained executable per platform and drop the `.msi` and `.dmg` installers from the first version
B. Design document governs: keep the `jpackage` `.msi` and `.dmg` installers
C. Ship both a self-contained executable and the installers
D. Not yet defined
X. Other (please specify)

[Answer]: A — ship a zipped `jpackage --type app-image` per platform (Windows x64, macOS Apple Silicon, macOS Intel). Users unzip once and double-click `AutoPPT.exe` (Windows) or `AutoPPT.app` (macOS); nothing is installed and Java is bundled. No `.msi` or `.dmg`, so WiX is not needed.

## Q3. [Developer] Do you have the Java and Swing skills for this stack, and is anything outside your experience?

Context: the design uses Java 21, Swing, Guice, the Google Slides and Drive APIs, and font measurement through AWT. Skill gaps are a feasibility risk in the RAID log.

A. Comfortable with the whole stack
B. Comfortable with Java, but new to the Google Slides and Drive APIs
C. Comfortable with Java, but new to Swing and font measurement
D. New to several parts (name them)
X. Other (please specify)

[Answer]: A

## Q4. [Developer] Is there a launch date or other time limit?

Context: the design document lists no timeline. A date decides whether the three spikes (S1 to S3) can run before the rest of the build.

A. No fixed date
B. A target date, with the legacy app still usable until then (give the date)
C. A hard date, because the legacy app will stop working (give the date and the reason)
D. Not yet defined
X. Other (please specify)

[Answer]: A

## Q5. [Developer] Is there a budget limit, or any paid service you must avoid?

Context: the design depends on free tiers and free fonts. A paid dependency would be a constraint. The design document says full Google verification is not pursued because it needs a paid security assessment.

A. No spend: free services and free fonts only
B. A small budget is available (give the amount)
C. Not yet defined
X. Other (please specify)

[Answer]: A

## Q6. [Developer] How should the shared ESV key and the unverified Google app be treated in the RAID log?

Context: the design document accepts two risks. The ESV conditions forbid sharing one key and require "ESV" with each quotation, so Crossway could revoke it and stop scripture text for every user. Google shows a one-time "not verified" screen, and caps unverified apps at 100 users.

A. Record both as accepted risks, as the design document says
B. Accept the Google risk, but get Crossway's permission for the shared key before launch
C. Treat both as launch blockers to resolve first
D. Not yet defined
X. Other (please specify)

[Answer]: A

## Q7. [Developer] If a spike fails (for example Century Gothic text widths do not match TeX Gyre Adventor), what happens?

Context: the design document gives fallbacks: a different template font for S1 and the Google Picker with `drive.file` for S3. Delivery planning needs to know whether a failed spike stops work or only changes the design.

A. Run S1 to S3 first, and use the documented fallback if one fails
B. Run S1 to S3 first, and stop to re-plan with you if one fails
C. Run S1 to S3 in parallel with the build, and use the fallback if one fails
D. Not yet defined
X. Other (please specify)

[Answer]: A

## Q8. [Developer] Does anything outside the project block or compete for your time?

Context: the Feasibility stage records organisational blockers, such as a change freeze or competing priorities, in the constraint register.

A. None
B. Competing priorities, so work happens in spare time (describe)
C. A blocker such as an approval or access needed from the church (describe)
D. Not yet defined
X. Other (please specify)

[Answer]: A

## Consolidated Summary Confirmation

Does this all look correct before I generate the artifact?

- Looks correct
- Request changes

[Answer]: Looks correct
