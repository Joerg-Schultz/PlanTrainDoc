package de.tierwohlteam.android.plantraindoc_v1.models.blueToothTools

import android.os.Message
import com.benasher44.uuid.Uuid
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.KEY_UUID_VISION_MAT
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object VisionMat : BTTool() {

    private val _dogOnMat: MutableStateFlow<Boolean> = MutableStateFlow(value = false)
    val dogOnMat: StateFlow<Boolean> = _dogOnMat

    override val toolUUID: Uuid?
        get() = Uuid.fromString(KEY_UUID_VISION_MAT)

    override fun toolReadAction(msg: Message) {
        val statusText = msg.obj.toString().replace("""\W""".toRegex(), "")
        _dogOnMat.value = statusText == "Start"
    }
}