# Units Generation Questions: AutoPPT Rewrite

Inputs: `components.md` and `decisions.md` (Domain Design: 32 components, ADR-001 to ADR-008), `requirements.md`, `stories.md`, `intent-backlog.md` (the eight proto-Units), `team-practices.md`, and `high-level-design.md` sections 9, 12 and 14. This stage groups the components into Units of work and draws the dependency graph. It does not pick the build order or the critical path: Delivery Planning does that.

A Unit is a piece the developer builds, tests and merges as one piece. A Unit depends on every Unit whose code its own code or tests need. Units with no edge between them could be built at the same time.

Please answer by editing this file: write the letter after each `[Answer]:`.

## Q1. What your documents already answer

Each line is the point, the answer, and where it comes from.

- **One app, one zip.** Every Unit ends up in the same self-contained app image for each platform, so no Unit is deployed on its own. Source: requirements.md FR16.1: "The app ships as a zipped self-contained app image for each of Windows x64, macOS Apple Silicon and macOS Intel."
- **Gradle modules.** The modules are `core`, `google`, `esv`, `library` and `app`, with `core` depending on none of the others. Source: components.md Ports section and ADR-001.
- **The module names are a suggestion.** The exact module boundaries are decided here. Source: requirements.md Open Questions: "The exact Gradle module boundaries are decided in units generation."
- **A thin first slice comes first.** It is the start-up screen and internet check (F0), the setup file and Google sign-in (F8), the template reader (F6), and one Bible passage slide written into a real PPT (F4), after the spikes S1 to S3. Source: team.md Walking Skeleton and requirements.md FR17.2.
- **The backlog is a starting point.** It lists eight proto-Units: spikes, thin end-to-end path, core layout, Bible passages, text slide types, service builder, hymns and sharing, packaging. Source: intent-backlog.md: "Units Generation decides the final unit boundaries."
- **Suggested units in the design document.** `core-layout`, `esv`, `library`, `google`, `app-shell` and `packaging`. Source: high-level-design.md section 14, step 3.
- **Unit names.** Each is `u{n}-{description}` in lowercase, with an id `U{n}`. Source: the stage's own format rule.

A. All correct
B. Some need changing (say which and how)
X. Other (please specify)

[Answer]: A

## Q2. [Architect] How are the components grouped into Units?

Context: the design document suggests six Units, one for each Gradle module. The backlog lists eight proto-Units by capability. A module-by-module split cannot give a first Unit that runs end to end, because a module on its own does not start. The thin first slice needs bits of `app`, `core`, `google` and `esv` together (for example the start-up screen, the sign-in and the writer).

A. By capability, following the backlog, with the thin first slice as the first Unit (about 8 Units). Each Unit owns whole components, and a Unit may span modules. (Recommended: the first Unit can run end to end, each later Unit adds one visible capability and can be tested on its own, and it keeps the owner's own order from the backlog. Reversible: merging or splitting two neighbouring Units later only moves rows in the story map.)
B. By Gradle module, as the design document suggests (about 6 Units, one for each module, plus a first slice). Each Unit has one build file, but the first slice would have to cut across all of them, so it would be a seventh Unit that repeats parts of the others.
C. By feature, one Unit per feature group in the requirements (F0 to F12, about 15 Units). Very small Units, but several share components (for example F1 and F2 both use `MakeSlidesRun` and `ServiceBuilder`), so edges multiply.
X. Other (please specify)

[Answer]: A

## Q3. [Architect] What is in the first Unit (the thin slice)?

Context: the first Unit must be the smallest slice that runs end to end (team.md). Making a PPT needs the template id and layout values, which `Configuration` reads from the shared library cache (`LibrarySync`). The start-up screen also refreshes the library (FR1.4), and wizard step 4 downloads it (FR2.8). The paginator and layouts are the main technical risk, but the slice has only one passage slide.

A. The first Unit includes the Gradle build and its checks (Spotless, the 80% `core` coverage gate and CI), start-up and the connection cover, the single-copy lock, the wizard's setup file, name and sign-in steps, `SecretStore`, `Redactor`, `GoogleAuth`, `TemplateReader`, `EsvClient`, a thin `MakeSlidesRun` with `SlideWriter` for one passage row, a thin `PassageLayout` and `Paginator`, and a read-only `LibrarySync` and `Configuration` (download and cache, no editing). The wizard's step 4 and the start-up refresh use the real library. (Recommended: the slice then runs for real with no stand-in files, and every later Unit builds on one working base. It is the largest Unit, about XL.)
B. As A, but `LibrarySync` and `Configuration` are replaced by a local configuration file for the slice. A smaller first Unit (about L), but the stand-in has to be thrown away later, and the wizard's step 4 and the start-up refresh would not run for real.
C. As A, plus the whole `Paginator` and all three layouts in the first Unit. The risky logic is tested earliest, but the first Unit grows much larger than a thin slice.
X. Other (please specify)

[Answer]: A

## Q4. [Architect] Where do the spikes live?

Context: spikes S1 to S3 test text measurement, the hanging indent with a superscript number, and Google sign-in as an unverified app. Their results set values the layout uses (safety margin, inner padding, indent method) and decide whether the fallback sign-in is used (FR17.1, US17.1). They are throwaway experiments, not shipped code.

A. A Unit of its own, `u1-spikes`, of kind `spec` (its output is recorded results and settings other Units read), with no code in the app. The first slice depends on it. (Recommended: US17.1 then has a Unit and a pass or fail record, and the DAG shows that the layout and sign-in Units wait for the results.)
B. Spike work stays outside the Units, as a Delivery Planning task. US17.1 stays a GAP in the story map.
C. Each spike joins the Unit it informs (S1 in the layout Unit, S2 in the layout Unit, S3 in the first slice). No separate Unit, but the layout Unit would start with an experiment instead of code.
X. Other (please specify)

[Answer]: A

## Q5. [Architect] How is the rest split after the first Unit?

Context: after the first slice the backlog lists core layout, Bible passages, text slide types, the service builder, hymns and sharing, and packaging. The components that remain are: the full `Paginator`, `PassageLayout`, `HymnLayout`, `TextBlockLayout` and `SimpleSlideLayout`; `ServiceBuilder`, `ServiceStore`, `ServiceDates`, `UserSettings`, `ErrorModel`, `SlidesCleaner` and the rest of `MakeSlidesRun`; `HymnStore`, `ReloadAllHymns`, `HymnMigrator`; `ReleaseChecker`; `TemplateChecker`; and the screens and editors in `app`.

A. Five Units after the first: (1) `layout` (the full paginator and the passage, hymn, text-block and simple layouts), (2) `service-builder` (the service, its rows, autosave, settings, error dialog, slide editors for passage and text slide types, full Make slides run with progress, clean-up and the Trash rules), (3) `hymns-and-sharing` (hymn editor, `HymnStore`, `ReloadAllHymns`, `HymnMigrator`), (4) `template-check` (`TemplateChecker`, the developer command), and (5) `packaging` (release and update check, `ReleaseChecker`, first-run guide). (Recommended: five clear capabilities, with `service-builder` the largest, and it matches the backlog. About 8 Units in all with the first Unit and the spikes.)
B. Split `service-builder` in two: the service builder with its editors, and Make slides with its progress and clean-up. Smaller Units, but the two share `ServiceBuilder` rows and `SlidePlan`, so each depends on the other's code unless a third piece is cut out.
C. Merge `layout` into the first Unit and `template-check` into `service-builder`. Fewer Units (about 5), but each is larger and the paginator work is no longer visible on its own.
X. Other (please specify)

[Answer]: A

## Q6. [Architect] Which Units may be built at the same time?

Context: the developer builds alone. A Unit with no edge between it and another could be built at the same time in its own copy of the project. Edges must be real: a Unit depends on another only if its code or tests need it. The first Unit and the spikes come first; Delivery Planning chooses the order.

A. Draw only the real code dependencies and leave the rest independent, so that `layout`, `hymns-and-sharing`, `template-check` and `packaging` have no edge between them where none exists. (Recommended: the graph states what depends on what, not what is convenient, and Delivery Planning is free to build them one at a time or in parallel.)
B. Make the Units a simple chain (each depends on the one before) so that nothing is ever built at the same time. Easier to follow, but it states dependencies that do not exist and would hide the real ones.
X. Other (please specify)

[Answer]: A

## Q7. [Architect] What kind is each Unit, and how are the cross-cutting stories assigned?

Context: a Unit has a kind: `service` (a deployed executable), `spec` (a contract or record consumed in place), `ui` (a frontend surface), `packaging` (build or distribution) or `library` (reusable code). The kind decides which design documents Construction writes for it. Eight stories had no component in Domain Design: US16.1 (zipped images), US17.1 (spikes), US17.2 (thin slice), US18.2 (operating systems), US18.3 (the goals), US18.7 (tested and tidy code), US18.8 (licensing and cost) and US18.9 (safe releases).

A. Kinds: spikes `spec`; first slice `ui` (it is the running app, with start-up, wizard and one slide); layout `library`; service builder `ui`; hymns and sharing `ui`; template check `library`; packaging `packaging`. Story assignment: US17.1 to the spikes Unit; US17.2 and US18.7 (the build, Spotless and coverage gate live in the first Unit) to the first Unit; US16.1, US18.2, US18.8 and US18.9 to `packaging` (the release build, the licences and the manual check); US18.3 (the goals) to `service-builder` and `layout` as the Units whose output the operator judges. (Recommended: each story has a Unit that can show it done, and no story is mapped to a Unit that does not touch it.)
B. As A, but the build, Spotless and coverage gate become a separate `packaging`-kind Unit before the first slice. It keeps the build work apart, but a Unit that cannot run is not a slice, and the first Unit would then not be the first Unit.
C. Leave the eight stories unmapped again (GAP) and hand them to Delivery Planning. Simpler, but the story map would not cover every story.
X. Other (please specify)

[Answer]: A

## Q8. [Architect] Which Unit owns the screens?

Context: `ScreenViews` and `SlideEditors` in `app` are two large components (ADR-008). Each screen belongs to a capability: the start-up screen and wizard to the first Unit, the main window to the service builder, the hymn editor and its confirmations to hymns and sharing, Settings to the service builder (its Reload all hymns button to hymns and sharing). The components are single entries in the catalogue, so a Unit that completes only some of their screens would need a split rule.

A. Each Unit builds only the screens and editors of its own capability, and the Unit that first builds `ScreenViews` or `SlideEditors` owns the component. Later Units add their screens to it, and the story map names every Unit that adds a screen. The Units that add to a component depend on the Unit that owns it. (Recommended: no screen is orphaned and the component stays single, as ADR-008 decided.)
B. Split `ScreenViews` and `SlideEditors` into one component for each screen group in this stage, so each Unit owns whole components. Cleaner ownership, but it changes the approved component catalogue after Domain Design was approved.
X. Other (please specify)

[Answer]: A

## Consolidated Summary Confirmation

Does this all look correct before I generate the artifact?

- Looks correct
- Request changes

[Answer]: Looks correct
