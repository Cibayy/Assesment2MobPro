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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.sp
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
                    shape = RoundedCornerShape(20.dp),
                    icon = { Icon(Icons.Default.PlaylistAdd, contentDescription = null) },
                    text = { Text("Playlist Baru", fontWeight = FontWeight.Bold) }
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
                val laguDiPlaylist = laguList.filter { it.playlistId == selectedPlaylist!!.id }

                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        PlaylistHeaderSection(selectedPlaylist!!, laguDiPlaylist.size, primaryColor)
                    }

                    if (laguDiPlaylist.isEmpty()) {
                        item {
                            EmptySongsView()
                        }
                    }

                    items(laguDiPlaylist) { lagu ->
                        LaguPremiumItem(
                            lagu = lagu,
                            onRemove = {
                                viewModel.updateLagu(lagu.copy(playlistId = 0))
                                Toast.makeText(context, "Berhasil dihapus dari playlist", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            } else {
                if (playlistList.isEmpty()) {
                    EmptyPlaylistView()
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(20.dp),
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
fun PlaylistHeaderSection(playlist: Playlist, count: Int, primaryColor: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = playlist.deskripsi.ifBlank { "Kumpulan lagu pilihan untuk harimu." },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.MusicNote,
                contentDescription = null,
                tint = primaryColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$count Lagu tersimpan",
                style = MaterialTheme.typography.labelLarge,
                color = primaryColor,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    }
}

@Composable
fun LaguPremiumItem(lagu: Lagu, onRemove: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color.Gray.copy(0.2f), Color.Gray.copy(0.1f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text("🎵", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = lagu.judul,
                style = MaterialTheme.typography.titleMedium,
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

        Box {
            IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = null)
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(
                    text = { Text("Hapus dari playlist") },
                    onClick = {
                        showMenu = false
                        onRemove()
                    },
                    leadingIcon = { Icon(Icons.Default.LinkOff, null) }
                )
            }
        }
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text("📋", fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = playlist.nama,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = playlist.deskripsi.ifBlank { "Playlist pribadi" },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = null)
            }

            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(
                    text = { Text("Ubah Nama") },
                    onClick = { showMenu = false; onEdit() },
                    leadingIcon = { Icon(Icons.Default.Edit, null) }
                )
                DropdownMenuItem(
                    text = { Text("Hapus Playlist") },
                    onClick = { showMenu = false; onDelete() },
                    leadingIcon = { Icon(Icons.Default.Delete, null) }
                )
            }
        }
    }
}

@Composable
fun EmptyPlaylistView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("✨", fontSize = 60.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Belum Ada Playlist", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Buat playlist pertamamu sekarang!", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun EmptySongsView() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.LibraryMusic, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray.copy(0.3f))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Playlist ini kosong", color = Color.Gray)
    }
}

@Composable
fun PlaylistDialog(playlist: Playlist?, onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var nama by remember { mutableStateOf(playlist?.nama ?: "") }
    var deskripsi by remember { mutableStateOf(playlist?.deskripsi ?: "") }
    var namaError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (playlist != null) "Edit Playlist" else "Buat Playlist", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nama,
                    onValueChange = { nama = it; namaError = false },
                    label = { Text("Nama Playlist") },
                    isError = namaError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    label = { Text("Deskripsi Singkat") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (nama.isBlank()) namaError = true else onSave(nama.trim(), deskripsi.trim()) },
                shape = RoundedCornerShape(12.dp)
            ) { Text("Simpan") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}