package com.example.dipl.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.dipl.R
import com.example.dipl.data.api.Api.apartmentInfoApiService
import com.example.dipl.data.api.service.ApartmentInfoApiService
import com.example.dipl.databinding.FragmentDialogBinding
import com.example.dipl.databinding.FragmentSettingsBinding
import com.example.dipl.domain.model.ApartmentInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DialogFragment : DialogFragment() {

    private var _binding: FragmentDialogBinding? = null
    private val binding: FragmentDialogBinding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentDialogBinding == null")

    private val args: DialogFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialogBinding.inflate(inflater, container, false)

        val call = apartmentInfoApiService.getApartmentHideStatus(args.apartmentId)
        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    val isHidden = response.body() ?: false
                    if (isHidden) {
                        // Квартира скрыта, отправляем запрос на отображение
                        binding.tvDel.text = "Отобразить размещение"} else {
                    }
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {

            }
        })


        binding.tvExit.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvDel.setOnClickListener {
            val apartmentId = args.apartmentId

            val call = apartmentInfoApiService.getApartmentHideStatus(apartmentId)
            call.enqueue(object : Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    if (response.isSuccessful) {
                        val isHidden = response.body() ?: false
                        if (isHidden) {
                            // Квартира скрыта, отправляем запрос на отображение
                            val showCall = apartmentInfoApiService.setApartmentShowStatus(apartmentId)
                            showCall.enqueue(object : Callback<ApartmentInfo> {
                                override fun onResponse(call: Call<ApartmentInfo>, response: Response<ApartmentInfo>) {
                                    if (response.isSuccessful) {
                                        findNavController().navigateUp()
                                    } else {
                                        Log.d("tvDel", "tvDel")
                                    }
                                }

                                override fun onFailure(call: Call<ApartmentInfo>, t: Throwable) {
                                    Log.d("tvDel", "tvDel")
                                }
                            })
                        } else {
                            // Квартира отображена, отправляем запрос на скрытие
                            val hideCall = apartmentInfoApiService.setApartmentHideStatus(apartmentId)
                            hideCall.enqueue(object : Callback<ApartmentInfo> {
                                override fun onResponse(call: Call<ApartmentInfo>, response: Response<ApartmentInfo>) {
                                    if (response.isSuccessful) {
                                        findNavController().navigateUp()
                                    } else {
                                        Log.d("tvDel", "tvDel")
                                    }
                                }

                                override fun onFailure(call: Call<ApartmentInfo>, t: Throwable) {
                                    Log.d("tvDel", "tvDel")
                                }
                            })
                        }
                    } else {
                        Log.d("tvDel", "tvDel")
                    }
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    Log.d("tvDel", "tvDel")
                }
            })
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
            params.x = resources.getDimensionPixelSize(R.dimen.dialog_right_margin) // Отступ справа
            params.y = resources.getDimensionPixelSize(R.dimen.dialog_top_margin) // Отступ сверху
            attributes = params

            //setDimAmount(0.0f)
            setBackgroundDrawableResource(R.drawable.rounded_corner_dialog_background)
            //setDimAmount(0.0f)
            setBackgroundDrawableResource(R.drawable.rounded_corner_dialog_background)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}