package com.example.power_play_assignment.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import com.example.power_play_assignment.databinding.ActivityImageProfileBinding
import com.example.power_play_assignment.room.database.DrawingDatabase
import com.example.power_play_assignment.room.entity.Image
import com.example.power_play_assignment.room.entity.Marker

class ImageProfileActivity : AppCompatActivity() {

    private lateinit var image: Image
    lateinit var binding:ActivityImageProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageId = intent.getLongExtra("imageId", -1)
        retrieveImageFromDatabase(imageId)

        binding.icBack.setOnClickListener {
            finish()
        }
    }

    private fun retrieveImageFromDatabase(imageId: Long){
        val imageDao = DrawingDatabase.getDatabase(this).imageDao()
        val imageLiveData = imageDao.getImageById(imageId)
        imageLiveData.observe(this) { image ->
            if (image != null) {
                this.image = image
                loadImage()
                loadMarkers()
            } else {
                // Handle case where the image is not found
                Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun loadImage() {
        // Set the image in the ImageView
        binding.imageView.setImageBitmap(getBitmapFromBytes(image.thumbnail))

        // Enable pinch-zoom functionality
        val scaleGestureDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scaleFactor = detector.scaleFactor
                binding.imageView.scaleX *= scaleFactor
                binding.imageView.scaleY *= scaleFactor
                return true
            }
        })

        binding.imageView.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            true
        }
    }

    private fun loadMarkers() {
        binding.markersContainer.removeAllViews()
        val markers = retrieveMarkersFromDatabase(image!!.id)
        markers.forEach { marker ->
            val markerView = createMarkerView(marker)
            binding.markersContainer.addView(markerView)
        }
    }

    private fun retrieveMarkersFromDatabase(imageId: Long): List<Marker> {
        // Retrieve the markers associated with the image from the database
        // Return a list of Marker objects
    }

    private fun createMarkerView(marker: Marker): View {
        // Create a marker view using the marker information
        // You can inflate a layout or programmatically create the view
        // Return the created marker view
    }

    private fun getBitmapFromBytes(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}