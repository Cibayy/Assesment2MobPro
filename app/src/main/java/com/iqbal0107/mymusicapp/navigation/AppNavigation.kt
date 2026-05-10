package com.iqbal0107.mymusicapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.iqbal0107.mymusicapp.ui.screen.*
import com.iqbal0107.mymusicapp.viewmodel.LaguViewModel

object Routes {
    const val LIST_LAGU = "list_lagu"
    const val TAMBAH_LAGU = "tambah_lagu"
    const val EDIT_LAGU = "edit_lagu/{laguId}"
    const val RECYCLE_BIN = "recycle_bin"
    const val PLAYLIST = "playlist"
    const val SETTINGS = "settings"
    fun editLagu(id: Int) = "edit_lagu/$id"
}

@Composable
fun AppNavigation(
    viewModel: LaguViewModel,
    isDarkMode: Boolean,
    themeColor: String,
    onToggleDarkMode: () -> Unit,
    onChangeThemeColor: (String) -> Unit
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.LIST_LAGU) {

        composable(Routes.LIST_LAGU) {
            ListLaguScreen(
                viewModel = viewModel,
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode,
                onNavigateToTambah = { navController.navigate(Routes.TAMBAH_LAGU) },
                onNavigateToEdit = { id -> navController.navigate(Routes.editLagu(id)) },
                onNavigateToRecycleBin = { navController.navigate(Routes.RECYCLE_BIN) },
                onNavigateToPlaylist = { navController.navigate(Routes.PLAYLIST) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(Routes.TAMBAH_LAGU) {
            TambahLaguScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(
            route = Routes.EDIT_LAGU,
            arguments = listOf(navArgument("laguId") { type = NavType.IntType })
        ) { backStackEntry ->
            val laguId = backStackEntry.arguments?.getInt("laguId") ?: 0
            EditLaguScreen(
                laguId = laguId,
                viewModel = viewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Routes.RECYCLE_BIN) {
            RecycleBinScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Routes.PLAYLIST) {
            PlaylistScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                isDarkMode = isDarkMode,
                themeColor = themeColor,
                onToggleDarkMode = onToggleDarkMode,
                onChangeThemeColor = onChangeThemeColor,
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}