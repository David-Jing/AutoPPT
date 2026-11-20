# Evidence

## Inspected

- `aidlc/spaces/default/memory/org.md`: the five sections (Way of Working, Walking Skeleton, Testing Posture, Deployment, Code Style), used as suggested defaults.
- `aidlc/spaces/default/memory/team.md`: no affirmed content yet, so no re-run baseline.
- `aidlc/spaces/default/intents/261117-autoppt-rewrite/ideation/scope-definition/scope-document.md`: scope, sequencing (spikes, thin path, breadth, editors).
- `aidlc/spaces/default/intents/261117-autoppt-rewrite/ideation/scope-definition/intent-backlog.md`: proto-Units, including proto-Unit 1 as the thin end-to-end path.
- `aidlc/spaces/default/intents/261117-autoppt-rewrite/ideation/feasibility/constraint-register.md`: technical, organisational and data constraints (Java 21, Gradle 9.x, no installer, no cloud hosting, solo decision maker).
- `git rev-parse --short HEAD` in the workspace: commit `b572109` at final integration (the lead draft recorded `04c8a06`).
- The design document `aidlc/spaces/default/knowledge/documents/high-level-design.md` was not read directly by the earlier draft. Its facts (Java 21, Gradle 9.x, GitHub Actions runners, two repos AutoPPT and AutoPPTData) come from the task brief and from the constraint register, which cites the design. Treated as customer data.
- The other feasibility files were not read: `feasibility-assessment.md`, `raid-log.md`, `feasibility-questions.md`.
- No support-agent reviews ran (collaborators off); the lead worked alone.

## Inferred

- Solo-developer practices (self-review, no staging) follow from C-O1 and the org defaults.
- CI tag-triggered releases follow from F11's reading of the latest GitHub release, plus the three platform targets. The exact workflow is not given in the inputs.
- Greenfield: no build files exist to inspect, so tooling choices come from the interview, not from discovered configuration.

## Interview Decisions

- Q1: work on `main`, commit directly for small changes, short-lived topic branches with squash merge for bigger ones, tag releases `vMAJOR.MINOR.PATCH`. Integration branch is `main`.
- Q2: build the thin end-to-end slice first, as the scope says.
- Q3: test-first (TDD) for the paginator and other pure logic, test-after for the rest. Recorded as Methodology `custom`.
- Q4: 80% line-coverage floor on the `core` module only, enforced in the build; no floor on the Swing UI or the Google, ESV and shared-library adapters; manual check of one real PPT built in Google Drive before each release.
- Q5: automated build on push to `main`, tag-triggered zipped releases published as drafts first, checked, then published by hand; no staging; rollback by marking an earlier release latest or deleting the bad one.
- Q6: Spotless only, with google-java-format, enforced in the build. No Checkstyle.
- Q7: ALWAYS say "PPT" or "slides", never "deck". NEVER write keys, tokens or setup-file secrets to logs or support details. NEVER show GitHub, repo, commit, API or token in the UI.
- The consolidated summary was confirmed as "Looks correct".
- Clarification given with the approval (caveat resolved): AutoPPTData holds only hymn Markdown and YAML config, with no code, so the test and style practices apply to the AutoPPT repository only.
- Clarification given with the approval (caveat resolved): "core" is the Gradle module named in the design document, section 9 (modules core, google, esv, library and app), and it holds the pure layout and pagination logic.

## Unresolved Uncertainty

- The exact GitHub Actions workflow and the Spotless and coverage-tool configuration are not yet written.
- The design document was not read directly, so any practice it states that differs from the constraint register is unchecked.
