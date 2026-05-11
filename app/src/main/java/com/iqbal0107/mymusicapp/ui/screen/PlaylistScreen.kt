@file:Suppress("SpellCheckingInspection")

package com.iqbal0107.mymusicapp.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.iqbal0107.mymusicapp.data.Lagu
import com.iqbal0107.mymusicapp.data.Playlist
import com.iqbal0107.mymusicapp.viewmodel.LaguViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(
    viewModel: LaguViewModel,
    onNavigateBack: () -> Unit
) {
    val playlistList by viewModel.playlistList.collectAsState()
    val laguList by viewModel.laguList.collectAsState()

    // State untuk melacak playlist mana yang sedang dibuka
    var selectedPlaylist by remember { mutableStateOf<Playlist?>(null) }
    var showTambahDialog by remember { mutableStateOf(false) }
    var editPlaylist by remember { mutableStateOf<Playlist?>(null) }
    val context = LocalContext.current
    val primaryColor = MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = selectedPlaylist?.nama ?: "Koleksi Playlist",
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedPlaylist != null) selectedPlaylist = null else onNavigateBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            if (selectedPlaylist == null) {
                ExtendedFloatingActionButton(
                    onClick = { showTambahDialog = true },
                    containerColor = primaryColor,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Playlist Baru") }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (selectedPlaylist != null) {
                // --- VIEW 1: DETAIL PLAYLIST (LAGU-LAGU DI DALAMNYA) ---
                val laguDiPlaylist = laguList.filter { it.playlistId == selectedPlaylist!!.id }

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Column(modifier = Modifier.padding(bottom = 16.dp)) {
                            Text(
                                text = selectedPlaylist?.deskripsi?.ifBlank { "Tidak ada deskripsi" } ?: "",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${laguDiPlaylist.size} Lagu",
                                style = MaterialTheme.typography.labelLarge,
                                color = primaryColor,
                                fontWeight = FontWeight.Bold
                            )
                            HorizontalDivider(modifier = Modifier.padding(top = 16.dp), thickness = 0.5.dp)
                        }
                    }

                    if (laguDiPlaylist.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                                Text("Playlist ini masih kosong 🎵", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    items(laguDiPlaylist) { lagu ->
                        LaguDiPlaylistCard(lagu = lagu)
                    }
                }
            } else {
                // --- VIEW 2: DAFTAR SEMUA PLAYLIST ---
                if (playlistList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📋", style = MaterialTheme.typography.displayLarge)
                            Text("Belum ada playlist", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(playlistList) { playlist ->
                            PlaylistItemModern(
                                playlist = playlist,
                                onClick = { selectedPlaylist = playlist },
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
    }

    if (showTambahDialog || editPlaylist != null) {
        PlaylistDialog(
            playlist = editPlaylist,
            onDismiss = { showTambahDialog = false; editPlaylist = null },
            onSave = { nama, deskripsi ->
                if (editPlaylist != null) {
                    viewModel.updatePlaylist(editPlaylist!!.copy(nama = nama, deskripsi = deskripsi))
                } else {
                    viewModel.tambahPlaylist(Playlist(nama = nama, deskripsi = deskripsi))
                }
                showTambahDialog = false; editPlaylist = null
            }
        )
    }
}

@Composable
fun PlaylistItemModern(
    playlist: Playlist,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("📋", style = MaterialTheme.typography.headlineSmall)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = playlist.nama,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = playlist.deskripsi.ifBlank { "Koleksi lagu favorit" },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = null)
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = { showMenu = false; onEdit() },
                        leadingIcon = { Icon(Icons.Default.Edit, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Hapus") },
                        onClick = { showMenu = false; onDelete() },
                        leadingIcon = { Icon(Icons.Default.Delete, null) }
                    )
                }
            }
        }
    }
}

@Composable
fun LaguDiPlaylistCard(lagu: Lagu) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(50.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("🎵", style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = lagu.judul,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = lagu.artis,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PlaylistDialog(
    playlist: Playlist?,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var nama by remember { mutableStateOf(playlist?.nama ?: "") }
    var deskripsi by remember { mutableStateOf(playlist?.deskripsi ?: "") }
    var namaError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (playlist != null) "Edit Playlist" else "Playlist Baru") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nama,
                    onValueChange = { nama = it; namaError = false },
                    label = { Text("Nama Playlist") },
                    isError = namaError,
                    supportingText = { if (namaError) Text("Nama tidak boleh kosong") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    label = { Text("Deskripsi (Opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nama.isBlank()) namaError = true
                    else onSave(nama.trim(), deskripsi.trim())
                },
                shape = RoundedCornerShape(8.dp)
            ) { Text("Simpan") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}