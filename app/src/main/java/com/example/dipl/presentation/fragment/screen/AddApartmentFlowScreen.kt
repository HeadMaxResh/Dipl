package com.example.dipl.presentation.fragment.screen

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AddApartmentFlowScreen(
    viewModel: AddApartmentComposeViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AddApartmentRoute.Photos.route
    ) {
        composable(AddApartmentRoute.Photos.route) {
            AddPhotosScreen(
                viewModel = viewModel,
                onNext = {
                    navController.navigate(AddApartmentRoute.Address.route)
                }
            )
        }

        composable(AddApartmentRoute.Address.route) {
            AddAddressScreen(
                viewModel = viewModel,
                onNext = {
                    navController.navigate(AddApartmentRoute.Description.route)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(AddApartmentRoute.Description.route) {
            AddDescriptionScreen(
                viewModel = viewModel,
                onNext = {
                    navController.navigate(AddApartmentRoute.Summary.route)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(AddApartmentRoute.Summary.route) {
            ApartmentPriceSummaryScreen(
                viewModel = viewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}