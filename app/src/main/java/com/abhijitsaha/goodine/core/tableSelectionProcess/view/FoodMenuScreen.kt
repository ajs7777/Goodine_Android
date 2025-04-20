package com.abhijitsaha.goodine.core.tableSelectionProcess.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.abhijitsaha.goodine.core.restaurantMenu.views.VegNonVegIcon
import com.abhijitsaha.goodine.models.MenuItem
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.abhijitsaha.goodine.core.tableSelectionProcess.viewModel.OrdersViewModel

@Composable
fun FoodMenuScreen(
    reservationId: String,
    menuItems: List<MenuItem>,
    viewModel: OrdersViewModel,
    onDismiss: () -> Unit
) {
    val quantities = remember { mutableStateMapOf<String, Int>() }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Order Food",
            fontSize = 35.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        LazyColumn {
            items(menuItems) { item ->
                MenuItemCard(
                    item = item,
                    quantities = quantities,
                    onQuantityChange = { id, newQty ->
                        quantities[id] = newQty
                    }
                )
            }
        }


        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val selectedItems = quantities.filterValues { it > 0 }

                if (selectedItems.isNotEmpty()) {
                    viewModel.saveOrderToFirestore(
                        reservationId = reservationId,
                        selectedItems = selectedItems,
                        menuItems = menuItems
                    )
                }
                onDismiss()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Place Order", color = Color.White, fontSize = 16.sp)
        }
    }
}

@Composable
fun MenuItemCard(
    item: MenuItem,
    quantities: MutableMap<String, Int>,
    onQuantityChange: (String, Int) -> Unit
) {
    val quantity = quantities[item.id] ?: 0
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(85.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
        ) {
            if (item.foodImage.isEmpty()) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Placeholder",
                    tint = Color.Gray,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(36.dp)
                )
            } else {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.foodImage)
                        .crossfade(true)
                        .build(),
                    contentDescription = item.foodname,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(6.dp)
                    .size(22.dp)
            ) {
                VegNonVegIcon(isVeg = item.veg)
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            Text(item.foodname, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            if (item.foodDescription.isNotBlank()) {
                Text(
                    item.foodDescription,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text("₹${item.foodPrice}", fontSize = 17.sp, color = Color.Gray)
        }

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE0DADA))
                .padding(horizontal = 15.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomShapeButton(isPlus = false, onClick = {
                val newQty = (quantities[item.id] ?: 0) - 1
                if (newQty <= 0) {
                    quantities.remove(item.id)
                    onQuantityChange(item.id, 0)
                } else {
                    quantities[item.id] = newQty
                    onQuantityChange(item.id, newQty)
                }
            })

            Text(
                quantity.toString(),
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 17.dp)
            )

            CustomShapeButton(isPlus = true, onClick = {
                val newQty = (quantities[item.id] ?: 0) + 1
                quantities[item.id] = newQty
                onQuantityChange(item.id, newQty)
            })
        }
    }
}

@Composable
fun CustomShapeButton(
    isPlus: Boolean,
    size: Dp = 13.dp,
    barThickness: Dp = 3.dp,
    color: Color = Color.Black,
    onClick: () -> Unit
) {
    val shapeSize = with(LocalDensity.current) { size.toPx() }
    val thickness = with(LocalDensity.current) { barThickness.toPx() }

    Box(
        modifier = Modifier
            .size(size)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onClick() })
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Horizontal bar
            drawRoundRect(
                color = color,
                topLeft = Offset(0f, size.toPx() / 2 - thickness / 2),
                size = Size(shapeSize, thickness),
                cornerRadius = CornerRadius(thickness / 2, thickness / 2)
            )
            // Vertical bar (only for plus)
            if (isPlus) {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(size.toPx() / 2 - thickness / 2, 0f),
                    size = Size(thickness, shapeSize),
                    cornerRadius = CornerRadius(thickness / 2, thickness / 2)
                )
            }
        }
    }
}

