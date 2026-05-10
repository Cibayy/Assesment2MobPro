package com.iqbal0107.mymusicapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist")
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nama: String,
    val deskripsi: String
)