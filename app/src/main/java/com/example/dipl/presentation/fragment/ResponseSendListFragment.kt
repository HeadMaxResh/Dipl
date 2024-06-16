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
import com.example.dipl.databinding.FragmentResponseListBinding
import com.example.dipl.databinding.FragmentResponseSendListBinding
import com.example.dipl.domain.model.ResponseApartment
import com.example.dipl.domain.model.User
import com.example.dipl.presentation.PrefManager
import com.example.dipl.presentation.adapter.ResponseAdapter
import com.example.dipl.presentation.adapter.ResponseSendAdapter
import com.google.android.material.progressindicator.CircularProgressIndicator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ResponseSendListFragment : Fragment() {

    private var _binding: FragmentResponseSendListBinding? = null
    private val binding: FragmentResponseSendListBinding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentResponseSendListBinding == null")

    private var user: User? = null
    private lateinit var responseAdapter: ResponseSendAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressIndicator: CircularProgressIndicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResponseSendListBinding.inflate(inflater, container, false)

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
            Api.responseApartmentApiService.getSentResponsesForUser(it).enqueue(object :
                Callback<List<ResponseApartment>> {
                override fun onResponse(call: Call<List<ResponseApartment>>, response: Response<List<ResponseApartment>>) {
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

                override fun onFailure(call: Call<List<ResponseApartment>>, t: Throwable) {
                    binding.swipeRefreshLayout.isRefreshing = false
                    // Обработка сбоя
                }
            })
        }
    }

    private fun displayResponsesInRecyclerView(
        responseApartments: List<ResponseApartment>,
        recyclerView: RecyclerView
    ) {
        responseAdapter = ResponseSendAdapter(responseApartments, user)
        //responseAdapter.setOnItemClickListener(this)
        recyclerView.adapter = responseAdapter
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