package de.tierwohlteam.android.plantraindoc_v1

import android.app.Activity
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.preference.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.plantraindoc_v1.others.Constants
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.KEY_USE_WEB_SERVER
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnSharedPreferenceChangeListener{
    @Inject
    lateinit var sharedPrefs: SharedPreferences

    private lateinit var appBarConfiguration: AppBarConfiguration
    private var topMenu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_PlanTrainDoc_v1)
        setContentView(R.layout.main_activity)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        sharedPrefs.registerOnSharedPreferenceChangeListener(this)
        with(sharedPrefs.edit()) {
            putBoolean(Constants.KEY_USE_LIGHT_GATE, false)
            apply()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.top_bar_menu, menu)
        topMenu = menu
        topMenu?.findItem(R.id.syncServerFragment)?.isVisible =
            sharedPrefs.getBoolean(KEY_USE_WEB_SERVER,false)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (sharedPreferences != null) {
            if(key.equals(KEY_USE_WEB_SERVER)) {
                //topMenu?.findItem(R.id.menuItemSync)?.isVisible = true
                topMenu?.findItem(R.id.syncServerFragment)?.isVisible =
                    sharedPreferences.getBoolean(KEY_USE_WEB_SERVER,false)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(this)
    }
}