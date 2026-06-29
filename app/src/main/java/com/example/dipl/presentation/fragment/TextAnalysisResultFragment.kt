package com.example.dipl.presentation.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.dipl.R
import com.example.dipl.databinding.FragmentTextAnalysisResultBinding
import com.example.dipl.presentation.viewmodel.AddApartmentSharedViewModel
import com.google.gson.JsonArray
import com.google.gson.JsonObject

class TextAnalysisResultFragment : Fragment() {

    private var _binding: FragmentTextAnalysisResultBinding? = null
    private val binding: FragmentTextAnalysisResultBinding
        get() = _binding ?: throw RuntimeException("FragmentTextAnalysisResultBinding == null")

    private val sharedViewModel: AddApartmentSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTextAnalysisResultBinding.inflate(inflater, container, false)

        renderResult()
        setupClickListeners()

        return binding.root
    }

    private fun setupClickListeners() {
        binding.btnNext.setOnClickListener {
            findNavController().navigate(
                R.id.action_textAnalysisResultFragment_to_apartmentPriceSummaryFragment
            )
        }

        binding.btnBackToForm.setOnClickListener {
            findNavController().popBackStack(R.id.addDescriptionFragment, false)
        }
    }

    private fun renderResult() {
        binding.llContent.removeAllViews()

        val json = sharedViewModel.textAnalysis

        if (json == null) {
            addCard(
                title = "Нет данных",
                value = "Результат анализа описания отсутствует. Вернитесь к форме и выполните анализ снова."
            )
            return
        }

        addCard(
            title = "Итоговая оценка описания",
            value = """
|Уровень: ${json.safeString("qualityLevel")}
|Длина текста: ${json.safeString("textLength")} символов
|Нормализованная длина: ${json.safeString("normalizedTextLength")} символов
""".trimMargin()
        )

        renderScores(json)
        renderApartmentFeatures(json)
        renderRepairFeatures(json)
        renderFurnitureFeatures(json)
        renderAppliancesFeatures(json)
        renderTenantRules(json)
        renderPriceImpact(json)
        renderRecommendations(json)
    }

    private fun renderScores(json: JsonObject) {
        val scores = json.safeObject("scores")

        addCard(
            title = "Оценки текста",
            value = """
|Полнота описания: ${scores.safeString("descriptionQualityScore")}
|Признаки квартиры: ${scores.safeString("textFeatureScore")}
""".trimMargin()
        )
    }

    private fun renderApartmentFeatures(json: JsonObject) {
        val features = json.safeObject("apartmentFeatures")

        addCard(
            title = "Характеристики квартиры",
            value = """
|Комнат: ${features.safeString("rooms")}
|Площадь: ${features.safeString("area")} м²
|Этаж: ${formatFloor(features.safeObject("floor"))}
|Балкон: ${formatBoolean(features.safeBoolean("hasBalcony"))}
|Парковка: ${formatBoolean(features.safeBoolean("hasParking"))}
|Лифт: ${formatBoolean(features.safeBoolean("hasElevator"))}
|Студия: ${formatBoolean(features.safeBoolean("isStudio"))}
|Изолированные комнаты: ${formatBoolean(features.safeBoolean("hasSeparateRooms"))}
""".trimMargin()
        )
    }

    private fun renderRepairFeatures(json: JsonObject) {
        val repair = json.safeObject("repairFeatures")

        addCard(
            title = "Ремонт",
            value = """
|Качество ремонта: ${repair.safeString("repairQuality")}
|Новый ремонт: ${formatBoolean(repair.safeBoolean("hasNewRepair"))}
|Дизайнерский ремонт: ${formatBoolean(repair.safeBoolean("hasDesignRepair"))}
|Требуется ремонт: ${formatBoolean(repair.safeBoolean("needsRepair"))}
""".trimMargin()
        )
    }

    private fun renderFurnitureFeatures(json: JsonObject) {
        val furniture = json.safeObject("furnitureFeatures")

        addCard(
            title = "Мебель",
            value = """
|Мебель указана: ${formatBoolean(furniture.safeBoolean("hasFurniture"))}
|Без мебели: ${formatBoolean(furniture.safeBoolean("noFurniture"))}
|Состояние мебели: ${furniture.safeString("furnitureCondition")}
|Кухонный гарнитур: ${formatBoolean(furniture.safeBoolean("hasKitchenFurniture"))}
|Шкаф: ${formatBoolean(furniture.safeBoolean("hasWardrobe"))}
|Кровать: ${formatBoolean(furniture.safeBoolean("hasBed"))}
|Диван: ${formatBoolean(furniture.safeBoolean("hasSofa"))}
""".trimMargin()
        )
    }

    private fun renderAppliancesFeatures(json: JsonObject) {
        val appliances = json.safeObject("appliancesFeatures")

        addCard(
            title = "Техника и оснащение",
            value = """
|Холодильник: ${formatBoolean(appliances.safeBoolean("refrigerator"))}
|Стиральная машина: ${formatBoolean(appliances.safeBoolean("washingMachine"))}
|Посудомоечная машина: ${formatBoolean(appliances.safeBoolean("dishwasher"))}
|Кондиционер: ${formatBoolean(appliances.safeBoolean("airConditioner"))}
|Телевизор: ${formatBoolean(appliances.safeBoolean("tv"))}
|Микроволновка: ${formatBoolean(appliances.safeBoolean("microwave"))}
|Интернет: ${formatBoolean(appliances.safeBoolean("internet"))}
|
|Количество найденной техники: ${appliances.safeString("appliancesCount")}
|Уровень оснащения: ${appliances.safeString("appliancesLevel")}
""".trimMargin()
        )
    }

    private fun renderTenantRules(json: JsonObject) {
        val rules = json.safeObject("tenantRules")

        addCard(
            title = "Условия проживания",
            value = """
|Можно с животными: ${formatBoolean(rules.safeBoolean("petsAllowed"))}
|Животные запрещены: ${formatBoolean(rules.safeBoolean("petsForbidden"))}
|Можно с детьми: ${formatBoolean(rules.safeBoolean("childrenAllowed"))}
|Дети запрещены: ${formatBoolean(rules.safeBoolean("childrenForbidden"))}
|Курение запрещено: ${formatBoolean(rules.safeBoolean("smokingForbidden"))}
|Только долгосрочная аренда: ${formatBoolean(rules.safeBoolean("longTermOnly"))}
|Залог указан: ${formatBoolean(rules.safeBoolean("depositMentioned"))}
|Коммунальные платежи указаны: ${formatBoolean(rules.safeBoolean("utilitiesMentioned"))}
""".trimMargin()
        )
    }

    private fun renderPriceImpact(json: JsonObject) {
        val priceImpact = json.safeObject("priceImpact")
        val coefficient = priceImpact.safeDouble("coefficient", 1.0)

        addCard(
            title = "Влияние описания на цену",
            value = """
|Коэффициент: ${formatCoefficient(coefficient)}
|
|${priceImpact.safeString("description")}
""".trimMargin()
        )
    }

    private fun renderRecommendations(json: JsonObject) {
        addCard(
            title = "Нюансы и рекомендации",
            value = formatStringArray(json.safeArray("recommendations"))
        )
    }

    private fun addCard(title: String, value: String) {
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            background = resources.getDrawable(R.drawable.bg_analysis_card, null)
            setPadding(28, 24, 28, 24)
        }

        val titleView = TextView(requireContext()).apply {
            text = title.trim()
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.START
            includeFontPadding = false
            setTextColor(resources.getColor(R.color.black, null))
        }

        val valueView = TextView(requireContext()).apply {
            text = value.trim()
            textSize = 14f
            gravity = Gravity.START
            includeFontPadding = false
            setTextColor(resources.getColor(R.color.dark_gray, null))
            setPadding(0, 10, 0, 0)
        }

        container.addView(titleView)
        container.addView(valueView)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(0, 0, 0, 16)

        binding.llContent.addView(container, params)
    }

    private fun formatFloor(floor: JsonObject?): String {
        if (floor == null) return "-"

        val currentFloor = floor.safeString("floor")
        val totalFloors = floor.safeString("totalFloors")

        return if (totalFloors == "-") {
            currentFloor
        } else {
            "$currentFloor из $totalFloors"
        }
    }

    private fun formatStringArray(array: JsonArray?): String {
        if (array == null || array.size() == 0) {
            return "Рекомендаций нет"
        }

        return array
            .mapNotNull {
                if (it.isJsonNull) null else "• ${it.asString}"
            }
            .joinToString("\n")
            .ifBlank { "Рекомендаций нет" }
    }

    private fun formatBoolean(value: Boolean): String {
        return if (value) "да" else "нет"
    }

    private fun formatCoefficient(value: Double): String {
        return when {
            value > 1.0 -> "+${((value - 1.0) * 100).toInt()}%"
            value < 1.0 -> "-${((1.0 - value) * 100).toInt()}%"
            else -> "0%"
        }
    }

    private fun JsonObject?.safeString(key: String, default: String = "-"): String {
        val value = this?.get(key)

        return if (value == null || value.isJsonNull) {
            default
        } else {
            value.asString
        }
    }

    private fun JsonObject?.safeBoolean(key: String, default: Boolean = false): Boolean {
        val value = this?.get(key)

        return if (value == null || value.isJsonNull) {
            default
        } else {
            value.asBoolean
        }
    }

    private fun JsonObject?.safeDouble(key: String, default: Double = 0.0): Double {
        val value = this?.get(key)

        return if (value == null || value.isJsonNull) {
            default
        } else {
            value.asDouble
        }
    }

    private fun JsonObject?.safeObject(key: String): JsonObject? {
        val value = this?.get(key)

        return if (value == null || value.isJsonNull || !value.isJsonObject) {
            null
        } else {
            value.asJsonObject
        }
    }

    private fun JsonObject?.safeArray(key: String): JsonArray? {
        val value = this?.get(key)

        return if (value == null || value.isJsonNull || !value.isJsonArray) {
            null
        } else {
            value.asJsonArray
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}