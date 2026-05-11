@file:Suppress("SpellCheckingInspection")

package com.iqbal0107.mymusicapp.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
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
import com.iqbal0107.mymusicapp.viewmodel.LaguViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecycleBinScreen(
    viewModel: LaguViewModel,
    onNavigateBack: () -> Unit
) {
    val deletedList by viewModel.deletedLaguList.collectAsState()
    val primaryColor = MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Recycle Bin",
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (deletedList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🗑️", fontSize = 60.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Tempat Sampah Kosong",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Lagu yang kamu hapus akan muncul di sini",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Column {
                            Text(
                                text = "Lagu yang dihapus sementara akan disimpan di sini sebelum dihapus permanen.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 22.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.DeleteSweep,
                                    contentDescription = null,
                                    tint = primaryColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${deletedList.size} Lagu ditemukan",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = primaryColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        }
                    }

                    items(deletedList) { lagu ->
                        RecycleBinItemModern(
                            lagu = lagu,
                            onRestore = { viewModel.restoreLagu(lagu.id) },
                            onDeletePermanent = { viewModel.deletePermanent(lagu.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecycleBinItemModern(
    lagu: Lagu,
    onRestore: () -> Unit,
    onDeletePermanent: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Hapus Permanen?", fontWeight = FontWeight.Bold) },
            text = { Text("Lagu \"${lagu.judul}\" akan dihapus selamanya dan tidak bisa dikembalikan.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeletePermanent()
                        showDialog = false
                        Toast.makeText(context, "Dihapus permanen", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

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
            Text(moodEmoji(lagu.mood), fontSize = 20.sp)
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
                text = "${lagu.artis} • ${lagu.genre}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row {
            IconButton(onClick = {
                onRestore()
                Toast.makeText(context, "Lagu berhasil dipulihkan!", Toast.LENGTH_SHORT).show()
            }) {
                Icon(
                    Icons.Default.Restore,
                    contentDescription = "Restore",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = { showDialog = true }) {
                Icon(
                    Icons.Default.DeleteForever,
                    contentDescription = "Hapus Permanen",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}