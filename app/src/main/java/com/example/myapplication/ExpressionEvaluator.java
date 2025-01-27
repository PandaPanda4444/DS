package com.example.myapplication;

import java.util.Stack;

public class ExpressionEvaluator {

    public static double evaluate(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("Expression is empty");
        }
        // Remove all whitespace from the expression
        expression = expression.replaceAll("\\s+", "");
        return evaluateInfix(expression);
    }

    private static double evaluateInfix(String expression) {
        Stack<Double> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        try {
            for (int i = 0; i < expression.length(); i++) {
                char c = expression.charAt(i);

                if (Character.isDigit(c) || (c == '-' && (i == 0 || expression.charAt(i-1) == '('))) {
                    StringBuilder sb = new StringBuilder();
                    if (c == '-') {
                        sb.append(c);
                        i++;
                    }
                    while (i < expression.length() && 
                          (Character.isDigit(expression.charAt(i)) || 
                           expression.charAt(i) == '.')) {
                        sb.append(expression.charAt(i++));
                    }
                    i--;
                    values.push(Double.parseDouble(sb.toString()));
                } else if (c == '(') {
                    operators.push(c);
                } else if (c == ')') {
                    while (!operators.isEmpty() && operators.peek() != '(') {
                        if (values.size() < 2) {
                            throw new IllegalArgumentException("Invalid expression format");
                        }
                        values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                    }
                    if (operators.isEmpty()) {
                        throw new IllegalArgumentException("Mismatched parentheses");
                    }
                    operators.pop(); // Remove '('
                } else if (isOperator(c)) {
                    while (!operators.isEmpty() && operators.peek() != '(' && 
                           precedence(c) <= precedence(operators.peek())) {
                        if (values.size() < 2) {
                            throw new IllegalArgumentException("Invalid expression format");
                        }
                        values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                    }
                    operators.push(c);
                } else if (!Character.isWhitespace(c)) {
                    throw new IllegalArgumentException("Invalid character: " + c);
                }
            }

            while (!operators.isEmpty()) {
                if (operators.peek() == '(') {
                    throw new IllegalArgumentException("Mismatched parentheses");
                }
                if (values.size() < 2) {
                    throw new IllegalArgumentException("Invalid expression format");
                }
                values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
            }

            if (values.isEmpty()) {
                throw new IllegalArgumentException("Empty expression");
            }
            if (values.size() > 1) {
                throw new IllegalArgumentException("Invalid expression format");
            }

            return values.pop();
            
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format");
        }
    }

    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private static int precedence(char op) {
        if (op == '+' || op == '-') return 1;
        if (op == '*' || op == '/') return 2;
        return 0;
    }

    private static double applyOperator(char op, double b, double a) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/': 
                if (b == 0) {
                    throw new IllegalArgumentException("Division by zero");
                }
                return a / b;
            default: 
                throw new IllegalArgumentException("Invalid operator: " + op);
        }
    }
}
