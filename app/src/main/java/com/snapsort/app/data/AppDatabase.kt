package com.snapsort.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.snapsort.app.data.dao.CollectionDao
import com.snapsort.app.data.dao.ScreenshotDao
import com.snapsort.app.data.model.CollectionEntity
import com.snapsort.app.data.model.ScreenshotContentEntity
import com.snapsort.app.data.model.ScreenshotEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CollectionEntity::class,
        ScreenshotEntity::class,
        ScreenshotContentEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun collectionDao(): CollectionDao
    abstract fun screenshotDao(): ScreenshotDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "snapsort.db"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.collectionDao(), database.screenshotDao())
                    }
                }
            }

            suspend fun populateInitialData(collectionDao: CollectionDao, screenshotDao: ScreenshotDao) {
                // Default Unsorted collection
                collectionDao.insert(
                    CollectionEntity(
                        id = "default_unsorted",
                        name = "Unsorted",
                        createdAt = 0L,
                        color = 0xFF64748B.toInt(),
                        isDefault = true
                    )
                )

                // Recommended Starter Collections
                val starterCategories = listOf(
                    Triple("col_shopping", "Shopping", 0xFF0D9488.toInt()),
                    Triple("col_finance", "Finance & Receipts", 0xFF4F46E5.toInt()),
                    Triple("col_chat", "Chat & Social", 0xFFE11D48.toInt()),
                    Triple("col_reading", "Articles & Reading", 0xFF0284C7.toInt())
                )

                for ((id, name, color) in starterCategories) {
                    collectionDao.insert(
                        CollectionEntity(
                            id = id,
                            name = name,
                            createdAt = System.currentTimeMillis(),
                            color = color,
                            isDefault = false
                        )
                    )
                }
            }
        }
    }
}
