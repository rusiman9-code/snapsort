package com.snapsort.app.data.repository

import android.content.Context
import com.snapsort.app.data.dao.CollectionDao
import com.snapsort.app.data.dao.ScreenshotDao
import com.snapsort.app.data.model.CollectionEntity
import com.snapsort.app.data.model.CollectionWithCount
import com.snapsort.app.data.model.ScreenshotContentEntity
import com.snapsort.app.data.model.ScreenshotEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class ScreenshotRepository(
    private val collectionDao: CollectionDao,
    private val screenshotDao: ScreenshotDao,
    private val context: Context
) {
    val allScreenshots: Flow<List<ScreenshotEntity>> = screenshotDao.getAllScreenshots()
    val recentScreenshots: Flow<List<ScreenshotEntity>> = screenshotDao.getRecentScreenshots(10)
    val collectionsWithCount: Flow<List<CollectionWithCount>> = collectionDao.getCollectionsWithCount()

    fun getScreenshotsByCollection(collectionId: String): Flow<List<ScreenshotEntity>> {
        return screenshotDao.getScreenshotsByCollection(collectionId)
    }

    suspend fun getScreenshotById(id: String): ScreenshotEntity? {
        return screenshotDao.getScreenshotById(id)
    }

    suspend fun getContent(screenshotId: String): ScreenshotContentEntity? {
        return screenshotDao.getContent(screenshotId)
    }

    suspend fun syncGalleryScreenshots(): Int = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val projection = arrayOf(
            android.provider.MediaStore.Images.Media._ID,
            android.provider.MediaStore.Images.Media.DISPLAY_NAME,
            android.provider.MediaStore.Images.Media.SIZE,
            android.provider.MediaStore.Images.Media.WIDTH,
            android.provider.MediaStore.Images.Media.HEIGHT,
            android.provider.MediaStore.Images.Media.DATE_MODIFIED
        )

        // Broader search: checks bucket name, display name, or path for Screenshot/Tangkapan layar/SnapSort
        val selection = """
            ${android.provider.MediaStore.Images.Media.BUCKET_DISPLAY_NAME} LIKE ? OR 
            ${android.provider.MediaStore.Images.Media.BUCKET_DISPLAY_NAME} LIKE ? OR 
            ${android.provider.MediaStore.Images.Media.DISPLAY_NAME} LIKE ? OR 
            ${android.provider.MediaStore.Images.Media.DISPLAY_NAME} LIKE ? OR
            ${android.provider.MediaStore.Images.Media.DISPLAY_NAME} LIKE ? OR
            ${android.provider.MediaStore.Images.Media.DATA} LIKE ?
        """.trimIndent()

        val selectionArgs = arrayOf(
            "%Screenshot%",
            "%Tangkapan%",
            "%Screenshot%",
            "%screenshot%",
            "%SnapSort%",
            "%Screenshot%"
        )

        val sortOrder = "${android.provider.MediaStore.Images.Media.DATE_MODIFIED} DESC"
        var importedCount = 0

        try {
            context.contentResolver.query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media._ID)
                val nameCol = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.DISPLAY_NAME)
                val sizeCol = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.SIZE)
                val widthCol = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.WIDTH)
                val heightCol = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.HEIGHT)
                val dateCol = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.DATE_MODIFIED)

                while (cursor.moveToNext()) {
                    val mediaId = cursor.getLong(idCol)
                    val uri = android.content.ContentUris.withAppendedId(
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        mediaId
                    ).toString()

                    val name = cursor.getString(nameCol) ?: "Screenshot"
                    val size = cursor.getLong(sizeCol)
                    val w = cursor.getInt(widthCol)
                    val h = cursor.getInt(heightCol)
                    val date = cursor.getLong(dateCol) * 1000L

                    if (screenshotDao.getScreenshotByUri(uri) == null) {
                        val entity = ScreenshotEntity(
                            id = java.util.UUID.randomUUID().toString(),
                            fileUri = uri,
                            fileName = name,
                            fileSize = size,
                            width = w,
                            height = h,
                            takenAt = if (date > 0) date else System.currentTimeMillis(),
                            collectionId = "default_unsorted",
                            ocrStatus = "PENDING"
                        )
                        screenshotDao.insert(entity)
                        importedCount++
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        importedCount
    }

    fun searchByText(query: String): Flow<List<ScreenshotEntity>> {
        return screenshotDao.searchByText(query)
    }

    suspend fun createCollection(name: String, color: Int): String {
        val id = UUID.randomUUID().toString()
        collectionDao.insert(
            CollectionEntity(
                id = id,
                name = name.trim(),
                createdAt = System.currentTimeMillis(),
                color = color
            )
        )
        return id
    }

    suspend fun saveScreenshot(
        fileUri: String,
        fileName: String,
        fileSize: Long,
        width: Int,
        height: Int,
        collectionId: String
    ): ScreenshotEntity {
        val entity = ScreenshotEntity(
            id = UUID.randomUUID().toString(),
            fileUri = fileUri,
            fileName = fileName,
            fileSize = fileSize,
            width = width,
            height = height,
            takenAt = System.currentTimeMillis(),
            collectionId = collectionId,
            ocrStatus = "PENDING"
        )
        screenshotDao.insert(entity)
        return entity
    }

    suspend fun saveOcrContent(screenshotId: String, text: String, blocksJson: String = "[]") {
        screenshotDao.insertContent(
            ScreenshotContentEntity(
                screenshotId = screenshotId,
                rawText = text,
                blocksJson = blocksJson,
                indexedAt = System.currentTimeMillis()
            )
        )
        val screenshot = screenshotDao.getScreenshotById(screenshotId)
        screenshot?.let {
            val status = if (text.isBlank()) "NO_TEXT" else "COMPLETED"
            screenshotDao.update(it.copy(ocrStatus = status))
        }
    }

    suspend fun moveScreenshots(ids: List<String>, targetCollectionId: String): Int {
        return screenshotDao.moveToCollection(ids, targetCollectionId)
    }

    suspend fun deleteScreenshots(ids: List<String>): Int {
        return screenshotDao.deleteByIds(ids)
    }
}
