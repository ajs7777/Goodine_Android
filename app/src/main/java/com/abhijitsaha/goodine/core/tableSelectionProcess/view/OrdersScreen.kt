package com.abhijitsaha.goodine.core.tableSelectionProcess.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abhijitsaha.goodine.core.tableSelectionProcess.viewModel.TableViewModel
import com.abhijitsaha.goodine.models.HistoryRecord
import com.abhijitsaha.goodine.models.Reservation
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import com.abhijitsaha.goodine.R
import com.abhijitsaha.goodine.core.restaurantMenu.viewModel.MenuViewModel
import com.abhijitsaha.goodine.core.tableSelectionProcess.viewModel.OrdersViewModel
import com.abhijitsaha.goodine.models.MenuItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    viewModel: TableViewModel = viewModel()
) {
    val active by viewModel.activeReservations.collectAsState()
    val history by viewModel.historyReservations.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedReservation by remember { mutableStateOf<Reservation?>(null) }

    val sortedActive = active.sortedByDescending { it.timestamp }
    val sortedHistory = history.sortedByDescending { it.billingTime }

    val menuVM: MenuViewModel = viewModel()
    val menuItems by menuVM.menuItems.collectAsState()

    var showReservationDetailScreen by remember { mutableStateOf(false) }
    var showHistoryDetailScreen by remember { mutableStateOf(false) }
    var reservationForDetails by remember { mutableStateOf<Reservation?>(null) }
    var historyForDetails by remember { mutableStateOf<HistoryRecord?>(null) }

    val orderVM: OrdersViewModel = viewModel()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Orders & History",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 35.sp
                ),
                modifier = Modifier
                    .padding(top = 22.dp, bottom = 15.dp)
            )

            LazyColumn {
                item {
                    Text(
                        "Active Reservations (${active.size})",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 23.sp
                        ),
                        modifier = Modifier.padding(vertical = 15.dp)
                    )
                }

                items(sortedActive) { reservation ->
                    ReservationCard(
                        reservation = reservation,
                        onPayBill = {
                            viewModel.markReservationAsPaid(reservation.id)
                        },
                        onDeleteReservation = {
                            viewModel.deleteReservation(reservation.id)
                        },
                        onAddFoodClick = {
                            selectedReservation = reservation
                            showBottomSheet = true
                        },
                        onCardClick = {
                            reservationForDetails = reservation
                            showReservationDetailScreen = true
                        }
                    )
                }

                item {
                    Spacer(Modifier.height(18.dp))
                    Text(
                        "Order History",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 23.sp
                        ),
                        modifier = Modifier.padding(bottom = 15.dp)
                    )
                }

                items(sortedHistory) { history ->
                    HistoryCard(
                        history = history,
                        onCardClick = {
                            historyForDetails = history
                            showHistoryDetailScreen = true
                        }
                    )
                }
            }

            BottomSheetHost(
                showBottomSheet = showBottomSheet,
                selectedReservation = selectedReservation,
                menuItems = menuItems,
                orderVM = orderVM,
                onDismissRequest = {
                    showBottomSheet = false
                }
            )
        }

        // FULL SCREEN DETAIL VIEW
        AnimatedVisibility(
            visible = showReservationDetailScreen,
            enter = slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(durationMillis = 500)),
            exit = slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(durationMillis = 500))
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                reservationForDetails?.let {
                    ReservationDetailScreen(
                        reservation = it,
                        onBack = {
                            showReservationDetailScreen = false
                        }
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = showHistoryDetailScreen,
            enter = slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(durationMillis = 500)),
            exit = slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(durationMillis = 500))
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                historyForDetails?.let {
                    HistoryDetailScreen(
                        history = it,
                        onBack = {
                            showHistoryDetailScreen = false
                        }
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetHost(
    showBottomSheet: Boolean,
    selectedReservation: Reservation?,
    menuItems: List<MenuItem>,
    orderVM: OrdersViewModel,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    // Launch effect to show the sheet when it's required
    LaunchedEffect(showBottomSheet) {
        if (showBottomSheet) {
            sheetState.show()
        } else {
            // Wait until the sheet is fully shown before hiding it
            sheetState.hide()
        }
    }

    // Only show the sheet if the reservation is selected
    if (showBottomSheet && selectedReservation != null) {
        ModalBottomSheet(
            onDismissRequest = {
                onDismissRequest() // Call onDismissRequest to set the showBottomSheet flag to false
            },
            sheetState = sheetState,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.91f)
            ) {
                FoodMenuScreen(
                    reservationId = selectedReservation.id,
                    menuItems = menuItems,
                    viewModel = orderVM,
                    onDismiss = { onDismissRequest() }
                )
            }
        }
    }
}

@Composable
fun ReservationCard(
    reservation: Reservation,
    onPayBill: () -> Unit,
    onDeleteReservation: () -> Unit,
    onAddFoodClick: () -> Unit,
    onCardClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onCardClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F7))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp).padding(top = 8.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Booking Date: ${dateFormat.format(reservation.timestamp)}", fontWeight = FontWeight.Normal, fontSize = 14.sp, color = Color.Gray)

                Spacer(modifier = Modifier.weight(1f))

                Text("${timeFormat.format(reservation.timestamp)}", fontWeight = FontWeight.Normal, fontSize = 13.sp, color = Color.Gray)
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier.size(37.dp).padding(horizontal = 5.dp)
                        .clickable { onDeleteReservation() },
                    tint = Color.Red
                )

            }
            Text("ID: ${reservation.id.takeLast(12)}",
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier.offset(y = (-8).dp)
            )


            Spacer(modifier = Modifier.height(8.dp))
            Row (
                verticalAlignment = Alignment.CenterVertically
            ){
                Image(
                    painter = painterResource(id = R.drawable.businessicon),
                    contentDescription = "Seal",
                    modifier = Modifier.size(90.dp),
                )
            PeopleCountDisplay(peopleCount = reservation.peopleCount)

                Spacer(modifier = Modifier.weight(1f))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End,
                ) {
                    Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFFFA726))
                                .clickable { onPayBill() }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                    {
                        Text("Pay Bill", color = Color.White, fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black)
                            .clickable { onAddFoodClick() }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text("Add Food", color = Color.White, fontWeight = FontWeight.Medium)
                    }
                }
            }


        }
    }
}

@Composable
fun HistoryCard(
    history: HistoryRecord,
    onCardClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onCardClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F7))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row {
                Text("Booking Date: ${dateFormat.format(history.timestamp)}", fontWeight = FontWeight.Normal, fontSize = 14.sp, color = Color.Gray)

                Spacer(modifier = Modifier.weight(1f))

                Text("${timeFormat.format(history.timestamp)}", fontWeight = FontWeight.Normal, fontSize = 13.sp, color = Color.Gray)

            }

            Text("ID: ${history.reservationID.takeLast(12)}", fontWeight = FontWeight.Normal, fontSize = 13.sp, color = Color.Gray, modifier = Modifier.offset(y = (-3).dp))

            Spacer(modifier = Modifier.height(8.dp))
            Row (
                verticalAlignment = Alignment.CenterVertically
            ){
                Image(
                    painter = painterResource(id = R.drawable.seal),
                    contentDescription = "Seal",
                    modifier = Modifier.size(64.dp),
                    colorFilter = ColorFilter.tint(Color.Gray)
                )
                Spacer(modifier = Modifier.width(10.dp))
                PeopleCountDisplay(peopleCount = history.peopleCount.mapKeys { it.key.toInt() })

                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Paid",
                    tint = Color(0xFF36D73B)
                )
                Spacer(modifier = Modifier.width(7.dp))
                Text("Paid", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ){
                history.billingTime?.let { billingTime ->
                    Text(
                        "Billing Time: ${dateFormat.format(billingTime)} at ${timeFormat.format(billingTime)}",
                        style = MaterialTheme.typography.bodySmall
                    )

                }
            }

        }
    }
}

@Composable
fun PeopleCountDisplay(peopleCount: Map<Int, Int>) {
    Column {
        peopleCount.forEach { (table, count) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Table $table: $count")
                Spacer(modifier = Modifier.width(3.dp))
                Icon(Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(19.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
