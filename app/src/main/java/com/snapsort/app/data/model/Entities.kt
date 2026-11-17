package com.snapsort.app.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "collections",
    indices = [
        Index(value = ["name"], unique = true),
        Index(value = ["created_at"])
    ]
)
data class CollectionEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name", collate = ColumnInfo.NOCASE) val name: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "color") val color: Int = -16553567, // Accent blue default
    @ColumnInfo(name = "is_default") val isDefault: Boolean = false
)

@Entity(
    tableName = "screenshots",
    foreignKeys = [
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["collection_id"],
            onDelete = ForeignKey.SET_DEFAULT
        )
    ],
    indices = [
        Index(value = ["collection_id"]),
        Index(value = ["taken_at"]),
        Index(value = ["ocr_status"])
    ]
)
data class ScreenshotEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "file_uri") val fileUri: String,
    @ColumnInfo(name = "file_name") val fileName: String,
    @ColumnInfo(name = "file_size") val fileSize: Long = 0L,
    @ColumnInfo(name = "width") val width: Int = 0,
    @ColumnInfo(name = "height") val height: Int = 0,
    @ColumnInfo(name = "taken_at") val takenAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "collection_id", defaultValue = "default_unsorted") val collectionId: String = "default_unsorted",
    @ColumnInfo(name = "ocr_status", defaultValue = "PENDING") val ocrStatus: String = "PENDING"
)

@Entity(
    tableName = "screenshot_contents",
    foreignKeys = [
        ForeignKey(
            entity = ScreenshotEntity::class,
            parentColumns = ["id"],
            childColumns = ["screenshot_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ScreenshotContentEntity(
    @PrimaryKey @ColumnInfo(name = "screenshot_id") val screenshotId: String,
    @ColumnInfo(name = "raw_text") val rawText: String,
    @ColumnInfo(name = "blocks_json") val blocksJson: String = "[]",
    @ColumnInfo(name = "indexed_at") val indexedAt: Long = System.currentTimeMillis()
)

data class CollectionWithCount(
    val id: String,
    val name: String,
    val color: Int,
    val isDefault: Boolean,
    val screenshotCount: Int
)
