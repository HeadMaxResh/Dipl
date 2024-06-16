package com.example.dipl.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.dipl.R
import com.example.dipl.data.api.service.ApartmentInfoApiService
import com.example.dipl.databinding.FragmentAddBinding
import com.example.dipl.databinding.FragmentNewReviewBinding
import com.example.dipl.domain.dto.ReviewDto
import com.example.dipl.presentation.PrefManager
import com.example.dipl.presentation.viewmodel.ApartmentInfoViewModel


class NewReviewFragment : Fragment() {

    private var _binding: FragmentNewReviewBinding? = null
    private val binding: FragmentNewReviewBinding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentNewReviewBinding == null")

    private val apartmentInfoViewModel: ApartmentInfoViewModel by viewModels()
    private var apartmentId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewReviewBinding.inflate(inflater, container, false)

        apartmentId = arguments?.getInt("apartmentId")

        apartmentId?.let { id ->
            binding.btnReview.setOnClickListener {
                performAddApartment()
                /*val action =
                    NewReviewFragmentDirections.actionNewReviewFragmentToDetailFragment(id)
                findNavController().navigate(action)*/
            }
        }

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

    private fun performAddApartment() {

        val ratingBar = binding.ratingBar
        val rating = ratingBar.rating.toInt().takeIf { it != 0 } ?: run {
            showError("Поставьте оценку")
            return
        }
        val dignity = binding.etDignity.text.toString().takeIf { it.isNotBlank() } ?: run {
            showError("Опишите достоинства")
            return
        }
        val flaws = binding.etFlaws.text.toString().takeIf { it.isNotBlank() } ?: run {
            showError("Опишите недостатки")
            return
        }
        val comments = binding.etComments.text.toString().takeIf { it.isNotBlank() } ?: run {
            showError("Введите комментарий")
            return
        }

        val user = PrefManager.getUser(requireContext())

        user?.let {
            val reviewDto = ReviewDto(
                rating,
                user,
                dignity,
                flaws,
                comments
            )
            apartmentId?.let { id ->
                apartmentInfoViewModel.addReviewToApartment(id, reviewDto)
                findNavController().navigateUp()
            }
        }

    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(),message, Toast.LENGTH_SHORT).show()
    }
}