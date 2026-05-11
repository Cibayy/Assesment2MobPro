package com.iqbal0107.mymusicapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Lagu::class, Playlist::class],
    version = 3,
    exportSchema = false
)
abstract class LaguDatabase : RoomDatabase() {

    abstract fun laguDao(): LaguDao
    abstract fun playlistDao(): PlaylistDao

    companion object {
        @Volatile
        private var INSTANCE: LaguDatabase? = null

        fun getDatabase(context: Context): LaguDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LaguDatabase::class.java,
                    "lagu_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}