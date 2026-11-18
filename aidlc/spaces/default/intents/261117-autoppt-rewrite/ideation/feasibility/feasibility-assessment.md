# Feasibility Assessment: AutoPPT Rewrite

Inputs: `intent-statement.md` (intent-capture), `feasibility-questions.md` (Q1 to Q8), and `high-level-design.md` v0.5.

## Verdict

Feasible. The developer is comfortable with the whole stack (Q3), has no fixed launch date (Q4), needs no spend (Q5), and has no outside blockers (Q8). The remaining uncertainty is three technical spikes (S1 to S3), each with a documented fallback (Q7).

## Technical Viability

| Area | Assessment | Basis |
|------|------------|-------|
| Language and UI | Viable. Java 21, Gradle 9.x, Swing with FlatLaf; Java 21 was chosen over 25 because Guice 7 runs on it without warnings. | Q1; design D1, D22, section 9 |
| Building slides in Google Slides | Viable. The app copies the church template, duplicates tagged slides, fills placeholders and deletes the template originals through the Slides and Drive APIs. | Q1; design F2 |
| Bible text | Viable with an accepted risk. English text comes from the ESV API; ESV conditions forbid sharing one key, so Crossway could revoke it. | Q1, Q6; design D6, D7 |
| Shared hymns and config | Viable. One file per hymn in the private GitHub repo `AutoPPTData`, read and written through GitHub's API with one shared token. | Q1; design D8, section 8 |
| Text fitting (overflow to the next slide) | Uncertain until spike S1. The paginator measures text with a bundled font, and Century Gothic is measured with TeX Gyre Adventor, whose matching widths are a hypothesis. | Q1, Q7; design D20, section 7.4 |
| Hanging indent with superscript verse numbers | Uncertain until spike S2 (tab or spaces after the number). | Q1; design section 12 |
| Google sign-in with personal accounts | Uncertain until spike S3. The app needs the restricted `drive` scope and runs as an unverified app capped at 100 users, against fewer than 10 users. | Q1, Q6; design section 10 |
| Distribution | Viable and simpler than the design document. The intent statement governs: a zipped `jpackage --type app-image` build per platform (Windows x64, macOS Apple Silicon, macOS Intel), double-clicked after unzipping. Java is bundled and nothing is installed. No `.msi` or `.dmg`, so the WiX Toolset is not needed. | Q2; intent-statement.md Constraints |

## Design Document Conflict Resolved

The design document's feature F11 first planned `.msi` and `.dmg` installers. Your answer to Q2 overrode that, and the design document has since been updated to match (D16 and F11 now describe zipped `jpackage --type app-image` builds with no installer). The first version ships zipped app images only.

## Spikes to Run First

| Spike | Question it answers | Fallback if it fails |
|-------|---------------------|----------------------|
| S1 | Does the paginator's measured text match what Slides renders? | Switch the template text boxes to a Google Fonts family such as Questrial or Didact Gothic, measured with their exact files |
| S2 | Does the hanging indent with a superscript verse number render evenly? | Use the other of tab or spaces after the number |
| S3 | Does Google show a warning (not a block) for the unverified app, and do refresh tokens last? | Use the `drive.file` scope with the Google Picker, so each user picks the template once |

Per Q7, S1 to S3 run before the rest of the build, and the documented fallback is used if one fails.

## Risk Summary

- Accepted risks (Q6): the shared ESV key could be revoked, and the Google app stays unverified.
- Intent risk: no legacy baseline is recorded (intent-statement.md, Accepted Risks).
- Security: keys and each user's Google refresh token are stored unencrypted in a user-only file in the app-data folder (Q1; design D25).

The full list is in `raid-log.md` and `constraint-register.md`.

## Assumptions & Open Questions

None.
