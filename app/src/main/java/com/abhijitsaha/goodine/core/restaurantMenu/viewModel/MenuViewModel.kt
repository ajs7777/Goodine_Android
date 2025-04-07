package com.abhijitsaha.goodine.core.restaurantMenu.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhijitsaha.goodine.models.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class MenuViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance().reference

    private val _menuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuItems: StateFlow<List<MenuItem>> = _menuItems

    private var menuListener: ListenerRegistration? = null

    private val _selectedMenuItem = MutableStateFlow<MenuItem?>(null)
    val selectedMenuItem: StateFlow<MenuItem?> = _selectedMenuItem

    init {
        fetchMenuItems()
    }

    fun selectMenuItem(item: MenuItem) {
        _selectedMenuItem.value = item
    }

    fun clearSelectedMenuItem() {
        _selectedMenuItem.value = null
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun fetchMenuItems() {
        val userId = getCurrentUserId()
        if (!isUserLoggedIn() || userId.isNullOrBlank()) {
            println("Error: User is not logged in. Cannot fetch menu items.")
            return
        }

        viewModelScope.launch {
            menuListener?.remove()

            menuListener = firestore.collection("business_users")
                .document(userId)
                .collection("menu")
                .orderBy("name") // Order menu items alphabetically by name
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        println("Firestore error: ${e.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        _menuItems.value = snapshot.documents.mapNotNull { doc ->
                            val id = doc.id
                            val name = doc.getString("name") ?: "Unknown"
                            val description = doc.getString("description") ?: ""
                            val price = doc.getDouble("price") ?: 0.0
                            val imageUrl = doc.getString("imageUrl") ?: ""

                            val isVeg = when (val vegValue = doc.get("isVeg")) {
                                is Boolean -> vegValue
                                is String -> vegValue.equals("true", ignoreCase = true)
                                else -> false
                            }

                            println("DEBUG: id=$id, name=$name, isVeg=$isVeg") // Debug log

                            MenuItem(
                                id = id,
                                foodname = name,
                                foodDescription = description,
                                foodPrice = price,
                                foodImage = imageUrl,
                                veg = isVeg
                            )
                        }
                    }
                }
        }
    }

    fun updateMenuItem(item: MenuItem, newImageUri: Uri?) {
        val userId = getCurrentUserId()
        if (!isUserLoggedIn() || userId.isNullOrBlank()) {
            println("Error: User is not logged in. Cannot update menu item.")
            return
        }

        viewModelScope.launch {
            val imageUrl = newImageUri?.let { uploadImage(it, item.id) } ?: item.foodImage

            saveItemToFirestore(
                userId = userId,
                id = item.id,
                name = item.foodname,
                description = item.foodDescription,
                price = item.foodPrice,
                isVeg = item.veg,
                imageUrl = imageUrl
            )
        }
    }

    fun saveMenuItem(name: String, description: String, price: Double, isVeg: Boolean, imageUri: Uri?) {
        val userId = getCurrentUserId()
        if (!isUserLoggedIn() || userId.isNullOrBlank()) {
            println("Error: User is not logged in. Cannot save menu item.")
            return
        }

        viewModelScope.launch {
            val menuItemId = UUID.randomUUID().toString()
            val imageUrl = imageUri?.let { uploadImage(it, menuItemId) }

            saveItemToFirestore(userId, menuItemId, name, description, price, isVeg, imageUrl)
        }
    }

    private suspend fun uploadImage(imageUri: Uri, menuItemId: String): String? {
        return try {
            val imageRef = storage.child("menu_images/$menuItemId.jpg") // **Fixed path issue**
            imageRef.putFile(imageUri).await()
            imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            println("Image upload failed: ${e.message}")
            null
        }
    }

    private fun saveItemToFirestore(userId: String, id: String, name: String, description: String, price: Double, isVeg: Boolean, imageUrl: String?) {
        val menuItem = hashMapOf(
            "id" to id,
            "name" to name,
            "description" to description,
            "price" to price,
            "imageUrl" to imageUrl,
            "isVeg" to isVeg
        )

        firestore.collection("business_users")
            .document(userId)
            .collection("menu")
            .document(id)
            .set(menuItem)
            .addOnSuccessListener {
                println("Menu item saved: $name (isVeg: $isVeg)")
                fetchMenuItems() // **Refresh list after saving**
            }
            .addOnFailureListener { e ->
                println("Failed to save menu item: ${e.message}")
            }
    }

    fun deleteMenuItem(itemId: String) {
        val userId = getCurrentUserId()
        if (!isUserLoggedIn() || userId.isNullOrBlank()) {
            println("Error: User is not logged in. Cannot delete menu item.")
            return
        }

        viewModelScope.launch {
            try {
                firestore.collection("business_users")
                    .document(userId)
                    .collection("menu")
                    .document(itemId)
                    .delete()
                    .await()

                _menuItems.value = _menuItems.value.filterNot { it.id == itemId }
            } catch (e: Exception) {
                println("Failed to delete menu item: ${e.message}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        menuListener?.remove() // Clean up Firestore listener
    }
}
