package com.example.dipl.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.dipl.R
import com.example.dipl.databinding.FragmentPhotoAnalysisResultBinding
import com.example.dipl.presentation.viewmodel.AddApartmentSharedViewModel

class PhotoAnalysisResultFragment : Fragment() {

    private var _binding: FragmentPhotoAnalysisResultBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: AddApartmentSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoAnalysisResultBinding.inflate(inflater, container, false)

        renderResult()

        binding.btnNext.setOnClickListener {
            findNavController().navigate(
                R.id.action_photoAnalysisResultFragment_to_addAddressFragment
            )
        }

        binding.btnBackToForm.setOnClickListener {
            findNavController().popBackStack(R.id.addFragment, false)
        }

        return binding.root
    }

    private fun renderResult() {
        val json = sharedViewModel.imageAnalysis ?: return
        val summary = json.getAsJsonObject("apartmentSummary")

        addBlock("Визуальная оценка", summary?.get("averageVisualScore")?.asString ?: "-")
        addBlock("Уровень качества", summary?.get("visualQualityLevel")?.asString ?: "-")
        addBlock("Ремонт", summary?.get("dominantRepairQuality")?.asString ?: "-")
        addBlock("Мебель", summary?.get("dominantFurnitureCondition")?.asString ?: "-")
        addBlock("Наполненность", summary?.get("dominantFurnitureLevel")?.asString ?: "-")

        val rooms = summary
            ?.getAsJsonArray("detectedRooms")
            ?.joinToString("\n") { "• ${it.asString}" }
            ?: "-"

        addBlock("Обнаруженные помещения", rooms)

        val recommendations = summary
            ?.getAsJsonArray("recommendations")
            ?.joinToString("\n") { "• ${it.asString}" }
            ?: "Рекомендаций нет"

        addBlock("Рекомендации", recommendations)
    }

    private fun addBlock(title: String, value: String) {
        val view = TextView(requireContext()).apply {
            text = "$title\n$value"
            textSize = 15f
            setTextColor(resources.getColor(R.color.black, null))
            setPadding(24, 20, 24, 20)
            background = resources.getDrawable(R.drawable.bg_analysis_card, null)
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 16)

        binding.llContent.addView(view, params)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}