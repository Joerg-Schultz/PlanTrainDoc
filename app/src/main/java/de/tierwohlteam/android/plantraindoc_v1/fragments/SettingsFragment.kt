package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import de.tierwohlteam.android.plantraindoc_v1.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}