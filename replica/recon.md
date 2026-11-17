# Recon map: Firefox ScreenshotGo (Android)

Scope: Core screenshot capture lifecycle, floating overlay trigger, immediate post-capture sorting, visual collections management, on-device OCR text scanning, and full-text screenshot search.  
For: Power mobile users, researchers, shoppers, and students who frequently take screenshots and need an effortless way to capture, organize, find, and extract text from images.  
Date: 2026-11-17  

---

## Sources

| # | source | URL / Reference | notes |
|---|---|---|---|
| 1 | Uploaded Android APK | `firefox-screenshotgo-0-8-1164.apk` (`org.mozilla.screenshot.go`) | Extracted layouts, drawables, Room SQLite schema, strings, and manifest components |
| 2 | Official Mozilla Support Docs | [Mozilla Support: ScreenshotGo Help](https://support.mozilla.org/kb/firefox-screenshotgo) | Core feature guides, collection management, and permissions |
| 3 | Mozilla Asia / Regional Announcements | Antara News & Mozilla Tech Blogs | User behavior studies (Southeast Asia screenshot volume), initial rollout scope |
| 4 | Google Play Listing Archives & APK Repos | Uptodown / APKMirror public archives | Feature highlights, changelogs, promotional screenshots, and UX patterns |
| 5 | Android Manifest & Bytecode Analysis | `org.mozilla.scryer` internal package inspection | Room DB schema, MediaProjection APIs, Foreground Services, System Overlay |

---

## Core loop

> **Capture any mobile screen instantly via an edge-docked floating GO button, immediately categorize it into custom collections via a quick-sorting panel, and search or copy text directly from screenshots using on-device OCR.**

---

## Screens

| ID | Screen Name | Route / How to Reach | Purpose | Key Components | States Seen |
|---|---|---|---|---|---|
| **S01** | Onboarding & Permissions | Initial launch / first open | Introduce app value proposition, request Storage and System Overlay permissions | Welcome hero graphic (`image_welcome.png`), explanatory benefit text, permission action buttons ("Let's GO", "Allow permission") | Initial, Permission Requested, Permission Denied, Retry |
| **S02** | Home Dashboard | Main app entry after onboarding | Main hub displaying recent screenshots and organized collections | Top Toolbar (Logo, Search icon, Settings gear), Quick Access horizontal carousel, Collections Grid, "+ New Collection" card | Empty (no screenshots, `image_emptypage.png`), Filled (recent cards + collections), Loading |
| **S03** | Collection Detail View | Tap on any collection card from S02 | View and manage all screenshots inside a specific category | Back navigation, Collection title & color chip, Options menu (Rename, Info, Delete), Screenshot multi-column grid, Multi-select action bar | Empty collection, Grid filled, Multi-selection active |
| **S04** | Screenshot Detail & OCR Viewer | Tap any screenshot thumbnail from S02 or S03 | Inspect screenshot in full-screen, trigger OCR scanning, select and act on recognized text | Fullscreen zoomable image viewer, OCR scanning indicator ("Recognizing text..."), Highlighted text bounding boxes, Action bar (Copy, Web Search, Share, Move to, Delete, Info) | Image loading, OCR processing, Text detected/highlighted, No text found, OCR error |
| **S05** | Floating "GO" Capture Overlay | Persistent system overlay (`SYSTEM_ALERT_WINDOW`) over third-party apps | Enable instant single-tap screenshot capture from any active app | Semi-transparent circular floating action button docked to screen edge, Drag-to-dismiss exit target at bottom | Idle docked, Dragging/Moving, Pressed/Capturing, Hovering over exit target |
| **S06** | Post-Capture Sorting Panel | Appears immediately after tapping GO button (or via capture notification) | Prompt user to categorize new screenshot right away without breaking context | Thumbnail preview, Collection selector chips/grid, "Create new collection" button, "Skip / Move to Unsorted" action, "Don't ask again" checkbox | Default prompt, Collection selected, Dismissed/Skipped |
| **S07** | Full-Text Search | Tap Search icon from Home (S02) Top Bar | Search across all screenshot OCR text contents in real time | Search query input ("Just type to search words within screenshots"), Clear button, Search results grid, Result counter ("%1$d results") | Initial/Empty prompt, Typing/Searching, Results populated, No results found ("Try another word") |
| **S08** | Collection Name Dialog | Tap "+ Create new collection" or "Rename collection" | Input or edit a collection name | Modal dialog, EditText input (max 20 characters), character counter, Save and Cancel buttons | Clean input, Filled, Name duplicate error, Character limit exceeded |
| **S09** | Screenshot / Collection Info Modal | Options menu -> "Screenshot info" or "Collection info" | Display file metadata and disk usage statistics | Info dialog, File size, storage path, capture timestamp, associated collection | Normal modal view |
| **S10** | Settings | Tap Settings gear icon from Home (S02) Top Bar | Configure capture behavior, sorting prompts, and app preferences | Preference list: "Use GO button" toggle, "Ask to sort" toggle, "Send usage data" toggle, About, Privacy Notice, Open Source Licenses | Default toggles, Toggled state |

---

## Flows

```text
F01 Quick Floating Capture & Sort Loop
    S05 (User taps Floating GO button anywhere on Android OS)
    -> System captures screen via MediaProjection API
    -> S06 (Sorting Panel overlay appears over current screen)
    -> User taps target collection (e.g. "Shopping")
    -> Screenshot saved to storage & cataloged in Room DB under selected Collection
    -> S06 dismisses, user returns seamlessly to underlying app
    Happy path clicks: 2 (1 tap GO button, 1 tap collection chip)
    Edge cases:
      - User chooses "Skip all": Screenshot saved directly to "Unsorted" collection.
      - User checks "Don't ask again": Future captures automatically routed to "Unsorted" without opening S06.
      - User drags GO button into bottom exit zone: Floating button hides until re-enabled from app/notification.

F02 OCR Text Extraction & Action
    S02 / S03 (User taps screenshot)
    -> S04 (Screenshot Detail & OCR Viewer opens)
    -> Background on-device OCR scans image for text strings
    -> Bounding boxes/overlays highlight detected text blocks
    -> User selects text or taps "Select all text"
    -> Quick Action options appear:
         - [Copy]: Text copied to system clipboard ("Copied" toast shown)
         - [Web search]: Opens default browser with query preset to highlighted text
         - [Share]: Opens system share sheet with extracted text payload
    Happy path clicks: 3 (Tap screenshot -> Tap text block -> Tap Action)
    Edge cases:
      - Low contrast/unclear text: "No text found" or "Crop screenshot and retry".
      - Offline OCR engine asset missing: Prompts to connect to network to install local OCR model.

F03 Full-Text Screenshot Search
    S02 (User taps Search icon in top bar)
    -> S07 (Full-Text Search screen opens, keyboard focuses)
    -> User enters query (e.g., "Invoice", "Promo code", "Receipt")
    -> Room DB queries `screenshot_content` table via `LIKE` / FTS indexing
    -> Instant results grid displays matching screenshots
    -> User taps matching screenshot -> opens S04
    Happy path clicks: 2 + typing
    Edge cases:
      - Zero matches: "No results found. Try another word".
      - Unindexed screenshots: Runs fast background indexing pass.

F04 Create & Manage Custom Collection
    S02 (Home) -> Tap "+ New Collection"
    -> S08 (Collection Name Dialog opens)
    -> User types collection name (e.g., "Work Travel") -> Tap "Save"
    -> New collection card added to S02 grid with generated color accent
    Happy path clicks: 3 + typing name
    Edge cases:
      - Duplicate name: "There is already a collection with this name".
      - Name over 20 characters: Input blocked with character counter alert.

F05 Multi-Select Batch Operations
    S03 (Collection Detail) -> Long press screenshot thumbnail
    -> Multi-select action bar appears (shows count "%1$d selected", "Select all")
    -> User selects multiple items
    -> User taps [Move to...] -> Selects target collection -> Items relocated in DB
    -> OR User taps [Delete] -> S09 confirmation prompt -> Files deleted permanently from storage
    Happy path clicks: 4
    Edge cases:
      - User deletes entire collection: Warns that all underlying screenshots will be permanently deleted from phone.

F06 Initial App Setup & Permission Onboarding
    S01 (App first launch)
    -> Screen explains storage access need
    -> User taps "Allow permission" -> Android OS Storage permission dialog -> User grants
    -> Screen explains floating GO capture button need
    -> User taps "Enable GO button" -> Android OS System Alert Window / Overlay permission screen -> User grants
    -> App launches S02 (Home Dashboard) ready for use
    Happy path clicks: 4
    Edge cases:
      - User denies overlay permission: Floating GO button disabled by default; app functions as gallery/manager with in-app capture disabled.
```

---

## Components

| Component | Variants | States | Used On |
|---|---|---|---|
| **Floating GO Button** | Circular icon, semi-transparent backdrop | Idle (docked to screen edge), Dragging, Pressed, Exit Target Hover | System overlay (S05) |
| **Screenshot Thumbnail Card** | Standard grid card, Quick access wide card | Default, Pressed, Multi-selected (checked badge), Loading placeholder | S02, S03, S07 |
| **Collection Card** | Standard category box with colored badge, "+ New Collection" dashed border card | Default, Pressed, Empty | S02 |
| **OCR Text Selection Overlay** | Highlight bounding box, Select All chip | Idle, Text Selected, Copying | S04 |
| **Viewer Bottom Action Bar** | Translucent bottom strip with action icons (Copy, Search, Move, Delete, Share) | Default, Disabled, Active | S04 |
| **Sorting Bottom Sheet / Dialog** | Compact modal sheet with thumbnail and horizontal/grid collection chips | Default, Chip Selected, Dismissing | S06 |
| **Search Input Bar** | Integrated app bar search with clear icon | Unfocused, Focused with keyboard, Populated, Query Cleared | S07 |
| **Modal Confirmation Dialog** | Danger (Delete confirmation), Info (Metadata display), Input (Collection name) | Open, Validating, Submitting, Dismissed | S03, S08, S09 |
| **Preference Toggle Row** | Switch compat row with primary title and explanatory subtitle | Checked, Unchecked, Disabled | S10 |

---

## Inferred Data Model

Extracted directly from the application's compiled AndroidX Room SQLite database:

```sql
-- Collections Table: Stores category folders
Collection (
    id TEXT PRIMARY KEY NOT NULL,          -- UUID / unique identifier
    name TEXT NOT NULL,                    -- Category label (e.g. "Shopping", "Unsorted", max 20 chars)
    date INTEGER NOT NULL,                 -- Creation timestamp (epoch millis)
    color INTEGER NOT NULL                 -- Color accent hex / integer code
)
-- Evidence: AndroidX Room bytecode schema in classes.dex
-- Confidence: High (Exact verified SQLite schema)

-- Screenshots Table: Stores reference to physical image files
Screenshot (
    id TEXT PRIMARY KEY NOT NULL,          -- Unique screenshot identifier
    absolute_path TEXT NOT NULL,           -- Local Android filesystem URI / path
    last_modified INTEGER NOT NULL,        -- Modification timestamp (epoch millis)
    collection_id TEXT NOT NULL,           -- Foreign key linking to Collection(id)
    FOREIGN KEY(collection_id) REFERENCES Collection(id) ON DELETE CASCADE
)
-- Evidence: AndroidX Room bytecode schema in classes.dex
-- Confidence: High (Exact verified SQLite schema)

-- Screenshot OCR Content Table: Stores extracted OCR text for searching
ScreenshotContent (
    id TEXT PRIMARY KEY NOT NULL,          -- References Screenshot(id)
    content_text TEXT,                     -- Full OCR text payload extracted by local ML Kit / OCR
    FOREIGN KEY(id) REFERENCES Screenshot(id) ON DELETE CASCADE
)
-- Evidence: AndroidX Room bytecode schema in classes.dex
-- Confidence: High (Exact verified SQLite schema)
```

### Entity Relationships
- `Collection` **1 : N** `Screenshot` (A collection contains zero or more screenshots; default collection is `Unsorted`).
- `Screenshot` **1 : 1** `ScreenshotContent` (Each screenshot has one associated OCR text payload used for full-text search).

---

## Feature Matrix Summary

See complete breakdown in `replica/features.csv`:
- **Must-have**: 14 features (Floating capture button, MediaProjection screen capture, Post-capture sorting panel, Unsorted fallback, Quick Access carousel, Collections grid, Fullscreen viewer, On-device OCR engine, Text copy, Full-text search, Create collection, Move screenshot, Delete screenshot, Storage onboarding).
- **Should-have**: 7 features (Rename collection, Color assignment, Web search action from OCR text, Batch multi-select, Toggle GO button in settings, Toggle Ask-to-sort, Screenshot info modal).
- **Could-have**: 4 features (Collection disk space stats, System Share sheet, Direct text snippet share, Drag-to-dismiss exit target).
- **Skip (Out of Scope)**: 2 features (Mozilla Telemetry / Firebase Scryer backend, Mozilla Firefox trademarks and brand assets).

---

## Out of Scope (Cannot or Should Not Be Cloned)

- **Mozilla Trademarks & Brand Assets**:
  - The names "Firefox", "ScreenshotGo", "Mozilla", and official fox/capture logo icons are proprietary trademarks.
  - The clone must adopt an original brand identity (e.g., *SnapSort*, *ScreenVault*, or *Loomis*).
- **Mozilla Cloud Telemetry & Feedback Endpoints**:
  - `https://scryer-production.firebaseio.com`
  - `https://qsurvey.mozilla.com/s3/Firefox-ScreenshotGo-Input`
  - Proprietary crashlytics and remote config services are replaced with privacy-first local operation or standard telemetry if desired.

---

## Technical Sizing & Complexity

- **Screens**: 10 primary UI screens & dialogs (Onboarding, Home, Collection, Viewer, Floating Overlay, Sorting Panel, Search, Collection Modal, Info Modal, Settings).
- **User Flows**: 6 distinct end-to-end flows.
- **Data Entities**: 3 clean Room database tables (`Collection`, `Screenshot`, `ScreenshotContent`).
- **Hard Technical Challenges**:
  1. **System Floating Overlay (`SYSTEM_ALERT_WINDOW`) & MediaProjection API**: Modern Android versions (Android 11–14+) impose strict background capture restrictions and require foreground service with `mediaProjection` type.
  2. **On-Device OCR Performance & Battery Consumption**: Running offline text recognition (via Google ML Kit Text Recognition or Tesseract) over large screenshot batches requires an asynchronous WorkManager background pipeline to avoid UI freezes or battery drain.
  3. **Scoped Storage Permissions**: Managing external screenshot directories across Android 10+ scoped storage boundaries while allowing permanent file deletion.

**Estimated Size**: **M (Medium — 2 to 3 weeks for a complete native Android or Flutter vertical slice)**.
