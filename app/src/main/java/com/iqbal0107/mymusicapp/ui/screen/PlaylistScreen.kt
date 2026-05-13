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

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Column {

                        Text(
                            text = selectedPlaylist?.nama ?: "My Playlist",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )

                        if (selectedPlaylist == null) {

                            Text(
                                text = "${playlistList.size} Playlist",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                        }
                    }
                },

                navigationIcon = {

                    IconButton(
                        onClick = {

                            if (selectedPlaylist != null) {
                                selectedPlaylist = null
                            } else {
                                onNavigateBack()
                            }
                        }
                    ) {

                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1565C0)
                )
            )
        },

        floatingActionButton = {

            if (selectedPlaylist == null) {

                FloatingActionButton(
                    onClick = {
                        showTambahDialog = true
                    },
                    containerColor = Color(0xFF1565C0),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(18.dp)
                ) {

                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            Icons.Default.Add,
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = "Tambah Playlist",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        },

        containerColor = Color(0xFFF4F1F4)

    ) { paddingValues ->

        if (selectedPlaylist != null) {

            val laguDiPlaylist =
                laguList.filter { it.playlistId == selectedPlaylist!!.id }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),

                contentPadding = PaddingValues(14.dp),

                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(laguDiPlaylist) { lagu ->

                    LaguPlaylistCard(
                        lagu = lagu,

                        onRemove = {

                            viewModel.updateLagu(
                                lagu.copy(playlistId = 0)
                            )

                            Toast.makeText(
                                context,
                                "Lagu dihapus dari playlist",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),

                contentPadding = PaddingValues(14.dp),

                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(playlistList) { playlist ->

                    PlaylistCardModern(

                        playlist = playlist,

                        onClick = {
                            selectedPlaylist = playlist
                        },

                        onEdit = {
                            editPlaylist = playlist
                        },

                        onDelete = {

                            viewModel.hapusPlaylist(playlist)

                            Toast.makeText(
                                context,
                                "Playlist dihapus",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }
        }
    }

    if (showTambahDialog || editPlaylist != null) {

        PlaylistDialog(

            playlist = editPlaylist,

            onDismiss = {

                showTambahDialog = false
                editPlaylist = null
            },

            onSave = { nama, deskripsi ->

                if (editPlaylist != null) {

                    viewModel.updatePlaylist(

                        editPlaylist!!.copy(
                            nama = nama,
                            deskripsi = deskripsi
                        )
                    )

                } else {

                    viewModel.tambahPlaylist(

                        Playlist(
                            nama = nama,
                            deskripsi = deskripsi
                        )
                    )
                }

                showTambahDialog = false
                editPlaylist = null
            }
        )
    }
}

@Composable
fun PlaylistCardModern(
    playlist: Playlist,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },

        shape = RoundedCornerShape(18.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE9E5F0)
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Color(0xFF1565C0).copy(alpha = 0.12f)
                    ),

                contentAlignment = Alignment.Center
            ) {

                Icon(
                    Icons.Default.LibraryMusic,
                    contentDescription = null,
                    tint = Color(0xFF1565C0),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = playlist.nama,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = playlist.deskripsi.ifBlank {
                        "Playlist pribadi"
                    },

                    fontSize = 12.sp,
                    color = Color.Gray,

                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFFD7E7FF)
                ) {

                    Text(
                        text = "Playlist",

                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 3.dp
                        ),

                        fontSize = 10.sp,
                        color = Color(0xFF1565C0)
                    )
                }
            }

            Box {

                IconButton(
                    onClick = {
                        expanded = true
                    }
                ) {

                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = null
                    )
                }

                DropdownMenu(
                    expanded = expanded,

                    onDismissRequest = {
                        expanded = false
                    }
                ) {

                    DropdownMenuItem(

                        text = {
                            Text("Edit")
                        },

                        onClick = {
                            expanded = false
                            onEdit()
                        }
                    )

                    DropdownMenuItem(

                        text = {
                            Text("Hapus")
                        },

                        onClick = {
                            expanded = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LaguPlaylistCard(
    lagu: Lagu,
    onRemove: () -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(18.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE9E5F0)
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Color(0xFF1565C0).copy(alpha = 0.12f)
                    ),

                contentAlignment = Alignment.Center
            ) {

                Icon(
                    Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Color(0xFF1565C0),
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = lagu.judul,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = lagu.artis,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFFD7E7FF)
                ) {

                    Text(
                        text = lagu.genre,

                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 3.dp
                        ),

                        fontSize = 10.sp,
                        color = Color(0xFF1565C0)
                    )
                }
            }

            Box {

                IconButton(
                    onClick = {
                        expanded = true
                    }
                ) {

                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = null
                    )
                }

                DropdownMenu(
                    expanded = expanded,

                    onDismissRequest = {
                        expanded = false
                    }
                ) {

                    DropdownMenuItem(

                        text = {
                            Text("Hapus")
                        },

                        onClick = {
                            expanded = false
                            onRemove()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PlaylistDialog(
    playlist: Playlist?,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {

    var nama by remember {
        mutableStateOf(playlist?.nama ?: "")
    }

    var deskripsi by remember {
        mutableStateOf(playlist?.deskripsi ?: "")
    }

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {

            Text(
                if (playlist != null)
                    "Edit Playlist"
                else
                    "Tambah Playlist"
            )
        },

        text = {

            Column {

                OutlinedTextField(
                    value = nama,

                    onValueChange = {
                        nama = it
                    },

                    label = {
                        Text("Nama Playlist")
                    },

                    singleLine = true,

                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = deskripsi,

                    onValueChange = {
                        deskripsi = it
                    },

                    label = {
                        Text("Deskripsi")
                    },

                    modifier = Modifier.fillMaxWidth()
                )
            }
        },

        confirmButton = {

            Button(

                onClick = {

                    if (nama.isNotBlank()) {

                        onSave(
                            nama.trim(),
                            deskripsi.trim()
                        )
                    }
                }
            ) {

                Text("Simpan")
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text("Batal")
            }
        }
    )
}