package com.iqbal0107.mymusicapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lagu")
data class Lagu(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val playlistId: Int = 0,
    val judul: String,
    val artis: String,
    val genre: String,
    val mood: String,
    val catatan: String,
    val isDeleted: Boolean = false
)