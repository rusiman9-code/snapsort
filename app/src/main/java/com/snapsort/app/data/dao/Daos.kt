package com.snapsort.app.data.dao

import androidx.room.*
import com.snapsort.app.data.model.CollectionEntity
import com.snapsort.app.data.model.CollectionWithCount
import com.snapsort.app.data.model.ScreenshotContentEntity
import com.snapsort.app.data.model.ScreenshotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {
    @Query("SELECT * FROM collections ORDER BY is_default DESC, created_at DESC")
    fun getAllCollections(): Flow<List<CollectionEntity>>

    @Query("""
        SELECT 
            c.id, 
            c.name, 
            c.color, 
            c.is_default AS isDefault, 
            COUNT(s.id) AS screenshotCount
        FROM collections c
        LEFT JOIN screenshots s ON c.id = s.collection_id
        GROUP BY c.id
        ORDER BY c.is_default DESC, c.created_at DESC
    """)
    fun getCollectionsWithCount(): Flow<List<CollectionWithCount>>

    @Query("SELECT * FROM collections WHERE id = :id LIMIT 1")
    suspend fun getCollectionById(id: String): CollectionEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(collection: CollectionEntity): Long

    @Update
    suspend fun update(collection: CollectionEntity)

    @Query("DELETE FROM collections WHERE id = :id AND is_default = 0")
    suspend fun deleteById(id: String): Int
}

@Dao
interface ScreenshotDao {
    @Query("SELECT * FROM screenshots ORDER BY taken_at DESC")
    fun getAllScreenshots(): Flow<List<ScreenshotEntity>>

    @Query("SELECT * FROM screenshots ORDER BY taken_at DESC LIMIT :limit")
    fun getRecentScreenshots(limit: Int = 10): Flow<List<ScreenshotEntity>>

    @Query("SELECT * FROM screenshots WHERE collection_id = :collectionId ORDER BY taken_at DESC")
    fun getScreenshotsByCollection(collectionId: String): Flow<List<ScreenshotEntity>>

    @Query("SELECT * FROM screenshots WHERE id = :id LIMIT 1")
    suspend fun getScreenshotById(id: String): ScreenshotEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(screenshot: ScreenshotEntity): Long

    @Update
    suspend fun update(screenshot: ScreenshotEntity)

    @Query("UPDATE screenshots SET collection_id = :targetCollectionId WHERE id IN (:ids)")
    suspend fun moveToCollection(ids: List<String>, targetCollectionId: String): Int

    @Query("DELETE FROM screenshots WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<String>): Int

    // Search query joining with screenshot_contents
    @Query("""
        SELECT s.* FROM screenshots s
        INNER JOIN screenshot_contents sc ON s.id = sc.screenshot_id
        WHERE sc.raw_text LIKE '%' || :query || '%'
        ORDER BY s.taken_at DESC
    """)
    fun searchByText(query: String): Flow<List<ScreenshotEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContent(content: ScreenshotContentEntity)

    @Query("SELECT * FROM screenshot_contents WHERE screenshot_id = :screenshotId LIMIT 1")
    suspend fun getContent(screenshotId: String): ScreenshotContentEntity?
}
