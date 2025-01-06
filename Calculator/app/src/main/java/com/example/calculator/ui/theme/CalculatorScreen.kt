import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import com.example.calculator.R
import java.math.BigDecimal
import java.math.RoundingMode

// Global state for calculator
var calculatorText by mutableStateOf("0")
var lastNumber by mutableStateOf(BigDecimal.ZERO)
var currNumber by mutableStateOf(BigDecimal.ZERO)
var currSymbol by mutableStateOf("")
var isNewCalculation by mutableStateOf(false)

fun HandleInput(input: String) {
    when (input) {
        "⌫", "±", "%", "÷", "×", "-", "+", "=", ",", "C" -> HandleSymbols(input)
        in "0".."9" -> HandleNumbers(input)
    }
}

fun HandleNumbers(input: String) {
    if (isNewCalculation) {
        currSymbol = ""
        calculatorText = if (calculatorText == "0") input else calculatorText + input
        isNewCalculation = false
    } else {
        if (calculatorText == "0") calculatorText = input
        else if (calculatorText.contains(",")) calculatorText += input
        else calculatorText += input
    }
}

fun HandleSymbols(input: String) {
    if (calculatorText == "0" && input !in listOf("C", ",")) return

    when (input) {
        "C" -> {
            calculatorText = "0"
            lastNumber = BigDecimal.ZERO
            currNumber = BigDecimal.ZERO
            currSymbol = ""
            isNewCalculation = false
        }
        "⌫" -> calculatorText = if (calculatorText.length > 1) calculatorText.dropLast(1) else "0"
        "," -> if (!calculatorText.contains(",")) calculatorText += ","
        "±" -> calculatorText = if (calculatorText.startsWith("-")) calculatorText.removePrefix("-") else "-$calculatorText"
        "=" -> {
            if (currSymbol.isNotEmpty()) FinishCurrent()
            isNewCalculation = true
        }
        else -> {
            if (currSymbol.isNotEmpty()) FinishCurrent()
            currSymbol = input
            lastNumber = calculatorText.replace(",", ".").toBigDecimal()
            calculatorText = "0"
            isNewCalculation = false
        }
    }
}

fun FinishCurrent() {
    currNumber = calculatorText.replace(",", ".").toBigDecimal()
    lastNumber = when (currSymbol) {
        "÷" -> lastNumber.divide(currNumber, 10, RoundingMode.HALF_UP)
        "×" -> lastNumber.multiply(currNumber)
        "-" -> lastNumber.subtract(currNumber)
        "+" -> lastNumber.add(currNumber)
        "%" -> lastNumber.remainder(currNumber)
        else -> lastNumber
    }
    calculatorText = formatBigDecimal(lastNumber)
    currSymbol = ""
}

fun formatBigDecimal(value: BigDecimal): String {
    val strippedValue = value.stripTrailingZeros()
    return if (strippedValue.scale() <= 0) strippedValue.toBigInteger().toString() else strippedValue.toPlainString().replace(".", ",")
}

@Composable
fun CalculatorScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .background(Color.Black)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(end = 16.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = calculatorText,
                fontSize = 80.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }

        val buttons = listOf(
            listOf("⌫", "±", "%", "÷"),
            listOf("1", "2", "3", "×"),
            listOf("4", "5", "6", "-"),
            listOf("7", "8", "9", "+"),
            listOf("C", "0", ",", "=")
        )

        buttons.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { text ->
                    Button(
                        onClick = { HandleInput(text) },
                        shape = CircleShape,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when (text) {
                                "⌫", "±", "%" -> Color(0xFF5C5C5F)
                                in "0".."9", "C", "," -> Color(0xFF2A2A2C)
                                "÷", "×", "-", "+", "=" -> Color(0xFFFF9F0A)
                                else -> Color.DarkGray
                            }
                        )
                    ) {
                        when (text) {
                            "⌫" -> Icon(
                                painter = painterResource(id = R.drawable.backspace),
                                contentDescription = "Backspace",
                                tint = Color.White
                            )
                            "±" -> Icon(
                                painter = painterResource(id = R.drawable.plus_minus_sign),
                                contentDescription = "Plus/Minus",
                                tint = Color.White
                            )
                            "%" -> Icon(
                                painter = painterResource(id = R.drawable.percentage),
                                contentDescription = "Percentage",
                                tint = Color.White,
                                modifier = Modifier.scale(1.75f)
                            )
                            "÷" -> Icon(
                                painter = painterResource(id = R.drawable.divide),
                                contentDescription = "Division",
                                tint = Color.White,
                                modifier = Modifier.scale(2f)
                            )
                            "×" -> Icon(
                                painter = painterResource(id = R.drawable.multiply),
                                contentDescription = "Multiplication",
                                tint = Color.White,
                                modifier = Modifier.scale(2f)
                            )
                            "-" -> Icon(
                                painter = painterResource(id = R.drawable.minus),
                                contentDescription = "Subtraction",
                                tint = Color.White,
                                modifier = Modifier.scale(1.75f)
                            )
                            "+" -> Icon(
                                painter = painterResource(id = R.drawable.plus),
                                contentDescription = "Addition",
                                tint = Color.White,
                                modifier = Modifier.scale(1.5f)
                            )
                            "=" -> Icon(
                                painter = painterResource(id = R.drawable.equals),
                                contentDescription = "Equals",
                                tint = Color.White,
                                modifier = Modifier.scale(1.25f)
                            )
                            "," -> Icon(
                                painter = painterResource(id = R.drawable.comma),
                                contentDescription = "Comma",
                                tint = Color.White,
                                modifier = Modifier.scale(2f).padding(bottom = 10.dp)
                            )
                            else -> Text(
                                text = text,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.W300,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorScreenPreview() {
    CalculatorScreen()
}
