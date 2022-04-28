package de.tierwohlteam.android.plantraindoc_v1.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegSession
import com.arthenica.ffmpegkit.ReturnCode
import com.benasher44.uuid.Uuid
import com.github.niqdev.mjpeg.MjpegRecordingHandler
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
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ToolsViewModel @Inject constructor(
    private val sharedPrefs: SharedPreferences
) : ViewModel() {

    var ptdCam: PTDCam? = null
    var ptdCamURL: String = ""
    private var recordingHandler: MjpegRecordingHandler? = null
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

    fun startPTDCamPreview(
        streamURL: String,
        resolution: PTDCam.Resolution,
        previewWindow: MjpegSurfaceView,
    ) {
        // TODO try / catch ?
        //ptdCam = PTDCam(streamURL)
        ptdCam = PTDCam("http://192.168.178.50:81/stream") // TODO
        ptdCamURL = streamURL
        //ptdCam.setResolution(resolution)
        ptdCam!!.load(previewWindow)
    }

    fun stopPTDCamPreview(previewWindow: MjpegSurfaceView) {
        ptdCam?.stopPreview(previewWindow)
    }

    fun startPTDCamRecording(context: Context?, miniWindow: MjpegSurfaceView) {
        recordingHandler = MjpegRecordingHandler(context!!)
        miniWindow.setOnFrameCapturedListener(recordingHandler!!)
        if (!recordingHandler!!.isRecording) {
            recordingHandler!!.startRecording()
        }
    }

    fun stopPTDCamRecording(context: Context?, sessionId: Uuid): String {
        return if (recordingHandler != null && recordingHandler!!.isRecording) {
            recordingHandler!!.stopRecording()
            val videoFiles = context?.getExternalFilesDir(null)!!.listFiles()?.toList() ?: emptyList()
            val newestVideoFile = videoFiles.maxByOrNull { it.lastModified() }
            if (newestVideoFile !=null && newestVideoFile.name.contains(".mjpeg")) {
                convertVideo(newestVideoFile, sessionId)
            }
            newestVideoFile.toString()
        } else {
            "Nothing recorded"
        }
    }

    private fun convertVideo(newestVideoFile: File, sessionId: Uuid) {
        viewModelScope.launch(Dispatchers.IO) {
            ptdCam?.convertToMp4(newestVideoFile, "$sessionId.mp4")
        }
    }
}
