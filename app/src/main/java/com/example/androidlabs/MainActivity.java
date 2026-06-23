package com.example.androidlabs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editName;
    private Button buttonNext;

    private SharedPreferences sharedPreferences;

    private static final String PREFS_NAME = "MyPrefs";
    private static final String NAME_KEY = "name";
    private static final int NAME_ACTIVITY_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editName = findViewById(R.id.editName);
        buttonNext = findViewById(R.id.buttonNext);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        String savedName = sharedPreferences.getString(NAME_KEY, "");

        if (!savedName.isEmpty()) {
            editName.setText(savedName);
        }

        buttonNext.setOnClickListener(v -> {
            String name = editName.getText().toString();

            Intent intent = new Intent(MainActivity.this, NameActivity.class);
            intent.putExtra(NAME_KEY, name);

            startActivityForResult(intent, NAME_ACTIVITY_REQUEST_CODE);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        String currentName = editName.getText().toString();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NAME_KEY, currentName);
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NAME_ACTIVITY_REQUEST_CODE) {
            if (resultCode == 0) {
                // User wants to change their name.
            } else if (resultCode == 1) {
                finish();
            }
        }
    }
}