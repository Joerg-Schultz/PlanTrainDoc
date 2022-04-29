package de.tierwohlteam.android.plantraindoc_v1.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benasher44.uuid.Uuid
import com.github.niqdev.mjpeg.MjpegSurfaceView
import dagger.hilt.android.lifecycle.HiltViewModel
import de.tierwohlteam.android.plantraindoc_v1.models.blueToothTools.LightGate
import de.tierwohlteam.android.plantraindoc_v1.models.ipTools.PTDCam
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.KEY_USE_LIGHT_GATE
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.KEY_PTDCAM_URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ToolsViewModel @Inject constructor(
    private val sharedPrefs: SharedPreferences
) : ViewModel() {

    var ptdCam: PTDCam? = null

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
    }

    fun startPTDCamWindow(
        streamURL: String = "",
        window: MjpegSurfaceView
    ) {
        val currentStreamURL = if (streamURL.isNotEmpty()) {
            sharedPrefs.edit().putString(KEY_PTDCAM_URL, streamURL).apply()
            streamURL
        } else
            sharedPrefs.getString(KEY_PTDCAM_URL, "")!!
        if (ptdCam == null) stopPTDCam()
        ptdCam = PTDCam(currentStreamURL)
        viewModelScope.launch {
            ptdCam!!.showInWindow(window)
        }
    }

    fun setPTDCamResolution(resolution: PTDCam.Resolution) {
        viewModelScope.launch(Dispatchers.IO) {
            ptdCam?.setResolution(resolution)
        }
    }

    fun setPTDCamVFlip(flip: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            ptdCam?.setVerticalFlip(flip)
        }
    }

    fun stopPTDCam() {
        ptdCam?.stop()
    }

    fun startPTDCamRecording(context: Context?) {
        if (context != null) {
            ptdCam?.startRecording(context)
        }
    }

    suspend fun stopPTDCamRecording(context: Context?, sessionId: Uuid): String {
        ptdCam?.stopRecording()
        val newestVideoFile = ptdCam?.newestVideo(context)
        return if (newestVideoFile != null) {
            ptdCam?.convertToMp4(newestVideoFile, "$sessionId.mp4")
            newestVideoFile.name
        } else {
            "Nothing recorded"
        }
    }
}
