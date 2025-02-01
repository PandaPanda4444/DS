package com.example.myapplication;

import java.util.Stack;
import java.util.ArrayList;
import java.util.List;

/**
 * A mathematical expression evaluator that supports Infix, Prefix, and Postfix notations.
 */
public class ExpressionEvaluator {
    
    /**
     * Evaluates a mathematical expression in the specified notation.
     * @param expression The mathematical expression to evaluate
     * @param notationType The notation type (INFIX, PREFIX, or POSTFIX)
     * @return The result of the evaluation
     * @throws IllegalArgumentException if the expression is invalid
     */
    public static double evaluate(String expression, NotationType notationType) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("Expression is empty");
        }
        
        // Split expression into tokens, preserving spaces for prefix and postfix
        switch (notationType) {
            case INFIX:
                // Remove spaces for infix as they're not needed
                expression = expression.replaceAll("\\s+", "");
                return evaluateInfix(expression);
            case PREFIX:
                return evaluatePrefix(expression);
            case POSTFIX:
                return evaluatePostfix(expression);
            default:
                throw new IllegalArgumentException("Unsupported notation type");
        }
    }

    /**
     * Evaluates an infix expression (e.g., "2 + 3 * 4")
     */
    private static double evaluateInfix(String expression) {
        Stack<Double> values = new Stack<>();
        Stack<String> operators = new Stack<>();
        
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            
            if (Character.isDigit(c) || (c == '-' && (i == 0 || expression.charAt(i-1) == '('))) {
                StringBuilder number = new StringBuilder();
                if (c == '-') {
                    number.append(c);
                    i++;
                }
                while (i < expression.length() && 
                      (Character.isDigit(expression.charAt(i)) || 
                       expression.charAt(i) == '.')) {
                    number.append(expression.charAt(i++));
                }
                i--;
                values.push(Double.parseDouble(number.toString()));
            }
            else if (c == '(') {
                operators.push(String.valueOf(c));
            }
            else if (c == ')') {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    values.push(applyOperator(operators.pop(), values));
                }
                if (operators.isEmpty()) {
                    throw new IllegalArgumentException("Mismatched parentheses");
                }
                operators.pop(); // Remove "("
            }
            else if (isMathFunction(c, expression, i)) {
                StringBuilder function = new StringBuilder();
                while (i < expression.length() && Character.isLetter(expression.charAt(i))) {
                    function.append(expression.charAt(i++));
                }
                i--;
                operators.push(function.toString());
            }
            else if (isOperator(String.valueOf(c))) {
                while (!operators.isEmpty() && !operators.peek().equals("(") &&
                       hasHigherPrecedence(operators.peek(), String.valueOf(c))) {
                    values.push(applyOperator(operators.pop(), values));
                }
                operators.push(String.valueOf(c));
            }
        }
        
        while (!operators.isEmpty()) {
            if (operators.peek().equals("(")) {
                throw new IllegalArgumentException("Mismatched parentheses");
            }
            values.push(applyOperator(operators.pop(), values));
        }
        
        if (values.size() != 1) {
            throw new IllegalArgumentException("Invalid expression");
        }
        
        return values.pop();
    }

    /**
     * Evaluates a prefix expression (e.g., "+ 2 * 3 4")
     */
    private static double evaluatePrefix(String expression) {
        Stack<Double> stack = new Stack<>();
        // Split by spaces and reverse the tokens
        String[] tokens = expression.trim().split("\\s+");
        
        for (int i = tokens.length - 1; i >= 0; i--) {
            String token = tokens[i];
            
            if (isOperator(token)) {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("Invalid prefix expression");
                }
                double a = stack.pop();
                double b = stack.pop();
                stack.push(applyBasicOperator(token, a, b));
            } else {
                try {
                    stack.push(Double.parseDouble(token));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid number: " + token);
                }
            }
        }
        
        if (stack.size() != 1) {
            throw new IllegalArgumentException("Invalid prefix expression");
        }
        return stack.pop();
    }

    /**
     * Evaluates a postfix expression (e.g., "2 3 4 * +")
     */
    private static double evaluatePostfix(String expression) {
        Stack<Double> stack = new Stack<>();
        String[] tokens = expression.trim().split("\\s+");
        
        for (String token : tokens) {
            if (isOperator(token)) {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("Invalid postfix expression");
                }
                double b = stack.pop();
                double a = stack.pop();
                stack.push(applyBasicOperator(token, a, b));
            } else {
                try {
                    stack.push(Double.parseDouble(token));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid number: " + token);
                }
            }
        }
        
        if (stack.size() != 1) {
            throw new IllegalArgumentException("Invalid postfix expression");
        }
        return stack.pop();
    }

    private static boolean isOperator(String op) {
        return op.matches("[+\\-*/^]") || 
               op.equals("SIN") || op.equals("COS") || op.equals("TAN") ||
               op.equals("LOG") || op.equals("SQRT");
    }

    private static boolean isMathFunction(char c, String expr, int pos) {
        if (!Character.isLetter(c)) return false;
        String remaining = expr.substring(pos);
        return remaining.startsWith("SIN") || remaining.startsWith("COS") ||
               remaining.startsWith("TAN") || remaining.startsWith("LOG") ||
               remaining.startsWith("SQRT");
    }

    private static boolean hasHigherPrecedence(String op1, String op2) {
        return getPrecedence(op1) >= getPrecedence(op2);
    }

    private static int getPrecedence(String op) {
        switch (op) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            case "^":
                return 3;
            case "SIN":
            case "COS":
            case "TAN":
            case "LOG":
            case "SQRT":
                return 4;
            default:
                return 0;
        }
    }

    private static double applyOperator(String op, Stack<Double> values) {
        switch (op) {
            case "+":
            case "-":
            case "*":
            case "/":
            case "^":
                double b = values.pop();
                double a = values.pop();
                return applyBasicOperator(op, a, b);
            case "SIN":
                return Math.sin(Math.toRadians(values.pop()));
            case "COS":
                return Math.cos(Math.toRadians(values.pop()));
            case "TAN":
                return Math.tan(Math.toRadians(values.pop()));
            case "LOG":
                return Math.log10(values.pop());
            case "SQRT":
                return Math.sqrt(values.pop());
            default:
                throw new IllegalArgumentException("Unknown operator: " + op);
        }
    }

    private static double applyBasicOperator(String op, double a, double b) {
        switch (op) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/": 
                if (b == 0) throw new IllegalArgumentException("Division by zero");
                return a / b;
            case "^": return Math.pow(a, b);
            default: throw new IllegalArgumentException("Unknown operator: " + op);
        }
    }
}
