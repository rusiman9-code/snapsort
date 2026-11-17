package com.snapsort.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.snapsort.app.data.model.ScreenshotEntity
import com.snapsort.app.ocr.OcrScanner
import com.snapsort.app.ocr.TextBlockItem
import com.snapsort.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DetailViewerScreen(
    screenshot: ScreenshotEntity,
    onBack: () -> Unit,
    onDelete: (String) -> Unit,
    onSaveOcrText: (String, String) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    var isScanning by remember { mutableStateOf(false) }
    var detectedText by remember { mutableStateOf<String?>(null) }
    var detectedBlocks by remember { mutableStateOf<List<TextBlockItem>>(emptyList()) }
    var selectedBlockIndices by remember { mutableStateOf(setOf<Int>()) }

    var showInfoDialog by remember { mutableStateOf(false) }
    var showBlocksSheet by remember { mutableStateOf(false) }
    // Index of block currently open in "free-select" mode (shows SelectionContainer)
    var freeSelectBlockIndex by remember { mutableStateOf<Int?>(null) }

    val sheetListState = rememberLazyListState()

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

    fun copyToClipboard(text: String, label: String = "Teks") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
        val snippet = if (text.length > 40) text.take(40) + "…" else text
        Toast.makeText(context, "Disalin: \"$snippet\"", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(screenshot.fileName, maxLines = 1, overflow = TextOverflow.Ellipsis, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showInfoDialog = true }) {
                        Icon(Icons.Default.Info, contentDescription = "Info", tint = Color.White)
                    }
                    IconButton(onClick = { onDelete(screenshot.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black.copy(alpha = 0.7f))
            )
        },
        bottomBar = {
            Surface(color = Color.Black.copy(alpha = 0.90f), modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Status bar
                    when {
                        isScanning -> Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = ColorAccent, strokeWidth = 2.dp)
                            Text("Memindai teks pada gambar…", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                        }
                        detectedBlocks.isNotEmpty() -> Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${detectedBlocks.size} blok teks ditemukan",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF38BDF8),
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Ketuk blok → pilih teks bebas",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.LightGray
                            )
                        }
                        else -> Text("Tidak ada teks terdeteksi", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showBlocksSheet = true },
                            enabled = detectedBlocks.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(containerColor = ColorAccent),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.SelectAll, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Blok Teks", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { if (!detectedText.isNullOrBlank()) copyToClipboard(detectedText!!, "Semua Teks") },
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
                contentDescription = "Screenshot penuh",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    // ─── Block Selection & Free-Select Sheet ──────────────────────────────────
    if (showBlocksSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBlocksSheet = false
                freeSelectBlockIndex = null
            },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = ColorSurfaceElevated,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle = {
                // Custom pill drag handle
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 4.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(ColorBorder)
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Blok Teks", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ColorText)
                        Text(
                            text = if (freeSelectBlockIndex != null)
                                "Mode pilih bebas — tekan & tahan untuk seleksi teks"
                            else
                                "Ketuk blok = pilih | Ikon teks = pilih bebas",
                            style = MaterialTheme.typography.bodySmall, color = ColorTextMuted
                        )
                    }

                    if (freeSelectBlockIndex == null) {
                        TextButton(onClick = {
                            if (selectedBlockIndices.size == detectedBlocks.size) {
                                selectedBlockIndices = emptySet()
                            } else {
                                selectedBlockIndices = detectedBlocks.indices.toSet()
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        }) {
                            Text(
                                text = if (selectedBlockIndices.size == detectedBlocks.size) "Batal Semua" else "Pilih Semua",
                                color = ColorAccent, fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        TextButton(onClick = { freeSelectBlockIndex = null }) {
                            Text("← Kembali", color = ColorAccent, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Selected blocks action bar
                AnimatedVisibility(
                    visible = selectedBlockIndices.isNotEmpty() && freeSelectBlockIndex == null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    val combinedText = selectedBlockIndices.sorted().joinToString("\n") { detectedBlocks[it].text }
                    Surface(
                        color = ColorAccentSubtle,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "${selectedBlockIndices.size} blok dipilih",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = ColorAccent
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { copyToClipboard(combinedText, "Blok Terpilih") },
                                    colors = ButtonDefaults.buttonColors(containerColor = ColorAccent),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Salin", fontSize = 13.sp)
                                }
                                OutlinedButton(
                                    onClick = {
                                        val i = Intent(Intent.ACTION_WEB_SEARCH).apply {
                                            putExtra(android.app.SearchManager.QUERY, combinedText)
                                        }
                                        context.startActivity(i)
                                    },
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Search, null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Cari", fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }

                // Free-select mode: show single block with native text selection
                if (freeSelectBlockIndex != null) {
                    val block = detectedBlocks[freeSelectBlockIndex!!]
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Tekan dan tahan teks di bawah untuk memilih kata atau kalimat tertentu:",
                            style = MaterialTheme.typography.bodySmall,
                            color = ColorTextMuted
                        )
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = ColorSurface,
                            border = androidx.compose.foundation.BorderStroke(2.dp, ColorAccent),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // SelectionContainer enables native Android text selection
                            // (long-press → drag handles → Copy/Share from Android toolbar)
                            SelectionContainer {
                                Text(
                                    text = block.text,
                                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 26.sp),
                                    color = ColorText,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                )
                            }
                        }
                        Text(
                            text = "💡 Gunakan handle biru dari Android untuk menyeret dan memilih teks secara bebas, lalu ketuk \"Salin\" dari menu yang muncul.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ColorTextMuted,
                            lineHeight = 18.sp
                        )
                    }
                } else {
                    // Block list with smooth scrolling
                    LazyColumn(
                        state = sheetListState,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 440.dp)
                    ) {
                        itemsIndexed(
                            items = detectedBlocks,
                            key = { index, _ -> index }
                        ) { index, block ->
                            val isSelected = selectedBlockIndices.contains(index)

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) ColorAccentSubtle else ColorSurface,
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) ColorAccent else ColorBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateItemPlacement()
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
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
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    // Block text (expandable naturally)
                                    Column(modifier = Modifier.weight(1f)) {
                                        // Blok number badge
                                        Text(
                                            text = "Blok ${index + 1}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isSelected) ColorAccent else ColorTextMuted,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(bottom = 2.dp)
                                        )
                                        Text(
                                            text = block.text,
                                            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                                            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                                            color = ColorText
                                        )
                                    }

                                    // Action icons (right column)
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(0.dp)
                                    ) {
                                        // Free select (native selection)
                                        IconButton(
                                            onClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                freeSelectBlockIndex = index
                                            },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.TextFields,
                                                contentDescription = "Pilih teks bebas",
                                                tint = ColorAccent,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        // Quick copy entire block
                                        IconButton(
                                            onClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                copyToClipboard(block.text, "Blok ${index + 1}")
                                            },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.ContentCopy,
                                                contentDescription = "Salin blok ini",
                                                tint = ColorTextMuted,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        // Web search
                                        IconButton(
                                            onClick = {
                                                val i = Intent(Intent.ACTION_WEB_SEARCH).apply {
                                                    putExtra(android.app.SearchManager.QUERY, block.text)
                                                }
                                                context.startActivity(i)
                                            },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Search,
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
                }
            }
        }
    }

    // Info Dialog
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text("Info Screenshot") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("File: ${screenshot.fileName}", fontWeight = FontWeight.SemiBold)
                    Text("Status OCR: ${screenshot.ocrStatus}")
                    Text("Blok Teks: ${detectedBlocks.size} blok")
                    Text("Diambil: ${java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale.getDefault()).format(screenshot.takenAt)}")
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) { Text("Tutup") }
            }
        )
    }
}
