package de.tierwohlteam.android.plantraindoc_v1.models.blueToothTools

import android.os.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object Clicker : BTTool() {
    private val _clickerStatus: MutableStateFlow<ClickerStatus> = MutableStateFlow(value = ClickerStatus.UNKNOWN)
    val clickerStatus: StateFlow<ClickerStatus> = _clickerStatus

    override fun toolReadAction(msg: Message) {
        val statusText = msg.obj.toString().replace("""\W""".toRegex(), "")
        _clickerStatus.value =  when (statusText) {
            "Click" -> ClickerStatus.CLICK
            "Reset" -> ClickerStatus.RESET
            else -> ClickerStatus.UNKNOWN
        }
    }
}

enum class ClickerStatus {
    CLICK, RESET, UNKNOWN
}