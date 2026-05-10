package com.iqbal0107.mymusicapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.iqbal0107.mymusicapp.ui.screen.EditLaguScreen
import com.iqbal0107.mymusicapp.ui.screen.ListLaguScreen
import com.iqbal0107.mymusicapp.ui.screen.TambahLaguScreen
import com.iqbal0107.mymusicapp.viewmodel.LaguViewModel

// Konstanta nama route
object Routes {
    const val LIST_LAGU = "list_lagu"
    const val TAMBAH_LAGU = "tambah_lagu"
    const val EDIT_LAGU = "edit_lagu/{laguId}"
    fun editLagu(id: Int) = "edit_lagu/$id"
}

@Composable
fun AppNavigation(viewModel: LaguViewModel, isDarkMode: Boolean, onToggleDarkMode: () -> Unit) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.LIST_LAGU) {

        // Screen 1: List Lagu
        composable(Routes.LIST_LAGU) {
            ListLaguScreen(
                viewModel = viewModel,
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode,
                onNavigateToTambah = { navController.navigate(Routes.TAMBAH_LAGU) },
                onNavigateToEdit = { id -> navController.navigate(Routes.editLagu(id)) }
            )
        }

        // Screen 2: Tambah Lagu
        composable(Routes.TAMBAH_LAGU) {
            TambahLaguScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        // Screen 3: Edit Lagu
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
    }
}