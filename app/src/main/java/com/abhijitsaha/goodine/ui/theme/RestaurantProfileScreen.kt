package com.abhijitsaha.goodine.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abhijitsaha.goodine.R

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RestaurantProfileScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        // Image Section
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.restaurant_image),
                contentDescription = "Restaurant Image",
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Status Badge
        Text(
            text = "Open Now",
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier
                .background(Color.Green, RoundedCornerShape(8.dp))
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
