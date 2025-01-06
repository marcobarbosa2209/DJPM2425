package ipca.examples.helloworld

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ipca.examples.helloworld.ui.theme.HelloWorldTheme
import ipca.examples.helloworld.ui.theme.Pink80

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting(modifier: Modifier = Modifier) {

    var name by remember { mutableStateOf("") }
    var greet by remember { mutableStateOf("") }

    // camel case name
    val camelCaseName = name.split(" ").joinToString(" ") { word ->
        word.lowercase().replaceFirstChar { it.uppercaseChar() }
    }

    val buttonClick: () -> Unit = { greet = "Hello $camelCaseName!" }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Enter Name") },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White,
                focusedIndicatorColor = Pink80,
                unfocusedIndicatorColor = Color.Gray
            ),
            shape = MaterialTheme.shapes.medium // Rounded corners
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row {
            Button(
                onClick = buttonClick,
                colors = ButtonDefaults.buttonColors(containerColor = Pink80),
                content = { Text(text = "Greet") }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = { greet = "Olá $camelCaseName!" },
                colors = ButtonDefaults.buttonColors(containerColor = Pink80),
                content = { Text(text = "Cumprimentar") }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = greet
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HelloWorldTheme {
        Greeting()
    }
}