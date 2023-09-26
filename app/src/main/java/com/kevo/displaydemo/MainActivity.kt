package com.kevo.displaydemo

import android.os.Bundle
import android.view.Display
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.kevo.displaydemo.databinding.ActivityMainBinding
import com.kevo.displaydemo.service.ServiceManager
import com.kevo.displaydemo.ui.BaseActivity
import com.kevo.displaydemo.ui.secondarydisplay.PresentationHelper
import com.kevo.displaydemo.ui.secondarydisplay.SimplePresentationFragment
import com.kevo.displaydemo.util.DeviceHelper

class MainActivity : BaseActivity(), PresentationHelper.Listener {

    override val TAG: String = "MainActivity"

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var presentationHelper: PresentationHelper
    private var preso: SimplePresentationFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab?.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?)!!
        val navController = navHostFragment.navController

        binding.navView?.let {
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_transform,
                    R.id.nav_admob,
                    R.id.nav_carousel,
                    R.id.nav_reflow,
                    R.id.nav_slideshow,
                    R.id.nav_settings
                ),
                binding.drawerLayout
            )

            // FIXME: This isn't working ...
            // Update Nav Drawer subtitle to show the Device Family Identifier
            it.findViewById<TextView>(R.id.nav_header_subtitle)?.text =
                    DeviceHelper.generateDeviceFamilyIdentifier()
            setupActionBarWithNavController(navController, appBarConfiguration)
            it.setupWithNavController(navController)
        }

        binding.appBarMain.contentMain.bottomNavView?.let {
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_transform,
                    R.id.nav_admob,
                    R.id.nav_carousel,
                    R.id.nav_reflow,
                    R.id.nav_slideshow
                )
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            it.setupWithNavController(navController)
        }

        presentationHelper = PresentationHelper(this, this)
    }

    override fun onResume() {
        super.onResume()
        ServiceManager.getInstance().startAccessoryDisplayService(application)
        presentationHelper.onResume()
    }

    override fun onPause() {
        presentationHelper.onPause()
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val result = super.onCreateOptionsMenu(menu)
        // Using findViewById because NavigationView exists in different layout files
        // between w600dp and w1240dp
        val navView: NavigationView? = findViewById(R.id.nav_view)
        if (navView == null) {
            // The navigation drawer already has the items including the items in the overflow menu
            // We only inflate the overflow menu if the navigation drawer isn't visible
            menuInflater.inflate(R.menu.overflow, menu)
        }
        return result
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_settings -> {
                val navController = findNavController(R.id.nav_host_fragment_content_main)
                navController.navigate(R.id.nav_settings)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun showPreso(display: Display) {
        preso = SimplePresentationFragment(this, themeResourceId, display)
        preso!!.show(supportFragmentManager, SimplePresentationFragment.TAG)
    }

    override fun clearPreso() {
        preso?.dismiss()
        preso = null
    }
}
