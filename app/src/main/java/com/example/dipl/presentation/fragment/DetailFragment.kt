package com.example.dipl.presentation.fragment

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.diplback.diplserver.model.Ein
import com.diplback.diplserver.model.Passport
import com.example.dipl.CircleTransformation
import com.example.dipl.R
import com.example.dipl.data.api.Api.einApiService
import com.example.dipl.data.api.Api.passportApiService
import com.example.dipl.data.api.Api.responseApartmentApiService
import com.example.dipl.databinding.FragmentDetailBinding
import com.example.dipl.domain.model.ApartmentInfo
import com.example.dipl.domain.model.ResponseApartment
import com.example.dipl.domain.model.User
import com.example.dipl.presentation.PrefManager
import com.example.dipl.presentation.adapter.ImageSliderAdapter
import com.example.dipl.presentation.adapter.ReviewSliderAdapter
import com.example.dipl.presentation.viewmodel.ApartmentInfoViewModel
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding: FragmentDetailBinding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentDetailBinding == null")

    private val apartmentInfoViewModel: ApartmentInfoViewModel by viewModels()
    private val args: DetailFragmentArgs by navArgs()
    private var apartmentId: Int? = null
    private var user: User? = null
    private var apartmentInfo: ApartmentInfo? = null


    private lateinit var progressIndicator: CircularProgressIndicator
    private lateinit var scrollView: ScrollView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(layoutInflater, container, false)

        user = PrefManager.getUser(requireContext())
        apartmentInfo = args.apartmentInfo

        progressIndicator = binding.circularProgressIndicator
        scrollView = binding.svDetail

        progressIndicator.visibility = View.VISIBLE
        scrollView.visibility = View.GONE

        /*apartmentInfoViewModel.getSelectedApartment().observe(viewLifecycleOwner, Observer { apartment ->
            apartmentInfo = apartment

        })*/

        binding.swipeRefreshLayout.setOnRefreshListener {
            loadApartmentInfo()
        }

        binding.btnChat.setOnClickListener {
            if (apartmentInfo != null) {
                val action = DetailFragmentDirections.actionDetailFragmentToChatDetailFragment(
                    apartmentInfo!!.userOwner
                )
                findNavController().navigate(action)
            }

        }

        apartmentInfo?.isFavorite?.let { setHeartButton(it) }

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        apartmentId = apartmentInfo?.id

        apartmentId?.let { id ->
            apartmentInfoViewModel.getApartmentById(id)
        }

        binding.btnResponse.setOnClickListener {
            user?.id?.let { userId ->
                apartmentId?.let { apartmentId ->
                    addResponseToApartment(apartmentId, userId)

                }
            }

        }

        if (apartmentInfo?.userOwner == PrefManager.getUser(requireContext())) {
            binding.btnResponse.visibility = View.GONE
        }

        apartmentId?.let { id ->
            binding.btnReview.setOnClickListener {
                val action = DetailFragmentDirections.actionDetailFragmentToNewReviewFragment(id)
                findNavController().navigate(action)
            }
        }

        loadApartmentInfo()

        binding.heart.setOnClickListener {
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
        }
    }

    private fun addResponseToApartment(apartmentId: Int, userId: Int) {

        passportApiService.getPassportByUserId(userId).enqueue(object : Callback<Passport> {
            override fun onResponse(call: Call<Passport>, response: Response<Passport>) {
                val passport = response.body()
                if (passport != null) {
                    // Паспорт найден, проверяем ЕИН
                    einApiService.getEinByUserId(userId).enqueue(object : Callback<Ein> {
                        override fun onResponse(call: Call<Ein>, response: Response<Ein>) {
                            val ein = response.body()
                            if (ein != null) {

                                val call = responseApartmentApiService.addResponseToApartment(
                                    apartmentId,
                                    userId
                                )
                                call.enqueue(object : Callback<ResponseApartment> {
                                    override fun onResponse(
                                        call: Call<ResponseApartment>,
                                        response: Response<ResponseApartment>
                                    ) {
                                        if (response.isSuccessful) {
                                            // Обработка успешного ответа
                                        } else {
                                            showError("Не удалось отправить отклик")
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<ResponseApartment>,
                                        t: Throwable
                                    ) {
                                        showError("Не удалось отправить отклик")
                                    }
                                })
                            } else {
                                showError("Добавьте в профиле Инн")
                            }
                        }

                        override fun onFailure(call: Call<Ein>, t: Throwable) {
                            showError("Добавьте в профиле Инн")
                        }
                    })
                } else {
                    showError("Добавьте в профиле паспорт")
                }
            }

            override fun onFailure(call: Call<Passport>, t: Throwable) {
                showError("Добавьте в профиле паспорт")
            }
        })
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

    private fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    private fun getImageByteArrayFromFile(imagePath: String): ByteArray {
        val file = File(imagePath)
        return file.readBytes()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setHeartButton(isFavorite: Boolean) {
        if (isFavorite) {
            binding.heart.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.red)
            )
        } else {
            binding.heart.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.black)
            )
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}


/*companion object {

        private const val EXTRA_CARD_ITEM_ID = "extra_card_item_id"

        fun newIntentItem(context: Context, cardId: Int): Intent {
            val intent = Intent(context, DetailFragment::class.java)
            intent.putExtra(EXTRA_CARD_ITEM_ID, cardId)
            return intent
        }
    }*/
/*companion object {
    fun newInstance(apartmentInfo: ApartmentInfo): Any {

    }
}*/
