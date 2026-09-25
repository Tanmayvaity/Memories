package com.example.memories.navigation

import androidx.annotation.DrawableRes
import androidx.navigation.NavController
import com.example.memories.R

val TOP_LEVEL_DESTINATIONS = listOf(
    TopLevelDestination(
        name = TopLevelScreen.Feed.route,
        route = TopLevelScreen.Feed,
        resource = R.drawable.ic_feed
    ),
    TopLevelDestination(
        name = TopLevelScreen.Search.route,
        route = TopLevelScreen.Search,
        resource = R.drawable.ic_search
    ),
    TopLevelDestination(
        name = TopLevelScreen.Other.route,
        route = TopLevelScreen.Other,
        resource = R.drawable.ic_other
    ),

)

class TopLevelNavigation(private val navController: NavController) {
//    navController.graph.findStartDestination().id
    fun navigateTo(destination: TopLevelDestination) {
        navController.navigate(destination.route){
            popUpTo(TopLevelScreen.Feed){
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}

data class TopLevelDestination(
    val name: String,
    val route: TopLevelScreen,
    @DrawableRes
    val resource: Int
)



