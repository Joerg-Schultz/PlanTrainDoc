package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.plantraindoc_v1.R
import de.tierwohlteam.android.plantraindoc_v1.databinding.CameraPreviewFragmentBinding
import de.tierwohlteam.android.plantraindoc_v1.models.ipTools.PTDCam
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.KEY_PTDCAM_URL
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.ToolsViewModel
import javax.inject.Inject

@AndroidEntryPoint
class CameraPreviewFragment : Fragment() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private var _binding: CameraPreviewFragmentBinding? = null
    private val binding get() = _binding!!

    private val toolsViewModel: ToolsViewModel by activityViewModels()

    private var previewRunning: Boolean = false

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

        binding.tiPtdcamUrl.setText(sharedPreferences.getString(KEY_PTDCAM_URL, "")!!)
        binding.switchVflip.isChecked = true

        binding.btnPreview.setOnClickListener {
            if (previewRunning) {
                toolsViewModel.stopPTDCam()
                binding.btnPreview.text = "Preview"
            } else {
                toolsViewModel.apply {
                    startPTDCamWindow(
                        streamURL = binding.tiPtdcamUrl.text.toString(),
                        window = binding.mjpegPtdcam
                    )
                    setPTDCamResolution(PTDCam.Resolution.DEFAULT)
                    setPTDCamVFlip(true)
                }
                binding.btnPreview.text = "Stop Preview"
            }
            previewRunning = ! previewRunning
        }

        binding.rgResolution.setOnCheckedChangeListener { _, checkedId ->
            val resolution = when (checkedId) {
                R.id.rb_640480 -> PTDCam.Resolution.R640x480
                R.id.rb_800600 -> PTDCam.Resolution.R800x600
                else -> PTDCam.Resolution.DEFAULT
            }
            toolsViewModel.setPTDCamResolution(resolution)
        }

        binding.switchVflip.setOnCheckedChangeListener { _, isChecked ->
            toolsViewModel.setPTDCamVFlip(isChecked)
        }
    }

    override fun onPause() {
        super.onPause()
        toolsViewModel.stopPTDCam()
    }
}