# Units of Work: AutoPPT Rewrite

Inputs: `components.md` and `decisions.md` (Domain Design), `requirements.md`, `stories.md`, `intent-backlog.md`, `team-practices.md`, and `units-generation-questions.md` (Q1 to Q8). A Unit is a piece the developer builds, tests and merges as one piece. All Units end up in one zipped app image for each platform (FR16.1), so no Unit is deployed on its own. This file does not choose the build order or the critical path: Delivery Planning does.

## Units

| Unit ID | Directory | Kind | Size | Name |
|---|---|---|---|---|
| U1 | u1-spikes | spec | S | Spikes S1 to S3 |
| U2 | u2-first-slice | ui | XL | Thin first slice |
| U3 | u3-layout | library | L | Layout |
| U4 | u4-service-builder | ui | XL | Service builder |
| U5 | u5-hymns-and-sharing | ui | L | Hymns and sharing |
| U6 | u6-template-check | library | S | Template check |
| U7 | u7-packaging | packaging | M | Packaging |

## Unit Definitions

### U1 u1-spikes (spec, S)

Runs the three experiments and records pass or fail and the values they set. No app code.

- **Depends on:** none.
- **Responsibilities:**
  - Run S1 (text measurement against what Slides renders), S2 (the hanging indent with a superscript number and the source-line alignment) and S3 (Google sign-in as an unverified app).
  - Record a pass or fail for each, and the values they set: the safety margin, the inner padding, the indent method, and whether the fallback sign-in is used (FR17.1; US17.1).
  - If a spike fails, record which documented fallback is used.
- **Boundaries:** Owns no component. Produces results and settings that U2 and U3 read.
- **Deployment model:** none (not shipped).
- **Complexity:** S.
- **Implementation notes and constraints:** Throwaway experiments. The recorded results are the product, not code. A failed spike changes U2 or U3, not this Unit.

### U2 u2-first-slice (ui, XL)

The running app end to end: build and checks, start-up, wizard, sign-in, template reader, one passage slide, read-only library and configuration.

- **Depends on:** none. It is a root and the first runnable Unit. It does not wait for the spikes, because S3 (sign-in) has a documented fallback.
- **Responsibilities:**
  - The Gradle build with five modules, Spotless, the 80% line-coverage gate on `core` and CI (US18.7).
  - Start-up, the internet check, the connection-lost cover and the single-copy lock (FR1, FR3.10).
  - The wizard: setup file, name and Google sign-in (FR2), with `SecretStore`, `Redactor` and `GoogleAuth`; the name is kept in `UserSettings`.
  - A read-only shared library and configuration: download, cache, refresh and schema checks (`LibrarySync`, `Configuration`).
  - The template reader and the ESV client, and a thin Make slides run that writes one Bible passage row into a real PPT (`MakeSlidesRun`, `SlideWriter`, thin `PassageLayout` and `Paginator`) (FR17.2).
  - **The initial `AutoPPTData` configuration files**, committed to the `AutoPPTData` repository (added at Delivery Planning Q6, by the developer): `config/app.yaml` (with the template v3 id `1Njchznjkd2toS1DqIvAStjP0w4gyMNq9S8j1q8v9U_E`), `config/slide-types.yaml`, `config/presets/normal.yaml`, `config/presets/holy-communion.yaml`, `config/layout.yaml` (the spike results fill its values), `config/fonts.yaml` and `README.md`. They follow contract C7 (`contract-summary.md`). This is a deliverable of U2, not a component.
- **Boundaries:** Everything needed to start the app, sign in and make one passage slide, and the initial `AutoPPTData` configuration files. Does not make hymn, text or service-builder screens, and does not edit hymns. U2 writes to a second repository (`AutoPPTData`), which must join the work before U2's Code Generation (`aidlc engine intent add-repo`).
- **Deployment model:** embedded in the one app image for each platform.
- **Complexity:** XL.
- **Implementation notes and constraints:** The largest Unit. Real services only, with no stand-in files. `Paginator` and `PassageLayout` have thin versions here that U3 completes, and `MakeSlidesRun` has a thin version that U4 completes. The Unit is complete when a real PPT is made end to end. The developer still adds the `autoppt:` tags to the template, supplies a real setup file, and sets the Google client to In production (external-dependency-map.md).

### U3 u3-layout (library, L)

The full paginator and the passage, hymn, text-block and simple slide layouts, with date rules.

- **Depends on:** u1-spikes, u2-first-slice.
- **Responsibilities:**
  - The full `Paginator` with the bundled measuring font and balanced splitting (FR5.7), using the values set by U1.
  - `PassageLayout` (V1 to V7, the Opening Passage and Offering), `HymnLayout` (H1 to H6, backgrounds), `TextBlockLayout` (T1 to T3, CommonMark) and `SimpleSlideLayout` (headers, schedule, static, title-only, Chinese Monthly Scripture).
  - `ServiceDates`: the coming Sunday, raised ordinals and next week's date.
- **Boundaries:** Pure layout logic in `core`. Does not call Google or the screens.
- **Deployment model:** embedded in the one app image for each platform.
- **Complexity:** L.
- **Implementation notes and constraints:** Test-first for the paginator and other pure logic (team.md). Network clients are tested against fakes.

### U4 u4-service-builder (ui, XL)

The service, its rows and editors for passage and text slide types, Settings, the error dialog, and the full Make slides run with progress and clean-up.

- **Depends on:** u2-first-slice, u3-layout.
- **Responsibilities:**
  - The service: order presets, rows, reorder, autosave and the row warnings (`ServiceBuilder`, `ServiceStore`).
  - The main window, the progress dialog, the error dialog and Settings (name, theme, clean-up, replace setup file, sign out, version), added to `ScreenViews`.
  - The editors for passage, text blocks, quote, schedule, header and title-only slide types (`SlideEditors`).
  - The full Make slides run: stop and finish rules, retries, the template access check, last week's PPT and half-built PPT clean-up (`MakeSlidesRun`, `SlidesCleaner`).
  - `ErrorModel`: plain messages, support details and the local log.
- **Boundaries:** Owns the service and the Make slides rules. Does not edit hymns beyond hosting the row, and does not build the update check.
- **Deployment model:** embedded in the one app image for each platform.
- **Complexity:** XL.
- **Implementation notes and constraints:** The second largest Unit. `MakeSlidesRun` is completed here, so its stop, finish and clean-up rules are tested against fakes in `core`.

### U5 u5-hymns-and-sharing (ui, L)

The hymn editor, saving, adding and deleting hymns, Reload all hymns, and the one-time migration.

- **Depends on:** u2-first-slice, u4-service-builder.
- **Responsibilities:**
  - The hymn editor and its confirmations: save, discard, delete and New hymn, with the first-save-wins rule (`HymnStore`).
  - Reload all hymns: the download and swap steps added to `LibrarySync`, the all-or-nothing rule in `ReloadAllHymns`, and its Settings button and confirmations.
  - The local hymn edits, the clearing rule in `ServiceBuilder`, and the newer-version badge.
  - The one-time migration of the 116 legacy hymns (`HymnMigrator`).
- **Boundaries:** Owns hymn writing and Reload all hymns. Reading the library stays in U2.
- **Deployment model:** embedded in the one app image for each platform.
- **Complexity:** L.
- **Implementation notes and constraints:** `LibrarySync` is owned by U2. This Unit only adds the two Reload steps behind the existing library port.

### U6 u6-template-check (library, S)

The developer-only Check template command.

- **Depends on:** u2-first-slice.
- **Responsibilities:**
  - The developer-only Check template command: tags, placeholders, hymn backgrounds and measurement fonts (`TemplateChecker`; FR13.4, FR13.5).
- **Boundaries:** Owns the template-checking rules only.
- **Deployment model:** embedded in the one app image for each platform.
- **Complexity:** S.
- **Implementation notes and constraints:** A small Unit. It needs the template reader and the configuration from U2 only.

### U7 u7-packaging (packaging, M)

Release builds, the update check and its screen, licences, and the first-run guide.

- **Depends on:** u2-first-slice.
- **Responsibilities:**
  - The update check and its start-up screen (`ReleaseChecker`; FR16.3 to FR16.5), added to `ScreenViews`.
  - The tagged release build for Windows x64, macOS Apple Silicon and macOS Intel as a draft release (FR16.1, NFR17).
  - The bundled-font licence files, the licence and dependency check, and the first-run guide (FR16.6, NFR15, NFR16).
  - The release manual check of one real PPT built in Google Drive.
- **Boundaries:** Owns release and distribution. Contains no layout or service logic.
- **Deployment model:** embedded in the one app image for each platform.
- **Complexity:** M.
- **Implementation notes and constraints:** The package build needs the app as it stands when tagged, but its own code and tests need only U2.

## Component Ownership

Each of the 32 components has one owning Unit, which completes it. Another Unit may build a thin version first or add its own screens. The component list is in `components.md`.

| Component | Owning Unit | Note |
|---|---|---|
| Startup | U2 u2-first-slice | - |
| ConnectionMonitor | U2 u2-first-slice | - |
| HttpsConnectivityProbe | U2 u2-first-slice | - |
| SingleInstanceGuard | U2 u2-first-slice | - |
| SecretStore | U2 u2-first-slice | - |
| Redactor | U2 u2-first-slice | - |
| SetupService | U2 u2-first-slice | - |
| GoogleAuth | U2 u2-first-slice | - |
| TemplateReader | U2 u2-first-slice | - |
| EsvClient | U2 u2-first-slice | - |
| LibrarySync | U2 u2-first-slice | read-only in U2; U5 adds the download and swap steps for Reload all hymns |
| Configuration | U2 u2-first-slice | - |
| SlideWriter | U2 u2-first-slice | - |
| UserSettings | U2 u2-first-slice | U2 holds the name; U4 adds the other settings and their screen |
| MakeSlidesRun | U4 u4-service-builder | thin version for one passage row in U2 |
| Paginator | U3 u3-layout | thin version in U2 |
| PassageLayout | U3 u3-layout | thin version in U2 |
| ScreenViews | U2 u2-first-slice | U4, U5 and U7 add their own screens |
| HymnLayout | U3 u3-layout | - |
| TextBlockLayout | U3 u3-layout | - |
| SimpleSlideLayout | U3 u3-layout | - |
| ServiceDates | U3 u3-layout | - |
| ServiceBuilder | U4 u4-service-builder | U5 adds the clearing of local hymn edits |
| ServiceStore | U4 u4-service-builder | - |
| ErrorModel | U4 u4-service-builder | - |
| SlidesCleaner | U4 u4-service-builder | - |
| SlideEditors | U4 u4-service-builder | U5 adds the hymn editor |
| HymnStore | U5 u5-hymns-and-sharing | - |
| ReloadAllHymns | U5 u5-hymns-and-sharing | - |
| HymnMigrator | U5 u5-hymns-and-sharing | - |
| TemplateChecker | U6 u6-template-check | - |
| ReleaseChecker | U7 u7-packaging | - |

## Sources

- [Q2] The Units follow the backlog by capability, with the thin first slice as U2 (units-generation-questions.md).
- [Q3] U2 holds the build, start-up, wizard, sign-in, template reader, ESV client, a thin Make slides run and the read-only library and configuration.
- [Q4] The spikes are U1, of kind `spec`. The owner later removed the edge from U2 to U1, so U2 is the first runnable Unit and only U3 waits for the spikes.
- [Q5] U3 to U7 are the five Units after the first slice.
- [Q7] The Unit kinds and the story assignment are the answers to Q7.
- [Delivery Planning Q6] The initial `AutoPPTData` configuration files are a deliverable of U2 (added after approval of this stage, with the developer's approval).
- [Q8] The Unit that first builds `ScreenViews` or `SlideEditors` owns it, and later Units add their screens.

## Assumptions & Open Questions

- [assumption] `UserSettings` is owned by U2 because the wizard's name step needs it. Q3 did not list it, and Q5 puts the other settings in U4. U4 adds those to the same component.
- [assumption] `ErrorModel` is owned by U4 as Q5 says. U2 shows its own plain messages in the wizard and start-up screens and uses `Redactor` for the log.
- [assumption] `MakeSlidesRun`, `Paginator` and `PassageLayout` have thin versions in U2 and are completed by U4 and U3. Each is one component with one owning Unit, as the catalogue requires.
- [assumption] The kind of U2 is `ui` and U1 is `spec`, as Q7 says. U1 has no code, so its Construction design documents are short.
- None otherwise.

Unit count: 7 (U1 to U7).
