package com.snapsort.app.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.snapsort.app.service.FloatingCaptureService
import com.snapsort.app.service.RequestCaptureActivity
import com.snapsort.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onSyncGallery: () -> Unit = {}
) {
    val context = LocalContext.current
    var isOverlayEnabled by remember {
        mutableStateOf(Settings.canDrawOverlays(context))
    }
    var askToSort by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = ColorText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ColorBg)
            )
        },
        containerColor = ColorBg
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                Text(
                    text = "Floating Button",
                    style = MaterialTheme.typography.labelSmall,
                    color = ColorAccent,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (!Settings.canDrawOverlays(context)) {
                                val intent = Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:${context.packageName}")
                                )
                                context.startActivity(intent)
                            } else {
                                if (isOverlayEnabled) {
                                    context.stopService(Intent(context, FloatingCaptureService::class.java))
                                    isOverlayEnabled = false
                                } else {
                                    val intent = Intent(context, RequestCaptureActivity::class.java)
                                    context.startActivity(intent)
                                    isOverlayEnabled = true
                                }
                            }
                        }
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Enable Floating Capture Button",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = ColorText
                        )
                        Text(
                            text = "Display an edge button over other apps to take screenshots anytime.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ColorTextMuted
                        )
                    }
                    Switch(
                        checked = isOverlayEnabled,
                        onCheckedChange = { checked ->
                            if (checked && !Settings.canDrawOverlays(context)) {
                                val intent = Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:${context.packageName}")
                                )
                                context.startActivity(intent)
                            } else {
                                isOverlayEnabled = checked
                                if (checked) {
                                    context.startActivity(Intent(context, RequestCaptureActivity::class.java))
                                } else {
                                    context.stopService(Intent(context, FloatingCaptureService::class.java))
                                }
                            }
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = ColorAccent)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ask to Sort After Capture",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = ColorText
                        )
                        Text(
                            text = "Show the sorting popup immediately after taking a new screenshot.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ColorTextMuted
                        )
                    }
                    Switch(
                        checked = askToSort,
                        onCheckedChange = { askToSort = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = ColorAccent)
                    )
                }
            }

            item { HorizontalDivider(color = ColorBorder, modifier = Modifier.padding(vertical = 12.dp)) }

            item {
                Text(
                    text = "Gallery & Storage",
                    style = MaterialTheme.typography.labelSmall,
                    color = ColorAccent,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSyncGallery() }
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Sync Device Screenshots",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = ColorText
                        )
                        Text(
                            text = "Scan and import screenshots from your phone gallery into SnapSort.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ColorTextMuted
                        )
                    }
                    Button(
                        onClick = onSyncGallery,
                        colors = ButtonDefaults.buttonColors(containerColor = ColorAccentSubtle)
                    ) {
                        Text("Sync Now", color = ColorAccent, fontWeight = FontWeight.Bold)
                    }
                }
            }

            item { HorizontalDivider(color = ColorBorder, modifier = Modifier.padding(vertical = 12.dp)) }

            item {
                Text(
                    text = "Privacy & About",
                    style = MaterialTheme.typography.labelSmall,
                    color = ColorAccent,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "100% On-Device & Private",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = ColorText
                    )
                    Text(
                        text = "SnapSort operates completely offline. Your screenshots and extracted texts never leave your device.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ColorTextMuted
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "SnapSort v1.0.0",
                        style = MaterialTheme.typography.bodySmall,
                        color = ColorTextMuted
                    )
                }
            }
        }
    }
}
