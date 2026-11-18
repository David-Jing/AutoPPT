# Intent Capture Questions

## Sources

- [desc] Initial description: "Refer to /Users/david/Documents/AutoPPT/aidlc/spaces/default/knowledge/documents/high-level-design.md"
- [scope] Workflow-selected scope: `mvp`.

## Q1. What your documents already answer

Context: the high-level design draft (`high-level-design.md`, v0.5) already covers several intent questions, so they are listed here once for you to confirm instead of being asked one by one.

- Business problem: the weekly Sunday worship deck for FCNABC's English congregation is built by a legacy Python console app driven by a hand-edited `.ini` file, and it is being rewritten (high-level-design.md §1, "Purpose": "The rewrite replaces the legacy Python console app, which was driven by a hand-edited `.ini` file.").
- Customer: operators, fewer than 10 people, who enter the week's details; the same people edit hymns; the developer maintains the template and setup files (high-level-design.md §4, "Users and roles").
- Success: "A non-technical operator builds the Sunday deck in under 10 minutes. Only the Chinese Monthly Scripture picture is added by hand." and verse, hymn and text slides "need no touch-up in most weeks" (high-level-design.md §3, goals G1 and G2).
- Left out of the first version: presenting or editing slides in the app, previewing slides, generating the Chinese pinyin picture, creating slide types or editing the template in the app, built-out Chinese support, restoring older hymn versions, merging concurrent edits, remote logging, Google Sheets input, code signing (high-level-design.md §3, "Non-goals (v1)").

A. All correct
B. Some need changing (say which and how)
X. Other (please specify)

[Answer]: A

## Q2. [Product owner] Who decides what is in scope and what comes first?

Context: the design document names the roles that use and maintain the app but not who sets priorities, and this decides who must approve the plan.

A. The developer decides alone
B. The developer decides, with church leadership approving
C. The developer decides after asking the operators
D. Not yet defined
X. Other (please specify)

[Answer]: A

## Q3. [Product owner] Beyond the 10-minute goal, how will you know the rewrite worked?

Context: the 10-minute and no-touch-up goals (Q1) are measurable; this asks whether anything else counts as success for this first version.

A. Operators stop using the legacy Python app completely
B. Generated decks match the reference decks with no manual fixes except the Chinese picture
C. Both A and B
D. Nothing beyond the goals already listed in Q1
E. Not yet defined
X. Other (please specify)

[Answer]: C — with two caveats. (1) The new app expands on the legacy app's features (Opening Passage, Spiritual Wisdom, Catechism, any number of hymns, schedule roles), so success is judged against the design document's rules, not only against the legacy output. (2) The reference PPTs contain manual mistakes (the missing Spiritual Wisdom slide in the 4 Oct PPT, hymn titles reading "(1/1)" across five slides), so generated slides should match their intended layout, not those mistakes. Terminology: say "PPT" or "slides", not "deck", in artifacts and in the UI.

## Q4. [Product owner] Why is the rewrite happening now?

Context: the design document says what is being replaced but not what triggered the rewrite; the answer shapes how success is judged.

A. The legacy app's hand-edited `.ini` file does not suit non-technical operators
B. The church needs installers that run on both Windows and macOS
C. Both A and B
D. Not yet defined
X. Other (please specify)

[Answer]: X. Other (please specify): A is the trigger. Windows and macOS support is the developer's own choice, not a church requirement: a self-contained app that opens on each user's computer makes distribution easy and avoids any cloud hosting. A formal installer is not required; any executable that can be opened on the host machine is enough.

## Q5. [Product owner] Who else cares about the outcome besides the operators, hymn editors and developer?

Context: the design document lists three roles; this checks for anyone else with a stake whose interests should be written down.

A. Church leadership (for example the pastor or worship committee)
B. The congregation, who see the finished slides on Sunday
C. Both A and B
D. None identified
E. Not yet defined
X. Other (please specify)

[Answer]: C

## Q6. [Product owner] How should progress be shared with the people who use the app?

Context: this sets the reporting cadence recorded in the stakeholder map.

A. No regular updates needed
B. A short note to the operators with each new version
C. A regular check-in with operators and leadership
D. Not yet defined
X. Other (please specify)

[Answer]: A

## Q7. [Product owner] Is the first version's boundary exactly the non-goals listed in Q1?

Context: the design document marks features as MVP or later, and this checks that nothing else should be left out of the first version.

A. Yes, exactly the non-goals in Q1
B. The non-goals in Q1, plus every feature the design document marks "later"
C. Not yet defined
X. Other (please specify)

[Answer]: B — features the design document marks "later" (L), such as the Chinese language selector, are not built in the first version. (Updated after scope definition: deleting a hymn moved from later into the first version, as a Must.)

## Q8. [Product owner] What are the measurable thresholds and constraints, and how do the reviewed stakeholder rows stand? (follow-up from the review)

Context: the review asked for measurable success checks, a legacy-retirement condition, delivery constraints kept out of the problem statement, and sources for two stakeholder rows. Your change request answered each point.

A. Use the answer below as written
X. Other (please specify)

[Answer]: X. Other (please specify): (1) No touch-up is needed on 4 consecutive Sundays after launch. The 10-minute goal is checked by the developer timing one operator's first full build. (2) Operators stop using the legacy Python app, checked after 4 consecutive Sundays on the new app. (3) Constraint: the app is a self-contained executable that opens on each user's computer, needs no formal installer and needs no cloud hosting. (4) Hymn editors can share hymns from inside the app, per the design document (high-level-design.md goal G3 and feature F3 "Save for everyone"). (5) The developer maintains the template, slide types, presets, measurement fonts and setup files and fixes reported errors, per the design document (high-level-design.md section 4 and decision D19). (6) The "only trigger" wording is dropped from the Initiative Trigger. (7) No legacy baseline is recorded; this is accepted as a risk.

## Consolidated Summary Confirmation

Does this all look correct before I generate the artifact?

- Looks correct
- Request changes

[Answer]: Looks correct
