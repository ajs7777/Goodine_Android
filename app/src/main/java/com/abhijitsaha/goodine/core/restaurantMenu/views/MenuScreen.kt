package com.abhijitsaha.goodine.core.restaurantMenu.views

import android.util.DisplayMetrics
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.abhijitsaha.goodine.core.restaurantMenu.viewModel.MenuViewModel
import com.abhijitsaha.goodine.models.MenuItem
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.abhijitsaha.goodine.R
import com.abhijitsaha.goodine.core.authentication.view.BoldArrowBackIcon


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    viewModel: MenuViewModel,
    onBack: () -> Unit,
) {

    var showAddItemScreen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true) // Prevent expansion
    val context = LocalContext.current
    val displayMetrics: DisplayMetrics = context.resources.displayMetrics
    val screenHeight = displayMetrics.heightPixels / displayMetrics.density
    val menuItems by viewModel.menuItems.collectAsState(initial = emptyList())
    val selectedItem by viewModel.selectedMenuItem.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("", fontSize = 24.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    BoldArrowBackIcon(onClick = onBack)
                },
                actions = {
                    TextButton(onClick = {showAddItemScreen = true}) {
                        Text(
                            text = "Add Item",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(1.dp))
                        BoldPlusIcon(onClick = { showAddItemScreen = true })
                    }
                },
                windowInsets = WindowInsets(0.dp) // Removes extra padding at the top
            )


        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Menu Title
                Text(
                    text = "Menu",
                    fontSize = 35.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier
                        .padding(start = 16.dp, top = 1.dp, bottom = 8.dp)
                )

                // Scrollable list of menu items
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(menuItems.size) { index ->
                        MenuItemCard(
                            item = menuItems[index],
                            onDelete = {
                                viewModel.deleteMenuItem(menuItems[index].id)
                            },
                            onClick = {
                                viewModel.selectMenuItem(menuItems[index])
                                showAddItemScreen = true
                            }
                        )

                    }
                }
            }
        }
    )
    if (showAddItemScreen) {
        ModalBottomSheet(
            onDismissRequest = {
                showAddItemScreen = false
                viewModel.clearSelectedMenuItem()
          },
            sheetState = sheetState,
            dragHandle = null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight.dp * 0.6f)
            ) {
                AddMenuItemScreen(
                    viewModel = viewModel,
                    onCancel = {
                        showAddItemScreen = false
                        viewModel.clearSelectedMenuItem()
                    },
                    onDone = {
                        showAddItemScreen = false
                        viewModel.clearSelectedMenuItem()
                    },
                    existingItem = selectedItem // Pass pre-filled data
                )
            }
        }
    }
}

@Composable
fun MenuItemCard(
    item: MenuItem, onDelete: () -> Unit, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(85.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
        ) {
            if (item.foodImage.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFE0E0E0)) // Light gray-ish custom color
                        .clip(RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Placeholder",
                        tint = Color.Gray,
                        modifier = Modifier.size(36.dp) // Custom size
                    )
                }

            } else {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.foodImage)
                        .crossfade(true)
                        .build(),
                    contentDescription = item.foodname,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.placeholder),
                    error = painterResource(id = R.drawable.placeholder)
                )

            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(6.dp)
                    .size(20.dp)
            ) {
                VegNonVegIcon(isVeg = item.veg )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy((-2).dp)
        ) {
            Text(
                item.foodname,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )
            if (item.foodDescription.isNotBlank()) {
                Text(
                    item.foodDescription,
                    color = Color.Gray,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text("₹${item.foodPrice}", color = Color.Gray)
        }


        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color.Red,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
fun VegNonVegIcon(modifier: Modifier = Modifier, isVeg: Boolean) {
    val color = if (isVeg) Color(0xFF00C200) else Color.Red

    Box(
        modifier = modifier
            .size(18.dp)
            .border(2.dp, color, shape = RectangleShape)
            .background(Color.White)
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
    }
}

@Composable
fun BoldPlusIcon(
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.primary
) {
    IconButton(onClick = onClick) {
        Canvas(modifier = Modifier.size(27.dp)) {
            val strokeWidth = 8f // Adjust thickness
            val centerX = size.width / 2
            val centerY = size.height / 2
            val lineLength = size.minDimension * 0.6f
            val halfLength = lineLength / 2

            // Horizontal Line
            drawLine(
                color = color,
                start = Offset(centerX - halfLength, centerY),
                end = Offset(centerX + halfLength, centerY),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            // Vertical Line
            drawLine(
                color = color,
                start = Offset(centerX, centerY - halfLength),
                end = Offset(centerX, centerY + halfLength),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

