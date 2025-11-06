# Mathematical Expression Calculator

This is an Android calculator application that evaluates mathematical expressions in three different notations:

- **Infix notation** (standard mathematical notation like `2 + 3 * 4`)
- **Prefix notation** (also known as Polish notation like `+ 2 * 3 4`)
- **Postfix notation** (also known as Reverse Polish notation like `2 3 4 * +`)

## Features

- Supports basic arithmetic operations: addition (+), subtraction (-), multiplication (*), division (/), and exponentiation (^)
- Handles parentheses for grouping operations
- Supports mathematical functions: SIN, COS, TAN, LOG, SQRT
- Clean and intuitive user interface with a display for results and input field for expressions
- Dedicated buttons for common operations and functions

## Core Components

### ExpressionEvaluator.java

Handles the evaluation of mathematical expressions in all three notations:

- Uses stack-based algorithms for efficient computation
- Implements proper operator precedence handling
- Supports negative numbers and decimal values
- Includes error handling for invalid expressions

### ExpressionFormatter.java

Provides formatting utilities for mathematical expressions:

- Converts expressions to properly parenthesized infix format

### NotationType.java

An enumeration defining the three supported notation types:

- INFIX
- PREFIX
- POSTFIX

## How to Use

1. Enter a mathematical expression in the input field using one of the supported notations
2. Press the "=" button to evaluate the expression
3. View the result in the display area
4. Use the "C" button to clear the current input
5. Use the "⌫" button to delete the last character entered

## Supported Operations

- Basic arithmetic: +, -, *, /, ^
- Trigonometric functions: SIN, COS, TAN (expects degrees)
- Logarithmic function: LOG (base 10)
- Square root: SQRT

## Example Expressions

- Infix: `(2 + 3) * 4`
- Prefix: `* + 2 3 4`
- Postfix: `2 3 + 4 *`

All three expressions above will produce the same result: `20`
