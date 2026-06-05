package com.example.dipl.presentation.fragment

import android.os.Bundle
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
import com.example.dipl.domain.request.CalculateFromAnalysisRequest
import com.example.dipl.presentation.PrefManager
import com.example.dipl.presentation.toPhotoPart
import com.example.dipl.presentation.utils.toJsonObject
import com.example.dipl.presentation.utils.toJsonRequestBody
import com.example.dipl.presentation.utils.toTextBody
import com.example.dipl.presentation.viewmodel.AddApartmentSharedViewModel
import com.example.dipl.presentation.viewmodel.ApartmentInfoViewModel
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

        setupClickListeners()
        evaluateApartment()

        return binding.root
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

        val photoParts = sharedViewModel.photos.mapIndexed { index, bytes ->
            bytes.toPhotoPart(index)
        }

        setLoading(true)

        /*val request = CalculateFromAnalysisRequest(
            area = sharedViewModel.area,
            rooms = sharedViewModel.rooms,
            textAnalysis = sharedViewModel.textAnalysis!!,
            imageAnalysis = sharedViewModel.imageAnalysis!!,
            geoAnalysis = sharedViewModel.geoAnalysis!!
        )*/

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

        /*analysisApiService.calculateFromAnalysis(request)
            .enqueue(object : Callback<ResponseBody> {

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

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    setLoading(false)
                    showError("Ошибка подключения: ${t.message}")
                }
            })*/
    }

    private fun renderPriceResult(json: JsonObject) {
        val price = json.getAsJsonObject("price")

        val recommendedPrice = price
            ?.get("recommendedPrice")
            ?.asInt ?: 0

        val minPrice = price
            ?.get("minPrice")
            ?.asInt ?: 0

        val maxPrice = price
            ?.get("maxPrice")
            ?.asInt ?: 0

        val marketBasePrice = price
            ?.get("marketBasePrice")
            ?.asInt ?: 0

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
        val coefficients = price?.getAsJsonObject("coefficients")

        val textCoefficient = coefficients?.get("textCoefficient")?.asDouble ?: 1.0
        val imageCoefficient = coefficients?.get("imageCoefficient")?.asDouble ?: 1.0
        val geoCoefficient = coefficients?.get("geoCoefficient")?.asDouble ?: 1.0

        binding.tvCoefficients.text = """
            Коэффициенты:
            • Описание: ${formatCoefficient(textCoefficient)}
            • Фотографии: ${formatCoefficient(imageCoefficient)}
            • Локация: ${formatCoefficient(geoCoefficient)}
        """.trimIndent()
    }

    private fun renderPriceFactors(price: JsonObject?) {
        val factors = price
            ?.getAsJsonArray("priceFactors")
            ?.joinToString("\n") { "• ${it.asString}" }
            ?: "Факторы не определены"

        binding.tvPriceFactors.text = """
            Факторы стоимости:
            $factors
        """.trimIndent()
    }

    private fun renderShortSummary(json: JsonObject) {
        val analysis = json.getAsJsonObject("analysis")

        val imageSummary = analysis
            ?.getAsJsonObject("imageAnalysis")
            ?.getAsJsonObject("apartmentSummary")

        val geoAnalysis = analysis
            ?.getAsJsonObject("geoAnalysis")

        val textAnalysis = analysis
            ?.getAsJsonObject("textAnalysis")

        val visualLevel = imageSummary
            ?.get("visualQualityLevel")
            ?.asString ?: "-"

        val locationLevel = geoAnalysis
            ?.get("qualityLevel")
            ?.asString ?: "-"

        val textLevel = textAnalysis
            ?.get("qualityLevel")
            ?.asString ?: "-"

        binding.tvShortSummary.text = """
            Сводка анализа:
            • Визуальное состояние: $visualLevel
            • Локация: $locationLevel
            • Описание: $textLevel
        """.trimIndent()
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
        AlertDialog.Builder(requireContext())
            .setTitle("Разместить квартиру?")
            .setMessage(
                """
                Рекомендованная цена: ${formatPrice(sharedViewModel.recommendedPrice)} ₽
                Итоговая цена: ${formatPrice(finalRent)} ₽
                
                Объявление будет создано с указанной стоимостью.
                """.trimIndent()
            )
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
        binding.btnCreateApartment.isEnabled = !isLoading && binding.llResultContainer.visibility == View.VISIBLE

        binding.btnCreateApartment.text = if (isLoading) {
            "Рассчитываем..."
        } else {
            "Разместить квартиру"
        }
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

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}