package de.tierwohlteam.android.plantraindoc_v1.models.BTTools

import android.os.Message
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.android.HandlerDispatcher
import kotlinx.coroutines.flow.*

class LightGate : BTTool() {
    private val _cooperation: MutableStateFlow<Boolean> = MutableStateFlow(value = false)
    val cooperation = _cooperation as StateFlow<Boolean>

    override fun toolReadAction(msg: Message) {
        val statusText = msg.obj.toString().replace("""\W""".toRegex(), "")
        Log.e("Arduino Message", "Status: $statusText")
        _cooperation.value = statusText == "Start"
    }
}