package com.abhijitsaha.goodine

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class BusinessAuthViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val restaurantCollection = db.collection("business_users")

    var errorMessage: String? = null
    var successMessage: String? = null
    var currentRestaurant by mutableStateOf<Restaurant?>(null)
        private set

    fun signUp(
        name: String,
        type: String,
        city: String,
        address: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Create user with Firebase Auth
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val userId = authResult.user?.uid ?: throw Exception("User ID not found")

                val restaurant = Restaurant(
                    id = userId,
                    ownerName = "",
                    name = name,
                    type = type,
                    city = city,
                    state = "",
                    address = address,
                    zipcode = "",
                    averageCost = "",
                    openingTime = Date(),
                    closingTime = Date(),
                    imageUrls = listOf(),
                    currency = "INR",
                    currencySymbol = "₹"
                )

                restaurantCollection.document(userId).set(restaurant).await()
                successMessage = "Restaurant registered successfully"
                onSuccess()
            } catch (e: Exception) {
                errorMessage = e.message
                onFailure(e.message ?: "Unknown error during sign-up")
            }
        }
    }

    fun signIn(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                successMessage = "Sign in successful"
                onSuccess()
            } catch (e: Exception) {
                errorMessage = e.message
                onFailure(e.message ?: "Failed to sign in")
            }
        }
    }

    fun signOut() {
        auth.signOut()
        successMessage = "Signed out successfully"
    }

    fun fetchCurrentRestaurant(onError: (String) -> Unit = {}) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val snapshot = restaurantCollection.document(userId).get().await()
                currentRestaurant = snapshot.toObject(Restaurant::class.java)
            } catch (e: Exception) {
                onError(e.message ?: "Unable to fetch restaurant details")
            }
        }
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}
