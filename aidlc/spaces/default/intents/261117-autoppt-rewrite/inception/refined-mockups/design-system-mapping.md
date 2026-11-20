# Design System Mapping: AutoPPT Rewrite

Inputs: `wireframes.md` and `user-flow.md` (rough mockups), `mockups.md`, `interaction-spec.md`, `stories.md`, `requirements.md`, `team-practices.md`, and `refined-mockups-questions.md` (Q1 to Q7). The design system is Java Swing with FlatLaf (design document D1; team-practices.md). There is no web component library. Class names below are the standard Swing classes. Where a choice is not yet decided it is marked "proposed" and is settled when the code is written.

## Themes and Colour (Q5)

| Item | Mapping | Status |
|---|---|---|
| Light theme | FlatLaf's light theme | decided (FR14.1) |
| Dark theme | FlatLaf's dark theme | decided (FR14.1) |
| Accent colour | The theme's default accent colour; no custom colour | decided (Q5) |
| Choice of theme | A radio button group in Settings (FR14.1), applied at once | decided (Q6) |
| State marks | A symbol and a word for every state, as well as any colour: `✔ Done`, `⚠ Problem`, `– Skipped`, `✎ To do in Google Slides`, `⟳ In progress`, `● Only on this computer` | decided (Q5; NFR8) |
| Text contrast | At least 4.5:1 in both themes, checked with a contrast measure | decided (NFR8) |
| Contrast of controls and borders | At least 3:1, following the framework's component template | proposed |

## Component Mapping

| Screen element | Swing class | Notes |
|---|---|---|
| Main window | `JFrame` | Opens at 1100 by 700, minimum 900 by 600, saves its size and position (Q2) |
| List and settings split | `JSplitPane` with a fixed left width of 320 | The divider is fixed in the first version (proposed) |
| Slide list | `JList` in a `JScrollPane`, with drag and drop through a `TransferHandler`, and visible Add, Remove, Move up and Move down `JButton`s under it (Q6) | A custom row renderer draws the handle, type and summary |
| Row warning mark | A symbol and a word in the row renderer | Q5 |
| Top bar | `JPanel` with a `JLabel` greeting, a date picker, `JComboBox` order, and `JButton`s | Make slides is the frame's default button |
| Date picker | A `JSpinner` with a date model, or a small date field with a drop-down calendar | proposed; decided when coding |
| Bottom bar | `JPanel` with `JButton` links | Check for updates, Settings, and a `JLabel` for the account |
| Settings panel | `JPanel` in a `JScrollPane` | Swapped by the row's slide type |
| Passage field | `JTextField` with a `JButton` labelled `?` | The `?` opens a `JPopupMenu` or `JDialog` with the help text, and replaces a hover tooltip, which a keyboard cannot reach (Q6) |
| Hymn picker | An editable, filtering `JComboBox` | Filters by title, other titles and first line |
| Lyrics area | `JTextArea` in a `JScrollPane` (Q4) | One text area with `[Verse 1]`-style headers |
| Text block editor | `JPanel` of blocks, each with a toolbar of `JToggleButton`s and a text area | Underline is not offered |
| Schedule name field | An editable `JComboBox` | One for each role in the template |
| Check box and radio group | `JCheckBox`, `JRadioButton` with a `ButtonGroup` | Space toggles |
| Wizard | A full window (`JFrame`) with a `CardLayout`, not a `JDialog` | One card for each step, with Back and Next; Escape is not bound (Q7); closing the window quits the app |
| Start-up screen | A small undecorated-title `JFrame` or `JDialog` | One `JLabel` for the status line |
| Progress dialog | A modal `JDialog` with a list of rows and a `JProgressBar` | Blocks the main window (Q3) |
| Confirmation and conflict dialogs | Modal `JDialog` with a message and buttons | Cancel is the default button |
| Error dialog | Modal `JDialog` | OK is the default button |
| Connection cover | A modal `JDialog` over the whole frame, or the frame's glass pane | Blocks all input behind it |
| Settings dialog | Modal `JDialog` | Includes the version label and the Reload all hymns `JButton`, which opens a confirmation `JDialog` with Cancel as the default (Q7), then a blocking progress `JDialog` and, on failure, an OK message `JDialog` (R-11 fix) |
| Tooltips and hints | `JToolTip` only for extra hints that nothing depends on, and visible labels or the `?` help button for anything needed | Important text is never tooltip-only |

## Layout Tokens (proposed)

| Token | Value | Use |
|---|---|---|
| Spacing unit | 8 pixels | Gaps between controls, padding in dialogs |
| Main window | 1100 by 700, minimum 900 by 600 | Q2 |
| Left list width | 320 pixels | Slide list |
| Dialog widths | 420 to 560 pixels, starting values finalised in code (Q6) | Start-up 420, confirmations 440, Settings 480, progress 520, wizard 560 |
| Type | The look-and-feel's default font and size | No custom font; the PPT's own fonts are in the template, not the app |
| Corner and border style | The theme's default | No custom styling |

## Wording Tokens

| Say | Never say |
|---|---|
| shared library | GitHub, repo |
| Check for updates | pull, sync |
| Save for everyone | commit, push |
| Delete for everyone | delete a file |
| Discard my changes | revert |
| Setup file | key file |
| Sign in with Google | OAuth |
| PPT, slides | deck |
| Copy details for support | stack trace dump |

Source: the plain-language vocabulary in the design document and the project rules (FR15.7; NFR18).

## Compliance With the Design System

- Every control is a standard Swing component styled by FlatLaf, so no custom painting is needed except the list row renderer and the progress row marks.
- Keyboard access, focus rings and default buttons come from the standard components. The accessibility checklist lists what must be checked on top of that (`accessibility-checklist.md`).
- The only decisions that are not yet fixed are the date picker's exact form, whether the divider can be dragged, and the contrast of controls. They are marked proposed above.

## Assumptions & Open Questions

None.
