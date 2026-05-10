package com.iqbal0107.mymusicapp.repository

import com.iqbal0107.mymusicapp.data.Lagu
import com.iqbal0107.mymusicapp.data.LaguDao
import com.iqbal0107.mymusicapp.data.Playlist
import com.iqbal0107.mymusicapp.data.PlaylistDao
import kotlinx.coroutines.flow.Flow

class LaguRepository(
    private val laguDao: LaguDao,
    private val playlistDao: PlaylistDao
) {
    fun getAllLagu(mood: String = "Semua"): Flow<List<Lagu>> = laguDao.getAllLagu(mood)
    fun getDeletedLagu(): Flow<List<Lagu>> = laguDao.getDeletedLagu()
    fun getLaguByPlaylist(playlistId: Int): Flow<List<Lagu>> = laguDao.getLaguByPlaylist(playlistId)
    suspend fun insertLagu(lagu: Lagu) = laguDao.insertLagu(lagu)
    suspend fun updateLagu(lagu: Lagu) = laguDao.updateLagu(lagu)
    suspend fun deleteLagu(lagu: Lagu) = laguDao.deleteLagu(lagu)
    suspend fun getLaguById(id: Int): Lagu? = laguDao.getLaguById(id)
    suspend fun moveToRecycleBin(id: Int) = laguDao.moveToRecycleBin(id)
    suspend fun restoreLagu(id: Int) = laguDao.restoreLagu(id)
    suspend fun deletePermanent(id: Int) = laguDao.deletePermanent(id)

    fun getAllPlaylist(): Flow<List<Playlist>> = playlistDao.getAllPlaylist()
    suspend fun insertPlaylist(playlist: Playlist) = playlistDao.insertPlaylist(playlist)
    suspend fun updatePlaylist(playlist: Playlist) = playlistDao.updatePlaylist(playlist)
    suspend fun deletePlaylist(playlist: Playlist) = playlistDao.deletePlaylist(playlist)
    suspend fun getPlaylistById(id: Int): Playlist? = playlistDao.getPlaylistById(id)
}