package com.snapsort.app

import android.app.Application
import com.snapsort.app.data.AppDatabase
import com.snapsort.app.data.repository.ScreenshotRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class SnapSortApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val repository by lazy {
        ScreenshotRepository(
            database.collectionDao(),
            database.screenshotDao(),
            this
        )
    }

    override fun onCreate() {
        super.onCreate()
    }
}
