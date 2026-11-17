package com.snapsort.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.snapsort.app.data.model.ScreenshotEntity
import com.snapsort.app.ocr.OcrScanner
import com.snapsort.app.ocr.TextBlockItem
import com.snapsort.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailViewerScreen(
    screenshot: ScreenshotEntity,
    onBack: () -> Unit,
    onDelete: (String) -> Unit,
    onSaveOcrText: (String, String) -> Unit
) {
    val context = LocalContext.current
    var isScanning by remember { mutableStateOf(false) }
    var detectedText by remember { mutableStateOf<String?>(null) }
    var detectedBlocks by remember { mutableStateOf<List<TextBlockItem>>(emptyList()) }
    var selectedBlockIndices by remember { mutableStateOf(setOf<Int>()) }

    var showInfoDialog by remember { mutableStateOf(false) }
    var showBlocksSheet by remember { mutableStateOf(false) }

    // Run OCR scan on enter
    LaunchedEffect(screenshot.id) {
        isScanning = true
        try {
            val scanner = OcrScanner(context)
            val uri = Uri.parse(screenshot.fileUri)
            val result = scanner.processImage(uri)
            detectedText = result.fullText
            detectedBlocks = result.blocks
            if (result.fullText.isNotBlank()) {
                onSaveOcrText(screenshot.id, result.fullText)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isScanning = false
        }
    }

    fun copyTextToClipboard(text: String, label: String = "Teks") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        val snippet = if (text.length > 40) text.take(40) + "…" else text
        Toast.makeText(context, "Disalin: \"$snippet\"", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(screenshot.fileName, maxLines = 1, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showInfoDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { onDelete(screenshot.id) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.7f),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Surface(
                color = Color.Black.copy(alpha = 0.9f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    if (isScanning) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = ColorAccent,
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = "Memindai teks pada gambar…",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                        }
                    } else if (detectedBlocks.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${detectedBlocks.size} blok teks terdeteksi",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF38BDF8), // Light sky blue
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Ketuk untuk memilih blok",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.LightGray
                            )
                        }
                    } else {
                        Text(
                            text = "Tidak ada teks terdeteksi",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Main Action: Open Block Selection Sheet
                        Button(
                            onClick = { showBlocksSheet = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ColorAccent),
                            enabled = detectedBlocks.isNotEmpty(),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.SelectAll, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Pilih Blok Teks", fontWeight = FontWeight.Bold)
                        }

                        // Copy All Button
                        OutlinedButton(
                            onClick = {
                                if (!detectedText.isNullOrBlank()) {
                                    copyTextToClipboard(detectedText!!, "Semua Teks")
                                }
                            },
                            enabled = !detectedText.isNullOrBlank(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Salin Semua")
                        }
                    }
                }
            }
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = screenshot.fileUri,
                contentDescription = "Full screenshot",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    // Modal Bottom Sheet: Select & Copy Text Blocks
    if (showBlocksSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBlocksSheet = false },
            containerColor = ColorSurfaceElevated,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Blok Teks Terdeteksi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ColorText
                        )
                        Text(
                            text = "Ketuk blok teks untuk memilih dan menyalin",
                            style = MaterialTheme.typography.bodySmall,
                            color = ColorTextMuted
                        )
                    }

                    TextButton(
                        onClick = {
                            if (selectedBlockIndices.size == detectedBlocks.size) {
                                selectedBlockIndices = emptySet()
                            } else {
                                selectedBlockIndices = detectedBlocks.indices.toSet()
                            }
                        }
                    ) {
                        Text(
                            text = if (selectedBlockIndices.size == detectedBlocks.size) "Batal Pilih" else "Pilih Semua",
                            color = ColorAccent,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Action Bar if one or more blocks are selected
                if (selectedBlockIndices.isNotEmpty()) {
                    val combinedSelectedText = selectedBlockIndices.sorted()
                        .map { detectedBlocks[it].text }
                        .joinToString("\n")

                    Surface(
                        color = ColorAccentSubtle,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${selectedBlockIndices.size} blok dipilih",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = ColorAccent
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        copyTextToClipboard(combinedSelectedText, "Blok Terpilih")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ColorAccent),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Salin", fontSize = 13.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        val searchIntent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                                            putExtra(android.app.SearchManager.QUERY, combinedSelectedText)
                                        }
                                        context.startActivity(searchIntent)
                                    },
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Cari", fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // List of Text Blocks
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .heightIn(max = 420.dp)
                ) {
                    itemsIndexed(detectedBlocks) { index, block ->
                        val isSelected = selectedBlockIndices.contains(index)

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) ColorAccentSubtle else ColorSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) ColorAccent else ColorBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedBlockIndices = if (isSelected) {
                                        selectedBlockIndices - index
                                    } else {
                                        selectedBlockIndices + index
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = block.text,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        color = ColorText
                                    )
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Instant Quick Copy Button for this specific block
                                    IconButton(
                                        onClick = {
                                            copyTextToClipboard(block.text, "Blok Teks")
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Salin blok ini",
                                            tint = ColorAccent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    // Quick Web Search for this specific block
                                    IconButton(
                                        onClick = {
                                            val searchIntent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                                                putExtra(android.app.SearchManager.QUERY, block.text)
                                            }
                                            context.startActivity(searchIntent)
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Cari di web",
                                            tint = ColorTextMuted,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text("Screenshot Info") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("File: ${screenshot.fileName}", fontWeight = FontWeight.SemiBold)
                    Text("Status: ${screenshot.ocrStatus}")
                    Text("Blok Teks: ${detectedBlocks.size} blok")
                    Text("Taken: ${java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale.getDefault()).format(screenshot.takenAt)}")
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }
}
