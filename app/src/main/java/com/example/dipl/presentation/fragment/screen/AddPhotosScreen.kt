package com.example.dipl.presentation.fragment.screen

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun AddPhotosScreen(
    viewModel: AddApartmentComposeViewModel,
    onNext: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        viewModel.addPhotos(uris)
    }

    Scaffold(
        bottomBar = {
            Button(
                onClick = {
                    if (state.photoUris.isEmpty()) {
                        Toast.makeText(context, "Добавьте фотографии", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    viewModel.analyzePhotos(
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
            Text(
                text = "Фотографии квартиры",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Добавьте фотографии жилья. Система оценит ремонт, мебель, комнаты и визуальное качество квартиры.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Text("Добавить фотографии")
            }

            Spacer(Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.photoUris) { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Фото квартиры",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
            }

            if (state.isLoading) {
                Spacer(Modifier.height(24.dp))
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }

    if (showDialog) {
        val summary = state.imageAnalysis?.apartmentSummary

        AlertDialog(
            onDismissRequest = {},
            title = { Text("Анализ фотографий") },
            text = {
                Text(
                    """
                    Визуальная оценка: ${summary?.averageVisualScore ?: "-"}
                    Уровень: ${summary?.visualQualityLevel ?: "-"}
                    Ремонт: ${summary?.dominantRepairQuality ?: "-"}
                    Мебель: ${summary?.dominantFurnitureLevel ?: "-"}
                    
                    Рекомендации:
                    ${summary?.recommendations?.joinToString("\n") { "• $it" } ?: "Нет рекомендаций"}
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