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
fun AddDescriptionScreen(
    viewModel: AddApartmentComposeViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var description by remember { mutableStateOf(state.description) }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            Button(
                onClick = {
                    if (description.length < 30) {
                        Toast.makeText(context, "Описание слишком короткое", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    viewModel.updateDescription(description)

                    viewModel.analyzeText(
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
            Text("Описание жилья", style = MaterialTheme.typography.headlineSmall)

            Spacer(Modifier.height(8.dp))

            Text(
                "Опишите ремонт, мебель, технику, условия аренды и важные особенности квартиры.",
                color = Color.Gray
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Описание") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                maxLines = 8
            )

            if (state.isLoading) {
                Spacer(Modifier.height(24.dp))
                CircularProgressIndicator()
            }
        }
    }

    if (showDialog) {
        val text = state.textAnalysis

        AlertDialog(
            onDismissRequest = {},
            title = { Text("Анализ описания") },
            text = {
                Text(
                    """
                    Качество описания: ${text?.qualityLevel ?: "-"}
                    Ремонт: ${text?.repairFeatures?.repairQuality ?: "-"}
                    Мебель: ${text?.furnitureFeatures?.furnitureCondition ?: "-"}
                    Техника: ${text?.appliancesFeatures?.appliancesLevel ?: "-"}
                    
                    Рекомендации:
                    ${text?.recommendations?.joinToString("\n") { "• $it" } ?: "Нет рекомендаций"}
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
        )
    }
}