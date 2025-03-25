package com.abhijitsaha.goodine

import android.app.TimePickerDialog
import android.net.Uri

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Locale


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RestaurantDetailsScreen(
    initialRestaurant: Restaurant = Restaurant(),
    onSave: (Restaurant) -> Unit
) {
    var name by remember { mutableStateOf(initialRestaurant.name) }
    var type by remember { mutableStateOf(initialRestaurant.type) }
    var address by remember { mutableStateOf(initialRestaurant.address) }
    var city by remember { mutableStateOf(initialRestaurant.city) }
    var state by remember { mutableStateOf(initialRestaurant.state) }
    var zipcode by remember { mutableStateOf(initialRestaurant.zipcode) }
    var costForTwo by remember { mutableStateOf(initialRestaurant.averageCost ?: "") }
    var currency by remember { mutableStateOf(initialRestaurant.currency) }
    var currencySymbol by remember { mutableStateOf(initialRestaurant.currencySymbol) }
    var openingTime by remember { mutableStateOf(initialRestaurant.openingTime) }
    var closingTime by remember { mutableStateOf(initialRestaurant.closingTime) }
    val imageUrls = remember { mutableStateListOf<String>().apply { addAll(initialRestaurant.imageUrls) } }

    val context = LocalContext.current

    val fromCalendar = remember { Calendar.getInstance() }
    val toCalendar = remember { Calendar.getInstance() }
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    val fromTimePicker = TimePickerDialog(
        context,
        { _, hour: Int, minute: Int ->
            fromCalendar.set(Calendar.HOUR_OF_DAY, hour)
            fromCalendar.set(Calendar.MINUTE, minute)
            openingTime = fromCalendar.time
        },
        fromCalendar.get(Calendar.HOUR_OF_DAY),
        fromCalendar.get(Calendar.MINUTE),
        false
    )

    val toTimePicker = TimePickerDialog(
        context,
        { _, hour: Int, minute: Int ->
            toCalendar.set(Calendar.HOUR_OF_DAY, hour)
            toCalendar.set(Calendar.MINUTE, minute)
            closingTime = toCalendar.time
        },
        toCalendar.get(Calendar.HOUR_OF_DAY),
        toCalendar.get(Calendar.MINUTE),
        false
    )

    Scaffold(
        bottomBar = {
            SaveAndContinueButton(
                onClick = {
                    val updatedRestaurant = Restaurant(
                        id = initialRestaurant.id,
                        ownerName = initialRestaurant.ownerName,
                        name = name,
                        type = type,
                        city = city,
                        state = state,
                        address = address,
                        zipcode = zipcode,
                        averageCost = costForTwo,
                        openingTime = openingTime,
                        closingTime = closingTime,
                        imageUrls = imageUrls.toList(),
                        currency = currency,
                        currencySymbol = currencySymbol,
                    )
                    onSave(updatedRestaurant)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(85.dp)
            )
        }
    ) { paddingValues ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ){
        Text(text = "Hotel Details", fontSize = 35.sp, fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(bottom = 20.dp)
            )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
            },
            placeholder = { Text("Business Name", modifier = Modifier.alpha(0.4f)) },
            modifier = Modifier.widthIn(500.dp),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors( // ✅ Fixed usage
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.Gray,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedTextColor = MaterialTheme.colorScheme.primary,
                unfocusedTextColor = MaterialTheme.colorScheme.primary,
                focusedPlaceholderColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray
            ),
            singleLine = true
        )

        OutlinedTextField(
            value = type,
            onValueChange = {
                type = it
            },
            placeholder = { Text("Indian, Chinese", modifier = Modifier.alpha(0.4f)) },
            modifier = Modifier.widthIn(500.dp),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors( // ✅ Fixed usage
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.Gray,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedTextColor = MaterialTheme.colorScheme.primary,
                unfocusedTextColor = MaterialTheme.colorScheme.primary,
                focusedPlaceholderColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray
            ),
            singleLine = true
        )
            OutlinedTextField(
                value = address,
                onValueChange = {
                    address = it
                },
                placeholder = { Text("Address", modifier = Modifier.alpha(0.4f)) },
                modifier = Modifier.widthIn(500.dp),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors( // ✅ Fixed usage
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.primary,
                    unfocusedTextColor = MaterialTheme.colorScheme.primary,
                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray
                ),
                singleLine = true
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                OutlinedTextField(
                    value = state,
                    onValueChange = {
                        state = it
                    },
                    placeholder = { Text("State", modifier = Modifier.alpha(0.4f)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors( // ✅ Fixed usage
                        focusedBorderColor = Color.Gray,
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = MaterialTheme.colorScheme.primary,
                        unfocusedTextColor = MaterialTheme.colorScheme.primary,
                        focusedPlaceholderColor = Color.Gray,
                        unfocusedPlaceholderColor = Color.Gray
                    ),
                    singleLine = true
                )
                OutlinedTextField(
                    value = city,
                    onValueChange = {
                        city = it
                    },
                    placeholder = { Text("City", modifier = Modifier.alpha(0.4f)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors( // ✅ Fixed usage
                        focusedBorderColor = Color.Gray,
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = MaterialTheme.colorScheme.primary,
                        unfocusedTextColor = MaterialTheme.colorScheme.primary,
                        focusedPlaceholderColor = Color.Gray,
                        unfocusedPlaceholderColor = Color.Gray
                    ),
                    singleLine = true
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                OutlinedTextField(
                    value = zipcode,
                    onValueChange = {
                        zipcode = it
                    },
                    placeholder = { Text("Zipcode", modifier = Modifier.alpha(0.4f)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors( // ✅ Fixed usage
                        focusedBorderColor = Color.Gray,
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = MaterialTheme.colorScheme.primary,
                        unfocusedTextColor = MaterialTheme.colorScheme.primary,
                        focusedPlaceholderColor = Color.Gray,
                        unfocusedPlaceholderColor = Color.Gray
                    ),
                    singleLine = true
                )
                UseMyLocationButton(onClick = {})
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Average Cost For two",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = costForTwo,
                    onValueChange = {
                        costForTwo = it
                    },
                    placeholder = { Text("", modifier = Modifier.alpha(0.4f)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors( // ✅ Fixed usage
                        focusedBorderColor = Color.Gray,
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = MaterialTheme.colorScheme.primary,
                        unfocusedTextColor = MaterialTheme.colorScheme.primary,
                        focusedPlaceholderColor = Color.Gray,
                        unfocusedPlaceholderColor = Color.Gray
                    ),
                    singleLine = true
                )

                CurrencyDropdown(
                    selectedCurrency = currency,
                    onCurrencySelected = {
                        currency = it.substringAfter(" ") // Extract "INR"
                        currencySymbol = it.substringBefore(" ") // Extract "₹"
                    }
                )

            }

            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = "Opening Hours:",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(225.dp)
                ) {
                    Text("From", color = Color(0xFFFF9800), fontWeight = FontWeight.Medium, fontSize = 20.sp)
                    TimeBox(time = "${timeFormat.format(openingTime)}" , onClick = { fromTimePicker.show() })
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(250.dp)
                ) {

                    Text("To", color = Color(0xFFFF9800), fontWeight = FontWeight.Medium, fontSize = 20.sp)
                    TimeBox(time = "${timeFormat.format(closingTime)}", onClick = { toTimePicker.show() })
                }
            }

            HorizontalDivider(
                color = Color.LightGray,
                thickness = 1.dp
            )

//            ImagePickerGrid(
//                images = imageUris,
//                onAddClick = { launcher.launch("image/*") },
//                onRemoveClick = { imageUris.remove(it) }
//            )

            Spacer(modifier = Modifier
                .height(50.dp)
                .padding(paddingValues)
            )
       }
    }
  }
}


@Composable
fun UseMyLocationButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)), // Orange color
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .height(57.dp)
            .width(175.dp)
    ) {
        Icon(
            imageVector = Icons.Default.MyLocation, // Use location icon
            contentDescription = "Location Icon",
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Use My Location",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyDropdown(
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val currencies = listOf("₹ INR", "$ USD", "€ EUR")

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.width(120.dp)
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedCurrency,
            onValueChange = {},
            label = null,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.Gray,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            modifier = Modifier.menuAnchor(
                MenuAnchorType.PrimaryEditable
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            currencies.forEach { currency ->
                DropdownMenuItem(
                    text = { Text(currency) },
                    onClick = {
                        onCurrencySelected(currency)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun TimeBox(time: String, onClick: () -> Unit) {
    Surface(
        color = Color(0xFFF2F2F2),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .width(100.dp)
            .height(40.dp)
            .clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = time, color = Color.Black, fontWeight = FontWeight.Medium, fontSize = 17.sp)
        }
    }
}

@Composable
fun ImagePickerGrid(
    modifier: Modifier = Modifier,
    images: List<Uri>,
    onAddClick: () -> Unit,
    onRemoveClick: (Uri) -> Unit
) {
    LazyRow(modifier = modifier.padding(8.dp)) {
        item {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.LightGray, RoundedCornerShape(8.dp))
                    .clickable { onAddClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.Black)
            }
        }

        items(images.size) { index ->
            val uri = images[index]
            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(100.dp)
            ) {
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                IconButton(
                    onClick = { onRemoveClick(uri) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(24.dp)
                        .background(Color.Red, CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White)
                }
            }
        }
    }
}
@Composable
fun SaveAndContinueButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isEnabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        enabled = isEnabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.LightGray
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
    ) {
        Text(
            text = "Save and Continue",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}


