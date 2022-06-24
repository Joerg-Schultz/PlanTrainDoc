package de.tierwohlteam.android.plantraindoc_v1.models.blueToothTools

import android.os.Message

object Feeder : BTTool() {
    override suspend fun toolReadAction(msg: Message) {
        /* NO-OP */
    }
    fun treat() {
        super.ConnectedThread().write("t")
    }
}