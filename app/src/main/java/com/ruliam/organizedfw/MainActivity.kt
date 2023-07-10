package com.ruliam.organizedfw

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.ruliam.organizedfw.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

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

        if (appLinkData?.path.equals("/forms")) {
            val request = NavDeepLinkRequest.Builder
                .fromUri("organized-app://com.ruliam.organizedfw.form/expense".toUri())
                .build()
            navController.navigate(request)
//            navController.navigate(com.ruliam.organizedfw.core.ui.R.id.action_homeFragment_to_groupPageFragment)
        }


        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("MyFirebaseService", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            // Log and toast
            Log.d("MyFirebaseService", "Getting Token on MainActivity: $token")
        })
    }

    private fun setupNavigation() {
        navController = findNavController(R.id.nav_host_fragment_content_main)
    }
}