# Personas

Inputs: `requirements.md`, `team-practices.md`, and `user-stories-questions.md` (Q1 to Q5). The design document's section 4 names the roles.

## Priority Ranking

1. Operator (the main user, and the one who carries the 10-minute goal)
2. Developer (maintains what operators never see)

## Operator

- **Role:** builds the weekly Sunday PPT for FCNABC's English congregation. Fewer than 10 people. Operators are non-technical (requirements.md FR15.7, intent-statement.md). The same people also edit, add, share and delete hymns, so hymn editing is part of this persona (Q4).
- **Goals:** enter the week's details and get a finished Google Slides PPT in under 10 minutes, with no touch-up on 4 consecutive Sundays, and add only the Chinese Monthly Scripture picture by hand (NFR5, NFR6).
- **Pain points:** the legacy app is driven by a hand-edited `.ini` file, and the reference PPTs contain manual mistakes that have to be fixed by hand (intent-statement.md).
- **Context:** a Windows or macOS computer, a personal Google account, an internet connection, and a default web browser. No technical vocabulary: the app says "shared library", "Check for updates" and "Save for everyone" (FR15.7).

## Developer

- **Role:** the sole decision-maker for scope and priority. Maintains the template, slide types, presets, measurement fonts and setup files, and fixes reported errors (requirements.md FR13; design document section 4).
- **Goals:** keep the app correct without code changes when the template changes, migrate the 116 legacy hymns once, run the spikes first, and publish each release safely (FR8, FR13, FR17, NFR17).
- **Pain points:** a template that drifts from the app, text measurement that does not match what Slides renders, and a release that reaches operators before it is checked.
- **Context:** Google Slides, the `AutoPPTData` repository, GitHub Actions, and this repository. Comfortable with the whole stack (feasibility-questions.md Q3).

## Persona Relationships

- The Operator uses what the Developer maintains. The Developer receives the Operator's reported errors through "Copy details for support" (FR15.3).
- Church leadership and the congregation have a stake in the outcome but do not use the app, so they have no stories (Q1).

## Assumptions & Open Questions

None.
