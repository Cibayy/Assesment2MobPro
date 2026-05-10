package com.iqbal0107.mymusicapp.repository

import com.iqbal0107.mymusicapp.data.Lagu
import com.iqbal0107.mymusicapp.data.LaguDao
import kotlinx.coroutines.flow.Flow

class LaguRepository(private val laguDao: LaguDao) {

    fun getAllLagu(): Flow<List<Lagu>> = laguDao.getAllLagu()

    fun getLaguByMood(mood: String): Flow<List<Lagu>> = laguDao.getLaguByMood(mood)

    suspend fun insertLagu(lagu: Lagu) = laguDao.insertLagu(lagu)

    suspend fun updateLagu(lagu: Lagu) = laguDao.updateLagu(lagu)

    suspend fun deleteLagu(lagu: Lagu) = laguDao.deleteLagu(lagu)

    suspend fun getLaguById(id: Int): Lagu? = laguDao.getLaguById(id)
}