# Contract Summary: AutoPPT Rewrite

Inputs: `unit-of-work.md` and `unit-of-work-dependency.md` (7 Units, 8 edges), `components.md` (32 components, the Ports table, ADR-001), `requirements.md`, `high-level-design.md` sections 2.3, 8, 9, 10 and F2 to F4, and `contract-design-questions.md` (Q1 to Q8). Names say "slide(s)" and never "Ppt" or "Deck" in a component, type or tag name.

This app is one desktop program. It opens no server and exposes no API to anyone outside it (NFR10). The contracts are of three kinds:

- **Between Units:** the ports owned by `core`, the failure kinds, the run listener, the slide plan and the data types that cross them (contracts C1 to C5).
- **Data files shared with the developer and the developer's tools:** the hymn file, the configuration files, the setup file, the secrets file, the service file, the settings file (C6 to C9), and the template tags (C10).
- **Third-party calls the app depends on:** pinned so the fakes match the real services (C11).

## Contracts

| # | Provider | Consumer | Mechanism | Owner |
|---|---|---|---|---|
| C1 | `core` ports (U2 defines, U2/U3/U5/U7 implement as noted) | Every Unit that calls a port | Java interfaces in `core` | U2 |
| C2 | `core` failure kinds | Every port implementation and caller | Java type in `core` | U2 |
| C3 | `MakeSlidesRun` (U4 completes; U2 thin version) | `ScreenViews` (progress dialog) | `RunListener` callback | U2 (shape), U4 (rules) |
| C4 | Layouts (U2 thin, U3 full) | `MakeSlidesRun`, `SlideWriterPort` | `SlidePlan` data type | U2 (closed shape) |
| C5 | `LibrarySync`, `Configuration`, `HymnStore`, and others | Layouts, `ServiceBuilder`, editors, every port user | Shared data types in `core` | U2 |
| C6 | Developer and `HymnStore` | `LibrarySync` | Hymn file (Markdown with front matter) | U2 (reader); U5 owns the id and save rules |
| C7 | Developer | `Configuration` | Configuration files (YAML) | U2 |
| C8 | Developer | `SetupService`, `SecretStore` | Setup file and `secrets.json` | U2 |
| C9 | App | `ServiceStore`, `UserSettings` | Service file and settings file (JSON) | U4 (service file), U2 (settings file) |
| C10 | Developer | `TemplateReader`, `TemplateChecker`, layouts | Template tags, placeholders and fonts | U2 (reader), U6 (checker) |
| C11 | Third-party services | Port implementations in `google`, `esv`, `library` | Pinned calls and error mapping | U2 (tables), each Unit that implements a port |

## Contract ownership rules

- **One owner for each contract.** The Unit that first builds a contract owns its spec, with these decisions (Q3). U2 owns every port signature, including `HymnStorePort` (hymn write), the reload steps on `LibrarySyncPort` and `ReleaseCheckerPort`, even though U5 and U7 build those implementations. U2 also freezes the closed `SlidePlan` shape (C4). U3, U4 and U5 own their rules (for example the hymn id, the SHA and the save message) and additive optional data only.
- **A change to a port signature is a U2 change.** It rechecks every Unit that uses or implements the port. Only an optional field in a YAML file needs no recheck.
- **Additive changes stay safe.** A new optional field in a data file, or a new method with a default in a Java type, needs no change in a consumer. A reader ignores a field it does not know while the file's `schemaVersion` is one the app knows.
- **Breaking changes are agreed by the developer** (the only decision-maker, constraint C-O1). The change is written in the owning spec first, and each Unit that uses it is rechecked.
- **Versioning of data files** is in the policy below.

## Versioning policy for data files

Every file the app reads carries a whole-number `schemaVersion` (Q4): the hymn file, each configuration file, the setup file, the service file and the settings file.

- Adding an optional field keeps the version. Removing or renaming a field, or changing what a field means, raises it.
- An app that finds a version higher than it knows treats the file as too new. A newer app reads every lower version it has shipped.
- **A too-new shared-library file never replaces a cached copy the app can still read.** The app keeps the cached file and tells the user to update, as FR1.4 does for a failed refresh.
- **One too-new hymn** is skipped and reported, like a malformed one (FR7.10). **A too-new configuration file** (`app.yaml`, `slide-types.yaml`, a preset, `layout.yaml` or `fonts.yaml`) blocks that file: the app keeps the readable cached copy of it, or when none exists, blocks the configuration and says to update.
- A too-new setup file is refused with an update message.
- Unknown fields are ignored while the version is one the app knows.

## C1. Ports owned by `core` (U2)

All port calls block the calling thread and are never made on the Swing thread (Q6). Every method states its time limit in its comment, and the table after the code gathers them. A time limit covers the whole call, not one request inside it. A call that exceeds it ends as `TimedOut`, and each caller applies its own policy (C2). No cancel token exists, because nothing stops a call in flight: a run stops between calls. Every method may throw `PortFailure` (C2) and nothing else.

```java
// package core.ports. Owned by U2. The implementing module is shown on each port.

/** Implemented in google (HttpsConnectivityProbe). Used by ConnectionMonitor. */
public interface ConnectivityProbe {
  /** One HTTPS request to a Google endpoint. Time limit 5 s (FR1.2).
   *  Returns false on no answer, a timeout, a captive portal or a blocked network.
   *  Never throws a PortFailure: it answers yes or no. */
  boolean isConnected();
}

/** Implemented in esv (EsvClient). Used by PassageLayout and SetupService. */
public interface BibleSource {
  /** One query for one piece of the passage field (already split on ';').
   *  Sends the reference as typed. Returns the canonical reference and the verses.
   *  A reference the service cannot find is NotFound. Over a service limit is TooLarge.
   *  Time limit 10 s. */
  Passage passage(String reference) throws PortFailure;
  /** Checks a CANDIDATE key with one inexpensive request, before anything is saved (FR2.4, FR2.9).
   *  KeyRejected if refused. Time limit 10 s. */
  void checkKey(String candidateEsvKey) throws PortFailure;
}

/** Implemented in library (LibrarySyncPort's implementation, LibrarySync).
 *  Used by Startup, Configuration, ServiceBuilder, SetupService and ReloadAllHymns.
 *  It never writes to the shared library. */
public interface LibrarySyncPort {
  /** Lists the library, downloads only files whose SHA changed, and updates the cache.
   *  Time limit 5 s (FR1.4). Every file it can read is kept in the cache even when other files
   *  are too new or malformed; those are listed in the result. It throws NeedsAppUpdate only
   *  when a too-new app.yaml or slide-types.yaml leaves no readable cached copy of that file. */
  RefreshResult refresh() throws PortFailure;
  /** First full download (wizard step 4). Reports progress 0..1. Time limit 60 s (the NFR3 target
   *  stays 30 s). Uses the saved keys. */
  void firstDownload(ProgressListener listener) throws PortFailure;
  /** Checks a CANDIDATE library token and repository with one inexpensive request,
   *  before anything is saved (FR2.4, FR2.9). KeyRejected if refused. Time limit 10 s. */
  void checkToken(String candidateLibraryToken, String candidateLibraryRepository) throws PortFailure;
  /** Reload step 1: downloads every hymn into a temporary copy and never touches the cache.
   *  Time limit 60 s. A malformed or too-new hymn is skipped and listed in the copy; it does not
   *  fail the reload. A failed transfer of any other hymn fails the call and keeps nothing. */
  TemporaryLibrary downloadAllToTemporary(ProgressListener listener) throws PortFailure;
  /** Reload step 2: replaces the cache with the temporary copy in one step. Local only, no network, no time limit. */
  void swapIn(TemporaryLibrary copy) throws PortFailure;
  /** Records a hymn the app has just written, with its new SHA, so the next save sends the new SHA.
   *  Local only. Called by HymnStore after a successful write (same module). */
  void recordWritten(Hymn hymn, String newSha);
  /** Removes a hymn the app has just deleted from the cache. Local only. */
  void recordDeleted(String hymnId);
  /** Reads from the cache. No network. */
  List<Hymn> cachedHymns();
  Optional<LibraryFile> cachedFile(String path);
}

/** Implemented in library (HymnStore). U2 owns the signature, U5 builds the implementation.
 *  Time limit for each call 30 s. After a success, the implementation calls
 *  LibrarySyncPort.recordWritten or recordDeleted (R-11). */
public interface HymnStorePort {
  /** Writes an edited hymn using the last-known SHA. First save wins.
   *  Conflict if someone changed the hymn first. The message names the hymn and the typed
   *  user name (FR7.3). Returns the new SHA. */
  String save(Hymn edited, String lastKnownSha, String userName) throws PortFailure;
  /** Adds a new hymn with no SHA. Conflict if the id already exists. Returns the new SHA. */
  String create(Hymn added, String userName) throws PortFailure;
  /** Deletes a hymn using the last-known SHA. Conflict if someone changed it first. */
  void delete(String hymnId, String lastKnownSha, String userName) throws PortFailure;
}

/** Implemented in library (ReleaseChecker). U2 owns the signature, U7 builds the implementation. */
public interface ReleaseCheckerPort {
  /** One request to the public latest-release endpoint. Time limit 5 s (FR16.3).
   *  Empty when the latest published release is not newer than currentVersion.
   *  On TimedOut or any other failure, the caller stays silent (FR16.5). */
  Optional<ReleaseInfo> newerThan(Version currentVersion) throws PortFailure;
}

/** Implemented in google (GoogleAuth). Used by the wizard and by the other google implementations. */
public interface GoogleAuthPort {
  /** Opens the system browser and waits for the loopback redirect. Time limit 5 minutes, because
   *  it waits on the user. Returns CANCELLED when the user closes or refuses the consent screen,
   *  and also when the 5 minutes pass (the caller shows no error and stays on the sign-in step).
   *  Saves the refresh token through SecretStore when it returns SIGNED_IN.
   *  KeyRejected when the Google client is disabled or deleted (FR2.7). */
  SignInResult signIn() throws PortFailure;
  /** An access token that is valid now. SignInExpired when the refresh token no longer works.
   *  Time limit 30 s. */
  AccessToken accessToken() throws PortFailure;
  /** Removes the refresh token through SecretStore (FR14.2). Local only. */
  void signOut();
}
public enum SignInResult { SIGNED_IN, CANCELLED }

/** Implemented in google (SlideWriter). Used by MakeSlidesRun. */
public interface TemplateCopyPort {
  /** Copies the template into the user's Drive. The copy is marked IN PROGRESS (an app property
   *  autoppt = in-progress) at copy time, so it is never taken for a finished file (R-05).
   *  AccessDenied or NotFound when the template is not shared or does not allow copying.
   *  Time limit 30 s. NEVER RETRIED: a TimedOut (or any other failure) stops the run, because a lost
   *  reply could leave a second copy behind (R-13). */
  SlidesFile copyTemplate(String templateId) throws PortFailure;
}
public interface SlideWriterPort {
  /** Writes ALL slides of one row in ONE atomic batchUpdate: all of them or none.
   *  A failed call leaves nothing of the row in the copy, so a row that fails is simply left out.
   *  A retry is safe: the new slides get object ids derived from the row id and the slide's
   *  position, so a retry after a reply was lost finds those ids already present and returns
   *  them without writing again. Returns the object ids of the slides it wrote, in order.
   *  Time limit 30 s. */
  List<SlideRef> writeRow(SlidesFile target, PlannedRow row) throws PortFailure;
  /** Final step. Puts the written slides in list order (updateSlidesPosition) and deletes
   *  every original template slide. orderedSlides is the concatenation of every successful
   *  writeRow result in row order. Time limit 30 s. */
  void finish(SlidesFile target, List<SlideRef> orderedSlides) throws PortFailure;
  /** Marks the copy FINISHED (app property autoppt = slides), only after a run whose every row
   *  was attempted. A STOPPED run is never marked, so it is never taken for last week's file.
   *  Time limit 30 s. */
  void markFinished(SlidesFile target) throws PortFailure;
  /** The link that opens the slides in the browser. Local only. */
  URI link(SlidesFile target);
}

/** Implemented in google (SlidesCleaner). Used by MakeSlidesRun. Time limit for each call 30 s. */
public interface SlidesCleanerPort {
  /** The user's most recent earlier file that is marked FINISHED (autoppt = slides), owned by
   *  the user, and not justMade. A file marked in-progress is never returned (R-05).
   *  Empty when none. Never returns a file shared by someone else. */
  Optional<SlidesFile> mostRecentEarlier(SlidesFile justMade) throws PortFailure;
  /** Moves one file to the Drive Trash, by id, so a half-built file that was remembered locally
   *  can be trashed. NotFound means it is already gone and counts as success. */
  void moveToTrash(String fileId) throws PortFailure;
  /** The user's own files marked IN PROGRESS (autoppt = in-progress), other than justMade.
   *  These are leftovers: a half-built copy of a stopped run, or an orphan copy from any cause
   *  (R-13). Never returns a file shared by someone else. */
  List<SlidesFile> leftoversInProgress(SlidesFile justMade) throws PortFailure;
}

/** Implemented in google (TemplateReader). Used by MakeSlidesRun, HymnLayout,
 *  SimpleSlideLayout, TemplateChecker and the Schedule editor. */
public interface TemplateReaderPort {
  /** Checks the template can be read and copied. AccessDenied or NotFound gives the
   *  share-the-template message (FR12.6). Run before the first run and after a failed copy.
   *  Time limit 30 s. */
  void checkAccess(String templateId) throws PortFailure;
  /** Tags, placeholders, fonts in placeholder boxes, schedule roles and the hymn-background pool.
   *  Time limit 30 s. */
  TemplateInfo read(String templateId) throws PortFailure;
}
```

Time limits, whole call (a limit is "none" only where the call is local or user driven):

| Port method | Limit | Note |
|---|---|---|
| `ConnectivityProbe.isConnected` | 5 s | FR1.2 |
| `LibrarySyncPort.refresh` | 5 s | FR1.4 |
| `LibrarySyncPort.checkToken`, `BibleSource.checkKey` | 10 s | working value |
| `BibleSource.passage` | 10 s | working value |
| `LibrarySyncPort.firstDownload`, `downloadAllToTemporary` | 60 s | The NFR3(c) target of 30 s for 500 hymns stays the target; 60 s is the failure threshold so a slow but working connection does not fail |
| `swapIn`, `recordWritten`, `recordDeleted`, `cachedHymns`, `cachedFile` | local | no network |
| `HymnStorePort.save`, `create`, `delete` | 30 s | working value |
| `ReleaseCheckerPort.newerThan` | 5 s | FR16.3 |
| `GoogleAuthPort.signIn` | 5 minutes | waits on the browser; ends as `CANCELLED` |
| `GoogleAuthPort.accessToken` and every Google call (`copyTemplate`, `writeRow`, `finish`, `markFinished`, `mostRecentEarlier`, `moveToTrash`, `checkAccess`, `read`) | 30 s each | working value |
| `GoogleAuthPort.signOut`, `SlideWriterPort.link` | local | no network |

Notes on the ports:

- `GoogleAuthPort` is used by the other `google` implementations and by the wizard's sign-in step. `HymnStorePort` is called by the hymn editor in `app`. Neither has a user inside `core` today, which `components.md` already recorded; this stage gives each its methods.
- A port never returns a Google, ESV or shared-library type. Every type it returns is defined in `core` (Domain Design ADR-001).
- **Setup checks use candidate values** (R-01). The wizard (FR2.4) and Replace setup file (FR2.9) pass the key, token and repository from the new file into `checkKey` and `checkToken`, so nothing is saved and the old keys stay in use until both checks pass. `SetupService` then saves through `SecretStore`.
- **A row write is atomic and idempotent** (R-02). `MakeSlidesRun` keeps each `writeRow` result and passes them in row order to `finish`, so the order of slides never depends on hidden state in `SlideWriter`.

## C2. Failure kinds (`core`, U2)

Every port converts what its service returns into one of these. Nothing else crosses a port (Q5). The caller decides the action from the call plus the kind, not the kind alone.

```java
public final class PortFailure extends Exception {
  public enum Kind {
    ConnectionLost,   // the probe failed, or a real network failure (not a timeout)
    TimedOut,         // a call exceeded its time limit; its own kind, not ConnectionLost
    SignInExpired,    // Google sign-in is no longer valid
    KeyRejected,      // ESV key, library token or Google client refused or disabled
    RateLimited,      // carries a wait hint (Retry-After or x-ratelimit-reset); retry
    ServerBusy,       // a 5xx; retry
    NotFound,         // a passage or a single hymn or file is not there
    AccessDenied,     // the template is not shared or does not allow copying
    Conflict,         // first save wins: stale SHA, or the new hymn id already exists
    DriveFull,        // Google Drive storage only; stop the run, no retry
    Malformed,        // a file or reply that cannot be parsed
    NeedsAppUpdate,   // a library or setup file parses as too new; never raised by Google or ESV
    TooLarge,         // the request is over a service limit (for example more than 500 verses); plain reason, no retry
    Unexpected        // anything else, with a code for the support details
  }
  Kind kind();
  Optional<Duration> waitHint();  // set only for RateLimited
  String code();                  // stable code for the error dialog and support details
}
```

Rules for the kinds (Q5, Q6):

- `NeedsAppUpdate` is distinct from `Malformed`. `SignInExpired` and `KeyRejected` stay separate. There is no `Cancelled` kind: cancelling a sign-in is the normal result `CANCELLED`, not a failure.
- `TooLarge` is added at this review to carry the FR5.11 plain reason for a request over 500 verses, which no earlier kind fits.
- `Conflict` covers a GitHub 409. A 422 is `Conflict` only when the body says the file already exists or the SHA was not supplied. Any other 422 is `Unexpected`.
- `DriveFull` is Google Drive storage only. A local disk-full is handled inside `ServiceStore` and never crosses a port.
- A timeout is `TimedOut`. `ConnectionLost` stops a Make slides run, and is used only when the probe failed or the error is a real network failure.
- **Retry:** `RateLimited`, `ServerBusy` and `TimedOut` on `writeRow` retry up to 3 times with increasing waits (FR12.8). `copyTemplate` is never retried (R-13): any failure of the copy stops the run. Other Google calls are not retried either, and their failures are handled as the table says. When a `RateLimited` wait hint is longer than 60 s, the retries stop and the call counts as failed. A retry is safe only because `writeRow` is atomic and idempotent (C1).

How each caller treats a kind. Only the combinations that can reach a user or change a run are listed; any other kind from a call is `Unexpected` to the caller and shows the error dialog.

| Call | Kind | Caller's action |
|---|---|---|
| `ConnectivityProbe.isConnected` | (no failure) | `false` means not connected; `ConnectionMonitor` re-checks every 5 s |
| `ReleaseCheckerPort.newerThan` | any | Stay silent and carry on (FR16.5) |
| `LibrarySyncPort.refresh` | `TimedOut`, `ConnectionLost`, `ServerBusy`, `RateLimited` | Keep the cached copy and show "Couldn't check for hymn updates"; no cache gives the Try again screen (FR1.4) |
| `LibrarySyncPort.refresh` | `KeyRejected` | "Ask the developer for a new setup file." (FR15.5) |
| `LibrarySyncPort.refresh` | `NeedsAppUpdate` | Keep the cached copy, tell the user to update (the contract of `refresh` in C1) |
| `LibrarySyncPort.firstDownload`, `checkToken`; `BibleSource.checkKey`; `GoogleAuthPort.signIn` (in the wizard or Replace setup file) | `ConnectionLost`, `TimedOut` | Show the connection cover and stay on the same step with what was typed (FR1.6). For `signIn`, `TimedOut` is not raised: after 5 minutes it returns `CANCELLED` |
| `LibrarySyncPort.checkToken`, `BibleSource.checkKey` | `KeyRejected` | Name the part that failed in plain words and keep Next off; the old keys stay in use (FR2.4, FR2.9) |
| `GoogleAuthPort.signIn` | `CANCELLED` (a result) | Stay on the sign-in step with no error |
| `GoogleAuthPort.signIn` | `KeyRejected` | "This setup file doesn't work with Google sign-in. Ask the developer for a new setup file." (FR2.7) |
| `LibrarySyncPort.downloadAllToTemporary`, `swapIn` | any | Nothing changes; "Couldn't reload the hymns. Nothing was changed." (FR14.5) |
| `HymnStorePort.save`, `create`, `delete` | `Conflict` | "Someone else saved this hymn first…" and the typed text stays in the row (FR7.4) |
| `HymnStorePort.*` | `KeyRejected` | Refuse the save or delete with "Ask the developer for a new setup file." (FR15.5); the typed text stays |
| `HymnStorePort.*` | `ConnectionLost`, `TimedOut` | The action ends as Problem with "The internet connection was lost" or a timeout message (FR1.6) |
| `HymnStorePort.*` | `ServerBusy`, `RateLimited`, `Malformed`, `Unexpected` | The action ends as Problem with a plain reason; the typed text stays; no retry |
| `TemplateReaderPort.checkAccess`; `TemplateCopyPort.copyTemplate` | `AccessDenied`, `NotFound` | The run STOPS with "Ask the developer to share the slide template with your Google account and allow copying." (FR12.3, FR12.6) |
| `TemplateCopyPort.copyTemplate` | any failure, including `TimedOut`, `RateLimited` and `ServerBusy` (never retried) | The run STOPS (FR12.3: a template copy failure stops the run); the rows not yet done show Skipped |
| `SlideWriterPort.finish`, `markFinished` | any failure | The run STOPS (FR12.3); the rows not yet done show Skipped |
| `SlideWriterPort.writeRow` | `RateLimited`, `ServerBusy`, `TimedOut` | Retry up to 3 times, then that row is Problem and its slides are left out (FR12.8) |
| `SlideWriterPort.writeRow` | `Unexpected`, `Malformed` | That row is Problem; the run goes on |
| `SlidesCleanerPort.*` | any | A Trash failure (last week's file, a half-built file or a leftover) is reported in the summary and never changes the outcome of a FINISHED run; a half-built file that cannot be trashed is remembered locally and trashed on the next successful run (FR12.4, FR12.5) |
| End of every FINISHED run (after `markFinished`) | (a step, not a failure) | `MakeSlidesRun` calls `leftoversInProgress(justMade)` and moves each file it returns, and each remembered half-built file id, to the Trash with `moveToTrash`, so an orphan copy from any cause is cleaned up (R-13). A failure of this step is a notice in the summary and changes nothing else. |
| any Make slides call | `ConnectionLost` | Stop the run; the rows not yet done show Skipped (FR1.6, FR12.3) |
| any Make slides call | `SignInExpired` | Stop the run and ask the user to sign in again (FR15.5) |
| any Make slides call | `DriveFull` | Stop the run with "Your Google Drive is full" (FR12.8) |
| `BibleSource.passage` | `NotFound` | The row shows "We couldn't find '…'" and is left out (FR5.10) |
| `BibleSource.passage` | `KeyRejected` | That row is Problem with "Ask the developer for a new setup file." (FR15.5); the run goes on |
| `BibleSource.passage` | `TooLarge`, `RateLimited` | The row shows a plain reason and the run continues (FR5.11) |
| `BibleSource.passage` | `TimedOut`, `ServerBusy` | That row is Problem with "Couldn't get the passage. Try Make slides again."; the run goes on (R-14) |
| `BibleSource.passage` | `ConnectionLost` | Stop the run; the rows not yet done show Skipped (FR1.6, FR12.3) |
| `BibleSource.passage` | `Unexpected`, `Malformed` | That row is Problem with a plain reason; the run goes on |

## C3. The run listener (U2 shape, U4 rules)

`MakeSlidesRun` calls the listener from its own background thread with one event for each row and one for the end (Q6). `ScreenViews` moves each event to the Swing thread. The listener holds no run rules.

```java
public interface RunListener {
  void rowStarted(RowId row);
  void rowDone(RowId row);
  void rowProblem(RowId row, String plainReason);
  void rowSkipped(RowId row);              // only after the run has stopped (FR12.3)
  void rowToDoInGoogleSlides(RowId row, String instruction);  // Chinese Monthly Scripture
  void runEnded(RunOutcome outcome);       // FINISHED or STOPPED, with the summary
}
public record RunOutcome(
    Kind kind,
    Optional<URI> link,                        // empty for a STOPPED run
    Optional<PortFailure.Kind> stopReason,     // empty for a FINISHED run
    List<String> notices) {                    // for example a Trash failure, or the half-built file's location (FR12.5)
  public enum Kind { FINISHED, STOPPED }
}
```

Rules: the count shown equals the rows no longer waiting or in progress over all rows. A run that is STOPPED reports `rowSkipped` for every row not yet done. A run that FINISHED may have Problem rows, and its slides are left out. A STOPPED run is never marked finished. Only a FINISHED run with no Problem rows moves last week's PPT to the Trash (FR12.4).

## C4. `SlidePlan` (U2 freezes the closed shape)

The layouts produce it, and `SlideWriterPort` writes it (design document section 9: the seam between layout and Google). U2 fixes this shape for one passage row, and it already includes the hymn background (R-06). U3 adds content to it but does not change its fields, and an addition is a U2 change (C1 rules).

```java
public record SlidePlan(String planId, String templateId, List<PlannedRow> rows) {}
public record PlannedRow(RowId row, List<PlannedSlide> slides) {}
public record PlannedSlide(
    String templateTag,                       // e.g. "autoppt:title-text main", "autoppt:offering", "autoppt:hymn-background"
    OptionalInt backgroundIndex,              // set only when templateTag is "autoppt:hymn-background": the index
                                              // within the pool, as TemplateInfo.hymnBackgrounds lists it
    Map<String, String> replacements,         // placeholder name -> text, e.g. "{Title}", "{Text}"
    List<StyledRange> styledRanges,           // see Style in C5
    String notes) {}                          // speaker notes; empty except the Chinese reminder
public record StyledRange(String placeholder, int start, int end, Style style) {}
```

Rules: `replacements` keys are only placeholders the template declares (`{Title}`, `{Text}`, the schedule roles, `{Date}`). There is no `{Reference}`: the reference is the last paragraph of `{Text}` (FR5.1, FR5.12). A row of several slides has one `PlannedSlide` for each. A hymn's slides share one `backgroundIndex` chosen by `HymnLayout`, which picks at random with no repeat in a service until the pool is used up (H6). `SlideWriter` finds the slide to duplicate by the tag, and by the index within the pool for hymn backgrounds.

## C5. Shared data types (`core`, U2)

These types are defined in `core` and cross the ports (ADR-001). The owning component creates and keeps the data. Shape only; the full types are Functional Design's.

| Type | Owner component | Fields |
|---|---|---|
| `Hymn` | `LibrarySync` | `hymnId`, `title`, `language`, `aliases`, `arrangement`, `blocks`, `updated`, `updatedBy`, `sha` |
| `LibraryFile` | `LibrarySync` | `path`, `sha`, `schemaVersion`, `content` (the file's UTF-8 text, so `Configuration` can read the YAML) |
| `RefreshResult` | `LibrarySync` | `changedPaths`, `skipped` (a list of `SkippedFile`), `tooNew` (paths of files too new for this app) |
| `SkippedFile` | `LibrarySync` | `path`, `reason` (`Malformed` or `NeedsAppUpdate`) |
| `TemporaryLibrary` | `LibrarySync` | An opaque handle to a downloaded copy: `hymnCount`, `skipped`; it discards itself when closed without `swapIn` |
| `ProgressListener` | `core` | `progress(double fraction)`, a fraction from 0 to 1, called from the downloading thread |
| `Version` | `ReleaseChecker` | `major`, `minor`, `patch`, compared in that order (FR16.3) |
| `ReleaseInfo` | `ReleaseChecker` | `version`, `downloadUrls` (one for each platform) |
| `AccessToken` | `GoogleAuth` | `value`, `expiresAt`. The value is given to the `Redactor` and never logged |
| `Passage` | `EsvClient` | `reference`, `canonicalReference`, `verses` (each with its number and text) |
| `SlidesFile` | `SlideWriter` | `slidesId`, `link`, `state` (`IN_PROGRESS` or `FINISHED`) |
| `SlideRef` | `SlideWriter` | `objectId`, the id of one slide in a copy |
| `RowId` | `ServiceBuilder` | `value`, a stable string for one row of the service |
| `TemplateInfo` | `TemplateReader` | `presentationId`, `slides` (each with `objectId`, `tags` as written, `placeholders`, `fontsInUse`), `scheduleRoles`, `hymnBackgrounds` |
| `HymnBackground` | `TemplateReader` | `index` (its position within the pool, from 0), `objectId` |
| `ScheduleRole` | `TemplateReader` | `label` (the left-column text), `placeholderKey` (the key `SimpleSlideLayout` uses in `PlannedSlide.replacements`) |
| `Style` | `core` | One of: `Bold`, `Italic`, `Superscript`, `HangingIndent(start, firstLine)`, `AlignEnd`, `Link(url)`, `BulletList`, `NumberedList` (FR5.8, FR5.9, FR9.1, FR9.6) |
| `SlideType`, `Preset`, `LayoutSettings`, `FontMap` | `Configuration` | as C7 |

`TemplateInfo.slides` lists tags as written, including duplicates, so `TemplateChecker` can report a tag that is not unique (FR13.1).

## C6. The hymn file (shared with the developer; U2 reads, U5 writes)

```yaml
# shared-schema: hymns/<id>.md — front matter, then blocks. File name = <id>.md and never changes.
hymn-file:
  frontMatter:
    schemaVersion: 1            # whole number (new: Q4 applies to every file)
    id: love-incarnate          # lowercase ASCII slug of the title; "hymn-" + short random id if no usable letters
    title: Love Incarnate
    language: en                # en | zh-Hant later
    aliases: []
    arrangement: [v1, c, v2, c] # optional; when absent, blocks appear in file order
    updated: 2026-11-16
    updatedBy: Grace            # the typed user name, not verified
  blocks:                       # after the front matter, each starting with a header line like [v1] or [c]
    - header: "[v1]"
      lines: ["Love incarnate, Love divine", "..."]
rules:
  - Read as UTF-8; tolerate a byte-order mark and CRLF line ends (FR7.10).
  - A malformed file is skipped and reported; every valid hymn still loads.
  - A file whose schemaVersion is higher than the app knows is skipped and reported, and a cached copy is kept.
  - The id is derived from the title; a number is added when the id exists (love-incarnate-2); clashes are checked case-insensitively (FR7.6).
  - The save message reads "Update hymn: <title> (by <name>)" (FR7.3).
```

## C7. Configuration files (shared with the developer; U2 reads)

```yaml
# shared-schema: config/ in the shared library. Each file has schemaVersion. Read-only in the app (FR13.3).
config:
  app.yaml:
    schemaVersion: 1
    templates:                 # a list, so a Chinese template can be added later (FR13.6)
      - id: "1Njchznjkd2toS1DqIvAStjP0w4gyMNq9S8j1q8v9U_E"
        language: en
        presentationId: "1Njchznjkd2toS1DqIvAStjP0w4gyMNq9S8j1q8v9U_E"
  slide-types.yaml:
    schemaVersion: 1
    types:                     # "+ Add slide" is built from this list (FR3.5)
      - tag: "autoppt:opening-passage"
        layoutKind: passage    # passage | hymn | text-blocks | quote | header | static | schedule | title-only
        editor: passage        # passage | hymn | text-blocks | quote | schedule | header | title-only
        defaultTitle: null
  presets/normal.yaml: { schemaVersion: 1, rows: [ { type: "autoppt:opening-passage" } ] }
  presets/holy-communion.yaml: { schemaVersion: 1, rows: [] }
  layout.yaml:
    schemaVersion: 1
    verseGap: 0                # set by spike S1
    safetyMargin: 0            # set by spike S1
    emptyRoleText: "N/A"
    sourceIndent: 0
  fonts.yaml:
    schemaVersion: 1
    fonts:                     # every font used in a placeholder box needs an entry (FR13.4)
      "Century Gothic": "TeX Gyre Adventor"
rules:
  - A too-new app.yaml or slide-types.yaml blocks that configuration; the app keeps the cached copy and tells the user to update.
  - A font in a placeholder box with no entry in fonts.yaml is an error that Check template reports; fonts in fixed static text are not checked.
```

## C8. The setup file and `secrets.json` (U2)

```yaml
# shared-schema: the setup file the developer hands over, and the secrets file the app saves.
setup-file:
  schemaVersion: 1
  googleClientId: string
  googleClientSecret: string
  esvKey: string
  libraryToken: string          # shown to people only as "setup file"; never as the word token
  libraryRepository: string
secrets-file:                   # secrets.json in the OS app-data folder; only SecretStore writes it
  schemaVersion: 1
  googleClientId: string
  googleClientSecret: string
  esvKey: string
  libraryToken: string
  libraryRepository: string
  googleRefreshToken: string    # added by GoogleAuth through SecretStore
rules:
  - SecretStore is the only writer: write a temporary file, then rename. macOS file mode 0600; Windows access list limited to the current user.
  - A setup file with a higher schemaVersion is refused with an update message (NeedsAppUpdate).
  - Every secret value is given to the Redactor before it is used. No secret reaches the log or the support details.
```

## C9. The service file and the settings file (`core`; app-data folder)

```yaml
# shared-schema: local files, never shared. Written by temporary file and rename. A full disk keeps the previous good file (NFR11).
service-file:                   # owner: U4 (ServiceStore)
  schemaVersion: 1
  serviceDate: date
  preset: string
  rows:
    - id: string
      type: string              # a slide-type tag
      settings: object          # hymn id plus local edits, reference, text blocks, quote and source, schedule names, or a title
settings-file:                  # owner: U2 holds the name, U4 adds the rest (UserSettings)
  schemaVersion: 1
  userName: string
  theme: light | dark
  deleteLastWeeks: boolean      # on by default
  windowBounds: { x: int, y: int, width: int, height: int }
  nameSuggestions: [string]
rules:
  - A file with a higher schemaVersion than the app knows is not overwritten; the app tells the user to update.
  - A local disk-full is handled inside ServiceStore and never crosses a port.
```

## C10. The template contract (U2 reads, U6 checks)

The template (Google Slides v3, id `1Njchznjkd2toS1DqIvAStjP0w4gyMNq9S8j1q8v9U_E`, 34 slides) is edited by hand by the developer. The tags are not in its speaker notes yet. `slide-types.yaml` holds this list, and `Check template` tests it (Q8; FR13.1, FR13.2, FR13.4).

| Slides | Purpose | Tag | Required placeholders |
|---|---|---|---|
| 0 | Opening Passage | `autoppt:opening-passage` | `{Text}` (28 pt, left). No title. The reference is the last paragraph of `{Text}`. |
| 1 to 5, 13 | Title plus text layouts used by headers, scripture, announcements, catechism, Spiritual Wisdom and Chinese Monthly Scripture | `autoppt:title-text <layout>`: the words `autoppt:title-text`, a space, and a layout name, one tag for each layout | `{Title}`, `{Text}` |
| 6 to 12, 15, 16 | Static slides (Lord's Prayer, Holy Communion, Apostles' Creed, Doxology, Benediction) | `autoppt:static <name>`: the words `autoppt:static`, a space, and a name | none |
| 14 | Offering: fixed "OFFERING" heading and a passage box | `autoppt:offering` | `{Text}` (one font size) |
| 17 | Next Week's Schedule table | `autoppt:schedule` | `{Date}` and the role placeholders |
| 18 to 33 | Hymn backgrounds | `autoppt:hymn-background` | `{Title}`, `{Text}`, styled like the title plus text layouts |

Rules for the template contract:

- **Tags** are unique, except `autoppt:hymn-background`, which marks a pool (FR13.1). One spelling is used everywhere: `autoppt:<kind>` or `autoppt:<kind> <name>`, with one space before the name.
- **A hymn background is identified by its index within the pool** (0 to 15 for slides 18 to 33), in template order. `TemplateReader` lists them with their slide object ids in `TemplateInfo.hymnBackgrounds`, and `PlannedSlide.backgroundIndex` names the chosen one (C4, R-06).
- **Placeholders:** each tagged slide has the placeholders its layout kind needs. There is no `{Reference}` placeholder (FR13.2).
- **Schedule roles are read from the template's schedule table** and are not a closed list (FR10.1). Today's names (the left-column text of slide 17) are only the current example. A new role in the template becomes a new field in the Schedule editor with no code change. Each role is read with its label and its placeholder key (`ScheduleRole`, C5).
- **Measurement fonts:** every font used in a placeholder box needs a measurement font in `fonts.yaml`, and a font without one is an error. Fonts in fixed static text are never measured and are not checked (FR13.4; NFR15).
- **Notes:** the app clears speaker notes on every generated slide, so tags never reach Presenter View, and writes back only the Chinese Monthly Scripture reminder (FR12.2).
- **Marker on made slides:** the copy carries a Drive app property with key `autoppt`. At copy time its value is `in-progress`, and only after a run whose every row was attempted does the app set the value to `slides` (the app marker). A stopped run's copy stays `in-progress`, so it is never taken for last week's file (R-05, FR12.5). The design document names the value `deck`; this contract uses `slides` to follow the wording rule. See Assumptions.

## C11. Third-party calls the app depends on (U2 pins the tables; each Unit that implements a port keeps its table true)

Each row states what we send, what we read, how an error maps to a failure kind (by response reason or body, not by status alone), and the caller's action (C2). A fake must behave as the row says. Sources are the providers' own documentation. Time limits are the whole-call limits in C1.

### ESV passage text service (`esv`; source: api.esv.org)

| Call | We send | We read | Error mapping | Caller's action |
|---|---|---|---|---|
| Passage text | `GET /v3/passage/text/` with the header `Authorization: Token <key>`, `q=<one piece of the field>`, headings, footnotes, passage references, short copyright and Selahs off, verse numbers on, `indent-poetry` off, `line-length=0` | `passage_meta[].canonical` (the reference line) and the passage text | An empty `passages` list, a reply with no passage text, or an error body saying the reference was not understood is `NotFound`. A reply saying the query is over 500 verses or half a book is `TooLarge`. 401 or 403 is `KeyRejected`. 429 is `RateLimited` (the throttle reply carries a wait hint). 5xx is `ServerBusy`. | `NotFound`: row error "We couldn't find '…'". `TooLarge`: a plain reason on the row (FR5.11). `KeyRejected`: row Problem, "Ask the developer for a new setup file." `RateLimited`: a plain reason, the run goes on. |
| Key check (a candidate key) | One inexpensive passage request with the candidate key | Any 200 | 401 or 403 is `KeyRejected` | Names the failing part in the wizard (FR2.4) |

Pinned limits (FR5.11): 500 verses per query or half a book, 5,000 queries a day, 1,000 an hour, 60 a minute. Passage text is kept in memory for the session only and never stored.

### Google sign-in (`google`; source: Google OAuth documentation)

| Call | We send | We read | Error mapping | Caller's action |
|---|---|---|---|---|
| Sign in | Opens the system browser; a loopback redirect on `127.0.0.1`; scopes `presentations` and `drive` | An authorization code, then an access token and a refresh token | `invalid_client` or a disabled or deleted client is `KeyRejected`. A user who closes or refuses the consent screen (`access_denied`), or 5 minutes without an answer, is the result `CANCELLED`. | `KeyRejected`: "This setup file doesn't work with Google sign-in. Ask the developer for a new setup file." (FR2.7). `CANCELLED`: stay on the sign-in step, no error. |
| Refresh the access token | The refresh token | A new access token | `invalid_grant` (revoked, expired or signed out) is `SignInExpired`. A network failure is `ConnectionLost`. | A run stops and asks the user to sign in again (FR15.5). |

### Google Drive (`google`; source: Drive API documentation)

| Call | We send | We read | Error mapping | Caller's action |
|---|---|---|---|---|
| Copy the template | `files.copy` of the template id, setting the app property `autoppt` to `in-progress` on the copy | The new file id and link | 403 or 404 on the template is `AccessDenied` or `NotFound`. 403 with reason `storageQuotaExceeded` is `DriveFull`. 403 with reason `rateLimitExceeded` or `userRateLimitExceeded`, and 429, are `RateLimited`. 5xx is `ServerBusy`. 401 is `SignInExpired`. | A failed copy STOPS the run (FR12.3). `AccessDenied` or `NotFound` gives the share-the-template message (FR12.6). `DriveFull` stops with no retry. The copy is never retried, so a rate limit, a 5xx or a timeout also stops the run (R-13). |
| Mark finished | `files.update` setting the app property `autoppt` to `slides` | Confirmation | As above | Failure stops the run's finish and is reported; the copy stays `in-progress` and is not taken for last week's file |
| Find last week's | `files.list` for files owned by the user with the app property `autoppt` equal to `slides`, newest first, excluding the one just made | File ids and names | As above | A failure never changes the outcome of a finished run; the summary says last week's slides were not moved |
| Find leftovers | `files.list` for files owned by the user with the app property `autoppt` equal to `in-progress`, excluding the one just made | File ids and names | As above | Each file is moved to the Trash by id at the end of a FINISHED run. A failure is a notice and changes nothing else (R-13). |
| Move to Trash | `files.update` setting `trashed` on one file id | Confirmation | As above. 404 means already gone and counts as success. | A failure never changes the outcome of a finished run. A half-built file that cannot be trashed is left, reported, and remembered locally for the next successful run (FR12.5). |

### Google Slides (`google`; source: Slides API documentation)

| Call | We send | We read | Error mapping | Caller's action |
|---|---|---|---|---|
| Read the template | `presentations.get` | Slides, speaker notes, placeholders and fonts in placeholder boxes | 403 or 404 is `AccessDenied` or `NotFound`. | Template message (FR12.6) |
| Write one row | One `batchUpdate` for the whole row (`duplicateObject` with object ids derived from the row id and position, `replaceAllText`, text style and paragraph style requests). A batchUpdate is applied entirely or not at all. | The reply for each request | A Slides quota 429 is `RateLimited`. 5xx is `ServerBusy`. 401 is `SignInExpired`. | Retry up to 3 times with increasing waits. A retry first reads the copy: slides already present under the derived ids mean the first attempt was applied, and no second write is made. After 3 failures that row is Problem (FR12.8). |
| Finish | One `batchUpdate` with `updateSlidesPosition` for each written slide in row order, then `deleteObject` for each original template slide | Confirmation | As above | A failure stops the run (FR12.3) |

### Shared library file service (`library`; source: the file service's contents documentation)

| Call | We send | We read | Error mapping | Caller's action |
|---|---|---|---|---|
| List the library | The tree listing of the repository | Each file's path and SHA | 404 on the private repository listing is `KeyRejected`. 401 is `KeyRejected`. 403 with rate-limit headers, and 429, are `RateLimited`. 403 saying the token cannot access the resource is `KeyRejected`. 5xx is `ServerBusy`. | Refresh keeps the cache on failure (FR1.4) |
| Read a file | The file's blob by SHA, only for files whose SHA changed | The file content | 404 for one hymn while the listing works is `NotFound`. Others as above. | A hymn that is `NotFound` or `Malformed` is skipped and reported |
| Write or add a hymn | A contents write with the last-known SHA (none for a new hymn) and a message naming the hymn and the user | The new SHA | 409 is `Conflict`. 422 is `Conflict` only when the body says the file already exists or the SHA was not supplied; any other 422 is `Unexpected`. 401 and a 403 saying the token cannot access the resource are `KeyRejected`. 403 with rate-limit headers, and 429, are `RateLimited`. 5xx is `ServerBusy`. | `Conflict`: "Someone else saved this hymn first…" (FR7.4). `KeyRejected`: "Ask the developer for a new setup file." The new SHA is recorded in the cache (C1, R-11). |
| Delete a hymn | A contents delete with the last-known SHA | Confirmation | 409 is `Conflict`. 404 is `NotFound` (already gone). Others as above. | Same message as a stale save (FR7.7) |
| Key check (a candidate token and repository) | One inexpensive listing request with the candidate values | Any 200 | 401, 404 on the listing, and 403 saying the token cannot access the resource are `KeyRejected` | Names the failing part in the wizard (FR2.4) |

### Update check and connectivity (`library`, `google`)

| Call | We send | We read | Error mapping | Caller's action |
|---|---|---|---|---|
| Latest release | One unauthenticated request to the public latest-release endpoint of the app's repository | The version and the download links | Any failure, including `TimedOut`, is ignored. | Silent (FR16.5). Only a published release counts, and newer means a higher major.minor.patch (FR16.3). |
| Connectivity | One HTTPS request to a Google endpoint | Any answer | No answer, a captive portal or a blocked network is "not connected". | `ConnectionMonitor` re-checks every 5 s (FR1.2, FR1.3) |

## Sources

- [Q1] The documents' answers: no public API, the ten ports, the hymn file, the library layout, file versioning, the setup file, the template tags, first-save-wins, and the retry rule.
- [Q2] Each contract is a fenced block: `java` for ports and types, `yaml` for data files.
- [Q3] The Unit that first builds a contract owns it. U2 owns every port signature and the closed `SlidePlan` shape.
- [Q4] A whole-number `schemaVersion` in every file, with the too-new rules.
- [Q5] The failure kinds, including `NeedsAppUpdate`, with the 409 and 422 rules and the wait hint.
- [Q6] Blocking calls, `TimedOut` as its own kind, and the `RunListener`.
- [Q7] The third-party call tables with reason-based error mapping and the caller's action.
- [Q8] The template tags, schedule roles read from the template, and the measurement-font rule.
- [scope] Requirement ids come from requirements.md; component and port names from components.md. The fixes for R-01 to R-12 of the first review are the developer's confirmed change request.

## Assumptions & Open Questions

- [assumption] The time limits not stated in the requirements are working values: 10 s for an ESV call and the key and token checks, 30 s for a Google call or a hymn write, 60 s for the first full download and for Reload all hymns (chosen by the developer so a slow but working connection does not fail while NFR3's 30 s stays the target), and 5 minutes for sign-in. The 5 s limits for the probe, the library refresh and the update check are from FR1.2, FR1.4 and FR16.3. Construction may change them.
- [assumption] `copyTemplate` is never retried (developer's decision, R-13). FR12.8 says to retry a rate limit or server error up to 3 times, but its wording is about making a row, and a copy has no row. So a rate limit or a 5xx on the copy stops the run, and the operator presses Make slides again. The orphan-copy cleanup at the end of every FINISHED run covers a copy that a lost reply left behind. A FINISHED run is one where every row was attempted and `markFinished` succeeded.
- [assumption] The Reload all hymns download uses the same 60 s limit as the first full download, because both fetch every hymn.
- [assumption] The port method names and types are working names that Functional Design and Construction refine. A change to a signature is a U2 change (ownership rules).
- [assumption] The Drive app property is key `autoppt`, value `in-progress` while a copy is being built and `slides` once a run has finished. The design document used the value `deck`; this contract uses `slides` to follow the wording rule. No release has shipped, so no existing file is affected.
- [assumption] A Drive app property is visible only to the OAuth client ID that wrote it (Drive documentation). So the marker is tied to the Google client in the setup file, not to the app alone: a new setup file with a different client ID would stop the app finding slides made under the old one, and last week's file would not be moved. The developer asked for this to be noted as tied to the app and not the client ID, but the provider documentation says otherwise, so this contract states the limit. If it matters, the shared `properties` field, which every client can read, can be used instead; that is a change to this contract.
- [assumption] `SlidePlan` here is a record with `planId`, `templateId` and a list of nested `PlannedRow` and `PlannedSlide`, and `PlannedSlide` carries `backgroundIndex`. `components.md` lists the `SlidePlan` entity with identifier `planId` and a flat list of attributes (`rows`, `slides`, `templateId`, `templateTag`, `replacements`, `styledRanges`, `notes`). The nesting is the same data, and `planId` is kept. Functional Design updates the entity listing to match.
- [assumption] `TooLarge` is a new failure kind added at this review (14 kinds in all), so the FR5.11 plain reason for a request over 500 verses has a kind. It goes beyond the list in your Q5 answer.
- [assumption] `refresh()` keeps every readable file it downloaded, even when other files are too new or malformed. It throws `NeedsAppUpdate` only when a too-new `app.yaml` or `slide-types.yaml` leaves no readable cached copy of that file.
- Open: the exact request bodies for the Slides `batchUpdate` depend on spike S2 (the hanging indent and superscript method) and S1 (line spacing and padding). They are set when U1 records its results, and the contract for `SlideWriterPort` is unchanged.
- Open: which Unit owns the failure code strings in the error dialog (`PortFailure.code`). `ErrorModel` (U4) uses them. U2 defines the field only.

| Contract | Question | Blocks |
|---|---|---|
| C11 Slides | The exact `batchUpdate` requests for the hanging indent | U3, after spike S2 |
