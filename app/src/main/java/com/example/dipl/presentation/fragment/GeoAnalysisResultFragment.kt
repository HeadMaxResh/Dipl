package com.example.dipl.presentation.fragment

import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dipl.R
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.dipl.databinding.FragmentGeoAnalysisResultBinding
import com.example.dipl.presentation.viewmodel.AddApartmentSharedViewModel
import com.google.gson.JsonArray
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
            findNavController().popBackStack(
                R.id.addAddressFragment,
                false
            )
        }
    }

    private fun renderResult() {
        val json = sharedViewModel.geoAnalysis ?: run {
            addCard(
                title = "Нет данных",
                value = "Результат геоанализа отсутствует. Вернитесь к форме адреса и выполните анализ снова."
            )
            return
        }

        val address = json.get("address")?.asString ?: sharedViewModel.fullAddress()
        val qualityLevel = json.get("qualityLevel")?.asString ?: "-"
        val coordinates = json.getAsJsonObject("coordinates")
        val lat = coordinates?.get("lat")?.asString ?: "-"
        val lon = coordinates?.get("lon")?.asString ?: "-"

        addCard(
            title = "Адрес",
            value = """
                $address
                
                Координаты:
                широта: $lat
                долгота: $lon
            """.trimIndent()
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
        val scores = json.getAsJsonObject("scores")

        addCard(
            title = "Оценки по категориям",
            value = """
                Итоговая оценка: ${scores?.get("locationScore")?.asString ?: "-"}
                Транспорт: ${scores?.get("transportScore")?.asString ?: "-"}
                Образование: ${scores?.get("educationScore")?.asString ?: "-"}
                Медицина: ${scores?.get("healthcareScore")?.asString ?: "-"}
                Магазины и сервисы: ${scores?.get("commercialScore")?.asString ?: "-"}
                Комфорт: ${scores?.get("comfortScore")?.asString ?: "-"}
                Трафик: ${scores?.get("trafficScore")?.asString ?: "-"}
            """.trimIndent()
        )
    }

    private fun renderNearestInfrastructure(json: JsonObject) {
        val nearest = json.getAsJsonObject("nearestInfrastructure")

        addCard(
            title = "Ближайший транспорт",
            value = """
                Автобус / маршрутка:
                ${formatObjects(nearest?.getAsJsonArray("busStops"))}
                
                Трамвай:
                ${formatObjects(nearest?.getAsJsonArray("tramStops"))}
                
                Метро:
                ${formatObjects(nearest?.getAsJsonArray("metroStations"))}
            """.trimIndent()
        )

        addCard(
            title = "Социальная инфраструктура",
            value = """
                Школы:
                ${formatObjects(nearest?.getAsJsonArray("schools"))}
                
                Детские сады:
                ${formatObjects(nearest?.getAsJsonArray("kindergartens"))}
                
                Больницы:
                ${formatObjects(nearest?.getAsJsonArray("hospitals"))}
                
                Поликлиники / клиники:
                ${formatObjects(nearest?.getAsJsonArray("clinics"))}
            """.trimIndent()
        )

        addCard(
            title = "Магазины, парки и парковки",
            value = """
                Магазины:
                ${formatObjects(nearest?.getAsJsonArray("shops"))}
                
                Аптеки:
                ${formatObjects(nearest?.getAsJsonArray("pharmacies"))}
                
                Парки:
                ${formatObjects(nearest?.getAsJsonArray("parks"))}
                
                Парковки:
                ${formatObjects(nearest?.getAsJsonArray("parking"))}
            """.trimIndent()
        )
    }

    private fun renderMarketAnalysis(json: JsonObject) {
        val market = json.getAsJsonObject("marketAnalysis") ?: return

        val source = market.get("marketSource")?.asString ?: "-"
        val basePrice = market.get("marketBasePrice")?.asInt ?: 0
        val avgM2 = market.get("averagePricePerSquareMeter")?.asString ?: "-"
        val medianM2 = market.get("medianPricePerSquareMeter")?.asString ?: "-"
        val usedAds = market.get("usedAdsCount")?.asString ?: "0"
        val cityUsedAds = market.get("usedCityAdsCount")?.asString ?: "0"
        val recommendation = market.get("recommendation")?.asString ?: ""

        addCard(
            title = "Рыночный анализ",
            value = """
                Источник расчета: ${formatMarketSource(source)}
                Базовая рыночная цена: ${formatPrice(basePrice)} ₽
                
                Средняя цена за м²: $avgM2 ₽
                Медианная цена за м²: $medianM2 ₽
                
                Использовано объявлений рядом: $usedAds
                Использовано объявлений по городу: $cityUsedAds
                
                $recommendation
            """.trimIndent()
        )
    }

    private fun renderPriceImpact(json: JsonObject) {
        val priceImpact = json.getAsJsonObject("priceImpact")

        val coefficient = priceImpact?.get("coefficient")?.asDouble ?: 1.0
        val description = priceImpact?.get("description")?.asString ?: "-"

        addCard(
            title = "Влияние локации на цену",
            value = """
                Коэффициент: ${formatCoefficient(coefficient)}
                
                $description
            """.trimIndent()
        )
    }

    private fun renderRecommendations(json: JsonObject) {
        val recommendations = json.getAsJsonArray("recommendations")

        addCard(
            title = "Рекомендации",
            value = formatStringArray(recommendations)
        )
    }

    private fun formatObjects(array: JsonArray?): String {
        if (array == null || array.size() == 0) {
            return "не найдено"
        }

        return array
            .take(5)
            .joinToString("\n") { element ->
                val obj = element.asJsonObject
                val name = obj.get("name")?.asString ?: "Без названия"
                val distance = obj.get("distanceMeters")?.asString ?: "-"
                "• $name — $distance м"
            }
    }

    private fun formatStringArray(array: JsonArray?): String {
        if (array == null || array.size() == 0) {
            return "Рекомендаций нет"
        }

        return array.joinToString("\n") {
            "• ${it.asString}"
        }
    }

    private fun addCard(title: String, value: String) {
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            background = resources.getDrawable(R.drawable.bg_analysis_card, null)
            setPadding(28, 24, 28, 24)
        }

        val titleView = TextView(requireContext()).apply {
            text = title
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(resources.getColor(R.color.black, null))
        }

        val valueView = TextView(requireContext()).apply {
            text = value
            textSize = 14f
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}