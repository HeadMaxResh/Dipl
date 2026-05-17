package com.example.dipl.presentation.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dipl.data.api.Api
import com.example.dipl.databinding.FragmentModerContractListBinding
import com.example.dipl.domain.model.Contract
import com.example.dipl.domain.model.ResponseApartment
import com.example.dipl.domain.model.User
import com.example.dipl.presentation.PrefManager
import com.example.dipl.presentation.adapter.ModerContractAdapter
import com.google.android.material.progressindicator.CircularProgressIndicator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ModerContractListFragment : Fragment() {

    private var _binding: FragmentModerContractListBinding? = null
    private val binding: FragmentModerContractListBinding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentModerContractListBinding == null")

    private var user: User? = null
    private lateinit var contractAdapter: ModerContractAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressIndicator: CircularProgressIndicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModerContractListBinding.inflate(layoutInflater, container, false)

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
            Api.contractApiService.getAllContracts().enqueue(object :
                Callback<List<Contract>> {
                override fun onResponse(call: Call<List<Contract>>, response: Response<List<Contract>>) {
                    if (response.isSuccessful) {
                        val responseList = response.body()
                        responseList?.let { it1 -> displayResponsesInRecyclerView(it1, recyclerView) }
                        binding.swipeRefreshLayout.isRefreshing = false
                    } else {
                        Log.d("loadResponsesFromApi", "loadResponsesFromApi")
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                }

                override fun onFailure(call: Call<List<Contract>>, t: Throwable) {
                    binding.swipeRefreshLayout.isRefreshing = false
                    Log.d("loadResponsesFromApi", "loadResponsesFromApi")
                }
            })
        }
    }

    private fun displayResponsesInRecyclerView(
        contractApartments: List<Contract>,
        recyclerView: RecyclerView
    ) {
        contractAdapter = ModerContractAdapter(contractApartments, user)
        //responseAdapter.setOnItemClickListener(this)
        recyclerView.adapter = contractAdapter
    }

    private fun updateResponseStatus(responseId: Int, status: String) {
        val call = Api.responseApartmentApiService.updateResponseStatus(responseId, status)
        call.enqueue(object : Callback<ResponseApartment> {
            override fun onResponse(call: Call<ResponseApartment>, response: Response<ResponseApartment>) {
                if (response.isSuccessful) {

                } else {

                }
            }

            override fun onFailure(call: Call<ResponseApartment>, t: Throwable) {
                Log.d("updateResponseStatus", "updateResponseStatus")
            }
        })
    }
}