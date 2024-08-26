package gt.edu.uvg.lab4

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gt.edu.uvg.lab4.ui.theme.Lab4Theme
import kotlin.math.pow
import kotlin.math.sqrt


val buttonList = listOf("(", " / ", " * ", ")", "7", "8", "9", " - ", "4", "5", "6", " + ", "1", "2", "3", "√ ", ".", "0", " ^ ", "=")
val two_operands_operators = listOf<String>("+", "-", "*", "/", "^")
val one_operand_operators = listOf<String>("√")
var previous = ""

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab4Theme {

                }
            }
        }
}


@Composable
fun CalcButton(symbol: String, modifier: Modifier = Modifier, containerColor: Color, contentColor: Color, onClickedValue: () -> Unit){
    Button (
        modifier = modifier
            .padding(start = 5.dp, end = 5.dp),
        shape = RoundedCornerShape(5),
        colors = ButtonDefaults.buttonColors(containerColor, contentColor),
        onClick =  onClickedValue ) {
        Text(
            text = symbol
        )
    }
}

@Composable
fun ButtonGrid(modifier: Modifier = Modifier, columns: Int, buttons: List<String>, editOperationFunc: (text: String) -> Unit, showResultFunc: () -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        itemsIndexed(buttons){
            index, button -> CalcButton(
            symbol = button,
            containerColor = if((index +1) % 4 == 0) Color(0xFF33939C) else MaterialTheme.colorScheme.inversePrimary,
            contentColor = Color.Black){if(button == "=") showResultFunc() else editOperationFunc(button)}
        }
    }
}


@Composable
fun Display(modifier: Modifier = Modifier, operation: String, result: String) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.inversePrimary
        ){
        Column(
            Modifier.width(270.dp),
            horizontalAlignment = Alignment.End,
            ){
            Text(text = operation, Modifier.padding(top = 5.dp, end = 5.dp), fontSize = 32.sp)
            Text(text = result, Modifier.padding(end=5.dp, bottom = 5.dp), fontSize = 22.sp)
        }
    }
}

@Composable
fun SpecialRow(modifier: Modifier = Modifier, editOperationFunc: (text: String) -> Unit, reset: () -> Unit, delete: () -> Unit) {
    Row {
        CalcButton(symbol = "AC", containerColor = MaterialTheme.colorScheme.inversePrimary, contentColor = Color.Black) {
            reset()
        }
        CalcButton(symbol = "e", containerColor = MaterialTheme.colorScheme.inversePrimary, contentColor = Color.Black) {
            editOperationFunc("e")
        }
        CalcButton(symbol = "DEL", containerColor = MaterialTheme.colorScheme.inversePrimary, contentColor = Color.Black) {
            delete()
        }
    }
}

@Composable
fun Calculator(modifier: Modifier = Modifier) {
    var operation by remember {
        mutableStateOf("")
    }

    var result by remember {
        mutableStateOf("")
    }

    fun editOperation(str: String = "1"){
        if(((previous.trim() in two_operands_operators || previous == "") && str.trim() in two_operands_operators) || ((previous.trim() in one_operand_operators) && str.trim() in one_operand_operators)){
        }else if(previous == "ERASE" && str.trim() !in two_operands_operators){
            operation = str
        }else if(str.trim() in one_operand_operators){
            if (previous == "" || previous.trim() == "("){
                operation = operation + str
            }
            else if(previous.trim() in two_operands_operators){
                operation = operation + str
            }else{
                operation = operation + " * " + str
            }
        }else{
            operation = operation + str

        }
        previous = str

    }

    fun showResult(){

        if(operation == ""){

        }else{
            result = operate(operation)
        }


        if (result == "ERROR" || result == "Infinity"){

            previous = ""
        }else{
            previous = "ERASE"
        }

        operation = ""

    }

    fun delete(){
        if(operation != ""){
            operation = operation.substring(0, operation.length -1)
        }
    }

    fun reset(){
        operation = ""
        result = ""
    }

    Surface (
        color = Color(0xFFEFEAD7),
        modifier = modifier.size(width = 290.dp, height = 440.dp),
        shape = RoundedCornerShape(10.dp)){
        Column (horizontalAlignment = Alignment.CenterHorizontally) {
            Display(modifier=modifier.padding(top = 15.dp, bottom = 5.dp), operation = operation, result = result)
            SpecialRow(editOperationFunc = {editOperation(it)}, reset = { reset()}, delete = {delete()})
            ButtonGrid(columns = 4, buttons = buttonList, editOperationFunc = {editOperation(it)}, showResultFunc = {showResult()})
        }
    }

}
@Preview(showBackground = true)
@Composable
fun ButtonGridPreview() {
    Lab4Theme {
        ButtonGrid(columns = 4, buttons = buttonList, editOperationFunc = {}, showResultFunc = {})
    }
}

@Preview
@Composable
private fun DisplayPreview() {
    Display(operation = "0", result = "0")
}

@Preview
@Composable
private fun CalculatorPreview() {
    Calculator()
}

//--------------------------------------------------------------------------------------------------------------------








fun operate(operation: String): String{
    var validator = ExpressionValidator();
    if(!validator.validateCharacters(operation)){
        return "ERROR"
    }
    if(!validator.validateParenthesis(operation)){
        return "ERROR"
    }

    var converter = ExpressionConverter()
    var postfix = converter.infixToPostfix(operation)

    var calculator = PostfixCalculator()
    return calculator.calculate(postfix).toString()

}




class ExpressionConverter {
    fun infixToPostfix(expression: String): MutableList<String> {
        val stack = mutableListOf<Char>();
        val postfix = mutableListOf<Char>();
        val numbersOrSpace =
            listOf<Char>('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'e', ' ', '.')


        for (char in expression) {
            when (char) {
                in numbersOrSpace -> postfix.add(char)
                '(' -> stack.add(char)
                '√' -> stack.add(char)
                '^' -> stack.add(char)
                ')' -> {
                    while (stack.isNotEmpty() && stack.last() != '(') {
                        postfix.add(stack.removeLast())
                    }
                    stack.removeLast()
                }

                else -> {
                    while (stack.isNotEmpty() && getPrecedence(char) <= getPrecedence(stack.last())) {
                        postfix.add(stack.removeLast())

                    }
                    stack.add(char)
                }
            }

        }

        while (stack.isNotEmpty()) {
            postfix.add(stack.removeLast())

        }

        return getPostFixList(postfix.joinToString(""));
    }

    private fun getPostFixList(postfix: String): MutableList<String> {
        val operators = listOf<Char>('+', '-', '*', '/', '^', '√')
        val postfixList = mutableListOf<String>()
        var current = ""
        for (char in postfix) {
            if (char in operators) {
                if (current != "") {
                    postfixList.add(current)
                    current = ""
                }
                postfixList.add(char.toString())
            } else if (char != ' ') {
                current = current + char
            } else if (current != "") {
                postfixList.add(current)
                current = ""
            }


        }
        if (postfixList.isEmpty()){
            postfixList.add(current)
        }
        return postfixList;
    }

    private fun getPrecedence(char: Char): Int {
        return when (char) {
            '+' -> 1;
            '-' -> 1;
            '*' -> 2;
            '/' -> 2;
            '^' -> 3;
            '√' -> 3;
            else -> 0;
        }
    }
}


class ExpressionValidator {
    fun validateCharacters(expression: String): Boolean {
        val regex = Regex("[^\\d +\\-/*^e√()\\[\\].]")
        return !regex.containsMatchIn(expression);
    }

    fun validateParenthesis(expression: String): Boolean {
        val stack = mutableListOf<Char>();
        for (ch in expression) {
            if (ch == '(') {
                stack.add(ch);
            } else if (ch == ')')
                if (stack.isNotEmpty()) {
                    stack.removeLast()
                } else {
                    return false;
                }
        }

        return stack.isEmpty();
    }
}


class PostfixCalculator {
    fun calculate(postfixExpression: MutableList<String>): Double {

        val stack = mutableListOf<String>()
        val operators = listOf<String>("+", "-", "*", "/", "^", "√")
        var current = ""
        var operand1 = ""
        var operand2 = ""
        while (postfixExpression.isNotEmpty()) {
            current = postfixExpression.removeFirst()
            if (current == "e") {
                current = " " + Math.E.toString()
            }
            if (current !in operators) {
                stack.add(current)
            } else if (current == "√") {
                operand1 = stack.removeLast()
                stack.add(getResult(operand1, operand2, current).toString())
            } else {
                operand1 = stack.removeLast()
                operand2 = stack.removeLast()
                stack.add(getResult(operand1, operand2, current).toString())
            }
        }
        if(stack.isEmpty()){
            return current.toDouble()
        }else{
            return stack.removeLast().toDouble()
        }

    }

    fun getResult(operand1: String, operand2: String, operator: String): Double {
        return when (operator) {
            "+" -> operand1.toDouble().plus(operand2.toDouble())
            "-" -> operand2.toDouble().minus(operand1.toDouble())
            "*" -> operand1.toDouble() * operand2.toDouble()
            "/" -> operand2.toDouble().div(operand1.toDouble())
            "^" -> operand2.toDouble().pow(operand1.toDouble())
            "√" -> sqrt(operand1.toDouble())
            else -> 0.0

        }
    }
}


