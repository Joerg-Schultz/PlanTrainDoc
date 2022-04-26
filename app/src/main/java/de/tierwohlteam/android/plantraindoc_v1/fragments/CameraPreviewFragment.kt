package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import de.tierwohlteam.android.plantraindoc_v1.R
import de.tierwohlteam.android.plantraindoc_v1.databinding.CameraPreviewFragmentBinding
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.PTDCamResolution
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.ToolsViewModel

class CameraPreviewFragment : Fragment() {

    private var _binding: CameraPreviewFragmentBinding? = null
    private val binding get() = _binding!!

    private val toolsViewModel: ToolsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CameraPreviewFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if  (toolsViewModel.ptdCamURL.isNotEmpty()) binding.tiPtdcamUrl.hint = toolsViewModel.ptdCamURL
        binding.btnPreview.setOnClickListener {
            toolsViewModel.previewCamera(
                streamURL = binding.tiPtdcamUrl.text.toString(),
                resolution = selectedResolution())
        }

    }

    private fun selectedResolution(): PTDCamResolution {
        return when (binding.rgResolution.checkedRadioButtonId) {
            R.id.rb_640480 -> PTDCamResolution.R640x480
            R.id.rb_800600 -> PTDCamResolution.R600x800
            else -> PTDCamResolution.R640x480
        }
    }
}