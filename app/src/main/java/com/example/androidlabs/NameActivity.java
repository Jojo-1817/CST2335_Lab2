package com.example.androidlabs;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NameActivity extends AppCompatActivity {

    private TextView textWelcome;
    private Button buttonThankYou;
    private Button buttonDontCallMe;

    private static final String NAME_KEY = "name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        textWelcome = findViewById(R.id.textWelcome);
        buttonThankYou = findViewById(R.id.buttonThankYou);
        buttonDontCallMe = findViewById(R.id.buttonDontCallMe);

        String name = getIntent().getStringExtra(NAME_KEY);

        if (name == null) {
            name = "";
        }

        textWelcome.setText(getString(R.string.welcome) + " " + name + "!");

        buttonDontCallMe.setOnClickListener(v -> {
            setResult(0);
            finish();
        });

        buttonThankYou.setOnClickListener(v -> {
            setResult(1);
            finish();
        });
    }
}
