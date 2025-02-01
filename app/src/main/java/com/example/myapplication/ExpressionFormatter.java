package com.example.myapplication;

import java.util.Stack;

public class ExpressionFormatter {

    // Method to format expression to Infix with complete parentheses
    public static String formatToInfixWithParentheses(String expression) {
        Stack<String> stack = new Stack<>();
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                StringBuilder number = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    number.append(expression.charAt(i++));
                }
                i--; // Adjust index after the loop
                result.append(number.toString());
            } else if (c == '(') {
                stack.push(String.valueOf(c));
            } else if (c == ')') {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    result.append(stack.pop());
                }
                if (!stack.isEmpty()) {
                    stack.pop(); // Remove '('
                }
            } else if (isOperator(c)) {
                while (!stack.isEmpty() && precedence(c) <= precedence(stack.peek().charAt(0))) {
                    result.append(stack.pop());
                }
                stack.push(String.valueOf(c));
            }
        }

        while (!stack.isEmpty()) {
            result.append(stack.pop());
        }

        return result.toString();
    }

    // Method to check if a character is an operator
    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    // Method to get operator precedence
    private static int precedence(char operator) {
        if (operator == '+' || operator == '-') {
            return 1;
        } else if (operator == '*' || operator == '/') {
            return 2;
        }
        return 0;
    }
}
