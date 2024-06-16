package com.example.dipl.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.diplback.diplserver.model.Passport
import com.example.dipl.R
import com.example.dipl.data.api.Api.passportApiService
import com.example.dipl.databinding.FragmentPassportBinding
import com.example.dipl.databinding.FragmentResponseListBinding
import com.example.dipl.domain.dto.PassportDto
import com.example.dipl.domain.model.User
import com.example.dipl.presentation.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PassportFragment : DialogFragment() {

    private var _binding: FragmentPassportBinding? = null
    private val binding: FragmentPassportBinding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentPassportBinding == null")

    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPassportBinding.inflate(inflater, container, false)

        user = PrefManager.getUser(requireContext())

        loadPassportData()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSavePassport.setOnClickListener {
            savePassportData()
            findNavController().navigateUp()
        }
    }

    private fun loadPassportData(){

        user?.id?.let { userId ->
            passportApiService.getPassportByUserId(userId).enqueue(object :
                Callback<Passport> {
                override fun onResponse(call: Call<Passport>, response: Response<Passport>) {
                    if (response.isSuccessful) {
                        val passport = response.body()
                        binding.etSurname.setText(passport?.surname)
                        binding.etName.setText(passport?.name)
                        binding.etLastname.setText(passport?.lastname)
                        binding.etSeries.setText(passport?.series)
                        binding.etNumber.setText(passport?.number)
                        binding.etRegistration.setText(passport?.registration)

                    } else {
                        Log.d("loadPassportData", "response not successful")
                    }
                }

                override fun onFailure(call: Call<Passport>, t: Throwable) {
                    Log.d("loadPassportData", "onFailure")
                }
            })
        }
    }

    private fun savePassportData() {

        val surname = binding.etSurname.text.toString()
        val name = binding.etName.text.toString()
        val lastname = binding.etLastname.text.toString()
        val series = binding.etSeries.text.toString()
        val number = binding.etNumber.text.toString()
        val registration = binding.etRegistration.text.toString()

        if (surname.isEmpty() || name.isEmpty() || lastname.isEmpty() || series.isEmpty() || number.isEmpty() || registration.isEmpty()) {
            Toast.makeText(requireContext(), "Пожалуйста, заполните все обязательные поля", Toast.LENGTH_SHORT).show()
            return
        }

        if (series.length != 4) {
            Toast.makeText(requireContext(), "Серия паспорта должна состоять из 4 символов", Toast.LENGTH_SHORT).show()
            return
        }

        if (number.length != 6) {
            Toast.makeText(requireContext(), "Номер паспорта должен состоять из 6 символов", Toast.LENGTH_SHORT).show()
            return
        }

        val passportDto = PassportDto(name, lastname, surname, series, number, registration)

        user?.id?.let { userId ->
            passportApiService.addPassportToUser(userId, passportDto).enqueue(object :
                Callback<Passport> {
                override fun onResponse(call: Call<Passport>, response: Response<Passport>) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Данные обновлены",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Не удалось обновить данные",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Passport>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "Не удалось обновить данные",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    override fun onStart() {
        super.onStart()

        setStyle(STYLE_NORMAL, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog)

        dialog?.window?.apply {
            val params = attributes
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            //params.gravity = Gravity.TOP or Gravity.END
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