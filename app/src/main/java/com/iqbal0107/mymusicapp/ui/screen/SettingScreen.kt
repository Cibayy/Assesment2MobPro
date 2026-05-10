package com.iqbal0107.mymusicapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val themeColors = mapOf(
    "Purple" to Color(0xFF6650A4),
    "Blue"   to Color(0xFF1565C0),
    "Green"  to Color(0xFF2E7D32),
    "Red"    to Color(0xFFC62828),
    "Orange" to Color(0xFFE65100)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    themeColor: String,
    onToggleDarkMode: () -> Unit,
    onChangeThemeColor: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🎨 Pengaturan Tema") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Mode Gelap", style = MaterialTheme.typography.titleMedium)
                        Text("Aktifkan tampilan gelap",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = isDarkMode, onCheckedChange = { onToggleDarkMode() })
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Warna Tema", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Pilih warna utama aplikasi",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        themeColors.forEach { (name, color) ->
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (themeColor == name) 3.dp else 0.dp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        shape = CircleShape
                                    )
                                    .clickable { onChangeThemeColor(name) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (themeColor == name) {
                                    Icon(Icons.Default.Check, contentDescription = null,
                                        tint = Color.White, modifier = Modifier.size(24.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}