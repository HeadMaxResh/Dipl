package com.example.dipl.presentation.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.diplback.diplserver.dto.ContractDto
import com.diplback.diplserver.model.Passport
import com.example.dipl.R
import com.example.dipl.data.api.Api
import com.example.dipl.data.api.Api.passportApiService
import com.example.dipl.databinding.FragmentContractBinding
import com.example.dipl.databinding.FragmentContractCheckBinding
import com.example.dipl.domain.model.Contract
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

class ContractCheckFragment : Fragment() {

    private var _binding: FragmentContractCheckBinding? = null
    private val binding: FragmentContractCheckBinding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentContractCheckBinding == null")

    private val args: ContractCheckFragmentArgs by navArgs()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContractCheckBinding.inflate(inflater, container, false)



        setInfo()

        binding.btnCreateContract.setOnClickListener {

            args.user.let { user ->
                val contractDto = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    args.apartmentInfo.userOwner.electronicSignature?.let { it1 ->
                        user.electronicSignature?.let { it2 ->
                            ContractDto(
                                apartmentInfoId = args.apartmentInfo.id,
                                userOwnerId = args.apartmentInfo.userOwner.id,
                                userSenderId = user.id,
                                date = LocalDate.now().toString(),
                                ownerElectronicSignature = it1,
                                senderElectronicSignature = it2
                            )
                        }
                    }
                } else {
                    TODO("VERSION.SDK_INT < O")
                }

                val call = contractDto?.let { it1 -> Api.contractApiService.createContract(it1) }
                call?.enqueue(object : Callback<Contract> {
                    override fun onResponse(call: Call<Contract>, response: Response<Contract>) {
                        if (response.isSuccessful) {
                            val contract = response.body()
                            /*val action = contract?.let { it1 ->
                                ResponseSendListFragmentDirections.actionResponseSendListFragmentToContractFragment(
                                    it1
                                )
                            }
                            action?.let { it1 -> holder.itemView.findNavController().navigate(it1) }*/
                            findNavController().navigateUp()
                            //Toast.makeText(context, "Контракт успешно создан", Toast.LENGTH_SHORT).show()
                        } else {
                            // Обработка ошибки при создании контракта
                            //Toast.makeText(context, "Ошибка при создании контракта", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Contract>, t: Throwable) {
                        // Обработка ошибки сети или запроса
                        //Toast.makeText(context, "Ошибка сети", Toast.LENGTH_SHORT).show()
                    }
                })

            }
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setInfo() {
        binding.city.text = args.apartmentInfo.city
        binding.date.text = LocalDate.now().toString()

        passportApiService.getPassportByUserId(args.apartmentInfo.userOwner.id).enqueue(object : Callback<Passport> {
            override fun onResponse(call: Call<Passport>, response: Response<Passport>) {
                if (response.isSuccessful) {
                    val passportOwner = response.body()
                    // Установка ФИО и паспорта владельца квартиры в TextView
                    binding.fioOwner.text = "${passportOwner?.lastname} ${passportOwner?.name} ${passportOwner?.surname}"
                    binding.passportOwner.text = passportOwner?.toString()
                }
            }

            override fun onFailure(call: Call<Passport>, t: Throwable) {
                // Обработка ошибки при получении паспорта владельца квартиры
            }
        })

        // Получение паспорта пользователя (user)
        passportApiService.getPassportByUserId(args.user.id).enqueue(object : Callback<Passport> {
            override fun onResponse(call: Call<Passport>, response: Response<Passport>) {
                if (response.isSuccessful) {
                    val passportUser = response.body()
                    // Установка ФИО и паспорта пользователя в TextView
                    binding.fioUser.text = "${passportUser?.lastname} ${passportUser?.name} ${passportUser?.surname}"
                    binding.passportUser.text = passportUser?.toString()
                }
            }

            override fun onFailure(call: Call<Passport>, t: Throwable) {
                // Обработка ошибки при получении паспорта пользователя
            }
        })

        binding.rent.text = args.apartmentInfo.rent.toString()


        /*binding.fioOwnerEnd.text =
            "${args.contract.passportOwner.lastname}" +
                    " ${args.contract.passportOwner.name} " +
                    "${args.contract.passportOwner.surname} "
        binding.signOwner.text =
            "${args.contract.passportOwner.lastname}" +
                    " ${args.contract.passportOwner.name} " +
                    "${args.contract.passportOwner.surname} "

        binding.fioUserEnd.text =
            "${args.contract.passportSender.lastname}" +
                    " ${args.contract.passportSender.name} " +
                    "${args.contract.passportSender.surname} "
        binding.signUser.text =
            "${args.contract.passportSender.lastname}" +
                    " ${args.contract.passportSender.name} " +
                    "${args.contract.passportSender.surname} "*/
    }


}