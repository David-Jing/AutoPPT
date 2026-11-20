# User Stories Questions (Story Plan)

Sources for this plan: the approved requirements (`requirements.md`, FR1 to FR17 and NFR1 to NFR18), the affirmed team practices (`team-practices.md`), the approved scope and backlog, and the design document's section on users and roles. The assessment of whether stories are needed is in `user-stories-assessment.md`: they are, because the app is user-facing with two personas (the Operator, including hymn editing, and the Developer) and rules the operator sees directly.

## Q1. What your documents already answer

Context: the documents already settle the people, the story format and the priority, so they are listed here once for you to confirm instead of being asked one by one.

- Personas: three roles from the design document: the Operator (fewer than 10 people, builds the weekly PPT and adds the Chinese picture by hand), the Hymn editor (edits, adds and shares hymns, the same people as Operators), and the Developer (maintains the template, slide types, presets, measurement fonts and setup files, and fixes reported errors). Church leadership and the congregation have a stake in the outcome but do not use the app, so they get no stories (high-level-design.md section 4; intent-statement.md).
- Story format: "As a [persona], I want [goal], so that [benefit]", with Given/When/Then acceptance criteria, checked against INVEST (the framework's inception rule).
- Story IDs: `US{group}.{seq}` for stories and `AC{group}.{seq}.{n}` for criteria. Every functional and non-functional requirement is traced to one or more stories in `traceability.json`.
- Priority: every first-version feature is a Must Have, because everything marked M must be present before the first real Sunday use and nothing is nice-to-have (scope-document.md; scope-definition Q3, Q5). The minimum viable boundary is decided later, in Delivery Planning.
- Build order: stories carry the proto-Unit order from the backlog (spikes, thin end-to-end path, breadth, editors), so Delivery Planning can sequence them (intent-backlog.md).

A. All correct
B. Some need changing (say which and how)
X. Other (please specify)

[Answer]: A

## Q2. [Product owner] How should the stories be grouped?

Context: the requirements are grouped by feature (FR1 to FR17), and the backlog is grouped into 8 build steps. The grouping sets the `US{group}` numbers, so it is hard to change later.

A. By feature, following the requirement groups (one story group for each FR group, for example group 1 is start-up and connection)
B. By workflow, following the operator's week (first start, plan the service, fill in the week, make slides, finish by hand), with developer and hymn-editor stories in their own groups
C. By build step, following the backlog's proto-Units
D. Not yet defined
X. Other (please specify)

[Answer]: A

## Q3. [Product owner] How small should a story be?

Context: the requirements have 120 sub-requirements. A story for each one would be too many to review, and a story for each feature group would hide many separate behaviours. Each story must be small enough to test with a few Given/When/Then criteria.

A. About one story for each cluster of closely related sub-requirements, with 2 to 5 acceptance criteria each (roughly 50 to 70 stories in all)
B. About one story for each feature group, with many acceptance criteria each (roughly 17 to 25 stories in all)
C. About one story for each sub-requirement (roughly 120 stories in all)
D. Not yet defined
X. Other (please specify)

[Answer]: A

## Q4. [Product owner] Should the Hymn editor be a separate persona from the Operator?

Context: the design document says the Hymn editors are the same people as the Operators, but hymn saving and deleting are their own tasks with their own rules (confirmations, first save wins). A separate persona gives those stories a clear actor, even though the same person plays both roles.

A. Yes, keep the Hymn editor as its own persona, noting that the same people play both roles
B. No, fold hymn editing into the Operator persona
C. Not yet defined
X. Other (please specify)

[Answer]: B — fold hymn editing into the Operator persona (the same people play both roles); the hymn rules (confirmations, first save wins) stay in the acceptance criteria.

## Q5. [Product owner] Which developer-only work should get stories?

Context: the Developer maintains things operators never see: the "Check template" command, the one-time migration of the 116 hymns, the release process, and the spikes. Some are in the first version's requirements (FR8, FR13, NFR17, FR17), and a story gives each one an owner and acceptance criteria.

A. Stories for the Developer for every developer-only requirement: the template check (FR13), the hymn migration (FR8), the spikes and first slice (FR17), and the draft-release process (NFR17)
B. Stories for the template check and the hymn migration only
C. No Developer stories; keep them as requirements
D. Not yet defined
X. Other (please specify)

[Answer]: A

## Q6. [Product owner] What changes follow from the review and from the developer's real template? (follow-up)

Context: your change request fixed every review point and brought in the developer's real template (v3, Google Slides id `1Njchznjkd2toS1DqIvAStjP0w4gyMNq9S8j1q8v9U_E`, with the design document sections 2.3, F1, F6, V6 and 7.4 updated). This records the answers the stories, the requirements and the wireframes cite. You confirmed these answers yourself.

A. Use the answer below as written
X. Other (please specify)

[Answer]: X. Other (please specify): Part A: (1) The soft balanced rule is stated in the criteria for hymns and for text items as well as passages and announcements: among the valid break points pick the split with the smallest line-count difference, aiming at most 2 lines apart. (2) The passage criterion matches FR5.7, including the exception when keep-together rules force a larger difference. (3) The INVEST note says clusters of 1 to 4 sub-requirements with 2 to 5 criteria. (4) The assessment lists two personas, the Operator (including hymn editing) and the Developer. (5) Each traceability row holds one story id. (6) New criteria: a half-built PPT is never treated as last week's (FR12.5); a new hymn whose id was just taken by someone else is refused on save (FR7.4); schedule fields suggest names used before (FR10.2); the wizard runs again when saved keys are missing or replaced (FR1.4). Part B, the template v3 changes: (1) The Opening Passage has one text box only and no title: the passage and then its reference go in that box, the reference as the last paragraph placed toward the right, on the last slide only. The optional title and the separate reference box are removed everywhere. (2) The Offering is no longer a static slide. It is a passage slide type: the user enters any passage (the same field, semicolon rule and tooltip as a Bible Passage), the text goes under the fixed "OFFERING" heading with the reference line after it, and a long passage continues onto more copies of the slide. It is a new requirement and story, and the Normal preset includes it. (3) Placeholders use only Century Gothic. Arial appears only in static text, which is never measured, and Times New Roman is not used. Only TeX Gyre Adventor is bundled. "Check template" checks fonts in placeholder boxes only. (4) The `autoppt:` tags are not in the template's speaker notes yet. The developer adds them, and "Check template" reports missing ones. Do not approve the stage; the developer reviews the revised files before approving.

## Consolidated Summary Confirmation

Does this all look correct before I generate the artifact?

- Looks correct
- Request changes

[Answer]: Looks correct
