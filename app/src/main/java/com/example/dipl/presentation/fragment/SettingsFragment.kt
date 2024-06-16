package com.example.dipl.presentation.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.dipl.R
import com.example.dipl.databinding.FragmentSettingsBinding
import com.example.dipl.presentation.PrefManager


class SettingsFragment : DialogFragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentSettingsBinding == null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        binding.tvExit.setOnClickListener {
            PrefManager.saveUser(requireContext(), null)
            PrefManager.setLoggedInState(requireContext(), false)

            val intent = requireActivity().intent
            requireActivity().finish()
            requireActivity().startActivity(intent)
        }

        binding.userSettings.setOnClickListener {
            val action = SettingsFragmentDirections.actionSettingsDialogFragmentToUserSettingsFragment()
            findNavController().navigate(action)
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        setStyle(STYLE_NORMAL, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog)

        dialog?.window?.apply {
            val params = attributes
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            params.gravity = Gravity.TOP or Gravity.END
            params.x = resources.getDimensionPixelSize(R.dimen.dialog_right_margin) // Отступ справа
            params.y = resources.getDimensionPixelSize(R.dimen.dialog_top_margin) // Отступ сверху
            attributes = params

            setDimAmount(0.0f)
            setBackgroundDrawableResource(R.drawable.rounded_corner_dialog_background)
            setDimAmount(0.0f)
            setBackgroundDrawableResource(R.drawable.rounded_corner_dialog_background)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}