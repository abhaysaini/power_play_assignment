package com.example.power_play_assignment.ui.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.format.DateUtils
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.power_play_assignment.adapter.ImageListAdapter
import com.example.power_play_assignment.databinding.ActivityMainBinding
import com.example.power_play_assignment.room.dao.ImageDao
import com.example.power_play_assignment.room.dao.MarkerDao
import com.example.power_play_assignment.room.database.DrawingDatabase
import com.example.power_play_assignment.room.entity.Image
import com.example.power_play_assignment.viewModel.ImageListViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() ,ImageListAdapter.OnDeleteClickListener {
    lateinit var binging:ActivityMainBinding
    private val PICK_IMAGE_REQUEST = 1
    private  var imageListAdapter= ImageListAdapter(this)
    private lateinit var viewModel: ImageListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binging = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binging.root)

        viewModel = ViewModelProvider(this)[ImageListViewModel::class.java]
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
        viewModel.imagesLiveData.observe(this) { images ->
            imageListAdapter.submitList(images)
            images.forEach { image ->
                val formattedTime = formatSocialTime(image.additionTime.toLong())
                image.additionTime = formattedTime
            }
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
                additionTime = System.currentTimeMillis().toString()
            )

            viewModel.insertImage(image)
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

    override fun onDeleteClick(image: Image) {
        viewModel.deleteImage(image)
    }


    private fun formatSocialTime(timestamp: Long): String {
        val currentTime = System.currentTimeMillis()
        val timeDifference = currentTime - timestamp

        return when {
            timeDifference < DateUtils.MINUTE_IN_MILLIS -> "few minutes ago"
            timeDifference < DateUtils.HOUR_IN_MILLIS -> {
                val minutes = (timeDifference / DateUtils.MINUTE_IN_MILLIS).toInt()
                "$minutes minutes ago"
            }
            timeDifference < DateUtils.DAY_IN_MILLIS -> {
                val hours = (timeDifference / DateUtils.HOUR_IN_MILLIS).toInt()
                "$hours hours ago"
            }
            else -> {
                formatAdditionTime(timestamp)
            }
        }
    }

    private fun formatAdditionTime(additionTime: Long): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val date = Date(additionTime)
        return dateFormat.format(date)
    }

}