package com.example.power_play_assignment.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.power_play_assignment.room.dao.ImageDao
import com.example.power_play_assignment.room.dao.MarkerDao
import com.example.power_play_assignment.room.entity.Image
import com.example.power_play_assignment.room.entity.Marker

@Database(entities = [Image::class, Marker::class], version = 1)
abstract class DrawingDatabase : RoomDatabase() {
    abstract fun imageDao(): ImageDao
    abstract fun markerDao(): MarkerDao

    companion object {
        @Volatile
        private var INSTANCE: DrawingDatabase? = null

        fun getDatabase(context: Context): DrawingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DrawingDatabase::class.java,
                    "drawing-db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
