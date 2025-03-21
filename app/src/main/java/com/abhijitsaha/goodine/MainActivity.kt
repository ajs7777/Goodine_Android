package com.abhijitsaha.goodine

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.abhijitsaha.goodine.ui.theme.GoodineTheme
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Allow both orientations only for tablets
        if (isTablet()) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        setContent {
            GoodineTheme {
                val navController = rememberNavController()
                Scaffold { paddingValues ->
                    AppNavigation(navController, Modifier.padding(paddingValues))
                }
            }
        }
    }

    private fun isTablet(): Boolean {
        return (resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    AnimatedNavHost(
        navController = navController,
        startDestination = "LoginScreen",
        modifier = modifier,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(500)) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(500)) },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(500)) },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(500)) }
    ) {
        composable("LoginScreen") {
            LoginWithNumberView(
                onNavigateToBusinessLogin = { navController.navigate("BusinessLoginScreen") }
            )
        }
        composable("BusinessLoginScreen") {
            BusinessLoginView(
                onBackClick = { navController.popBackStack() },
                onForgotPasswordClick = { /* Handle forgot password */ }
            )
        }
    }
}
