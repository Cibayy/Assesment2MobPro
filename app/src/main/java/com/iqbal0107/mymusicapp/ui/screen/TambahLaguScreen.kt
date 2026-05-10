package com.iqbal0107.mymusicapp.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.iqbal0107.mymusicapp.data.Lagu
import com.iqbal0107.mymusicapp.viewmodel.LaguViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahLaguScreen(
    viewModel: LaguViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val playlistList by viewModel.playlistList.collectAsState()

    var judul by remember { mutableStateOf("") }
    var artis by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var catatan by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf("Happy") }
    var expandedMood by remember { mutableStateOf(false) }
    var selectedPlaylistId by remember { mutableStateOf<Int?>(null) }
    var expandedPlaylist by remember { mutableStateOf(false) }

    var judulError by remember { mutableStateOf(false) }
    var artisError by remember { mutableStateOf(false) }
    var playlistError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Lagu") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = judul, onValueChange = { judul = it; judulError = false },
                label = { Text("Judul Lagu *") }, isError = judulError,
                supportingText = { if (judulError) Text("Judul tidak boleh kosong") },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )
            OutlinedTextField(
                value = artis, onValueChange = { artis = it; artisError = false },
                label = { Text("Artis *") }, isError = artisError,
                supportingText = { if (artisError) Text("Artis tidak boleh kosong") },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )
            OutlinedTextField(
                value = genre, onValueChange = { genre = it },
                label = { Text("Genre") }, modifier = Modifier.fillMaxWidth(), singleLine = true
            )

            ExposedDropdownMenuBox(
                expanded = expandedMood,
                onExpandedChange = { expandedMood = !expandedMood }
            ) {
                OutlinedTextField(
                    value = "${moodEmoji(selectedMood)} $selectedMood",
                    onValueChange = {}, readOnly = true, label = { Text("Mood") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMood) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedMood,
                    onDismissRequest = { expandedMood = false }
                ) {
                    moodList.filter { it != "Semua" }.forEach { mood ->
                        DropdownMenuItem(
                            text = { Text("${moodEmoji(mood)} $mood") },
                            onClick = { selectedMood = mood; expandedMood = false }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = expandedPlaylist,
                onExpandedChange = { expandedPlaylist = !expandedPlaylist }
            ) {
                OutlinedTextField(
                    value = playlistList.find { it.id == selectedPlaylistId }?.nama ?: "Pilih Playlist *",
                    onValueChange = {}, readOnly = true, label = { Text("Playlist *") },
                    isError = playlistError,
                    supportingText = { if (playlistError) Text("Pilih playlist terlebih dahulu") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPlaylist) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedPlaylist,
                    onDismissRequest = { expandedPlaylist = false }
                ) {
                    if (playlistList.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("Belum ada playlist, buat dulu!") },
                            onClick = { expandedPlaylist = false }
                        )
                    } else {
                        playlistList.forEach { playlist ->
                            DropdownMenuItem(
                                text = { Text(playlist.nama) },
                                onClick = {
                                    selectedPlaylistId = playlist.id
                                    expandedPlaylist = false
                                    playlistError = false
                                }
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = catatan, onValueChange = { catatan = it },
                label = { Text("Catatan pribadi") },
                modifier = Modifier.fillMaxWidth(), minLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    judulError = judul.isBlank()
                    artisError = artis.isBlank()
                    playlistError = selectedPlaylistId == null

                    if (judulError || artisError || playlistError) {
                        Toast.makeText(context, "Mohon isi field yang wajib diisi!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    viewModel.tambahLagu(
                        Lagu(
                            judul = judul.trim(),
                            artis = artis.trim(),
                            genre = genre.trim(),
                            mood = selectedMood,
                            catatan = catatan.trim(),
                            playlistId = selectedPlaylistId!!
                        )
                    )
                    Toast.makeText(context, "Lagu berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Simpan Lagu") }
        }
    }
}