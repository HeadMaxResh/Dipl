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
import com.example.dipl.databinding.FragmentMyApartmentsBinding
import com.example.dipl.databinding.FragmentProfileBinding
import com.example.dipl.domain.model.ApartmentInfo
import com.example.dipl.domain.model.User
import com.example.dipl.presentation.PrefManager
import com.example.dipl.presentation.adapter.CardItemAdapter
import com.google.android.material.progressindicator.CircularProgressIndicator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyApartmentsFragment : Fragment(), CardItemAdapter.OnItemClickListener {

    private var _binding: FragmentMyApartmentsBinding? = null
    private val binding: FragmentMyApartmentsBinding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentMyApartmentsBinding == null")

    private lateinit var cardItemAdapter: CardItemAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressIndicator: CircularProgressIndicator
    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyApartmentsBinding.inflate(inflater, container, false)

        progressIndicator = binding.circularProgressIndicator
        recyclerView = binding.rvItemList

        progressIndicator.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(context)

        loadAllUserApartments(recyclerView)

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            loadAllUserApartments(recyclerView)
        }
    }

    private fun loadAllUserApartments(recyclerView: RecyclerView) {
        val userOwnerId = PrefManager.getUser(requireContext())?.id

        val callApartments = userOwnerId?.let { Api.apartmentInfoApiService.getApartmentsByUser(it) }
        callApartments?.enqueue(object : Callback<List<ApartmentInfo>> {
            override fun onResponse(
                call: Call<List<ApartmentInfo>>,
                response: Response<List<ApartmentInfo>>
            ) {
                if (response.isSuccessful) {
                    val apartments = response.body()
                    apartments?.let {
                        displayApartmentsInRecyclerView(it, recyclerView)
                    }
                    progressIndicator.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<List<ApartmentInfo>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun displayApartmentsInRecyclerView(
        apartmentInfos: List<ApartmentInfo>,
        recyclerView: RecyclerView
    ) {
        if (apartmentInfos.isEmpty()) {
            binding.tvEmptyList.visibility = View.VISIBLE
        } else {
            binding.tvEmptyList.visibility = View.GONE
            cardItemAdapter = CardItemAdapter(apartmentInfos, user)
            cardItemAdapter.setOnItemClickListener(this)
            recyclerView.adapter = cardItemAdapter
        }
    }

    override fun onItemClick(apartmentInfo: ApartmentInfo) {
        val action = MyApartmentsFragmentDirections.actionMyApartmentsFragmentToDetailFragment(
            apartmentInfo
        )
        findNavController().navigate(action)
    }

    override fun onAddToFavorites(apartmentInfo: ApartmentInfo) {}

    override fun onRemoveFromFavorites(apartmentInfo: ApartmentInfo) {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}