package com.example.myapplication;


import java.util.Stack;

public class ExpressionFormatter {

    // متد برای قالب‌بندی عبارت به صورت Infix با پرانتز کامل
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
                stack.push(number.toString());
            } else if (c == '(') {
                stack.push(String.valueOf(c));
            } else if (c == ')') {
                StringBuilder subExpression = new StringBuilder();
                while (!stack.peek().equals("(")) {
                    subExpression.insert(0, stack.pop());
                }
                stack.pop(); // Remove '('
                stack.push("(" + subExpression.toString() + ")");
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

    // متد بررسی اینکه آیا این کاراکتر یک عملگر است
    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    // متد برای گرفتن اولویت عملگر
    private static int precedence(char operator) {
        if (operator == '+' || operator == '-') {
            return 1;
        } else if (operator == '*' || operator == '/') {
            return 2;
        }
        return 0;
    }
}
