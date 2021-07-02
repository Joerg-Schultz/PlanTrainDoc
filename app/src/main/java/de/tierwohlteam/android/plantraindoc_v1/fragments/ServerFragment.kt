package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.tierwohlteam.android.plantraindoc_v1.databinding.ServerFragmentBinding

class ServerFragment : Fragment() {
    private var _binding: ServerFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = ServerFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

}