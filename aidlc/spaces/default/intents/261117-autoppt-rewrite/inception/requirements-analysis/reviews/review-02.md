## Review

**Verdict:** READY
**Reviewer:** aidlc-product-lead-agent
**Date:** 2026-11-18T04:50:19Z
**Iteration:** 1
**Verdict Dictated:** no

### Findings

| ID | Severity | Location | Finding | Required action | Status |
|---|---|---|---|---|---|
| R-01 | Major | aidlc/spaces/default/intents/261117-autoppt-rewrite/inception/requirements-analysis/requirements.md > FR12.3, FR12.4, FR12.5, FR1.6 | The text does not say when a run counts as stopped (FR12.5) and when it counts as finished with failed rows (FR12.3). FR1.6 says a lost connection ends the running action as Problem, which reads like a failed row, not a stopped run. A run that finishes with Problem rows seems to still move last week's PPT to the Trash (FR12.4), which could leave the operator with a PPT that has missing slides and no old one. | State exactly which failures stop the run (lost connection, Google refusing a request, quota) and which only fail one row. State whether last week's PPT is moved to the Trash when any row ended as Problem, and add a pass or fail check for it. | Resolved (reviewer) |
| R-02 | Major | aidlc/spaces/default/intents/261117-autoppt-rewrite/inception/requirements-analysis/requirements.md > NFR3 (and NFR1) | NFR3 says "without losing speed or failing", which has no threshold, so it cannot be passed or failed. The 2-minute target in NFR1 covers 60 slides only, and nothing sets a time for the 100-slide size that Q4 confirmed. The hymn library refresh at 500 hymns also has no time limit. | Give NFR3 a number: a make time for 100 slides (or say the 2-minute target scales and state how), and a time for loading or refreshing 500 hymns. Mark them provisional like NFR1 if they are not yet measured. | Resolved (reviewer) |
| R-03 | Minor | aidlc/spaces/default/intents/261117-autoppt-rewrite/inception/requirements-analysis/requirements.md > FR1.1 and FR1.4 | FR1.1 says every launch shows the three status lines in order, but FR1.4 skips "Getting ready…" on first start. The FR1.1 check fails on a first start. | Reword FR1.1 to say "in order, except as FR1.4 describes", or list the first-start sequence in FR1.1. | Resolved (reviewer) |
| R-04 | Minor | aidlc/spaces/default/intents/261117-autoppt-rewrite/inception/requirements-analysis/requirements.md > NFR2, FR1.2, FR1.4, FR16.3 | The 10-second start-up (Q3) holds only "when the connection works". Three steps run in sequence (the internet check and the update check have a 5-second timeout each, and the library refresh has none). It is not stated whether a slow or failing update check or library refresh counts against the 10 seconds. FR1.4 also says nothing about a library refresh that fails at start-up (for example a rejected shared-library token, or GitHub down) while the internet is up. | State what the 10 seconds includes, and state what the app shows and does when the start-up library refresh fails and a local copy exists. | Resolved (reviewer) |
| R-05 | Minor | aidlc/spaces/default/intents/261117-autoppt-rewrite/inception/requirements-analysis/requirements.md > FR5.7, FR6.2, FR9.3, FR12.7 | Several layout checks use words with no number: "balanced", "similar line counts", "similar" line lengths, "as few slides as possible", and "the developer judges the layout". The paginator is the main risk and is built test-first, so testers need a tolerance (for example a maximum difference in line count between slides). | Add a numeric tolerance to each, or say the tolerance is set by spike S1 and held in the layout configuration, as Open Questions already does for spacing. | Resolved (reviewer) |
| R-06 | Minor | aidlc/spaces/default/intents/261117-autoppt-rewrite/inception/requirements-analysis/requirements.md > FR12 (network and quota corner) | Nothing covers Google Slides or Drive rate limits (a 429 response), retries, or a full Drive. FR5.11 handles the ESV limits only. These can stop a 60-slide run and relate to the 2-minute target. | Add a requirement for what happens on a Google rate-limit or storage-full response (retry with a limit, or a plain message), with a check. | Resolved (reviewer) |
| R-07 | Minor | aidlc/spaces/default/intents/261117-autoppt-rewrite/inception/requirements-analysis/requirements.md > FR7.1, FR7.8, FR3.3 | Local hymn edits ("Only on this computer") have no stated life. It is not said whether they survive closing the app or "New service", or what happens when "Check for updates" brings a newer shared version of a hymn that has a local edit. This is two copies of one fact. | State where local edits are kept and what Check for updates does to a locally edited hymn, with a check. | Resolved (reviewer) |
| R-08 | Minor | aidlc/spaces/default/intents/261117-autoppt-rewrite/inception/requirements-analysis/requirements.md > FR7.6 | The hymn id comes from the title, with a number added on a clash. Titles in Chinese or with no Latin letters, punctuation, and ids that differ only by case (case-insensitive Windows and macOS file names) are not covered. | State the id rule for these cases (for example transliteration or a fallback id), and check a case-only clash. | Resolved (reviewer) |
| R-09 | Minor | aidlc/spaces/default/intents/261117-autoppt-rewrite/inception/requirements-analysis/requirements.md > FR3.7, NFR11 (concurrency corner) | Two copies of the app open on the same computer, writing the same service file and `secrets.json`, are not addressed. | Add a requirement (block a second copy, or say it is out of scope with a reason). | Resolved (reviewer) |
| R-10 | Minor | aidlc/spaces/default/intents/261117-autoppt-rewrite/inception/requirements-analysis/requirements.md > FR10.2, FR3.2 | "The Sunday after the service date" is unclear when the user picks a service date that is not a Sunday, and "today" has no stated time zone. | State the rule for a non-Sunday date (the next Sunday after it) and that dates use the computer's local time zone. | Resolved (reviewer) |
| R-11 | Minor | aidlc/spaces/default/intents/261117-autoppt-rewrite/inception/requirements-analysis/requirements.md > NFR18 | The check "a text search of artifacts" for the word "deck" will match this file, which quotes the word in its own header and in NFR18. | Limit the search to UI strings and generated PPT text, or exclude the rule's own statement. | Resolved (reviewer) |
| R-12 | Minor | aidlc/spaces/default/intents/261117-autoppt-rewrite/inception/requirements-analysis/requirements.md > FR9.3 | An announcement is split "at the last sentence that fits", which fills each slide as far as it goes. The check in the same requirement says the split must have the smallest line-count difference among valid break points. These two rules can give different splits. FR5.6 and FR5.7 have the same pairing but state that the keep-together rules decide the break points first. | Say in FR9.3 that the last-sentence-that-fits rule sets the valid break points and the smallest-difference rule picks among them. Alternatively, limit the balanced check to Supplications. | New |
| R-13 | Minor | aidlc/spaces/default/intents/261117-autoppt-rewrite/inception/requirements-analysis/requirements.md > NFR13 and NFR14 | These two have no pass or fail check, unlike the other requirements. | Add a check to each. For NFR13, the build fails below 80% core line coverage. For NFR14, the build fails on a Spotless violation. | New |
| R-14 | Minor | aidlc/spaces/default/intents/261117-autoppt-rewrite/inception/requirements-analysis/requirements.md > FR1.4 | When the library refresh fails and no cached copy exists (for example a first start that was interrupted in wizard step 4), the app has nothing to fall back on. The requirement is silent. | State what the user sees in that case, or record an assumption that the main window cannot open without a cached library. | New |
| R-15 | Minor | aidlc/spaces/default/intents/261117-autoppt-rewrite/inception/requirements-analysis/requirements.md > FR3.10 | After a crash the single-copy check might leave a stale marker, which would block every later launch with "AutoPPT is already open". The requirement does not cover this. | Add that a copy killed by a crash does not block the next launch. Add a check that kills the app and starts it again. | New |
| R-16 | Minor | aidlc/spaces/default/intents/261117-autoppt-rewrite/inception/requirements-analysis/requirements.md > NFR2 | The worst case of the stated timeouts is 5 seconds for the internet check, 5 for the version check and 5 for the library refresh. That is more than the 10-second target, even though each step times out as specified. | State that the 10 seconds applies when each step answers normally, or lower the individual timeouts. The check on a blocked library will otherwise fail by design. | New |

> R-01 Reviewer note: FR12.3, FR12.4, FR12.5 and FR1.6 now agree: stop causes, finish rule, Skipped rows, and Trash only after a finished run with no Problem rows. Each has a check.

> R-02 Reviewer note: NFR3 now carries 3.5 minutes for 100 slides, 5 seconds for a changed-file refresh and 30 seconds for 500 hymns, marked provisional. It is consistent with NFR1 (2 minutes for 60 slides) and FR7.8.

> R-03 Reviewer note: FR1.1 now excepts first start and points to FR1.4. The check covers both cases.

> R-04 Reviewer note: NFR2 and FR1.4 agree on the 10-second scope, the 5-second refresh timeout, the cached fallback and the message.

> R-05 Reviewer note: The soft "balanced" wording is the same in FR5.7, FR6.2, FR12.7 and FR9.3, with a testable smallest-difference rule. See the new finding on FR9.3 for one leftover tension.

> R-06 Reviewer note: FR12.8 gives the retry count and the Drive-full stop. It is consistent with FR12.3.

> R-07 Reviewer note: FR7.8 covers persistence, New service clearing and the newer-version badge, each with a check.

> R-08 Reviewer note: FR7.6 gives the slug rule, the random fallback and case-insensitive clashes, each with a check.

> R-09 Reviewer note: FR3.10 is added and matches traceability.json.

> R-10 Reviewer note: FR3.2 and FR10.2 agree on the next Sunday and the local time zone.

> R-11 Reviewer note: NFR18 limits the search to UI strings and generated PPT text.

### Summary

All eleven prior decisions are reflected consistently in requirements.md and traceability.json. The ids list matches exactly, including FR3.10 and FR12.8. No Critical or Major findings are open, and the remaining items are small wording gaps that do not stop engineering from starting.
