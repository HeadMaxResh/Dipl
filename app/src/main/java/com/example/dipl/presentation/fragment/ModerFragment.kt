package com.example.dipl.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.dipl.databinding.FragmentModerBinding
import com.example.dipl.presentation.PrefManager

class ModerFragment : Fragment() {

    private var _binding: FragmentModerBinding? = null
    private val binding: FragmentModerBinding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentModerBinding == null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModerBinding.inflate(inflater, container, false)

        binding.tvExit.setOnClickListener {
            PrefManager.saveUser(requireContext(), null)
            PrefManager.setLoggedInState(requireContext(), false)

            val intent = requireActivity().intent
            requireActivity().finish()
            requireActivity().startActivity(intent)
        }

        binding.cvAparments.setOnClickListener {
            val action = ModerFragmentDirections.actionModerFragmentToModerApartmentListFragment()
            findNavController().navigate(action)
        }

        binding.cvResponses.setOnClickListener {
            val action = ModerFragmentDirections.actionModerFragmentToModerChatFragment()
            findNavController().navigate(action)
        }

        binding.cvContracts.setOnClickListener {
            val action = ModerFragmentDirections.actionModerFragmentToModerContractListFragment()
            findNavController().navigate(action)
        }

        return binding.root
    }

}