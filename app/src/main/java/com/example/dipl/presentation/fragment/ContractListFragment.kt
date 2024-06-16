package com.example.dipl.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dipl.R
import com.example.dipl.data.api.Api
import com.example.dipl.databinding.FragmentContractListBinding
import com.example.dipl.databinding.FragmentEinBinding
import com.example.dipl.databinding.FragmentProfileBinding
import com.example.dipl.domain.model.Contract
import com.example.dipl.domain.model.ResponseApartment
import com.example.dipl.domain.model.User
import com.example.dipl.presentation.PrefManager
import com.example.dipl.presentation.adapter.ContractAdapter
import com.example.dipl.presentation.adapter.ResponseSendAdapter
import com.google.android.material.progressindicator.CircularProgressIndicator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContractListFragment : Fragment() {

    private var _binding: FragmentContractListBinding? = null
    private val binding: FragmentContractListBinding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentContractListBinding == null")

    private var user: User? = null
    private lateinit var contractAdapter: ContractAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressIndicator: CircularProgressIndicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContractListBinding.inflate(inflater, container, false)

        progressIndicator = binding.circularProgressIndicator
        recyclerView = binding.rvItemList
        user = PrefManager.getUser(requireContext())

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            loadResponsesFromApi(recyclerView)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(context)

        loadResponsesFromApi(recyclerView)
    }

    private fun loadResponsesFromApi(recyclerView: RecyclerView) {
        user?.id?.let {
            Api.contractApiService.getContractsByUserSenderOrOwner(it).enqueue(object :
                Callback<List<Contract>> {
                override fun onResponse(call: Call<List<Contract>>, response: Response<List<Contract>>) {
                    if (response.isSuccessful) {
                        val responseList = response.body()
                        // Обновите адаптер RecyclerView с responseList
                        responseList?.let { it1 -> displayResponsesInRecyclerView(it1, recyclerView) }
                        binding.swipeRefreshLayout.isRefreshing = false
                    } else {
                        // Обработка ошибки
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                }

                override fun onFailure(call: Call<List<Contract>>, t: Throwable) {
                    binding.swipeRefreshLayout.isRefreshing = false
                    // Обработка сбоя
                }
            })
        }
    }

    private fun displayResponsesInRecyclerView(
        contractApartments: List<Contract>,
        recyclerView: RecyclerView
    ) {
        contractAdapter = ContractAdapter(contractApartments, user)
        //responseAdapter.setOnItemClickListener(this)
        recyclerView.adapter = contractAdapter
    }

    // Метод для обновления статуса отклика через API
    private fun updateResponseStatus(responseId: Int, status: String) {
        val call = Api.responseApartmentApiService.updateResponseStatus(responseId, status)
        call.enqueue(object : Callback<ResponseApartment> {
            override fun onResponse(call: Call<ResponseApartment>, response: Response<ResponseApartment>) {
                if (response.isSuccessful) {
                    // Обновить UI или выполнить действия после успешного обновления статуса
                } else {
                    // Обработать ошибку
                }
            }

            override fun onFailure(call: Call<ResponseApartment>, t: Throwable) {
                // Обработать сбой запроса
            }
        })
    }
}