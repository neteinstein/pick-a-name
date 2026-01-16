package org.neteinstein.pickaname.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.neteinstein.pickaname.presentation.namedetails.NameDetailsScreen
import org.neteinstein.pickaname.presentation.namelist.NameListScreen

/**
 * Navigation routes
 */
sealed class Screen(val route: String) {
    object NameList : Screen("name_list")
    object NameDetails : Screen("name_details/{nameId}") {
        fun createRoute(nameId: Long) = "name_details/$nameId"
    }
}

/**
 * Main navigation graph
 */
@Composable
fun PickANameNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.NameList.route
    ) {
        composable(Screen.NameList.route) {
            NameListScreen(
                onNameClick = { nameId ->
                    navController.navigate(Screen.NameDetails.createRoute(nameId))
                }
            )
        }
        
        composable(
            route = Screen.NameDetails.route,
            arguments = listOf(
                navArgument("nameId") {
                    type = NavType.LongType
                }
            )
        ) {
            NameDetailsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
