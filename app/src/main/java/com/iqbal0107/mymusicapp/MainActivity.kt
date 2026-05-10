package com.iqbal0107.mymusicapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iqbal0107.mymusicapp.datastore.PreferencesManager
import com.iqbal0107.mymusicapp.navigation.AppNavigation
import com.iqbal0107.mymusicapp.ui.screen.themeColors
import com.iqbal0107.mymusicapp.ui.theme.MyMusicAppTheme
import com.iqbal0107.mymusicapp.viewmodel.LaguViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: LaguViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferencesManager = PreferencesManager(this)

        setContent {
            val isDarkMode by preferencesManager.isDarkMode.collectAsStateWithLifecycle(false)
            val themeColor by preferencesManager.themeColor.collectAsStateWithLifecycle("Purple")
            val scope = rememberCoroutineScope()
            val seedColor = themeColors[themeColor]

            MyMusicAppTheme(darkTheme = isDarkMode, seedColor = seedColor) {
                AppNavigation(
                    viewModel = viewModel,
                    isDarkMode = isDarkMode,
                    themeColor = themeColor,
                    onToggleDarkMode = {
                        scope.launch { preferencesManager.setDarkMode(!isDarkMode) }
                    },
                    onChangeThemeColor = { color ->
                        scope.launch { preferencesManager.setThemeColor(color) }
                    }
                )
            }
        }
    }
}