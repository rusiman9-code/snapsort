package com.snapsort.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import com.snapsort.app.ui.screens.*
import com.snapsort.app.ui.theme.SnapSortTheme
import kotlinx.coroutines.launch

sealed class Screen {
    object Onboarding : Screen()
    object Home : Screen()
    data class Collection(val id: String, val name: String) : Screen()
    data class Detail(val id: String) : Screen()
    object Search : Screen()
    object Settings : Screen()
}

class MainActivity : ComponentActivity() {

    private val repository by lazy { (application as SnapSortApplication).repository }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SnapSortTheme {
                val scope = rememberCoroutineScope()
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
                var showSortingSheet by remember { mutableStateOf(false) }
                var pendingCapturedId by remember { mutableStateOf<String?>(null) }

                // State from Repository
                val recentScreenshots by repository.recentScreenshots.collectAsState(initial = emptyList())
                val allScreenshots by repository.allScreenshots.collectAsState(initial = emptyList())
                val collections by repository.collectionsWithCount.collectAsState(initial = emptyList())

                var searchQuery by remember { mutableStateOf("") }
                val searchResults by repository.searchByText(searchQuery).collectAsState(initial = emptyList())

                // Function to sync gallery
                fun doSyncGallery() {
                    scope.launch {
                        val imported = repository.syncGalleryScreenshots()
                        val msg = if (imported > 0) {
                            "Imported $imported new screenshots from gallery"
                        } else {
                            "Gallery is up to date"
                        }
                        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                    }
                }

                // Storage permission launcher
                val storagePermissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    val granted = permissions.values.any { it }
                    if (granted) {
                        doSyncGallery()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Storage permission needed to read screenshots from gallery",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                // Check permissions on launch
                LaunchedEffect(Unit) {
                    val permissionsToRequest = mutableListOf<String>()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_MEDIA_IMAGES)
                            != PackageManager.PERMISSION_GRANTED) {
                            permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
                        }
                    } else {
                        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    }

                    if (permissionsToRequest.isNotEmpty()) {
                        storagePermissionLauncher.launch(permissionsToRequest.toTypedArray())
                    } else {
                        // Already granted, auto sync
                        scope.launch { repository.syncGalleryScreenshots() }
                    }
                }

                // Handle incoming Intent to trigger sorting prompt
                LaunchedEffect(intent) {
                    if (intent.getBooleanExtra("trigger_sort_prompt", false)) {
                        pendingCapturedId = intent.getStringExtra("captured_id")
                        showSortingSheet = true
                    }
                }

                when (val screen = currentScreen) {
                    is Screen.Onboarding -> {
                        OnboardingScreen(
                            onComplete = { currentScreen = Screen.Home }
                        )
                    }
                    is Screen.Home -> {
                        HomeScreen(
                            recentScreenshots = recentScreenshots,
                            collections = collections,
                            onOpenScreenshot = { id -> currentScreen = Screen.Detail(id) },
                            onOpenCollection = { id, name -> currentScreen = Screen.Collection(id, name) },
                            onNavigateSearch = { currentScreen = Screen.Search },
                            onNavigateSettings = { currentScreen = Screen.Settings },
                            onCreateCollection = { name ->
                                scope.launch {
                                    repository.createCollection(name, 0xFF0284C7.toInt())
                                }
                            },
                            onTriggerCapture = {
                                showSortingSheet = true
                            },
                            onSyncGallery = {
                                doSyncGallery()
                            }
                        )
                    }
                    is Screen.Collection -> {
                        val colScreenshots by repository.getScreenshotsByCollection(screen.id)
                            .collectAsState(initial = emptyList())

                        CollectionDetailScreen(
                            collectionId = screen.id,
                            collectionName = screen.name,
                            screenshots = colScreenshots,
                            collections = collections,
                            onBack = { currentScreen = Screen.Home },
                            onOpenScreenshot = { id -> currentScreen = Screen.Detail(id) },
                            onDeleteScreenshots = { ids ->
                                scope.launch { repository.deleteScreenshots(ids) }
                            },
                            onMoveScreenshots = { ids, targetId ->
                                scope.launch { repository.moveScreenshots(ids, targetId) }
                            }
                        )
                    }
                    is Screen.Detail -> {
                        val shot = allScreenshots.find { it.id == screen.id }
                        if (shot != null) {
                            DetailViewerScreen(
                                screenshot = shot,
                                onBack = { currentScreen = Screen.Home },
                                onDelete = { id ->
                                    scope.launch {
                                        repository.deleteScreenshots(listOf(id))
                                        currentScreen = Screen.Home
                                    }
                                },
                                onSaveOcrText = { id, text ->
                                    scope.launch { repository.saveOcrContent(id, text) }
                                }
                            )
                        } else {
                            currentScreen = Screen.Home
                        }
                    }
                    is Screen.Search -> {
                        SearchScreen(
                            searchResults = if (searchQuery.isBlank()) emptyList() else searchResults,
                            searchQuery = searchQuery,
                            onQueryChange = { searchQuery = it },
                            onBack = { currentScreen = Screen.Home },
                            onOpenScreenshot = { id -> currentScreen = Screen.Detail(id) }
                        )
                    }
                    is Screen.Settings -> {
                        SettingsScreen(
                            onBack = { currentScreen = Screen.Home },
                            onSyncGallery = { doSyncGallery() }
                        )
                    }
                }

                // Sorting Panel Modal Sheet
                if (showSortingSheet) {
                    SortingPanelSheet(
                        collections = collections,
                        onSelectCollection = { colId ->
                            pendingCapturedId?.let { capId ->
                                scope.launch {
                                    repository.moveScreenshots(listOf(capId), colId)
                                    pendingCapturedId = null
                                }
                            }
                            showSortingSheet = false
                        },
                        onSkip = {
                            pendingCapturedId = null
                            showSortingSheet = false
                        },
                        onCreateCollection = { name ->
                            scope.launch {
                                val newId = repository.createCollection(name, 0xFF0284C7.toInt())
                                pendingCapturedId?.let { capId ->
                                    repository.moveScreenshots(listOf(capId), newId)
                                    pendingCapturedId = null
                                }
                            }
                            showSortingSheet = false
                        }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}
