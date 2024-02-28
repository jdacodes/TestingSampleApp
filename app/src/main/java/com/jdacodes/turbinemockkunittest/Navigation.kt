package com.jdacodes.turbinemockkunittest

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.jdacodes.turbinemockkunittest.authlogin.AuthLoginScreen


fun NavGraphBuilder.authGraph(
  route: String,
  navController: NavController
){
    navigation(
        startDestination = Route.AuthLogin.link,
        route = route
    ){
        login(navController)
        profile(navController)
    }
}
fun NavGraphBuilder.login(navController: NavController) {
    composable( route = Route.AuthLogin.link ) {
        AuthLoginScreen(navController)
    }
}

fun NavGraphBuilder.profile(navController: NavController) {
    composable(
        route = Route.AuthProfile.link,
    ) {
        AuthProfileScreen(navController)
    }
}