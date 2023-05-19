package com.example.power_play_assignment.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.power_play_assignment.databinding.MarkerDetailBottomSheetBinding
import com.example.power_play_assignment.room.entity.Marker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MarkerDetailBottomSheet :BottomSheetDialogFragment() {

    lateinit var binding: MarkerDetailBottomSheetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.icBack.setOnClickListener {
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MarkerDetailBottomSheetBinding.inflate(layoutInflater)
        return binding.root
    }

    fun showMarkerDetailsDialog(marker: Marker) {
        binding.titleTextView.text = marker.title
        binding.detailsTextView.text = marker.description
        binding.creationTimeTextView.text = marker.markerCreationTime
    }

    companion object {
        fun getInstance(marker: Marker): MarkerDetailBottomSheet {
            return MarkerDetailBottomSheet()
        }
    }
}