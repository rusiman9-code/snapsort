package com.snapsort.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snapsort.app.data.model.CollectionWithCount
import com.snapsort.app.data.model.ScreenshotEntity
import com.snapsort.app.ui.components.AddCollectionCard
import com.snapsort.app.ui.components.AppLogo
import com.snapsort.app.ui.components.CollectionCard
import com.snapsort.app.ui.components.ScreenshotCard
import com.snapsort.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    recentScreenshots: List<ScreenshotEntity>,
    collections: List<CollectionWithCount>,
    onOpenScreenshot: (String) -> Unit,
    onOpenCollection: (String, String) -> Unit,
    onNavigateSearch: () -> Unit,
    onNavigateSettings: () -> Unit,
    onCreateCollection: (String) -> Unit,
    onTriggerCapture: () -> Unit,
    onSyncGallery: () -> Unit = {}
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var newCollectionName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AppLogo(size = 32.dp)
                        Text(
                            text = "SnapSort",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = ColorText
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSyncGallery) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Sync gallery screenshots",
                            tint = ColorText
                        )
                    }
                    IconButton(onClick = onNavigateSearch) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search screenshots",
                            tint = ColorText
                        )
                    }
                    IconButton(onClick = onNavigateSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = ColorText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ColorBg)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onTriggerCapture,
                containerColor = ColorAccent,
                contentColor = ColorOnAccent,
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Take screenshot"
                )
            }
        },
        containerColor = ColorBg
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 88.dp)
        ) {
            // Quick Access Section
            if (recentScreenshots.isNotEmpty()) {
                item {
                    Text(
                        text = "Quick Access",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ColorText,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                    )
                }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(recentScreenshots) { shot ->
                            Box(modifier = Modifier.width(130.dp)) {
                                ScreenshotCard(
                                    screenshot = shot,
                                    onClick = { onOpenScreenshot(shot.id) }
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }

            // Collections Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Collections",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ColorText
                    )
                    Text(
                        text = "${collections.size} categories",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ColorTextMuted
                    )
                }
            }

            // Collections Grid (2 items per row)
            val chunked = collections.chunked(2)
            items(chunked) { pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        CollectionCard(
                            collection = pair[0],
                            onClick = { onOpenCollection(pair[0].id, pair[0].name) }
                        )
                    }
                    if (pair.size > 1) {
                        Box(modifier = Modifier.weight(1f)) {
                            CollectionCard(
                                collection = pair[1],
                                onClick = { onOpenCollection(pair[1].id, pair[1].name) }
                            )
                        }
                    } else {
                        Box(modifier = Modifier.weight(1f)) {
                            AddCollectionCard(onClick = { showAddDialog = true })
                        }
                    }
                }
            }

            if (collections.size % 2 == 0) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                    ) {
                        AddCollectionCard(onClick = { showAddDialog = true })
                    }
                }
            }

            // Empty State when no screenshots exist
            if (recentScreenshots.isEmpty() && collections.all { it.screenshotCount == 0 }) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp, vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(ColorAccentSubtle),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = ColorAccent,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Text(
                            text = "No screenshots yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ColorText
                        )
                        Text(
                            text = "Tap the floating button anywhere on your phone to capture and sort screenshots automatically.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ColorTextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("New Collection") },
            text = {
                OutlinedTextField(
                    value = newCollectionName,
                    onValueChange = { if (it.length <= 25) newCollectionName = it },
                    label = { Text("Collection name") },
                    placeholder = { Text("e.g. Travel, Work") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCollectionName.isNotBlank()) {
                            onCreateCollection(newCollectionName.trim())
                            newCollectionName = ""
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorAccent)
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
