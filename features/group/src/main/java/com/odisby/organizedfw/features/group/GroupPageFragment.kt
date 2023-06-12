package com.odisby.organizedfw.features.group

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

}