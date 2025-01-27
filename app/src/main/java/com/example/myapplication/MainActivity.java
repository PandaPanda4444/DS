package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText inputExpression = findViewById(R.id.inputExpression);
        Button evaluateButton = findViewById(R.id.evaluateButton);
        TextView resultTextView = findViewById(R.id.resultTextView);

        evaluateButton.setOnClickListener(v -> {
            String expression = inputExpression.getText().toString();

            try {
                // Calculate the expression
                double result = ExpressionEvaluator.evaluate(expression);
                resultTextView.setText("Result: " + result);
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, "Invalid expression!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void main(String[] args) {
        // Test different expressions
        String[] testExpressions = {
            "2 + 3",
            "4 * 5 - 6",
            "(7 + 8) * 2",
            "-5 + 3",
            "10 / 2",
            "2.5 * 3"
        };

        for (String expr : testExpressions) {
            try {
                double result = ExpressionEvaluator.evaluate(expr);
                System.out.println("Expression: " + expr + " = " + result);
            } catch (IllegalArgumentException e) {
                System.out.println("Error evaluating '" + expr + "': " + e.getMessage());
            }
        }
    }
}
