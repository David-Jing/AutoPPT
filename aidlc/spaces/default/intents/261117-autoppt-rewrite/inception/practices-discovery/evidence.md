# Evidence

## Inspected

- `aidlc/spaces/default/memory/org.md`: the five sections (Way of Working, Walking Skeleton, Testing Posture, Deployment, Code Style), used as suggested defaults.
- `aidlc/spaces/default/memory/team.md`: no affirmed content yet, so no re-run baseline.
- `aidlc/spaces/default/intents/261117-autoppt-rewrite/ideation/scope-definition/scope-document.md`: scope, sequencing (spikes, thin path, breadth, editors).
- `aidlc/spaces/default/intents/261117-autoppt-rewrite/ideation/scope-definition/intent-backlog.md`: proto-Units, including proto-Unit 1 as the thin end-to-end path.
- `aidlc/spaces/default/intents/261117-autoppt-rewrite/ideation/feasibility/constraint-register.md`: technical, organisational and data constraints (Java 21, Gradle 9.x, no installer, no cloud hosting, solo decision maker).
- `git rev-parse --short HEAD` in the workspace: commit `04c8a06` exists.
- The design document `aidlc/spaces/default/knowledge/documents/high-level-design.md` was not read directly. Its facts (Java 21, Gradle 9.x, GitHub Actions runners, two repos AutoPPT and AutoPPTData) come from the task brief and from the constraint register, which cites the design. Treated as customer data.
- The other feasibility files were not read: `feasibility-assessment.md`, `raid-log.md`, `feasibility-questions.md`.

## Inferred

- Solo-developer practices (trunk-based on `main`, self-review, no staging) follow from C-O1 and the org defaults, not from any stated team habit.
- Test-after is the org default. No input states a methodology.
- CI tag-triggered releases follow from F11's reading of the latest GitHub release, plus the three platform targets. The exact workflow is not given in the inputs.
- Formatter and linter names are examples, not discovered tooling. No build files exist to inspect (greenfield).

## Unresolved Uncertainty

- Whether the developer wants tdd or bdd for any part, such as the paginator.
- Whether to adopt the 80% coverage floor.
- Whether short-lived branches or direct commits to `main` are preferred, and whether the AutoPPTData repo follows the same rules.
- Which formatter and linter to use.
- Whether a manual pre-release check is wanted, and how rollback should work in practice.
- No hard constraints were stated by the human, so no Mandated or Forbidden rules were recorded.
