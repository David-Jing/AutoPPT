# RAID Log: AutoPPT Rewrite

Inputs: `intent-statement.md`, `feasibility-questions.md` (Q1 to Q8), and `high-level-design.md` v0.5.

## Risks

| ID | Risk | Status | Response | Source |
|----|------|--------|----------|--------|
| R-1 | Crossway revokes the shared ESV key, stopping scripture text for every user | Accepted | Accepted as the design document says; the developer can issue a new setup file | Q6; design D6, D7 |
| R-2 | Google's unverified-app screen or token expiry blocks sign-in | Accepted | Accepted; spike S3 confirms behaviour, and the fallback is `drive.file` with the Google Picker | Q6, Q7; design section 10 |
| R-3 | Measured text differs from what Slides renders, so slides overflow or are under-filled | Open | Spike S1 first; fallback is a Google Fonts family measured with its exact files | Q7; design section 12 |
| R-4 | The hanging indent with superscript verse numbers renders unevenly | Open | Spike S2 first; choose between a tab and spaces | Q7; design section 12 |
| R-5 | Keys and the Google refresh token are stored unencrypted, so anyone with that user's login on that computer can read them | Accepted | Accepted for fewer than 10 users; the file is user-only | Q1; design D25 |
| R-6 | No legacy baseline exists to compare the 10-minute target with | Accepted | Accepted in intent capture; no baseline is recorded | intent-statement.md Accepted Risks |
| R-7 | A leaked setup file stays usable until the developer revokes its keys | Open | Developer revokes and reissues; the app offers to delete the file after import | Q1; design section 10 |
| R-8 | The Intel macOS build stops being possible when GitHub's last x86_64 runner image retires in August 2027 | Open | Apple Silicon remains; revisit before then | Q1; design F11, D16 |

## Assumptions

| ID | Assumption | Source |
|----|------------|--------|
| A-1 | The developer is comfortable with Java, Swing, Guice and the Google Slides and Drive APIs, so no skill gap is planned for | Q3 |
| A-2 | Free services and free fonts cover every need | Q5 |
| A-3 | TeX Gyre Adventor widths match Century Gothic closely enough; this stays a hypothesis until S1 | Q1; design D20 |
| A-4 | No fixed launch date applies | Q4 |

## Issues

| ID | Issue | Resolution | Source |
|----|-------|------------|--------|
| I-1 | The design document plans `.msi` and `.dmg` installers, while the intent statement needs only a self-contained executable | Resolved: zipped app images only, and the design document's D16 and F11 were updated to match | Q2 |

## Dependencies

| ID | Dependency | Source |
|----|------------|--------|
| D-1 | Google Slides and Drive APIs, and the church's Google Slides template shared with each user | Q1; design F2, section 10 |
| D-2 | The ESV API and the shared key | Q1; design D6, D7 |
| D-3 | The private GitHub repo `AutoPPTData` and one shared token | Q1; design D8, section 10 |
| D-4 | Spikes S1 to S3 finish before the rest of the build | Q7 |
| D-5 | GitHub Actions runners for Windows x64, macOS Apple Silicon and macOS Intel builds | Q2; design F11 |

## Assumptions & Open Questions

None.
