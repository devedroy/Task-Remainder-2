package com.example.taskremainder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddItemActivity extends AppCompatActivity implements EditDateDialogFragment.EditDialogListener {

    private int priority;
    private static int[] PRIORITY_COLORS = new int[]{
            R.color.colorPriorityHigh,
            R.color.colorPriorityMid,
            R.color.colorPriorityLow
    };

    private int status;
    private Spinner statusSpinner;
    private ArrayAdapter<CharSequence> statusAdapter;
    private static int[] STATUS_COLORS = new int[]{
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
                ((TextView) parent.getChildAt(0)).setTextColor(ContextCompat.getColor(getApplicationContext(), PRIORITY_COLORS[position]));
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
                ((TextView) parent.getChildAt(0)).setTextColor(ContextCompat.getColor(getApplicationContext(), STATUS_COLORS[position]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.button_esc) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFinishEditDialog(String date) {
        TextView tvDate = (TextView) findViewById(R.id.text_view_dialog_add_date);
        tvDate.setText(date);

        SimpleDateFormat sdf = new SimpleDateFormat(("yyyy/MM/dd"));
        try {
            if (sdf.parse(date).before(sdf.parse(getCurrentDateTime())))
                statusSpinner.setSelection(2);
            else
                statusSpinner.setSelection(2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void onAdd(View view) {
        EditText etTitle = (EditText) findViewById(R.id.edit_text_add_item_title);
        EditText etBody = (EditText) findViewById(R.id.edit_text_add_item_body);
        Spinner spinner = (Spinner) findViewById(R.id.spinner_add_priority);
        TextView tvDate = (TextView) findViewById(R.id.text_view_dialog_add_date);
        Spinner statusSpinner = (Spinner) findViewById(R.id.spinner_add_status);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");

        try {
            if ("".equals(etTitle.getText().toString())) {
                String text = getResources().getString(R.string.message_valid_name);

                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

                String title = getIntent().getStringExtra("title");
                etTitle.setText(title);
            } else if (simpleDateFormat.parse(tvDate.getText().toString()).before(simpleDateFormat.parse(getCurrentDateTime())) && statusSpinner.getSelectedItemPosition() + 1 != 3) {
                String text = getResources().getString(R.string.expired_message);

                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

                status = getIntent().getIntExtra("status", 0);
                statusSpinner.setSelection(status - 1);
            } else if (!simpleDateFormat.parse(tvDate.getText().toString()).before(simpleDateFormat.parse(getCurrentDateTime())) && statusSpinner.getSelectedItemPosition() + 1 == 3) {
                String text = getResources().getString(R.string.unexpired_message);

                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

                status = getIntent().getIntExtra("status", 0);
                statusSpinner.setSelection(status - 1);
            } else {
                Intent data = new Intent();

                data.putExtra("itemTitle", etTitle.getText().toString());
                data.putExtra("itemBody", etBody.getText().toString());
                data.putExtra("itemPriority", spinner.getSelectedItemPosition() + 1);
                data.putExtra("itemDate", tvDate.getText().toString());
                data.putExtra("itemStatus", statusSpinner.getSelectedItemPosition() + 1);

                setResult(RESULT_OK, data);
                finish();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void onDatePick(View view) {
        showEditDialog();
    }

    private void showEditDialog() {
        TextView tvDate = (TextView) findViewById(R.id.text_view_dialog_add_date);
        EditDateDialogFragment.newInstance(tvDate.getText().toString()).show(getSupportFragmentManager(), "fragment_edit_date_dialog");
    }

    private String getCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String dateTime = dateFormat.format(calendar.getTime());
        return dateTime;
    }
}