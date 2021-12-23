package de.tierwohlteam.android.plantraindoc_v1.models.BTTools

import android.os.Message
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class LightGate: BTTool() {
    private val _trialSuccess: MutableSharedFlow<String> = MutableSharedFlow()
    val trialSuccess = _trialSuccess as SharedFlow<String>

    override fun toolAction(msg: Message) {
        when (msg.what) {
            // If the updates come from the Thread to Create Connection
            CONNECTION_STATUS -> GlobalScope.launch {
                _connectionMessage.emit(
                    if (msg.arg1 == 1) "Bluetooth Connected" else "Connection Failed"
                )
            }
            // If the updates come from the Thread for Data Exchange
            MESSAGE_READ -> GlobalScope.launch {
                //val statusText = msg.obj.toString().replace("/n", "")
                val statusText = msg.obj.toString().replace("""\W""".toRegex(),"")
                Log.e("Arduino Message", "Status: $statusText")
                _trialSuccess.emit(
                    if (statusText == "Start") "Start" else "Stop"
                )
            }
        }
    }
}