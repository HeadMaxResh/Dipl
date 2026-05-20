package com.example.dipl.presentation.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.dipl.data.api.Api
import com.example.dipl.databinding.FragmentModerApartmentListBinding
import com.example.dipl.domain.model.ApartmentInfo
import com.example.dipl.domain.model.User
import com.example.dipl.presentation.PrefManager
import com.example.dipl.presentation.adapter.ModerCardItemAdapter
import com.google.android.material.progressindicator.CircularProgressIndicator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ModerApartmentListFragment : Fragment(), ModerCardItemAdapter.OnItemClickListener {

    private var _binding: FragmentModerApartmentListBinding? = null
    private val binding: FragmentModerApartmentListBinding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentModerApartmentListBinding == null")

    private lateinit var cardItemAdapter: ModerCardItemAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressIndicator: CircularProgressIndicator
    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModerApartmentListBinding.inflate(inflater, container, false)

        progressIndicator = binding.circularProgressIndicator
        recyclerView = binding.rvItemList
        user = PrefManager.getUser(requireContext())

        loadApartmentsFromApi(recyclerView)

        progressIndicator.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefreshLayout.setOnRefreshListener {
            loadApartmentsFromApi(recyclerView)
        }

    }

    private fun loadApartmentsFromApi(recyclerView: RecyclerView) {


        Api.apartmentInfoApiService.getAllApartments().enqueue(object :
            Callback<List<ApartmentInfo>> {
            override fun onResponse(
                call: Call<List<ApartmentInfo>>,
                response: Response<List<ApartmentInfo>>
            ) {
                if (response.isSuccessful) {
                    val apartmentInfos = response.body()

                    apartmentInfos?.let {
                        displayApartmentsInRecyclerView(it, recyclerView)
                    }
                    progressIndicator.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                } else {
                    Log.d("loadApartmentsFromApi", "loadApartmentsFromApi")
                }
                binding.swipeRefreshLayout.isRefreshing = false
            }

            override fun onFailure(call: Call<List<ApartmentInfo>>, t: Throwable) {
                Log.d("loadApartmentsFromApi", "loadApartmentsFromApi")
                binding.swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    private fun displayApartmentsInRecyclerView(
        apartmentInfos: List<ApartmentInfo>,
        recyclerView: RecyclerView
    ) {
        cardItemAdapter = ModerCardItemAdapter(apartmentInfos, user)
        cardItemAdapter.setOnItemClickListener(this)
        recyclerView.adapter = cardItemAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(apartmentInfo: ApartmentInfo) {
        val action = ModerApartmentListFragmentDirections.actionModerApartmentListFragmentToModerDetailFragment(apartmentInfo)
        findNavController().navigate(action)
    }

    override fun onRemove(apartmentInfo: ApartmentInfo) {
        TODO("Not yet implemented")
    }

}