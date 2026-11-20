# Bolt Plan: AutoPPT Rewrite

A **Bolt** is one build pass over a piece of the work, ending in something that runs and a clear check that it works. Here each Bolt builds exactly one **Unit of work** (a piece the developer builds, tests and merges as one piece). This plan says the order. The engine builds Units one at a time in the order of the edge block in `unit-of-work-dependency.md`, which lists them in the order below, and it does not read this file for grouping or order.

Inputs: `components.md` (the 32 components, each owned by one Unit, so each Bolt builds whole components or the parts of a component that `unit-of-work.md` assigns to it), `unit-of-work.md`, `unit-of-work-dependency.md`, `unit-of-work-story-map.md`, `contract-summary.md`, `team-practices.md`, and `delivery-planning-questions.md` (Q1 to Q8). Every Bolt traces to a Unit, and every Unit to the stories in `unit-of-work-story-map.md`.

## The order

| Bolt | Unit | Size | Walking skeleton | Depends on (Units) |
|---|---|---|---|---|
| B1 | U2 `u2-first-slice` | XL | **Yes** | none |
| B2 | U1 `u1-spikes` | S | no | none |
| B3 | U3 `u3-layout` | L | no | U1, U2 |
| B4 | U6 `u6-template-check` | S | no | U2 |
| B5 | U4 `u4-service-builder` | XL | no | U2, U3 |
| B6 | U5 `u5-hymns-and-sharing` | L | no | U2, U4 |
| B7 | U7 `u7-packaging` | M | no | U2 |

The order follows the graph: every Bolt comes after the Bolts of the Units it depends on. U1 and U2 are both roots, so B2 before B3 is the only constraint on U1. There is no departure from topological order, so nothing needs a justification beyond the reasons in `risk-and-sequencing-rationale.md`.

## Before Construction: the spikes, run by hand

The spikes S1 to S3 run before any Bolt, outside the engine (Q2). The AI developer agent runs S1 (text measurement against what Slides renders) and S2 (the hanging indent with a superscript number) with the developer. S3 (Google sign-in as an unverified app) needs the developer's Google account and a Gmail sign-in that is not a test user, so the developer does it. Each gets a pass or fail, and a failed spike uses its documented fallback (feasibility-questions.md Q7). The results set values in `config/layout.yaml` (the safety margin, the inner padding, the indent method) and decide whether the fallback sign-in is used. B2 later records the results as the Unit U1.

## Bolt entries

### B1. U2 `u2-first-slice` (the walking skeleton)

- **Units:** U2.
- **Walking skeleton:** yes. It proves the whole chain connects: the app starts, the wizard saves the keys, Google signs the user in, the template reader finds the slide, the ESV service returns text, and a real PPT is written in the user's Drive.
- **What it proves out:** the Gradle build with its checks; start-up and the internet check; the connection cover; the single-copy lock; the wizard (setup file, name, sign-in); `SecretStore`, `Redactor` and `GoogleAuth`; the read-only shared library and `Configuration`; `TemplateReader`; `EsvClient`; a thin `MakeSlidesRun` and `SlideWriter` with thin `PassageLayout` and `Paginator`; and every port in `contract-summary.md` C1, which all later Units use.
- **Also delivers:** the initial `AutoPPTData` configuration files, committed to the `AutoPPTData` repository: `config/app.yaml` (with the template v3 id `1Njchznjkd2toS1DqIvAStjP0w4gyMNq9S8j1q8v9U_E`), `config/slide-types.yaml`, `config/presets/normal.yaml`, `config/presets/holy-communion.yaml`, `config/layout.yaml` (the spike results fill its values), `config/fonts.yaml` and `README.md`.
- **Definition of Done:**
  - The automatic check passes: formatting (Spotless), every test with network clients replaced by fakes, and the 80% line-coverage floor on `core`.
  - Each port in contract C1 has an interface in `core` and an implementation in its module, and each failure kind and time limit in C2 and C1 is tested against a fake.
  - The initial configuration files exist in `AutoPPTData` and the app reads them.
  - The developer runs the demo below for real and approves the skeleton.
- **Confidence hypothesis:** the app can be started, set up and signed in, and it can write one passage into a real PPT through the real Google and ESV services. If this holds, every later Bolt builds on a base that connects.
- **Expected demo:** start the app, go through the wizard with a real setup file (setup file, name, Google sign-in), press Make slides on one Bible passage row, and see a real PPT open in the browser with that passage on a slide (Q5).
- **Real check:** the automatic build above, plus the developer's own manual run of the demo with the real services, confirmed at the skeleton approval. The automatic part does not prove the real Google or ESV run, because the team rule keeps real services out of automatic checks.
- **Stories:** the 24 stories that list U2 in `unit-of-work-story-map.md` (the start-up, wizard, library, template and first-slice stories).
- **Blocked by (the developer's tasks):** the `autoppt:` tags in the template, a real setup file, and the Google client set to In production. See `external-dependency-map.md`.

### B2. U1 `u1-spikes`

- **Units:** U1.
- **Walking skeleton:** no.
- **What it proves out:** nothing new is built. It records the pass or fail of S1, S2 and S3 and the values they set (the safety margin, the inner padding, the indent method, and whether the fallback sign-in is used), so that U3 and the spike story are traceable (US17.1).
- **Definition of Done:** each of S1, S2 and S3 has a recorded pass or fail, and any failed spike names the fallback used. `layout.yaml` carries the measured values.
- **Confidence hypothesis:** the text measurement with TeX Gyre Adventor is close enough to Century Gothic, the hanging indent renders evenly, and sign-in works for a user who is not a test user.
- **Expected demo:** the three recorded results, each with the evidence the spike gathered.
- **Real check:** a reviewer can read each result and its evidence. There is no code to test.
- **Stories:** US17.1.
- **Note:** U3 depends on this Bolt. The spikes themselves ran before Construction (above).

### B3. U3 `u3-layout`

- **Units:** U3.
- **Walking skeleton:** no.
- **What it proves out:** the main technical risk. The full `Paginator` with the bundled font and balanced splitting, `PassageLayout`, `HymnLayout`, `TextBlockLayout`, `SimpleSlideLayout` and `ServiceDates`.
- **Definition of Done:**
  - The layout tests pass, written before the code for the paginator and other pure logic (team rule).
  - They include the reference cases from the two reference PPTs and the rules FR5.7 and FR6.2 (the chosen split has the smallest line-count difference among valid break points).
  - The 80% coverage floor on `core` holds.
  - A passage, a hymn and a text block each produce a `SlidePlan` that the thin writer from B1 can write.
- **Confidence hypothesis:** the paginator produces balanced slides that do not overflow, when measured against what Slides renders.
- **Expected demo:** build a plan for a long passage, a five-slide hymn and a long announcement, write each with the B1 writer, and open the PPT to see no overflow.
- **Real check:** the automatic check plus the developer's manual look at that PPT.
- **Stories:** the 18 stories that list U3 in the story map.

### B4. U6 `u6-template-check`

- **Units:** U6.
- **Walking skeleton:** no.
- **What it proves out:** the developer-only Check template command (`TemplateChecker`).
- **Definition of Done:** the checker reports a missing tag, a missing placeholder and an unknown font in a placeholder box, and does not report a different font in fixed static text. Its tests use a fake `TemplateReaderPort`.
- **Confidence hypothesis:** tag and font mistakes in the template show up before an operator hits them.
- **Expected demo:** run Check template on the template v3 with its missing tags and see every missing tag reported.
- **Real check:** the automatic check plus the run on the real template.
- **Stories:** US13.3.

### B5. U4 `u4-service-builder`

- **Units:** U4.
- **Walking skeleton:** no.
- **What it proves out:** the service, its rows and editors, Settings, the error dialog, and the full Make slides run with progress and clean-up (`ServiceBuilder`, `ServiceStore`, `ErrorModel`, `SlidesCleaner`, the editors, and the completed `MakeSlidesRun`).
- **Definition of Done:** the stop and finish rules, the retries, the template access check, last week's PPT and half-built PPT clean-up, and the leftover cleanup at the end of a finished run are tested against fakes. Every screen follows the keyboard, contrast and wording rules.
- **Confidence hypothesis:** an operator can build a whole service and make the PPT, and a failed row or a lost connection never leaves them without last week's PPT or with a half-built one.
- **Expected demo:** build a full Sunday service from the Normal order, make the PPT, see it open in the browser with every row done, and see last week's PPT moved to the Trash. Cut the connection mid-run and see the run stop with the rest Skipped.
- **Real check:** the automatic check plus the developer's manual run.
- **Stories:** the 34 stories that list U4 in the story map.

### B6. U5 `u5-hymns-and-sharing`

- **Units:** U5.
- **Walking skeleton:** no.
- **What it proves out:** the hymn editor and its confirmations, `HymnStore`, `ReloadAllHymns`, and `HymnMigrator`.
- **Definition of Done:** first-save-wins is tested against a fake (a stale save is refused with the plain message), Reload all hymns changes nothing on a failed download, local hymn edits survive closing the app, and the one-time migration converts the 116 legacy hymns with a spot check of five.
- **Confidence hypothesis:** several operators can share hymns safely, and a bad edit or a failed reload loses nothing.
- **Expected demo:** edit and save a hymn for everyone, see a stale save refused, reload all hymns, and migrate the legacy hymns.
- **Real check:** the automatic check plus the developer's manual run against the real `AutoPPTData` repository.
- **Stories:** the 11 stories that list U5 in the story map.
- **Blocked by (the developer's tasks):** the legacy `HymnDatabase.db`, for the migration.

### B7. U7 `u7-packaging`

- **Units:** U7.
- **Walking skeleton:** no.
- **What it proves out:** the zipped app image for each platform, the update check and its screen (`ReleaseChecker`), the licences and the first-run guide.
- **Definition of Done:** a pushed version tag builds the three zips as a draft release, the update check shows the prompt only for a newer published release, and the zip holds only TeX Gyre Adventor with its licence.
- **Confidence hypothesis:** an operator can unzip and run the app on each supported computer, and hears about a new version only after the developer publishes it.
- **Expected demo:** tag a version, check the three zips, publish the release and see the update prompt in an older build.
- **Real check:** the automatic check, the manual run of one real PPT built in Google Drive (team rule), and a run of each zip on a clean machine.
- **Stories:** the 9 stories that list U7 in the story map.

## How Bolts are approved

Gated for now (Q8): the developer approves each Unit's completion. The skeleton in B1 always needs the developer's own approval. After the skeleton is approved, the engine offers to switch to autonomous completion, and the developer decides then. Plan Approval, summary confirmation and the check command stay the developer's decisions either way.

## Construction settings

Construction Iteration is unit-major (each Unit's design stages and code, one Unit at a time) and Execution is serial, as recorded in `aidlc-state.md`. The branch is `main`, with short-lived topic branches that squash-merge (team.md). The verification command is chosen at the end of this stage (see the approval message).

## Sources

- [Q1] The documents' answers: one builder, the walking skeleton on, U2 first, unit-major and serial, the test and release rules, no fixed date.
- [Q2] The spikes run by hand before Construction, and the engine's first Unit is U2.
- [Q3] The order U3, U6, U4, U5, U7 after the slice.
- [Q4] One Unit for each Bolt, 7 Bolts.
- [Q5] The demo and check of the first Bolt.
- [Q6] The configuration files are a U2 deliverable, and what stays with the developer.
- [Q8] Gated approval for now.
- [scope] Unit, story and contract ids come from the Units Generation and Contract Design files.

## Assumptions & Open Questions

- [assumption] The edge block of `unit-of-work-dependency.md` was reordered so that the engine takes U2 first and U1 second. The engine takes the first unit it can build. If the engine does not follow that order, the fix is to revisit Units Generation, as Q2 said.
- [decided] B1 writes the initial configuration files to `/Users/david/Documents/AutoPPTData` by explicit path (developer's choice A). `aidlc engine intent add-repo AutoPPTData` was refused ("this piece of work treats the workspace folder itself as its code repo, so it records no repos to change"), so `AutoPPTData` is not a recorded repository of this work. B1's Code Generation plan names each file in `AutoPPTData` in its source manifest, the developer approves that plan, and the developer commits the files in that repository. The AI developer agent does not commit there.
- [assumption] The developer runs the spikes before B1 starts, so `layout.yaml` has measured values when U2 writes it. If they are not done, U2 writes placeholder values and B2 or B3 fills them.
- Open: whether the engine, with two root Units, builds U1 before U2 despite the listing order. Checked when Construction starts.
