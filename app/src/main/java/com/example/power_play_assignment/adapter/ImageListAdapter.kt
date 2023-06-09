package com.example.power_play_assignment.adapter

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.power_play_assignment.databinding.ItemImageBinding
import com.example.power_play_assignment.room.entity.Image
import com.example.power_play_assignment.ui.ImageProfileActivity
import java.text.SimpleDateFormat
import java.util.*

class ImageListAdapter(private val onDeleteClickListener: OnDeleteClickListener) : ListAdapter<Image, ImageListAdapter.ImageViewHolder>(ImageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemImageBinding.inflate(inflater, parent, false)
        return ImageViewHolder(binding)
    }

    interface OnDeleteClickListener {
        fun onDeleteClick(image: Image)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = getItem(position)
        holder.bind(image)
    }

    inner class ImageViewHolder(private val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(image: Image) {
            binding.imageNameTextView.text = image.name
            binding.additionTimeTextView.text = image.additionTime.toString()
            binding.thumbnailImageView.setImageBitmap(getBitmapFromBytes(image.thumbnail))
            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, ImageProfileActivity::class.java)
                intent.putExtra("imageId", image.id)
                binding.root.context.startActivity(intent)
            }
            binding.deleteDrawing.setOnClickListener {
                onDeleteClickListener.onDeleteClick(image)
            }
        }
    }

    private class ImageDiffCallback : DiffUtil.ItemCallback<Image>() {
        override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem == newItem
        }
    }

    private fun getBitmapFromBytes(bytes: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}
