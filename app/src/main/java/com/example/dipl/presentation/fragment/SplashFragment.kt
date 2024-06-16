package com.example.dipl.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.dipl.R
import com.example.dipl.presentation.PrefManager


class SplashFragment : Fragment(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.postDelayed({
            val loggedIn = PrefManager.getLoggedInState(requireContext())

            val action = if (!loggedIn) {
                R.id.action_splashFragment_to_loginFragment
            } else {
                R.id.action_splashFragment_to_mainFragment
            }
            findNavController().navigate(
                action,
                bundleOf(MainFragment.key to "NAME"),
                navOptions {
                    anim {
                        enter = androidx.navigation.ui.R.anim.nav_default_enter_anim
                        popEnter = androidx.navigation.ui.R.anim.nav_default_pop_enter_anim
                        popExit = androidx.navigation.ui.R.anim.nav_default_pop_exit_anim
                        exit = androidx.navigation.ui.R.anim.nav_default_exit_anim
                    }
                    launchSingleTop = true
                    popUpTo(R.id.application_nav_graph) {
                        inclusive = true
                    }
                }
            )
        }, 1000)
    }
}