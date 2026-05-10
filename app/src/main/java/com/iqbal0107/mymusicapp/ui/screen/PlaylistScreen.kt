package com.iqbal0107.mymusicapp.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.iqbal0107.mymusicapp.data.Playlist
import com.iqbal0107.mymusicapp.viewmodel.LaguViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(
    viewModel: LaguViewModel,
    onNavigateBack: () -> Unit
) {
    val playlistList by viewModel.playlistList.collectAsState()
    var showTambahDialog by remember { mutableStateOf(false) }
    var editPlaylist by remember { mutableStateOf<Playlist?>(null) }
    val context = LocalContext.current

    if (showTambahDialog || editPlaylist != null) {
        PlaylistDialog(
            playlist = editPlaylist,
            onDismiss = { showTambahDialog = false; editPlaylist = null },
            onSave = { nama, deskripsi ->
                if (editPlaylist != null) {
                    viewModel.updatePlaylist(editPlaylist!!.copy(nama = nama, deskripsi = deskripsi))
                    Toast.makeText(context, "Playlist diupdate!", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.tambahPlaylist(Playlist(nama = nama, deskripsi = deskripsi))
                    Toast.makeText(context, "Playlist ditambahkan!", Toast.LENGTH_SHORT).show()
                }
                showTambahDialog = false
                editPlaylist = null
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📋 Playlist") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showTambahDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Playlist")
            }
        }
    ) { paddingValues ->
        if (playlistList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📋", style = MaterialTheme.typography.displayLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Belum ada playlist", style = MaterialTheme.typography.titleMedium)
                    Text("Tap + untuk buat playlist", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(playlistList) { playlist ->
                    PlaylistItem(
                        playlist = playlist,
                        onEdit = { editPlaylist = playlist },
                        onDelete = {
                            viewModel.hapusPlaylist(playlist)
                            Toast.makeText(context, "Playlist dihapus", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PlaylistDialog(playlist: Playlist?, onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var nama by remember { mutableStateOf(playlist?.nama ?: "") }
    var deskripsi by remember { mutableStateOf(playlist?.deskripsi ?: "") }
    var namaError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (playlist != null) "Edit Playlist" else "Tambah Playlist") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nama, onValueChange = { nama = it; namaError = false },
                    label = { Text("Nama Playlist *") }, isError = namaError,
                    supportingText = { if (namaError) Text("Nama tidak boleh kosong") },
                    singleLine = true, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = deskripsi, onValueChange = { deskripsi = it },
                    label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                namaError = nama.isBlank()
                if (!namaError) onSave(nama.trim(), deskripsi.trim())
            }) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}

@Composable
fun PlaylistItem(playlist: Playlist, onEdit: () -> Unit, onDelete: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Hapus Playlist?") },
            text = { Text("Semua lagu di \"${playlist.nama}\" juga akan dihapus!") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDialog = false }) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Batal") } }
        )
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("📋", style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(end = 12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(playlist.nama, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (playlist.deskripsi.isNotBlank()) {
                    Text(playlist.deskripsi, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, null)
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