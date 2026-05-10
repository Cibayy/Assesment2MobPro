package com.iqbal0107.mymusicapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LaguDao {

    @Query("SELECT * FROM lagu WHERE isDeleted = 0 AND (:mood = 'Semua' OR mood = :mood) ORDER BY judul ASC")
    fun getAllLagu(mood: String = "Semua"): Flow<List<Lagu>>

    @Query("SELECT * FROM lagu WHERE isDeleted = 1 ORDER BY judul ASC")
    fun getDeletedLagu(): Flow<List<Lagu>>

    @Query("SELECT * FROM lagu WHERE playlistId = :playlistId AND isDeleted = 0 ORDER BY judul ASC")
    fun getLaguByPlaylist(playlistId: Int): Flow<List<Lagu>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLagu(lagu: Lagu)

    @Update
    suspend fun updateLagu(lagu: Lagu)

    @Delete
    suspend fun deleteLagu(lagu: Lagu)

    @Query("SELECT * FROM lagu WHERE id = :id")
    suspend fun getLaguById(id: Int): Lagu?

    @Query("UPDATE lagu SET isDeleted = 1 WHERE id = :id")
    suspend fun moveToRecycleBin(id: Int)

    @Query("UPDATE lagu SET isDeleted = 0 WHERE id = :id")
    suspend fun restoreLagu(id: Int)

    @Query("DELETE FROM lagu WHERE id = :id")
    suspend fun deletePermanent(id: Int)
}