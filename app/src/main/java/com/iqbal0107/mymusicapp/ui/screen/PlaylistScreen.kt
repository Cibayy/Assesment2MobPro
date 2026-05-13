@file:Suppress("SpellCheckingInspection")

package com.iqbal0107.mymusicapp.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onBackgroundColor = MaterialTheme.colorScheme.onBackground

    if (selectedPlaylist != null) {
        val laguDiPlaylist = laguList.filter { it.playlistId == selectedPlaylist!!.id }
        SpotifyStylePlaylistDetail(
            playlist = selectedPlaylist!!,
            laguList = laguDiPlaylist,
            primaryColor = primaryColor,
            backgroundColor = backgroundColor,
            onNavigateBack = { selectedPlaylist = null },
            onRemoveLagu = { lagu ->
                viewModel.updateLagu(lagu.copy(playlistId = 0))
                Toast.makeText(context, "Dihapus dari playlist", Toast.LENGTH_SHORT).show()
            }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "My Playlist",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            Text(
                                text = "${playlistList.size} Playlist",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryColor)
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { showTambahDialog = true },
                    containerColor = primaryColor,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(18.dp),
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Tambah Playlist", fontWeight = FontWeight.SemiBold) }
                )
            },
            containerColor = backgroundColor
        ) { paddingValues ->
            if (playlistList.isEmpty()) {
                EmptyPlaylistState(
                    primaryColor = primaryColor,
                    backgroundColor = backgroundColor,
                    modifier = Modifier.padding(paddingValues)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(playlistList) { playlist ->
                        val jumlahLagu = laguList.count { it.playlistId == playlist.id }
                        PlaylistCardModern(
                            playlist = playlist,
                            jumlahLagu = jumlahLagu,
                            primaryColor = primaryColor,
                            surfaceColor = surfaceColor,
                            onBackgroundColor = onBackgroundColor,
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
                showTambahDialog = false
                editPlaylist = null
            }
        )
    }
}

// ── Spotify-style detail playlist ─────────────────────────
@Composable
fun SpotifyStylePlaylistDetail(
    playlist: Playlist,
    laguList: List<Lagu>,
    primaryColor: Color,
    backgroundColor: Color,
    onNavigateBack: () -> Unit,
    onRemoveLagu: (Lagu) -> Unit
) {
    // Ambil mood unik dari lagu-lagu di playlist
    val uniqueMoods = laguList.map { it.mood }.distinct().take(4)

    val heroGradient = listOf(
        primaryColor.copy(alpha = 0.95f),
        primaryColor.copy(alpha = 0.6f),
        primaryColor.copy(alpha = 0.2f),
        backgroundColor
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // ── Hero Section ──
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp)
                        .background(Brush.verticalGradient(heroGradient))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 72.dp, start = 20.dp, end = 20.dp, bottom = 20.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        // Cover art — grid emoji mood unik
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            primaryColor.copy(alpha = 0.8f),
                                            primaryColor.copy(alpha = 0.3f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                uniqueMoods.isEmpty() -> {
                                    Icon(
                                        Icons.Default.LibraryMusic,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.5f),
                                        modifier = Modifier.size(64.dp)
                                    )
                                }
                                uniqueMoods.size == 1 -> {
                                    Text(moodEmoji(uniqueMoods[0]), fontSize = 64.sp)
                                }
                                uniqueMoods.size == 2 -> {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(moodEmoji(uniqueMoods[0]), fontSize = 44.sp)
                                            Text(moodEmoji(uniqueMoods[1]), fontSize = 44.sp)
                                        }
                                    }
                                }
                                uniqueMoods.size == 3 -> {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.fillMaxSize().padding(12.dp),
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(moodEmoji(uniqueMoods[0]), fontSize = 38.sp)
                                            Text(moodEmoji(uniqueMoods[1]), fontSize = 38.sp)
                                        }
                                        Text(moodEmoji(uniqueMoods[2]), fontSize = 38.sp)
                                    }
                                }
                                else -> {
                                    // 4 emoji grid 2x2
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.fillMaxSize().padding(12.dp)
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text(moodEmoji(uniqueMoods[0]), fontSize = 36.sp)
                                            Text(moodEmoji(uniqueMoods[1]), fontSize = 36.sp)
                                        }
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text(moodEmoji(uniqueMoods[2]), fontSize = 36.sp)
                                            Text(moodEmoji(uniqueMoods[3]), fontSize = 36.sp)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Judul & deskripsi rata kiri
                        Text(
                            text = playlist.nama,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (playlist.deskripsi.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = playlist.deskripsi,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Badge jumlah lagu
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = primaryColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${laguList.size} lagu",
                                fontSize = 13.sp,
                                color = primaryColor,
                                fontWeight = FontWeight.SemiBold
                            )

                            // Badge mood unik
                            if (uniqueMoods.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = uniqueMoods.joinToString(" ") { moodEmoji(it) },
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            // ── Divider + label ──
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (laguList.isEmpty()) "Belum ada lagu"
                        else "Lagu dalam playlist",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                )
            }

            // ── Empty state ──
            if (laguList.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.MusicOff,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Playlist ini masih kosong",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Tambahkan lagu via menu ⋮ di halaman utama",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // ── List lagu ──
            items(laguList) { lagu ->
                SpotifyLaguRow(
                    lagu = lagu,
                    primaryColor = primaryColor,
                    backgroundColor = backgroundColor,
                    onRemove = { onRemoveLagu(lagu) }
                )
            }
        }

        // ── Tombol back floating ──
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .padding(top = 40.dp, start = 8.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.35f))
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali",
                tint = Color.White
            )
        }
    }
}

// ── Row lagu gaya Spotify ──────────────────────────────────
@Composable
fun SpotifyLaguRow(
    lagu: Lagu,
    primaryColor: Color,
    backgroundColor: Color,
    onRemove: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Hapus dari Playlist?") },
            text = { Text("\"${lagu.judul}\" akan dikeluarkan dari playlist.") },
            confirmButton = {
                TextButton(onClick = { onRemove(); showDialog = false }) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Batal") }
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mood badge
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(moodColor(lagu.mood).copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Text(moodEmoji(lagu.mood), fontSize = 22.sp)
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = lagu.judul,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = buildString {
                    append(lagu.artis)
                    if (lagu.genre.isNotBlank()) append(" • ${lagu.genre}")
                },
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Box {
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Hapus dari Playlist") },
                    onClick = { showMenu = false; showDialog = true },
                    leadingIcon = { Icon(Icons.Default.LinkOff, null) }
                )
            }
        }
    }

    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
    )
}

// ── Card playlist di halaman utama ─────────────────────────
@Composable
fun PlaylistCardModern(
    playlist: Playlist,
    jumlahLagu: Int,
    primaryColor: Color,
    surfaceColor: Color,
    onBackgroundColor: Color,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Playlist?") },
            text = { Text("Playlist \"${playlist.nama}\" akan dihapus.") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteDialog = false }) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(primaryColor.copy(alpha = 0.15f), surfaceColor)
                    )
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(primaryColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.LibraryMusic,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = playlist.nama,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = onBackgroundColor
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = playlist.deskripsi.ifBlank { "Playlist pribadi" },
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = primaryColor.copy(alpha = 0.12f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = primaryColor,
                                    modifier = Modifier.size(10.dp)
                                )
                                Text(
                                    text = "$jumlahLagu lagu",
                                    fontSize = 10.sp,
                                    color = primaryColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = primaryColor.copy(alpha = 0.07f)
                        ) {
                            Text(
                                text = "Playlist",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                fontSize = 10.sp,
                                color = primaryColor.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = null)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = { expanded = false; onEdit() },
                            leadingIcon = { Icon(Icons.Default.Edit, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Hapus") },
                            onClick = { expanded = false; showDeleteDialog = true },
                            leadingIcon = { Icon(Icons.Default.Delete, null) }
                        )
                    }
                }
            }
        }
    }
}

// ── Empty states ───────────────────────────────────────────
@Composable
fun EmptyPlaylistState(
    primaryColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize().background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = primaryColor.copy(alpha = 0.1f),
                modifier = Modifier.size(120.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🎵", fontSize = 52.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Belum Ada Playlist",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Buat playlist pertamamu sekarang!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── Dialog tambah/edit playlist ───────────────────────────
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
        title = {
            Text(
                if (playlist != null) "Edit Playlist" else "Tambah Playlist",
                fontWeight = FontWeight.Bold
            )
        },
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
                onClick = {
                    if (nama.isBlank()) namaError = true
                    else onSave(nama.trim(), deskripsi.trim())
                },
                shape = RoundedCornerShape(12.dp)
            ) { Text("Simpan") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}