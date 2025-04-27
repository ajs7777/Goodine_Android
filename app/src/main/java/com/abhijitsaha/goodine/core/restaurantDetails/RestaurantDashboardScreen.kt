package com.abhijitsaha.goodine.core.restaurantDetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun RestaurantDashboardScreen(
    isTablet: Boolean,
    restaurantProfileContent: @Composable () -> Unit,
    menuContent: @Composable () -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidthDp = this@BoxWithConstraints.maxWidth

        val configuration = LocalConfiguration.current
        val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT

        if (isTablet) {
            //            // Tablet layout: Side-by-side
            Row(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(if (isTablet && !isPortrait) 1f else 1.5f)) {
                    restaurantProfileContent()
                }
                Box(modifier = Modifier.weight(2f)) {
                    menuContent()
                }
            }
        } else {
            restaurantProfileContent
        }
    }
}
