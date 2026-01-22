package com.rootlink.mystoremanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.rootlink.mystoremanager.ui.navigation.AppNavGraph
import com.rootlink.mystoremanager.ui.navigation.BottomNavigationBar
import com.rootlink.mystoremanager.ui.navigation.MainRoute
import com.rootlink.mystoremanager.ui.screen.dashboard.CompanySetupScreen
import com.rootlink.mystoremanager.ui.theme.MyStoreManagerTheme
import com.rootlink.mystoremanager.ui.viewmodel.AppStartupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            MyStoreManagerTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val viewModel: AppStartupViewModel = hiltViewModel()

    val isCompanySetup by viewModel.isCompanySetup.collectAsState()

    // Wait until DB check finishes
    if (isCompanySetup == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (isCompanySetup == false) {
        // 🚨 FIRST TIME ONLY
        CompanySetupScreen(
            onDone = {
                viewModel.refresh()
            }
        )
    } else {
        // ✅ NORMAL APP FLOW
        Scaffold(
            bottomBar = {
                BottomNavigationBar(navController)
            }
        ) { paddingValues ->
            AppNavGraph(
                navController = navController,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

