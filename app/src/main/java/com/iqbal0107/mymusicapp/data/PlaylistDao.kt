package com.iqbal0107.mymusicapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Query("SELECT * FROM playlist ORDER BY nama ASC")
    fun getAllPlaylist(): Flow<List<Playlist>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: Playlist)

    @Update
    suspend fun updatePlaylist(playlist: Playlist)

    @Delete
    suspend fun deletePlaylist(playlist: Playlist)

    @Query("SELECT * FROM playlist WHERE id = :id")
    suspend fun getPlaylistById(id: Int): Playlist?
}