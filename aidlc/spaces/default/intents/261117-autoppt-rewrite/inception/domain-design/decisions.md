# Architecture Decisions: AutoPPT Rewrite

Each decision follows from an answer in `domain-design-questions.md` and is realised in `components.md`.

## ADR-001: Start from the design document's modules and seams, with slide names

- **Context:** The design document (sections 9 and 9.1) already names the Gradle modules (`core`, `google`, `esv`, `library`, `app`), says `core` has no network or Swing dependency, and names the parts tests replace with fakes. The project rule forbids the word "deck", and the owner asked that no name use "Ppt" or "Deck" either (Q1).
- **Decision:** Keep the document's modules and seams. Use `SlidePlan`, `SlideWriter` and `SlidesCleaner` in place of `DeckPlan`, `DeckWriter` and `DeckCleaner`. Module direction (added at the first review, R-01): `core` owns an interface (a port) for everything outside it: Google (template copy, `SlideWriter`, `SlidesCleaner`, `TemplateReader`, `GoogleAuth`), ESV (`BibleSource`), the shared library (`LibrarySync`, `HymnStore`, `ReleaseChecker`) and a `ConnectivityProbe`. The `google`, `esv` and `library` modules implement the ports. Module dependencies therefore run one way only: `app` depends on `google`, `esv` and `library`, and those depend on `core`. `core` depends on none of them. The HTTPS probe is implemented in `google` (`HttpsConnectivityProbe`), so `ConnectionMonitor` in `core` keeps the re-check timing and the events and has no network code. The ports are in the Ports table of `components.md`, and the Module column shows where each component lives. The data types that cross a port (`Hymn`, `LibraryFile`, `TemplateInfo`, `Passage`, `ReleaseInfo`) are defined in `core`; the owning component creates and keeps the data, and Contract Design settles the types.
- **Consequences:** Positive: no rework of the agreed structure, and the wording rule holds in code. Negative: the document had to be updated (done), and any older note using the old names is stale. Every call from `core` to a non-core component needs a port, which adds interfaces, and a port's methods must be settled in Contract Design. A Gradle build also cannot have a circular project dependency, which this direction avoids.
- **Alternatives Rejected:** Letting `core` call the other modules directly (core and those modules would depend on each other, which Gradle rejects, and `core` would gain network code). Keeping the document's old names (breaks the wording rule). Naming the cleaner `PptCleaner` (the owner rejected "Ppt" in names).

## ADR-002: A shared Paginator with one layout component for each rule family

- **Context:** Passages follow V1 to V7, hymns H1 to H6 and text blocks T1 to T3. All three use the same balanced-split rule (FR5.7), and several slide types do not paginate (Q2).
- **Decision:** One `Paginator` (measuring, line breaking, balanced splitting) with `PassageLayout`, `HymnLayout`, `TextBlockLayout` and `SimpleSlideLayout` above it. Each produces entries of the `SlidePlan`.
- **Consequences:** Positive: the balancing rule and its tests live in one place; each family changes for its own reasons; the pure logic sits in `core` under the 80% floor. Negative: four layout components and one more interface (the break points each layout passes to the paginator). Reversible: the layouts depend only on the paginator's interface.
- **Alternatives Rejected:** One `LayoutEngine` with per-type settings (mixes unrelated rules in one large component). One layout component per slide type (about 15 components, and the shared rule would be repeated or pulled out anyway).

## ADR-003: Separate reading the hymn library from writing it, and keep local edits with the service

- **Context:** Refresh and Reload all hymns only read (FR7.8, FR14.5). Save, add and delete use the last-known SHA and the first save wins (FR7.3, FR7.4, FR7.7). Local edits are saved with the service row (FR7.8) (Q3).
- **Decision:** `LibrarySync` owns the cached `Hymn` entities and their SHAs and handles refresh and reload. `HymnStore` owns writes and depends on `LibrarySync`. `LocalHymnEdit` belongs to `ServiceBuilder`.
- **Consequences:** Positive: the conflict rule is isolated and testable; the shared model carries no local state; a reload clears local edits by asking `ServiceBuilder`, not by reaching into the library. Negative: a reload needs `LibrarySync` and `ServiceBuilder` to cooperate. Changed at the first review (R-02): a `ReloadAllHymns` component in `core` owns the FR14.5 rule, so the screens only start the action. It asks the library port to download every hymn into a temporary copy; only on full success does it swap the cache and tell `ServiceBuilder` to clear every unsaved hymn edit; on failure nothing changes. The rule sits in `core` under the 80% coverage floor.
- **Alternatives Rejected:** The screen layer coordinating the reload (business rule outside the coverage floor, and a bug could lose unsaved edits). One `HymnLibrary` doing everything (mixes refresh and conflict rules). `HymnStore` also keeping local edits (a library component would own data saved with the service).

## ADR-004: Configuration, template reading and template checking are three components

- **Context:** The configuration lives in the shared library and is maintained by the developer (FR13.3). The template is a Google file found by tags (FR13.1). The developer-only check compares them (FR13.4) (Q4).
- **Decision:** `Configuration` (`core`) owns slide types, presets, layout values and the font map. `TemplateReader` (`google`) reads tags, placeholders and fonts. `TemplateChecker` (`core`) holds the comparison rules.
- **Consequences:** Positive: the checking rules are pure and fall under the 80% floor; `google` holds only reading. Negative: the checker needs a fake reader in tests, which is the intended seam.
- **Alternatives Rejected:** One component that reads the template and checks it (pure rules would depend on Google). Configuration folded into `LibrarySync` (the sync would have to understand slide types, presets and fonts).

## ADR-005: The Make slides rules live in `core`

- **Context:** A run ends STOPPED or FINISHED (FR12.3), retries on 429 and 5xx (FR12.8), and moves old PPTs to the Trash only under strict rules (FR12.4, FR12.5). This is where mistakes would cost an operator an old PPT (Q5).
- **Decision:** `MakeSlidesRun` in `core` owns the run, each row's result, the stop, finish and retry rules, and the clean-up decisions. It calls `SlideWriter`, `SlidesCleaner` and `TemplateReader` through ports owned by `core` (`SlideWriterPort`, `TemplateCopyPort`, `SlidesCleanerPort`, `TemplateReaderPort`) and implemented in `google`, and takes the template id from `Configuration` (R-03). It checks that the template can be read before the first run and after a failed copy (FR12.6). A connection error from the writer or cleaner is treated as the lost-connection stop (FR1.6, R-04).
- **Consequences:** Positive: the rules are tested with fakes and counted by the 80% floor; the Swing code only shows progress. Negative: `core` defines the ports `google` implements, so a port change touches both modules; this is the one-way module direction of ADR-001.
- **Alternatives Rejected:** The run in `app` next to the progress dialog (rules fall outside the coverage floor and mix with Swing). The run in `google` (rules could not be tested without Google fakes).

## ADR-006: One writer for secrets, and a separate redactor

- **Context:** `secrets.json` holds every key and the Google refresh token, with owner-only permissions and an atomic write (FR2.5, NFR9, NFR11). One redactor keeps secrets out of logs and support text (FR15.4) (Q7).
- **Decision:** `SecretStore` is the only writer of `secrets.json`. `SetupService` and `GoogleAuth` save through it. `Redactor` is a separate component that `SecretStore` tells about every secret.
- **Consequences:** Positive: permissions and atomic writes are done once; two writers cannot clobber each other. Negative: `GoogleAuth` and `SetupService` both depend on `SecretStore`. The redactor must be told a secret before it is used, so `SecretStore` raises that on every save.
- **Alternatives Rejected:** Each component writing its own part of `secrets.json` (two writers of one file). The redactor inside `SecretStore` (it also cleans log lines that have nothing to do with storage).

## ADR-007: Start-up and connection handling are three components, plus supporting components the document did not name

- **Context:** Start-up runs a fixed sequence (FR1.1 to FR1.5, FR16.3). A lost connection is watched for the whole session (FR1.6). Only one copy may run (FR3.10) (Q6). Several rules also need an owner that the design document leaves implicit: dates, local service saving, user settings, error text and the legacy migration.
- **Decision:** `Startup`, `ConnectionMonitor` and `SingleInstanceGuard` are separate; `ReleaseChecker` stays in `library`. I added `ServiceDates`, `ServiceStore`, `UserSettings`, `ErrorModel` and `HymnMigrator`, each with its own data or rules.
- **Consequences:** Positive: the start-up order and timeouts are unit tested without a window; every entity has one owner. Negative: five components beyond the document; the developer should confirm them at the gate. Reversible: each is small and could merge into a neighbour later.
- **Alternatives Rejected:** One `AppLifecycle` (the sequence, the monitor and the lock change for different reasons). Leaving the sequence in Swing code (nothing about order or timeouts could be unit tested). Leaving the supporting rules unowned (ambiguous ownership).

## ADR-008: One view per screen and seven editor kinds chosen by the configuration

- **Context:** The slide-type configuration already names an editor for each type (design document section 8.1). The editors behave differently: the hymn editor has confirmations and the block editor has formatting buttons (Q8).
- **Decision:** `ScreenViews` holds one view and one view model per screen. `SlideEditors` holds seven kinds: Passage, Hymn, Text blocks, Quote, Schedule, Header and Title-only. Each depends on `core` only through its view model.
- **Consequences:** Positive: behaviour that differs is kept apart; a new slide type that reuses an editor kind needs configuration only. Negative: `ScreenViews` and `SlideEditors` are large components, and Units Generation may split them. The wizard is a full window (`JFrame`), so Escape does nothing there.
- **Alternatives Rejected:** One generic editor (cannot express confirmations and formatting buttons). One view for each slide type (repeats the same editor).
