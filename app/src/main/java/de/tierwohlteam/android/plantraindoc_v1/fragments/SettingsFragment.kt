package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.plantraindoc_v1.R
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.KEY_HAS_ACCOUNT
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.KEY_USE_CLICKER
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.KEY_USE_SPEECH
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.ServerViewModel
import javax.inject.Inject


@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {
    private val serverViewModel: ServerViewModel by activityViewModels()

    @Inject
    lateinit var sharedPrefs: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        findPreference<SwitchPreferenceCompat>(KEY_USE_CLICKER)?.setOnPreferenceChangeListener{ preference, newValue ->
            val snackBarText = if(newValue == true)  R.string.ClickerOn else R.string.ClickerOff
            Snackbar.make(requireView(),snackBarText, Snackbar.LENGTH_LONG).show()
            true
        }

        findPreference<SwitchPreferenceCompat>(KEY_USE_SPEECH)?.setOnPreferenceChangeListener{ preference, newValue ->
            if (newValue == true) {
                if (sharedPrefs.getBoolean(KEY_HAS_ACCOUNT, false)) {
                    findNavController().navigate(R.id.action_settingsFragment_to_loginServerFragment)
                } else {
                    findNavController().navigate(R.id.action_settingsFragment_to_registerServerFragment)
                }
            }
            if (newValue == false) {
                serverViewModel.logout()
                Snackbar.make(requireView(),R.string.logged_out, Snackbar.LENGTH_LONG).show()
            }
            true
        }

    }
}
