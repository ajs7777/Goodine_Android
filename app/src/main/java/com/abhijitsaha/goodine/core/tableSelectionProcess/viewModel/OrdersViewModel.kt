package com.abhijitsaha.goodine.core.tableSelectionProcess.viewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhijitsaha.goodine.models.MenuItem
import com.abhijitsaha.goodine.models.OrderItem
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.abhijitsaha.goodine.models.Order

class OrdersViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> get() = _orders

    fun saveOrderToFirestore(
        reservationId: String,
        selectedItems: Map<String, Int>,
        menuItems: List<MenuItem>
    ) {
        val userId = auth.currentUser?.uid ?: return

        val orderRef = db.collection("business_users")
            .document(userId)
            .collection("reservations")
            .document(reservationId)
            .collection("orders")
            .document()

        val orderData = mutableMapOf<String, OrderItem>()

        selectedItems.forEach { (itemId, quantity) ->
            val menuItem = menuItems.find { it.id == itemId }
            menuItem?.let {
                orderData[itemId] = OrderItem(
                    name = it.foodname,
                    price = it.foodPrice.toDouble(),
                    quantity = quantity
                )
            }
        }

        val order = Order(
            id = orderRef.id,
            userId = userId,
            items = orderData,
            timestamp = Timestamp.now(),
            status = "pending"
        )

        orderRef.set(order)
            .addOnSuccessListener {
                println("Order successfully saved!")
            }
            .addOnFailureListener {
                println("Error saving order: ${it.localizedMessage}")
            }
    }

    fun fetchOrders(reservationId: String) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("business_users")
            .document(userId)
            .collection("reservations")
            .document(reservationId)
            .collection("orders")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Error fetching orders: ${error.localizedMessage}")
                    return@addSnapshotListener
                }

                val fetchedOrders = snapshot?.documents?.mapNotNull { it.toObject(Order::class.java)
                } ?: emptyList()
                viewModelScope.launch {
                    _orders.emit(fetchedOrders)
                }
            }
    }

    fun decreaseItemQuantity(orderId: String, reservationId: String, itemId: String) {
        val userId = auth.currentUser?.uid ?: return

        val orderRef = db.collection("business_users")
            .document(userId)
            .collection("reservations")
            .document(reservationId)
            .collection("orders")
            .document(orderId)

        orderRef.get().addOnSuccessListener { snapshot ->
            val order = snapshot.toObject(Order::class.java)
            order?.let {
                val updatedItems = it.items.toMutableMap()
                val item = updatedItems[itemId]

                if (item != null) {
                    if (item.quantity > 1) {
                        // 👇 Create a new OrderItem with quantity - 1
                        updatedItems[itemId] = item.copy(quantity = item.quantity - 1)
                    } else {
                        updatedItems.remove(itemId) // Remove the item if quantity hits 0
                    }

                    orderRef.update("items", updatedItems)
                        .addOnSuccessListener {
                            println("Item quantity updated successfully")
                        }
                        .addOnFailureListener { e ->
                            println("Failed to update item quantity: ${e.localizedMessage}")
                        }
                }
            }
        }.addOnFailureListener {
            println("Failed to fetch order: ${it.localizedMessage}")
        }
    }


}
