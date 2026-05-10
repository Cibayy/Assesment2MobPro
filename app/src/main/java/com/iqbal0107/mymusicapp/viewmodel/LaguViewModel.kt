package com.iqbal0107.mymusicapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.iqbal0107.mymusicapp.data.Lagu
import com.iqbal0107.mymusicapp.data.LaguDatabase
import com.iqbal0107.mymusicapp.data.Playlist
import com.iqbal0107.mymusicapp.repository.LaguRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LaguViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LaguRepository

    private val _selectedMood = MutableStateFlow("Semua")
    val selectedMood: StateFlow<String> = _selectedMood.asStateFlow()

    private val _laguList = MutableStateFlow<List<Lagu>>(emptyList())
    val laguList: StateFlow<List<Lagu>> = _laguList.asStateFlow()

    private val _deletedLaguList = MutableStateFlow<List<Lagu>>(emptyList())
    val deletedLaguList: StateFlow<List<Lagu>> = _deletedLaguList.asStateFlow()

    private val _playlistList = MutableStateFlow<List<Playlist>>(emptyList())
    val playlistList: StateFlow<List<Playlist>> = _playlistList.asStateFlow()

    init {
        val db = LaguDatabase.getDatabase(application)
        repository = LaguRepository(db.laguDao(), db.playlistDao())
        loadLagu()
        loadDeletedLagu()
        loadPlaylist()
    }

    private fun loadLagu() {
        viewModelScope.launch {
            repository.getAllLagu(_selectedMood.value).collect {
                _laguList.value = it
            }
        }
    }

    private fun loadDeletedLagu() {
        viewModelScope.launch {
            repository.getDeletedLagu().collect {
                _deletedLaguList.value = it
            }
        }
    }

    private fun loadPlaylist() {
        viewModelScope.launch {
            repository.getAllPlaylist().collect {
                _playlistList.value = it
            }
        }
    }

    fun filterByMood(mood: String) {
        _selectedMood.value = mood
        loadLagu()
    }

    fun tambahLagu(lagu: Lagu) = viewModelScope.launch { repository.insertLagu(lagu) }
    fun updateLagu(lagu: Lagu) = viewModelScope.launch { repository.updateLagu(lagu) }
    fun moveToRecycleBin(id: Int) = viewModelScope.launch { repository.moveToRecycleBin(id) }
    fun restoreLagu(id: Int) = viewModelScope.launch { repository.restoreLagu(id) }
    fun deletePermanent(id: Int) = viewModelScope.launch { repository.deletePermanent(id) }

    fun tambahPlaylist(playlist: Playlist) = viewModelScope.launch { repository.insertPlaylist(playlist) }
    fun updatePlaylist(playlist: Playlist) = viewModelScope.launch { repository.updatePlaylist(playlist) }
    fun hapusPlaylist(playlist: Playlist) = viewModelScope.launch { repository.deletePlaylist(playlist) }

    suspend fun getLaguById(id: Int): Lagu? = repository.getLaguById(id)
    suspend fun getPlaylistById(id: Int): Playlist? = repository.getPlaylistById(id)
}