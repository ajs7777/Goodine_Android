package com.abhijitsaha.goodine

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RestaurantProfileScreen(
    navController: NavHostController,
    businessAuthVM: BusinessAuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
) {

    val restaurant = businessAuthVM.currentRestaurant
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }


    LaunchedEffect(Unit) {
        businessAuthVM.fetchCurrentRestaurant()
    }

    if (businessAuthVM.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.Gray)
        }
    } else {

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Image Section
            RestaurantScreen()

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Open Now",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .background(Color(0xFFCBE97B), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    )

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                showSheet = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE0E0E0),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                        modifier = Modifier
                            .heightIn(min = 32.dp)
                            .defaultMinSize(minHeight = 1.dp),
                        contentPadding = PaddingValues(horizontal = 15.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = "Edit Profile",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                }


                Spacer(modifier = Modifier.height(5.dp))

                // Restaurant Info
                Text(
                    text = restaurant?.name ?: "Loading...",
                    fontSize = 35.sp,
                    fontWeight = FontWeight.ExtraBold)

                Spacer(modifier = Modifier.height(5.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(text = restaurant?.type ?: "Cuisine", fontSize = 18.sp, color = Color.Black)
                        Text(text = "${restaurant?.city ?: ""} | 2 Km", fontSize = 18.sp, color = Color.Gray)
                    }

                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "⭐ 4.5(3k Ratings)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        if (!restaurant?.averageCost.isNullOrEmpty()) {
                            Text(
                                text = "${restaurant.currencySymbol}${restaurant.averageCost} for two",
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                        }

                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                HorizontalDivider(
                    color = Color.LightGray,
                    thickness = 1.dp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Location and Time
                Text(text = "Loaction", fontSize = 27.sp, fontWeight = FontWeight.ExtraBold)

                Spacer(modifier = Modifier.height(20.dp))

                // Location Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 15.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = Color.Black,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = restaurant?.address ?: "Address not available",
                        color = Color.Black,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Time Row
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Timing",
                        tint = Color.Black,
                        modifier = Modifier
                            .padding(2.dp)
                            .size(19.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${timeFormat.format(restaurant?.openingTime ?: Date())} – ${timeFormat.format(restaurant?.closingTime ?: Date())}",

                        color = Color.Black,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                HorizontalDivider(
                    color = Color.LightGray,
                    thickness = 1.dp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Features
                Text(text = "Features", fontSize = 27.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FeatureTag("Reservation Available")
                    Spacer(modifier = Modifier.width(8.dp))
                    FeatureTag("Dine in Available")
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 30.dp)
                    .padding(bottom = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                TextButton(
                    onClick = {
                        businessAuthVM.signOut()
                        navController.navigate("MainLoginPage") {
                            popUpTo("RestaurantNavigationBar") { inclusive = true }
                        }
                    },
                    enabled = !businessAuthVM.isLoading
                ) {
                    Text(
                        text = "Log Out",
                        color = Color.Red,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

        }
        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showSheet = false
                },
                sheetState = sheetState,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.9f)
                ) {
                    restaurant?.let {
                        RestaurantDetailsScreen(
                            initialRestaurant = it,
                            onSave = { updated ->
                                businessAuthVM.updateRestaurant(updated)
                            }
                        )
                    } ?: run {
                        // Show loading indicator or fallback UI
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }

    }
    }
}

@Composable
fun FeatureTag(text: String) {
    Text(
        text = text,
        fontSize = 17.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .background(Color(0xFFE5E5EA), RoundedCornerShape(9.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RestaurantImageCarousel(images: List<Int>) {
    val pagerState = rememberPagerState { images.size }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp) // Height of the image carousel
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Image(
                painter = painterResource(id = images[page]),
                contentDescription = "Restaurant Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Overlay the custom pager indicator at the bottom center
        CustomPagerIndicator(
            pageCount = images.size,
            currentPage = pagerState.currentPage,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp) // adjust to position closer/further from bottom
        )
    }
}

@Composable
fun RestaurantScreen() {
    val restaurantImages = listOf(
        R.drawable.restaurant1,
        R.drawable.restaurant2,
        R.drawable.restaurant3
    )

    Column {
        RestaurantImageCarousel(images = restaurantImages)
    }
}


@Composable
fun CustomPagerIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = Color.White,
    inactiveColor: Color = Color.White.copy(alpha = 0.4f),
    dotSize: Dp = 8.dp,
    spacing: Dp = 10.dp
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .clip(CircleShape)
                    .background(if (index == currentPage) activeColor else inactiveColor)
            )
            if (index < pageCount - 1) {
                Spacer(modifier = Modifier.width(spacing))
            }
        }
    }
}

