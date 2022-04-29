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
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class PTDCam(ptdCamURL: String) {

    private val timeOut = 5 //sec
    private lateinit var recordingHandler: MjpegRecordingHandler
    private val streamURL = "$ptdCamURL:81/stream"
    private var resolution = Resolution.R640x480

    private val controlURL = "$ptdCamURL/control"
    private var vflip = true

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

    suspend fun setResolution(newResolution: Resolution) {
        val response: HttpResponse = HttpClient(Android).request(controlURL) {
            parameter("var", "framesize")
            parameter("val", newResolution.esp32Size.toString())
        }
        if (response.status == HttpStatusCode.OK) resolution = newResolution
    }

    suspend fun verticalFlip(vFlip: Boolean) {
        vflip = vFlip
        val response: HttpResponse = HttpClient(Android).request(controlURL) {
            parameter("var", "vflip")
            parameter("val", if (vFlip) "1" else "0" )
        }
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


    enum class Resolution(val esp32Size: Int) {
        R640x480(6),
        R600x800(7),
        DEFAULT(6)
    }

}