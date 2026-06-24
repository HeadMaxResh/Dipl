package com.example.dipl.presentation.fragment.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun ApartmentPriceSummaryScreen(
    viewModel: AddApartmentComposeViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (state.priceAnalysis == null) {
            viewModel.calculatePrice(
                onError = {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    var finalRentText by remember(state.finalRent) {
        mutableStateOf(
            if (state.finalRent > 0) state.finalRent.toString() else ""
        )
    }

    Scaffold(
        bottomBar = {
            Button(
                onClick = {
                    val rent = finalRentText.toIntOrNull()

                    if (rent == null || rent <= 0) {
                        Toast.makeText(context, "Введите корректную цену", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    viewModel.updateFinalRent(rent)

                    Toast.makeText(
                        context,
                        "Дальше здесь вызываешь сохранение квартиры",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                enabled = !state.isLoading && state.priceAnalysis != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text("Разместить квартиру")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Рекомендованная цена", style = MaterialTheme.typography.headlineSmall)

            Spacer(Modifier.height(16.dp))

            if (state.isLoading) {
                CircularProgressIndicator()
                Text("Выполняется итоговый расчет...")
            } else {
                val price = state.priceAnalysis?.price

                Text(
                    text = "${price?.recommendedPrice ?: "-"} ₽/мес",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Диапазон: ${price?.minPrice ?: "-"} – ${price?.maxPrice ?: "-"} ₽",
                    color = Color.Gray
                )

                Text(
                    text = "Рыночная база: ${price?.marketBasePrice ?: "-"} ₽",
                    color = Color.Gray
                )

                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    value = finalRentText,
                    onValueChange = { finalRentText = it },
                    label = { Text("Итоговая цена") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(20.dp))

                Text("Факторы стоимости:")

                price?.priceFactors?.forEach {
                    Text("• $it")
                }
            }
        }
    }
}