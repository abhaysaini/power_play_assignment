package com.example.power_play_assignment.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "markers",
    foreignKeys = [
        ForeignKey(
            entity = Image::class,
            parentColumns = ["id"],
            childColumns = ["imageId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Marker(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val imageId: Long,
    val x: Float,
    val y: Float,
    val markerCreationTime:Long,
    val title:String,
    val description :String
)
