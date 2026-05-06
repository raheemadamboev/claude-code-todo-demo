# Handoff: ToDo App — Android Screens

## Overview
A simple personal ToDo app for Android. Three core flows:

1. **Onboarding** — first-launch screen with **3 horizontally swipeable feature cards** ending in a "Get started" CTA.
2. **ToDo List** — primary screen. Lists all tasks; supports swipe-to-complete / swipe-to-delete, drag-to-reorder, pull-to-refresh, animated check, and a Floating Action Button to add a new task. Tapping a row opens the **Detail** screen.
3. **ToDo Detail** — full task editor: title, notes, tag/category, priority, due date, completion toggle, delete.
4. **Support** — contact-the-developer screen with two action cards: **Phone** (tel: deeplink) and **Email** (mailto: deeplink).

The screens are **separate destinations** (no bottom tab bar). Navigation between Onboarding → List → Detail / Support is via push transitions and explicit back / done buttons.

## About the Design Files
The files in `source/` are **design references built in HTML/JSX as a clickable prototype**. They are not production code to copy directly — they target an in-browser React + Babel runtime and use a custom phone-shell wrapper for presentation.

Your job is to **recreate this design in the target Android codebase** using its established patterns and libraries:

- **If the project uses Jetpack Compose + Material 3** (recommended): map every component to its M3 equivalent (`Scaffold`, `LargeTopAppBar`, `Card`, `Checkbox`, `FloatingActionButton`, `HorizontalPager`, etc.). Use `MaterialTheme.colorScheme` for color and `MaterialTheme.typography` for type. Don't hard-code the hex values below — define them in your theme tokens.
- **If the project uses XML Views + Material Components**: use `MaterialToolbar`, `MaterialCardView`, `MaterialCheckBox`, `ExtendedFloatingActionButton`, `ViewPager2` for the onboarding pager, `ItemTouchHelper` for swipe & drag gestures, and `SwipeRefreshLayout` for pull-to-refresh.
- **If there's no Android codebase yet**: scaffold a new Compose project (Material 3, Kotlin, single-activity, NavHost-based navigation) — that's the modern default and most directly mirrors this design.

Open the prototype (`source/ToDo App.html`) in a browser to see exact motion and gesture behavior, then build the equivalents natively.

## Fidelity
**High-fidelity.** Final colors, typography, spacing, motion timings, and copy. Recreate the visuals pixel-faithfully, but **substitute the codebase's design system**: M3 tokens for color/type, M3 components for chrome. The exact hex values below are the reference palette — wire them into your theme, then reference them via tokens in code.

---

## Screens / Views

All screens are presented inside a 380×820 Android phone frame at design time. The app content area is the full screen minus the system status bar (24dp top inset) and gesture nav bar (32dp bottom inset).

### 1. Onboarding

**Purpose**: First-launch tour. User swipes through 3 feature cards, then taps "Get started" to enter the app.

**Layout** (top → bottom):
- **Status bar** — transparent, dark icons (light surface).
- **Skip button** — top-right, 16dp inset. Text-only, color = `onSurfaceVariant`. Hides on the last page.
- **Pager region** — fills the available height. Each page contains:
  - **Hero illustration**, vertically centered in the top 55% of the page. ~280×280dp. Uses the variation's `primaryContainer` / `tertiaryContainer` / `secondaryContainer` background tone, with a foreground SVG composition that conceptually represents the feature (a hand capturing a card, sorted stacks, a focus reticle). **Replace these with real illustrations** from your team's library; the SVGs in the prototype are placeholders.
  - **Title** — Display Small (36sp / Roboto 700). `onSurface`. Centered, max 2 lines.
  - **Body** — Body Large (16sp / Roboto 400, line-height 1.5). `onSurfaceVariant`. Centered, max 3 lines, side padding 32dp.
- **Pager dots** — 3 dots, 8dp circles, 8dp gap. Active dot is a 24dp pill (`primary`); inactive dots are `outline @ 32% opacity`.
- **Primary button** — full-width minus 24dp side padding. 56dp tall, fully rounded (28dp radius). `primary` background, `onPrimary` label. Label = "Next" on pages 1–2, "Get started" on page 3.

**Three feature pages** (swipe horizontally to advance):

| # | Title | Body | Tone |
|---|---|---|---|
| 1 | Capture every thought | Quick-add tasks with tags, due dates and notes — without breaking your flow. | `primaryContainer` |
| 2 | Stay on top of your day | Swipe to complete, drag to reorder, pull to refresh. The list adapts to you. | `tertiaryContainer` |
| 3 | Focus when it matters | Priorities, categories and clean details so the next step is always obvious. | `secondaryContainer` |

**Behavior**:
- Horizontal swipe between pages with snap. ViewPager2 / HorizontalPager.
- Cross-fade illustration; slide-in title/body (200ms, fast-out-slow-in).
- Skip and "Get started" both navigate to the List screen and persist `onboarded=true` so the screen never shows again.

---

### 2. ToDo List

**Purpose**: Browse, complete, reorder, swipe-delete, and add tasks.

**Layout** (top → bottom):
- **TopAppBar (Large)** — 112dp tall when expanded, collapses to 64dp on scroll. Background = `surface`. Title "Today" in Display Small (32sp Roboto 500), `onSurface`. Subtitle in Body Medium (14sp), `onSurfaceVariant`, e.g. "5 of 7 to do".
  - **Trailing icons**: Search, Help (opens Support screen). 24dp icons, 48dp touch targets, color = `onSurfaceVariant`.
- **List body** — `LazyColumn` / `RecyclerView`. Side padding 16dp; vertical gap between items 8dp.
- **Floating Action Button** — Extended FAB, bottom-right with 24dp inset from edges. Pill shape, 56dp tall. `primaryContainer` background, `onPrimaryContainer` foreground. Icon + label "Add". Lifts above the gesture nav bar.

**ToDo row component**:
- **Container**: `surfaceContainer` background, 20dp corner radius, 14dp vertical / 16dp horizontal padding. 1dp elevation.
- **Layout** (left → right):
  - **Drag handle** — visible on long-press. 16dp wide, `onSurfaceVariant @ 60%`.
  - **Animated checkbox** — 24dp circle. Empty = 2dp `outline` ring. Checked = filled with `primary`, white check-mark drawn with a 220ms spring scale-in (cubic-bezier(.34, 1.56, .64, 1)). Tapping toggles `done`; row title gets strikethrough + `onSurface @ 60% opacity` when done.
  - **Text column** (1fr): **Title** Body Large (16sp Roboto 500, `onSurface`). **Meta row** below in Body Small (12sp Roboto 400, `onSurfaceVariant`): tag chip + due-date pill + priority flag.
  - **Trailing chevron** — 16dp, `onSurfaceVariant @ 50%`.
- **Tag chip**: 8dp horizontal padding, 4dp vertical, fully rounded. Color comes from `TAG_COLORS_LIGHT` / `TAG_COLORS_DARK` keyed by `tagColor`.
- **Priority flag**: red (`error`) for high, amber for medium, hidden for low. 12dp icon + label.
- **Due-date pill**: Body Small with a 14dp clock icon prefix.

**Gestures & behavior**:
- **Tap row** → push Detail screen.
- **Swipe right** → reveal green "Complete" action; release past 60% to toggle done. The swipe-revealed background uses `primaryContainer`.
- **Swipe left** → reveal red "Delete" action (`errorContainer`). Release past 60% to delete (with undo snackbar — 4 seconds).
- **Long-press** → enter drag mode; row scales to 1.02 with elevated shadow; reorder via drag handle.
- **Pull-to-refresh** → standard M3 indicator above the list. Spins for 800ms then resolves (no real fetch in the design).
- **FAB tap** → opens an "Add task" bottom sheet (fields: title, tag, priority, due). The prototype only shows a placeholder; flesh out per your data layer.

**Sample data** (`source/todo-state.jsx`, `SAMPLE_TODOS`): 7 tasks across tags Work, Errand, Personal, Learn, Health, Home — covers the empty/half/done states.

---

### 3. ToDo Detail

**Purpose**: View and edit a single task.

**Layout** (top → bottom):
- **TopAppBar** — small variant, 64dp. Leading: back arrow. Trailing: overflow menu (Delete, Duplicate). Background = `surface`.
- **Hero block** — 24dp side padding, 24dp top, 16dp bottom.
  - **Animated check** — 28dp version of the list-row check. Inline left of the title.
  - **Title** — editable Headline Medium (28sp Roboto 500). `onSurface`. Multi-line. Strikethrough when done.
- **Meta chip row** — horizontally scrollable, 12dp gap. Three chips: tag (colored chip from palette), priority (`flag` icon + level), due date (`clock` icon + relative date). Tap any chip to edit it inline.
- **Section: Notes** — section header in Label Large (14sp Roboto 500, `onSurfaceVariant`, all-caps tracking 0.1). Multi-line `TextField` filled to `surfaceContainer`, 16dp radius, 16dp inner padding. Placeholder: "Add notes…".
- **Section: Activity** (optional / nice-to-have) — Body Medium meta lines: "Created 2 days ago", "Last edited just now". `onSurfaceVariant`.
- **Bottom action row** — sticky to bottom. Two buttons full-width minus 24dp gutter:
  - **Mark complete** (filled tonal, `secondaryContainer` / `onSecondaryContainer`)
  - **Delete** (text button, `error` color)

**Behavior**:
- Back arrow returns to List with the row in its updated state (state lifts to a shared store — see `source/todo-state.jsx`, `useTodos`).
- All chip taps open an inline editor (date picker, tag picker, priority picker).
- Animated check toggles `done` with the same motion as the list.

---

### 4. Support

**Purpose**: Let users contact the developer.

**Layout** (top → bottom):
- **TopAppBar** — small. Leading back arrow. Title "Support".
- **Header block** — 24dp padding. **Headline Small** "Need a hand?" + **Body Large** "We usually reply within one business day." `onSurface` / `onSurfaceVariant`.
- **Two contact cards**, stacked vertically with 12dp gap, side padding 16dp:
  - **Phone card**:
    - Container: `surfaceContainer`, 20dp radius, 16dp padding.
    - Layout: 48dp circular icon avatar (`primaryContainer` bg, `onPrimaryContainer` icon — phone glyph) + text column + chevron.
    - Title (Body Large, 500): "Call us"
    - Subtitle (Body Medium, `onSurfaceVariant`): "+1 (555) 234-9981"
    - Tap action: `tel:+15552349981` deeplink.
  - **Email card**: same shape; mail icon; "Email us" / "support@todo.example"; opens `mailto:`.
- **Footer caption** — Body Small `onSurfaceVariant`, centered: "Mon–Fri · 9am–5pm PT".

**Behavior**:
- Tapping a card fires the OS intent (`Intent.ACTION_DIAL` / `ACTION_SENDTO mailto:`). On long-press, copy the value to clipboard and show a snackbar "Copied".

---

## Design Tokens

### Color (Variation A — primary/recommended palette)

This design has three variations in the prototype. Variation A is the canonical one; B and C in the source are alternative palettes the user can compare. Use **A** unless otherwise directed.

**Light**

| Token | Hex |
|---|---|
| primary | `#0B6E4F` |
| onPrimary | `#FFFFFF` |
| primaryContainer | `#9BF0CF` |
| onPrimaryContainer | `#002115` |
| secondary | `#4C6358` |
| secondaryContainer | `#CEE9DA` |
| tertiary | `#3D6373` |
| tertiaryContainer | `#C0E8FB` |
| surface | `#F5FBF7` |
| surfaceContainer | `#E9F2EC` |
| surfaceContainerHigh | `#E0EBE3` |
| onSurface | `#172019` |
| onSurfaceVariant | `#3F4943` |
| outline | `#6F7A73` |
| error | `#BA1A1A` |
| errorContainer | `#FFDAD6` |

**Dark**

| Token | Hex |
|---|---|
| primary | `#80D5B4` |
| onPrimary | `#003824` |
| primaryContainer | `#005138` |
| onPrimaryContainer | `#9BF0CF` |
| secondary | `#B2CCBF` |
| secondaryContainer | `#344B40` |
| tertiary | `#A4CCDF` |
| tertiaryContainer | `#234C5B` |
| surface | `#0F1511` |
| surfaceContainer | `#1B2520` |
| surfaceContainerHigh | `#252F2A` |
| onSurface | `#DEE5DF` |
| onSurfaceVariant | `#BFC9C1` |
| outline | `#89938C` |
| error | `#FFB4AB` |
| errorContainer | `#93000A` |

### Tag palette

`work`, `errand`, `personal`, `learn`, `health`, `home`. Both light and dark values are in `source/todo-state.jsx` (`TAG_COLORS_LIGHT` / `TAG_COLORS_DARK`).

### Typography

Roboto. Weights: 400, 500, 700. Map to M3 type scale:

| Role | Style | Use |
|---|---|---|
| Display Small | 36 / 44 / Roboto 700 | Onboarding title |
| Headline Medium | 28 / 36 / Roboto 500 | Detail title |
| Headline Small | 24 / 32 / Roboto 500 | Support header |
| Title Large | 22 / 28 / Roboto 500 | TopAppBar title |
| Body Large | 16 / 24 / Roboto 400 (500 for emphasis) | List row title, body copy |
| Body Medium | 14 / 20 / Roboto 400 | Subtitles, secondary text |
| Body Small | 12 / 16 / Roboto 400 | Meta rows, captions |
| Label Large | 14 / 20 / Roboto 500 | Buttons, section labels (tracking 0.1) |

### Spacing scale

`4 / 8 / 12 / 16 / 20 / 24 / 32 / 48 / 56 / 80` dp.

### Radius scale

`8` (pills/chips inner) · `12` (small cards) · `16` (text fields, secondary cards) · `20` (todo rows / contact cards) · `28` (full-bleed primary buttons, FAB).

### Elevation / shadow

- Cards: M3 level 1 (`0 1px 2px rgba(0,0,0,.06), 0 1px 3px rgba(0,0,0,.04)`).
- Dragging row: M3 level 3.
- FAB: M3 level 3 resting, level 4 on press.

### Motion

| Element | Property | Duration | Easing |
|---|---|---|---|
| Checkbox fill | `background-color`, `border-color` | 220ms | ease |
| Check-mark path | `opacity`, `transform: scale` | 180ms / 220ms | spring `cubic-bezier(.34, 1.56, .64, 1)` |
| Onboarding page change | `translateX` | 280ms | M3 emphasized |
| Swipe-action reveal | follows finger; snaps at 60% threshold | 200ms snap | M3 standard decelerate |
| Pull-to-refresh | indicator scale + spin | 800ms | linear spin |
| Bottom-sheet open | translateY + scrim alpha | 320ms | M3 emphasized |

---

## State Management

Lift task list to an app-level store (ViewModel + StateFlow on Android, or a Compose `rememberSaveable` map for a quick prototype). The reference store is in `source/todo-state.jsx`:

```js
useTodos(initial) → { todos, toggle, remove, update, reorder, add, setTodos }
```

Translate verbatim:

- `toggle(id)` — flips `done`.
- `remove(id)` — drops by id.
- `update(id, patch)` — shallow merge.
- `reorder(fromIdx, toIdx)` — array splice.
- `add(todo)` — prepends a new task with a generated id.

A task object:

```kotlin
data class Todo(
  val id: String,
  val title: String,
  val notes: String = "",
  val tag: String,
  val tagColor: TagColor,    // enum: WORK, ERRAND, PERSONAL, LEARN, HEALTH, HOME
  val priority: Priority,    // HIGH, MEDIUM, LOW
  val due: String?,          // already-formatted relative string in the design; replace with Instant + formatter
  val done: Boolean = false,
)
```

Persistence: Room DB recommended. Keep `due` as `Instant` and format at the UI layer; the prototype's pre-formatted strings ("Today, 4:00 PM") were a shortcut.

---

## Assets

- **Roboto** — Google Fonts, weights 400/500/700. Load via the system font on Android.
- **Icons** — drawn inline in the prototype (`Icon` map in `todo-state.jsx`). On Android, replace with **Material Symbols (Outlined)** at 24dp: `arrow_back`, `more_vert`, `search`, `tune`, `add`, `flag`, `schedule`, `phone`, `mail`, `chevron_right`, `drag_indicator`, `refresh`, `check`, `delete`, `list_alt`, `help_outline`.
- **Onboarding illustrations** — the prototype's SVG art is **placeholder**. Commission or reuse production illustrations sized to 280×280dp and matching the three onboarding tones (primaryContainer / tertiaryContainer / secondaryContainer).

No external image assets are required to ship.

---

## Files in this handoff

- `README.md` — this document.
- `source/ToDo App.html` — the host page that runs the prototype. Open in a browser to see live behavior. Contains the screen-router glue, theme switch, and inline static frames.
- `source/phone-shell.jsx` — pixel-accurate Android phone frame component used to present screens at design time. **For reference only** — do not port to production.
- `source/todo-state.jsx` — sample data, `useTodos` hook, animated checkbox, icon set, tag palettes. The shared logic worth porting.
- `source/variant-a.jsx` — the canonical variation: full implementations of all four screens (onboarding, list, detail, support) plus the M3 theme. **Read this file alongside the screen specs above** — it shows the exact composition of every component.

To verify behavior, open `source/ToDo App.html` in a browser. Use the on-screen Tweaks panel to toggle dark mode and jump between screens.
