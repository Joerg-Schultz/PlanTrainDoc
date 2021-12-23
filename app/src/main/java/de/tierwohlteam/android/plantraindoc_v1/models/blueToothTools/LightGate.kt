package de.tierwohlteam.android.plantraindoc_v1.models.blueToothTools

import android.os.Message
import android.util.Log
import kotlinx.coroutines.flow.*

/**
 * Lightgate is an object => I can have only one...
 * Can I use injection to get a list of tools?
 */
object LightGate : BTTool() {
    private val _cooperation: MutableStateFlow<Boolean> = MutableStateFlow(value = false)
    val cooperation = _cooperation as StateFlow<Boolean>

    override fun toolReadAction(msg: Message) {
        val statusText = msg.obj.toString().replace("""\W""".toRegex(), "")
        Log.e("Arduino Message", "Status: $statusText")
        Log.e("LIGHTGATE", "Setting cooperation to ${statusText == "Start"}")
        _cooperation.value = statusText == "Start"
    }
}