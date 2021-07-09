package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import de.tierwohlteam.android.plantraindoc_v1.databinding.SyncServerFragmentBinding
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.ServerViewModel

class SyncServerFragment: Fragment() {

    private val serverViewModel: ServerViewModel by activityViewModels()

    private var _binding: SyncServerFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = SyncServerFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
}