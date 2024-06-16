package com.example.dipl.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.dipl.R
import com.example.dipl.databinding.FragmentContractBinding
import com.example.dipl.databinding.FragmentResponseListBinding

class ContractFragment : Fragment() {

    private var _binding: FragmentContractBinding? = null
    private val binding: FragmentContractBinding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentContractBinding == null")

    private val args: ContractFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContractBinding.inflate(inflater, container, false)

        binding.city.text = args.contract.apartmentInfo.city.toString()
        binding.date.text = args.contract.date
        binding.fioOwner.text =
                "${args.contract.passportOwner.lastname}" +
                " ${args.contract.passportOwner.name} " +
                "${args.contract.passportOwner.surname} "
        binding.passportOwner.text = args.contract.passportOwner.toString()
        binding.fioUser.text =
                "${args.contract.passportSender.lastname}" +
                " ${args.contract.passportSender.name} " +
                "${args.contract.passportSender.surname} "
        binding.passportUser.text = args.contract.passportSender.toString()
        binding.apartmentAddress.text = args.contract.apartmentInfo.name
        binding.rent.text = args.contract.apartmentInfo.rent.toString()
        binding.fioOwnerEnd.text =
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
                    "${args.contract.passportSender.surname} "

        return binding.root
    }


}