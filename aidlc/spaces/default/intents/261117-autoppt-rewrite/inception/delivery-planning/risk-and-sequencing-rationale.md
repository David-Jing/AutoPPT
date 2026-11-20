# Risk and Sequencing Rationale: AutoPPT Rewrite

This file explains why the **Bolts** (build passes, one for each Unit of work) are in the order in `bolt-plan.md`. It stands alone.

Inputs: `unit-of-work-dependency.md`, the RAID log, `delivery-planning-questions.md` (Q2, Q3, Q4, Q7), `team-practices.md`, and `requirements.md`.

## The method

The order is **risk first, then a thin end-to-end slice, then breadth**, as the scope document set (Sequencing, Q2). The thin slice is the **walking skeleton**: the smallest working version that runs the whole way through, built first to prove the pieces connect before the real features go in. No formal scoring model (such as WSJF, which divides value, urgency and risk reduction by size) was chosen. You were not asked to score the Units, and with one builder, seven Units and no fixed date, a ranking by judgment is enough. The order below is a qualitative one, and the reasons are given for each step.

## The order and why

| Step | Bolt | Reason |
|---|---|---|
| Before Construction | The spikes S1 to S3, run by hand | They test the largest unknowns (RAID R-2, R-3, R-4) with the least work. The team rule says the slice comes after the spikes (Q2). Their results set `layout.yaml` and decide whether the fallback sign-in is used. |
| 1 | B1 U2 the walking skeleton | Everything else needs the ports, the build, the sign-in and the library that this Unit creates. It proves the pieces connect (a real PPT from the real services) before layout and screens are built on top. It is also the Unit with the most real-service risk left after the spikes. |
| 2 | B2 U1 the spike record | It builds nothing. It is kept as a Unit so that US17.1 has a place, and U3 needs its results. It follows U2 because the engine takes a root Unit that can run first, and U1 cannot run. |
| 3 | B3 U3 layout | The paginator is the main technical risk in the design (requirements.md Intent Analysis), and every slide type depends on it. It comes right after the slice, while only the thin version exists, so a failure shows early and costs the least. It needs B2's results. |
| 4 | B4 U6 template check | It is small and depends only on U2. Running it before the full service builder means tag and font mistakes in the template show up before the full Make slides run is built on top of them. It removes a blocker cheaply (Q3). |
| 5 | B5 U4 service builder | The most value to operators: a whole service and the full Make slides run. It needs the layouts (U3). |
| 6 | B6 U5 hymns and sharing | It extends the editors and Settings that U4 builds, and `ReloadAllHymns` calls `ServiceBuilder`. It is a Must Have, but the app can make a PPT with hymns from the cache before the sharing is in place. |
| 7 | B7 U7 packaging | It is only useful once the other Units exist, because it packages and releases them. The update check depends only on U2 by code, so it could move earlier, but a release before the Units exist has nothing to release (Q3). |

## Risks and how the order handles each

| RAID id | Risk | How the order handles it |
|---|---|---|
| R-3 | Measured text differs from what Slides renders | Spike S1 first, then U3 right after the slice. |
| R-4 | The hanging indent with a superscript number renders unevenly | Spike S2 first. U3 uses the result. |
| R-2 | Google blocks sign-in or tokens expire | Spike S3 first. U2 uses the primary sign-in and switches to the fallback if S3 fails (accepted rework risk, Units Generation). |
| R-1, R-5, R-7 | Shared ESV key revoked, keys stored unencrypted, a leaked setup file stays usable | Accepted in earlier stages. U2 builds `SecretStore` and `Redactor`, so the protections exist from the first Bolt. |
| R-8 | The Intel macOS build ends in August 2027 | Affects only B7, and only later. |
| New | The `autoppt:` template tags are missing | The developer adds them. U6 (B4) reports every missing one. Until then B1's real run cannot find a slide, so the developer adds at least the one tag the slice needs first. |
| New | The engine builds a different first Unit | Checked when Construction starts. See `bolt-plan.md` Assumptions. |

## Dependency check

The order follows the graph in `unit-of-work-dependency.md`. Every Bolt comes after the Bolts of the Units it depends on:

- B1 U2: no dependency.
- B2 U1: no dependency.
- B3 U3: U1 (B2) and U2 (B1), both earlier.
- B4 U6: U2 (B1), earlier.
- B5 U4: U2 (B1) and U3 (B3), both earlier.
- B6 U5: U2 (B1) and U4 (B5), both earlier.
- B7 U7: U2 (B1), earlier.

There is no departure from topological order.

## Sources

- [Q2] The spikes before Construction, then U2 first.
- [Q3] U3, U6, U4, U5, U7 and the reason for each.
- [Q4] One Bolt for each Unit.
- [Q7] The paginator and text measurement are the main worry, so S1 to S3 run first and U3 follows the slice.
- [scope] Risk ids come from the RAID log.

## Assumptions & Open Questions

- [assumption] No formal scoring model (such as WSJF) is used. Q1 of the stage file asks about it, and this plan did not ask it as a separate question, because a qualitative order was enough for one builder. Tell me at the gate if you want scores added.
- [assumption] The developer adds at least the tags the slice needs to the template before B1's real run, and the rest before B4.
- None otherwise.
