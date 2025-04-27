package com.abhijitsaha.goodine.core.authentication.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abhijitsaha.goodine.R

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainLoginPage(
    onNavigateToBusinessLogin: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.weight(1f))
        // App Icon
        Image(
            painter = painterResource(id = R.drawable.login_icon),
            contentDescription = "Login Icon",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(width = 500.dp, height = 280.dp)
        )

        // Title
        Column(
            modifier = Modifier.padding(bottom = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Get started with App",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Login or signup to use App",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }

        // Business Login Button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 15.dp)
        ) {
            GoodineButton(
                onClick = {
                    println("Login With Business clicked!")
                    onNavigateToBusinessLogin()
                },
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .widthIn(500.dp)
                    .height(60.dp),
                backgroundColor = MaterialTheme.colorScheme.primary,
                cornerRadius = 12.dp
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.store),
                        contentDescription = "Store Icon",
                        modifier = Modifier.size(17.dp, 16.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Login With Business",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(4f))

        // Footer
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.goodine_text),
                contentDescription = "Goodine Logo",
                modifier = Modifier
                    .padding(1.dp)
                    .size(width = 110.dp, height = 50.dp)
                    .alpha(0.4f)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "By clicking, I accept the",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
                Text(
                    text = " Terms & Conditions ",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = " & ",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
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
// Custom Button
@Composable
fun GoodineButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    cornerRadius: Dp = 8.dp,
    content: @Composable () -> Unit
) {
   Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(cornerRadius)
    ) {
        content()
    }
}