package com.example.power_play_assignment.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.power_play_assignment.room.database.DrawingDatabase
import com.example.power_play_assignment.room.entity.Marker
import kotlinx.coroutines.flow.Flow

@Dao
interface MarkerDao {
    @Insert
    suspend fun insertMarker(marker: Marker): Long

    @Query("SELECT * FROM markers WHERE imageId = :imageId")
    fun getMarkersForImage(imageId: Long): LiveData<List<Marker>>

    @Delete
    suspend fun deleteMarker(marker: Marker)
}
