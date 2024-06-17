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
import com.example.dipl.data.api.Api.responseApartmentApiService
import com.example.dipl.databinding.FragmentMyApartmentsBinding
import com.example.dipl.databinding.FragmentResponseListBinding
import com.example.dipl.domain.model.ApartmentInfo
import com.example.dipl.domain.model.ResponseApartment
import com.example.dipl.domain.model.User
import com.example.dipl.presentation.PrefManager
import com.example.dipl.presentation.adapter.CardItemAdapter
import com.example.dipl.presentation.adapter.ResponseAdapter
import com.google.android.material.progressindicator.CircularProgressIndicator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResponseListFragment : Fragment() {

    private var _binding: FragmentResponseListBinding? = null
    private val binding: FragmentResponseListBinding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentResponseListBinding == null")

    private var user: User? = null
    private lateinit var responseAdapter: ResponseAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressIndicator: CircularProgressIndicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResponseListBinding.inflate(inflater, container, false)

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
            responseApartmentApiService.getResponsesForApartmentOwner(it).enqueue(object :
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
        responseAdapter = ResponseAdapter(responseApartments, user)
        //responseAdapter.setOnItemClickListener(this)
        recyclerView.adapter = responseAdapter
    }

    // Метод для обновления статуса отклика через API
    private fun updateResponseStatus(responseId: Int, status: String) {
        val call = responseApartmentApiService.updateResponseStatus(responseId, status)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}