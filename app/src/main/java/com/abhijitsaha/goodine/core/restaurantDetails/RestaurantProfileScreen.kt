package com.abhijitsaha.goodine.core.restaurantDetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.abhijitsaha.goodine.R
import com.abhijitsaha.goodine.core.authentication.viewModel.BusinessAuthViewModel
import com.abhijitsaha.goodine.core.restaurantMenu.viewModel.MenuViewModel
import com.abhijitsaha.goodine.core.restaurantMenu.views.MenuScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.platform.LocalConfiguration
import kotlinx.coroutines.delay

@Composable
fun isTablet(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp >= 600
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RestaurantProfileScreen(
    navController: NavHostController,
    businessAuthVM: BusinessAuthViewModel = viewModel(),
) {
    val restaurant = businessAuthVM.currentRestaurant
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    var showMenuScreen by remember { mutableStateOf(false) }
    val menuViewModel: MenuViewModel = viewModel()

    LaunchedEffect(Unit) {
        businessAuthVM.fetchCurrentRestaurant()
    }
    if (businessAuthVM.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
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
            RestaurantScreen(images = restaurant?.imageUrls ?: emptyList())
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
                    var currentTime by remember { mutableStateOf(Date()) }

                    LaunchedEffect(Unit) {
                        while (true) {
                            delay(60 * 1000)
                            currentTime = Date()
                        }
                    }



                    var statusText: String
                    var backgroundColor: Color
                    var textColor: Color

                    restaurant?.let {
                        val openTime = it.openingTime
                        val closeTime = it.closingTime

                        if (openTime != null && closeTime != null) {
                            val openSoonThreshold = Date(openTime.time - 30 * 60 * 1000)
                            val closeSoonThreshold = Date(closeTime.time - 30 * 60 * 1000)

                            val isOpenNow = currentTime.after(openTime) && currentTime.before(closeTime)
                            val isOpeningSoon = currentTime.after(openSoonThreshold) && currentTime.before(openTime)
                            val isClosingSoon = currentTime.after(closeSoonThreshold) && currentTime.before(closeTime)

                            when {
                                isClosingSoon -> {
                                    statusText = "Closes Soon"
                                    backgroundColor = Color(0xFFF57F17)
                                    textColor = Color(0xFFFFFFFF)
                                }
                                isOpenNow -> {
                                    statusText = "Open Now"
                                    backgroundColor = Color(0xFFCBE97B)
                                    textColor = Color.Black
                                }
                                isOpeningSoon -> {
                                    statusText = "Opens Soon"
                                    backgroundColor = Color(0xFFFFF176)
                                    textColor = Color.Black
                                }
                                else -> {
                                    statusText = "Closed"
                                    backgroundColor = Color(0xFFD9192C)
                                    textColor = Color.White
                                }
                            }

                        } else {
                            statusText = "Closed"
                            backgroundColor = Color.LightGray
                            textColor = Color.DarkGray
                        }
                    } ?: run {
                        statusText = "Loading..."
                        backgroundColor = Color.LightGray
                        textColor = Color.DarkGray
                    }
                    Text(
                        text = statusText,
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .background(backgroundColor, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    )



                    Button(
                        onClick = {
                            coroutineScope.launch {
                                showSheet = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF757575).copy(alpha = 0.3f),
                            contentColor = MaterialTheme.colorScheme.primary
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
                    color = MaterialTheme.colorScheme.primary,
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
                        Text(text = restaurant?.type ?: "Cuisine", fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                        Text(text = "${restaurant?.city ?: ""} ", fontSize = 18.sp, color = Color.Gray)
                    }

                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {

                        Text(
                            text = "⭐️No Ratings",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        if (!restaurant?.averageCost.isNullOrEmpty()) {
                            Text(
                                text = "${restaurant.currencySymbol}${restaurant.averageCost} for two",
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.primary
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
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = restaurant?.address ?: "Address not available",
                        color = MaterialTheme.colorScheme.primary,
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
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(2.dp)
                            .size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${timeFormat.format(restaurant?.openingTime ?: Date())} – ${timeFormat.format(restaurant?.closingTime ?: Date())}",
                        color = MaterialTheme.colorScheme.primary,
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
                        color = Color(0xFFE72020),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

        }
        if (!isTablet()) {
            FloatingMenuButton(
                onClick = {
                    showMenuScreen = true
                }
            )
        }


        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showSheet = false
                },
                sheetState = sheetState,
                modifier = Modifier.widthIn(max = 650.dp).fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .widthIn(max = 650.dp)
                        .fillMaxWidth()
                        .fillMaxHeight(0.9f)
                ) {
                    restaurant?.let {
                        RestaurantDetailsScreen(
                            initialRestaurant = it,
                            businessAuthViewModel = businessAuthVM,
                            onSave = { updated ->
                                businessAuthVM.updateRestaurant(updated)
                                coroutineScope.launch {
                                    sheetState.hide()
                                    showSheet = false
                                }
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

    AnimatedVisibility(
        visible = showMenuScreen,
        enter = slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(durationMillis = 500)),
        exit = slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(durationMillis = 500))
    ) {
        MenuScreen(
            viewModel = menuViewModel,
            onBack = { showMenuScreen = false }
        )
    }
}

@Composable
fun FeatureTag(text: String) {
    Text(
        text = text,
        fontSize = 17.sp,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .background(Color(0xFF757575).copy(alpha = 0.3f), RoundedCornerShape(9.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RestaurantImageCarousel(images: List<String>) {
    val pagerState = rememberPagerState { images.size }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            CachedAsyncImage(
                imageUrl = images[page],
                contentDescription = "Restaurant Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        CustomPagerIndicator(
            pageCount = images.size,
            currentPage = pagerState.currentPage,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
        )
    }
}

@Composable
fun RestaurantScreen(images: List<String>) {
    Column {
        RestaurantImageCarousel(images = images)
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

@Composable
fun FloatingMenuButton(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.secondary
            ),
            modifier = Modifier
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(12.dp),
                    ambientColor = Color.Black.copy(alpha = 0.7f),
                    spotColor = Color.Black.copy(alpha = 0.7f)
                ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.businessicon),
                contentDescription = "Menu Icon",
                modifier = Modifier.size(34.dp)
            )
            Text(text = "Menu", fontSize = 18.sp, modifier = Modifier.padding(horizontal = 6.dp))
        }
    }
}
