package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.plantraindoc_v1.R
import de.tierwohlteam.android.plantraindoc_v1.others.Constants
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.DEFAULT_USER_EMAIL
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.DEFAULT_USER_NAME
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.DEFAULT_USER_PASSWORD
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.ServerViewModel
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {
    private val serverViewModel: ServerViewModel by activityViewModels()

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
                    Toast.makeText(context, "True else", Toast.LENGTH_SHORT).show()
                    Log.d("LOGIN", "in login if")
                    findNavController().navigate(R.id.action_settingsFragment_to_loginServerFragment)
                } else {
                    findNavController().navigate(R.id.action_settingsFragment_to_registerServerFragment)
                }
            }
            if (newValue == false) {
                serverViewModel.logout()
                Toast.makeText(activity, "You are logged out", Toast.LENGTH_LONG).show()
            }
            true
        }

    }
}
