# User Stories: AutoPPT Rewrite

Inputs: `requirements.md` (the FR and NFR ids cited below), `team-practices.md`, `personas.md` and `user-stories-questions.md` (Q1 to Q6). Stories follow "As a [persona], I want [goal], so that [benefit]" with Given/When/Then criteria, grouped by the requirement groups (Q2) and sized as clusters of closely related sub-requirements (Q3). Hymn editing is part of the Operator (Q4). Developer-only work has its own stories (Q5). Every story is a Must Have (scope-document.md; Q1). INVEST notes are in the last section. Terms: "PPT" and "slides" name the output.

## Group 1: Start-up and internet connection (FR1)

### US1.1 Start-up screen
As an Operator, I want a start-up screen that shows the app name, the version and what the app is doing, so that I know the app is working when it opens. Priority: Must Have. Requirements: FR1.1, FR1.4, FR1.5.
- AC1.1.1 Given the app has been set up, When I launch it, Then one status line at a time shows "Checking your internet connection…", then "Checking for updates…", then "Getting ready…", and the main window opens.
- AC1.1.2 Given this is my first start, When I launch the app, Then only the first two status lines show, the shared library loads in the last wizard step, and the wizard opens.
- AC1.1.3 Given the shared library cannot be refreshed but a cached copy exists, When the app starts, Then it uses the cached copy, shows "Couldn't check for hymn updates", and opens the main window.
- AC1.1.4 Given the library cannot be refreshed and no cached copy exists, When the app starts, Then the main window does not open and I see "Couldn't get the shared hymn library. Check your connection and choose Try again."
- AC1.1.5 Given the saved keys are missing or were replaced, When I launch the app, Then the wizard runs again from step 1, only the connection and the app version are checked first, and "Getting ready…" is skipped.

### US1.2 No connection at start-up
As an Operator, I want a clear message and a way to retry when there is no internet at start-up, so that I am never left with a blank or broken window. Priority: Must Have. Requirements: FR1.2, FR1.3.
- AC1.2.1 Given the network is blocked or behind a captive portal, When the app starts, Then within 5 seconds it stops and shows "You're not connected to the internet. Connect, then choose Try again." with Try again and Quit, and nothing else opens.
- AC1.2.2 Given that message is showing, When the connection returns, Then the app re-checks by itself within 5 seconds and carries on without my clicking.
- AC1.2.3 Given that message is showing, When I press Escape, Then nothing happens, and Try again is the default button.

### US1.3 Connection lost while the app is open
As an Operator, I want my typed work kept when the connection drops, so that I never lose what I entered. Priority: Must Have. Requirements: FR1.6, FR1.7.
- AC1.3.1 Given the main window or the wizard is open, When the connection is lost, Then the window is covered by the same message with Try again and Quit, Escape does nothing, and nothing behind the cover takes input.
- AC1.3.2 Given the cover is showing, When the connection returns, Then the cover closes by itself, my typed work is still there, and the wizard is on the same step with what I typed.
- AC1.3.3 Given a hymn save is running, When the connection is lost, Then it ends as Problem with "The internet connection was lost".

## Group 2: First-start wizard (FR2)

### US2.1 Set up from the setup file
As an Operator, I want to drop in the setup file I was given, so that the app has what it needs without my typing keys. Priority: Must Have. Requirements: FR2.1, FR2.3, FR2.4, FR2.5.
- AC2.1.1 Given this is my first start, When the app opens, Then the wizard shows step 1 of 4 and nothing else, and on every later start the main window opens directly.
- AC2.1.2 Given I drop in or browse to a valid setup file, When it is accepted, Then Next is enabled and the app offers "Delete the setup file now".
- AC2.1.3 Given a setup file whose scripture key or shared-library token is wrong, When I add it, Then the app names the part that failed in plain words and Next stays disabled.
- AC2.1.4 Given the file is accepted, When setup finishes, Then the keys are in `secrets.json` in the app-data folder, readable only by me.

### US2.2 Enter my name and sign in
As an Operator, I want to give my name and sign in with Google, so that slides are made in my own Drive and my hymn saves carry my name. Priority: Must Have. Requirements: FR2.6, FR2.7.
- AC2.2.1 Given I am on step 2, When the name field is empty, Then Next is disabled, and when I type a name, Then Next is enabled.
- AC2.2.2 Given I am on step 3, When I choose "Sign in with Google", Then my browser opens for sign-in and, after I sign in, the app shows I am signed in and enables Next.
- AC2.2.3 Given the Google client was disabled or deleted, When I sign in, Then I see "This setup file doesn't work with Google sign-in. Ask the developer for a new setup file."

### US2.3 Step through the wizard
As an Operator, I want one step per screen with Back and Next, so that I am never faced with all the setup at once. Priority: Must Have. Requirements: FR2.2, FR2.8, FR2.9.
- AC2.3.1 Given I am in the wizard, When I move between steps, Then each step is its own screen with Back, Next and a "Step n of 4" marker (setup file, your name, sign in with Google, shared library ready).
- AC2.3.2 Given I am on step 4, When the shared library is downloading, Then a progress bar shows and Finish is disabled until it is done, then Finish opens the main window.
- AC2.3.3 Given setup is done, When I replace the setup file from Settings, Then the new file goes through the step 1 checks.

## Group 3: Service builder (FR3)

### US3.1 Start a service
As an Operator, I want the date and an order to fill in the usual slides, so that I start from what the church normally does. Priority: Must Have. Requirements: FR3.2, FR3.3.
- AC3.1.1 Given today is a Sunday, When I start a service, Then the date is today, and on any other day it is the next Sunday, using my computer's local time zone.
- AC3.1.2 Given I choose the Normal or the Holy Communion order, When I confirm, Then the slide list fills with that order's default slides, and the Normal order includes the Offering.
- AC3.1.3 Given I choose "New service", When it starts, Then the settings are empty and nothing is copied from earlier services or PPTs.

### US3.2 Arrange the slide list
As an Operator, I want to reorder, add and remove slides, so that the PPT matches this week's service. Priority: Must Have. Requirements: FR3.1, FR3.4, FR3.5.
- AC3.2.1 Given the main window is open, When I look at it, Then the slides are listed in generation order on the left, the selected slide's settings are on the right, and the top bar says "Hi [name]".
- AC3.2.2 Given a slide is selected, When I drag its handle or use Alt+Up or Alt+Down, Then it moves.
- AC3.2.3 Given I choose "+ Add slide", When I pick a type, Then it is added, the same type can be added again, and Remove takes a row out of this service only.

### US3.3 See what is wrong with a row
As an Operator, I want each row to tell me in plain words when it cannot be made, so that I fix it before making slides. Priority: Must Have. Requirements: FR3.6, FR3.8.
- AC3.3.1 Given a hymn row has no hymn chosen, When I look at the list, Then the row shows a warning with "Choose a hymn".
- AC3.3.2 Given an empty service or an empty hymn library, When I look at the screen, Then I see "This service has no slides yet." or "No hymns yet" with a hint for what to do next.

### US3.4 Never lose typed work
As an Operator, I want my service saved as I type, so that closing the app loses nothing. Priority: Must Have. Requirements: FR3.7, NFR11.
- AC3.4.1 Given I have typed into a row, When I close and reopen the app, Then my service is as I left it.
- AC3.4.2 Given the app is killed while a save is in progress, When I reopen it, Then the previous good service file is intact.

### US3.5 Run one copy and one run at a time
As an Operator, I want the app to stop me making two PPTs at once or opening two copies, so that nothing is overwritten. Priority: Must Have. Requirements: FR3.9, FR3.10.
- AC3.5.1 Given a run is in progress, When I click Make slides again, Then nothing happens, because the button is disabled.
- AC3.5.2 Given the app is already open, When I launch a second copy, Then it shows "AutoPPT is already open" and exits.
- AC3.5.3 Given the app was killed by a crash, When I launch it again, Then it starts normally, because the lock is released when the process ends.

## Group 4: Slide types (FR4)

### US4.1 Choose from the available slide types
As an Operator, I want every kind of slide the church uses available to add, so that I can build the whole service. Priority: Must Have. Requirements: FR4.1, FR4.2, FR4.4.
- AC4.1.1 Given I choose "+ Add slide", When I open the list, Then I can add Opening Passage, Offering, Bible Passage, Chinese Monthly Scripture, Hymn, Announcements, Supplications, Catechism, Spiritual Wisdom, Header, Sermon Header, Next Week's Schedule, Static and Title-only slides.
- AC4.1.2 Given I add a Static slide (Lord's Prayer, Creed, Holy Communion, Doxology or Benediction), When I select it, Then it says the slide is copied as it is and has no settings, and the Offering is not among the static slides.
- AC4.1.3 Given I start a new service, When I look at the next-week rows, Then they are empty and I enter them like any other row.

### US4.2 Headers with the date
As an Operator, I want header slides with the date filled in correctly, so that I do not type the date or its ending. Priority: Must Have. Requirements: FR4.3.
- AC4.2.1 Given a service on September 27, When the PPT is made, Then the header reads "September 27th, 2026" with "th" raised.
- AC4.2.2 Given service days 1, 2, 3, 4, 11, 12, 13, 21, 22, 23 and 31, When headers are made, Then the endings are st, nd, rd, th, th, th, th, st, nd, rd and st.
- AC4.2.3 Given a Sermon Header, When I enter a title and a speaker, Then both appear on the slide.

## Group 5: Bible passages (FR5)

### US5.1 Opening Passage
As an Operator, I want an opening passage with its reference placed after the text, so that the first slide matches the church's opening. Priority: Must Have. Requirements: FR5.1, FR5.9.
- AC5.1.1 Given I add an Opening Passage row, When I look at its settings, Then there is a passage field and no title field.
- AC5.1.2 Given I enter John 8:12, When the PPT is made, Then the one text box holds the passage and then the reference "—John 8:12" as its last paragraph, placed toward the right, and the slide has no separate reference box.
- AC5.1.3 Given a long passage that needs several slides, When the PPT is made, Then the reference is the last paragraph of the last slide only and the other slides end with the passage.

### US5.2 Add a Bible passage
As an Operator, I want to type a reference and get the passage text, so that I never copy scripture by hand. Priority: Must Have. Requirements: FR5.2, FR5.3, FR5.4.
- AC5.2.1 Given I enter "Psalm 23; Romans 8:28", When the PPT is made, Then two passages are fetched, each with its canonical reference, and each starts on a new slide.
- AC5.2.2 Given I press the `?` help button next to the passage field with the mouse or the keyboard, When the pop-up opens, Then it shows what works ("Genesis 1:1-10", "John 3:16, 18", "Psalm 23; Romans 8:28") and what does not (Chinese book names, other translations).
- AC5.2.3 Given I switch on "show the reference after the passage", When the PPT is made, Then the reference follows the passage, and verse numbers are on and headings and footnotes are off.

### US5.3 Passages that fit the slides
As an Operator, I want long passages to flow across slides without cutting a verse, so that I do not touch up slides. Priority: Must Have. Requirements: FR5.5, FR5.6, FR5.7.
- AC5.3.1 Given a passage with poetry line breaks such as Psalm 23, When the PPT is made, Then each verse is one paragraph with a gap between verses.
- AC5.3.2 Given a verse that does not fit on the slide, When the PPT is made, Then it starts on the next slide, and it is split only if it alone is taller than a slide, and then at a line boundary.
- AC5.3.3 Given a passage over several slides, When the break points are valid, Then the app aims for slides that differ by at most 2 lines, a larger difference is allowed when the keep-together rules (whole verses) make 2 impossible, and the split with the smallest line-count difference among the valid break points is chosen; the title repeats on each slide.

### US5.4 Verse numbers and references
As an Operator, I want raised verse numbers and a right-placed reference, so that the passage looks like the church's slides. Priority: Must Have. Requirements: FR5.8, FR5.9.
- AC5.4.1 Given a passage with verse numbers, When the PPT is made, Then the numbers are raised and continuation lines line up with the start of the first line's text.
- AC5.4.2 Given the reference is shown after the passage, When the PPT is made, Then it follows the last verse and is placed toward the right, and a Bible Passage over several slides shows its reference on the last slide only (FR5.9).

### US5.5 Passages the app cannot find
As an Operator, I want a plain message when a reference is wrong, so that I can fix a typo before making slides. Priority: Must Have. Requirements: FR5.10.
- AC5.5.1 Given I enter "Jhon 3:16", When the app looks it up, Then the row shows "We couldn't find 'Jhon 3:16'" and is left out of the run.
- AC5.5.2 Given the passage field is empty, When I look at the row, Then it shows the same kind of warning.

### US5.6 Stay within the scripture service's limits
As an Operator, I want the app to stay within the scripture service's limits, so that one busy day does not stop other rows. Priority: Must Have. Requirements: FR5.11.
- AC5.6.1 Given I ask for more than 500 verses in one query, When I make slides, Then that row shows a plain reason and the run continues.
- AC5.6.2 Given passages were fetched, When I check the computer, Then no passage text is stored on disk.

### US5.7 Offering with a passage
As an Operator, I want the Offering slide to carry a passage under its fixed heading, so that the offering has its reading. Priority: Must Have. Requirements: FR5.12, FR5.9, FR5.3, FR3.3.
- AC5.7.1 Given I add an Offering row, When I look at its settings, Then I get the same passage field, the same semicolon rule and the same `?` help button as a Bible Passage.
- AC5.7.2 Given I enter a passage, When the PPT is made, Then the text is in the text box under the fixed "OFFERING" heading, in one font size, with the reference line after it.
- AC5.7.3 Given a long passage, When the PPT is made, Then it continues onto more copies of the Offering slide and the reference is on the last one only.
- AC5.7.4 Given I choose the Normal order, When the slide list fills, Then it includes an Offering row.

## Group 6: Hymn display (FR6)

### US6.1 Hymn blocks and lines
As an Operator, I want hymn lyrics laid out in whole blocks with even lines, so that hymn slides need no touch-up. Priority: Must Have. Requirements: FR6.1, FR6.2, FR6.3, FR5.7.
- AC6.1.1 Given blocks that share a slide, When the PPT is made, Then a gap separates them, a block that does not fit moves to the next slide, and a block taller than a slide is split evenly.
- AC6.1.2 Given a long lyric line, When the PPT is made, Then it splits at a comma, semicolon, exclamation mark or full stop between 40% and 80% of its length, otherwise at the space nearest the middle, and no text is left to soft-wrap.
- AC6.1.3 Given a repeated block, When the PPT is made, Then it collapses to "(x2)" and adjacent short blocks merge when they fit.
- AC6.1.4 Given a hymn that needs several slides, When the break points between whole blocks are valid, Then the app aims for slides that differ by at most 2 lines, a larger difference is allowed when keeping whole hymn blocks together makes 2 impossible, and the split with the smallest line-count difference among the valid break points is chosen.

### US6.2 Hymn titles and backgrounds
As an Operator, I want correct slide counts in hymn titles and varied backgrounds, so that the hymn section looks right. Priority: Must Have. Requirements: FR6.4, FR6.5, FR6.7.
- AC6.2.1 Given a five-slide hymn, When the PPT is made, Then the slide titles read "TITLE (1/5)" to "TITLE (5/5)", and a long title uses a second size or the body moves down.
- AC6.2.2 Given a service with more hymns than backgrounds, When the PPT is made, Then each hymn has one background shared by its slides, and none repeats until every one has been used.
- AC6.2.3 Given any hymn, When the PPT is made, Then no CCLI number, copyright line, song credit or credits slide appears.

### US6.3 Hymn order of blocks
As an Operator, I want a hymn's saved order of verses and choruses followed, so that it is sung in the right order. Priority: Must Have. Requirements: FR6.6.
- AC6.3.1 Given a hymn with an arrangement, When the PPT is made, Then its blocks appear in that order.
- AC6.3.2 Given a hymn with no arrangement, When the PPT is made, Then its blocks appear in file order.

## Group 7: Hymn editing and sharing (FR7)

### US7.1 Choose and edit a hymn
As an Operator, I want to find a hymn and change its lyrics for this service, so that the slides show what we sing. Priority: Must Have. Requirements: FR7.1, FR7.10.
- AC7.1.1 Given the hymn dropdown, When I search by title, other title or first line, Then I find the hymn and its lyrics fill the editor.
- AC7.1.2 Given I edit the lyrics, When I look at the hymn, Then it shows "Only on this computer".
- AC7.1.3 Given one malformed hymn file among good ones, When the library loads, Then that hymn is skipped and reported and every valid hymn still loads.

### US7.2 Save a hymn for everyone
As an Operator, I want to share my hymn change with everyone, so that nobody retypes it. Priority: Must Have. Requirements: FR7.2, FR7.3, FR7.4.
- AC7.2.1 Given I changed a hymn, When I choose "Save for everyone", Then a confirmation names the hymn and the effect, Cancel is the default button, Escape cancels, and Cancel changes nothing.
- AC7.2.2 Given I confirm, When the hymn is saved, Then it is written to the shared library with a message such as "Update hymn: Love Incarnate (by Grace)".
- AC7.2.3 Given someone else saved the hymn first, When I confirm, Then the save is refused with "Someone else saved this hymn first. Choose Check for updates, then make your change again." and my typed text stays in the row.

### US7.3 Discard my changes
As an Operator, I want to undo my local hymn edits, so that I can start again from the shared version. Priority: Must Have. Requirements: FR7.5, FR7.2.
- AC7.3.1 Given a locally edited hymn, When I choose "Discard my changes", Then a confirmation names the hymn and the effect, and Cancel changes nothing.
- AC7.3.2 Given I confirm, When the hymn returns to the shared version, Then only my unsaved local edits are lost and older versions cannot be restored.

### US7.4 Add a new hymn
As an Operator, I want to add a hymn from pasted lyrics, so that new songs join the library. Priority: Must Have. Requirements: FR7.6.
- AC7.4.1 Given I choose "New hymn…" and paste lyrics, When I confirm, Then the lyrics split into blocks on blank lines and `[Chorus]`-style headers.
- AC7.4.2 Given the title "Love Incarnate", When the id is made, Then it is `love-incarnate`, and a second hymn with that title gets `love-incarnate-2`.
- AC7.4.3 Given a title with no usable letters such as a Chinese title, When the id is made, Then it is "hymn-" plus a short random id, and a title that differs only by case still counts as a clash.
- AC7.4.4 Given I made a new hymn whose id was just taken by someone else, When I choose "Save for everyone" and confirm, Then the save is refused with "Someone else saved this hymn first. Choose Check for updates, then make your change again." and my typed text stays in the row.

### US7.5 Delete a hymn for everyone
As an Operator, I want to remove a hymn nobody sings, so that the library stays tidy. Priority: Must Have. Requirements: FR7.7, FR7.2.
- AC7.5.1 Given a hymn, When I choose "Delete for everyone", Then a confirmation asks "Delete 'Love Incarnate' for everyone?", Cancel is the default button, Escape cancels, and Cancel changes nothing.
- AC7.5.2 Given I confirm, When nobody changed the hymn first, Then it is removed from the shared library, and users cannot undo it.
- AC7.5.3 Given someone changed the hymn first, When I confirm, Then the delete is refused with the same "Check for updates" message, and a service row already using the hymn keeps its lyrics.

### US7.6 Keep the library fresh
As an Operator, I want my hymn list to update, so that I see what others saved. Priority: Must Have. Requirements: FR7.8, FR7.9.
- AC7.6.1 Given a hymn changed elsewhere, When I choose "Check for updates", Then I see the newer version, and a refresh of changed files completes within 5 seconds.
- AC7.6.2 Given a locally edited hymn with a newer shared version, When I check for updates, Then my edit is kept and a "newer shared version available" badge appears, and local edits survive closing the app until "New service".
- AC7.6.3 Given a file with a higher `schemaVersion` than the app knows, When it loads, Then the app refuses it and tells me to update.

## Group 8: Hymn migration (FR8)

### US8.1 Migrate the legacy hymns
As a Developer, I want a one-time tool that converts the 116 legacy hymns, so that operators have their hymns on day one. Priority: Must Have. Requirements: FR8.1, FR8.2.
- AC8.1.1 Given the legacy `HymnDatabase.db`, When I run the tool, Then it creates 116 hymn files, and a spot check of five hymns matches the database.
- AC8.1.2 Given one row that cannot be converted, When I run the tool, Then it lists that row and the others still convert.

## Group 9: Text blocks (FR9)

### US9.1 Write announcements and supplications
As an Operator, I want to write each announcement or supplication as its own block with simple formatting, so that I do not use another tool. Priority: Must Have. Requirements: FR9.1, FR9.2.
- AC9.1.1 Given the editor, When I use the buttons, Then I can add, remove and reorder blocks and use bold, italic, bullet list, numbered list and link.
- AC9.1.2 Given I paste something the app cannot render, such as a table, When I save, Then it shows as plain text with a warning, and underline is not offered.

### US9.2 Text that continues across slides
As an Operator, I want long announcements to continue on another slide sensibly, so that no sentence is cut. Priority: Must Have. Requirements: FR9.3, FR9.6, FR5.7.
- AC9.2.1 Given a long announcement, When the PPT is made, Then it continues on another slide with the same title, split at a sentence boundary and otherwise at a word, choosing the split with the smallest line-count difference among valid break points.
- AC9.2.2 Given supplications, When the PPT is made, Then they are numbered items on as few slides as possible, and lists use the slides' own bullets and numbering.
- AC9.2.3 Given text that needs several slides, When the sentence boundaries give valid break points, Then the app aims for slides that differ by at most 2 lines, a larger difference is allowed when keeping sentences together makes 2 impossible, and the split with the smallest line-count difference among the valid break points is chosen.

### US9.3 Catechism
As an Operator, I want to write the catechism title and answer myself, so that any catechism can be used. Priority: Must Have. Requirements: FR9.4.
- AC9.3.1 Given a title and a body, When the PPT is made, Then the catechism slide shows both, with one row for this week and one for next week.
- AC9.3.2 Given a long body, When the PPT is made, Then it continues on the next slide.

### US9.4 Spiritual Wisdom
As an Operator, I want a quote slide with its source on the right, so that it matches past slides. Priority: Must Have. Requirements: FR9.5.
- AC9.4.1 Given a quote of several paragraphs, When the PPT is made, Then paragraphs are separated by a gap and continue onto more slides when needed.
- AC9.4.2 Given a source line, When the PPT is made, Then it follows the last paragraph on the last slide, starts with "— " and sits toward the right.

### US9.5 Robust text and nothing stored centrally
As an Operator, I want odd text handled, so that a long word or an emoji never breaks a slide. Priority: Must Have. Requirements: FR9.7, FR9.8.
- AC9.5.1 Given text with a very long unbroken word, emoji, combining marks or right-to-left text, When the PPT is made, Then no text is cut off or overflows the slide, and no line is left to soft-wrap.
- AC9.5.2 Given announcements or catechism, When I make slides, Then no network call is made except to Slides and Drive.

## Group 10: Next Week's Schedule (FR10)

### US10.1 Fill in next week's roles
As an Operator, I want a name field for every role in the schedule, so that the table is complete. Priority: Must Have. Requirements: FR10.1, FR10.3.
- AC10.1.1 Given a template with a new role, When I open the schedule editor, Then a new field appears for it with no code change.
- AC10.1.2 Given a role I leave empty, When the PPT is made, Then it prints "N/A" and the cell formatting is kept.

### US10.2 Schedule date
As an Operator, I want next week's date worked out and the names I used before suggested, so that I type less. Priority: Must Have. Requirements: FR10.2.
- AC10.2.1 Given a Sunday service date, When the PPT is made, Then the schedule date is the following Sunday with the raised ending.
- AC10.2.2 Given a service date that is not a Sunday, When the PPT is made, Then the schedule date is the next Sunday after it, in my computer's local time zone.
- AC10.2.3 Given I have filled in names before, When I click a role field, Then it suggests the names used before.

## Group 11: Chinese Monthly Scripture (FR11)

### US11.1 Chinese Monthly Scripture reminder
As an Operator, I want the slide made with its title and a reminder to add the picture, so that I remember the one manual step. Priority: Must Have. Requirements: FR11.1, FR11.2.
- AC11.1.1 Given the row, When I select it, Then I can set only a title (default 本月金句) and I read that the body is empty and I add the picture in Google Slides.
- AC11.1.2 Given the PPT is made, When the run ends, Then the summary says "To do in Google Slides: add the Chinese verse picture to slide N" and a reminder is in that slide's speaker notes.

## Group 12: Make slides (FR12)

### US12.1 Make the PPT
As an Operator, I want one button to build the PPT from the church template, so that I do not assemble slides by hand. Priority: Must Have. Requirements: FR12.1, FR12.2.
- AC12.1.1 Given my service, When I choose Make slides, Then a new PPT is built from the template in my Drive and opens in my default browser, and I can copy its link.
- AC12.1.2 Given the run is done, When I open the PPT, Then it has only the service slides, in list order, with no leftover template slides and no speaker notes except the reminder.

### US12.2 See progress and results
As an Operator, I want to see how each row went, so that I know what to fix. Priority: Must Have. Requirements: FR12.3.
- AC12.2.1 Given a run, When it is going, Then each row shows Done, Problem with a plain reason, Skipped, To do in Google Slides, or In progress, and the count equals the finished rows over all rows.
- AC12.2.2 Given one row fails, When every row was attempted, Then the run finishes with that row as Problem and its slides left out.
- AC12.2.3 Given the connection is lost, sign-in fails, the template copy fails or the Drive is full, When the run cannot go on, Then it stops and the rows not yet done show Skipped.

### US12.3 Clear last week's PPT safely
As an Operator, I want last week's PPT moved to the Trash only when the new one is complete, so that I am never left with nothing. Priority: Must Have. Requirements: FR12.4.
- AC12.3.1 Given the option is on and a run finished with no Problem rows, When it ends, Then only my most recent earlier PPT made by the app, not the new one, moves to the Trash and the summary names it.
- AC12.3.2 Given a run stopped, or finished with any Problem row, When it ends, Then nothing moves to the Trash.
- AC12.3.3 Given the option is off, or a PPT is shared by someone else, When a run ends, Then that PPT is not touched.

### US12.4 Clean up a stopped run
As an Operator, I want a half-built PPT tidied away, so that I do not mistake it for a finished one. Priority: Must Have. Requirements: FR12.5.
- AC12.4.1 Given a run stopped partway, When the app can reach Drive, Then it moves the half-built PPT to the Trash.
- AC12.4.2 Given it cannot, When the run ends, Then it leaves the file, tells me it is incomplete and where it is, and moves it to the Trash on my next successful run.
- AC12.4.3 Given a half-built PPT is still in my Drive, When the next run finishes and last week's PPT is looked for, Then the half-built PPT is never taken to be last week's PPT, and only the most recent finished PPT made by the app can be moved to the Trash.

### US12.5 Template access
As an Operator, I want a clear message when the template is not shared with me, so that I know who to ask. Priority: Must Have. Requirements: FR12.6.
- AC12.5.1 Given a template that is not shared or does not allow copying, When I make slides, Then I see "Ask the developer to share the slide template with your Google account and allow copying."

### US12.6 Layout matches the intended slides
As the Developer, I want made PPTs to follow the intended layout and not the old manual mistakes, so that operators trust the output. Priority: Must Have. Requirements: FR12.7.
- AC12.6.1 Given the two reference PPTs, When I compare a made PPT, Then it includes the Spiritual Wisdom slide and shows hymn counts such as (1/5) to (5/5), the balanced rule of FR5.7 holds on its passages, hymns and text items (the split with the smallest line-count difference among the valid break points), and the line-splitting rule of FR6.2 holds on its hymn lines. As a stated extra, the developer's own visual judgement of the layout also applies.

### US12.7 Handle Google limits
As an Operator, I want the app to wait and retry when Google is busy, so that one busy moment does not ruin a run. Priority: Must Have. Requirements: FR12.8.
- AC12.7.1 Given Google answers with a rate limit or server error, When a row is being made, Then the app retries up to 3 times with increasing waits and then marks the row Problem.
- AC12.7.2 Given Google says my Drive is full, When I make slides, Then the run stops with "Your Google Drive is full".

## Group 13: Template and configuration (FR13)

### US13.1 Template slides found by tag
As a Developer, I want slides found by tags in the speaker notes, so that I can rearrange the template without code changes. Priority: Must Have. Requirements: FR13.1, FR13.2.
- AC13.1.1 Given I shuffle the template slides, When an operator makes a PPT, Then each row uses the right slide.
- AC13.1.2 Given the template, When I check it, Then tags are unique except the hymn-background tag, and the placeholders `{Title}`, `{Text}` and the schedule roles are kept, with the Opening Passage and Offering slides having a `{Text}` box only and no `{Reference}` placeholder.
- AC13.1.3 Given the developer's template v3 whose speaker notes do not hold the `autoppt:` tags yet, When I check it, Then every missing tag is reported until I add it.

### US13.2 Configuration kept by the developer
As a Developer, I want the configuration to live in the shared library and be read-only in the app, so that operators cannot break the slide types. Priority: Must Have. Requirements: FR13.3, FR13.6.
- AC13.2.1 Given the app, When an operator looks for a way to create a slide type or edit the template, Then none exists.
- AC13.2.2 Given the app, When I look at its text, Then all UI text is in resource files and no language selector shows.

### US13.3 Check the template
As a Developer, I want a "Check template" command, so that I find template mistakes before an operator does. Priority: Must Have. Requirements: FR13.4, FR13.5.
- AC13.3.1 Given a template copy with a missing tag, a missing placeholder and an unknown font in a placeholder box, When I run the check, Then it reports all three.
- AC13.3.2 Given the check passes on a copy, When I put the copy's id in the configuration, Then operators generate from it and not from a half-edited template.
- AC13.3.3 Given a different font in a fixed static text box, When I run the check, Then it is not reported, because static text is never measured and only fonts in placeholder boxes are checked.

## Group 14: Settings (FR14)

### US14.1 Settings
As an Operator, I want to change my name, theme and the clean-up option, so that the app suits me. Priority: Must Have. Requirements: FR14.1, FR14.2, FR14.3.
- AC14.1.1 Given Settings, When I open it, Then I can set "Delete last week's slides" on or off, change my name, choose light or dark theme, replace the setup file, sign out of Google and reload all hymns (US14.3).
- AC14.1.2 Given I change my name, When I close Settings, Then the top bar greets me with the new name.
- AC14.1.3 Given I sign out of Google, When I make slides, Then the app asks me to sign in again.

### US14.2 Know the version
As an Operator, I want to see the app version, so that I can tell the Developer which one I have. Priority: Must Have. Requirements: FR14.4.
- AC14.2.1 Given Settings, When I scroll to the bottom, Then I see for example "AutoPPT 1.0.0", and the same version is on the start-up screen and in "Copy details for support".

### US14.3 Reload all hymns
As an Operator, I want to replace every hymn on my computer with the shared version, so that I can start clean when my hymns look wrong. Priority: Must Have. Requirements: FR14.5.
- AC14.3.1 Given Settings, When I choose Reload all hymns, Then I am asked "Replace every hymn on this computer with the shared version? Changes you haven't saved for everyone will be lost." and Cancel is the default button.
- AC14.3.2 Given that question, When I choose Cancel or press Escape, Then nothing changes.
- AC14.3.3 Given that question, When I confirm, Then this computer's copy of the shared library and every unsaved hymn edit, including edits kept in the current service's rows, are replaced and discarded once every hymn has been downloaded again from the shared library.
- AC14.3.4 Given I reload all hymns, When it finishes, Then the shared library is unchanged.
- AC14.3.5 Given I confirm, When the download runs, Then the same blocking progress as other downloads shows, and my hymns and unsaved edits stay as they were until every hymn has arrived.
- AC14.3.6 Given the download fails, When it stops, Then nothing changes (every hymn and unsaved edit is as it was) and I see "Couldn't reload the hymns. Nothing was changed."

## Group 15: Errors (FR15)

### US15.1 Understand a problem
As an Operator, I want a plain message and a way to send details, so that I can get help. Priority: Must Have. Requirements: FR15.2, FR15.3.
- AC15.1.1 Given a problem with what I typed, When I look at the row, Then it shows a warning before I make slides.
- AC15.1.2 Given a runtime problem, When a dialog opens, Then it has a plain summary and what to try, "Copy details for support" and "Open log folder".
- AC15.1.3 Given I copy the details, When I paste them, Then they hold the version, operating system, Java version, error code, stack trace and the affected row.

### US15.2 Keep secrets out of logs
As a Developer, I want a local log with secrets removed, so that support details are safe to share. Priority: Must Have. Requirements: FR15.1, FR15.4.
- AC15.2.1 Given the app runs, When I look at the log, Then it is a local rolling file and nothing is sent anywhere.
- AC15.2.2 Given known secrets in a test, When they reach the log or the details text, Then none appears, and passage text is not logged.

### US15.3 Key and connection problems
As an Operator, I want each key problem to say what to do, so that I am not given a generic error. Priority: Must Have. Requirements: FR15.5, FR15.6.
- AC15.3.1 Given the scripture key or the shared-library token is rejected, When I use the app, Then I see "Ask the developer for a new setup file."
- AC15.3.2 Given my Google sign-in is no longer valid, When I make slides, Then the app asks me to sign in again.
- AC15.3.3 Given the connection is lost during a run, When the run reports, Then it says "The internet connection was lost" and not a generic error.

### US15.4 Plain words everywhere
As an Operator, I want the app never to use technical words, so that I understand every screen. Priority: Must Have. Requirements: FR15.7.
- AC15.4.1 Given all UI strings, When they are searched, Then none says "GitHub", "repo", "commit", "API" or "token".
- AC15.4.2 Given the vocabulary, When I use the hymn actions, Then they are called "Check for updates", "Save for everyone", "Delete for everyone" and "Discard my changes", and the setup file is called "Setup file".

## Group 16: Distribution and update check (FR16)

### US16.1 Download and run
As an Operator, I want to unzip the app once and double-click it, so that I need no installer. Priority: Must Have. Requirements: FR16.1, FR16.2, FR16.6.
- AC16.1.1 Given a zip for my computer (Windows x64, macOS Apple Silicon or macOS Intel), When I unzip it and double-click, Then the app starts with Java bundled, and nothing is installed.
- AC16.1.2 Given I replace the app folder with a new version, When I start it, Then my settings and secrets are kept, because they live in the app-data folder.
- AC16.1.3 Given the first-run guide, When I read it, Then it covers the Windows SmartScreen warning and the macOS "Open Anyway" steps.

### US16.2 Hear about a new version
As an Operator, I want to be told when a newer version exists, so that I can get it. Priority: Must Have. Requirements: FR16.3, FR16.4, FR16.5.
- AC16.2.1 Given the latest published release has a higher major.minor.patch version, When the app starts, Then it shows "A new version of AutoPPT is available (x.y.z)" with Download and Continue, Continue is the default and Escape means Continue.
- AC16.2.2 Given I choose Download, When my browser opens the zip for my computer, Then the screen stays open so I can still choose Continue.
- AC16.2.3 Given the check fails or times out after 5 seconds, or the release is not newer, When the app starts, Then it carries on silently, and the app never downloads or replaces itself.

## Group 17: Spikes and first slice (FR17)

### US17.1 Run the spikes first
As a Developer, I want the three spikes done before the build, so that the riskiest guesses are tested early. Priority: Must Have. Requirements: FR17.1.
- AC17.1.1 Given the spikes S1 to S3 (text measurement, hanging indent, Google sign-in as an unverified app), When each is run, Then a pass or fail is recorded.
- AC17.1.2 Given a spike fails, When I decide, Then its documented fallback is used.

### US17.2 Build the thin first slice
As a Developer, I want one slice running end to end first, so that I know the pieces connect. Priority: Must Have. Requirements: FR17.2.
- AC17.2.1 Given the start-up screen, the setup file and Google sign-in, the template reader and one Bible passage slide, When I run them together, Then a real PPT is made end to end.

## Group 18: Quality (NFR1 to NFR18)

### US18.1 Fast enough
As an Operator, I want making slides and starting up to be quick, so that the 10-minute goal holds. Priority: Must Have. Requirements: NFR1, NFR2, NFR3.
- AC18.1.1 Given a PPT of up to 60 slides, When I make slides on a normal home connection, Then it takes at most 2 minutes, and up to 100 slides take at most 3.5 minutes (provisional targets).
- AC18.1.2 Given every start-up check answers normally, When I launch the app, Then the main window opens within 10 seconds.
- AC18.1.3 Given a shared library of 500 hymns, When I first download it, Then it takes at most 30 seconds, and a refresh of changed files takes at most 5 seconds.

### US18.2 Runs on my computer
As an Operator, I want the app to run on my Windows or Mac, so that anyone can use it. Priority: Must Have. Requirements: NFR4.
- AC18.2.1 Given Windows 10, Windows 11, macOS 12 or later on Apple Silicon, or macOS 12 or later on Intel, When I run the app, Then it works on each.

### US18.3 The goals are met
As the Developer, I want the success measures checked, so that I know the rewrite worked. Priority: Must Have. Requirements: NFR5, NFR6, NFR7.
- AC18.3.1 Given one operator's first full build, When I time it, Then it takes under 10 minutes, with only the Chinese picture added by hand.
- AC18.3.2 Given 4 consecutive Sundays after launch, When I review the slides, Then no touch-up was needed, and operators have stopped using the legacy app.

### US18.4 Usable by keyboard with readable text
As an Operator, I want every screen usable by keyboard with readable contrast, so that the app suits me. Priority: Must Have. Requirements: NFR8.
- AC18.4.1 Given any screen, When I use only the keyboard, Then I can do everything on it.
- AC18.4.2 Given the light and dark themes, When I measure text contrast, Then it is at least 4.5:1 in both.

### US18.5 Secrets and privacy
As a Developer, I want secrets protected and nothing sent elsewhere, so that operators' data stays theirs. Priority: Must Have. Requirements: NFR9, NFR10.
- AC18.5.1 Given a full run, When I check `secrets.json` and the log, Then the file is readable only by the user, and no secret is in the log.
- AC18.5.2 Given a network capture of a full run, When I read it, Then it shows traffic only to Google, the shared library, the scripture service and the update check.

### US18.6 Reliable data
As an Operator, I want my files safe and compatible, so that an upgrade never loses my work. Priority: Must Have. Requirements: NFR11, NFR12.
- AC18.6.1 Given a crash or a full disk during a save, When I reopen the app, Then the previous good file survives and a clear message was shown.
- AC18.6.2 Given an older app and newer data files, When the older app loads them, Then it refuses with an update message, and a newer app reads older files.

### US18.7 Tested and tidy code
As a Developer, I want tests and formatting enforced in the build, so that the paginator stays correct. Priority: Must Have. Requirements: NFR13, NFR14.
- AC18.7.1 Given the `core` module, When line coverage falls below 80%, Then the build fails.
- AC18.7.2 Given code that breaks the formatting rule, When I build, Then the build fails on a Spotless violation.

### US18.8 Licensing and cost
As a Developer, I want only free services and fonts with their licences, so that the church pays nothing. Priority: Must Have. Requirements: NFR15, NFR16.
- AC18.8.1 Given the zip, When I open it, Then the only bundled font is TeX Gyre Adventor with its licence file next to it, placeholder boxes in the template use only Century Gothic, and "Check template" flags a font the app cannot measure in a placeholder box.
- AC18.8.2 Given the build, When I list dependencies, Then none is paid.

### US18.9 Safe releases
As a Developer, I want each release published as a draft first, so that operators only see it after I check it. Priority: Must Have. Requirements: NFR17.
- AC18.9.1 Given a pushed version tag, When the build finishes, Then a draft GitHub release is created, and I publish it by hand after checking the zips.
- AC18.9.2 Given a draft release, When an operator starts the app, Then no update prompt appears until I publish it.

### US18.10 Consistent wording
As an Operator, I want the app to say "PPT" or "slides", so that the words match what I know. Priority: Must Have. Requirements: NFR18.
- AC18.10.1 Given the UI strings and generated PPT text, When they are searched for the word "deck", Then none is found.

## INVEST Notes

- **Independent:** each story can be built and tested alone, except that stories in a group follow the proto-Unit order in `intent-backlog.md`.
- **Negotiable:** the exact wording of messages comes from the requirements and may be refined at the refined-mockups stage.
- **Valuable:** each story names a benefit, and developer stories name the operator outcome they protect.
- **Estimable and Small:** each story is a cluster of 1 to 4 closely related sub-requirements with 1 to 5 criteria. A story with a single criterion is fine when its behaviour is narrow.
- **Testable:** each criterion is one Given/When/Then with an observable result.

## Assumptions & Open Questions

None.
