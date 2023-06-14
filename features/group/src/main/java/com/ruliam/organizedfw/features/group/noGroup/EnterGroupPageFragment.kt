package com.ruliam.organizedfw.features.group.noGroup

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ruliam.organizedfw.features.group.databinding.FragmentEnterGroupPageBinding

class EnterGroupPageFragment : Fragment() {

    private var _binding: FragmentEnterGroupPageBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: EnterGroupPageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[EnterGroupPageViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnterGroupPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    companion object {
        const val TAG = "NoGroupPageFragment"
    }
}