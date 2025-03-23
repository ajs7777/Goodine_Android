package com.abhijitsaha.goodine

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessLoginView(
    onBackClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    businessAuthVM : BusinessAuthViewModel = viewModel ()
){
    val focusManager = LocalFocusManager.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var showSignupScreen by remember { mutableStateOf(false) }
    var showRestaurantNavigationBar by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
    ) {
        if (showRestaurantNavigationBar) {
            RestaurantNavigationBar()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    BoldArrowBackIcon(onClick = onBackClick)
                }

                Spacer(modifier = Modifier.weight(0.5f))

                Image(
                    painter = painterResource(id = R.drawable.businessicon),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(320.dp)
                )

                Spacer(modifier = Modifier.weight(0.2f))

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null
                    },
                    placeholder = { Text("Enter your email", modifier = Modifier.alpha(0.4f)) },
                    modifier = Modifier.widthIn(500.dp),
                    shape = RoundedCornerShape(15.dp),
                    isError = emailError != null,
                    supportingText = { emailError?.let { Text(it, color = Color(0xFFFFA500)) } },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Gray,
                        unfocusedBorderColor = Color.Gray
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(2.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = null
                    },
                    placeholder = { Text("Enter your password", modifier = Modifier.alpha(0.4f)) },
                    modifier = Modifier.widthIn(500.dp),
                    shape = RoundedCornerShape(15.dp),
                    isError = passwordError != null,
                    supportingText = { passwordError?.let { Text(it, color = Color(0xFFFFA500)) } },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Gray,
                        unfocusedBorderColor = Color.Gray
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(5.dp))

                Button(
                    onClick = {
                        var valid = true
                        if (email.isEmpty()) {
                            emailError = "Email is required"
                            valid = false
                        }
                        if (password.isEmpty()) {
                            passwordError = "Password is required"
                            valid = false
                        }

                        if (valid) {
                            businessAuthVM.signIn(
                                email = email,
                                password = password,
                                onSuccess = {
                                    showRestaurantNavigationBar = true
                                },
                                onFailure = { error ->
                                    passwordError = error // Show the error under password field, or you can use Snackbar
                                }
                            )
                        }

                    },
                    modifier = Modifier.widthIn(500.dp).height(65.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Log In", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.widthIn(500.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Create Business Account",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { showSignupScreen = true }
                    )
                    Text(
                        "Forgot password?",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onForgotPasswordClick() }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.goodine_text),
                        contentDescription = "Goodine Logo",
                        modifier = Modifier
                            .padding(1.dp)
                            .size(width = 110.dp, height = 50.dp)
                            .alpha(0.4f)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "By clicking, I accept the",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = " Terms & Conditions ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = " & ",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = " Privacy Policy",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }

    AnimatedVisibility(
        visible = showSignupScreen,
        enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(durationMillis = 500)),
        exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(durationMillis = 500))
    ) {
        BusinessSignupScreen(onClose = { showSignupScreen = false })
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewBusinessLoginView() {
    BusinessLoginView(
        onBackClick = {},
        onForgotPasswordClick = {}
    )
}
@Composable
fun BoldArrowBackIcon(
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.primary
) {
    IconButton(onClick = onClick) {
        Canvas(modifier = Modifier.size(30.dp)) {
            val strokeWidth = 8f // Adjust thickness
            val arrowLength = size.minDimension * 0.6f
            val startX = size.width * 0.7f
            val centerY = size.height / 2
            val endX = startX - arrowLength

            drawLine(
                color = color,
                start = Offset(startX, centerY),
                end = Offset(endX, centerY),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = color,
                start = Offset(endX, centerY),
                end = Offset(endX + arrowLength * 0.4f, centerY - arrowLength * 0.4f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = color,
                start = Offset(endX, centerY),
                end = Offset(endX + arrowLength * 0.4f, centerY + arrowLength * 0.4f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

