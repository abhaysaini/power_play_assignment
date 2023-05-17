package com.example.power_play_assignment.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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

                // Create a new marker object with the provided title, details, and coordinates
                val newMarker = Marker(imageId = image.id,x= x,y= y,markerCreationTime= System.currentTimeMillis(),title= "title",description= "details")
                insertMarkerIntoDatabase(newMarker)

                // Create and add the marker view to the markers container

                return true
            }
        })

        binding.imageView.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            gestureDetector.onTouchEvent(event)
            true
        }
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

//
//    private fun showCommentDialog(x: Float, y: Float) {
//        val dialog = Dialog(this)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setContentView(R.layout.dialog_detail_marker)
//
//        val layoutParams = WindowManager.LayoutParams()
//        layoutParams.copyFrom(dialog.window?.attributes)
//        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
//        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
//        layoutParams.x = x.toInt()
//        layoutParams.y = y.toInt()
//
//        val editTextTitle = dialog.findViewById<EditText>(R.id.title_edit_text)
//        val editTextDesc = dialog.findViewById<EditText>(R.id.description_edit_text)
//        val buttonSave = dialog.findViewById<Button>(R.id.save_button)
//        val buttonCancel = dialog.findViewById<Button>(R.id.cancel_button)
//
//        buttonSave.setOnClickListener {
//            val title = editTextTitle.text.toString()
//            val desc = editTextDesc.text.toString()
//            if (title.isNotEmpty()) {
//                val marker = CommentMarker(x, y, title, true)
//                commentMarkers.add(marker)
////                val customView = layoutInflater.inflate(R.layout.layout_comment, null)
////                imageView.addMarker(marker, customView)
//                dialog.dismiss()
//            } else {
//                Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show()
//            }
//
//            if (desc.isNotEmpty()) {
//                val marker = CommentMarker(x, y, title, true)
//                commentMarkers.add(marker)
////                val customView = layoutInflater.inflate(R.layout.layout_comment, null)
////                imageView.addMarker(marker, customView)
//                dialog.dismiss()
//            } else {
//                Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        buttonCancel.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        dialog.show()
//        dialog.window?.attributes = layoutParams
//    }
}