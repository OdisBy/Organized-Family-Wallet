package com.ruliam.organizedfw

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ruliam.organizedfw.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.findNavController

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setupNavigation()

        // ATTENTION: This was auto-generated to handle app links.
        val appLinkIntent: Intent = intent
        val appLinkData: Uri? = appLinkIntent.data
        if (appLinkData?.path.equals("/group_page")) {
            val request = NavDeepLinkRequest.Builder
                .fromUri("organized-app://com.ruliam.organizedfw.group/page".toUri())
                .build()
            navController.navigate(request)
//            navController.navigate(com.ruliam.organizedfw.core.ui.R.id.action_homeFragment_to_groupPageFragment)
        }
    }

    private fun setupNavigation() {
        navController = findNavController(R.id.nav_host_fragment_content_main)
    }
}