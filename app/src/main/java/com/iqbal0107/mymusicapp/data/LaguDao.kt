package com.iqbal0107.mymusicapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LaguDao {

    // Ambil semua lagu, diurutkan by judul A-Z
    @Query("SELECT * FROM lagu ORDER BY judul ASC")
    fun getAllLagu(): Flow<List<Lagu>>

    // Ambil lagu berdasarkan mood
    @Query("SELECT * FROM lagu WHERE mood = :mood ORDER BY judul ASC")
    fun getLaguByMood(mood: String): Flow<List<Lagu>>

    // Tambah lagu baru
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLagu(lagu: Lagu)

    // Update lagu
    @Update
    suspend fun updateLagu(lagu: Lagu)

    // Hapus lagu
    @Delete
    suspend fun deleteLagu(lagu: Lagu)

    // Ambil lagu by id (untuk form edit)
    @Query("SELECT * FROM lagu WHERE id = :id")
    suspend fun getLaguById(id: Int): Lagu?
}