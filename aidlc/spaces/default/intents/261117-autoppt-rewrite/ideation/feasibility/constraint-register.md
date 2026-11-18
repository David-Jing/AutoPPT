# Constraint Register: AutoPPT Rewrite

Inputs: `intent-statement.md`, `feasibility-questions.md` (Q1 to Q8), and `high-level-design.md` v0.5.

## Technical

| ID | Constraint | Source |
|----|------------|--------|
| C-T1 | Java 21 (LTS), Gradle 9.x, Swing with FlatLaf, Google Guice 7 with constructor injection only | Q1; design D1, D22 |
| C-T2 | The app runs on Windows and macOS (Apple Silicon and Intel) | intent-statement.md Constraints; Q2 |
| C-T3 | Distribution is a zipped self-contained app image per platform with Java bundled: no formal installer, no `.msi`, no `.dmg`, no WiX | Q2; intent-statement.md Constraints |
| C-T4 | No cloud hosting; the only external services are Google, GitHub and the ESV API | Q1; intent-statement.md Constraints |
| C-T5 | The template may use only fonts the app can measure (Century Gothic measured with TeX Gyre Adventor, Times New Roman with Tinos, or a Google Fonts family) | Q1; design D20, section 7.4 |
| C-T6 | ESV limits: 500 verses per query, 5,000 queries a day, 1,000 an hour, 60 a minute, and no more than 500 verses stored locally, so passage text is kept in memory for the session only | Q1; design F4 |
| C-T7 | Google scopes `presentations` and `drive` (a restricted scope); the template must be shared with each user with copying allowed | Q1; design section 10 |
| C-T8 | Free services and free fonts only | Q5 |

## Organisational

| ID | Constraint | Source |
|----|------------|--------|
| C-O1 | The developer alone decides scope and priority | intent-statement.md Initial Scope Signal |
| C-O2 | No fixed launch date; the legacy app stays usable meanwhile | Q4 |
| C-O3 | No competing priorities or approvals block the work | Q8 |
| C-O4 | Users are non-technical, so the UI never says "GitHub", "repo", "commit", "API" or "token" | Q1; design D5 |
| C-O5 | Artifacts and the UI say "PPT" or "slides" | intent-statement.md Success Metrics |

## Regulatory and Data

| ID | Constraint | Source |
|----|------------|--------|
| C-R1 | No regulated data (such as payment or health data) is in scope | Q1 |
| C-R2 | The ESV conditions of use ask for "ESV" with each quotation and forbid sharing a key; both are accepted risks | Q6; design D6, D7 |
| C-R3 | The shared ESV key, the shared-library token, the Google OAuth client secret and each user's refresh token sit unencrypted in a user-only file; a redactor keeps them out of logs | Q1; design D25, F9 |
| C-R4 | Bundled fonts ship with their GUST, OFL or Apache licence files | Q1; design section 10 |
| C-R5 | Unverified Google apps are capped at 100 users, well above fewer than 10 | Q6; design section 10 |

## Scope Boundary (from intent capture)

The first version leaves out the intent statement's listed non-goals and every feature the design document marks "later" (intent-statement.md, Initial Scope Signal).

## Assumptions & Open Questions

None.
