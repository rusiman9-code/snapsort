# Component Specifications: SnapSort

Specifications for all components identified during recon. All components use design tokens defined in [`tokens.json`](file:///d:/APPS/Screenshoot%20Go/replica/design/tokens.json) and follow strict accessibility guidelines.

---

### 1. FloatingCaptureButton
- **Purpose**: System-wide floating action button pinned to screen edge for one-tap screenshot capture over any active app.
- **Variants**: Default circle (56dp), Expanded capturing feedback, Drag-to-dismiss hover.
- **Sizes**: 56x56 dp circular touch target (meets 48dp Android touch target minimum).
- **States**:
  - `idle`: Docked to nearest screen edge, opacity 0.75, elevation 8dp (`shadow-floating`).
  - `dragging`: Opacity 1.0, scale 1.05, follows finger touch coordinates.
  - `hover_exit`: Hovering over the bottom dismiss zone; tints to `danger` (`#b91c1c`).
  - `capturing`: Quick flash/pulse scale animation (150ms).
- **Tokens**: `bg accent` (`#0369a1`), `icon on-accent` (`#ffffff`), `radius pill`.
- **A11y**: Screen reader description: *"Take screenshot button"*. High contrast icon.
- **Used on**: S05 (System Overlay).

---

### 2. ScreenshotThumbnailCard
- **Purpose**: Displays captured screenshot thumbnail in galleries and search results.
- **Variants**: Grid card (aspect ratio ~ 9:16), Wide quick-access carousel card.
- **Sizes**: Adaptive grid cell (2-3 columns on phones), Quick Access card (160dp width x 260dp height).
- **States**:
  - `default`: Clean image thumbnail with rounded corners (`radius-md`).
  - `pressed`: Ripple effect, slight downscale (0.98x).
  - `selected`: Blue outline border (`accent`), checkmark badge overlay in top-right corner.
  - `loading`: Shimmer skeleton placeholder.
- **Tokens**: `surface surface-elevated`, `border border`, `radius md`, `shadow card`.
- **A11y**: Content description: *"Screenshot taken on [Date], Collection [Name]"*.
- **Used on**: S02 (Home), S03 (Collection Detail), S07 (Search).

---

### 3. CollectionCard
- **Purpose**: Visual card representing a screenshot category with color identifier and item count.
- **Variants**: Standard category card, "+ New Collection" dashed placeholder card.
- **Sizes**: 2-column grid item, min height 110dp.
- **States**:
  - `default`: Solid surface background, colored category badge dot, title, count subtitle.
  - `pressed`: Surface highlight ripple.
  - `empty`: Shows "0 screenshots" label.
  - `dashed_add`: Dashed border (`border-input`), center plus icon and "New collection" text.
- **Tokens**: `bg surface`, `text text`, `text-muted text-muted`, `border border`, `radius lg`.
- **A11y**: Announces: *"[Collection Name], [N] screenshots, tap to open"*.
- **Used on**: S02 (Home).

---

### 4. OcrTextSelectionOverlay
- **Purpose**: Interactive overlay drawn directly above the screenshot image, highlighting detected text lines/words.
- **Variants**: Unselected bounding box, Active highlight box.
- **States**:
  - `idle`: Subtle semi-transparent rounded rectangle outline over detected text blocks.
  - `selected`: Solid `accent-subtle` highlight with floating contextual toolbar ([Copy], [Web Search], [Share]).
  - `active_selection`: User dragging selection handles.
- **Tokens**: `fill accent-subtle` (opacity 0.35), `border accent`, `toolbar bg surface-elevated`.
- **A11y**: Selected text is exposed to Android AccessibilityNodeInfo as selectable text spans.
- **Used on**: S04 (Detail Viewer).

---

### 5. ViewerBottomActionBar
- **Purpose**: Bottom utility toolbar for detail viewer actions.
- **Variants**: Full actions (Copy, Search, Move, Share, Delete, Info).
- **Sizes**: 64dp height bottom bar + system navigation insets.
- **States**:
  - `default`: Translucent backdrop blur, horizontally spaced icon buttons.
  - `disabled`: Text action disabled when no OCR text is selected.
  - `danger`: Delete button with red icon tint (`danger`).
- **Tokens**: `bg surface-elevated` (with 85% alpha backdrop blur), `border border`, `text text`.
- **A11y**: 48dp minimum touch target per action button with clear `contentDescription`.
- **Used on**: S04 (Detail Viewer).

---

### 6. PostCaptureSortingSheet
- **Purpose**: Prompt modal appearing immediately after screen capture to file the screenshot into a collection.
- **Variants**: Bottom sheet modal (with thumbnail preview & horizontal chip list).
- **Sizes**: Max height 340dp, anchored to bottom.
- **States**:
  - `default`: Displays thumbnail preview on left, collection chips on right/grid, "Skip" action.
  - `chip_pressed`: Chip highlights with `accent` background.
  - `checkbox_checked`: "Don't ask again" toggle active.
  - `dismissing`: Slides down smoothly (200ms ease).
- **Tokens**: `bg surface-elevated`, `shadow sheet`, `radius xl` (top corners), `border border`.
- **A11y**: Traps modal accessibility focus; Escape / Back key dismisses to "Unsorted".
- **Used on**: S06 (Post-Capture).

---

### 7. SearchInputBar
- **Purpose**: Real-time query input with clear trigger and filter chips.
- **Variants**: Clean search bar, Query active with clear ("X") button.
- **Sizes**: Height 48dp, full width with 16dp horizontal padding.
- **States**:
  - `unfocused`: Displays search icon + placeholder *"Search words in screenshots..."*.
  - `focused`: Active cursor, keyboard shown, border turns to `accent`.
  - `populated`: Clear button visible on trailing edge.
- **Tokens**: `bg surface`, `border border-input`, `text text`, `text-muted text-muted`, `radius pill`.
- **A11y**: Clear label for screen readers: *"Search screenshot text"*; announces search result count updates.
- **Used on**: S07 (Full-Text Search).

---

### 8. ModalDialog
- **Purpose**: Generic modal for collection naming, deletion confirmation, and file info.
- **Variants**: Input Dialog (Edit collection name), Confirmation Dialog (Delete), Info Dialog (Metadata).
- **Sizes**: Max width 360dp, centered on screen.
- **States**:
  - `default`: Scrim overlay (`rgba(15, 23, 42, 0.4)`), elevated card, title, body, action buttons.
  - `input_error`: Error message below text input (e.g. duplicate name / character limit).
  - `loading`: Submit button enters loading spinner.
- **Tokens**: `bg surface-elevated`, `radius lg`, `shadow floating`, `text text`.
- **A11y**: Native AlertDialog / Compose Dialog semantics, handles back dismiss cleanly.
- **Used on**: S03, S08, S09.

---

### 9. PreferenceToggleRow
- **Purpose**: Standard setting list item with toggle switch.
- **Variants**: Standard toggle, Navigation row (arrow right).
- **Sizes**: Min height 56dp.
- **States**:
  - `checked`: Switch thumb in active position (`accent`).
  - `unchecked`: Switch thumb in neutral position (`border-input`).
  - `pressed`: Row background highlights (`surface`).
- **Tokens**: `text text`, `text-muted text-muted`, `switch accent`.
- **A11y**: Exposes `Role.Switch` to accessibility services; announces toggle state changes.
- **Used on**: S10 (Settings).

---

### 10. Button
- **Purpose**: Universal button component for primary and secondary actions.
- **Variants**:
  - `primary`: Filled with `accent` (`#0369a1`), text `on-accent`.
  - `secondary`: Outlined with `border-input`, text `text`.
  - `ghost`: Transparent background, text `accent`.
  - `danger`: Filled with `danger` (`#b91c1c`), text `on-danger`.
- **Sizes**: Small (36dp height, text-xs), Medium (44dp height, text-sm), Large (52dp height, text-base).
- **States**: `default`, `pressed` (alpha 0.9, scale 0.98), `disabled` (alpha 0.45), `loading` (spinner).
- **Tokens**: `radius md`, `motion fast`.
- **A11y**: Always announces label; when in loading state, preserves accessibility text.
- **Used on**: S01, S02, S06, S08, S09.
