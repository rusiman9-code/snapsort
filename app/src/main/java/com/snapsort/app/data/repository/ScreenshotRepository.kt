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
