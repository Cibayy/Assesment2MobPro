package com.iqbal0107.mymusicapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iqbal0107.mymusicapp.datastore.PreferencesManager
import com.iqbal0107.mymusicapp.navigation.AppNavigation
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
            val scope = rememberCoroutineScope()

            MyMusicAppTheme(darkTheme = isDarkMode) {
                AppNavigation(
                    viewModel = viewModel,
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = {
                        scope.launch {
                            preferencesManager.setDarkMode(!isDarkMode)
                        }
                    }
                )
            }
        }
    }
}