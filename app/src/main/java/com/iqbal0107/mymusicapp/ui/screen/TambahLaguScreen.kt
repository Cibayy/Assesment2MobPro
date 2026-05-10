package com.iqbal0107.mymusicapp.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

    var judul by remember { mutableStateOf("") }
    var artis by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var catatan by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf("Happy") }
    var expandedMood by remember { mutableStateOf(false) }

    // Error state
    var judulError by remember { mutableStateOf(false) }
    var artisError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Lagu") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
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
            // Input Judul Lagu
            OutlinedTextField(
                value = judul,
                onValueChange = { judul = it; judulError = false },
                label = { Text("Judul Lagu *") },
                isError = judulError,
                supportingText = { if (judulError) Text("Judul tidak boleh kosong") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Input Artis
            OutlinedTextField(
                value = artis,
                onValueChange = { artis = it; artisError = false },
                label = { Text("Artis *") },
                isError = artisError,
                supportingText = { if (artisError) Text("Artis tidak boleh kosong") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Input Genre
            OutlinedTextField(
                value = genre,
                onValueChange = { genre = it },
                label = { Text("Genre (Pop, Rock, dll)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Dropdown Mood
            ExposedDropdownMenuBox(
                expanded = expandedMood,
                onExpandedChange = { expandedMood = !expandedMood }
            ) {
                OutlinedTextField(
                    value = "${moodEmoji(selectedMood)} $selectedMood",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Mood") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMood) },
                    modifier = Modifier
                        .menuAnchor()
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

            // Input Catatan
            OutlinedTextField(
                value = catatan,
                onValueChange = { catatan = it },
                label = { Text("Catatan pribadi") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tombol Simpan
            Button(
                onClick = {
                    // Sanity check
                    judulError = judul.isBlank()
                    artisError = artis.isBlank()

                    if (judulError || artisError) {
                        Toast.makeText(context, "Mohon isi field yang wajib diisi!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    viewModel.tambahLagu(
                        Lagu(
                            judul = judul.trim(),
                            artis = artis.trim(),
                            genre = genre.trim(),
                            mood = selectedMood,
                            catatan = catatan.trim()
                        )
                    )
                    Toast.makeText(context, "Lagu berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan Lagu")
            }
        }
    }
}