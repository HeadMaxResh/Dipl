package com.example.dipl.presentation.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.dipl.R
import com.example.dipl.data.api.Api.analysisApiService
import com.example.dipl.databinding.FragmentApartmentPriceSummaryBinding
import com.example.dipl.domain.dto.ApartmentInfoDto
import com.example.dipl.domain.model.User
import com.example.dipl.presentation.PrefManager
import com.example.dipl.presentation.utils.toJsonObject
import com.example.dipl.presentation.utils.toJsonRequestBody
import com.example.dipl.presentation.viewmodel.AddApartmentSharedViewModel
import com.example.dipl.presentation.viewmodel.ApartmentInfoViewModel
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class ApartmentPriceSummaryFragment : Fragment() {

    private var _binding: FragmentApartmentPriceSummaryBinding? = null
    private val binding: FragmentApartmentPriceSummaryBinding
        get() = _binding ?: throw RuntimeException("FragmentApartmentPriceSummaryBinding == null")

    private val sharedViewModel: AddApartmentSharedViewModel by activityViewModels()
    private val apartmentInfoViewModel: ApartmentInfoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApartmentPriceSummaryBinding.inflate(inflater, container, false)

        prepareTextViews()
        setupClickListeners()
        evaluateApartment()

        return binding.root
    }

    private fun prepareTextViews() {
        listOf(
            binding.tvCoefficients,
            binding.tvPriceFactors,
            binding.tvShortSummary
        ).forEach { textView ->
            textView.gravity = Gravity.START
            textView.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
            textView.includeFontPadding = false
            textView.setLineSpacing(0f, 1.15f)
        }
    }

    private fun setupClickListeners() {
        binding.btnCreateApartment.setOnClickListener {
            createApartment()
        }
    }

    private fun evaluateApartment() {
        if (sharedViewModel.photos.isEmpty()) {
            showError("Отсутствуют фотографии")
            return
        }

        if (sharedViewModel.description.isBlank()) {
            showError("Отсутствует описание")
            return
        }

        if (sharedViewModel.fullAddress().isBlank()) {
            showError("Отсутствует адрес")
            return
        }

        if (
            sharedViewModel.textAnalysis == null ||
            sharedViewModel.imageAnalysis == null ||
            sharedViewModel.geoAnalysis == null
        ) {
            showError("Не все результаты анализа получены")
            return
        }

        setLoading(true)

        val requestJson = JsonObject().apply {
            addProperty("area", sharedViewModel.area)
            addProperty("rooms", sharedViewModel.rooms)
            add("textAnalysis", sharedViewModel.textAnalysis)
            add("imageAnalysis", sharedViewModel.imageAnalysis)
            add("geoAnalysis", sharedViewModel.geoAnalysis)
        }

        analysisApiService.calculateFromAnalysis(
            requestJson.toJsonRequestBody()
        ).enqueue(object : Callback<ResponseBody> {

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                setLoading(false)

                val body = response.body()

                if (response.isSuccessful && body != null) {
                    val json = body.toJsonObject()
                    sharedViewModel.priceAnalysis = json
                    renderPriceResult(json)
                } else {
                    showError("Не удалось рассчитать стоимость")
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

    private fun renderPriceResult(json: JsonObject) {
        val price = json.safeObject("price")

        val recommendedPrice = price.safeInt("recommendedPrice")
        val minPrice = price.safeInt("minPrice")
        val maxPrice = price.safeInt("maxPrice")
        val marketBasePrice = price.safeInt("marketBasePrice")

        sharedViewModel.recommendedPrice = recommendedPrice
        sharedViewModel.finalRent = recommendedPrice

        binding.tvRecommendedPrice.text = "${formatPrice(recommendedPrice)} ₽/мес"
        binding.tvPriceRange.text = "Диапазон: ${formatPrice(minPrice)} – ${formatPrice(maxPrice)} ₽"
        binding.tvMarketBasePrice.text = "Базовая рыночная цена: ${formatPrice(marketBasePrice)} ₽"
        binding.etFinalRent.setText(recommendedPrice.toString())

        renderCoefficients(price)
        renderPriceFactors(price)
        renderShortSummary(json)

        binding.llResultContainer.visibility = View.VISIBLE
        binding.btnCreateApartment.isEnabled = true
    }

    private fun renderCoefficients(price: JsonObject?) {
        val coefficients = price.safeObject("coefficients")

        val textCoefficient = coefficients.safeDouble("textCoefficient", 1.0)
        val imageCoefficient = coefficients.safeDouble("imageCoefficient", 1.0)
        val geoCoefficient = coefficients.safeDouble("geoCoefficient", 1.0)

        binding.tvCoefficients.text = buildLines(
            listOf(
                "Коэффициенты:",
                "• Описание: ${formatCoefficient(textCoefficient)}",
                "• Фотографии: ${formatCoefficient(imageCoefficient)}",
                "• Локация: ${formatCoefficient(geoCoefficient)}"
            )
        )
    }

    private fun renderPriceFactors(price: JsonObject?) {
        val factors = formatStringArray(
            price.safeArray("priceFactors"),
            "Факторы не определены"
        )

        binding.tvPriceFactors.text = buildLines(
            listOf("Факторы стоимости:") + factors
        )
    }

    private fun renderShortSummary(json: JsonObject) {
        val analysis = json.safeObject("analysis")

        val imageSummary = analysis
            .safeObject("imageAnalysis")
            .safeObject("apartmentSummary")

        val geoAnalysis = analysis.safeObject("geoAnalysis")
        val textAnalysis = analysis.safeObject("textAnalysis")

        val visualLevel = imageSummary.safeString("visualQualityLevel")
        val locationLevel = geoAnalysis.safeString("qualityLevel")
        val textLevel = textAnalysis.safeString("qualityLevel")

        binding.tvShortSummary.text = buildLines(
            listOf(
                "Сводка анализа:",
                "• Визуальное состояние: $visualLevel",
                "• Локация: $locationLevel",
                "• Описание: $textLevel"
            )
        )
    }

    private fun createApartment() {
        val finalRent = binding.etFinalRent.text
            ?.toString()
            ?.trim()
            ?.toIntOrNull()

        if (finalRent == null || finalRent <= 0) {
            showError("Введите корректную итоговую цену")
            return
        }

        val user = PrefManager.getUser(requireContext())

        if (user == null) {
            showError("Пользователь не найден")
            return
        }

        sharedViewModel.finalRent = finalRent
        showCreateConfirmDialog(user, finalRent)
    }

    private fun showCreateConfirmDialog(user: User, finalRent: Int) {
        val message = buildLines(
            listOf(
                "Рекомендованная цена: ${formatPrice(sharedViewModel.recommendedPrice)} ₽",
                "Итоговая цена: ${formatPrice(finalRent)} ₽",
                "",
                "Объявление будет создано с указанной стоимостью."
            )
        )

        AlertDialog.Builder(requireContext())
            .setTitle("Разместить квартиру?")
            .setMessage(message)
            .setPositiveButton("Разместить") { _, _ ->
                saveApartment(user, finalRent)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun saveApartment(user: User, finalRent: Int) {
        val imagePaths = sharedViewModel.photos.map {
            saveByteArrayToInternalStorage(it)
        }

        val apartmentInfoDto = ApartmentInfoDto(
            sharedViewModel.address,
            sharedViewModel.city,
            finalRent,
            0.0,
            sharedViewModel.area,
            imagePaths,
            sharedViewModel.rooms,
            user,
            sharedViewModel.description,
            sharedViewModel.cadastr
        )

        apartmentInfoViewModel.addApartment(apartmentInfoDto)
        sharedViewModel.clear()

        Toast.makeText(
            requireContext(),
            "Квартира успешно добавлена",
            Toast.LENGTH_SHORT
        ).show()

        findNavController().navigate(
            R.id.action_apartmentPriceSummaryFragment_to_homeFragment
        )
    }

    private fun saveByteArrayToInternalStorage(byteArray: ByteArray): String {
        val context = requireContext()
        val imageHash = byteArray.contentHashCode()
        val fileName = "$imageHash.jpg"
        val file = File(context.filesDir, fileName)

        if (!file.exists()) {
            FileOutputStream(file).use {
                it.write(byteArray)
            }
        }

        return file.absolutePath
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.tvStatus.visibility = if (isLoading) View.VISIBLE else View.GONE

        binding.btnCreateApartment.isEnabled =
            !isLoading && binding.llResultContainer.visibility == View.VISIBLE

        binding.btnCreateApartment.text = if (isLoading) {
            "Рассчитываем..."
        } else {
            "Разместить квартиру"
        }
    }

    private fun formatStringArray(
        array: JsonArray?,
        emptyText: String
    ): List<String> {
        if (array == null || array.size() == 0) {
            return listOf(emptyText)
        }

        val rows = array
            .mapNotNull {
                if (it.isJsonNull) null else "• ${it.asString}"
            }
            .filter { it.isNotBlank() }

        return rows.ifEmpty {
            listOf(emptyText)
        }
    }

    private fun buildLines(lines: List<String>): String {
        return lines.joinToString("\n") { it.trim() }.trim()
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

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}