package com.example.myapplication; // Package declaration

import android.graphics.Color; // Import for color manipulation
import android.os.Bundle; // Import for activity lifecycle management
import android.util.Log; // Import for logging
import android.view.View; // Import for view handling
import android.widget.Button; // Import for button widget
import android.widget.EditText; // Import for edit text widget
import android.widget.TextView; // Import for text view widget
import androidx.appcompat.app.AppCompatActivity; // Import for app compatibility

import java.util.ArrayList; // Import for array list
import java.util.Collections; // Import for collections utility
import java.util.List; // Import for list interface
import java.util.Stack; // Import for stack data structure
import java.util.regex.Matcher; // Import for regex matching
import java.util.regex.Pattern; // Import for regex pattern

public class MainActivity extends AppCompatActivity implements View.OnClickListener { // Main activity class

    private EditText inputExpression; // EditText for user input
    private TextView resultTextView; // TextView for displaying results

    @Override
    protected void onCreate(Bundle savedInstanceState) { // onCreate method
        super.onCreate(savedInstanceState); // Call to superclass method
        setContentView(R.layout.activity_main); // Set the layout for the activity

        inputExpression = findViewById(R.id.inputExpression); // Initialize input field
        resultTextView = findViewById(R.id.resultTextView); // Initialize result display
        setupCalculatorButtons(); // Setup button listeners
    }

    private void setupCalculatorButtons() { // Method to setup button listeners
        int[] buttonIds = new int[]{ // Array of button IDs
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
            R.id.btnDot, R.id.btnPlus, R.id.btnMinus, R.id.btnMultiply,
            R.id.btnDivide, R.id.btnPower, R.id.btnParenLeft, R.id.btnParenRight,
            R.id.btnClear, R.id.btnDelete, R.id.btnEquals, R.id.btnSpace
        };
        
        for (int id : buttonIds) { // Loop through button IDs
            setButtonClickListener(id); // Set click listener for each button
        }
    }

    private void setButtonClickListener(int buttonId) { // Method to set click listener
        findViewById(buttonId).setOnClickListener(this); // Set this class as listener
    }

    @Override
    public void onClick(View view) { // Handle button clicks
        if (view instanceof Button) { // Check if view is a button
            Button button = (Button) view; // Cast view to button
            String buttonText = button.getText().toString(); // Get button text

            switch (view.getId()) { // Switch based on button ID
                case R.id.btnEquals: // Equals button
                    calculateResult(); // Calculate result
                    break;
                case R.id.btnClear: // Clear button
                    inputExpression.setText(""); // Clear input
                    resultTextView.setText(""); // Clear result
                    break;
                case R.id.btnDelete: // Delete button
                    String currentText = inputExpression.getText().toString(); // Get current text
                    if (!currentText.isEmpty()) { // Check if text is not empty
                        inputExpression.setText(currentText.substring(0, currentText.length() - 1)); // Remove last character
                    }
                    break;
                default: // Default case for other buttons
                    handleSpecialCharacters(buttonText); // Handle special characters
            }
        }
    }

    private void handleSpecialCharacters(String buttonText) { // Handle special characters
        String processedText = buttonText
            .replace("×", "*") // Replace multiplication symbol
            .replace("÷", "/") // Replace division symbol
            .replace("^2", "^") // Replace power symbol
            .replace("Space", " "); // Replace space text
        inputExpression.append(processedText); // Append processed text to input
    }

    private void calculateResult() { // Calculate the result of the expression
        try {
            String expression = inputExpression.getText().toString().trim(); // Get and trim input
            if (expression.isEmpty()) { // Check if input is empty
                showError("Please enter an expression"); // Show error message
                return; // Exit method
            }

            NotationType notation = detectNotationType(expression); // Detect notation type
            
            // Pre-process expression to handle negative numbers correctly
            if (notation == NotationType.INFIX) { // Check if notation is infix
                expression = expression.replace("(-", "(0-"); // Replace negative sign
                if (expression.startsWith("-")) { // Check if expression starts with negative
                    expression = "0" + expression; // Prepend zero
                }
            }
            
            // Insert spaces between tokens for infix notation
            expression = insertSpaces(expression); // Insert spaces
            
            // Log the expression before evaluation
            Log.d("CalculateResult", "Expression with spaces: " + expression); // Log expression
            
            double result = ExpressionEvaluator.evaluate(expression, notation); // Evaluate expression
            String formattedExpression = addParentheses(expression, notation); // Format expression
            
            displayResult(notation, formattedExpression, result); // Display result
        } catch (Exception e) { // Catch exceptions
            showError("Invalid expression: " + e.getMessage()); // Show error message
        }
    }

    private String insertSpaces(String expression) { // Insert spaces in expression
        return expression.replaceAll("(?<=[-+*/^()])|(?=[-+*/^()])", " "); // Regex to add spaces
    }

    private String addParentheses(String expression, NotationType notation) { // Add parentheses to expression
        try {
            List<String> tokens = tokenizeExpression(expression); // Tokenize expression
            if (tokens.isEmpty()) return expression; // Return if no tokens

            switch (notation) { // Switch based on notation type
                case INFIX:
                    return processInfixExpression(tokens); // Process infix
                case POSTFIX:
                    return postfixToParenthesizedInfix(tokens); // Process postfix
                case PREFIX:
                    return prefixToParenthesizedInfix(tokens); // Process prefix
                default:
                    return expression; // Default return
            }
        } catch (Exception e) { // Catch exceptions
            return expression; // Return original expression
        }
    }

    private String processInfixExpression(List<String> tokens) { // Process infix expression
        List<String> postfix = infixToPostfix(tokens); // Convert to postfix
        return postfixToParenthesizedInfix(postfix); // Convert to parenthesized infix
    }

    private String postfixToParenthesizedInfix(List<String> postfix) { // Convert postfix to parenthesized infix
        Stack<String> stack = new Stack<>(); // Stack for processing
        
        for (String token : postfix) { // Iterate over tokens
            if (isOperator(token)) { // Check if token is operator
                if (stack.size() < 2) { // Check stack size
                    throw new IllegalArgumentException("Invalid expression"); // Throw exception
                }
                String right = stack.pop(); // Pop right operand
                String left = stack.pop(); // Pop left operand
                stack.push("(" + left + " " + token + " " + right + ")"); // Push combined expression
            } else {
                stack.push(token); // Push number
            }
        }
        
        return stack.isEmpty() ? "" : stack.pop(); // Return final expression
    }

    private String prefixToParenthesizedInfix(List<String> tokens) { // Convert prefix to parenthesized infix
        Stack<String> stack = new Stack<>(); // Stack for processing
        
        // Iterate over tokens in reverse order for prefix processing
        for (int i = tokens.size() - 1; i >= 0; i--) {
            String token = tokens.get(i);
            
            if (isOperator(token)) { // Check if token is operator
                if (stack.size() < 2) { // Check stack size
                    throw new IllegalArgumentException("Invalid expression"); // Throw exception
                }
                String left = stack.pop(); // Pop left operand
                String right = stack.pop(); // Pop right operand
                stack.push("(" + left + " " + token + " " + right + ")"); // Push combined expression
            } else {
                stack.push(token); // Push number
            }
        }
        
        return stack.isEmpty() ? "" : stack.pop(); // Return final expression
    }

    private List<String> tokenizeExpression(String expression) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = Pattern.compile("-?\\d*\\.?\\d+|[-+*/()^]").matcher(expression);
        while (matcher.find()) {
            String token = matcher.group().trim();
            if (!token.isEmpty()) tokens.add(token);
        }
        return tokens;
    }

    private static List<String> infixToPostfix(List<String> tokens) {
        Stack<String> stack = new Stack<>();
        List<String> output = new ArrayList<>();

        for (String token : tokens) {
            if (isNumber(token)) {
                output.add(token);
            } else if ("(".equals(token)) {
                stack.push(token);
            } else if (")".equals(token)) {
                while (!stack.isEmpty() && !"(".equals(stack.peek())) {
                    output.add(stack.pop());
                }
                if (!stack.isEmpty()) stack.pop();
            } else if (isOperator(token)) {
                while (!stack.isEmpty() && !"(".equals(stack.peek()) && 
                       hasHigherPrecedence(stack.peek(), token)) {
                    output.add(stack.pop());
                }
                stack.push(token);
            }
        }

        while (!stack.isEmpty()) {
            output.add(stack.pop());
        }

        return output;
    }

    private static boolean isNumber(String token) {
        return token.matches("-?\\d+(\\.\\d+)?");
    }

    private static boolean isOperator(String token) {
        return "+-*/^".contains(token);
    }

    private static boolean hasHigherPrecedence(String op1, String op2) {
        int p1 = getPrecedence(op1);
        int p2 = getPrecedence(op2);
        return p1 > p2 || (p1 == p2 && isLeftAssociative(op1));
    }

    private static int getPrecedence(String op) {
        switch (op) {
            case "+":
            case "-": return 1;
            case "*":
            case "/": return 2;
            case "^": return 3;
            default: return 0;
        }
    }

    private static boolean isLeftAssociative(String op) {
        return !"^".equals(op);
    }

    private NotationType detectNotationType(String expression) { // Detect notation type
        List<String> tokens = tokenizeExpression(expression); // Tokenize expression
        if (tokens.isEmpty()) return NotationType.INFIX; // Return infix if no tokens
        
        // Check first and last tokens
        String firstToken = tokens.get(0); // Get first token
        String lastToken = tokens.get(tokens.size() - 1); // Get last token
        
        // If first token is an operator (except minus for negative numbers)
        if (isOperator(firstToken) && !firstToken.equals("-")) { // Check if first token is operator and not minus
            return NotationType.PREFIX; // Return prefix
        }
        // If last token is an operator
        else if (isOperator(lastToken)) { // Check if last token is operator
            return NotationType.POSTFIX; // Return postfix
        }
        // Default to infix
        return NotationType.INFIX; // Return infix
    }

    private void displayResult(NotationType notation, String expression, double result) { // Display result
        String formatted = String.format("Type: %s\n %s = %.2f", // Format result string
                notation.toString(), expression, result); // Include notation type, expression, and result
        resultTextView.setTextColor(Color.parseColor("#212121")); // Set text color
        resultTextView.setText(formatted); // Set text in result view
    }

    private void showError(String message) { // Show error message
        resultTextView.setTextColor(Color.RED); // Set text color to red
        resultTextView.setText(message); // Set error message in result view
    }

    // Inner enum for notation types
    enum NotationType { // Enum for notation types
        INFIX, PREFIX, POSTFIX // Define notation types
    }

    // Mock evaluator class (should be implemented separately)
    static class ExpressionEvaluator { // Static class for expression evaluation
        static double evaluate(String expr, NotationType type) { // Evaluate expression
            try {
                List<String> tokens = tokenizeExpression(expr); // Tokenize expression
                switch (type) { // Switch based on notation type
                    case INFIX:
                        return evaluatePostfix(infixToPostfix(tokens)); // Evaluate infix
                    case POSTFIX:
                        return evaluatePostfix(tokens); // Evaluate postfix
                    case PREFIX:
                        Collections.reverse(tokens); // Reverse tokens for prefix
                        return evaluatePostfix(prefixToPostfix(tokens)); // Evaluate prefix
                    default:
                        return 0.0; // Default return value
                }
            } catch (Exception e) { // Catch exceptions
                throw new IllegalArgumentException(""); // Throw exception
            }
        }

        private static List<String> tokenizeExpression(String expression) {
            List<String> tokens = new ArrayList<>();
            Matcher matcher = Pattern.compile("-?\\d*\\.?\\d+|[-+*/()^]").matcher(expression);
            while (matcher.find()) {
                String token = matcher.group().trim();
                if (!token.isEmpty()) tokens.add(token);
            }
            return tokens;
        }

        private static List<String> prefixToPostfix(List<String> prefix) {
            Stack<List<String>> stack = new Stack<>();
            
            for (String token : prefix) {
                if (isOperator(token)) {
                    if (stack.size() < 2) {
                        throw new IllegalArgumentException("Invalid prefix: not enough operands for " + token);
                    }
                    List<String> op1 = stack.pop(); // Right operand
                    List<String> op2 = stack.pop(); // Left operand
                    List<String> temp = new ArrayList<>();
                    temp.addAll(op2);
                    temp.addAll(op1);
                    temp.add(token);
                    stack.push(temp);
                } else {
                    List<String> temp = new ArrayList<>();
                    temp.add(token);
                    stack.push(temp);
                }
            }
            
            if (stack.size() != 1) {
                throw new IllegalArgumentException("Invalid prefix: unbalanced expression");
            }
            return stack.pop();
        }

        private static double evaluatePostfix(List<String> postfix) { // Evaluate postfix expression
            Stack<Double> stack = new Stack<>(); // Stack for evaluation
            
            for (String token : postfix) { // Iterate over tokens
                if (isNumber(token)) { // Check if token is number
                    stack.push(Double.parseDouble(token)); // Push number to stack
                } else if (isOperator(token)) { // Check if token is operator
                    double b = stack.pop(); // Pop second operand
                    double a = stack.pop(); // Pop first operand
                    stack.push(applyOperator(token, a, b)); // Apply operator and push result
                }
            }
            
            return stack.pop(); // Return final result
        }

        private static double applyOperator(String operator, double a, double b) { // Apply operator
            switch (operator) { // Switch based on operator
                case "+": return a + b; // Addition
                case "-": return a - b; // Subtraction
                case "*": return a * b; // Multiplication
                case "/": 
                    if (b == 0) throw new ArithmeticException("Division by zero"); // Check for division by zero
                    return a / b; // Division
                case "^": return Math.pow(a, b); // Exponentiation
                default: throw new IllegalArgumentException("Unknown operator: " + operator); // Throw exception for unknown operator
            }
        }

        private static boolean isNumber(String token) {
            return token.matches("-?\\d+(\\.\\d+)?");
        }

        private static boolean isOperator(String token) {
            return "+-*/^".contains(token);
        }

        private static boolean hasHigherPrecedence(String op1, String op2) {
            int p1 = getPrecedence(op1);
            int p2 = getPrecedence(op2);
            return p1 > p2 || (p1 == p2 && isLeftAssociative(op1));
        }

        private static int getPrecedence(String op) {
            switch (op) {
                case "+":
                case "-": return 1;
                case "*":
                case "/": return 2;
                case "^": return 3;
                default: return 0;
            }
        }
    }
}