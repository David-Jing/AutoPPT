## Review

**Verdict:** READY
**Reviewer:** aidlc-product-lead-agent
**Date:** 2026-11-17T03:35:52Z
**Iteration:** 1
**Verdict Dictated:** no

### Findings

| ID | Severity | Location | Finding | Required action | Status |
|---|---|---|---|---|---|
| R-01 | Major | aidlc/spaces/default/intents/261117-autoppt-rewrite/ideation/rough-mockups/wireframes.md > S2d, S4 accessibility notes | The notes say the offline banner "is announced as a status message" and the row list "is a status region announced as rows change". Q2 was answered A (keyboard-only use plus readable contrast), and the screen-reader option B was not chosen. These lines add a screen-reader requirement that nobody confirmed. | Remove the screen-reader wording, or ask the human at the gate whether to adopt Q2 option B. | New |
| R-02 | Minor | aidlc/spaces/default/intents/261117-autoppt-rewrite/ideation/rough-mockups/wireframes.md > Conventions (Accessibility) | "Readable contrast in both themes" has no pass/fail threshold, so QA cannot test it. The heading levels and focus-start rules in each screen note (for example "focus starts on Cancel") are also stated as fact without being confirmed in Q2. | Add a measurable contrast target if the human wants one (for example a minimum ratio). Mark the heading-level and focus rules as proposed, or confirm them at the gate. | New |
| R-03 | Minor | aidlc/spaces/default/intents/261117-autoppt-rewrite/ideation/rough-mockups/wireframes.md > S3h heading and Screen index (S3a to S3h) | The heading promises Static and Title-only rows. Static gets one line of text. Title-only gets no sketch. Supplications is named in S3d but has no distinct sketch, and the Q4 editor list includes title-only. | Add a short Title-only sketch, or state that it reuses the S3b title field. State whether Supplications matches the Announcements editor. | New |
| R-04 | Minor | aidlc/spaces/default/intents/261117-autoppt-rewrite/ideation/rough-mockups/wireframes.md > S4 | The progress view shows "7 of 10" beside six rows, and it has no state for a row that is being made right now. The four states match Q3, but the in-progress look is untestable as drawn. | Make the count match the sketch, and say how the current row is shown. | New |
| R-05 | Minor | aidlc/spaces/default/intents/261117-autoppt-rewrite/ideation/rough-mockups/wireframes.md > S1 and S2d | Q3 requires offline behaviour. The mockups cover it only for the main window. First start needs the network (sign-in, library download) and has no offline message. The S1 layout also shows both "ready" checks under Step 1 while the library download is Step 4. | Add a one-line offline state for S1, and tidy the S1 step order so the "Shared library is ready" check matches Step 4. | New |
| R-06 | Minor | aidlc/spaces/default/intents/261117-autoppt-rewrite/ideation/rough-mockups/wireframes.md > S6 | Settings shows "Your name", "Replace setup file" and "Sign out of Google" in addition to the delete-last-week checkbox and the theme choice. I could not confirm these controls against the confirmed answers or the scope documents. The design document is context only here. | Confirm at the gate that these controls come from design document F10. If they do not, remove them. | New |

### Summary

Coverage is complete against Q1 to Q4. The screens, states and one-line notes are all present. "Delete for everyone" appears in the first version (S2, S3c, S7, S8) and no language selector appears, both matching scope-document.md and intent-backlog.md. The wording uses "PPT" or "slides" and never "deck". The one item to weigh before approving is the screen-reader wording, which goes beyond the keyboard and contrast level chosen in Q2. The other gaps are small and do not block engineering.
