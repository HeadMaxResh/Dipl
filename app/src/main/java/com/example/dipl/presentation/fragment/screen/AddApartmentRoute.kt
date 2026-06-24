package com.example.dipl.presentation.fragment.screen

sealed class AddApartmentRoute(val route: String) {
    data object Photos : AddApartmentRoute("add_photos")
    data object Address : AddApartmentRoute("add_address")
    data object Description : AddApartmentRoute("add_description")
    data object Summary : AddApartmentRoute("apartment_price_summary")
}