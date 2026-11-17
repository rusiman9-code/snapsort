# Build Log: SnapSort

Build records tracking screen-by-screen completion, vertical slice verification, and technical notes.

| Screen ID | Screen Name | Date | Status | What Was Harder Than Expected / Notes |
|---|---|---|---|---|
| **S01** | Onboarding & Permissions | 2026-11-17 | **Done** | Handling modern Android 10-14 permission flows (Overlay + Scoped Storage) cleanly. |
| **S02** | Home Dashboard | 2026-11-17 | **Done** | Balancing horizontal Quick Access carousel (`LazyRow`) with two-column collection grid; empty state with action cues. |
| **S03** | Collection Detail View | 2026-11-17 | **Done** | Multi-select state management with batch move and batch deletion dialogs. |
| **S04** | Detail Viewer & OCR Reader | 2026-11-17 | **Done** | On-device ML Kit text recognition integration; asynchronous scanning with instant Copy & Web Search actions. |
| **S05** | Floating Capture Overlay | 2026-11-17 | **Done** | `WindowManager` layout parameters and draggable physics with touch event disambiguation (click vs drag). |
| **S06** | Post-Capture Sorting Sheet | 2026-11-17 | **Done** | `ModalBottomSheet` displaying recent capture with instant category chips and quick "Skip" to Unsorted. |
| **S07** | Full-Text Search | 2026-11-17 | **Done** | Reactive search querying SQLite Room database against indexed OCR strings. |
| **S08** | New Collection Modal | 2026-11-17 | **Done** | Quick dialog for naming categories with color badge auto-assignment. |
| **S09** | Screenshot Info Modal | 2026-11-17 | **Done** | Formatted display of file resolution, capture time, and OCR status. |
| **S10** | Settings | 2026-11-17 | **Done** | Direct intents to Android system overlay permission screen with active service status toggle. |

---

### Build Milestone Summary
- **Vertical Slice**: Core capture loop (S05 Floating Button -> Capture -> S06 Quick Sort -> S02 Home list) is fully functional.
- **On-Device OCR & Search**: Google ML Kit Vision Text Recognition is integrated 100% offline; text search runs directly against local SQLite Room database.
- **Binary Status**: Debug APK built successfully at `app/build/outputs/apk/debug/app-debug.apk` (18.0 MB, Gradle 8.4, Java 21).
