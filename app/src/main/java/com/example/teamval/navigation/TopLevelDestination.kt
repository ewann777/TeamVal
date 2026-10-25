package com.example.teamval.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.teamval.R

enum class TopLevelDestination(
    val route: String,
    @param:StringRes @get:StringRes val labelRes: Int,
    @param:DrawableRes @get:DrawableRes val iconRes: Int
) {
    Home("home", R.string.nav_home, R.drawable.ic_home),
    Stacks("stacks", R.string.nav_stacks, R.drawable.ic_team),
    Scrims("scrims", R.string.nav_scrims, R.drawable.ic_challenge),
    Store("store", R.string.nav_store, R.drawable.ic_store),
    Profile("profile", R.string.nav_profile, R.drawable.ic_profile)
}

fun NavHostController.navigateToTopLevel(destination: TopLevelDestination) {
    navigate(destination.route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
