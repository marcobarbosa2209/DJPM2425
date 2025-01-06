package ipca.example.shoppinglist.ui.items

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType // **Added Import**
import androidx.compose.ui.tooling.preview.Preview // **Added Import**
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ipca.example.shoppinglist.ui.theme.ShoppingListTheme

@Composable
fun AddItemView(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    listId: String,
    onItemAdded: () -> Unit
) {
    val viewModel: AddItemViewModel = viewModel()
    val state = viewModel.state.value

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Add Item to List",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TextField(
                value = state.name,
                onValueChange = { viewModel.onNameChange(it) },
                label = { Text("Item Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            TextField(
                value = state.quantity,
                onValueChange = { viewModel.onQuantityChange(it) },
                label = { Text("Quantity") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Checkbox(
                    checked = state.isBought,
                    onCheckedChange = { viewModel.onIsBoughtChange(it) }
                )
                Text(text = "Bought", style = MaterialTheme.typography.bodyLarge)
            }

            if (state.error != null) {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = {
                    if (state.name.isNotBlank() && state.quantity.isNotBlank()) {
                        viewModel.addItem(listId) {
                            onItemAdded()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Add Item")
                }
            }
        }

        Button(
            modifier = Modifier
                .padding(16.dp)
                .size(64.dp),
            onClick = {
                if (state.name.isNotBlank() && state.quantity.isNotBlank()) {
                    viewModel.addItem(listId) {
                        onItemAdded()
                    }
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Icon",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun AddItemViewPreview() {
    ShoppingListTheme {
        AddItemView(
            listId = "exampleListId",
            onItemAdded = {}
        )
    }
}