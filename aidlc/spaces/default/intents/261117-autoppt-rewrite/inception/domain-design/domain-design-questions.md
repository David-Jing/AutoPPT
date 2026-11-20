# Domain Design Questions: AutoPPT Rewrite

Inputs: `requirements.md` (FR1 to FR17, NFR1 to NFR18), `stories.md` (US1.1 to US18.10), `team-practices.md`, and `high-level-design.md` section 9 (the design document). This stage names the building blocks (components) of the app and says which one owns each piece of data. It does not group them into deployable units, which is Units Generation's job, and it does not choose the tech stack.

A component is code we write. Google Slides, Google Drive, the ESV service and the shared library are things a component uses, not components.

Please answer by editing this file: write the letter after each `[Answer]:`.

## Q1. What your documents already answer

Your documents already settle these points, so I have not asked them separately. Each line is the point, the answer, and where it comes from.

- **Gradle modules.** The modules are `core`, `google`, `esv`, `library` and `app`. Source: high-level-design.md section 9: "Modules: `core`, `google`, `esv`, `library`, `app`."
- **What `core` may use.** `core` is pure Java with no network or Swing dependencies. Source: high-level-design.md section 9: "`core` has no network or Swing dependencies."
- **The seam between layout and Google.** Layout produces a plan of slides, and a writer in `google` turns it into Google requests. Source: high-level-design.md section 9: "`DeckPlan` is the seam: layout produces it, and `DeckWriter` turns it into a `batchUpdate`."
- **Parts that tests swap for fakes.** The Google, ESV and library clients, `TextMeasurer`, `LineBreaker`, `BibleSource`, `SecretStore` and `Clock`. Source: high-level-design.md section 9.1: "Seams that get bindings. These are the parts tests swap for fakes."
- **Which module holds which block.** `google` holds `GoogleAuth`, `TemplateReader`, the writer and the cleaner. `esv` holds `EsvClient`. `library` holds `LibrarySync`, `HymnStore` and `ReleaseChecker`. Source: high-level-design.md section 9 diagram.
- **Where configuration lives.** Slide types, presets, layout values and fonts live in the shared library and are read-only in the app. Source: requirements.md FR13.3.
- **Names.** The design document calls the plan, writer and cleaner `DeckPlan`, `DeckWriter` and `DeckCleaner`. The project rule says to write "PPT" or "slides", never "deck", so this design calls them `SlidePlan`, `SlideWriter` and `PptCleaner`. This is the one change I made to your document's names.

A. All correct
B. Some need changing (say which and how)
X. Other (please specify)

[Answer]: B — all correct, except the names: use "slide(s)" only in component and type names, never "Ppt" or "Deck". So `SlidePlan`, `SlideWriter` and `SlidesCleaner` (not `PptCleaner`).

## Q2. [Architect] How is layout split into components?

Context: three families of rules decide how text fits on slides. Bible passages follow V1 to V7 (FR5), hymns follow H1 to H6 (FR6), and announcements, supplications, catechism and Spiritual Wisdom follow T1 to T3 (FR9). All three share one idea: pick the valid break with the smallest line-count difference (FR5.7). The slide types with no pagination (Header, Sermon Header, Next Week's Schedule, Static, Title-only, Chinese Monthly Scripture) only fill placeholders.

A. A shared `Paginator` (measuring, line breaking, balancing) with three layout components on top: `PassageLayout`, `HymnLayout` and `TextBlockLayout`, plus a small `SimpleSlideLayout` for the types that do not paginate. Each produces entries of the `SlidePlan`. (Recommended: it matches your document's layout, keeps the balancing rule in one place, and each family changes for its own reasons. Reversibility is high, because the three layouts only depend on the paginator's interface.)
B. One `LayoutEngine` that handles every slide type through per-type rule settings. Fewer components, but one large component whose rules for passages, hymns and text would be mixed together.
C. One layout component for each slide type (about 15). Very clear ownership, but the shared balancing rule would be repeated or pulled into a helper anyway.
X. Other (please specify)

[Answer]: A

## Q3. [Architect] Who owns hymns, and how is reading kept apart from writing?

Context: the shared hymn library holds one file per hymn. The app caches a copy with each file's version number (the SHA). Saving or deleting uses the last-known SHA, so the first save wins (FR7.3, FR7.4, FR7.7). Refresh (FR7.8) and the new "Reload all hymns" (FR14.5, downloads into a temporary copy, then swaps) only read. Local edits ("Only on this computer") are kept in the service's rows (FR7.8), so they are not part of the library.

A. Two components in `library`, as in your document: `LibrarySync` owns the cached `Hymn` entities and their SHAs and handles refresh and reload, and `HymnStore` handles save, new hymn and delete with the SHA check, depending on `LibrarySync`. Local edits belong to the service row (owned by the service builder). (Recommended: reading and caching change for different reasons than conflict rules for writing, and local edits stay out of the shared model.)
B. One `HymnLibrary` component that does reading, caching, reload, save and delete. One place for hymns, but the conflict rule and the refresh and reload rules would sit in the same component.
C. As A, but `HymnStore` also keeps the local edits. Everything about hymn changes is in one place, but local edits would then be owned by a library component although they are saved with the service.
X. Other (please specify)

[Answer]: A

## Q4. [Architect] How are configuration and the template separated?

Context: the configuration (app, slide types, presets, layout values, fonts) lives in the shared library and the developer maintains it (FR13.3). The template is a Google Slides file found by tag in each slide's speaker notes (FR13.1). The developer-only "Check template" command compares the two (FR13.4). Both carry a schema version (FR7.9, NFR12).

A. Two components. `Configuration` (in `core`) owns the slide types, presets, layout values and font map read from the library cache, and refuses newer schema versions. `TemplateReader` (in `google`) reads the template's tags, placeholders and fonts. `TemplateChecker` (in `core`, used by the developer command) compares the two. (Recommended: the checking rules are pure logic that can be tested with the 80% floor in `core`, and the Google reading stays in `google`.)
B. One `Configuration` component that also reads the template and checks it. Fewer components, but pure rules would depend on Google.
C. Configuration folded into `LibrarySync`. Fewer parts, but the sync would then have to understand slide types, presets and fonts.
X. Other (please specify)

[Answer]: A

## Q5. [Architect] Where do the Make slides rules live?

Context: a run copies the template, builds the slides, shows progress for each row, and then ends as STOPPED or FINISHED (FR12.3). Retries follow FR12.8. The old PPT moves to the Trash only after a finished run with no Problem rows (FR12.4). A stopped run tries to trash its half-built PPT, or remembers its id for the next run (FR12.5). All of this is rules, not Google calls, so it can be tested with fakes.

A. A `MakeSlidesRun` component in `core` owns the run, each row's result, and the stop, finish, retry and clean-up rules. It calls interfaces implemented in `google` (`SlideWriter`, `PptCleaner`, template copy). (Recommended: the rules are the part most likely to go wrong, and in `core` they are covered by the 80% floor and by fakes.)
B. The run lives in the `app` module as a view model, next to the progress dialog. Simple, but the stop and clean-up rules would be mixed with Swing code and fall outside the coverage floor.
C. The run lives in `google`, next to the writer. The rules would then depend on Google classes, so they could not be tested without Google fakes.
X. Other (please specify)

[Answer]: A

## Q6. [Architect] How is start-up and connection handling split?

Context: every launch runs a fixed sequence: internet check, update check, library refresh, load saved keys (FR1.1 to FR1.5, FR16.3). While the app is open, a lost connection covers the window and clears by itself (FR1.6). Only one copy of the app may run (FR3.10), by an operating-system file lock.

A. Three components: `Startup` (in `core`, the sequence and its timeouts, using interfaces for each check), `ConnectionMonitor` (in `core`, the re-check every 5 seconds and the lost and restored events) and `SingleInstanceGuard` (in `app`, the file lock). `ReleaseChecker` stays in `library` as your document says. (Recommended: each has its own lifecycle and its own tests, and the sequence is testable without a window.)
B. One `AppLifecycle` component that does all of it. Fewer parts, but the sequence, the monitor and the lock change for different reasons.
C. Leave the sequence and the monitor in the Swing code of `app`. Less design now, but nothing about the order or the timeouts could be unit tested.
X. Other (please specify)

[Answer]: A

## Q7. [Architect] Who owns secrets, the setup file and Google sign-in?

Context: the setup file holds the Google client ID and secret, the ESV key and the shared-library token (FR2.3). Accepted keys and the Google refresh token are saved in one `secrets.json` with owner-only permissions and an atomic write (FR2.5, NFR9, NFR11). Settings can replace the setup file or sign out (FR14.2). One redactor keeps secrets out of logs and support details (FR15.4).

A. `SecretStore` is the only owner and writer of `secrets.json` (every key and the refresh token). `SetupService` (in `core`) holds the wizard rules: read the file, check the ESV key and library token, and save through `SecretStore`. `GoogleAuth` (in `google`) signs in and saves the refresh token through `SecretStore`. A separate `Redactor` (in `core`) is told every secret by `SecretStore`. (Recommended: one writer makes the permissions and the atomic write easy to get right and to test.)
B. Each of `SetupService` and `GoogleAuth` writes its own part of `secrets.json`. Fewer links, but two writers of one file can clobber each other.
C. Put the redactor inside `SecretStore`. One fewer component, but the redactor is also used on log lines that have nothing to do with storage.
X. Other (please specify)

[Answer]: A

## Q8. [Designer] How are the screens in `app` structured?

Context: the screens are in mockups.md. There are the start-up screen, the first-start wizard (a full window, not a dialog), the main window with its connection cover, editors for each kind of slide type, the progress dialog, the error dialog, Settings, and the confirmation and message dialogs. The slide-type configuration names an `editor` for each type (design document section 8.1), so the choice of editor is driven by the configuration.

A. One view and one view model for each screen, and seven editor kinds chosen by the configuration: Passage, Hymn, Text blocks, Quote, Schedule, Header and Title-only. Each editor depends on `core` only through its view model. (Recommended: the editors behave differently, and the configuration already names the editor kind.)
B. One generic editor that builds its fields from the slide type's settings. Less code, but the hymn editor and the block editor have behaviour (confirmations, formatting buttons) a generic editor cannot express.
C. A view for each slide type (about 15). Simple, but it repeats the same editor several times (Static, Chinese Monthly Scripture and Title-only differ only in their fields).
X. Other (please specify)

[Answer]: A

## Consolidated Summary Confirmation

Does this all look correct before I generate the artifact?

- Looks correct
- Request changes

[Answer]: Looks correct
