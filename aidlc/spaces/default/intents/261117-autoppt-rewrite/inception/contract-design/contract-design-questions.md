# Contract Design Questions: AutoPPT Rewrite

Inputs: `unit-of-work.md` and `unit-of-work-dependency.md` (7 Units, 8 edges), `components.md` (32 components, the Ports table), `requirements.md`, and `high-level-design.md` sections 7.4, 8 and 10. A contract is an agreement across a boundary: what crosses it, in what shape, and what happens when something goes wrong.

This app is one desktop program. It runs no server and exposes no API to anyone outside it. So the contracts here are of three kinds:

- **Between Units:** the ports owned by `core` and the data types that cross them, which one Unit provides and another uses.
- **Data files shared with the developer's tools:** the hymn file, the configuration files, the setup file and the template tags. The developer or a tool writes them, and the app reads them (and writes hymn files).
- **Third-party services the app calls:** ESV, Google Slides and Drive, Google sign-in, and the shared library's file service. We do not own these, but the calls we depend on are pinned so that fakes match the real thing.

Please answer by editing this file: write the letter after each `[Answer]:`.

## Q1. What your documents already answer

Each line is the point, the answer, and where it comes from.

- **No public API.** The app opens no server and sends data only to Google, the shared library, the ESV service and the update check. Source: requirements.md NFR10: "no data sent anywhere except Google, the shared library, the ESV API and the update check, and no cloud hosting."
- **The ports exist.** `core` owns ten ports (`ConnectivityProbe`, `BibleSource`, `LibrarySyncPort`, `HymnStorePort`, `ReleaseCheckerPort`, `SlideWriterPort`, `TemplateCopyPort`, `SlidesCleanerPort`, `TemplateReaderPort`, `GoogleAuthPort`), and the data types that cross them are defined in `core`. The exact methods and types are for this stage. Source: components.md Ports section, and ADR-001.
- **The hymn file.** One Markdown file for each hymn, with a front-matter block (`id`, `title`, `language`, `aliases`, `arrangement`, `updated`, `updatedBy`) and blocks named like `[v1]` and `[c]`. The file name is `<id>.md` and never changes. Source: high-level-design.md section 8.2.
- **The shared library layout.** `hymns/<id>.md` and `config/` with `app.yaml`, `slide-types.yaml`, `presets/normal.yaml`, `presets/holy-communion.yaml`, `layout.yaml` and `fonts.yaml`, plus a `README.md`. Source: high-level-design.md section 8.1.
- **Versioning of files.** Files carry a `schemaVersion`. An older app refuses newer files and tells the user to update, and a newer app reads older ones. Source: requirements.md FR7.9 and NFR12.
- **The setup file.** It holds the Google client ID and secret, the ESV key and the shared-library token with its repository name. Source: requirements.md FR2.3.
- **Template slides are found by a tag in the speaker notes.** Tags are unique except the hymn-background pool. The placeholders are `{Title}`, `{Text}` and the schedule roles. Source: requirements.md FR13.1, FR13.2.
- **First save wins.** A write sends the last-known version (SHA) of the file, and a stale write or an existing new id is refused with one plain message. Source: requirements.md FR7.3, FR7.4, FR7.7.
- **Retry rule.** On a rate-limit or server error from Google the app retries up to 3 times with increasing waits, then marks the row Problem. Source: requirements.md FR12.8.
- **Names.** No contract name uses "Ppt" or "Deck". Source: your answer to Domain Design Q1.

A. All correct
B. Some need changing (say which and how)
X. Other (please specify)

[Answer]: A

## Q2. [Architect] How are the contracts between Units written down?

Context: the boundaries between Units are Java interfaces and the types that cross them, all in `core`. They are compiled together, so a mismatch fails the build. The stage expects a fenced spec block for each contract (OpenAPI, AsyncAPI or a shared schema), and allows "any other contract format appropriate to the integration mechanism". None of these boundaries is an HTTP or event interface.

A. For each contract, a fenced `java` block holding the interface and its types, with a short rule for each method (what it returns, what it throws, whether it blocks and on which thread). Data files get a fenced `yaml` shared-schema block. (Recommended: this is the form a developer builds from, and the compiler checks it. Reversible: the blocks can become real source files in Construction without changing the contract.)
B. Plain tables only, with no code blocks. Easier to read, but the exact method shapes are left to Construction and the stage's fenced-spec rule is not met.
C. Write them as OpenAPI-style documents as if the ports were services. A familiar format, but nothing here is an HTTP call, so the document would describe things that do not exist.
X. Other (please specify)

[Answer]: A

## Q3. [Architect] Who owns each contract, and how does a change get agreed?

Context: a port is used by the Unit that calls it and implemented by another. Per ADR-001 the port is owned by `core`, which is built first in U2. A change to a port touches every Unit that uses or implements it. The developer is the only person who decides (constraint C-O1).

A. The Unit that first builds a contract owns its spec: U2 owns the ports and the setup-file and secrets contracts, U3 owns the layout and `SlidePlan` extensions, U4 owns the service-builder contracts, U5 owns the hymn-write contracts, and the developer's data contracts (hymn file, configuration) belong to U2 as the reader. A change is made in the owning Unit's spec first, and each Unit that uses it is rechecked. An addition that no consumer must handle (a new optional field, a new method with a default) needs no recheck. (Recommended: one owner for each contract and one rule for change. It matches the DAG, because every consumer already depends on the owner.)
B. One owner for all contracts, U2, because it builds `core` first. Simple, but U3 to U5 would have to ask U2's spec to change for contracts that only they use.
C. No owner. Each Unit changes a contract as it needs, and the compiler finds the mismatches. Fastest, but nothing records who agreed.
X. Other (please specify)

[Answer]: A, with a tweak: U2 owns every port signature, including HymnStorePort (hymn write), the reload steps on LibrarySyncPort and ReleaseCheckerPort, even though U5 and U7 build those implementations; U2 also freezes the closed SlidePlan shape (templateTag, replacements, styledRanges, notes). U3, U4 and U5 own their rules (e.g. hymn id, SHA, save message) and additive optional data only. Any port signature change is a U2 change and rechecks every dependent Unit; only an optional YAML field needs no recheck.

## Q4. [Architect] How do the data files change over time?

Context: the hymn file, the configuration files and the setup file are written by people or tools other than the app, and may be newer or older than the app that reads them (FR7.9, NFR12). The shared library keeps history, so a bad write can be undone by the developer.

A. A whole-number `schemaVersion` in each file. Adding an optional field keeps the version, and the reader ignores fields it does not know. Removing or renaming a field, or changing what a field means, raises the version. An app that finds a higher version refuses the file and says to update. A newer app reads every lower version it has shipped. The setup file carries its own version in the same way. (Recommended: it is the rule your requirements already state, written down as a policy.)
B. As A, but only the configuration files carry a version. The hymn file has none, because it has a small, fixed shape. Fewer fields, but FR7.9 says every file carries one, and a later change to the hymn file would have no signal.
C. Use semantic versions (major.minor.patch) in each file. More detail, but the app only needs to know "newer or not", and a second number adds nothing the reader can act on.
X. Other (please specify)

[Answer]: A, with a tweak: the service file and the settings file also carry a whole-number schemaVersion. A shared-library file with a higher schemaVersion never replaces a cached copy the app can still read: keep the cached file and tell the user to update (as FR1.4 does). One too-new hymn is skipped and reported (like FR7.10); a too-new app.yaml or slide-types.yaml blocks that configuration. Unknown fields are ignored while the schemaVersion is one the app knows.

## Q5. [Architect] What failures can cross a port?

Context: the run, the start-up and the wizard decide what to do from the kind of failure: a lost connection stops a run (FR1.6, FR12.3), a sign-in that is no longer valid asks to sign in again (FR15.5), a rate limit retries 3 times (FR12.8), a full Drive stops a run (FR12.8), a rejected key says "Ask the developer for a new setup file." (FR15.5), a stale write says "Someone else saved this hymn first" (FR7.4), a template not shared gives its own message (FR12.6), and a malformed hymn file is skipped (FR7.10). Each port wraps a different third-party service that reports these differently.

A. A small fixed set of failure kinds defined in `core`: `ConnectionLost`, `SignInExpired`, `KeyRejected`, `RateLimited` (with the retry advice), `ServerBusy`, `NotFound`, `AccessDenied`, `Conflict`, `DriveFull`, `Malformed` and `Unexpected`. Every port converts what its service returns into one of these, and nothing else crosses a port. `ErrorModel` and `MakeSlidesRun` decide from the kind only. (Recommended: callers need no knowledge of Google, ESV or the shared library, and fakes can raise each kind to test every rule.)
B. Let each port pass on the service's own failure (an HTTP status or the Google client's exception). Less converting code, but `core` would have to know every service's codes, which breaks ADR-001's one-way rule.
C. Return a plain message and a flag per call. Simple, but the rules for retry, stop and sign-in need more than a message.
X. Other (please specify)

[Answer]: A plus NeedsAppUpdate, with tweaks: NeedsAppUpdate is raised only when a library or setup file parses as too new (never by Google or ESV), and is distinct from Malformed. No Cancelled kind (nothing stops a call in flight). SignInExpired and KeyRejected stay separate. Conflict covers GitHub 409, and 422 only when the body says the file already exists or the sha was not supplied; any other 422 is Unexpected. RateLimited carries the wait hint (Retry-After or x-ratelimit-reset). DriveFull is Google Drive storage only; local disk-full stays inside ServiceStore. The caller decides the action from the call plus the kind, not the kind alone.

## Q6. [Architect] How does a call behave: blocking, threads, time limits and progress?

Context: the screens run on the Swing thread and must not block (team.md; design document section 9.1). Network calls have time limits already stated: the internet check 5 seconds (FR1.2), the library refresh 5 seconds (FR1.4), the update check 5 seconds (FR16.3). A run reports progress for each row to the progress dialog (FR12.3).

A. Port calls block the calling thread and are never made on the Swing thread. Each port method states its time limit, and a call that exceeds it ends as `ConnectionLost` (a timeout is a lost connection for the caller). The run reports progress through a `RunListener` that `MakeSlidesRun` calls from its own background thread with one event for each row (started, Done, Problem with a reason, Skipped, To do in Google Slides) and one for the end (stopped or finished); the screens move the event to the Swing thread. (Recommended: blocking calls are simple to fake and test, and one listener keeps the screen free of run rules.)
B. Every port call returns a future, and the callers combine them. Fewer threads to manage by hand, but every caller and every fake would need to handle futures, and a simple sequence of steps becomes harder to read and test.
C. Calls block, but the run reports by the screen polling its state. Simple, but the progress dialog would lag and a poll cannot show the order of events.
X. Other (please specify)

[Answer]: A, with a tweak: a timeout is its own failure kind, TimedOut, not ConnectionLost. Each caller applies its own policy: the connectivity probe returns not connected; the update check stays silent; the library refresh keeps the cached copy; a Google row retries like a 5xx (FR12.8) and then becomes Problem. ConnectionLost (which stops a Make slides run) is used only when the probe has failed or the error is a real network failure. No cancel token is needed: the run stops between calls.

## Q7. [Architect] Which third-party calls are pinned?

Context: the fakes that test the ports must behave like the real services, and a service that changes its behaviour breaks the app. We do not own these services, so there is no spec of ours to write, but we can record the exact calls we depend on and what we do with each reply. The services are the ESV passage text service, Google sign-in, the Google Slides and Drive services, and the shared library's file service (list, read, write and delete a file, with a version number).

A. For each service, a table of the calls the app makes: the call, what we send, what we read from the reply, which failure kind each error maps to, and the time limit. The tables sit in `contract-summary.md` under "Third-party calls the app depends on", with the source of each service's own documentation. (Recommended: fakes then have one place to match, and a change by a provider is easy to find.)
B. Only list the services and say they are tested against fakes. Shorter, but a fake could drift from the real call without anyone seeing it.
C. Write full OpenAPI documents for each service. Complete, but it copies the providers' own long documents, and most of the calls are never used.
X. Other (please specify)

[Answer]: A, with a tweak: map each service's errors by response reason/body, not status alone. Drive 403 storageQuotaExceeded is DriveFull (stop the run, no retry); Drive 403 rateLimitExceeded/userRateLimitExceeded and 429 are RateLimited (retry); Slides quota 429 is RateLimited. GitHub 404 on the private repo listing is KeyRejected, 404 for one hymn while the listing works is NotFound, 403 with rate-limit headers is RateLimited, 403 saying the token cannot access the resource is KeyRejected. ESV: pin the Authorization: Token header and the throttling limits, and record a passage miss as an empty or error passage body. Each table row also states the caller's action: retry, stop, use cache, or stay silent.

## Q8. [Architect] How is the template contract recorded?

Context: the template (Google Slides, v3) is edited by hand by the developer and read by the app. Its speaker-note tags, placeholders and fonts are a contract between the template and `slide-types.yaml` (FR13.1, FR13.2). The template does not yet hold the `autoppt:` tags, and `Check template` (U6) will report every missing one (FR13.4).

A. Record it as a data contract in `contract-summary.md`: the tag names for each slide type, the placeholders each layout kind needs (`{Title}`, `{Text}`, the schedule roles, and no `{Reference}`), the hymn-background pool tag, and the rule that a tag is unique. The list of tags is the one `slide-types.yaml` will hold, and `Check template` tests it. (Recommended: the developer needs one list to add tags against, and U2, U3 and U6 each read it the same way.)
B. Leave the tag names to the developer when the template is edited, and only record the rule. Less to write now, but U2's reader and U6's checker would need the names before the developer has chosen them.
X. Other (please specify)

[Answer]: A, with a tweak: the schedule roles are read from the template's schedule table (not a closed list; FR10.1), with today's F7 names only as the current example. The contract also includes the measurement-font rule: every font used in a placeholder box needs a measurement font (FR13.4).

## Consolidated Summary Confirmation

Does this all look correct before I generate the artifact?

- Looks correct
- Request changes

[Answer]: Looks correct
