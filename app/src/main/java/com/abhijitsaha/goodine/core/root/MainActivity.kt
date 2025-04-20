package com.abhijitsaha.goodine.core.root

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
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.abhijitsaha.goodine.core.tabBar.RestaurantNavigationBar
import com.abhijitsaha.goodine.core.authentication.view.BusinessLoginView
import com.abhijitsaha.goodine.core.authentication.view.MainLoginPage
import com.abhijitsaha.goodine.core.tableSelectionProcess.view.ReservationDetailScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        // Allow both orientations only for tablets
        if (isTablet()) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        setContent {
            GoodineTheme {
                val navController = rememberNavController()

                val user = FirebaseAuth.getInstance().currentUser
                val startDestination = if (user != null) "RestaurantNavigationBar" else "MainLoginPage"

                Scaffold { paddingValues ->
                    AppNavigation(
                        navController = navController,
                        modifier = Modifier.padding(paddingValues),
                        startDestination = startDestination
                    )
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
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(500)) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(500)) },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(500)) },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(500)) }
    ) {
        composable("MainLoginPage") {
            MainLoginPage(
                onNavigateToBusinessLogin = { navController.navigate("BusinessLoginScreen") }
            )
        }
        composable("BusinessLoginScreen") {
            BusinessLoginView(
                navController = navController,
                onBackClick = { navController.popBackStack() },
                onForgotPasswordClick = { /* Handle forgot password */ }
            )
        }
        composable("RestaurantNavigationBar") {
            RestaurantNavigationBar(navController = navController)
        }

    }
}
