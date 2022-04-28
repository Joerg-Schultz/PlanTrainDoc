package de.tierwohlteam.android.plantraindoc_v1.models.ipTools

import android.content.Context
import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegSession
import com.arthenica.ffmpegkit.ReturnCode
import com.github.niqdev.mjpeg.*
import kotlinx.coroutines.coroutineScope
import java.io.File
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
                        //setDisplayMode(DisplayMode.BEST_FIT)
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

    fun stopPreview(previewWindow: MjpegSurfaceView) {
        thread {
            previewWindow.stopPlayback()
        }
    }

    fun startRecording(context: Context, window: MjpegSurfaceView) {
        recordingHandler = MjpegRecordingHandler(context)
        window.setOnFrameCapturedListener(recordingHandler)
        recordingHandler.startRecording()
    }

    fun stopRecording() {
        if (recordingHandler.isRecording) recordingHandler.stopRecording()
    }

    fun newestVideo(context: Context?) : File? {
        val videoFiles = context?.getExternalFilesDir(null)!!.listFiles()?.toList() ?: emptyList()
        return videoFiles.filter { it.name.contains(".mjpeg") } .maxByOrNull { it.lastModified() }
    }

    suspend fun convertToMp4(mjpegVideo: File, newName: String) : Boolean =
        coroutineScope {
            val fullPath = mjpegVideo.absolutePath
            val mp4Path = fullPath.replace(mjpegVideo.name, newName)
            val session: FFmpegSession = FFmpegKit.execute("-i $fullPath $mp4Path")
            if (ReturnCode.isSuccess(session.returnCode)) {
                mjpegVideo.delete()
                return@coroutineScope true
            } else if (ReturnCode.isCancel(session.returnCode)) {
                Log.d("FFMPEG", "Canceled conversion of ${mjpegVideo.name}!")
                return@coroutineScope false
            } else {
                // FAILURE
                Log.d(
                    "FFMPEG",
                    java.lang.String.format(
                        "Command failed with state %s and rc %s.%s",
                        session.state,
                        session.returnCode,
                        session.failStackTrace
                    )
                )
                return@coroutineScope false
            }
        }


    enum class Resolution {
        R640x480,
        R600x800
    }

}