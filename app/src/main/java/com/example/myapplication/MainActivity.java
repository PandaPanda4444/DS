package com.example.myapplication; // Package declaration

import android.annotation.SuppressLint;
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

// Main activity class for the calculator app
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Initialize UI components
    private EditText inputExpression;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup input and result display
        inputExpression = findViewById(R.id.inputExpression);
        resultTextView = findViewById(R.id.resultTextView);
        setupCalculatorButtons();
    }

    // Setup button listeners for calculator
    private void setupCalculatorButtons() {
        int[] buttonIds = new int[]{
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
            R.id.btnDot, R.id.btnPlus, R.id.btnMinus, R.id.btnMultiply,
            R.id.btnDivide, R.id.btnPower, R.id.btnParenLeft, R.id.btnParenRight,
            R.id.btnClear, R.id.btnDelete, R.id.btnEquals, R.id.btnSpace
        };
        
        for (int id : buttonIds) {
            setButtonClickListener(id);
        }
    }

    // Set click listener for a button
    private void setButtonClickListener(int buttonId) {
        findViewById(buttonId).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view instanceof Button) {
            Button button = (Button) view;
            String buttonText = button.getText().toString();

            switch (view.getId()) {
                case R.id.btnEquals:
                    calculateResult();
                    break;
                case R.id.btnClear:
                    inputExpression.setText("");
                    resultTextView.setText("");
                    break;
                case R.id.btnDelete:
                    String currentText = inputExpression.getText().toString();
                    if (!currentText.isEmpty()) {
                        inputExpression.setText(currentText.substring(0, currentText.length() - 1));
                    }
                    break;
                default:
                    handleSpecialCharacters(buttonText);
            }
        }
    }

    // Handle special characters in button text
    private void handleSpecialCharacters(String buttonText) {
        String processedText = buttonText
            .replace("×", "*")
            .replace("÷", "/")
            .replace("^2", "^")
            .replace("Space", " ");
        inputExpression.append(processedText);
    }

    // Calculate the result of the expression
    private void calculateResult() {
        try {
            String expression = inputExpression.getText().toString().trim();
            if (expression.isEmpty()) {
                showError("Please enter an expression");
                return;
            }

            NotationType notation = detectNotationType(expression);
            
            // Pre-process expression for infix notation
            if (notation == NotationType.INFIX) {
                expression = expression.replace("(-", "(0-");
                if (expression.startsWith("-")) {
                    expression = "0" + expression;
                }
            }
            
            expression = insertSpaces(expression);
            Log.d("CalculateResult", "Expression with spaces: " + expression);
            
            double result = ExpressionEvaluator.evaluate(expression, notation);
            String formattedExpression = addParentheses(expression, notation);
            
            displayResult(notation, formattedExpression, result);
        } catch (Exception e) {
            showError("Invalid expression: " + e.getMessage());
        }
    }

    // Insert spaces around operators and parentheses
    private String insertSpaces(String expression) {
        return expression.replaceAll("(?<=[-+*/^()])|(?=[-+*/^()])", " ");
    }

    // Add parentheses to the expression based on notation
    private String addParentheses(String expression, NotationType notation) {
        try {
            List<String> tokens = tokenizeExpression(expression);
            if (tokens.isEmpty()) return expression;

            switch (notation) {
                case INFIX:
                    return processInfixExpression(tokens);
                case POSTFIX:
                    return postfixToParenthesizedInfix(tokens);
                case PREFIX:
                    return prefixToParenthesizedInfix(tokens);
                default:
                    return expression;
            }
        } catch (Exception e) {
            return expression;
        }
    }

    // Process infix expression to add parentheses
    private String processInfixExpression(List<String> tokens) {
        List<String> postfix = infixToPostfix(tokens);
        return postfixToParenthesizedInfix(postfix);
    }

    // Convert postfix expression to parenthesized infix
    private String postfixToParenthesizedInfix(List<String> postfix) {
        Stack<String> stack = new Stack<>();
        
        for (String token : postfix) {
            if (isOperator(token)) {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("Invalid expression");
                }
                String right = stack.pop();
                String left = stack.pop();
                stack.push("(" + left + " " + token + " " + right + ")");
            } else {
                stack.push(token);
            }
        }
        
        return stack.isEmpty() ? "" : stack.pop();
    }

    // Convert prefix expression to parenthesized infix
    private String prefixToParenthesizedInfix(List<String> tokens) {
        Stack<String> stack = new Stack<>();
        
        for (int i = tokens.size() - 1; i >= 0; i--) {
            String token = tokens.get(i);
            
            if (isOperator(token)) {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("Invalid expression");
                }
                String left = stack.pop();
                String right = stack.pop();
                stack.push("(" + left + " " + token + " " + right + ")");
            } else {
                stack.push(token);
            }
        }
        
        return stack.isEmpty() ? "" : stack.pop();
    }

    // Tokenize the expression into a list of strings
    private List<String> tokenizeExpression(String expression) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = Pattern.compile("-?\\d*\\.?\\d+|[-+*/()^]").matcher(expression);
        while (matcher.find()) {
            String token = matcher.group().trim();
            if (!token.isEmpty()) tokens.add(token);
        }
        return tokens;
    }

    // Convert infix expression to postfix
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

    // Check if a token is a number
    private static boolean isNumber(String token) {
        return token.matches("-?\\d+(\\.\\d+)?");
    }

    // Check if a token is an operator
    private static boolean isOperator(String token) {
        return "+-*/^".contains(token);
    }

    // Determine if one operator has higher precedence than another
    private static boolean hasHigherPrecedence(String op1, String op2) {
        int p1 = getPrecedence(op1);
        int p2 = getPrecedence(op2);
        return p1 > p2 || (p1 == p2 && isLeftAssociative(op1));
    }

    // Get the precedence of an operator
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

    // Check if an operator is left associative
    private static boolean isLeftAssociative(String op) {
        return !"^".equals(op);
    }

    // Detect the notation type of an expression
    private NotationType detectNotationType(String expression) {
        List<String> tokens = tokenizeExpression(expression);
        if (tokens.isEmpty()) return NotationType.INFIX;
        
        String firstToken = tokens.get(0);
        String lastToken = tokens.get(tokens.size() - 1);
        
        if (isOperator(firstToken) && !firstToken.equals("-")) {
            return NotationType.PREFIX;
        } else if (isOperator(lastToken)) {
            return NotationType.POSTFIX;
        }
        return NotationType.INFIX;
    }

    // Display the result of the calculation
    private void displayResult(NotationType notation, String expression, double result) {
        @SuppressLint("DefaultLocale") String formatted = String.format("Type: %s\n %s = %.2f", notation.toString(), expression, result);
        resultTextView.setTextColor(Color.parseColor("#212121"));
        resultTextView.setText(formatted);
    }

    // Show an error message
    private void showError(String message) {
        resultTextView.setTextColor(Color.RED);
        resultTextView.setText(message);
    }

    // Enum for notation types
    enum NotationType {
        INFIX, PREFIX, POSTFIX
    }

    // Static class for expression evaluation
    static class ExpressionEvaluator {
        // Evaluate an expression based on its notation type
        static double evaluate(String expr, NotationType type) {
            try {
                List<String> tokens = tokenizeExpression(expr);
                switch (type) {
                    case INFIX:
                        return evaluatePostfix(infixToPostfix(tokens));
                    case POSTFIX:
                        return evaluatePostfix(tokens);
                    case PREFIX:
                        Collections.reverse(tokens);
                        return evaluatePostfix(prefixToPostfix(tokens));
                    default:
                        return 0.0;
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("");
            }
        }

        // Tokenize the expression into a list of strings
        private static List<String> tokenizeExpression(String expression) {
            List<String> tokens = new ArrayList<>();
            Matcher matcher = Pattern.compile("-?\\d*\\.?\\d+|[-+*/()^]").matcher(expression);
            while (matcher.find()) {
                String token = matcher.group().trim();
                if (!token.isEmpty()) tokens.add(token);
            }
            return tokens;
        }

        // Convert prefix expression to postfix
        private static List<String> prefixToPostfix(List<String> prefix) {
            Stack<List<String>> stack = new Stack<>();
            
            for (String token : prefix) {
                if (isOperator(token)) {
                    if (stack.size() < 2) {
                        throw new IllegalArgumentException("Invalid prefix: not enough operands for " + token);
                    }
                    List<String> op1 = stack.pop();
                    List<String> op2 = stack.pop();
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

        // Evaluate a postfix expression
        private static double evaluatePostfix(List<String> postfix) {
            Stack<Double> stack = new Stack<>();
            
            for (String token : postfix) {
                if (isNumber(token)) {
                    stack.push(Double.parseDouble(token));
                } else if (isOperator(token)) {
                    double b = stack.pop();
                    double a = stack.pop();
                    stack.push(applyOperator(token, a, b));
                }
            }
            
            return stack.pop();
        }

        // Apply an operator to two operands
        private static double applyOperator(String operator, double a, double b) {
            switch (operator) {
                case "+": return a + b;
                case "-": return a - b;
                case "*": return a * b;
                case "/": 
                    if (b == 0) throw new ArithmeticException("Division by zero");
                    return a / b;
                case "^": return Math.pow(a, b);
                default: throw new IllegalArgumentException("Unknown operator: " + operator);
            }
        }

        // Check if a token is a number
        private static boolean isNumber(String token) {
            return token.matches("-?\\d+(\\.\\d+)?");
        }

        // Check if a token is an operator
        private static boolean isOperator(String token) {
            return "+-*/^".contains(token);
        }

        // Determine if one operator has higher precedence than another
        private static boolean hasHigherPrecedence(String op1, String op2) {
            int p1 = getPrecedence(op1);
            int p2 = getPrecedence(op2);
            return p1 > p2 || (p1 == p2 && isLeftAssociative(op1));
        }

        // Get the precedence of an operator
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