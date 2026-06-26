package com.example.androidlabs;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
    private TodoDatabaseHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        todoListView = findViewById(R.id.todoListView);
        todoEditText = findViewById(R.id.todoEditText);
        urgentSwitch = findViewById(R.id.urgentSwitch);
        addButton = findViewById(R.id.addButton);

        dbHelper = new TodoDatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        todoItems = new ArrayList<>();
        loadTodosFromDatabase();

        todoAdapter = new TodoAdapter();
        todoListView.setAdapter(todoAdapter);

        addButton.setOnClickListener(v -> {
            String text = todoEditText.getText().toString();
            boolean urgent = urgentSwitch.isChecked();

            if (!text.isEmpty()) {
                ContentValues values = new ContentValues();
                values.put(TodoDatabaseHelper.COL_TEXT, text);
                values.put(TodoDatabaseHelper.COL_URGENT, urgent ? 1 : 0);

                long newId = db.insert(TodoDatabaseHelper.TABLE_NAME, null, values);

                TodoItem newItem = new TodoItem(newId, text, urgent);
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
                TodoItem itemToDelete = todoItems.get(position);

                db.delete(
                        TodoDatabaseHelper.TABLE_NAME,
                        TodoDatabaseHelper.COL_ID + " = ?",
                        new String[]{String.valueOf(itemToDelete.getId())}
                );

                todoItems.remove(position);
                todoAdapter.notifyDataSetChanged();
            });

            builder.setNegativeButton(R.string.no, null);
            builder.create().show();

            return true;
        });
    }

    private void loadTodosFromDatabase() {
        Cursor cursor = db.query(
                TodoDatabaseHelper.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        printCursor(cursor);

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(TodoDatabaseHelper.COL_ID));
            String text = cursor.getString(cursor.getColumnIndexOrThrow(TodoDatabaseHelper.COL_TEXT));
            int urgentInt = cursor.getInt(cursor.getColumnIndexOrThrow(TodoDatabaseHelper.COL_URGENT));

            boolean urgent = urgentInt == 1;

            TodoItem item = new TodoItem(id, text, urgent);
            todoItems.add(item);
        }

        cursor.close();
    }

    private void printCursor(Cursor c) {
        Log.d("CursorInfo", "Database Version: " + db.getVersion());
        Log.d("CursorInfo", "Number of columns: " + c.getColumnCount());

        String[] columnNames = c.getColumnNames();

        for (String columnName : columnNames) {
            Log.d("CursorInfo", "Column name: " + columnName);
        }

        Log.d("CursorInfo", "Number of results: " + c.getCount());

        if (c.moveToFirst()) {
            do {
                StringBuilder row = new StringBuilder();

                for (int i = 0; i < c.getColumnCount(); i++) {
                    row.append(c.getColumnName(i))
                            .append(": ")
                            .append(c.getString(i))
                            .append(" ");
                }

                Log.d("CursorInfo", "Row: " + row.toString());

            } while (c.moveToNext());
        }

        c.moveToPosition(-1);
    }

    private static class TodoItem {
        private long id;
        private String text;
        private boolean urgent;

        public TodoItem(long id, String text, boolean urgent) {
            this.id = id;
            this.text = text;
            this.urgent = urgent;
        }

        public long getId() {
            return id;
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
            return todoItems.get(position).getId();
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