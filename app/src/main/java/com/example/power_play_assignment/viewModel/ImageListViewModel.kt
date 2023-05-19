package com.example.power_play_assignment.viewModel

import android.app.Application
import android.text.format.DateUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.power_play_assignment.room.dao.ImageDao
import com.example.power_play_assignment.room.dao.MarkerDao
import com.example.power_play_assignment.room.database.DrawingDatabase
import com.example.power_play_assignment.room.entity.Image
import com.example.power_play_assignment.room.entity.Marker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ImageListViewModel(application: Application) : AndroidViewModel(application) {

    private val imageDao: ImageDao
    private val _imageList = MutableLiveData<List<Image>>()
    val imagesLiveData: LiveData<List<Image>> get() = _imageList
    val imageList: LiveData<List<Image>> = _imageList

    init {
        val database = DrawingDatabase.getDatabase(application)
        imageDao = database.imageDao()
        observeImages()
    }

    private fun observeImages() {
        imageDao.getAllImages().observeForever { images ->
            _imageList.value = images
        }
    }

    fun insertImage(image: Image) {
        viewModelScope.launch {
            imageDao.insertImage(image)
        }
    }


    fun deleteImage(image: Image) {
        viewModelScope.launch(Dispatchers.IO) {
            imageDao.deleteImage(image)
        }
    }
}
