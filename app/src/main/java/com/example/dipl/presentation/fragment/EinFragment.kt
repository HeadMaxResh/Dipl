package com.example.dipl.presentation.fragment

import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.diplback.diplserver.model.Ein
import com.diplback.diplserver.model.Passport
import com.example.dipl.R
import com.example.dipl.data.api.Api.einApiService
import com.example.dipl.databinding.FragmentEinBinding
import com.example.dipl.databinding.FragmentResponseListBinding
import com.example.dipl.domain.dto.EinDto
import com.example.dipl.domain.dto.PassportDto
import com.example.dipl.domain.model.User
import com.example.dipl.presentation.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EinFragment : DialogFragment() {

    private var _binding: FragmentEinBinding? = null
    private val binding: FragmentEinBinding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentEinBinding == null")

    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEinBinding.inflate(inflater, container, false)

        user = PrefManager.getUser(requireContext())

        loadEinData()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSave.setOnClickListener {
            saveEinData()
            findNavController().navigateUp()
        }

    }

    private fun saveEinData() {

        val einNumber = binding.etEin.text.toString()

        if (einNumber.isEmpty() || einNumber.length != 12) {
            Toast.makeText(requireContext(), "Введите корректный Инн", Toast.LENGTH_SHORT).show()
            return
        }

        if (!einNumber.matches(Regex("\\d{12}"))) {
            Toast.makeText(requireContext(), "Инн должен состоять только из цифр", Toast.LENGTH_SHORT).show()
            return
        }

        val einDto = EinDto(einNumber.toLong())

        user?.id?.let { userId ->
            einApiService.addEinToUser(userId, einDto).enqueue(object :
                Callback<Ein> {
                override fun onResponse(call: Call<Ein>, response: Response<Ein>) {
                    if (response.isSuccessful) {


                    } else {
                        T
                    }
                }

                override fun onFailure(call: Call<Ein>, t: Throwable) {

                }
            })
        }

    }

    private fun loadEinData() {
        user?.id?.let { userId ->
            einApiService.getEinByUserId(userId).enqueue(object :
                Callback<Ein> {
                override fun onResponse(call: Call<Ein>, response: Response<Ein>) {
                    if (response.isSuccessful) {
                        val ein = response.body()
                        binding.etEin.setText(ein?.einNumber.toString())

                    } else {
                        Log.d("loadPassportData", "response not successful")
                    }
                }

                override fun onFailure(call: Call<Ein>, t: Throwable) {
                    Log.d("loadPassportData", "onFailure")
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