# Accessibility Checklist: AutoPPT Rewrite

Inputs: `wireframes.md` and `user-flow.md` (rough mockups), `mockups.md`, `interaction-spec.md`, `design-system-mapping.md`, `stories.md`, `requirements.md`, `team-practices.md`, and `refined-mockups-questions.md` (Q1 to Q7).

## Target (decided)

- Every screen is fully usable by keyboard alone (NFR8; story US18.4).
- Text keeps a contrast ratio of at least 4.5:1 in both the light and dark themes (NFR8).
- Screen-reader support is not part of the first version, so no screen-reader announcement is required or checked (rough-mockups-questions.md Q2).
- Every state is shown with a symbol and a word as well as any colour (refined-mockups-questions.md Q5).

The checks for each screen are the four that a Swing window can be tested on: tab order, initial focus, the default button, and where focus returns after a dialog closes (decided, refined-mockups-questions.md Q6). Web page ideas such as heading levels and page regions do not apply to a Swing window, and screen readers are out of scope, so they are not checked. Items marked "proposed" are confirmed when the screens are built.

## Global Checks

| # | Check | How to pass | Status |
|---|---|---|---|
| G1 | Keyboard-only use | Every action on every screen can be done with Tab, Shift+Tab, Enter, Space, the arrow keys, Alt+Up, Alt+Down and Escape | decided |
| G2 | Visible focus | The focused control has a visible outline in both themes | proposed |
| G3 | Tab order | Tab follows the order in the Screen Checks table below | decided (Q6) |
| G4 | Text contrast | A contrast measure of 4.5:1 or more for text on its background, in both themes | decided |
| G5 | Not colour alone | Each state has a symbol and a word (`✔ Done`, `⚠ Problem`, `– Skipped`, `✎ To do in Google Slides`, `⟳ In progress`, `● Only on this computer`) | decided |
| G6 | Default buttons | Each dialog has the default button named in `interaction-spec.md`, and Enter presses it | decided |
| G7 | Escape | Each dialog follows the single Escape rule in `interaction-spec.md` | decided (Q6) |
| G8 | Labels | Every field has a visible label above it, not a tooltip alone | proposed |
| G9 | Disabled controls | A disabled control shows its reason as visible text, not only in a tooltip | proposed |
| G10 | Focus after a dialog | When a dialog closes, focus returns to the control that opened it | decided (Q6) |
| G11 | Window sizes | At 900 by 600 no control is hidden, and the right panel scrolls | decided (Q2) |
| G12 | Cover blocks input | While the connection cover shows, nothing behind it can take focus or input | decided (FR1.7) |

## Screen Checks

| Screen | Tab order | Initial focus | Default button | Where focus returns when it closes |
|---|---|---|---|---|
| Start-up: checking (M0a) | None; no control while checking | None; closing the window quits the app | None | Not applicable |
| Start-up: no connection (M0b) | Try again, then Quit | Try again | Try again (Escape does nothing) | Not applicable |
| Start-up: new version (M0c) | Download, then Continue | Continue | Continue (Escape means Continue) | The next start-up step or the main window |
| Start-up: library not available (M0d) | Try again, then Quit | Try again | Try again | Not applicable; Escape quits the app |
| Wizard step 1 (M1a) | Browse, Delete the setup file now, Back, Next | Browse | Next, once on (Escape does nothing) | Not applicable |
| Wizard step 2 (M1b) | Name field, Back, Next | Name field | Next, once on (Escape does nothing) | Not applicable |
| Wizard step 3 (M1c) | Sign in with Google, Back, Next | Sign in with Google | Sign in with Google; Next once sign-in succeeds (Escape does nothing) | Not applicable |
| Wizard step 4 (M1d) | Back, Finish | Finish, once on | Finish, once on (Escape does nothing) | The main window |
| Main window (M2) | Top bar (date, order, New service, Make slides), slide list, Add, Remove, Move up, Move down, settings panel, bottom bar | The slide list | Make slides | A dialog returns focus to the control that opened it |
| Editors (M3) | Fields in reading order, then the buttons of the panel | The first field of the panel | None, except in the New hymn dialog (Add hymn) | The `?` pop-up and New hymn dialog return focus to their opener |
| Progress dialog (M4) | Open in browser, Copy link, Close | None during a run; Close once the run ends | Close (once on) | The Make slides button |
| Error dialog (M5) | Copy details for support, Open log folder, OK | OK | OK | The control that was in use |
| Settings (M6) | Delete last week's slides, Your name, Theme, Replace setup file, Sign out of Google, Reload all hymns, Close | The first check box | Close | The Settings link in the bottom bar |
| Hymn confirmations (M7) | Cancel, then the action button | Cancel | Cancel (Escape cancels) | The button that opened it |
| Reload all hymns confirmation (M7) | Cancel, then Reload all hymns | Cancel | Cancel (Escape cancels) | The Reload all hymns button in Settings |
| Reload all hymns progress (M7) | Close, off until the download ends | None while it downloads | None while it downloads; Escape does nothing | The Reload all hymns button in Settings |
| Reload all hymns failure (M7) | OK | OK | OK (Escape closes it) | The Reload all hymns button in Settings |
| Hymn not saved (M7) | Check for updates, then Close | Check for updates | Check for updates | The control that was in use |
| Connection cover (M2d) | Try again, then Quit | Try again | Try again (Escape does nothing) | The control that had focus before the cover |
| AutoPPT is already open (M8a) | OK | OK | OK (Enter and Escape close it) | Not applicable; the second copy exits |
| Sign in again (M8b) | Sign in with Google, Back, Next | Sign in with Google | Sign in with Google; Next once sign-in succeeds (Escape does nothing) | The main window |
| Replace setup file result (M8c) | OK | OK | OK | The Settings dialog |

Each cell is a pass or fail check on the built screen. The four check types (tab order, initial focus, default button, focus return) are decided (Q6). The default buttons and Escape results follow the decided rules in `interaction-spec.md`. The tab order and initial focus values are proposals, confirmed when the screens are built, and so are the entries `interaction-spec.md` marks as proposed (such as the library-not-available default). The wizard Escape rule is decided (Q7).

## Out of Scope

- Screen-reader announcements, ARIA-style roles and labels for a screen reader (rough-mockups-questions.md Q2).
- A phone or tablet layout (design document D1).
- Language other than English (design document section 11).

## Assumptions & Open Questions

None.
