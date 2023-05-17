package com.example.power_play_assignment.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.power_play_assignment.room.database.DrawingDatabase
import com.example.power_play_assignment.room.entity.Image
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    @Insert
    suspend fun insertImage(image: Image): Long

    @Query("SELECT * FROM images")
    fun getAllImages(): LiveData<List<Image>>

    @Query("SELECT * FROM images WHERE id = :imageId")
    fun getImageById(imageId: Long): LiveData<Image>

    @Delete
    suspend fun deleteImage(image: Image)
}
