package com.odisby.organizedfw.features.group.withGroup

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.odisby.organizedfw.core.ui.R
import com.odisby.organizedfw.features.group.databinding.FragmentGroupPageBinding

class GroupPageFragment : Fragment() {

    private var _binding: FragmentGroupPageBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: GroupPageViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[GroupPageViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.groupInviteCodeLayout.setOnClickListener {
//            copyCodeToClipboard()
            shareInviteCode()
        }

    }

    private fun shareInviteCode() {
        try{
            val inviteCode = binding.groupInviteCode.text
            // TODO add app link to open the app or play store
            val inviteMessage = "Venha organizar suas finanças comigo no app Organized com o código de convite: $inviteCode"

            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, inviteMessage)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        } catch (e: Exception){
            Log.w(TAG, "Error on share invite code: ${e.localizedMessage}")
        }
    }

    private fun copyCodeToClipboard() {
        try{
            val inviteCode = binding.groupInviteCode.text

            val clipboardManager = requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", inviteCode)
            clipboardManager.setPrimaryClip(clipData)

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
                Toast.makeText(context, "Text copied to clip board", Toast.LENGTH_SHORT)
                    .show()

            binding.iconGroupLayout.setImageResource(R.drawable.ic_check)

        } catch (e: Exception){
            binding.iconGroupLayout.setImageResource(R.drawable.ic_copy_content)
            Log.w(TAG, "Error on copy to clipboard: ${e.localizedMessage}")
        }

        binding.iconGroupLayout.setImageResource(R.drawable.ic_check)
    }

    companion object {
        const val TAG = "GroupPageFragment"
    }

}