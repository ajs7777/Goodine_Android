package com.abhijitsaha.goodine

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.*



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RestaurantProfileScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        // Image Section
        RestaurantScreen()

        Spacer(modifier = Modifier.height(8.dp))

        // Status Badge
        Text(
            text = "Open Now",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier
                .background(Color(0xFFCBE97B), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Restaurant Info
        Text(text = "Ajs", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = "Indian", fontSize = 16.sp, color = Color.Gray)
        Text(text = "Agartala | 2 Km", fontSize = 14.sp, color = Color.Gray)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "⭐ 4.5 (3k Ratings)", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "₹600 for two", fontSize = 14.sp, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Features
        Text(text = "Features", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth()) {
            FeatureTag("Reservation Available")
            Spacer(modifier = Modifier.width(8.dp))
            FeatureTag("Dine in Available")
        }
    }
}

@Composable
fun FeatureTag(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        modifier = Modifier
            .background(Color.LightGray, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RestaurantImageCarousel(images: List<Int>) {
    val pagerState = rememberPagerState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalPager(
            count = images.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) { page ->
            Image(
                painter = painterResource(id = images[page]),
                contentDescription = "Restaurant Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Page indicator dots
        HorizontalPagerIndicator(
            pagerState = pagerState,
            activeColor = MaterialTheme.colorScheme.primary,
            inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            modifier = Modifier.padding(8.dp)
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
        // ... other UI elements
    }
}
