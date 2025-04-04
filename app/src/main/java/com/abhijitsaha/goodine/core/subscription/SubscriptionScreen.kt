package com.abhijitsaha.goodine.core.subscription

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import com.abhijitsaha.goodine.R
import com.abhijitsaha.goodine.core.authentication.view.BoldCloseIcon
import kotlinx.coroutines.delay

@Composable
fun SubscriptionScreen(onClose: () -> Unit) {
    var selectedPlan by remember { mutableStateOf("Yearly") }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.End
    ) {
        BoldCloseIcon(onClick = { onClose() })
    }
}
        CroppedImageCarousel()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 50.dp)
            .padding(top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column{
            Text(
                text = "Subscription",
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "Access to premium feature and Easy generate bills.",
                fontSize = 16.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(25.dp))
        Box {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(y = (-10).dp)
            ) {
                BestOfferTag()
            }

            SubscriptionOption("₹", "11,999", "/ Yearly", selectedPlan == "Yearly") {
                selectedPlan = "Yearly"
            }
        }

        SubscriptionOption("₹", "1,199", "/ Monthly", selectedPlan == "Monthly") {
            selectedPlan = "Monthly"
        }

        Spacer(modifier = Modifier.height(3.dp))
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    imageVector = Icons.Default.Check,
                    modifier = Modifier.size(30.dp)
                        .padding(end = 5.dp),
                    contentDescription = "Selected",
                    tint = Color(0xFFFFA500)
                )
                Text(text = "Select Tables and particular seats", color = Color.Gray, fontSize = 13.sp)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    imageVector = Icons.Default.Check,
                    modifier = Modifier.size(30.dp)
                        .padding(end = 5.dp),
                    contentDescription = "Selected",
                    tint = Color(0xFFFFA500)
                )
            Text(text = "Easily Generate bills And track payments", color = Color.Gray, fontSize = 13.sp)
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = { /* Handle Subscription */ },
            modifier = Modifier
                .height(55.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
        ) {
            Text(text = "Buy Premium", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
        }

        Row(
            Modifier.padding(8.dp)
        ) {
            Text(
                text = "Terms of use",
                color = Color.Gray,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            )
            Spacer(modifier = Modifier.width(50.dp))
            Text(
                text = "Privacy Policy",
                color = Color.Gray,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            )
        }

    }
}
}

@Composable
fun SubscriptionOption(currency: String, plan: String, duration: String, isSelected: Boolean, onSelect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(89.dp)
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) Color(0xFFE5E5EA) else Color(0xFFE5E5EA))
            .padding(16.dp)
            .clickable { onSelect() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Text(text = currency, fontSize = 20.sp, fontWeight = FontWeight.Normal, modifier = Modifier.padding(top = 4.dp))
                Text(text = plan, fontSize = 30.sp, fontWeight = FontWeight.Black)
            }

            Text(
                text = duration, fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFA500)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    modifier = Modifier.size(16.dp),
                    contentDescription = "Selected",
                    tint = Color.White
                )
            }
        }
    }
}
@Composable
fun BestOfferTag() {
    Box(
        modifier = Modifier
            .width(75.dp)
            .height(55.dp)
            .background(Color(0xFFFF9800), shape = RoundedCornerShape(10.dp)), // Orange background
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Best Offer",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(bottom = 35.dp)

        )
    }
}


@Composable
fun CroppedImageCarousel() {
    val images = listOf(
        R.drawable.image1, R.drawable.image2,
        R.drawable.image3, R.drawable.image4
    )

    var currentIndex by remember { mutableIntStateOf(0) }
    val scrollState = rememberLazyListState()

    // Auto-scroll effect with centering
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000) // Delay between transitions
            currentIndex = (currentIndex + 1) % images.size
            scrollState.animateScrollToItem(currentIndex, scrollOffset = -80) // Centering effect
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        LazyRow(
            state = scrollState,
            modifier = Modifier.fillMaxWidth(),
            flingBehavior = rememberSnapFlingBehavior(lazyListState = scrollState), // Snap to center
            horizontalArrangement = Arrangement.spacedBy((10).dp), // Overlapping effect
            contentPadding = PaddingValues(horizontal = 120.dp) // Ensures centering
        ) {
            itemsIndexed(images) { index, image ->
                val scale by animateFloatAsState(
                    targetValue = if (index == currentIndex) 1.2f else 0.9f,
                    animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                    label = "scale"
                )
                val alpha by animateFloatAsState(
                    targetValue = if (index == currentIndex) 1f else 0.6f,
                    animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                    label = "alpha"
                )

                Image(
                    painter = painterResource(id = image),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(width = 170.dp, height = 200.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            alpha = alpha
                        )
                )
            }
        }
    }
}


@Preview
@Composable
fun PreviewSubscriptionScreen() {
    SubscriptionScreen(onClose = {})
}
