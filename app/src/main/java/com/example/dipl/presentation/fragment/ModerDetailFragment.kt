package com.example.dipl.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.dipl.CircleTransformation
import com.example.dipl.databinding.FragmentModerDetailBinding
import com.example.dipl.domain.model.ApartmentInfo
import com.example.dipl.domain.model.User
import com.example.dipl.presentation.PrefManager
import com.example.dipl.presentation.adapter.ImageSliderAdapter
import com.example.dipl.presentation.adapter.ReviewSliderAdapter
import com.example.dipl.presentation.viewmodel.ApartmentInfoViewModel
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.squareup.picasso.Picasso
import java.io.File


class ModerDetailFragment : Fragment() {

    private var _binding: FragmentModerDetailBinding? = null
    private val binding: FragmentModerDetailBinding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentModerDetailBinding == null")

    private val args: ModerDetailFragmentArgs by navArgs()
    private val apartmentInfoViewModel: ApartmentInfoViewModel by viewModels()
    private var apartmentId: Int? = null
    private var user: User? = null
    private var apartmentInfo: ApartmentInfo? = null

    private lateinit var progressIndicator: CircularProgressIndicator
    private lateinit var scrollView: ScrollView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModerDetailBinding.inflate(inflater, container, false)

        user = PrefManager.getUser(requireContext())
        apartmentInfo = args.apartmentInfo

        progressIndicator = binding.circularProgressIndicator
        scrollView = binding.svDetail

        progressIndicator.visibility = View.VISIBLE
        scrollView.visibility = View.GONE

        binding.swipeRefreshLayout.setOnRefreshListener {
            loadApartmentInfo()
        }

        binding.btnChat.setOnClickListener {
            if (apartmentInfo != null) {
                val action = ModerDetailFragmentDirections.actionModerDetailFragmentToChatDetailFragment2(
                    apartmentInfo!!.userOwner
                )
                findNavController().navigate(action)
            }

        }

        //apartmentInfo?.isFavorite?.let { setHeartButton(it) }

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.del.setOnClickListener {
            val action = ModerDetailFragmentDirections.actionModerDetailFragmentToDialogFragment(
                apartmentInfo!!.id)
            findNavController().navigate(action)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        apartmentId = apartmentInfo?.id

        apartmentId?.let { id ->
            apartmentInfoViewModel.getApartmentById(id)
        }

        /*binding.btnResponse.setOnClickListener {
            user?.id?.let { userId ->
                apartmentId?.let { apartmentId ->
                    addResponseToApartment(apartmentId, userId)

                }
            }

        }*/

        /*if (apartmentInfo?.userOwner == PrefManager.getUser(requireContext())) {
            binding.btnResponse.visibility = View.GONE
        }

        apartmentId?.let { id ->
            binding.btnReview.setOnClickListener {
                val action = DetailFragmentDirections.actionDetailFragmentToNewReviewFragment(id)
                findNavController().navigate(action)
            }
        }*/

        loadApartmentInfo()

        /*binding.heart.setOnClickListener {
            apartmentInfo?.let { apartmentInfo ->
                if (apartmentInfo.userOwner.id == user?.id) {
                    Toast.makeText(
                        requireContext(),
                        "Вы не можете сохранять в понравившиеся собственные квартиры",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (apartmentInfo.isFavorite) {
                        apartmentInfoViewModel.removeFromFavorites(user?.id ?: 0, apartmentInfo)
                        apartmentInfo.isFavorite = false
                        setHeartButton(apartmentInfo.isFavorite)
                    } else {
                        apartmentInfoViewModel.addToFavorites(user?.id ?: 0, apartmentInfo)
                        apartmentInfo.isFavorite = true
                        setHeartButton(apartmentInfo.isFavorite)
                    }
                }
            }
        }*/
    }

    private fun loadApartmentInfo() {
        apartmentInfoViewModel.getSelectedApartment()
            .observe(viewLifecycleOwner, Observer { apartment ->

                val images =
                    apartment.listImages.map { imagePath -> getImageByteArrayFromFile(imagePath) }
                        .toMutableList()
                val adapterDetail = ImageSliderAdapter(images)
                binding.vpDetail.adapter = adapterDetail
                binding.tvName.text = apartment.name
                binding.tvCity.text = apartment.city
                binding.tvRent.text = apartment.rent.toString()
                binding.tvRate.text = apartment.rate.toString()
                binding.tvOwnerName.text = apartment.userOwner.name
                binding.tvDescription.text = apartment.description
                binding.tvCadastr.text = apartment.cadastr

                val imagePath = apartment.userOwner.photoUser
                imagePath?.let {
                    val file = File(it)
                    if (file.exists()) {
                        Picasso.get()
                            .load(file)
                            .transform(CircleTransformation())
                            .into(binding.ivProfile)
                    }
                }

                val reviews = apartment.reviewList
                if (reviews != null) {
                    val viewPagerReview = binding.vpReview
                    val adapterReview = ReviewSliderAdapter(reviews)
                    viewPagerReview.adapter = adapterReview
                    viewPagerReview.visibility = View.VISIBLE
                } else {
                    binding.vpReview.visibility = View.GONE
                }

                progressIndicator.visibility = View.GONE
                scrollView.visibility = View.VISIBLE
                binding.swipeRefreshLayout.isRefreshing = false
            })
    }




    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun getImageByteArrayFromFile(imagePath: String): ByteArray {
        val file = File(imagePath)
        return file.readBytes()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}