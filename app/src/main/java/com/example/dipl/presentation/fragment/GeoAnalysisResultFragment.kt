package com.example.dipl.presentation.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.dipl.R
import com.example.dipl.databinding.FragmentGeoAnalysisResultBinding
import com.example.dipl.presentation.viewmodel.AddApartmentSharedViewModel
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject

class GeoAnalysisResultFragment : Fragment() {

    private var _binding: FragmentGeoAnalysisResultBinding? = null
    private val binding: FragmentGeoAnalysisResultBinding
        get() = _binding ?: throw RuntimeException("FragmentGeoAnalysisResultBinding == null")

    private val sharedViewModel: AddApartmentSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGeoAnalysisResultBinding.inflate(inflater, container, false)

        renderResult()
        setupClickListeners()

        return binding.root
    }

    private fun setupClickListeners() {
        binding.btnNext.setOnClickListener {
            findNavController().navigate(
                R.id.action_geoAnalysisResultFragment_to_addDescriptionFragment
            )
        }

        binding.btnBackToForm.setOnClickListener {
            findNavController().popBackStack(R.id.addAddressFragment, false)
        }
    }

    private fun renderResult() {
        binding.llContent.removeAllViews()

        val json = sharedViewModel.geoAnalysis

        if (json == null) {
            addCard(
                title = "Нет данных",
                value = "Результат геоанализа отсутствует. Вернитесь к форме адреса и выполните анализ снова."
            )
            return
        }

        val address = json.safeString("address", sharedViewModel.fullAddress())
        val qualityLevel = json.safeString("qualityLevel")
        val coordinates = json.safeObject("coordinates")
        val lat = coordinates.safeString("lat")
        val lon = coordinates.safeString("lon")

        addCard(
            title = "Адрес",
            value = """
|$address
|
|Координаты:
|широта: $lat
|долгота: $lon
""".trimMargin()
        )

        addCard(
            title = "Итоговая оценка локации",
            value = qualityLevel
        )

        renderScores(json)
        renderNearestInfrastructure(json)
        renderMarketAnalysis(json)
        renderPriceImpact(json)
        renderRecommendations(json)
    }

    private fun renderScores(json: JsonObject) {
        val scores = json.safeObject("scores")

        addCard(
            title = "Оценки по категориям",
            value = """
|Итоговая оценка: ${scores.safeString("locationScore")}
|Транспорт: ${scores.safeString("transportScore")}
|Образование: ${scores.safeString("educationScore")}
|Медицина: ${scores.safeString("healthcareScore")}
|Магазины и сервисы: ${scores.safeString("commercialScore")}
|Комфорт: ${scores.safeString("comfortScore")}
|Трафик: ${scores.safeString("trafficScore")}
""".trimMargin()
        )
    }

    private fun renderNearestInfrastructure(json: JsonObject) {
        val nearest = json.safeObject("nearestInfrastructure")

        addCard(
            title = "Ближайший транспорт",
            value = """
|Автобус / маршрутка:
|${formatObjects(nearest.safeArray("busStops"))}
|
|Трамвай:
|${formatObjects(nearest.safeArray("tramStops"))}
|
|Метро:
|${formatObjects(nearest.safeArray("metroStations"))}
""".trimMargin()
        )

        addCard(
            title = "Социальная инфраструктура",
            value = """
|Школы:
|${formatObjects(nearest.safeArray("schools"))}
|
|Детские сады:
|${formatObjects(nearest.safeArray("kindergartens"))}
|
|Больницы:
|${formatObjects(nearest.safeArray("hospitals"))}
|
|Поликлиники / клиники:
|${formatObjects(nearest.safeArray("clinics"))}
""".trimMargin()
        )

        addCard(
            title = "Магазины, парки и парковки",
            value = """
|Магазины:
|${formatObjects(nearest.safeArray("shops"))}
|
|Аптеки:
|${formatObjects(nearest.safeArray("pharmacies"))}
|
|Парки:
|${formatObjects(nearest.safeArray("parks"))}
|
|Парковки:
|${formatObjects(nearest.safeArray("parking"))}
""".trimMargin()
        )
    }

    private fun renderMarketAnalysis(json: JsonObject) {
        val market = json.safeObject("marketAnalysis") ?: return

        val source = market.safeString("marketSource")
        val basePrice = market.safeInt("marketBasePrice")
        val avgM2 = market.safeString("averagePricePerSquareMeter")
        val medianM2 = market.safeString("medianPricePerSquareMeter")
        val usedAds = market.safeString("usedAdsCount", "0")
        val cityUsedAds = market.safeString("usedCityAdsCount", "0")
        val recommendation = market.safeString("recommendation", "")

        addCard(
            title = "Рыночный анализ",
            value = """
|Источник расчета: ${formatMarketSource(source)}
|Базовая рыночная цена: ${formatPrice(basePrice)} ₽
|
|Средняя цена за м²: $avgM2 ₽
|Медианная цена за м²: $medianM2 ₽
|
|Использовано объявлений рядом: $usedAds
|Использовано объявлений по городу: $cityUsedAds
|
|$recommendation
""".trimMargin()
        )
    }

    private fun renderPriceImpact(json: JsonObject) {
        val priceImpact = json.safeObject("priceImpact")

        val coefficient = priceImpact.safeDouble("coefficient", 1.0)
        val description = priceImpact.safeString("description")

        addCard(
            title = "Влияние локации на цену",
            value = """
|Коэффициент: ${formatCoefficient(coefficient)}
|
|$description
""".trimMargin()
        )
    }

    private fun renderRecommendations(json: JsonObject) {
        addCard(
            title = "Рекомендации",
            value = formatStringArray(json.safeArray("recommendations"))
        )
    }

    private fun formatObjects(array: JsonArray?): String {
        if (array == null || array.size() == 0) {
            return "не найдено"
        }

        return array
            .take(5)
            .mapNotNull { element ->
                val obj = element.asSafeObject() ?: return@mapNotNull null
                val name = obj.safeString("name", "Без названия")
                val distance = obj.safeString("distanceMeters", "-")
                "• $name — $distance м"
            }
            .joinToString("\n")
            .ifBlank { "не найдено" }
    }

    private fun formatStringArray(array: JsonArray?): String {
        if (array == null || array.size() == 0) {
            return "Рекомендаций нет"
        }

        return array
            .mapNotNull { element ->
                if (element.isJsonNull) null else "• ${element.asString}"
            }
            .joinToString("\n")
            .ifBlank { "Рекомендаций нет" }
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

    private fun formatPrice(value: Int): String {
        return "%,d".format(value).replace(",", " ")
    }

    private fun formatCoefficient(value: Double): String {
        return when {
            value > 1.0 -> "+${((value - 1.0) * 100).toInt()}%"
            value < 1.0 -> "-${((1.0 - value) * 100).toInt()}%"
            else -> "0%"
        }
    }

    private fun formatMarketSource(source: String): String {
        return when (source) {
            "nearby" -> "объявления рядом"
            "city" -> "среднее по городу"
            "fallback" -> "резервная модель"
            else -> source
        }
    }

    private fun JsonObject?.safeString(
        key: String,
        default: String = "-"
    ): String {
        val value = this?.get(key)
        return if (value == null || value.isJsonNull) {
            default
        } else {
            value.asString
        }
    }

    private fun JsonObject?.safeInt(
        key: String,
        default: Int = 0
    ): Int {
        val value = this?.get(key)
        return if (value == null || value.isJsonNull) {
            default
        } else {
            value.asInt
        }
    }

    private fun JsonObject?.safeDouble(
        key: String,
        default: Double = 0.0
    ): Double {
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

    private fun JsonElement?.asSafeObject(): JsonObject? {
        return if (this == null || this.isJsonNull || !this.isJsonObject) {
            null
        } else {
            this.asJsonObject
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}