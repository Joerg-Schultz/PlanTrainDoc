package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.plantraindoc_v1.R

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var sharedPreferences : SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        //Preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)

        findPreference<SwitchPreferenceCompat>("UseWeb")?.setOnPreferenceChangeListener { preference, newValue ->
            if (newValue == true) {
                makeAccount()
                Toast.makeText(activity, "Hallo", Toast.LENGTH_LONG).show()
            }
            //write new value to prefs
            true
        }

    }

    private fun makeAccount(){
        if(sharedPreferences.getBoolean("hasServerAccount", false)){
            view?.let { Snackbar.make(it, "You already have an account", Snackbar.LENGTH_SHORT).show() }
        }
        else{
            view?.let { Snackbar.make(it, "Create account now", Snackbar.LENGTH_SHORT).show() }

        }
    }
}