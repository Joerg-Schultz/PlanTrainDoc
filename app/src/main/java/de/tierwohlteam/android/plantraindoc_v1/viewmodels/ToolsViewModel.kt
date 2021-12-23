package de.tierwohlteam.android.plantraindoc_v1.viewmodels

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.tierwohlteam.android.plantraindoc_v1.models.blueToothTools.LightGate
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.KEY_USE_LIGHT_GATE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ToolsViewModel @Inject constructor(
    private val sharedPrefs: SharedPreferences
) : ViewModel() {

    private val _cooperationLightGate: MutableStateFlow<Boolean> = MutableStateFlow(value = false)
    val cooperationLightGate: StateFlow<Boolean> = _cooperationLightGate

    init {
        val useLightGate = sharedPrefs.getBoolean(KEY_USE_LIGHT_GATE, false)
        Log.e("LIGHTGATE", "Pref: $useLightGate")
        if (useLightGate) {
            viewModelScope.launch {
                LightGate.cooperation.collect {
                    Log.e("LIGHTGATE", "Collected $it")
                    _cooperationLightGate.value = it
                }
            }
        }
    }
}