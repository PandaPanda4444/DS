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
                // قالب‌بندی عبارت به صورت Infix با پرانتز کامل
                String formattedExpression = ExpressionFormatter.formatToInfixWithParentheses(expression);
                resultTextView.setText("عبارت قالب‌بندی شده: " + formattedExpression);

                // محاسبه عبارت
                double result = ExpressionEvaluator.evaluate(expression);
                resultTextView.append("\nنتیجه: " + result);
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, "عبارت نامعتبر است!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
