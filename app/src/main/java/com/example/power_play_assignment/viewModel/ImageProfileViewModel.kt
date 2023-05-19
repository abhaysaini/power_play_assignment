package com.example.power_play_assignment.viewModel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.power_play_assignment.room.dao.ImageDao
import com.example.power_play_assignment.room.dao.MarkerDao
import com.example.power_play_assignment.room.database.DrawingDatabase
import com.example.power_play_assignment.room.entity.Image
import com.example.power_play_assignment.room.entity.Marker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImageProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val imageDao: ImageDao
    private val markerDao: MarkerDao

    init {
        val database = DrawingDatabase.getDatabase(application)
        imageDao = database.imageDao()
        markerDao = database.markerDao()
    }

    fun getImageById(imageId: Long): LiveData<Image> {
        return imageDao.getImageById(imageId)
    }

    fun insertMarker(marker: Marker) {
        viewModelScope.launch(Dispatchers.IO) {
            markerDao.insertMarker(marker)
        }
    }

    fun getMarkersForImage(imageId: Long): LiveData<List<Marker>> {
        return markerDao.getMarkersForImage(imageId)
    }

    fun deleteMarker(marker: Marker) {
        viewModelScope.launch(Dispatchers.IO) {
            markerDao.deleteMarker(marker)
        }
    }
}
