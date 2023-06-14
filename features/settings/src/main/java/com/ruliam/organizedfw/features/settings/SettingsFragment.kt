package com.ruliam.organizedfw.features.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.ruliam.organizedfw.features.settings.datastore.SettingsDataStore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment: PreferenceFragmentCompat() {

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore = settingsDataStore
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.theme?.applyStyle(R.style.PreferenceThemeOverlayStyle, true)
        context?.theme?.applyStyle(R.style.Theme_Organized_Settings, true)
        requireActivity().window.statusBarColor = requireActivity().getColor(com.ruliam.organizedfw.core.ui.R.color.primary_color)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = super.onCreateView(inflater, container, savedInstanceState)
        val activity = activity as AppCompatActivity?
        activity?.setSupportActionBar(root.findViewById<View>(R.id.app_toolbar) as Toolbar?)
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.supportActionBar?.title = getString(R.string.settings_label)
        // Listener to back button
        root.findViewById<Toolbar>(R.id.app_toolbar)?.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        return root
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        Log.d(TAG, "click on ${preference.key}")
        return when (preference.key) {
            "share_key" -> {
                val urlShare = Intent(Intent.ACTION_VIEW, Uri.parse(urlShare))
                startActivity(urlShare)
                true
            }
            "issues_key" -> {
                val urlShare = Intent(Intent.ACTION_VIEW, Uri.parse(urlIssue))
                startActivity(urlShare)
                true
            }
            "logout_key" -> {
                // log out
//                Firebase.auth.signOut()
                true
            }
            else -> super.onPreferenceTreeClick(preference)
        }
    }

    companion object {
        private const val TAG = "SettingsPage"
        private const val urlShare = "https://github.com/"
        private const val urlIssue = "https://github.com/"

    }
}