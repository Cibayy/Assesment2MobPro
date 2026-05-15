package com.iqbal0107.mymusicapp.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.iqbal0107.mymusicapp.data.Lagu
import com.iqbal0107.mymusicapp.data.Playlist
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

fun moodColor(mood: String): Color = when (mood) {
    "Happy"    -> Color(0xFFFFD700)
    "Sedih"    -> Color(0xFF90CAF9)
    "Fokus"    -> Color(0xFFA5D6A7)
    "Santai"   -> Color(0xFFCE93D8)
    "Semangat" -> Color(0xFFFFAB91)
    else       -> Color(0xFFB0BEC5)
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
    val playlistList by viewModel.playlistList.collectAsState()
    val selectedMood by viewModel.selectedMood.collectAsState()
    var showMenu by remember { mutableStateOf(false) }
    var isGridView by remember { mutableStateOf(false) }

    val primaryColor = MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎵", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "My Playlist",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryColor,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { isGridView = !isGridView }) {
                        Icon(
                            imageVector = if (isGridView) Icons.AutoMirrored.Filled.ViewList
                            else Icons.Default.GridView,
                            contentDescription = "Toggle View"
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
            ExtendedFloatingActionButton(
                onClick = onNavigateToTambah,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Tambah Lagu") },
                containerColor = primaryColor,
                contentColor = Color.White
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Stats bar
            Surface(
                color = primaryColor.copy(alpha = 0.1f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (laguList.isEmpty()) "Belum ada lagu"
                        else "${laguList.size} lagu",
                        style = MaterialTheme.typography.labelLarge,
                        color = primaryColor,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = if (selectedMood == "Semua") "Semua mood"
                        else "${moodEmoji(selectedMood)} $selectedMood",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Filter chips
            LazyRow(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(moodList) { mood ->
                    FilterChip(
                        selected = selectedMood == mood,
                        onClick = { viewModel.filterByMood(mood) },
                        label = {
                            Text(if (mood == "Semua") "🎵 Semua" else "${moodEmoji(mood)} $mood")
                        },
                        shape = RoundedCornerShape(50),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = primaryColor,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Empty state
            if (laguList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = RoundedCornerShape(32.dp),
                            color = primaryColor.copy(alpha = 0.1f),
                            modifier = Modifier.size(120.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🎵", style = MaterialTheme.typography.displayMedium)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Belum ada lagu",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Tap tombol + untuk tambah lagu favorit",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                if (isGridView) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 12.dp, end = 12.dp,
                            top = 4.dp, bottom = 100.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(laguList) { lagu ->
                            LaguGridItem(
                                lagu = lagu,
                                playlistList = playlistList,
                                primaryColor = primaryColor,
                                onEdit = { onNavigateToEdit(lagu.id) },
                                onDelete = { viewModel.moveToRecycleBin(lagu.id) },
                                onAddToPlaylist = { playlistId ->
                                    viewModel.updateLagu(lagu.copy(playlistId = playlistId))
                                },
                                onTambahPlaylistBaru = { onNavigateToPlaylist() }
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 12.dp, end = 12.dp,
                            top = 4.dp, bottom = 100.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(laguList) { lagu ->
                            LaguItem(
                                lagu = lagu,
                                playlistList = playlistList,
                                primaryColor = primaryColor,
                                onEdit = { onNavigateToEdit(lagu.id) },
                                onDelete = { viewModel.moveToRecycleBin(lagu.id) },
                                onAddToPlaylist = { playlistId ->
                                    viewModel.updateLagu(lagu.copy(playlistId = playlistId))
                                },
                                onTambahPlaylistBaru = { onNavigateToPlaylist() }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── LIST ITEM ──────────────────────────────────────────────
@Composable
fun LaguItem(
    lagu: Lagu,
    playlistList: List<Playlist>,
    primaryColor: Color,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAddToPlaylist: (Int) -> Unit,
    onTambahPlaylistBaru: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPlaylistDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    DeleteDialog(
        show = showDeleteDialog,
        judulLagu = lagu.judul,
        onConfirm = {
            onDelete()
            showDeleteDialog = false
            Toast.makeText(context, "Dipindahkan ke Recycle Bin", Toast.LENGTH_SHORT).show()
        },
        onDismiss = { showDeleteDialog = false }
    )

    PlaylistPickerDialog(
        show = showPlaylistDialog,
        lagu = lagu,
        playlistList = playlistList,
        primaryColor = primaryColor,
        onPick = { playlistId, namaPlaylist ->
            onAddToPlaylist(playlistId)
            showPlaylistDialog = false
            Toast.makeText(context, "Ditambahkan ke $namaPlaylist", Toast.LENGTH_SHORT).show()
        },
        onTambahBaru = {
            showPlaylistDialog = false
            onTambahPlaylistBaru()
        },
        onDismiss = { showPlaylistDialog = false }
    )

    val cardBrush = Brush.horizontalGradient(
        colors = listOf(
            moodColor(lagu.mood).copy(alpha = 0.2f),
            MaterialTheme.colorScheme.surface
        )
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBrush)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = moodColor(lagu.mood).copy(alpha = 0.35f),
                    modifier = Modifier.size(54.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(moodEmoji(lagu.mood), style = MaterialTheme.typography.headlineSmall)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        lagu.judul,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        lagu.artis,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (lagu.catatan.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "📝 ${lagu.catatan}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontStyle = FontStyle.Italic
                            ),
                            color = MaterialTheme.colorScheme.secondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (lagu.genre.isNotBlank()) {
                            AssistChip(
                                onClick = {},
                                label = {
                                    Text(
                                        lagu.genre,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.height(22.dp),
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = primaryColor.copy(alpha = 0.1f),
                                    labelColor = primaryColor
                                ),
                                border = null
                            )
                        }
                        val namaPlaylist = playlistList.find { it.id == lagu.playlistId }?.nama
                        if (namaPlaylist != null) {
                            AssistChip(
                                onClick = {},
                                label = {
                                    Text(
                                        "📋 $namaPlaylist",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.height(22.dp),
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = primaryColor.copy(alpha = 0.1f),
                                    labelColor = primaryColor
                                ),
                                border = null
                            )
                        }
                    }
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
                            text = { Text("Edit") },
                            onClick = { showMenu = false; onEdit() },
                            leadingIcon = { Icon(Icons.Default.Edit, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Tambah ke Playlist") },
                            onClick = { showMenu = false; showPlaylistDialog = true },
                            leadingIcon = {
                                Icon(Icons.AutoMirrored.Filled.PlaylistAdd, null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Hapus") },
                            onClick = { showMenu = false; showDeleteDialog = true },
                            leadingIcon = { Icon(Icons.Default.Delete, null) }
                        )
                    }
                }
            }
        }
    }
}

// ── GRID ITEM ──────────────────────────────────────────────
@Composable
fun LaguGridItem(
    lagu: Lagu,
    playlistList: List<Playlist>,
    primaryColor: Color,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAddToPlaylist: (Int) -> Unit,
    onTambahPlaylistBaru: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPlaylistDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    DeleteDialog(
        show = showDeleteDialog,
        judulLagu = lagu.judul,
        onConfirm = {
            onDelete()
            showDeleteDialog = false
            Toast.makeText(context, "Dipindahkan ke Recycle Bin", Toast.LENGTH_SHORT).show()
        },
        onDismiss = { showDeleteDialog = false }
    )

    PlaylistPickerDialog(
        show = showPlaylistDialog,
        lagu = lagu,
        playlistList = playlistList,
        primaryColor = primaryColor,
        onPick = { playlistId, namaPlaylist ->
            onAddToPlaylist(playlistId)
            showPlaylistDialog = false
            Toast.makeText(context, "Ditambahkan ke $namaPlaylist", Toast.LENGTH_SHORT).show()
        },
        onTambahBaru = {
            showPlaylistDialog = false
            onTambahPlaylistBaru()
        },
        onDismiss = { showPlaylistDialog = false }
    )

    val cardBrush = Brush.verticalGradient(
        colors = listOf(
            moodColor(lagu.mood).copy(alpha = 0.3f),
            MaterialTheme.colorScheme.surface
        )
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBrush)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = moodColor(lagu.mood).copy(alpha = 0.4f),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(moodEmoji(lagu.mood), style = MaterialTheme.typography.titleLarge)
                        }
                    }
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                modifier = Modifier.size(18.dp)
                            )
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
                                text = { Text("Tambah ke Playlist") },
                                onClick = { showMenu = false; showPlaylistDialog = true },
                                leadingIcon = {
                                    Icon(Icons.AutoMirrored.Filled.PlaylistAdd, null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Hapus") },
                                onClick = { showMenu = false; showDeleteDialog = true },
                                leadingIcon = { Icon(Icons.Default.Delete, null) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    lagu.judul,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    lagu.artis,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (lagu.catatan.isNotBlank()) {
                    Text(
                        text = "📝 ${lagu.catatan}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontStyle = FontStyle.Italic
                        ),
                        color = MaterialTheme.colorScheme.secondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (lagu.genre.isNotBlank()) {
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(lagu.genre, style = MaterialTheme.typography.labelSmall)
                        },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.height(22.dp),
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = primaryColor.copy(alpha = 0.1f),
                            labelColor = primaryColor
                        ),
                        border = null
                    )
                }

                val namaPlaylist = playlistList.find { it.id == lagu.playlistId }?.nama
                if (namaPlaylist != null) {
                    Text(
                        "📋 $namaPlaylist",
                        style = MaterialTheme.typography.labelSmall,
                        color = primaryColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// ── SHARED DIALOGS ─────────────────────────────────────────
@Composable
fun DeleteDialog(
    show: Boolean,
    judulLagu: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!show) return
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hapus Lagu?") },
        text = { Text("\"$judulLagu\" akan dipindahkan ke Recycle Bin.") },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Hapus") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
fun PlaylistPickerDialog(
    show: Boolean,
    lagu: Lagu,
    playlistList: List<Playlist>,
    primaryColor: Color,
    onPick: (Int, String) -> Unit,
    onTambahBaru: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!show) return
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah ke Playlist") },
        text = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(primaryColor.copy(alpha = 0.1f))
                        .clickable { onTambahBaru() }
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = primaryColor,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Buat Playlist Baru",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = primaryColor
                    )
                }

                if (playlistList.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Playlist Tersedia",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    playlistList.forEach { playlist ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onPick(playlist.id, playlist.nama) }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("📋", style = MaterialTheme.typography.titleSmall)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    playlist.nama,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Medium
                                )
                                if (playlist.deskripsi.isNotBlank()) {
                                    Text(
                                        playlist.deskripsi,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            if (lagu.playlistId == playlist.id) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = primaryColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        if (playlist != playlistList.last()) HorizontalDivider()
                    }
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Belum ada playlist. Tap 'Buat Playlist Baru' di atas!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Tutup") }
        }
    )
}