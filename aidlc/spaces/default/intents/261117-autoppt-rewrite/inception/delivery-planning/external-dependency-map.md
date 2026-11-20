# External Dependency Map: AutoPPT Rewrite

This file lists the things outside the code that a **Bolt** (one build pass over one Unit of work) needs, who owns each, and what happens if it is late. It stands alone: the Bolts are in `bolt-plan.md`.

Inputs: `delivery-planning-questions.md` (Q6), the RAID log (D-1 to D-5), `contract-summary.md` and `unit-of-work.md`.

There is no external team. Every gate below belongs to the developer, so no approval lead time applies. If an item slips, the affected Bolt builds against fakes (a stand-in for a real service in a test) and finishes the real run later (Q6).

## Gated items

| # | Item | Owner | Time it takes | Blocks | If it slips |
|---|---|---|---|---|---|
| E1 | The `autoppt:` tags in the template's speaker notes (template v3) | Developer | A short edit, once the tag names in `contract-summary.md` C10 are agreed | B1's real run (the slice needs the tag of the slide it writes), then B4 (U6 reports the missing ones) and B5 | Build against a fake `TemplateReaderPort`; B1's demo waits for the tags |
| E2 | A real setup file (Google client ID and secret, ESV key, library token and repository) | Developer | Developer-made | B1's real run and demo | Build against fakes; the demo waits |
| E3 | The Google client set to "In production" without verification (spike S3) | Developer | A settings change in the Google project | B1's real sign-in for anyone who is not a test user | Use the fallback (`drive.file` with the Google Picker); that is rework in U2 |
| E4 | The legacy `HymnDatabase.db` (in the legacy AutoPPTMaker repository) | Developer | Available now | B6's migration | The migration builds against a fake database; B6's migration demo waits |
| E5 | Writing the configuration files into the separate `AutoPPTData` repository. `aidlc engine intent add-repo AutoPPTData` was refused, so the repository is not recorded in this work. Decided: B1 writes the files there by explicit path, approved in B1's Code Generation plan, and the developer commits them | Developer | The plan approval and a commit | B1's configuration deliverable | The configuration files are written in this repository and copied to `AutoPPTData` by hand |
| E6 | The spike results (S1, S2, S3) | Developer and the AI developer agent | Before Construction | `layout.yaml` values in B1, and B3 | `layout.yaml` carries placeholder values until the results exist; B3 waits for them |
| E7 | GitHub Actions runners for Windows x64, macOS Apple Silicon and macOS Intel | GitHub | The Intel runner image retires in August 2027 (RAID R-8) | B7's Intel build, and only later | Apple Silicon remains; revisit before then |
| E8 | The church's Google Slides template shared with each operator, with copying allowed | Developer | A sharing setting for each user | Every real run, from B1 | The share-the-template message shows (FR12.6); share it and run again |

## Services the code calls

These are not gates, but a Bolt cannot run for real without them. They are pinned in `contract-summary.md` C11.

| Service | Used by Bolt | Notes |
|---|---|---|
| Google sign-in, Slides and Drive | B1 and every later Bolt | Free; the app is unverified and capped at 100 users (constraint C-R5) |
| ESV passage text service | B1, B3, B5 | The shared key can be revoked by the publisher (RAID R-1, accepted) |
| The shared library's file service | B1, B6 | A private repository and one shared token |
| The public latest-release endpoint | B7 | One request, silent on failure |

## Sources

- [Q6] The gated items and the change that U2 creates the configuration files.
- [scope] Gates and risks come from the RAID log (D-1 to D-5, R-1, R-8) and the constraint register.

## Assumptions & Open Questions

- [assumption] E1 needs the tag names from `contract-summary.md` C10 to be the ones the developer writes. One tag spelling is used: `autoppt:<kind>` or `autoppt:<kind> <name>`.
- [decided] E5 follows the developer's choice A (explicit path, approved in B1's plan, committed by the developer).
- None otherwise.
