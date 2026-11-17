package com.snapsort.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
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

                // State from Repository
                val recentScreenshots by repository.recentScreenshots.collectAsState(initial = emptyList())
                val allScreenshots by repository.allScreenshots.collectAsState(initial = emptyList())
                val collections by repository.collectionsWithCount.collectAsState(initial = emptyList())

                var searchQuery by remember { mutableStateOf("") }
                val searchResults by repository.searchByText(searchQuery).collectAsState(initial = emptyList())

                // Handle incoming Intent to trigger sorting prompt
                LaunchedEffect(intent) {
                    if (intent.getBooleanExtra("trigger_sort_prompt", false)) {
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
                            onBack = { currentScreen = Screen.Home }
                        )
                    }
                }

                // Sorting Panel Modal Sheet
                if (showSortingSheet) {
                    SortingPanelSheet(
                        collections = collections,
                        onSelectCollection = { _ ->
                            showSortingSheet = false
                        },
                        onSkip = {
                            showSortingSheet = false
                        },
                        onCreateCollection = { name ->
                            scope.launch {
                                repository.createCollection(name, 0xFF0284C7.toInt())
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
