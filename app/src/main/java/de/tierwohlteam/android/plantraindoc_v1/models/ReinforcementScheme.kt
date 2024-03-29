package de.tierwohlteam.android.plantraindoc_v1.models

import kotlin.random.Random

//make the exposed variable immutable
class ReinforcementScheme() {
    class ReinforcementLevel(val steps: List<Float>) {
        companion object {
            //TODO start at variabel positions
            var pos = 0
        }

        fun getStep(): Float {
            if(pos >= steps.size) pos = 0
            return steps[pos++]
        }
    }

    private val levels: MutableMap<Float, ReinforcementLevel> = mutableMapOf()

    fun addLevel(level: Float, variations: List<Float>) {
        levels[level] = ReinforcementLevel(variations)
    }
    fun getStep(level: Float): Float? = levels[level]?.getStep()
    fun getLevels() : List<Float> = levels.keys.toList().sorted()
    fun getValues(level: Float): List<Float> = levels[level]?.steps ?: emptyList()
}

