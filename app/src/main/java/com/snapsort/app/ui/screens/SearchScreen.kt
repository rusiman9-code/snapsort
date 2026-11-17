package com.snapsort.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.snapsort.app.data.model.ScreenshotEntity
import com.snapsort.app.ui.components.ScreenshotCard
import com.snapsort.app.ui.components.SearchInputBar
import com.snapsort.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchResults: List<ScreenshotEntity>,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit,
    onOpenScreenshot: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    SearchInputBar(
                        query = searchQuery,
                        onQueryChange = onQueryChange,
                        onClear = { onQueryChange("") }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = ColorText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ColorBg)
            )
        },
        containerColor = ColorBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (searchQuery.isNotBlank()) {
                Text(
                    text = "${searchResults.size} results found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ColorTextMuted,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            if (searchQuery.isNotBlank() && searchResults.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "No results found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ColorText
                        )
                        Text(
                            text = "Try searching for a different word or phrase.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ColorTextMuted
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(searchResults) { shot ->
                        ScreenshotCard(
                            screenshot = shot,
                            onClick = { onOpenScreenshot(shot.id) }
                        )
                    }
                }
            }
        }
    }
}
