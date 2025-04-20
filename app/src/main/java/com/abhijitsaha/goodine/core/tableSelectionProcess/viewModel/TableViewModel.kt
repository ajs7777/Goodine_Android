package com.abhijitsaha.goodine.core.tableSelectionProcess.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log
import com.abhijitsaha.goodine.models.HistoryRecord
import com.abhijitsaha.goodine.models.Reservation
import java.util.*
import com.google.firebase.Timestamp


class TableViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userID = auth.currentUser?.uid

    private val _rows = MutableStateFlow(3)
    val rows: StateFlow<Int> = _rows

    private val _columns = MutableStateFlow(2)
    val columns: StateFlow<Int> = _columns

    // Only have ONE StateFlow for selected seats
    private val _selectedSeats = MutableStateFlow<Map<Int, List<Boolean>>>(emptyMap())
    val selectedSeats: StateFlow<Map<Int, List<Boolean>>> = _selectedSeats

    private val _reservedSeats = MutableStateFlow<Map<Int, List<Boolean>>>(emptyMap())
    val reservedSeats: StateFlow<Map<Int, List<Boolean>>> = _reservedSeats

    private val _reservations = MutableStateFlow<List<Reservation>>(emptyList())
    val reservations: StateFlow<List<Reservation>> = _reservations

    private val _activeReservations = MutableStateFlow<List<Reservation>>(emptyList())
    val activeReservations: StateFlow<List<Reservation>> = _activeReservations

    private val _historyReservations = MutableStateFlow<List<HistoryRecord>>(emptyList())
    val historyReservations: StateFlow<List<HistoryRecord>> = _historyReservations

    private val _lastReservationId = MutableStateFlow<String?>(null)
    val lastReservationId: StateFlow<String?> = _lastReservationId


    init {
        initializeAllTables()
        fetchReservations()
    }


    fun updateRows(value: Int) {
        _rows.value = value
        initializeAllTables()
    }

    fun updateColumns(value: Int) {
        _columns.value = value
        initializeAllTables()
    }

    private fun initializeAllTables() {
        val totalTables = _rows.value * _columns.value
        val initialSeats = mutableMapOf<Int, List<Boolean>>()
        for (i in 1..totalTables) {
            initialSeats[i] = List(4) { false }
        }

        _selectedSeats.value = initialSeats
        Log.d("TableViewModel", "Initialized tables: ${_selectedSeats.value}")
    }

    fun toggleSeat(tableNumber: Int, seatIndex: Int) {
        val currentMap = _selectedSeats.value.toMutableMap()
        val seatList = currentMap[tableNumber]?.toMutableList() ?: List(4) { false }.toMutableList()

        seatList[seatIndex] = !seatList[seatIndex]
        currentMap[tableNumber] = seatList
        _selectedSeats.value = currentMap

        Log.d("ToggleSeat", "Table: $tableNumber, Seat: $seatIndex toggled. Current Map: $currentMap")
    }

    fun saveTableLayout() {
        userID?.let { uid ->
            val tableData = hashMapOf(
                "rows" to _rows.value,
                "columns" to _columns.value,
                "userID" to uid
            )

            db.collection("business_users")
                .document(uid)
                .collection("tables")
                .document("layout")
                .set(tableData)
                .addOnSuccessListener {
                    Log.d("TableViewModel", "Table layout saved successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("TableViewModel", "Failed to save table layout", e)
                }
        }
    }

    fun fetchTableLayout(onComplete: () -> Unit) {
        userID?.let { uid ->
            db.collection("business_users")
                .document(uid)
                .collection("tables")
                .document("layout")
                .get()
                .addOnSuccessListener { doc ->
                    doc?.let {
                        val fetchedRows = it.getLong("rows")?.toInt()
                        val fetchedCols = it.getLong("columns")?.toInt()
                        if (fetchedRows != null && fetchedCols != null) {
                            _rows.value = fetchedRows
                            _columns.value = fetchedCols
                        }
                    }
                    initializeAllTables()
                    onComplete()
                }
                .addOnFailureListener {
                    Log.e("TableViewModel", "Failed to fetch table layout", it)
                    initializeAllTables()
                    onComplete()
                }
        } ?: run {
            initializeAllTables()
            onComplete()
        }
    }

    fun saveReservation(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                val uid = auth.currentUser?.uid ?: throw Exception("User not logged in")

                val selectedMap = _selectedSeats.value
                Log.d("TableViewModel", "Attempting to save with seats: $selectedMap")

                val filteredSeats = selectedMap.filterValues { it.any { selected -> selected } }

                if (filteredSeats.isEmpty()) {
                    throw Exception("No seats selected")
                }

                val reservationID = UUID.randomUUID().toString()
                val reservationData = mutableMapOf<String, Any>(
                    "reservationID" to reservationID,
                    "isPaid" to false,
                    "timestamp" to Date()
                )

                for ((tableNumber, seatStates) in filteredSeats) {
                    reservationData["table_${tableNumber}_seats"] = seatStates
                    reservationData["table_$tableNumber"] = seatStates.count { it }
                }


                Log.d("TableViewModel", "Final reservation data: $reservationData")

                db.collection("business_users")
                    .document(uid)
                    .collection("reservations")
                    .document(reservationID)
                    .set(reservationData)
                    .addOnSuccessListener {
                        _lastReservationId.value = reservationID
                        Log.d("TableViewModel", "Reservation saved successfully")
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        Log.e("TableViewModel", "Firestore save failed", e)
                        onFailure(e)
                    }

            } catch (e: Exception) {
                Log.e("TableViewModel", "Exception while saving reservation", e)
                onFailure(e)
            }
        }
    }

    fun fetchReservedSeats() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("business_users")
            .document(uid)
            .collection("reservations")
            .get()
            .addOnSuccessListener { result ->
                val reservedMap = mutableMapOf<Int, MutableList<Boolean>>()

                for (document in result) {
                    for ((key, value) in document.data) {
                        if (key.startsWith("table_") && key.endsWith("_seats")) {
                            val tableNumber = key.removePrefix("table_").removeSuffix("_seats").toIntOrNull()
                            val seats = value as? List<Boolean>
                            if (tableNumber != null && seats != null) {
                                val current = reservedMap.getOrPut(tableNumber) { MutableList(4) { false } }
                                seats.forEachIndexed { index, isReserved ->
                                    if (isReserved) current[index] = true
                                }
                            }
                        }
                    }
                }

                _reservedSeats.value = reservedMap
                Log.d("TableViewModel", "Reserved Seats Map: $reservedMap")
            }
            .addOnFailureListener {
                Log.e("TableViewModel", "Failed to fetch reserved seats", it)
            }
    }

    fun fetchReservations() {
        val uid = auth.currentUser?.uid ?: return
        val businessRef = db.collection("business_users").document(uid)

        businessRef.collection("reservations").get()
            .addOnSuccessListener { activeSnapshot ->
                val active = activeSnapshot.documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    val id = doc.id
                    val timestamp = when (val ts = data["timestamp"]) {
                        is Timestamp -> ts.toDate()
                        is Date -> ts
                        else -> Date()
                    }

                    val isPaid = data["isPaid"] as? Boolean ?: false

                    val seats = mutableMapOf<Int, List<Boolean>>()
                    val peopleCount = mutableMapOf<Int, Int>()
                    val tables = mutableListOf<Int>()

                    for ((key, value) in data) {
                        if (key.startsWith("table_") && key.endsWith("_seats")) {
                            val tableNumber = key.removePrefix("table_").removeSuffix("_seats").toIntOrNull()
                            if (tableNumber != null && value is List<*>) {
                                @Suppress("UNCHECKED_CAST")
                                seats[tableNumber] = value as List<Boolean>
                                tables.add(tableNumber)
                            }
                        } else if (key.startsWith("table_") && !key.endsWith("_seats")) {
                            val tableNumber = key.removePrefix("table_").toIntOrNull()
                            val count = (value as? Long)?.toInt()
                            if (tableNumber != null && count != null) {
                                peopleCount[tableNumber] = count
                            }
                        }
                    }

                    Reservation(
                        id = id,
                        timestamp = timestamp,
                        isPaid = isPaid,
                        seats = seats,
                        tables = tables,
                        peopleCount = peopleCount
                    )
                }


                _activeReservations.value = active

                businessRef.collection("history").get()
                    .addOnSuccessListener { historySnapshot ->
                        val history = historySnapshot.documents.mapNotNull { doc ->
                            try {
                                val data = doc.data ?: return@mapNotNull null
                                val reservationID = doc.id
                                val isPaid = data["isPaid"] as? Boolean ?: false
                                val timestamp = when (val ts = data["timestamp"]) {
                                    is Timestamp -> ts.toDate()
                                    is Date -> ts
                                    else -> Date()
                                }
                                val billingTime = when (val ts = data["billingTime"]) {
                                    is Timestamp -> ts.toDate()
                                    is Date -> ts
                                    else -> Date()
                                }
                                val tableCounts =mutableListOf<Int>()
                                val peopleCounts = mutableMapOf<Int, Int>()
                                val tableSeats = mutableMapOf<Int, List<Boolean>>()

                                for ((key, value) in data) {
                                    if (key.startsWith("table_") && key.endsWith("_seats")) {
                                        val tableNum = key.removePrefix("table_").removeSuffix("_seats").toIntOrNull()
                                        if (tableNum != null && value is List<*>) {
                                            @Suppress("UNCHECKED_CAST")
                                            tableSeats[tableNum] = value as List<Boolean>
                                            tableCounts.add(tableNum)
                                        }
                                    } else if (key.startsWith("table_") && !key.endsWith("_seats")) {
                                        val tableNum = key.removePrefix("table_").toIntOrNull()
                                        val count = (value as? Long)?.toInt()
                                        if (tableNum != null && count != null) {
                                            peopleCounts[tableNum] = count
                                        }
                                    }
                                }

                                HistoryRecord(
                                    reservationID = reservationID,
                                    isPaid = isPaid,
                                    billingTime = billingTime,
                                    timestamp = timestamp,
                                    tables = tableCounts,
                                    seats = tableSeats,
                                    peopleCount = peopleCounts
                                )
                            } catch (e: Exception) {
                                Log.e("HistoryParse", "Failed to parse history record: ${doc.id}", e)
                                null
                            }
                        }

                        _historyReservations.value = history
                    }
            }
    }

    fun markReservationAsPaid(reservationId: String) {
        val uid = auth.currentUser?.uid ?: return
        val businessRef = db.collection("business_users").document(uid)

        val reservationDocRef = businessRef.collection("reservations").document(reservationId)
        val historyCollectionRef = businessRef.collection("history")
        val billingTime = Date()

        Log.d("markAsPaid", "Fetching reservation: $reservationId")

        reservationDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val data = document.data?.toMutableMap() ?: return@addOnSuccessListener
                    val timestamp = when (val ts = data["timestamp"]) {
                        is Timestamp -> ts.toDate()
                        is Date -> ts
                        else -> Date()
                    }
                    val isPaid = true
                    val billingTime = Date()

                    val seats = mutableMapOf<Int, List<Boolean>>()
                    val peopleCount = mutableMapOf<Int, Int>()

                    for ((key, value) in data) {
                        if (key.startsWith("table_") && key.endsWith("_seats")) {
                            val tableNumber = key.removePrefix("table_").removeSuffix("_seats").toIntOrNull()
                            if (tableNumber != null && value is List<*>) {
                                @Suppress("UNCHECKED_CAST")
                                seats[tableNumber] = value as List<Boolean>
                            }
                        } else if (key.startsWith("table_") && !key.endsWith("_seats")) {
                            val tableNumber = key.removePrefix("table_").toIntOrNull()
                            val count = (value as? Long)?.toInt()
                            if (tableNumber != null && count != null) {
                                peopleCount[tableNumber] = count
                            }
                        }
                    }

                    val historyData = mutableMapOf<String, Any>(
                        "reservationID" to reservationId,
                        "timestamp" to timestamp,
                        "billingTime" to billingTime,
                        "isPaid" to true
                    )

                    for ((tableNum, count) in peopleCount) {
                        historyData["table_$tableNum"] = count
                    }
                    for ((tableNum, seatList) in seats) {
                        historyData["table_${tableNum}_seats"] = seatList
                    }

                    historyCollectionRef.document(reservationId).set(historyData)
                        .addOnSuccessListener {
                            reservationDocRef.delete()
                                .addOnSuccessListener {
                                    _reservations.value = _reservations.value.filterNot { it.id == reservationId }
                                    fetchReservations()
                                }
                                .addOnFailureListener { error ->
                                    Log.e("Reservation", "Failed to delete original reservation", error)
                                }
                        }
                        .addOnFailureListener { error ->
                            Log.e("Reservation", "Failed to save to history", error)
                        }
                }
            }

    }

    fun deleteReservation(reservationId: String, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        val uid = auth.currentUser?.uid ?: return
        val reservationRef = db.collection("business_users")
            .document(uid)
            .collection("reservations")
            .document(reservationId)

        reservationRef.delete()
            .addOnSuccessListener {
                Log.d("DeleteReservation", "Reservation $reservationId deleted successfully")
                _reservations.value = _reservations.value.filterNot { it.id == reservationId }
                fetchReservations() // Refresh the state after deletion
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("DeleteReservation", "Failed to delete reservation $reservationId", e)
                onFailure(e)
            }
    }

    fun getActiveReservationById(id: String): Reservation? =
        activeReservations.value.find { it.id == id }

    fun getHistoryRecordById(id: String): HistoryRecord? =
        historyReservations.value.find { it.reservationID == id }


}
