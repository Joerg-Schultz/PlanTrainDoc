package de.tierwohlteam.android.plantraindoc_v1.others

import kotlin.math.roundToInt

fun percentage(a: Int, b:Int) : Int{
        return if((a+b) == 0) 0 else
            ((a.toFloat() / (a + b)) * 100).roundToInt()
    }
