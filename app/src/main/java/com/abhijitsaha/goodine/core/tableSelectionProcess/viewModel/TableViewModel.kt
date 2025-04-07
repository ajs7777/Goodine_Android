package com.abhijitsaha.goodine.core.tableSelectionProcess.viewModel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TableViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userID = auth.currentUser?.uid

    private val _rows = MutableStateFlow(3)
    val rows: StateFlow<Int> = _rows

    private val _columns = MutableStateFlow(2)
    val columns: StateFlow<Int> = _columns

    fun updateRows(value: Int) {
        _rows.value = value
    }

    fun updateColumns(value: Int) {
        _columns.value = value
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
        }
    }

    fun fetchTableLayout() {
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
                }
        }
    }
}
