package com.example.power_play_assignment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.power_play_assignment.adapter.ImageListAdapter
import com.example.power_play_assignment.databinding.ActivityMainBinding
import com.example.power_play_assignment.room.dao.ImageDao
import com.example.power_play_assignment.room.dao.MarkerDao
import com.example.power_play_assignment.room.database.DrawingDatabase
import com.example.power_play_assignment.room.entity.Image
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() ,ImageListAdapter.OnDeleteClickListener {
    lateinit var binging:ActivityMainBinding
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var database: DrawingDatabase
    private lateinit var imageDao: ImageDao
    private lateinit var markerDao: MarkerDao
    private  var imageListAdapter= ImageListAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binging = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binging.root)
        database = Room.databaseBuilder(applicationContext, DrawingDatabase::class.java, "drawing-db")
            .build()
        imageDao = database.imageDao()
        markerDao = database.markerDao()
        observeImages()
        binging.addDrawing.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
        binging.imageList.apply {
            adapter = imageListAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

    }

    private fun observeImages() {
        imageDao.getAllImages().observe(this) { images ->
            imageListAdapter.submitList(images)
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            val imageName = getImageName(imageUri!!)

            val imageByteArray = getImageByteArray(imageUri) // Implement this function to convert URI to byte array

            // Create a new Image object
            val image = Image(
                name = imageName,
                uri = imageUri.toString(),
                thumbnail = imageByteArray!!,
                additionTime = System.currentTimeMillis()
            )

            lifecycleScope.launch {
                val imageId = imageDao.insertImage(image)
//                imageDao.insertImage(image)
                imageListAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun getImageName(imageUri: Uri): String {
        val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
        val cursor = contentResolver.query(imageUri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                return it.getString(nameIndex)
            }
        }
        return ""
    }

    private fun getImageByteArray(imageUri: Uri): ByteArray? {
        val inputStream = contentResolver.openInputStream(imageUri)
        return inputStream?.use { input ->
            val buffer = ByteArray(input.available())
            input.read(buffer)
            buffer
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onDeleteClick(image: Image) {
        val imageDao = DrawingDatabase.getDatabase(this).imageDao()
        GlobalScope.launch(Dispatchers.IO) {
            imageDao.deleteImage(image)
        }
        observeImages()
    }
}