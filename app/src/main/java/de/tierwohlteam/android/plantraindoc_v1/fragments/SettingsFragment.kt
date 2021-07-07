package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.plantraindoc_v1.R
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {
    @Inject
    lateinit var sharedPrefs: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        findPreference<SwitchPreferenceCompat>("useClicker")?.setOnPreferenceChangeListener{ preference, newValue ->
            if (newValue == true){
                Toast.makeText(activity, "Clicker true", Toast.LENGTH_LONG).show()
            }
            if (newValue == false) Toast.makeText(activity, "Clicker false", Toast.LENGTH_LONG).show()
            true
        }

        findPreference<SwitchPreferenceCompat>("useWebServer")?.setOnPreferenceChangeListener{ preference, newValue ->
            if (newValue == true) {
                if (sharedPrefs.getBoolean("hasAccount", false)) {
                    Toast.makeText(activity, "You can now sync your training", Toast.LENGTH_LONG).show()
                } else {
                    findNavController().navigate(R.id.action_settingsFragment_to_registerServerFragment)
                }
            }
            if (newValue == false) Toast.makeText(activity, "Sync is not available", Toast.LENGTH_LONG).show()
            true
        }

    }

}
