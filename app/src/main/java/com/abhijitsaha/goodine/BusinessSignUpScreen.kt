package com.abhijitsaha.goodine

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun BusinessSignupScreen(
    onClose: () -> Unit,
    businessAuthVM : BusinessAuthViewModel = viewModel()
) {
    var businessName by remember { mutableStateOf("") }
    var businessType by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    //val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                BoldCloseIcon(onClick = { onClose() })
            }

            Column (
                horizontalAlignment = Alignment.Start
            ){
                Image(
                    painter = painterResource(id = R.drawable.businessicon),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(80.dp)
                )
                Text(
                    text = "Get started with Goodine business",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Give your customers a better experience by setting up your own online store.",
                    fontSize = 12.sp,
                    color = Color.Gray
                ) }
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = businessName,
                onValueChange = {
                    businessName = it
                    errorMessage = null
                },
                placeholder = { Text("Business Name", modifier = Modifier.alpha(0.4f)) },
                modifier = Modifier.widthIn(500.dp),
                shape = RoundedCornerShape(15.dp),
                isError = errorMessage != null,
                supportingText = {
                    errorMessage?.let { Text(it, color = Color(0xFFFFA500)) }
                },
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
                value = businessType,
                onValueChange = {
                    businessType = it
                    errorMessage = null
                },
                placeholder = { Text("Business Type", modifier = Modifier.alpha(0.4f)) },
                modifier = Modifier.widthIn(500.dp),
                shape = RoundedCornerShape(15.dp),
                isError = errorMessage != null,
                supportingText = {
                    errorMessage?.let { Text(it, color = Color(0xFFFFA500)) }
                },
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
                    errorMessage = null
                },
                placeholder = { Text("Address", modifier = Modifier.alpha(0.4f)) },
                modifier = Modifier.widthIn(500.dp),
                shape = RoundedCornerShape(15.dp),
                isError = errorMessage != null,
                supportingText = {
                    errorMessage?.let { Text(it, color = Color(0xFFFFA500)) }
                },
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
                    errorMessage = null
                },
                placeholder = { Text("City", modifier = Modifier.alpha(0.4f)) },
                modifier = Modifier.widthIn(500.dp),
                shape = RoundedCornerShape(15.dp),
                isError = errorMessage != null,
                supportingText = {
                    errorMessage?.let { Text(it, color = Color(0xFFFFA500)) }
                },
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
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = null
                },
                placeholder = { Text("Enter your email", modifier = Modifier.alpha(0.4f)) },
                modifier = Modifier.widthIn(500.dp),
                shape = RoundedCornerShape(15.dp),
                isError = errorMessage != null,
                supportingText = {
                    errorMessage?.let { Text(it, color = Color(0xFFFFA500)) }
                },
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
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = null
                },
                placeholder = { Text("Enter your password", modifier = Modifier.alpha(0.4f)) },
                modifier = Modifier.widthIn(500.dp),
                shape = RoundedCornerShape(15.dp),
                isError = errorMessage != null,
                supportingText = {
                    errorMessage?.let { Text(it, color = Color(0xFFFFA500)) }
                },
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

            Button(
                onClick = {
                    businessAuthVM.signUp(
                        name = businessName,
                        type = businessType,
                        city = city,
                        address = address,
                        email = email,
                        password = password,
                        onSuccess = {
                            successMessage = "Business registered successfully"
                            errorMessage = null
                        },
                        onFailure = { error ->
                            errorMessage = error
                            successMessage = null
                        }
                    )
                },
                modifier = Modifier
                    .widthIn(500.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = businessName.isNotEmpty() && businessType.isNotEmpty() && address.isNotEmpty() && city.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
            ) {
                Text("Sign Up", color = MaterialTheme.colorScheme.secondary, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(5.dp))

            // Show messages
            successMessage?.let {
                Text(
                    text = it,
                    color = Color(0xFF4CAF50),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Image(
                    painter = painterResource(id = R.drawable.goodine_text),
                    contentDescription = "GoodineLogo",
                    modifier = Modifier
                        .size(width = 100.dp, height = 50.dp)
                        .alpha(0.4f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 30.dp)
                ) {
                    Text(
                        text = "By clicking, I accept the",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        text = " Terms & Conditions ",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = " & ",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        text = " Privacy Policy",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }

}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewBusinessSignupScreen() {
    BusinessSignupScreen(onClose = {})
}

@Composable
fun BoldCloseIcon(
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.primary
) {
    IconButton(onClick = onClick) {
        Canvas(modifier = Modifier.size(28.dp)) {
            val strokeWidth = 8f // Adjust thickness for bold effect
            val startOffset = size.minDimension * 0.2f
            val endOffset = size.minDimension * 0.8f

            // Draw first diagonal line (\)
            drawLine(
                color = color,
                start = Offset(startOffset, startOffset),
                end = Offset(endOffset, endOffset),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            // Draw second diagonal line (/)
            drawLine(
                color = color,
                start = Offset(startOffset, endOffset),
                end = Offset(endOffset, startOffset),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}
