-- ==============================================================================
-- Schema: ScreenGo / SnapSort (Rebuild of Firefox ScreenshotGo)
-- Dialect: SQLite / AndroidX Room
-- Description: Local-first database with FTS5 full-text search indexing.
-- ==============================================================================

-- 1. Collections: Organizes screenshots into categories
CREATE TABLE IF NOT EXISTS collections (
    id TEXT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL COLLATE NOCASE,
    created_at INTEGER NOT NULL,
    color INTEGER NOT NULL DEFAULT -16744193, -- Default brand teal/accent
    is_default INTEGER NOT NULL DEFAULT 0    -- 1 for "Unsorted" system collection
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_collections_name ON collections(name);
CREATE INDEX IF NOT EXISTS idx_collections_created_at ON collections(created_at DESC);

-- Seed default "Unsorted" collection
INSERT OR IGNORE INTO collections (id, name, created_at, color, is_default)
VALUES ('default_unsorted', 'Unsorted', 0, -7829368, 1);


-- 2. Screenshots: Stores references to image files on device
CREATE TABLE IF NOT EXISTS screenshots (
    id TEXT PRIMARY KEY NOT NULL,
    file_uri TEXT NOT NULL,                 -- content:// URI or absolute file path
    file_name TEXT NOT NULL,
    file_size INTEGER NOT NULL DEFAULT 0,   -- File size in bytes
    width INTEGER NOT NULL DEFAULT 0,       -- Image width in pixels
    height INTEGER NOT NULL DEFAULT 0,      -- Image height in pixels
    taken_at INTEGER NOT NULL,              -- Timestamp of capture (epoch ms)
    collection_id TEXT NOT NULL DEFAULT 'default_unsorted',
    ocr_status TEXT NOT NULL DEFAULT 'PENDING'
        CHECK (ocr_status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'NO_TEXT')),
    FOREIGN KEY (collection_id) REFERENCES collections(id) ON DELETE SET DEFAULT
);

CREATE INDEX IF NOT EXISTS idx_screenshots_collection_id ON screenshots(collection_id);
CREATE INDEX IF NOT EXISTS idx_screenshots_taken_at ON screenshots(taken_at DESC);
CREATE INDEX IF NOT EXISTS idx_screenshots_ocr_status ON screenshots(ocr_status);


-- 3. Screenshot OCR Content: Structured text recognized from screenshot
CREATE TABLE IF NOT EXISTS screenshot_contents (
    screenshot_id TEXT PRIMARY KEY NOT NULL,
    raw_text TEXT NOT NULL,                 -- Full recognized text payload
    blocks_json TEXT NOT NULL DEFAULT '[]', -- Serialized bounding boxes: [{"text":"...", "rect":[l,t,r,b]}]
    indexed_at INTEGER NOT NULL,
    FOREIGN KEY (screenshot_id) REFERENCES screenshots(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_screenshot_contents_indexed_at ON screenshot_contents(indexed_at DESC);


-- 4. Full-Text Search Virtual Table (FTS5): Enables instant keyword search
CREATE VIRTUAL TABLE IF NOT EXISTS screenshot_contents_fts USING fts5 (
    screenshot_id UNINDEXED,
    raw_text,
    content='screenshot_contents',
    content_rowid='rowid'
);

-- Triggers to automatically keep FTS5 index synchronized with screenshot_contents
CREATE TRIGGER IF NOT EXISTS trg_screenshot_contents_ai AFTER INSERT ON screenshot_contents BEGIN
    INSERT INTO screenshot_contents_fts(rowid, screenshot_id, raw_text)
    VALUES (new.rowid, new.screenshot_id, new.raw_text);
END;

CREATE TRIGGER IF NOT EXISTS trg_screenshot_contents_ad AFTER DELETE ON screenshot_contents BEGIN
    INSERT INTO screenshot_contents_fts(screenshot_contents_fts, rowid, screenshot_id, raw_text)
    VALUES ('delete', old.rowid, old.screenshot_id, old.raw_text);
END;

CREATE TRIGGER IF NOT EXISTS trg_screenshot_contents_au AFTER UPDATE ON screenshot_contents BEGIN
    INSERT INTO screenshot_contents_fts(screenshot_contents_fts, rowid, screenshot_id, raw_text)
    VALUES ('delete', old.rowid, old.screenshot_id, old.raw_text);
    INSERT INTO screenshot_contents_fts(rowid, screenshot_id, raw_text)
    VALUES (new.rowid, new.screenshot_id, new.raw_text);
END;
