package com.abhijitsaha.goodine.core.tableSelectionProcess.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abhijitsaha.goodine.core.restaurantMenu.viewModel.MenuViewModel
import com.abhijitsaha.goodine.core.tableSelectionProcess.viewModel.OrdersViewModel
import com.abhijitsaha.goodine.core.tableSelectionProcess.viewModel.TableViewModel
import com.abhijitsaha.goodine.models.HistoryRecord
import com.abhijitsaha.goodine.models.Reservation
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalConfiguration


@Composable
fun DetailedOrdersScreen(
    viewModel: TableViewModel = viewModel(),
    isTablet: Boolean = true
) {

    LaunchedEffect(Unit) {
        viewModel.fetchReservations()
    }

    val active by viewModel.activeReservations.collectAsState()
    val history by viewModel.historyReservations.collectAsState()

    val sortedActive = active.sortedByDescending { it.timestamp }
    val sortedHistory = history.sortedByDescending { it.billingTime }

    val menuVM: MenuViewModel = viewModel()
    val menuItems by menuVM.menuItems.collectAsState()

    val orderVM: OrdersViewModel = viewModel()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedReservation by remember { mutableStateOf<Reservation?>(null) }
    var selectedHistory by remember { mutableStateOf<HistoryRecord?>(null) }

    var showPayBillDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var reservationToActOn by remember { mutableStateOf<Reservation?>(null) }

    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT


    Row(modifier = Modifier.fillMaxSize()) {
        // Left Pane – Orders List
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            Text(
                text = "Orders & History",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 35.sp
                ),
                modifier = Modifier.padding(top = 22.dp, bottom = 15.dp)
            )

            LazyColumn {
                item {
                    Text("Active Reservations (${active.size})", fontWeight = FontWeight.Bold, fontSize = 23.sp)
                }

                items(sortedActive) { reservation ->
                    ReservationCard(
                        reservation = reservation,
                        onPayBill = {
                            reservationToActOn = reservation
                            showPayBillDialog = true
                        },
                        onDeleteReservation = {
                            reservationToActOn = reservation
                            showDeleteDialog = true
                        },
                        onAddFoodClick = {
                            selectedReservation = reservation
                            showBottomSheet = true
                        },
                        onCardClick = {
                            selectedReservation = reservation
                            selectedHistory = null
                        }
                    )
                }

                item {
                    Text("Order History", fontWeight = FontWeight.Bold, fontSize = 23.sp, modifier = Modifier.padding(vertical = 16.dp))
                }

                items(sortedHistory) { historyItem ->
                    HistoryCard(
                        history = historyItem,
                        onCardClick = {
                            selectedHistory = historyItem
                            selectedReservation = null
                        }
                    )
                }
            }
        }

        // Right Pane – Details
        if (isTablet) {
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f), modifier = Modifier.fillMaxHeight().width(1.dp))

            Box(
                modifier = Modifier
                    .weight(if (isTablet && !isPortrait) 1f else 1f)
                    .fillMaxHeight()
                    .padding(16.dp)
            ) {
                when {
                    selectedReservation != null -> {
                        ReservationDetailScreen(
                            reservation = selectedReservation!!,
                            onBack = { selectedReservation = null }
                        )
                    }

                    selectedHistory != null -> {
                        HistoryDetailScreen(
                            history = selectedHistory!!,
                            onBack = { selectedHistory = null }
                        )
                    }

                    else -> {
                        Text("Select a reservation or history item", color = Color.Gray)
                    }
                }
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

        if (showPayBillDialog && reservationToActOn != null) {
            AlertDialog(
                onDismissRequest = { showPayBillDialog = false },
                title = { Text("Confirm Payment") },
                text = { Text("Do you want to complete the payment?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.markReservationAsPaid(reservationToActOn!!.id)
                        showPayBillDialog = false
                    }) {
                        Text("Done")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showPayBillDialog = false
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showDeleteDialog && reservationToActOn != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Cancel Reservation") },
                text = { Text("Are you sure you want to delete this reservation?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteReservation(reservationToActOn!!.id)
                        showDeleteDialog = false
                    }) {
                        Text("Delete", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
