package com.example.dipl.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.dipl.R
import com.example.dipl.databinding.FragmentAddDescriptionBinding
import com.example.dipl.presentation.viewmodel.AddApartmentSharedViewModel
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.dipl.data.api.Api.analysisApiService
import com.example.dipl.presentation.utils.toJsonObject
import okhttp3.ResponseBody

class AddDescriptionFragment : Fragment() {

    private var _binding: FragmentAddDescriptionBinding? = null
    private val binding: FragmentAddDescriptionBinding
        get() = _binding ?: throw RuntimeException("FragmentAddDescriptionBinding == null")

    private val sharedViewModel: AddApartmentSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddDescriptionBinding.inflate(inflater, container, false)

        restorePreviousData()
        setupClickListeners()

        return binding.root
    }

    private fun restorePreviousData() {
        if (sharedViewModel.description.isNotBlank()) {
            binding.etDescription.setText(sharedViewModel.description)
        }
    }

    private fun setupClickListeners() {
        binding.btnNext.setOnClickListener {
            validateAndAnalyzeDescription()
        }
    }

    private fun validateAndAnalyzeDescription() {
        val description = binding.etDescription.text
            ?.toString()
            ?.trim()
            .orEmpty()

        if (description.isBlank()) {
            showError("Введите описание квартиры")
            return
        }

        if (description.length < 30) {
            showError("Описание слишком короткое")
            return
        }

        sharedViewModel.description = description

        analyzeText(description)
    }

    private fun analyzeText(description: String) {
        setLoading(true)

        analysisApiService.analyzeText(description)
            .enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    setLoading(false)

                    val body = response.body()

                    if (response.isSuccessful && body != null) {
                        val json = body.toJsonObject()

                        sharedViewModel.textAnalysis = json

                        showAnalysisDialog(
                            title = "Анализ описания",
                            message = buildTextAnalysisMessage(json)
                        ) {
                            findNavController().navigate(
                                R.id.action_addDescriptionFragment_to_textAnalysisResultFragment
                            )
                        }
                    } else {
                        showError("Не удалось выполнить анализ описания")
                    }
                }

                override fun onFailure(
                    call: Call<ResponseBody>,
                    t: Throwable
                ) {
                    setLoading(false)
                    showError("Ошибка подключения: ${t.message}")
                }
            })
    }

    private fun buildTextAnalysisMessage(json: JsonObject): String {
        val qualityLevel = json.get("qualityLevel")?.asString ?: "-"

        val scores = json.getAsJsonObject("scores")
        val descriptionQualityScore = scores?.get("descriptionQualityScore")?.asString ?: "-"
        val textFeatureScore = scores?.get("textFeatureScore")?.asString ?: "-"

        val repair = json.getAsJsonObject("repairFeatures")
        val repairQuality = repair?.get("repairQuality")?.asString ?: "-"
        val hasNewRepair = repair?.get("hasNewRepair")?.asBoolean ?: false

        val furniture = json.getAsJsonObject("furnitureFeatures")
        val hasFurniture = furniture?.get("hasFurniture")?.asBoolean ?: false
        val furnitureCondition = furniture?.get("furnitureCondition")?.asString ?: "-"

        val appliances = json.getAsJsonObject("appliancesFeatures")
        val appliancesCount = appliances?.get("appliancesCount")?.asString ?: "-"
        val appliancesLevel = appliances?.get("appliancesLevel")?.asString ?: "-"

        val tenantRules = json.getAsJsonObject("tenantRules")
        val petsForbidden = tenantRules?.get("petsForbidden")?.asBoolean ?: false
        val childrenForbidden = tenantRules?.get("childrenForbidden")?.asBoolean ?: false
        val depositMentioned = tenantRules?.get("depositMentioned")?.asBoolean ?: false
        val utilitiesMentioned = tenantRules?.get("utilitiesMentioned")?.asBoolean ?: false

        val priceImpact = json.getAsJsonObject("priceImpact")
        val coefficient = priceImpact?.get("coefficient")?.asString ?: "-"
        val impactDescription = priceImpact?.get("description")?.asString ?: ""

        val recommendations = json
            .getAsJsonArray("recommendations")
            ?.joinToString("\n") { "• ${it.asString}" }
            ?: "Рекомендации отсутствуют"

        return """
            Качество описания: $qualityLevel
            Оценка полноты: $descriptionQualityScore
            Оценка признаков: $textFeatureScore
            
            Ремонт: $repairQuality
            Новый ремонт: ${formatBoolean(hasNewRepair)}
            
            Мебель: ${formatBoolean(hasFurniture)}
            Состояние мебели: $furnitureCondition
            
            Количество техники: $appliancesCount
            Уровень оснащения: $appliancesLevel
            
            Условия:
            Запрет животных: ${formatBoolean(petsForbidden)}
            Запрет детей: ${formatBoolean(childrenForbidden)}
            Залог указан: ${formatBoolean(depositMentioned)}
            Коммунальные услуги указаны: ${formatBoolean(utilitiesMentioned)}
            
            Коэффициент влияния на цену: $coefficient
            $impactDescription
            
            Нюансы и рекомендации:
            $recommendations
        """.trimIndent()
    }

    private fun formatBoolean(value: Boolean): String {
        return if (value) "да" else "нет"
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnNext.isEnabled = !isLoading
        binding.etDescription.isEnabled = !isLoading

        binding.btnNext.text = if (isLoading) {
            "Анализируем..."
        } else {
            "Далее"
        }
    }

    private fun showAnalysisDialog(
        title: String,
        message: String,
        onContinue: () -> Unit
    ) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Продолжить") { _, _ ->
                onContinue()
            }
            .setNegativeButton("Остаться", null)
            .show()
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}