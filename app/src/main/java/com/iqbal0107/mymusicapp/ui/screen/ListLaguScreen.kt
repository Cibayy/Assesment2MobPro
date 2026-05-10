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
    onNavigateToEdit: (Int) -> Unit
) {
    val laguList by viewModel.laguList.collectAsState()
    val selectedMood by viewModel.selectedMood.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🎵 My Playlist") },
                actions = {
                    // Toggle Dark Mode
                    IconButton(onClick = onToggleDarkMode) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Dark Mode"
                        )
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

            // Filter Mood (LazyRow chip)
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

            // Empty State
            if (laguList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🎵", style = MaterialTheme.typography.displayLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Belum ada lagu",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Tap + untuk tambah lagu favorit",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                // List Lagu dengan padding bawah agar tidak tertutup FAB
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 8.dp, end = 8.dp,
                        top = 4.dp, bottom = 80.dp // padding bawah agar tidak tertutup FAB
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(laguList) { lagu ->
                        LaguItem(
                            lagu = lagu,
                            onEdit = { onNavigateToEdit(lagu.id) },
                            onDelete = { viewModel.hapusLagu(lagu) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LaguItem(
    lagu: Lagu,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Dialog Konfirmasi Hapus
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Hapus Lagu?") },
            text = { Text("Yakin ingin menghapus \"${lagu.judul}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDialog = false
                    Toast.makeText(context, "Lagu dihapus", Toast.LENGTH_SHORT).show()
                }) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Batal") }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji mood sebagai "gambar"
            Text(
                text = moodEmoji(lagu.mood),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(end = 12.dp)
            )

            // Info Lagu (2 komponen: judul + artis/genre)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lagu.judul,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${lagu.artis} • ${lagu.genre}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = lagu.mood,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Overflow menu
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
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