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
fun AddAddressScreen(
    viewModel: AddApartmentComposeViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var city by remember { mutableStateOf(state.city.ifBlank { "Саратов" }) }
    var address by remember { mutableStateOf(state.address) }
    var apartmentNumber by remember { mutableStateOf(state.apartmentNumber) }
    var rooms by remember { mutableStateOf(state.rooms.toString()) }
    var area by remember { mutableStateOf(if (state.area > 0) state.area.toString() else "") }
    var cadastr by remember { mutableStateOf(state.cadastr) }

    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            Button(
                onClick = {
                    val roomsInt = rooms.toIntOrNull()
                    val areaFloat = area.replace(",", ".").toFloatOrNull()

                    if (city.isBlank() || address.isBlank()) {
                        Toast.makeText(context, "Введите город и адрес", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (roomsInt == null || areaFloat == null) {
                        Toast.makeText(context, "Проверьте комнаты и площадь", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    viewModel.updateAddress(
                        city = city,
                        address = address,
                        apartmentNumber = apartmentNumber,
                        rooms = roomsInt,
                        area = areaFloat,
                        cadastr = cadastr
                    )

                    viewModel.analyzeLocation(
                        onSuccess = { showDialog = true },
                        onError = {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text(if (state.isLoading) "Анализируем..." else "Далее")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Адрес квартиры", style = MaterialTheme.typography.headlineSmall)

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("Город") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Адрес дома") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = apartmentNumber,
                onValueChange = { apartmentNumber = it },
                label = { Text("Номер квартиры") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = rooms,
                onValueChange = { rooms = it },
                label = { Text("Количество комнат") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = area,
                onValueChange = { area = it },
                label = { Text("Площадь") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = cadastr,
                onValueChange = { cadastr = it },
                label = { Text("Кадастровый номер") },
                modifier = Modifier.fillMaxWidth()
            )

            if (state.isLoading) {
                Spacer(Modifier.height(24.dp))
                CircularProgressIndicator()
            }
        }
    }

    if (showDialog) {
        val geo = state.geoAnalysis

        /*AlertDialog(
            onDismissRequest = {},
            title = { Text("Геоанализ") },
            text = {
                Text(
                    """
                    Локация: ${geo?.qualityLevel ?: "-"}
                    Оценка: ${geo?.scores?.locationScore ?: "-"}
                    Транспорт: ${geo?.scores?.transportScore ?: "-"}
                    Инфраструктура: ${geo?.scores?.commercialScore ?: "-"}
                    
                    Влияние на цену:
                    ${geo?.priceImpact?.description ?: "-"}
                    """.trimIndent()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        onNext()
                    }
                ) {
                    Text("Продолжить")
                }
            }
        )*/
    }
}