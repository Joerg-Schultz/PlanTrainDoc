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

class PTDCam(val ptdCamURL: String) {

    private val timeOut = 5 //sec
    private lateinit var recordingHandler: MjpegRecordingHandler
    private lateinit var displayWindow: MjpegSurfaceView
    private val streamURL = "$ptdCamURL:81/stream"
    private val controlURL = "$ptdCamURL/control"

    fun showInWindow(window: MjpegSurfaceView) {
        displayWindow = window
        Mjpeg.newInstance()
            .credential("Ich", "1234")
            .open(streamURL, timeOut)
            .subscribe(
                { inputStream: MjpegInputStream ->
                    displayWindow.apply {
                        setSource(inputStream)
                        //setDisplayMode(DisplayMode.BEST_FIT)
                        setDisplayMode(DisplayMode.SCALE_FIT)
                    }
                }
            ) { throwable: Throwable ->
                Log.e(javaClass.simpleName, "mjpeg error", throwable)
            }
    }

    suspend fun setResolution(resolution: Resolution = Resolution.DEFAULT) {
        val response: HttpResponse = HttpClient(Android).request(controlURL) {
            parameter("var", "framesize")
            parameter("val", resolution.esp32Size.toString())
        }
    }

    suspend fun setVerticalFlip(vFlip: Boolean = true) {
        val response: HttpResponse = HttpClient(Android).request(controlURL) {
            parameter("var", "vflip")
            parameter("val", if (vFlip) "1" else "0" )
        }
    }

    fun stop() {
        thread {
            displayWindow.stopPlayback()
//            displayWindow.clearStream()
        }
    }

    fun startRecording(context: Context) {
        recordingHandler = MjpegRecordingHandler(context)
        displayWindow.setOnFrameCapturedListener(recordingHandler)
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
            // https://www.bogotobogo.com/FFMpeg/ffmpeg_video_speed_up_slow_down.php
            // -i TheGoodTheBadAndTheUgly.mp4 -vf  "setpts=4*PTS" DownTheGoodTheBadAndTheUgly.mp4

            val session: FFmpegSession = FFmpegKit.execute("-i $fullPath -vf  \"setpts=2.5*PTS\" $mp4Path")
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


    /**
    10 UXGA(1600×1200)
    9 SXGA(1280×1024)
    8 XGA(1024×768)
    7 SVGA(800×600)
    6 VGA(640×480)
    5 CIF(400×296)
    4 QVGA(320×240)
    3 HQVGA(240×176)
    0 QQVGA(160×120)
     */
    enum class Resolution(val esp32Size: Int) {
        R320x240(4),
        R400x296(5),
        R640x480(6),
        R800x600(7),
        R1024x768(8),
        DEFAULT(6)
    }

}