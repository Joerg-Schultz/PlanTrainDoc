package de.tierwohlteam.android.plantraindoc.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.plantraindoc.R
import de.tierwohlteam.android.plantraindoc.others.Constants.KEY_HAS_ACCOUNT
import de.tierwohlteam.android.plantraindoc.others.Constants.KEY_USE_WEB_SERVER
import de.tierwohlteam.android.plantraindoc.viewmodels.ServerViewModel
import javax.inject.Inject


@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {
    private val serverViewModel: ServerViewModel by activityViewModels()

    @Inject
    lateinit var sharedPrefs: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        findPreference<SwitchPreferenceCompat>(KEY_USE_WEB_SERVER)?.setOnPreferenceChangeListener{ preference, newValue ->
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
