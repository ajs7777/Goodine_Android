package com.abhijitsaha.goodine.core.tabBar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.abhijitsaha.goodine.R
import com.abhijitsaha.goodine.core.restaurantDetails.RestaurantDashboardScreen
import com.abhijitsaha.goodine.core.restaurantDetails.RestaurantProfileScreen
import com.abhijitsaha.goodine.core.restaurantMenu.viewModel.MenuViewModel
import com.abhijitsaha.goodine.core.restaurantMenu.views.MenuScreen
import com.abhijitsaha.goodine.core.tableSelectionProcess.view.DetailedOrdersScreen
import com.abhijitsaha.goodine.core.tableSelectionProcess.view.OrdersScreen
import com.abhijitsaha.goodine.core.tableSelectionProcess.view.TableScreen
import com.abhijitsaha.goodine.models.Reservation
import com.google.android.gms.common.util.DeviceProperties.isTablet

@Composable
fun RestaurantNavigationBar(
    navController: NavHostController
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val menuViewModel: MenuViewModel = viewModel()
    val context = LocalContext.current

    val items = listOf(
        BottomNavItem("Profile", R.drawable.fork_knife),
        BottomNavItem("Table", R.drawable.table),
        BottomNavItem("Orders", R.drawable.clock)
    )
    val selectedReservationState = remember { mutableStateOf<Reservation?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Show the selected screen
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            when (selectedIndex) {
                0 -> if (isTablet(context)) {
                    RestaurantDashboardScreen(
                        isTablet = true,
                        restaurantProfileContent = { RestaurantProfileScreen( navController = navController) },
                        menuContent = { MenuScreen(
                            viewModel = menuViewModel,
                            onBack = { navController.popBackStack() }
                        ) }
                    )
                } else {
                    RestaurantProfileScreen( navController = navController)
                }
                1 -> TableScreen()
                2 ->  if (isTablet(context)) {
                        DetailedOrdersScreen()
                }
                else { OrdersScreen()
                }
            }
        }

        // Bottom Navigation Bar
        Box {
            // Shadow-like effect at the top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.08f))
                        )
                    )
                    .align(Alignment.TopCenter)
            )

            // Main Row content
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                items.forEachIndexed { index, item ->
                    Box(
                        modifier = Modifier
                            .padding(5.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                selectedIndex = index
                            }
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = item.title,
                                modifier = Modifier
                                    .size(30.dp)
                                    .padding(bottom = 1.dp),
                                tint = if (selectedIndex == index) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                            Text(
                                text = item.title,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (selectedIndex == index) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        }
                    }
                }
            }
        }

    }
}


data class BottomNavItem(val title: String, val icon: Int)


