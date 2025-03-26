package com.abhijitsaha.goodine

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class BusinessAuthViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val restaurantCollection = db.collection("business_users")

    var errorMessage: String? = null
    var successMessage: String? = null
    var currentRestaurant by mutableStateOf<Restaurant?>(null)
        private set
    var isLoggedIn by mutableStateOf(auth.currentUser != null)
        private set
    var isLoading by mutableStateOf(false)
        private set

    var isUploadingImage by mutableStateOf(false)
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
                isLoggedIn = true
                successMessage = "Sign in successful"
                onSuccess()
            } catch (e: Exception) {
                errorMessage = e.message
                onFailure(e.message ?: "Failed to sign in")
            }
        }
    }


    fun signOut() {
        viewModelScope.launch {
            isLoading = true
            try {
                auth.signOut()
                currentRestaurant = null
                isLoggedIn = false
                successMessage = "Signed out successfully"
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }


    fun fetchCurrentRestaurant(onError: (String) -> Unit = {}) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            isLoading = true
            try {
                val snapshot = restaurantCollection.document(userId).get().await()
                currentRestaurant = snapshot.toObject(Restaurant::class.java)
            } catch (e: Exception) {
                onError(e.message ?: "Unable to fetch restaurant details")
            }finally {
                isLoading = false
            }
        }
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun updateRestaurant(
        restaurant: Restaurant,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                // If the restaurant has an ID, update it. Otherwise, generate a new doc.
                val docRef = if (restaurant.id.isNotEmpty()) {
                    restaurantCollection.document(restaurant.id)
                } else {
                    restaurantCollection.document()
                }

                val restaurantWithId = restaurant.copy(id = docRef.id)
                docRef.set(restaurantWithId).await()

                // ✅ Fetch the latest data and update local state
                val updatedSnapshot = docRef.get().await()
                currentRestaurant = updatedSnapshot.toObject(Restaurant::class.java)

                Log.d("RestaurantViewModel", "Restaurant updated: ${docRef.id}")
                onSuccess()
            } catch (e: Exception) {
                Log.e("RestaurantViewModel", "Error updating restaurant: ${e.localizedMessage}")
                onError(e)
            }
        }
    }

    suspend fun uploadImageToFirebase(uri: Uri): String = suspendCoroutine { continuation ->
        isUploadingImage = true
        val storageRef = Firebase.storage.reference
        val imageRef = storageRef.child("business_users/${UUID.randomUUID()}.jpg")

        imageRef.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                imageRef.downloadUrl
            }
            .addOnSuccessListener { downloadUrl ->
                isUploadingImage = false
                continuation.resume(downloadUrl.toString())
            }
            .addOnFailureListener { exception ->
                isUploadingImage = false
                continuation.resumeWithException(exception)
            }
    }

    suspend fun deleteImageFromFirebase(imageUrl: String): Boolean = suspendCoroutine { continuation ->
        try {
            val storageRef = Firebase.storage.getReferenceFromUrl(imageUrl)

            storageRef.delete()
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        } catch (e: Exception) {
            continuation.resumeWithException(e)
        }
    }


}
