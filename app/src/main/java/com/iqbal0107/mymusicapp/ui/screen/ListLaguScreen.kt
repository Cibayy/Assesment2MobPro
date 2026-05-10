package com.iqbal0107.mymusicapp.ui.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.iqbal0107.mymusicapp.data.Lagu
import com.iqbal0107.mymusicapp.viewmodel.LaguViewModel

val moodList = listOf("Semua", "Happy", "Sedih", "Fokus", "Santai", "Semangat")

fun moodEmoji(mood: String): String = when (mood) {
    "Happy"    -> "😊"
    "Sedih"    -> "😢"
    "Fokus"    -> "🎯"
    "Santai"   -> "😌"
    "Semangat" -> "🔥"
    else       -> "🎵"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListLaguScreen(
    viewModel: LaguViewModel,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onNavigateToTambah: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    onNavigateToRecycleBin: () -> Unit,
    onNavigateToPlaylist: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val laguList by viewModel.laguList.collectAsState()
    val selectedMood by viewModel.selectedMood.collectAsState()
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🎵 My Playlist") },
                actions = {
                    IconButton(onClick = onToggleDarkMode) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Dark Mode"
                        )
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("🗑️ Recycle Bin") },
                                onClick = { showMenu = false; onNavigateToRecycleBin() }
                            )
                            DropdownMenuItem(
                                text = { Text("📋 Playlist") },
                                onClick = { showMenu = false; onNavigateToPlaylist() }
                            )
                            DropdownMenuItem(
                                text = { Text("🎨 Pengaturan Tema") },
                                onClick = { showMenu = false; onNavigateToSettings() }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToTambah) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Lagu")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            LazyRow(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(moodList) { mood ->
                    FilterChip(
                        selected = selectedMood == mood,
                        onClick = { viewModel.filterByMood(mood) },
                        label = { Text(if (mood == "Semua") "Semua" else "${moodEmoji(mood)} $mood") }
                    )
                }
            }

            if (laguList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎵", style = MaterialTheme.typography.displayLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Belum ada lagu", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Tap + untuk tambah lagu favorit",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(laguList) { lagu ->
                        LaguItem(
                            lagu = lagu,
                            onEdit = { onNavigateToEdit(lagu.id) },
                            onDelete = { viewModel.moveToRecycleBin(lagu.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LaguItem(lagu: Lagu, onEdit: () -> Unit, onDelete: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Hapus Lagu?") },
            text = { Text("\"${lagu.judul}\" akan dipindahkan ke Recycle Bin.") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDialog = false
                    Toast.makeText(context, "Dipindahkan ke Recycle Bin", Toast.LENGTH_SHORT).show()
                }) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Batal") }
            }
        )
    }

    Card(modifier = Modifier.fillMaxWidth().clickable { onEdit() }) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                moodEmoji(lagu.mood),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(end = 12.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(lagu.judul, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    "${lagu.artis} • ${lagu.genre}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(lagu.mood, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary)
            }
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = { showMenu = false; onEdit() },
                        leadingIcon = { Icon(Icons.Default.Edit, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Hapus") },
                        onClick = { showMenu = false; showDialog = true },
                        leadingIcon = { Icon(Icons.Default.Delete, null) }
                    )
                }
            }
        }
    }
}