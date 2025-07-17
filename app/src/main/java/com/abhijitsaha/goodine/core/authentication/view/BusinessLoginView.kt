package com.abhijitsaha.goodine.core.authentication.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.abhijitsaha.goodine.R
import com.abhijitsaha.goodine.core.authentication.viewModel.BusinessAuthViewModel
import com.abhijitsaha.goodine.core.tabBar.RestaurantNavigationBar

@Composable
fun BusinessLoginView(
    navController: NavHostController,
    onBackClick: () -> Unit,
    businessAuthVM: BusinessAuthViewModel = viewModel()
) {
    val focusManager = LocalFocusManager.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    var showSignupScreen by remember { mutableStateOf(false) }
    var showResetPasswordScreen by remember { mutableStateOf(false) }
    var showRestaurantNavigationBar by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
    ) {
        AnimatedVisibility(
            visible = showRestaurantNavigationBar,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            RestaurantNavigationBar(navController = navController)
        }

        AnimatedVisibility(
            visible = showSignupScreen,
            enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(500)),
            exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(500))
        ) {
            BusinessSignupScreen(onClose = { showSignupScreen = false })
        }

        AnimatedVisibility(
            visible = showResetPasswordScreen,
            enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(500)),
            exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(500))
        ) {
            BusinessResetPasswordView(onBackClick = { showResetPasswordScreen = false })
        }

        AnimatedVisibility(
            visible = !showSignupScreen && !showResetPasswordScreen && !showRestaurantNavigationBar,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            LoginForm(
                email = email,
                password = password,
                emailError = emailError,
                passwordError = passwordError,
                onEmailChange = {
                    email = it
                    emailError = null
                },
                onPasswordChange = {
                    password = it
                    passwordError = null
                },
                onLoginClick = {
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
                            onSuccess = { showRestaurantNavigationBar = true },
                            onFailure = { error -> passwordError = error }
                        )
                    }
                },
                onSignupClick = { showSignupScreen = true },
                onForgotPasswordClick = { showResetPasswordScreen = true },
                onBackClick = onBackClick
            )
        }
    }

}

@Composable
fun LoginForm(
    email: String,
    password: String,
    emailError: String?,
    passwordError: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onSignupClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            BoldArrowBackIcon(onClick = onBackClick)
        }

        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(id = R.drawable.businessicon),
            contentDescription = "App Logo",
            modifier = Modifier.size(320.dp)
        )

        Spacer(modifier = Modifier.weight(0.2f))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
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
            onValueChange = onPasswordChange,
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
            onClick = onLoginClick,
            modifier = Modifier
                .widthIn(500.dp)
                .height(60.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.secondary
            )
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
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onSignupClick)
            )
            Text(
                "Forgot password?",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onForgotPasswordClick)
            )
        }

        Spacer(modifier = Modifier.weight(6f))

        Column(
            modifier = Modifier.padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                Text("By clicking, I accept the", fontSize = 10.sp, color = Color.Gray)
                Text(
                    text = " Terms & Conditions ",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(" & ", fontSize = 10.sp, color = Color.Gray)
                Text(
                    text = " Privacy Policy",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun BusinessResetPasswordView(
    onBackClick: () -> Unit,
    businessAuthVM: BusinessAuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            BoldCloseIcon(onClick = { onBackClick() })
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text("Reset Password", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = null
                message = null
            },
            placeholder = { Text("Enter your email", modifier = Modifier.alpha(0.4f)) },
            modifier = Modifier.widthIn(500.dp),
            shape = RoundedCornerShape(15.dp),
            isError = emailError != null,
            supportingText = { emailError?.let { Text(it, color = Color.Red) } },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = {
                if (email.isBlank()) {
                    emailError = "Email is required"
                    return@Button
                }

                businessAuthVM.resetPassword(
                    email = email,
                    onSuccess = { message = "Reset link sent to your email" },
                    onFailure = { emailError = it }
                )
            },
            modifier = Modifier
                .widthIn(500.dp)
                .height(60.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text(text = "Send Reset Link", fontSize = 18.sp)
        }



        Spacer(modifier = Modifier.height(16.dp))

        message?.let {
            Text(text = it, color = Color.Green, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun BoldArrowBackIcon(
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.primary
) {
    IconButton(onClick = onClick) {
        Canvas(modifier = Modifier.size(30.dp)) {
            val strokeWidth = 8f
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
