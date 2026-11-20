# Team Allocation: AutoPPT Rewrite

This file says who builds each **Bolt** (one build pass over one Unit of work, ending in something that runs and a clear check). It stands alone: the order of the Bolts is in `bolt-plan.md`.

Inputs: `bolt-plan.md`, `delivery-planning-questions.md` (Q1, Q8), `team-practices.md`, and the RAID log.

## Who builds

There is one builder: the developer, working with the AI developer agent (`aidlc-developer-agent`). No team was formed, because this workflow's scope (mvp) skips Team Formation, and the RAID log (A-1) says the developer is comfortable with Java, Swing, Guice and the Google APIs. So there is no mob (a group working on one thing together) and no Program Board (a board that tracks several teams): there is only one team.

| Bolt | Unit | Builder | The developer's own part |
|---|---|---|---|
| B1 | U2 `u2-first-slice` | AI developer agent | Runs the demo with real services and approves the skeleton. Adds the template tags, supplies a real setup file, sets the Google client to In production, and decides when `AutoPPTData` joins the work. |
| B2 | U1 `u1-spikes` | AI developer agent records the results | Ran the spikes before Construction: S1 and S2 with the AI developer agent, and S3 (Google account, a Gmail sign-in that is not a test user) alone. |
| B3 | U3 `u3-layout` | AI developer agent | Looks at the made PPT for overflow. |
| B4 | U6 `u6-template-check` | AI developer agent | Runs Check template on the real template. |
| B5 | U4 `u4-service-builder` | AI developer agent | Runs the full demo, including a cut connection. |
| B6 | U5 `u5-hymns-and-sharing` | AI developer agent | Provides the legacy `HymnDatabase.db`, and runs the demo against the real `AutoPPTData`. |
| B7 | U7 `u7-packaging` | AI developer agent | Publishes the draft release by hand after checking the zips, and runs each zip on a clean machine. |

## Working rules

- **Approval:** gated for now. The developer approves each Unit's completion. After the skeleton (B1) is approved, the engine offers to switch to autonomous completion (Q8).
- **One Unit at a time.** Construction is unit-major and serial. The Units U3, U6 and U7 could be built at the same time by a larger team, but a solo developer builds them one after another.
- **Tests:** test first for the paginator and other pure logic in `core`, and implement then test for everything else. Network clients are tested against fakes (team.md).
- **Branches:** `main` in both repositories, with short-lived topic branches that squash-merge for bigger changes (team.md).

## Sources

- [Q1] One builder, the AI developer agent with the developer.
- [Q2] The spikes run by hand, S1 and S2 with the agent, S3 by the developer.
- [Q8] Gated approval for now.
- [scope] Team rules from `team-practices.md`.

## Assumptions & Open Questions

- [assumption] The developer is available to run the real-service demos and the manual checks in B1, B3, B5, B6 and B7. A Bolt cannot be approved without them.
- None otherwise.
