package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.plantraindoc_v1.R
import de.tierwohlteam.android.plantraindoc_v1.models.blueToothTools.BTTool
import de.tierwohlteam.android.plantraindoc_v1.models.blueToothTools.LightGate
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.KEY_HAS_ACCOUNT
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.KEY_USE_LIGHT_GATE
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.KEY_USE_WEB_SERVER
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.ServerViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {
    private val serverViewModel: ServerViewModel by activityViewModels()

    @Inject
    lateinit var sharedPrefs: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        findPreference<SwitchPreferenceCompat>(KEY_USE_WEB_SERVER)?.setOnPreferenceChangeListener { preference, newValue ->
            if (newValue == true) {
                if (sharedPrefs.getBoolean(KEY_HAS_ACCOUNT, false)) {
                    findNavController().navigate(R.id.action_settingsFragment_to_loginServerFragment)
                } else {
                    findNavController().navigate(R.id.action_settingsFragment_to_registerServerFragment)
                }
            }
            if (newValue == false) {
                serverViewModel.logout()
                Snackbar.make(requireView(), R.string.logged_out, Snackbar.LENGTH_LONG).show()
            }
            true
        }

        findPreference<SwitchPreferenceCompat>(KEY_USE_LIGHT_GATE)?.setOnPreferenceChangeListener { preference, newValue ->
            if (newValue == true) {
                //TODO replace !!
                val lightGate = LightGate
                connectDialog(this.context!!, lightGate)
            }
            if (newValue == false) {
                lifecycleScope.launch {
                    LightGate.cancelConnection()
                    Snackbar.make(requireView(), "Lightgate disconnected", Snackbar.LENGTH_LONG).show()
                }
            }
            true
        }
    }

    private fun connectDialog(context: Context, tool: BTTool) {
        val pairedDevices = tool.getPairedDevices()
        var selectedDevice: BluetoothDevice? = pairedDevices.firstOrNull()
        lifecycleScope.launch {
            tool.connectionMessage.collect {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()
                if (it.contains("failed")) {
                    //set pref to false
                    with(sharedPrefs.edit()) {
                        putBoolean(KEY_USE_LIGHT_GATE, false)
                        apply()
                    }
                }
            }
        }
        MaterialAlertDialogBuilder(context)
            .setTitle(resources.getString(R.string.paired))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
            }
            .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                selectedDevice?.let {
                    lifecycleScope.launch {
                        tool.startCommunication(it)
                    }
                }
            }
            .setSingleChoiceItems(pairedDevices.map { it.name } .toTypedArray(), 0) { dialog, which ->
                selectedDevice = pairedDevices[which]
            }
            .show()
    }
}
