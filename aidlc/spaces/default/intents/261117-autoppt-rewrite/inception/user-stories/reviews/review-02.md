## Review

**Verdict:** READY
**Reviewer:** aidlc-product-lead-agent
**Date:** 2026-11-18T05:17:19Z
**Iteration:** 1
**Verdict Dictated:** no

### Findings

| ID | Severity | Location | Finding | Required action | Status |
|---|---|---|---|---|---|
| R-01 | Major | aidlc/spaces/default/intents/261117-autoppt-rewrite/inception/user-stories/stories.md > US6.1 (AC6.1.1 to AC6.1.3) and US9.2 (AC9.2.2) | The soft "balanced" rule is only partly carried into the criteria. FR6.2 requires that the split of a hymn into slides has the smallest line-count difference among its valid break points, and FR9.3 requires the same for the slides of one supplication item. US6.1 says only "split evenly" for a block taller than a slide and has no criterion on balancing a hymn's slides. AC9.2.2 (supplications) says only "as few slides as possible". Only AC9.2.1 (announcements) and AC5.3.3 (passages) state the rule. QA could not test the hymn or supplication case from these criteria. | Add a criterion to US6.1 and to US9.2 that, given a hymn (or a supplication item) with several valid break points, the chosen split has the smallest line-count difference, and that a split within 2 lines is chosen whenever one exists. | Resolved (reviewer) |
| R-02 | Minor | aidlc/spaces/default/intents/261117-autoppt-rewrite/inception/user-stories/stories.md > AC5.3.3 | FR5.7 allows a difference larger than 2 lines when the keep-together rules (whole verses, sentence boundaries) make 2 impossible. AC5.3.3 says "aiming for at most 2 lines apart" and does not state that exception, and "when the break points are valid" is vague. | Reword to match FR5.7: the split with the smallest line-count difference is chosen, a split within 2 lines is chosen when one exists, and a larger difference is accepted only when the keep-together rules force it. | Resolved (reviewer) |
| R-03 | Minor | aidlc/spaces/default/intents/261117-autoppt-rewrite/inception/user-stories/stories.md > INVEST Notes, Estimable and Small | The note says each story is a cluster of 2 to 4 sub-requirements with 2 to 4 criteria. Many stories cite one sub-requirement (for example US5.1, US5.5, US5.6, US6.3, US7.4, US9.3, US9.4, US10.2, US12.2 to US12.5), and the Q3 plan allowed 2 to 5 criteria. The note is not true as written. | Correct the note to say clusters of 1 to 4 sub-requirements, or remove the numbers. | Unresolved |
| R-04 | Minor | aidlc/spaces/default/intents/261117-autoppt-rewrite/inception/user-stories/user-stories-assessment.md > Rationale and Factors Considered | The assessment names three roles, including a separate Hymn editor, and says the story plan answers show Hymn editing folded into the Operator (Q4 answer B, followed in stories.md and personas.md). The documents disagree about the persona set. | Reword the assessment to two personas, Operator (including hymn editing) and Developer. | Resolved (reviewer) |
| R-05 | Minor | aidlc/spaces/default/intents/261117-autoppt-rewrite/inception/user-stories/traceability.json > coverage FR7.2 and NFR11 | The target field holds a comma-separated string ("US7.2, US7.3, US7.5", "US3.4, US18.6") while every other row holds one id. A checker that looks up each target in stories.md would not find these strings. | Use one id per row, or a list, consistently, so each target resolves to a story id. | Resolved (reviewer) |
| R-06 | Minor | aidlc/spaces/default/intents/261117-autoppt-rewrite/inception/user-stories/stories.md > US12.4, US7.2, US10.2, US1.1 | Four requirement details have no criterion: (1) FR12.5, a half-built PPT is never treated as last week's PPT. (2) FR7.4, a new hymn whose id already exists is refused on save. (3) FR10.2, schedule fields suggest names used before. (4) FR1.4, the wizard path when saved keys are missing or replaced (AC1.1.2 covers first start only). | Add a criterion for each, or fold it into the nearest existing criterion. | Resolved (reviewer) |
| R-07 | Minor | aidlc/spaces/default/intents/261117-autoppt-rewrite/inception/user-stories/stories.md > US10.1 (AC10.1.3) and US10.2 | AC10.1.3 (names suggested from earlier entries) tests FR10.2, but US10.1 lists only FR10.1 and FR10.3. FR10.2 is traced to US10.2, which has no criterion for the suggestion. | Move AC10.1.3 to US10.2, or add FR10.2 to the Requirements line of US10.1. | New |
| R-08 | Minor | aidlc/spaces/default/intents/261117-autoppt-rewrite/inception/user-stories/stories.md > US5.4 (AC5.4.2) and US5.2 | FR5.9 says the reference appears on the last slide only for any passage that spans several slides. No criterion tests this for a Bible Passage. AC5.1.3 covers the Opening Passage only, and AC5.7.3 covers the Offering only. | Add to AC5.4.2, or add a new criterion: for a Bible Passage over several slides, the reference is on the last slide only. | New |
| R-09 | Minor | aidlc/spaces/default/intents/261117-autoppt-rewrite/inception/user-stories/stories.md > AC9.5.1 | "The layout is not broken" has no observable pass or fail. | State the result, for example that no text is cut off or overflows the slide, and that no verified line is left to soft-wrap. | New |
| R-10 | Minor | aidlc/spaces/default/intents/261117-autoppt-rewrite/inception/user-stories/stories.md > AC12.6.1 | "I judge the layout" is a judgement rather than a checkable result. The requirement (FR12.7) puts it with the developer, so this is acceptable only if that is stated. | Add a measurable part, such as the FR5.7 and FR6.2 rules applied to the made PPT, and keep the developer's visual judgement as a stated extra. | New |

> R-01 Reviewer note: US6.1 now has AC6.1.4 and US9.2 has AC9.2.1 and AC9.2.3. Each says to pick the split with the smallest line-count difference among valid break points, aiming at most 2 lines apart.

> R-02 Reviewer note: AC5.3.3 now includes the larger difference allowed when keeping whole verses together makes 2 impossible. It matches FR5.7.

> R-03 Reviewer note: The INVEST note now says 1 to 4 sub-requirements with 2 to 5 criteria. The 1 to 4 part holds. The 2 to 5 part does not: US12.5, US12.6, US17.2, US18.2 and US18.10 each have a single criterion.

> R-04 Reviewer note: user-stories-assessment.md and personas.md both name two personas, the Operator (including hymn editing) and the Developer.

> R-05 Reviewer note: Every row in traceability.json has one story id as its target.

> R-06 Reviewer note: All four criteria exist: AC12.4.3 (FR12.5), AC7.4.4 (FR7.4), AC10.1.3 (FR10.2) and AC1.1.5 (FR1.4). AC10.1.3 sits in a story that does not cite FR10.2, which is raised below as a new finding.

### Summary

The template v3 changes are consistent across stories.md, traceability.json, requirements.md and wireframes S3a. This covers the Opening Passage with one text box and no title, the Offering as a passage slide (FR5.12 and US5.7, in the Normal preset), the fonts (Century Gothic in placeholders, TeX Gyre Adventor bundled, placeholder-only checking), and the missing `autoppt:` tags that Check template reports (AC13.1.3). Traceability covers every FR sub-requirement including FR5.12 and every NFR, with OK targets that exist. Story ids, actor, goal and benefit are present, and "deck" appears only in the rule AC18.10.1. R-01 is fixed. Only minor gaps remain, and engineering can start.
