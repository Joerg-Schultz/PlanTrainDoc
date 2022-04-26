package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.tierwohlteam.android.plantraindoc_v1.databinding.CameraPreviewFragmentBinding

class CameraPreviewFragment : Fragment() {

    private var _binding: CameraPreviewFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CameraPreviewFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
}