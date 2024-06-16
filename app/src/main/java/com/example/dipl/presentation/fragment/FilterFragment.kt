package com.example.dipl.presentation.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.dipl.R
import com.example.dipl.databinding.FragmentFilterBinding


class FilterFragment : DialogFragment() {

    private var _binding: FragmentFilterBinding? = null
    private val binding: FragmentFilterBinding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentFilterBinding == null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterBinding.inflate(layoutInflater, container, false)

        setupCitySpinner()

        binding.btnSearch.setOnClickListener {
            search()
        }

        return binding.root
    }

    private fun search() {
        val city = binding.spinnerCity.selectedItem.toString()

        val minRentStr = binding.etPriceFrom.text.toString()
        val maxRentStr = binding.etPriceUpTo.text.toString()
        val minAreaStr = binding.etSquareFrom.text.toString()
        val maxAreaStr = binding.etSquareUpTo.text.toString()

        val minRent = minRentStr.toIntOrNull() ?: 0
        val maxRent = maxRentStr.toIntOrNull() ?: Int.MAX_VALUE
        val minArea = minAreaStr.toFloatOrNull() ?: 0f
        val maxArea = maxAreaStr.toFloatOrNull() ?: Float.MAX_VALUE

        if (minRent < 0 || maxRent < 0 || minArea < 0f || maxArea < 0f) {
            showError("Значение не может быть отрицательным")
            return
        }

        if (maxRent < minRent) {
            showError("Максимальная аренда не может быть меньше минимальной")
            return
        }

        if (maxArea < minArea) {
            showError("Максимальная площадь не может быть меньше минимальной")
            return
        }

        val selectedRadioButtonId = binding.rgCountRooms.checkedRadioButtonId
        val selectedRadioButton =
            binding.rgCountRooms.findViewById<RadioButton>(selectedRadioButtonId)


        val countRooms = selectedRadioButton?.text.toString().toIntOrNull() ?: run {
            showError("Выберите количество комнат")
            return
        }

        val argument = FilterFragmentDirections.actionFilterFragmentToHomeFragment(
            city = city,
            minRent = minRent,
            maxRent = maxRent,
            minArea = minArea,
            maxArea = maxArea,
            countRooms = countRooms.toInt()
        )
        findNavController().navigate(argument)
    }

    override fun onStart() {
        super.onStart()

        setStyle(STYLE_NORMAL, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog)

        dialog?.window?.apply {
            val params = attributes
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
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

    private fun setupCitySpinner() {
        val russianCityNames = resources.getStringArray(R.array.city_names)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            russianCityNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCity.adapter = adapter
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(),message, Toast.LENGTH_SHORT).show()
    }
}