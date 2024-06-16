package com.example.dipl.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dipl.R
import com.example.dipl.data.api.Api.apartmentInfoApiService
import com.example.dipl.data.api.Api.userApiService
import com.example.dipl.databinding.FragmentHomeBinding
import com.example.dipl.domain.model.ApartmentInfo
import com.example.dipl.domain.model.User
import com.example.dipl.presentation.PrefManager
import com.example.dipl.presentation.adapter.CardItemAdapter
import com.google.android.material.progressindicator.CircularProgressIndicator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment(), CardItemAdapter.OnItemClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentHomeBinding == null")

    private lateinit var cardItemAdapter: CardItemAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressIndicator: CircularProgressIndicator
    private val args: HomeFragmentArgs by navArgs()
    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        progressIndicator = binding.circularProgressIndicator
        recyclerView = binding.rvItemList
        user = PrefManager.getUser(requireContext())

        progressIndicator.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val cardItems =

        recyclerView.layoutManager = LinearLayoutManager(context)

        if (args.city == "city") {
            loadApartmentsFromApi(recyclerView)
        } else {
            loadApartmentsFromApiWithFilter(recyclerView)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            loadApartmentsFromApi(recyclerView)
        }

        val searchView = binding.llSearch.findViewById<SearchView>(R.id.sv_search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchApartmentsByName(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // Можно добавить логику для обработки изменений в тексте
                return false
            }
        })

        binding.btnFilter.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToFilterFragment()
            findNavController().navigate(action)
        }

        /*recyclerView.layoutManager = LinearLayoutManager(context)
        cardItemAdapter = CardItemAdapter(cardItems)
        cardItemAdapter.setOnItemClickListener(this)
        recyclerView.adapter = cardItemAdapter*/

        //setupClickListener()
    }

    private fun loadApartmentsFromApiWithFilter(recyclerView: RecyclerView) {

        apartmentInfoApiService.getApartmentsByFilter(
            args.city,
            args.minRent,
            args.maxRent,
            args.minArea,
            args.maxArea,
            args.countRooms
        ).enqueue(object :
            Callback<List<ApartmentInfo>> {
            override fun onResponse(call: Call<List<ApartmentInfo>>, response: Response<List<ApartmentInfo>>) {
                if (response.isSuccessful) {
                    val apartmentInfos = response.body()
                    user?.let { setApartmentIsFavorite(apartmentInfos, it, recyclerView) }

                    apartmentInfos?.let {
                        displayApartmentsInRecyclerView(it, recyclerView)
                    }

                    progressIndicator.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                } else {
                    // Обработка ошибки
                }
            }

            override fun onFailure(call: Call<List<ApartmentInfo>>, t: Throwable) {
                // Обработка ошибки
            }
        })

    }

    private fun loadApartmentsFromApi(recyclerView: RecyclerView) {


        apartmentInfoApiService.getAllApartments().enqueue(object : Callback<List<ApartmentInfo>> {
            override fun onResponse(
                call: Call<List<ApartmentInfo>>,
                response: Response<List<ApartmentInfo>>
            ) {
                if (response.isSuccessful) {
                    val apartmentInfos = response.body()

                    user?.let { setApartmentIsFavorite(apartmentInfos, it, recyclerView) }

                    apartmentInfos?.let {
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
        cardItemAdapter = CardItemAdapter(apartmentInfos, user)
        cardItemAdapter.setOnItemClickListener(this)
        recyclerView.adapter = cardItemAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(apartmentInfo: ApartmentInfo) {
        val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(apartmentInfo)
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
                        }
                    }

                    override fun onFailure(call: Call<Boolean>, t: Throwable) {
                        TODO("Not yet implemented")
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
                        }
                    }

                    override fun onFailure(call: Call<Boolean>, t: Throwable) {
                        TODO("Not yet implemented")
                    }


                })
        }
    }

    private fun searchApartmentsByName(name: String) {
        apartmentInfoApiService.getApartmentsByName(name).enqueue(object : Callback<List<ApartmentInfo>> {
            override fun onResponse(call: Call<List<ApartmentInfo>>, response: Response<List<ApartmentInfo>>) {
                if (response.isSuccessful) {
                    val apartmentInfos = response.body()

                    user?.let { setApartmentIsFavorite(apartmentInfos, it, recyclerView) }

                    apartmentInfos?.let {
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


/*private fun setupClickListener() {
        cardItemAdapter.setOnItemClickListener
    }*/

/*private fun generateDummyCardItems(): List<ApartmentInfo> {
    val apartmentInfos = mutableListOf<ApartmentInfo>()
    apartmentInfos.add(ApartmentInfo(0, "Москва", "12", 12, listOf( R.drawable.ic_apartment_btn_image, R.drawable.ic_apartment_btn_image, R.drawable.ic_apartment_btn_image), "Москва", "улица 1", 2, 5, 5, User(4, "rgfhgf", "rgrfedgrf", null, "ft", "567657", 5), "null"))
    apartmentInfos.add(ApartmentInfo(0, "Омск", "12", 12, listOf( R.drawable.ic_apartment_btn_image, R.drawable.ic_apartment_btn_image, R.drawable.ic_apartment_btn_image), "Омск", "улица 1", 2, 5, 5, User(4, "rgfhgf", "rgrfedgrf", null, "ft", "567657", 5), "null"))
    apartmentInfos.add(ApartmentInfo(0, "Нижний Новгород", "12", 12, listOf( R.drawable.ic_apartment_btn_image, R.drawable.ic_apartment_btn_image, R.drawable.ic_apartment_btn_image), "Нижний Новгород", "улица 1", 2, 5, 5, User(4, "rgfhgf", "rgrfedgrf", null, "ft", "567657", 5), "null"))
    apartmentInfos.add(ApartmentInfo(0, "Саратов", "12", 12, listOf(  R.drawable.ic_apartment_btn_image, R.drawable.ic_apartment_btn_image, R.drawable.ic_apartment_btn_image), "Саратов", "улица 1", 2, 5, 5, User(4, "rgfhgf", "rgrfedgrf", null, "ft", "567657", 5), "null"))
    return apartmentInfos
}*/