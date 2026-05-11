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
import com.iqbal0107.mymusicapp.data.Lagu
import com.iqbal0107.mymusicapp.viewmodel.LaguViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecycleBinScreen(
    viewModel: LaguViewModel,
    onNavigateBack: () -> Unit
) {
    val deletedList by viewModel.deletedLaguList.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🗑️ Recycle Bin") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (deletedList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🗑️", style = MaterialTheme.typography.displayLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Recycle Bin kosong", style = MaterialTheme.typography.titleMedium)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(deletedList) { lagu ->
                    RecycleBinItem(
                        lagu = lagu,
                        onRestore = { viewModel.restoreLagu(lagu.id) },
                        onDeletePermanent = { viewModel.deletePermanent(lagu.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun RecycleBinItem(lagu: Lagu, onRestore: () -> Unit, onDeletePermanent: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Hapus Permanen?") },
            text = { Text("\"${lagu.judul}\" akan dihapus selamanya!") },
            confirmButton = {
                TextButton(onClick = {
                    onDeletePermanent()
                    showDialog = false
                    Toast.makeText(context, "Dihapus permanen", Toast.LENGTH_SHORT).show()
                }) { Text("Hapus", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Batal") }
            }
        )
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                moodEmoji(lagu.mood),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(end = 12.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(lagu.judul, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)
                Text("${lagu.artis} • ${lagu.genre}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = {
                onRestore()
                Toast.makeText(context, "Lagu dikembalikan!", Toast.LENGTH_SHORT).show()
            }) {
                Icon(Icons.Default.Restore, contentDescription = "Restore",
                    tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.DeleteForever, contentDescription = "Hapus Permanen",
                    tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}