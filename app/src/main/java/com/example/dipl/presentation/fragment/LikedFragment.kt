package com.example.dipl.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dipl.data.api.Api.userApiService
import com.example.dipl.databinding.FragmentLikedBinding
import com.example.dipl.domain.model.ApartmentInfo
import com.example.dipl.domain.model.User
import com.example.dipl.presentation.PrefManager
import com.example.dipl.presentation.adapter.CardItemAdapter
import com.google.android.material.progressindicator.CircularProgressIndicator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LikedFragment : Fragment(), CardItemAdapter.OnItemClickListener {

    private var _binding: FragmentLikedBinding? = null
    private val binding: FragmentLikedBinding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentLikedBinding == null")

    private lateinit var cardItemAdapter: CardItemAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressIndicator: CircularProgressIndicator
    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLikedBinding.inflate(inflater, container, false)

        user = PrefManager.getUser(requireContext())

        progressIndicator = binding.circularProgressIndicator
        recyclerView = binding.rvItemListLiked

        progressIndicator.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(context)
        loadLikedApartmentsFromApi(recyclerView)

        binding.swipeRefreshLayout.setOnRefreshListener {
            loadLikedApartmentsFromApi(recyclerView)
        }
    }

    private fun loadLikedApartmentsFromApi(recyclerView: RecyclerView) {

        val user = PrefManager.getUser(requireContext())

        user?.id?.let {
            userApiService.getFavoriteApartments(it)
                .enqueue(object : Callback<List<ApartmentInfo>> {
                    override fun onResponse(
                        call: Call<List<ApartmentInfo>>,
                        response: Response<List<ApartmentInfo>>
                    ) {
                        if (response.isSuccessful) {

                            val apartmentInfos = response.body()

                            setApartmentIsFavorite(apartmentInfos, user, recyclerView)

                            apartmentInfos?.let { it ->
                                displayApartmentsInRecyclerView(it, recyclerView)
                            }
                            progressIndicator.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                        } else {
                            // Обработка ошибки
                        }
                        binding.swipeRefreshLayout.isRefreshing = false
                    }

                    override fun onFailure(call: Call<List<ApartmentInfo>>, t: Throwable) {
                        // Обработка ошибки
                        binding.swipeRefreshLayout.isRefreshing = false
                    }

                })
        }

    }

    private fun setApartmentIsFavorite(
        apartmentInfos: List<ApartmentInfo>?,
        user: User,
        recyclerView: RecyclerView
    ) {
        apartmentInfos?.let { list ->
            list.forEach { apartmentInfo ->
                userApiService.checkIfApartmentIsFavorite(user.id, apartmentInfo.id)
                    .enqueue(object : Callback<Boolean> {
                        override fun onResponse(
                            call: Call<Boolean>,
                            response: Response<Boolean>
                        ) {
                            if (response.isSuccessful) {
                                val isFavorite = response.body() ?: false
                                apartmentInfo.isFavorite = isFavorite
                            } else {
                                // Обработка ошибки
                            }
                            displayApartmentsInRecyclerView(list, recyclerView)
                        }

                        override fun onFailure(call: Call<Boolean>, t: Throwable) {
                            // Обработка ошибки
                        }
                    })
            }
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(apartmentInfo: ApartmentInfo) {
        val action = LikedFragmentDirections.actionLikedFragmentToDetailFragment(apartmentInfo)
        findNavController().navigate(action)
    }

    override fun onAddToFavorites(apartmentInfo: ApartmentInfo) {

        user?.let {
            userApiService.addFavoriteApartment(it.id, apartmentInfo.id)
                .enqueue(object : Callback<Boolean> {
                    override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                        if (response.isSuccessful) {
                            // Apartment successfully added to favorites
                            apartmentInfo.isFavorite = true
                            cardItemAdapter.notifyDataSetChanged()
                        } else {
                            // Handle unsuccessful response
                            // For example, show an error message to the user
                            Log.e("AddToFavorites", "Error: ${response.code()}")
                            // You can also show a Toast message
                            Toast.makeText(
                                requireContext(),
                                "Failed to add apartment to favorites",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Boolean>, t: Throwable) {
                        Log.e("AddToFavorites", "Error: ${t.message}")
                        // You can show a Toast message or dialog with error information
                        Toast.makeText(
                            requireContext(),
                            "Network error. Please try again later",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })
        }

    }

    override fun onRemoveFromFavorites(apartmentInfo: ApartmentInfo) {
        user?.let {
            userApiService.removeFavoriteApartment(it.id, apartmentInfo.id)
                .enqueue(object : Callback<Boolean> {
                    override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                        if (response.isSuccessful && response.body() == true) {
                            // Apartment successfully removed from favorites
                            apartmentInfo.isFavorite = false
                            cardItemAdapter.notifyDataSetChanged()
                        } else {
                            Log.e("RemoveFromFavorites", "Error: ${response.code()}")
                            // You can also show a Toast message
                            Toast.makeText(
                                requireContext(),
                                "Failed to remove apartment from favorites",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Boolean>, t: Throwable) {
                        Log.e("RemoveFromFavorites", "Error: ${t.message}")
                        // You can show a Toast message or dialog with error information
                        Toast.makeText(
                            requireContext(),
                            "Network error. Please try again later",
                            Toast.LENGTH_SHORT
                        ).show()
                    }


                })
        }
    }
}