package com.example.taskremainder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.DialogInterface;
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

public class EditItemActivity extends AppCompatActivity implements EditDateDialogFragment.EditDialogListener {

    private int priority;
    private static int[] PRIORITY_COLORS = new int[]{
            R.color.colorPriorityHigh,
            R.color.colorPriorityMid,
            R.color.colorPriorityLow
    };

    private int status;
    private static int[] STATUS_COLORS = new int[]{
            R.color.colorBlack,
            R.color.colorDoneStatus,
            R.color.colorExpiredStatus
    };
    private Spinner statusSpinner;

    private ArrayAdapter<CharSequence> statusAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        setTitle(getResources().getString(R.string.edit_task));

        // Title
        String title = getIntent().getStringExtra("title");
        EditText titleEditText = (EditText) findViewById(R.id.edit_text_edit_item_title);
        titleEditText.setText(title);

        // Body
        String body = getIntent().getStringExtra("body");
        EditText bodyEditText = (EditText) findViewById(R.id.edit_text_edit_item_body);
        bodyEditText.setText(body);

        // Priority
        priority = getIntent().getIntExtra("priority", 0);
        Spinner spinner = (Spinner) findViewById(R.id.spinner_edit_priority);

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
        TextView dateTextView = (TextView) findViewById(R.id.text_view_dialog_edit_date);
        dateTextView.setText(date);

        status = getIntent().getIntExtra("status", 0);
        statusSpinner = (Spinner) findViewById(R.id.spinner_edit_status);

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

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.button_esc) {
            finish();
            return true;
        }

        if (item.getItemId() == R.id.button_refresh) {
            // Reset Title
            String title = getIntent().getStringExtra("title");
            EditText titleEditText = (EditText) findViewById(R.id.edit_text_edit_item_title);
            titleEditText.setText(title);

            // Reset Body
            String body = getIntent().getStringExtra("body");
            EditText bodyEditText = (EditText) findViewById(R.id.edit_text_edit_item_body);
            bodyEditText.setText(body);

            // Reset Priority
            priority = getIntent().getIntExtra("priority", 0);
            Spinner spinner = (Spinner) findViewById(R.id.spinner_edit_priority);
            spinner.setSelection(priority - 1);

            //  Reset Date
            String date = getIntent().getStringExtra("date");
            TextView dateTextView = (TextView) findViewById(R.id.text_view_dialog_edit_date);
            dateTextView.setText(date);

            // Reset Status
            status = getIntent().getIntExtra("status", 0);
            statusSpinner = (Spinner) findViewById(R.id.spinner_edit_status);
            statusSpinner.setSelection(status - 1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFinishEditDialog(String date) {
        TextView tvDate = (TextView) findViewById(R.id.text_view_dialog_edit_date);
        tvDate.setText(date);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");

        try {
            if (simpleDateFormat.parse(date).before(simpleDateFormat.parse(getCurrentDateTime())))
                statusSpinner.setSelection(2);
            else
                statusSpinner.setSelection(status - 1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void onSave(View view) {
        EditText etTitle = (EditText) findViewById(R.id.edit_text_edit_item_title);
        EditText etBody = (EditText) findViewById(R.id.edit_text_edit_item_body);
        Spinner spinner = (Spinner) findViewById(R.id.spinner_edit_priority);
        TextView tvDate = (TextView) findViewById(R.id.text_view_dialog_edit_date);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        try {
            if ("".equals(etTitle.getText().toString())) {

                String text = getResources().getString(R.string.message_valid_name);

                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

                // Reset title
                String title = getIntent().getStringExtra("title");
                etTitle.setText(title);
            } else if (simpleDateFormat.parse(tvDate.getText().toString()).before(simpleDateFormat.parse(getCurrentDateTime())) && statusSpinner.getSelectedItemPosition() + 1 != 3) {

                String text = getResources().getString(R.string.expired_message);

                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

                // Reset status
                status = getIntent().getIntExtra("status", 0);
                statusSpinner.setSelection(2);
            } else if (!simpleDateFormat.parse(tvDate.getText().toString()).before(simpleDateFormat.parse(getCurrentDateTime())) && statusSpinner.getSelectedItemPosition() + 1 == 3) {
                String text = getResources().getString(R.string.unexpired_message);

                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                // Reset status
                status = getIntent().getIntExtra("status", 0);
                statusSpinner.setSelection(status - 1);
            } else {
                Intent data = new Intent();

                data.putExtra("itemTitle", etTitle.getText().toString());
                data.putExtra("itemBody", etBody.getText().toString());
                data.putExtra("itemPriority", spinner.getSelectedItemPosition() + 1);
                data.putExtra("itemDate", tvDate.getText().toString());
                data.putExtra("itemStatus", statusSpinner.getSelectedItemPosition() + 1);

                int pos = getIntent().getIntExtra("pos", 0);
                data.putExtra("pos", pos);
                setResult(RESULT_OK, data);
                finish();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void onDelete(View v) {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.title_delete_confirm))
                .setMessage(getResources().getString(R.string.message_delete_confirm))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent data = new Intent();
                        int pos = getIntent().getIntExtra("pos", 0);
                        data.putExtra("pos", pos);
                        setResult(RESULT_OK, data);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void onDatePick(View view) {
        showEditDialog();
    }

    private void showEditDialog() {
        TextView tvDate = (TextView) findViewById(R.id.text_view_dialog_edit_date);
        EditDateDialogFragment editDateDialogFragment = EditDateDialogFragment.newInstance(tvDate.getText().toString());

        editDateDialogFragment.show(getSupportFragmentManager(), "fragment_edit_date_dialog");
    }

    private String getCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String dateTime = dateFormat.format(calendar.getTime());
        return dateTime;
    }
}