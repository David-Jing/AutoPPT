# Unit-of-Work Story Map: AutoPPT Rewrite

One row for each user story. A Unit is named on a row only when it implements part of that story. Cross-cutting stories name each Unit that adds to them.

## Story to Unit

| Story | Unit(s) | Directory |
|---|---|---|
| US1.1 | U2; U7 | u2-first-slice; u7-packaging |
| US1.2 | U2 | u2-first-slice |
| US1.3 | U2 | u2-first-slice |
| US2.1 | U2 | u2-first-slice |
| US2.2 | U2 | u2-first-slice |
| US2.3 | U2; U4 | u2-first-slice; u4-service-builder |
| US3.1 | U4; U3 | u4-service-builder; u3-layout |
| US3.2 | U4 | u4-service-builder |
| US3.3 | U4 | u4-service-builder |
| US3.4 | U4 | u4-service-builder |
| US3.5 | U2; U4 | u2-first-slice; u4-service-builder |
| US4.1 | U4 | u4-service-builder |
| US4.2 | U3; U4 | u3-layout; u4-service-builder |
| US5.1 | U3; U4 | u3-layout; u4-service-builder |
| US5.2 | U4 | u4-service-builder |
| US5.3 | U3 | u3-layout |
| US5.4 | U3 | u3-layout |
| US5.5 | U2; U4 | u2-first-slice; u4-service-builder |
| US5.6 | U2 | u2-first-slice |
| US5.7 | U3; U4 | u3-layout; u4-service-builder |
| US6.1 | U3 | u3-layout |
| US6.2 | U3 | u3-layout |
| US6.3 | U3 | u3-layout |
| US7.1 | U5 | u5-hymns-and-sharing |
| US7.2 | U5 | u5-hymns-and-sharing |
| US7.3 | U5 | u5-hymns-and-sharing |
| US7.4 | U5 | u5-hymns-and-sharing |
| US7.5 | U5 | u5-hymns-and-sharing |
| US7.6 | U2; U4; U5 | u2-first-slice; u4-service-builder; u5-hymns-and-sharing |
| US8.1 | U5 | u5-hymns-and-sharing |
| US9.1 | U4 | u4-service-builder |
| US9.2 | U3 | u3-layout |
| US9.3 | U3; U4 | u3-layout; u4-service-builder |
| US9.4 | U3; U4 | u3-layout; u4-service-builder |
| US9.5 | U3 | u3-layout |
| US10.1 | U3; U4 | u3-layout; u4-service-builder |
| US10.2 | U3 | u3-layout |
| US11.1 | U3; U4 | u3-layout; u4-service-builder |
| US12.1 | U2; U4 | u2-first-slice; u4-service-builder |
| US12.2 | U4 | u4-service-builder |
| US12.3 | U4 | u4-service-builder |
| US12.4 | U4 | u4-service-builder |
| US12.5 | U2; U4 | u2-first-slice; u4-service-builder |
| US12.6 | U3 | u3-layout |
| US12.7 | U4 | u4-service-builder |
| US13.1 | U2 | u2-first-slice |
| US13.2 | U2 | u2-first-slice |
| US13.3 | U6 | u6-template-check |
| US14.1 | U4 | u4-service-builder |
| US14.3 | U5 | u5-hymns-and-sharing |
| US14.2 | U4 | u4-service-builder |
| US15.1 | U4 | u4-service-builder |
| US15.2 | U2 | u2-first-slice |
| US15.3 | U2; U4 | u2-first-slice; u4-service-builder |
| US15.4 | U2; U4; U5; U7 | u2-first-slice; u4-service-builder; u5-hymns-and-sharing; u7-packaging |
| US16.1 | U7 | u7-packaging |
| US16.2 | U7 | u7-packaging |
| US17.1 | U1 | u1-spikes |
| US17.2 | U2 | u2-first-slice |
| US18.1 | U2; U4 | u2-first-slice; u4-service-builder |
| US18.2 | U7 | u7-packaging |
| US18.3 | U3; U4 | u3-layout; u4-service-builder |
| US18.4 | U2; U4; U5; U7 | u2-first-slice; u4-service-builder; u5-hymns-and-sharing; u7-packaging |
| US18.5 | U2 | u2-first-slice |
| US18.6 | U2; U4 | u2-first-slice; u4-service-builder |
| US18.7 | U2 | u2-first-slice |
| US18.8 | U7 | u7-packaging |
| US18.9 | U7 | u7-packaging |
| US18.10 | U2; U4; U5; U7 | u2-first-slice; u4-service-builder; u5-hymns-and-sharing; u7-packaging |

## Rows that span several Units

- US1.1: U2 builds the start-up screen and runs the "Checking for updates…" line against a fake `ReleaseCheckerPort`. U7 adds the real update check (`ReleaseChecker`).
- US3.1: U4 builds the main window and the service. U3 owns the date rule (`ServiceDates`) that gives the default service date.
- US2.3: U2 builds the wizard steps and the progress bar. U4 adds Replace setup file in Settings (AC2.3.3).
- US3.5: U2 holds the single-copy lock. U4 holds the one-run-at-a-time rule.
- US4.2, US9.3, US9.4, US10.1 and US11.1: U3 does the layout, and U4 does the editor.
- US5.1, US5.5 and US5.7: U3 does the layout and U4 does the editor. U2 reports the missing-passage error (US5.5).
- US7.6: U2 refreshes the library and checks the schema, U4 holds the Check for updates button, and U5 holds the newer-version badge on local edits.
- US12.1 and US12.5: U2 makes the first PPT and checks the template can be read. U4 completes the run.
- US15.3: U2 covers the setup-file and sign-in messages, and U4 covers the messages during a run.
- US15.4, US18.4 and US18.10: every Unit that adds screens or text (U2, U4, U5, U7) must follow the wording, keyboard and contrast rules.
- US18.1: U2 covers start-up speed and the library download. U4 covers the time to make slides.
- US18.3: the success measures are checked on the output of U3 and U4.
- US18.6: U2 covers `secrets.json` and data compatibility, and U4 covers the service file.

## Coverage Verification

- Every one of the 69 stories has a row and at least one Unit.
- Every Unit has rows: U1 1, U2 24, U3 18, U4 34, U5 11, U6 1, U7 9.
- The eight stories left as GAP in Domain Design now have a Unit: US17.1 U1; US17.2 and US18.7 U2; US16.1, US18.2, US18.8 and US18.9 U7; US18.3 U3 and U4.

## Sources

- [Q7] The story assignment, as answered in units-generation-questions.md.
- [scope] Story ids come from stories.md.

## Assumptions & Open Questions

- [assumption] Stories that name several Units are traced in each Unit's Functional Design for the acceptance criteria that Unit implements. The criteria split by Unit is noted in the Rows that span several Units section above.
- None otherwise.

The story ids are the 69 in stories.md.
