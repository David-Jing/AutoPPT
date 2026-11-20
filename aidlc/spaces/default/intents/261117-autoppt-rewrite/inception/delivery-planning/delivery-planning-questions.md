# Delivery Planning Questions: AutoPPT Rewrite

Inputs: `unit-of-work.md`, `unit-of-work-dependency.md` and `unit-of-work-story-map.md` (7 Units, 8 edges), `contract-summary.md`, `components.md`, `requirements.md`, `stories.md`, `team-practices.md`, the RAID log and constraint register, and the memory rules (`team.md`, `org.md`).

This stage plans the order in which the Units are built. A **Bolt** is one build pass over a piece of the work, ending in something that runs and a clear check that it works. Unit Generation drew what can depend on what. This stage chooses the path through it, which is a value judgment only you can make.

Please answer by editing this file: write the letter after each `[Answer]:`.

## Q1. What your documents already answer

Each line is the point, the answer, and where it comes from.

- **One builder.** The developer builds alone and no team was formed, so the planned builder for every Bolt is the AI developer agent working with you. Source: this workflow's scope (mvp) skips Team Formation; RAID log A-1 says you are comfortable with Java, Swing, Guice and the Google APIs.
- **Walking skeleton on.** The first Unit that runs is a thin end-to-end slice: start-up and internet check, setup file and Google sign-in, template reader, and one Bible passage slide written into a real PPT. A real integrated check and your own approval prove it before the other Units start. Source: team.md Walking Skeleton and the mvp scope.
- **The first Unit that runs is U2.** U2 is a root of the graph, so the engine can build it first. Only U3 waits for the spikes (U1). Source: unit-of-work-dependency.md.
- **Order of build inside a Bolt.** Each Unit goes through its design stages and then code, one Unit at a time (unit-major, serial), with a check when it is done. Source: aidlc-state.md (Construction Iteration: unit-major, Execution: serial).
- **How tests are written.** Test first for the paginator and other pure logic in `core`; implement then test for everything else; an 80% line-coverage floor on `core` only; network clients against fakes; one manual check of a real PPT built in Google Drive before each release. Source: team.md Testing Posture.
- **Branches and releases.** Work happens on `main` in both repositories, with short-lived topic branches for bigger changes (squash merge). Releases are tagged `vMAJOR.MINOR.PATCH` and published by hand after checking the zips. There is no staging environment, and the legacy app stays usable until 4 consecutive Sundays on the new one. Source: team.md Way of Working and Deployment.
- **No fixed date.** Source: constraint-register.md C-O2 and RAID log A-4.
- **Parallel building is possible but you build alone.** U3, U6 and U7 have no edge between them after U2. Source: unit-of-work-dependency.md Parallel Development Opportunities.

A. All correct
B. Some need changing (say which and how)
X. Other (please specify)

[Answer]: A

## Q2. [Delivery] What do we build first?

Context: the spikes S1 to S3 are small experiments (text measurement, hanging indent with a superscript number, Google sign-in as an unverified app). U3 (layout) needs S1 and S2 results. U2 (the thin slice) uses the primary sign-in and falls back to a documented alternative if S3 fails, so U2 does not wait for the spikes (your Units Generation decision). The team rule says the slice comes after the spikes. The engine builds the first Unit in the graph as the walking skeleton, and it must be able to run.

A. **Spikes first by hand, then the slice.** You run S1 to S3 as small experiments before Construction starts, as the team rule says, and the U1 Unit later only records the results (nothing is built in it). The first Unit the engine builds is U2. To make that so, I list `u2-first-slice` first in the edge block of `unit-of-work-dependency.md`. If the engine still picks U1 first, we revisit Units Generation before building. (Recommended: it keeps the team rule that spikes come first, it puts the risk in S3 and S1 up front, and the engine's first Unit is the one that runs.)
B. **Slice first, spikes second.** Bolt 1 is U2 and Bolt 2 is U1 (the spikes, run through the engine), before U3. It follows the graph exactly, but S3 failing late means rework in U2's sign-in, and it overrides the team rule that the slice comes after the spikes. If you choose this, I record that exception.
C. **Spikes as the first Bolt in the engine.** Bolt 1 is U1 and Bolt 2 is U2. It also keeps the team rule, but U1 has no code, so the engine's skeleton rule (the first Unit must run end to end) would not hold, and Units Generation would have to be revisited.
X. Other (please specify)

[Answer]: A — the spikes S1 to S3 run before Construction, outside the engine; the AI developer agent runs S1 and S2 with the developer, and S3 needs the developer's Google account and a non-test-user Gmail sign-in. U1 only records the results. The engine's first Unit is U2.

## Q3. [Delivery] How are the remaining Units ordered?

Context: after U2 (and the spikes), U3 (layout) is needed by U4 (service builder), which U5 (hymns and sharing) builds on. U6 (template check) and U7 (packaging) each depend only on U2. Value to operators comes from U4 (a complete service and the full Make slides run) and U5 (hymns). U6 gives you a way to find missing template tags before they block a run, and U7 is only useful once the other Units exist.

A. **U3, then U6, then U4, U5, U7.** Layout first because it is the main technical risk and the next Unit needs it, then the small template check so tag mistakes show up before the full run is built, then the service builder, hymns and sharing, and packaging last. (Recommended: risk first, then the cheap Unit that removes a blocker, then value, with the release work last because it needs everything.)
B. **U3, U4, U5, U6, U7.** Straight down the graph. Simple, but the template check comes late, after the run that needs correct tags.
C. **U4's passage-only version before U3 completes.** Operators could use the app earlier, but it would need a stand-in layout, which the plan avoids (U2 already has a thin one).
X. Other (please specify)

[Answer]: A

## Q4. [Delivery] How big is one Bolt?

Context: there are 7 Units, and U2 and U4 are the largest (XL). Each Unit goes through design stages and code, then a check.

A. One Unit for each Bolt, 7 Bolts. Each Bolt ends with that Unit's check and a demo. (Recommended: it matches the Units, keeps each check small, and a solo builder gets feedback after every Unit.)
B. Bundle the small ones: U1 with U3 (the spikes feed the layout), and U6 with U7 (both small and independent). 5 Bolts. Fewer checkpoints, but a bundle hides which Unit broke a check.
C. Split the two XL Units into two Bolts each (for example U2: start-up and wizard, then sign-in and the first slide). Smaller steps, but U2 must run end to end as one slice, so splitting it breaks the walking-skeleton rule.
X. Other (please specify)

[Answer]: A

## Q5. [Delivery] What does the first Bolt (the thin slice) show, and how is it checked?

Context: with the walking skeleton on, the first Bolt needs an expected demo and a real check that proves it end to end. The team rule says network clients are tested against fakes and real services are used only in the spikes and the one manual check. The automatic check can therefore prove everything except the real Google run.

A. **Demo:** start the app, go through the wizard with a real setup file (setup file, name, Google sign-in), press Make slides on one Bible passage row, and see a real PPT open in your browser with that passage on a slide. **Check:** an automatic build that runs formatting, every test (with fakes) and the 80% coverage gate on `core`, plus your own manual run of the demo, which you confirm at the skeleton approval. (Recommended: it follows the team rule, and the real service is exercised once by you.)
B. **Demo as above, but the check also includes an automatic end-to-end test** that signs in and writes a slide with the real services, using a setup file you point it at. It proves more, but it needs your credentials and network on every check, which goes against the team rule.
C. **Defer** choosing the check until the first checkpoint asks for it. Allowed for a project with no code yet, but then nothing proves the slice until then.
X. Other (please specify)

[Answer]: A

## Q6. [Delivery] Which things outside the code could hold us up?

Context: some work belongs to you, not to a Unit. If it is late, a Bolt cannot finish.

- **Template tags.** The church template (v3) does not hold the `autoppt:` tags yet. You add them. U2's template reader and U6's checker need them. Owner: you.
- **The `AutoPPTData` configuration.** The files `app.yaml`, `slide-types.yaml`, the presets, `layout.yaml` and `fonts.yaml` (contract C7) live in the shared library and no Unit writes them. They are developer-maintained (FR13.3). U2 reads them, so they must exist, with the template id and slide types, before U2's first real run. The spike results set values in `layout.yaml`. Owner: you.
- **The setup file.** A real setup file (Google client, ESV key, library token) is needed for the first real run. Owner: you.
- **The Google client in production.** The Google client must be set to "In production" without verification for sign-in to work for people who are not test users (S3). Owner: you.
- **GitHub Actions runners.** The Intel macOS build ends when GitHub retires its last x86_64 runner image in August 2027 (RAID R-8). Blocks only U7's Intel build, and only later.
- **The legacy hymns.** The 116 hymns come from the legacy `HymnDatabase.db`. U5's migration needs that file. Owner: you.

A. All correct as listed. For each: the owner is you, there is no external team, a delay blocks only the named Bolt (the template tags and the `AutoPPTData` configuration block U2; the setup file and the Google client block U2's real run; the legacy file blocks U5), and if one slips we build against fakes and finish the real run later. (Recommended: it names every gate and its fallback, and nothing here is outside your control.)
B. Some need changing, or something is missing (say which and how).
X. Other (please specify)

[Answer]: B — all correct, with one change: U2 also creates the initial AutoPPTData configuration files as a deliverable, committed to the AutoPPTData repository: config/app.yaml (with the v3 template id 1Njchznjkd2toS1DqIvAStjP0w4gyMNq9S8j1q8v9U_E), config/slide-types.yaml, config/presets/normal.yaml, config/presets/holy-communion.yaml, config/layout.yaml (spike results fill its values), config/fonts.yaml and README.md. That removes the configuration from the developer's blocker list. The developer still owns: adding the autoppt: template tags, a real setup file, setting the Google client to In production, and the legacy HymnDatabase.db (available in the legacy AutoPPTMaker repository). For each, a delay blocks only the named Bolt and we build against fakes meanwhile.

## Q7. [Delivery] What worries you most, so we tackle it early?

Context: the risks open in the RAID log are the text measurement drifting from what Slides draws (R-3), the hanging indent with superscript numbers (R-4), Google blocking or expiring sign-in (R-2), and a leaked setup file (R-7). The paginator is also the main technical risk in the design (requirements.md Intent Analysis).

A. The paginator and text measurement: S1 and U3 come early, and U3 is the next Unit after the slice. (Recommended: it is the largest unknown, and every slide type depends on it.)
B. Google sign-in: S3 is run first, before the slice, because everything else needs a signed-in user.
C. Both: S1 to S3 all run before the slice (Q2 option A already does this), and U3 follows the slice.
X. Other (please specify)

[Answer]: C

## Q8. [Delivery] How are Bolts approved while building?

Context: the engine ends each Unit with a check and your approval (gated), or lets later Units go on by themselves once you say so (autonomous). The skeleton Bolt always needs your own approval. Plan approval, the summary confirmation and the check command stay your decisions either way.

A. Gated for now: you approve each Unit's completion. After the skeleton is approved, the engine offers to switch to autonomous, and you decide then. (Recommended: you do not commit to autonomy before you have seen the slice run.)
B. Autonomous from the start after the skeleton, with no later offer. Fewer stops, but you would not see a Unit before the next one starts.
X. Other (please specify)

[Answer]: A

## Consolidated Summary Confirmation

Does this all look correct before I generate the artifact?

- Looks correct
- Request changes

[Answer]: Looks correct
