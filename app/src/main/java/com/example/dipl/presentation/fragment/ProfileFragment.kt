package com.example.dipl.presentation.fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.diplback.diplserver.model.Review
import com.example.dipl.CircleTransformation
import com.example.dipl.R
import com.example.dipl.data.api.Api.apartmentInfoApiService
import com.example.dipl.databinding.FragmentProfileBinding
import com.example.dipl.domain.model.ApartmentInfo
import com.example.dipl.domain.model.User
import com.example.dipl.presentation.PrefManager
import com.example.dipl.presentation.adapter.ReviewSliderAdapter
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.net.HttpURLConnection
import java.net.URL


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentProfileBinding == null")

    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        user = PrefManager.getUser(requireContext())

        loadAllUserReviews()

        val imagePath = user?.photoUser
        imagePath?.let {
            val file = File(it)
            if (file.exists()) {
                Picasso.get()
                    .load(file)
                    .transform(CircleTransformation())
                    .into(binding.ivPerson)
            }
        }

        binding.tvUserName.text = user?.name + "" + user?.surname

        binding.cvAparments.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToMyApartmentsFragment()
            findNavController().navigate(action)
        }

        binding.cvResponses.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToResponseListFragment()
            findNavController().navigate(action)
        }

        binding.cvResponsesSend.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToResponseSendListFragment()
            findNavController().navigate(action)
        }

        binding.cvPassport.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToPassportFragment()
            findNavController().navigate(action)
        }

        binding.cvEin.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToEinFragment()
            findNavController().navigate(action)
        }

        binding.cvContracts.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToContractListFragment()
            findNavController().navigate(action)
        }

        binding.btnSettings.setOnClickListener {
            /*val action = ProfileFragmentDirections.actionProfileFragmentToSettingsFragment()
            findNavController().navigate(action)*/
            /*val settingsFragment = SettingsFragment()
            settingsFragment.show(childFragmentManager, "SettingsFragment")*/
            val action = ProfileFragmentDirections.actionProfileFragmentToSettingsDialogFragment()
            findNavController().navigate(action)
        }



        return binding.root
    }


    private fun loadAllUserReviews() {
        val userOwnerId = PrefManager.getUser(requireContext())?.id

        val callApartments = userOwnerId?.let { apartmentInfoApiService.getApartmentsByUser(it) }
        callApartments?.enqueue(object : Callback<List<ApartmentInfo>> {
            override fun onResponse(call: Call<List<ApartmentInfo>>, response: Response<List<ApartmentInfo>>) {
                if (response.isSuccessful) {
                    val apartments = response.body()

                    val allReviews = mutableListOf<Review>()

                    apartments?.forEach { apartment ->
                        val reviews = apartment.reviewList
                        if (reviews != null) {
                            allReviews.addAll(reviews)
                        }
                    }

                    viewLifecycleOwner.lifecycleScope.launch {
                        if (allReviews.isNotEmpty()) {
                            binding.cvReviews.visibility = View.VISIBLE
                            val adapter = ReviewSliderAdapter(allReviews)
                            binding.vpReview.adapter = adapter
                        } else {
                            binding.cvReviews.visibility = View.GONE
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<ApartmentInfo>>, t: Throwable) {
                // Обработка ошибки
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
