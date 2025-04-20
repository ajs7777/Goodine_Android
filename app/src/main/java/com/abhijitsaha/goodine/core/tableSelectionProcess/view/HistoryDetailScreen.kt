package com.abhijitsaha.goodine.core.tableSelectionProcess.view


import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abhijitsaha.goodine.core.authentication.view.BoldArrowBackIcon
import com.abhijitsaha.goodine.core.tableSelectionProcess.viewModel.OrdersViewModel
import com.abhijitsaha.goodine.models.Reservation
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.abhijitsaha.goodine.models.HistoryRecord
import com.abhijitsaha.goodine.models.Order
import com.abhijitsaha.goodine.models.OrderItem
import com.abhijitsaha.goodine.models.Restaurant
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.collections.forEach

@Composable
fun HistoryDetailScreen(
    history: HistoryRecord,
    onBack: () -> Unit,
) {
    val orderVM: OrdersViewModel = viewModel()
    val orders by orderVM.orders.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(history.reservationID) {
        orderVM.fetchOrders(history.reservationID)
    }

    val totalPrice = orders.sumOf { order ->
        order.items.values.sumOf { it.quantity * it.price }
    }

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }

    Column {
        Column(modifier = Modifier.padding(start = 5.dp)) {
            BoldArrowBackIcon(onClick = onBack)
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp).padding(top = 16.dp).padding(bottom = 1.dp)
                .fillMaxSize()
        ) {
            Text(
                "Reservation ID: ${history.reservationID.takeLast(12)}",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp
            )
            Spacer(Modifier.height(4.dp))
            Text("Booking Date: ${dateFormat.format(history.timestamp)}")
            Text("Booking Time: ${timeFormat.format(history.timestamp)}")

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Text("Selected Tables & Seats", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Spacer(Modifier.height(4.dp))
            history.tables.forEach { table ->
                val seatCount = history.peopleCount[table] ?: 0
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Table $table - $seatCount ")
                    Icon(Icons.Default.Person, contentDescription = "Person")
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Text("Ordered Items", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Spacer(Modifier.height(8.dp))

            Column {
                if (orders.isEmpty()) {
                    Text("No orders placed yet.")
                } else {
                    orders.forEach { order ->
                        order.items.forEach { (itemId, item) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${item.name} - ${item.quantity} x ₹${"%.2f".format(item.price)}", fontWeight = FontWeight.Medium)

                            }
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total Price:", fontWeight = FontWeight.Bold)
                Text("₹${"%.2f".format(totalPrice)}", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val pdf = generateHistoryPDF(
                        context = context,
                        history = history,
                        orders = orders,
                        restaurant = null, // Pass your Restaurant object if available
                        totalPrice = totalPrice
                    )
                    pdf?.let { printPDF(context, it) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Print, contentDescription = "Print")
                Spacer(Modifier.width(8.dp))
                Text("Print Slip")
            }
        }
    }
}

fun generateHistoryPDF(
    context: Context,
    history: HistoryRecord,
    orders: List<Order>,
    restaurant: Restaurant?,
    totalPrice: Double
): File? {
    val document = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(250, 600, 1).create()
    val page = document.startPage(pageInfo)
    val canvas = page.canvas

    val titlePaint = Paint().apply {
        typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
        textSize = 16f
        textAlign = Paint.Align.CENTER
    }

    val bodyPaint = Paint().apply {
        textSize = 12f
        textAlign = Paint.Align.CENTER
    }

    val monoPaint = Paint().apply {
        typeface = Typeface.MONOSPACE
        textSize = 12f
        textAlign = Paint.Align.CENTER
    }

    val pageWidth = pageInfo.pageWidth.toFloat()
    var yOffset = 20f

    fun drawCenteredText(text: String, paint: Paint) {
        canvas.drawText(text, pageWidth / 2, yOffset, paint)
        yOffset += 20f
    }

    fun drawItemRow(item: OrderItem) {
        val formattedPrice = String.format("%s%.2f", restaurant?.currencySymbol ?: "", item.price)
        val formattedQty = "%2d".format(item.quantity)
        val truncatedName = if (item.name.length > 15) item.name.take(15) + "…" else item.name
        val line = String.format("%-15s %3s  %6s", truncatedName, formattedQty, formattedPrice)

        drawCenteredText(line, monoPaint)
    }

    // Draw header
    drawCenteredText("RESTAURANT ORDER SLIP", titlePaint)
    drawCenteredText("Reservation ID: ${history.reservationID.takeLast(12)}", bodyPaint)
    drawCenteredText("Date: ${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(history.timestamp)}", bodyPaint)
    drawCenteredText("Time: ${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(history.timestamp)}", bodyPaint)

    yOffset += 10f
    drawCenteredText("Table & Seats", titlePaint)
    drawCenteredText("--------------------------------", monoPaint)

    history.tables.forEach { table ->
        history.seats[table]?.let { seats ->
            val count = seats.count { it }
            if (count > 0) {
                drawCenteredText("Table $table - $count Seats", monoPaint)
            }
        }
    }

    yOffset += 10f
    drawCenteredText("Ordered Items", titlePaint)
    drawCenteredText("--------------------------------", monoPaint)
    drawCenteredText("Item             Qty   Price", monoPaint)
    drawCenteredText("--------------------------------", monoPaint)

    orders.forEach { order ->
        order.items.values.forEach { item ->
            drawItemRow(item)
        }
    }

    drawCenteredText("--------------------------------", monoPaint)
    yOffset += 10f
    drawCenteredText("TOTAL: ${restaurant?.currencySymbol ?: ""}%.2f".format(totalPrice), titlePaint)

    document.finishPage(page)

    val file = File(context.cacheDir, "OrderSlip.pdf")
    return try {
        document.writeTo(FileOutputStream(file))
        document.close()
        file
    } catch (e: IOException) {
        Log.e("PDF", "Error writing PDF", e)
        null
    }
}


