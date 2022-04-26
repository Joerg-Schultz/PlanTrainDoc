package de.tierwohlteam.android.plantraindoc_v1.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.niqdev.mjpeg.MjpegSurfaceView
import dagger.hilt.android.lifecycle.HiltViewModel
import de.tierwohlteam.android.plantraindoc_v1.models.blueToothTools.LightGate
import de.tierwohlteam.android.plantraindoc_v1.models.ipTools.PTDCam
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.KEY_USE_LIGHT_GATE
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.KEY_PTDCAM_URL
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ToolsViewModel @Inject constructor(
    private val sharedPrefs: SharedPreferences
) : ViewModel() {

    private lateinit var ptdCam: PTDCam
    var ptdCamURL: String = ""
    private val _cooperationLightGate: MutableStateFlow<Boolean> = MutableStateFlow(value = false)
    val cooperationLightGate: StateFlow<Boolean> = _cooperationLightGate

    init {
        val useLightGate = sharedPrefs.getBoolean(KEY_USE_LIGHT_GATE, false)
        if (useLightGate) {
            viewModelScope.launch {
                LightGate.cooperation.collect {
                    _cooperationLightGate.value = it
                }
            }
        }
        ptdCamURL = sharedPrefs.getString(KEY_PTDCAM_URL, "").toString()
    }

    fun startPreview(
        streamURL: String,
        resolution: PTDCam.Resolution,
        previewWindow: MjpegSurfaceView,
    ) {
        // TODO try / catch ?
        //ptdCam = PTDCam(streamURL)
        ptdCam = PTDCam("http://192.168.178.50:81/stream") // TODO
        ptdCamURL = streamURL
        //ptdCam.setResolution(resolution)
        ptdCam.load(previewWindow)
    }

    fun stopPreview(previewWindow: MjpegSurfaceView) {
        ptdCam.stopPreview(previewWindow)
    }
}
