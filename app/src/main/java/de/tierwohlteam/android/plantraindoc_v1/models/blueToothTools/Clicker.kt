package de.tierwohlteam.android.plantraindoc_v1.models.blueToothTools

import android.os.Message
import android.util.Log
import kotlinx.coroutines.flow.*

object Clicker : BTTool() {

    private val _clickerStatus = MutableSharedFlow<ClickerStatus>(replay = 1)
    val clickerStatus = _clickerStatus.asSharedFlow()

    override suspend fun toolReadAction(msg: Message) {
        val msgText = msg.obj.toString()
        Log.d("3BClicker",msgText)
        val statusText = msg.obj.toString().replace("""\W""".toRegex(), "")
        _clickerStatus.emit(
            when (statusText) {
                "Click" -> ClickerStatus.CLICK
                "Reset" -> ClickerStatus.RESET
                else -> ClickerStatus.UNKNOWN
            }
        )
    }
}

enum class ClickerStatus {
    CLICK, RESET, UNKNOWN
}