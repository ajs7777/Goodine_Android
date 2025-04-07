package com.abhijitsaha.goodine.core.tableSelectionProcess.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abhijitsaha.goodine.R
import com.abhijitsaha.goodine.core.tableSelectionProcess.viewModel.TableViewModel
import java.text.SimpleDateFormat
import java.util.*


class TableActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TableScreen()
        }
    }
}

@Composable
fun TableScreen() {
    val currentTime = remember { mutableStateOf("Loading...\n--:--:-- --") }
    val dateParts = currentTime.value.split("\n")
    LaunchedEffect(Unit) {
        while (true) {
            currentTime.value = SimpleDateFormat("d MMM yyyy\nhh:mm:ss a", Locale.getDefault()).format(Date())
            kotlinx.coroutines.delay(1000L) // 1 second
        }
    }

    val viewModel: TableViewModel = viewModel()

    var isEditing by remember { mutableStateOf(false) }
    val rows by viewModel.rows.collectAsState()
    val columns by viewModel.columns.collectAsState()

    val tableCount = rows * columns

    LaunchedEffect(Unit) {
        viewModel.fetchTableLayout()
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val maxWidth = this.maxWidth
        val maxHeight = this.maxHeight * 0.74f
        val verticalSpacing = maxHeight * 0.03f
        val horizontalSpacing = maxWidth * 0.03f

        val availableHeight = maxHeight - verticalSpacing * (rows - 1)
        val availableWidth = maxWidth - horizontalSpacing * (columns - 1)

        val maxTableHeight = availableHeight / rows
        val maxTableWidth = availableWidth / columns

        val tableSize = minOf(maxTableHeight, maxTableWidth)
        val seatSize = tableSize / 2.9f
        val seatSpacing = seatSize / 4


        Column(modifier = Modifier.fillMaxSize()) {

            // ✅ Title
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Table Selection",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))


            if (isEditing) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically

                ) {

                    Icon(
                        painter = painterResource(id = R.drawable.column),
                        contentDescription = "Menu Icon",
                        modifier = Modifier.size(25.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Stepper(
                        value = columns,
                        onValueChange = { viewModel.updateColumns(it) },
                        minValue = 1,
                        maxValue = 8
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Icon(
                        painter = painterResource(id = R.drawable.above),
                        contentDescription = "Menu Icon",
                        modifier = Modifier.size(25.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Stepper(
                        value = rows,
                        onValueChange = { viewModel.updateRows(it)  },
                        minValue = 1,
                        maxValue = 10
                    )

                    Button(
                        onClick = {
                            viewModel.saveTableLayout()
                            isEditing = false
                          },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        elevation = null
                    ) {
                        Text("Done", fontSize = 18.sp, color = Color.Black)
                    }

                }
            } else {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = dateParts.getOrNull(0) ?: "",
                            color = Color(0xFFFF9800),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = dateParts.getOrNull(1) ?: "",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    if (!isEditing) {
                        Row(
                            modifier = Modifier
                                .clickable { isEditing = true }
                                .background(Color.Black, shape = RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp), // Adjust padding for size
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Edit Layout",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Add Table",
                                color = Color.White,
                                fontWeight = FontWeight.Medium,
                                fontSize = 17.sp
                            )
                        }

                    }
                }
            }


            Spacer(modifier = Modifier.height(10.dp))

            HorizontalDivider(color = Color.LightGray, thickness = 1.dp)

            Spacer(modifier = Modifier.height(15.dp))

                // ✅ Table Grid View
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(maxHeight)
                    .padding(bottom = 10.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        var tableNumber = 1
                        repeat(rows) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(horizontalSpacing, Alignment.CenterHorizontally)
                            ) {
                                repeat(columns) {
                                    if (tableNumber <= tableCount) {
                                        TableItem(
                                            tableNumber = tableNumber,
                                            tableSize = tableSize,
                                            seatSize = seatSize,
                                            seatSpacing = seatSpacing
                                        )
                                        tableNumber++
                                    }
                                }
                            }
                            if (it != rows - 1) {
                                Spacer(modifier = Modifier.height(verticalSpacing))
                            }
                        }

                    }
                }

                // ✅ Done Button (could be used to save selection)
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text("Done", color = Color.White, fontSize = 18.sp)
                }

        }
    }
}

@Composable
fun TableItem(tableNumber: Int, tableSize: Dp, seatSize: Dp, seatSpacing: Dp) {
    val seatStates = remember { mutableStateListOf(false, false, false, false) }

    Box(
        modifier = Modifier
            .size(tableSize) // This is respected
            .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(18.dp)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(seatSpacing)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(seatSpacing)) {
                    SeatItem(seatStates, 0, seatSize)
                    SeatItem(seatStates, 1, seatSize)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(seatSpacing)) {
                    SeatItem(seatStates, 2, seatSize)
                    SeatItem(seatStates, 3, seatSize)
                }
            }
        }

        Text(
            text = "$tableNumber",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}


@Composable
fun SeatItem(seatStates: MutableList<Boolean>, index: Int, seatSize: Dp) {
    Box(
        modifier = Modifier
            .size(seatSize)
            .background(
                if (seatStates[index]) Color(0xFF47E14C) else Color.LightGray,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { seatStates[index] = !seatStates[index] }
    )
}


@Composable
fun Stepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minValue: Int = 0,
    maxValue: Int = Int.MAX_VALUE
) {
    Row(
        modifier = modifier
            .background(Color(0xFFF2F2F2), shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { if (value > minValue) onValueChange(value - 1) },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Decrease",
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.width(1.dp))

        Text(
            text = value.toString(),
            modifier = Modifier.width(22.dp),
            textAlign = TextAlign.Center,
            fontSize = 15.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(1.dp))

        IconButton(
            onClick = { if (value < maxValue) onValueChange(value + 1) },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase",
                tint = Color.Black
            )
        }
    }
}
