package com.example.androidlabs;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView todoListView;
    private EditText todoEditText;
    private Switch urgentSwitch;
    private Button addButton;

    private ArrayList<TodoItem> todoItems;
    private TodoAdapter todoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        todoListView = findViewById(R.id.todoListView);
        todoEditText = findViewById(R.id.todoEditText);
        urgentSwitch = findViewById(R.id.urgentSwitch);
        addButton = findViewById(R.id.addButton);

        todoItems = new ArrayList<>();
        todoAdapter = new TodoAdapter();
        todoListView.setAdapter(todoAdapter);

        addButton.setOnClickListener(v -> {
            String text = todoEditText.getText().toString();
            boolean urgent = urgentSwitch.isChecked();

            if (!text.isEmpty()) {
                TodoItem newItem = new TodoItem(text, urgent);
                todoItems.add(newItem);

                todoEditText.setText("");
                urgentSwitch.setChecked(false);

                todoAdapter.notifyDataSetChanged();
            }
        });

        todoListView.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle(R.string.delete_title);
            builder.setMessage(getString(R.string.selected_row) + position);

            builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                todoItems.remove(position);
                todoAdapter.notifyDataSetChanged();
            });

            builder.setNegativeButton(R.string.no, null);

            builder.create().show();

            return true;
        });
    }

    private static class TodoItem {
        private String text;
        private boolean urgent;

        public TodoItem(String text, boolean urgent) {
            this.text = text;
            this.urgent = urgent;
        }

        public String getText() {
            return text;
        }

        public boolean isUrgent() {
            return urgent;
        }
    }

    private class TodoAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return todoItems.size();
        }

        @Override
        public Object getItem(int position) {
            return todoItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.todo_row, parent, false);

            TextView todoTextView = view.findViewById(R.id.todoTextView);
            TodoItem item = todoItems.get(position);

            todoTextView.setText(item.getText());

            if (item.isUrgent()) {
                view.setBackgroundColor(Color.RED);
                todoTextView.setTextColor(Color.WHITE);
            } else {
                view.setBackgroundColor(Color.WHITE);
                todoTextView.setTextColor(Color.BLACK);
            }

            return view;
        }
    }
}