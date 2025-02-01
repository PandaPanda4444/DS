package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.NotationType;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText inputExpression;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        inputExpression = findViewById(R.id.inputExpression);
        resultTextView = findViewById(R.id.resultTextView);

        // Set up calculator buttons
        setupCalculatorButtons();
    }

    private void setupCalculatorButtons() {
        // Number buttons
        setButtonClickListener(R.id.btn1);
        setButtonClickListener(R.id.btn2);
        setButtonClickListener(R.id.btn3);
        setButtonClickListener(R.id.btn4);
        setButtonClickListener(R.id.btn5);
        setButtonClickListener(R.id.btn6);
        setButtonClickListener(R.id.btn7);
        setButtonClickListener(R.id.btn8);
        setButtonClickListener(R.id.btn9);
        
        // Operator buttons
        setButtonClickListener(R.id.btnPlus);
        setButtonClickListener(R.id.btnMinus);
        setButtonClickListener(R.id.btnMultiply);
        setButtonClickListener(R.id.btnDivide);
        setButtonClickListener(R.id.btnPower);
        
        // Other buttons
        setButtonClickListener(R.id.btnEquals);
        setButtonClickListener(R.id.btnClear);
        setButtonClickListener(R.id.btnDelete);
        setButtonClickListener(R.id.btnSpace);
    }

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
                    // Handle special cases for operators
                    if (buttonText.equals("×")) buttonText = "*";
                    if (buttonText.equals("÷")) buttonText = "/";
                    if (buttonText.equals("^2")) buttonText = "^";
                    if (buttonText.equals("Space")) buttonText = " ";
                    
                    // Append the button text to the input
                    inputExpression.append(buttonText);
            }
        }
    }

    private void calculateResult() {
        try {
            String expression = inputExpression.getText().toString().trim();
            if (expression.isEmpty()) {
                Toast.makeText(this, "Please enter an expression", Toast.LENGTH_SHORT).show();
                return;
            }
            
            NotationType detectedNotation = detectNotationType(expression);
            double result = ExpressionEvaluator.evaluate(expression, detectedNotation);
            String formattedResult = String.format("Type: %s\n\n%s = %.2f", 
                                                 detectedNotation.toString(),
                                                 expression,
                                                 result);
            resultTextView.setText(formattedResult);
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Invalid expression", Toast.LENGTH_SHORT).show();
        }
    }

    private NotationType detectNotationType(String expression) {
        expression = expression.trim();
        
        // Check if empty
        if (expression.isEmpty()) {
            return NotationType.INFIX; // default
        }

        // Remove spaces and get first character
        String noSpaces = expression.replaceAll("\\s+", "");
        char firstChar = noSpaces.charAt(0);
        
        // Prefix notation typically starts with an operator
        if ("+-*/^".indexOf(firstChar) >= 0) {
            return NotationType.PREFIX;
        }
        
        // Postfix notation typically ends with an operator
        char lastChar = noSpaces.charAt(noSpaces.length() - 1);
        if ("+-*/^".indexOf(lastChar) >= 0) {
            return NotationType.POSTFIX;
        }
        
        // Default to infix notation
        return NotationType.INFIX;
    }
}
