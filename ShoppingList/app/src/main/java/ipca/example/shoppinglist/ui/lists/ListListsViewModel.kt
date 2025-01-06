package ipca.example.shoppinglist.ui.lists

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth // **Correct Import**
import com.google.firebase.firestore.ktx.firestore // **Correct Import**
import com.google.firebase.ktx.Firebase // **Correct Import**
import ipca.example.shoppinglist.TAG
import ipca.example.shoppinglist.models.ListItems

data class ListListsState(
    val listItemsList: List<ListItems> = arrayListOf(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ListListsViewModel : ViewModel() {

    var state = mutableStateOf(ListListsState())
        private set

    fun getLists() {
        val db = Firebase.firestore
        val auth = Firebase.auth
        val currentUser = auth.currentUser

        if (currentUser == null) {
            state.value = state.value.copy(
                error = "User not logged in",
                isLoading = false
            )
            return
        }

        val userEmail = currentUser.email ?: return

        state.value = state.value.copy(isLoading = true, error = null)

        db.collection("users")
            .document(userEmail)
            .collection("lists")
            .get()
            .addOnSuccessListener { documents ->
                val listItemsList = arrayListOf<ListItems>()
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val listItem = document.toObject(ListItems::class.java)
                    listItem.docId = document.id
                    listItemsList.add(listItem)
                }
                state.value = state.value.copy(
                    listItemsList = listItemsList,
                    isLoading = false
                )
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
                state.value = state.value.copy(
                    error = "Failed to fetch lists. Please try again.",
                    isLoading = false
                )
            }
    }
}