package de.tierwohlteam.android.plantraindoc_v1.models.ipTools

import android.content.Context
import android.util.Log
import com.github.niqdev.mjpeg.*
import kotlin.concurrent.thread

class PTDCam(private val streamURL: String) {

    private val timeOut = 5 //sec
    private lateinit var recordingHandler: MjpegRecordingHandler

    fun load(previewWindow: MjpegSurfaceView) {
        Mjpeg.newInstance()
            .credential("Ich", "1234")
            .open(streamURL, timeOut)
            .subscribe(
                { inputStream: MjpegInputStream ->
                    previewWindow.apply {
                        setSource(inputStream)
                        setDisplayMode(DisplayMode.SCALE_FIT)
                    }
                }
            ) { throwable: Throwable ->
                Log.e(javaClass.simpleName, "mjpeg error", throwable)
            }
    }

    fun setResolution(resolution: Resolution) {
        TODO("Not yet implemented")
    }

    fun startPreview(previewWindow: MjpegSurfaceView, context: Context?) {
        recordingHandler = context?.let { MjpegRecordingHandler(it) }!!
        previewWindow.setOnFrameCapturedListener(recordingHandler)
    }

    fun stopPreview(previewWindow: MjpegSurfaceView) {
        thread {
            previewWindow.stopPlayback()
        }
    }

    enum class Resolution {
        R640x480,
        R600x800
    }

}