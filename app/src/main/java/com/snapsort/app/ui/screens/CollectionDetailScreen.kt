package com.snapsort.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileMove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.snapsort.app.data.model.CollectionWithCount
import com.snapsort.app.data.model.ScreenshotEntity
import com.snapsort.app.ui.components.ScreenshotCard
import com.snapsort.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionDetailScreen(
    collectionId: String,
    collectionName: String,
    screenshots: List<ScreenshotEntity>,
    collections: List<CollectionWithCount>,
    onBack: () -> Unit,
    onOpenScreenshot: (String) -> Unit,
    onDeleteScreenshots: (List<String>) -> Unit,
    onMoveScreenshots: (List<String>, String) -> Unit
) {
    var selectedIds by remember { mutableStateOf(setOf<String>()) }
    val isSelectionMode = selectedIds.isNotEmpty()
    var showMoveDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isSelectionMode) "${selectedIds.size} selected" else collectionName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = ColorText
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isSelectionMode) selectedIds = emptySet() else onBack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = ColorText
                        )
                    }
                },
                actions = {
                    if (isSelectionMode) {
                        IconButton(onClick = { showMoveDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.DriveFileMove,
                                contentDescription = "Move",
                                tint = ColorAccent
                            )
                        }
                        IconButton(onClick = {
                            onDeleteScreenshots(selectedIds.toList())
                            selectedIds = emptySet()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = ColorDanger
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ColorBg)
            )
        },
        containerColor = ColorBg
    ) { paddingValues ->
        if (screenshots.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No screenshots in this collection",
                    style = MaterialTheme.typography.bodyLarge,
                    color = ColorTextMuted
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(screenshots) { shot ->
                    val isSelected = selectedIds.contains(shot.id)
                    ScreenshotCard(
                        screenshot = shot,
                        isSelected = isSelected,
                        isSelectionMode = isSelectionMode,
                        onClick = {
                            if (isSelectionMode) {
                                selectedIds = if (isSelected) {
                                    selectedIds - shot.id
                                } else {
                                    selectedIds + shot.id
                                }
                            } else {
                                onOpenScreenshot(shot.id)
                            }
                        },
                        onLongClick = {
                            selectedIds = selectedIds + shot.id
                        }
                    )
                }
            }
        }
    }

    if (showMoveDialog) {
        AlertDialog(
            onDismissRequest = { showMoveDialog = false },
            title = { Text("Move ${selectedIds.size} screenshots to") },
            text = {
                Column {
                    collections.filter { it.id != collectionId }.forEach { col ->
                        TextButton(
                            onClick = {
                                onMoveScreenshots(selectedIds.toList(), col.id)
                                selectedIds = emptySet()
                                showMoveDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(col.name, color = ColorText)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showMoveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
