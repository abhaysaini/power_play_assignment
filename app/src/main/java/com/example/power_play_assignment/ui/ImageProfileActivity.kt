package com.example.power_play_assignment.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import com.example.power_play_assignment.R
import com.example.power_play_assignment.databinding.ActivityImageProfileBinding
import com.example.power_play_assignment.room.database.DrawingDatabase
import com.example.power_play_assignment.room.entity.Image
import com.example.power_play_assignment.room.entity.Marker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
                Log.i("abhay","Enter into scale function")
                val scaleFactor = detector.scaleFactor
                binding.imageView.scaleX *= scaleFactor
                binding.imageView.scaleY *= scaleFactor
                return true
            }
        })

        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(event: MotionEvent): Boolean {
                // Get the coordinates of the double tap
                val x = event.x
                val y = event.y

                Log.i("abhay","Enter into double tap  function")
                Log.i("abhay","$x" + " $y")

                showMarkerInputDialog(x, y)

                // Create a new marker object with the provided title, details, and coordinates
//                val newMarker = Marker(imageId = image.id,x= x,y= y,markerCreationTime= System.currentTimeMillis(),title= "title",description= "details")
//                insertMarkerIntoDatabase(newMarker)

                // Create and add the marker view to the markers container

                return true
            }
        })

        binding.imageView.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            gestureDetector.onTouchEvent(event)
            true
        }

        val markersLiveData = retrieveMarkersFromDatabase(image.id)
        markersLiveData.observe(this) { markers ->

            binding.markersContainer.removeAllViews()

            // Add all the markers to the view
            for (marker in markers) {
                val markerView = createMarkerView(marker)
                binding.markersContainer.addView(markerView)
            }
            // ...

            // Add click listeners to the marker views
            for (i in 0 until binding.markersContainer.childCount) {
                val markerView = binding.markersContainer.getChildAt(i)
                markerView.setOnClickListener {
                    val marker = markerView.tag as Marker
                    showMarkerDetailsDialog(marker)
                }
            }
        }
    }

    private fun showMarkerDetailsDialog(marker: Marker) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_marker_details, null)
        val titleTextView = dialogView.findViewById<TextView>(R.id.titleTextView)
        val detailsTextView = dialogView.findViewById<TextView>(R.id.detailsTextView)
        titleTextView.text = marker.title
        detailsTextView.text = marker.description

        AlertDialog.Builder(this)
            .setTitle("Marker Details")
            .setView(dialogView)
            .setPositiveButton("OK", null)
            .show()
    }

    fun insertMarkerIntoDatabase(newMarker: Marker) {
        val markerDao = DrawingDatabase.getDatabase(this).markerDao()
        CoroutineScope(Dispatchers.IO).launch {
            markerDao.insertMarker(newMarker)
        }
    }

    private fun retrieveMarkersFromDatabase(imageId: Long):LiveData<List<Marker>> {
        val markerDao = DrawingDatabase.getDatabase(this).markerDao()
        return markerDao.getMarkersForImage(imageId)
    }

    private fun getBitmapFromBytes(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    private fun showMarkerInputDialog(x: Float, y: Float) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_marker_input, null)
        val titleEditText = dialogView.findViewById<EditText>(R.id.editTextTitle)
        val detailsEditText = dialogView.findViewById<EditText>(R.id.editTextDetails)

        AlertDialog.Builder(this)
            .setTitle("Add Marker")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val title = titleEditText.text.toString()
                val details = detailsEditText.text.toString()

                // Create a new marker object with the provided title, details, and coordinates
                val newMarker = Marker(
                    imageId = image.id,
                    x = x,
                    y = y,
                    markerCreationTime = System.currentTimeMillis(),
                    title = title,
                    description = details
                )
                insertMarkerIntoDatabase(newMarker)

                // Create and add the marker view to the markers container
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun createMarkerView(marker: Marker): View {
        val markerView = layoutInflater.inflate(R.layout.marker_item, null)
        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.leftMargin = marker.x.toInt()
        layoutParams.topMargin = marker.y.toInt()
        markerView.layoutParams = layoutParams

        // Set the pin icon
        val pinImageView = markerView.findViewById<ImageView>(R.id.pinImageView)
        pinImageView.setImageResource(R.drawable.pin_icon)

        // Store the marker data in the tag of the markerView
        markerView.tag = marker

        return markerView
    }

}