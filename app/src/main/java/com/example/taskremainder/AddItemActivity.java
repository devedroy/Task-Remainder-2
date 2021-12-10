package com.example.taskremainder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class AddItemActivity extends AppCompatActivity {

    private int priority;
    private static int[] PRIORITY_COLORS = new int[] {
            R.color.colorPriorityHigh,
            R.color.colorPriorityMid,
            R.color.colorPriorityLow
    };

    private int status;
    private Spinner statusSpinner;
    private ArrayAdapter<CharSequence> statusAdapter;
    private static int[] STATUS_COLORS = new int[] {
            R.color.colorBlack,
            R.color.colorDoneStatus,
            R.color.colorExpiredStatus
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        setTitle(getResources().getString(R.string.add_task));

        String title = getIntent().getStringExtra("title");
        EditText titleEditText = (EditText) findViewById(R.id.edit_text_add_item_title);
        titleEditText.setText(title);

        String body = getIntent().getStringExtra("body");
        EditText bodyEditText = (EditText) findViewById(R.id.edit_text_add_item_body);
        bodyEditText.setText(body);

        priority = getIntent().getIntExtra("priority", 0);
        Spinner spinner = (Spinner) findViewById(R.id.spinner_add_priority);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.priority_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setSelection(priority - 1);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)parent.getChildAt(0)).setTextColor(ContextCompat.getColor(getApplicationContext(), PRIORITY_COLORS[position]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String date = getIntent().getStringExtra("date");
        TextView dateTextView = (TextView) findViewById(R.id.text_view_dialog_add_date);
        dateTextView.setText(date);

        status = getIntent().getIntExtra("status", 0);
        statusSpinner = (Spinner) findViewById(R.id.spinner_add_status);
        statusAdapter = ArrayAdapter.createFromResource(this, R.array.status_array, R.layout.spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);
        statusSpinner.setSelection(status - 1);
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)parent.getChildAt(0)).setTextColor(ContextCompat.getColor(getApplicationContext(), STATUS_COLORS[position]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Con)
    }
}