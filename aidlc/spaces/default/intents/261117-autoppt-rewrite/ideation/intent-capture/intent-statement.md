# Intent Statement: AutoPPT Rewrite

## Problem Statement

- The weekly Sunday worship PPT for FCNABC's English congregation is built by a legacy Python console app driven by a hand-edited `.ini` file. This app is being rewritten. [Q1]
- The hand-edited `.ini` file does not suit non-technical operators. [Q4]

## Target Customer

- Operators (fewer than 10 people) enter the week's details and make the slides. [Q1]
- Hymn editors, who are the same people, edit hymns and share them from inside the app (`high-level-design.md` goal G3 and feature F3 "Save for everyone"). [Q1] [Q8]
- The developer maintains the template, slide types, presets, measurement fonts and setup files and fixes reported errors (`high-level-design.md` section 4 and decision D19). [Q8]
- Church leadership and the congregation also have a stake in the outcome. [Q5]

## Success Metrics

- A non-technical operator builds the Sunday PPT in under 10 minutes. Only the Chinese Monthly Scripture picture is added by hand. [Q1]
- Verse, hymn and text slides need no touch-up on 4 consecutive Sundays after launch. [Q8]
- The 10-minute goal is checked by the developer timing one operator's first full build. [Q8]
- Operators stop using the legacy Python app, checked after 4 consecutive Sundays on the new app. [Q8]
- Generated PPTs match the reference PPTs' intended layout with no manual fixes except the Chinese picture. The developer judges whether a generated slide matches the intended layout, as the sole decision-maker. [Q3] [Q2]
- Success is judged against the design document's rules, not only against the legacy output, because the new app adds features such as Opening Passage, Spiritual Wisdom, Catechism, any number of hymns and schedule roles. [Q3]
- The reference PPTs' manual mistakes are not copied: the missing Spiritual Wisdom slide in the 4 Oct PPT, and hymn titles reading "(1/1)" across five slides. [Q3]

## Initiative Trigger

- The hand-edited `.ini` file of the legacy app does not suit non-technical operators. [Q4] [Q8]

## Initial Scope Signal

- Workflow-selected scope: `mvp`. [scope]
- Product boundary: the first version leaves out presenting or editing slides in the app, previewing slides, generating the Chinese pinyin picture, creating slide types or editing the template in the app, built-out Chinese support, restoring older hymn versions, merging concurrent edits, remote logging, Google Sheets input and code signing. [Q1]
- The first version also leaves out every feature the design document marks "later", such as the Chinese language selector. Deleting a hymn was moved from later into the first version during scope definition. [Q7]
- The developer alone decides scope and priority. [Q2]

## Constraints

- The app is a self-contained executable that opens on each user's computer. It needs no formal installer and no cloud hosting. [Q8]
- Supporting Windows and macOS is the developer's own choice, made to keep distribution easy, not a church requirement. [Q4]

## Accepted Risks

- No legacy baseline (such as how long the legacy process takes) is recorded, so the 10-minute target has nothing to compare against. [Q8]

## Assumptions & Open Questions

None.
