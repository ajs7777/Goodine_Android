package com.abhijitsaha.goodine.core.restaurantMenu.views

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.abhijitsaha.goodine.core.restaurantMenu.viewModel.MenuViewModel
import com.abhijitsaha.goodine.models.MenuItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMenuItemScreen(
    viewModel: MenuViewModel,
    existingItem: MenuItem? = null,
    onCancel: () -> Unit,
    onDone: () -> Unit
) {
    var name by remember { mutableStateOf(existingItem?.foodname ?: "") }
    var description by remember { mutableStateOf(existingItem?.foodDescription ?: "") }
    var price by remember { mutableStateOf(existingItem?.foodPrice?.toString() ?: "") }
    var isVeg by remember { mutableStateOf(existingItem?.veg ?: false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUrl by remember { mutableStateOf(existingItem?.foodImage) } // Store existing image URL

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
        imageUrl = null // Reset imageUrl when a new image is picked
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(
                        onClick = onCancel,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Cancel", fontSize = 18.sp)
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            val finalPrice = price.toDoubleOrNull() ?: 0.0
                            if (name.isNotBlank() && price.isNotBlank()) {
                                if (existingItem != null) {
                                    viewModel.updateMenuItem(
                                        existingItem.copy(
                                            foodname = name,
                                            foodDescription = description,
                                            foodPrice = finalPrice,
                                            veg = isVeg,
                                            foodImage = imageUrl ?: existingItem.foodImage
                                        ),
                                        imageUri
                                    )
                                } else {
                                    viewModel.saveMenuItem(name, description, finalPrice, isVeg, imageUri)
                                }
                                onDone()
                            }
                        },
                        enabled = name.isNotBlank() && price.isNotBlank(),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Done", fontSize = 18.sp)
                    }
                },
                windowInsets = WindowInsets(0.dp)

            )
        }
    ) { paddingValues ->

    Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {

        Text(
            text = if (existingItem != null) "Edit Item" else "Add Item",
            fontSize = 35.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Food Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp)
            )

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(15.dp)
            )
        }

        Row (
            modifier = Modifier.padding(top = 12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ){
            Column {
                Text("Upload Image", fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Gray.copy(alpha = 0.1f))
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        imageUri != null -> Image(
                            painter = rememberAsyncImagePainter(imageUri),
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        !imageUrl.isNullOrBlank() -> Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = "Existing Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        else -> Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Upload",
                            tint = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Veg", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                Spacer(modifier = Modifier.width(7.dp))
                Switch(
                    checked = isVeg,
                    onCheckedChange = { isVeg = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        uncheckedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF39C93D),
                        uncheckedTrackColor = Color(0xFFD0D0D0),
                        uncheckedBorderColor = Color(0xFFD0D0D0),
                    )
                )
            }
        }
        }
    }
}
