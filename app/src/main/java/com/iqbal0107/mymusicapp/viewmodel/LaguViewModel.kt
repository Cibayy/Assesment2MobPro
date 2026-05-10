package com.iqbal0107.mymusicapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.iqbal0107.mymusicapp.data.Lagu
import com.iqbal0107.mymusicapp.data.LaguDatabase
import com.iqbal0107.mymusicapp.repository.LaguRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LaguViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LaguRepository

    // State untuk filter mood yang dipilih
    private val _selectedMood = MutableStateFlow("Semua")
    val selectedMood: StateFlow<String> = _selectedMood.asStateFlow()

    // State untuk list lagu
    private val _laguList = MutableStateFlow<List<Lagu>>(emptyList())
    val laguList: StateFlow<List<Lagu>> = _laguList.asStateFlow()

    init {
        val dao = LaguDatabase.getDatabase(application).laguDao()
        repository = LaguRepository(dao)
        loadLagu()
    }

    private fun loadLagu() {
        viewModelScope.launch {
            if (_selectedMood.value == "Semua") {
                repository.getAllLagu().collect { _laguList.value = it }
            } else {
                repository.getLaguByMood(_selectedMood.value).collect { _laguList.value = it }
            }
        }
    }

    fun filterByMood(mood: String) {
        _selectedMood.value = mood
        loadLagu()
    }

    fun tambahLagu(lagu: Lagu) {
        viewModelScope.launch { repository.insertLagu(lagu) }
    }

    fun updateLagu(lagu: Lagu) {
        viewModelScope.launch { repository.updateLagu(lagu) }
    }

    fun hapusLagu(lagu: Lagu) {
        viewModelScope.launch { repository.deleteLagu(lagu) }
    }

    suspend fun getLaguById(id: Int): Lagu? {
        return repository.getLaguById(id)
    }
}