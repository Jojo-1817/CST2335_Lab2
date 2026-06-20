package com.example.androidlabs;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private TextView textLabel;
    private EditText editText;
    private Button buttonPress;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_constraint);

        textLabel = findViewById(R.id.textLabel);
        editText = findViewById(R.id.editText);
        buttonPress = findViewById(R.id.buttonPress);
        checkBox = findViewById(R.id.checkBox);

        buttonPress.setOnClickListener(v -> {
            textLabel.setText(editText.getText().toString());

            Toast.makeText(
                    MainActivity.this,
                    getResources().getString(R.string.toast_message),
                    Toast.LENGTH_SHORT
            ).show();
        });

        checkBox.setOnCheckedChangeListener((CompoundButton cb, boolean b) -> {
            String status = b ? getString(R.string.on) : getString(R.string.off);

            Snackbar.make(
                    cb,
                    getString(R.string.snackbar_message) + " " + status,
                    Snackbar.LENGTH_LONG
            ).setAction(
                    getString(R.string.undo),
                    click -> cb.setChecked(!b)
            ).show();
        });
    }
}