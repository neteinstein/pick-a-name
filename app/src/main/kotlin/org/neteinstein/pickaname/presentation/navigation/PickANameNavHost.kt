package org.neteinstein.pickaname.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.neteinstein.pickaname.presentation.namelist.NameListScreen
import org.neteinstein.pickaname.presentation.settings.SettingsScreen
import org.neteinstein.pickaname.presentation.splash.SplashScreen
import org.neteinstein.pickaname.presentation.sync.SyncOrigin
import org.neteinstein.pickaname.presentation.sync.SyncScreen

/**
 * App-wide nav graph: Splash decides between Sync (empty DB) and the name list; Settings can
 * trigger a fresh Sync when the source URL changes; a successful Sync always lands on the name
 * list with the whole back stack cleared, so the user never lands back on Splash/Sync via "back".
 */
@Composable
fun PickANameNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToSync = {
                    navController.navigate(Routes.sync(SyncOrigin.ONBOARDING)) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToNameList = {
                    navController.navigate(Routes.NAME_LIST) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Routes.SYNC_PATTERN,
            arguments = listOf(navArgument("origin") { type = NavType.StringType })
        ) {
            SyncScreen(
                onSyncSuccess = {
                    navController.navigate(Routes.NAME_LIST) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                onEditSource = {
                    navController.navigate(Routes.SETTINGS)
                }
            )
        }
        composable(Routes.NAME_LIST) {
            NameListScreen(
                onOpenSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onSourceUpdated = {
                    navController.navigate(Routes.sync(SyncOrigin.SETTINGS))
                }
            )
        }
    }
}
