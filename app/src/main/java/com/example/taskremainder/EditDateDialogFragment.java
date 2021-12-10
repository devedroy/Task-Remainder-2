package com.example.taskremainder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class EditDateDialogFragment extends DialogFragment {

    private Button okBtn;

    public interface EditDialogListener {
        void onFinishEditDialog(String date);
    }

    public EditDateDialogFragment() {

    }

    public static EditDateDialogFragment newInstance(String date) {
        EditDateDialogFragment fragment = new EditDateDialogFragment();
        Bundle args = new Bundle();
        args.putString("dueDate", date);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_date_dialog, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().setTitle("Due Date");

        String dateString = getArguments().getString("dueDate", "2000/03/24");
        String[] splitedDate = dateString.split("/");

        int year = Integer.parseInt(splitedDate[0]);
        int month = Integer.parseInt(splitedDate[1]) - 1;
        int day = Integer.parseInt(splitedDate[2]);

        DatePicker datePicker = (DatePicker) getView().findViewById(R.id.date_picker);
        datePicker.updateDate(year, month, day);

       okBtn = (Button) view.findViewById(R.id.button_ok);
       okBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               dismissDialog();
           }
       });
    }

    private void dismissDialog() {
        EditDialogListener listener = (EditDialogListener) getActivity();
        DatePicker datePicker = (DatePicker) getView().findViewById(R.id.date_picker);

        int month = datePicker.getMonth() + 1;
        int day = datePicker.getDayOfMonth();

        String formattedMonth;
        String formattedDay;

        if (day < 10) {
            formattedDay = "0" + day;
        }
        else {
            formattedDay = String.valueOf(day);
        }

        if (month < 10) {
            formattedMonth = "0" + month;
        }
        else {
            formattedMonth = String.valueOf(month);
        }

        String dateString = datePicker.getYear() + "/" + formattedMonth + "/" + formattedDay;
        listener.onFinishEditDialog(dateString);
        dismiss();
    }
}
