package com.example.taskremainder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class TodoAdapter extends ArrayAdapter<TodoItem> {
    private Context mContext;
    private String mCurrentTime;

    public TodoAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public TodoAdapter(Context context, ArrayList<TodoItem> items, String currentTime) {
        super(context, 0, items);
        this.mContext = context;
        this.mCurrentTime = currentTime;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TodoItem item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_todo, parent, false);
        }
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
        TextView tvBody = (TextView) convertView.findViewById(R.id.tv_body);
        TextView tvDate = (TextView) convertView.findViewById(R.id.tv_date);
        TextView tvStatus = (TextView) convertView.findViewById(R.id.tv_status);

        tvTitle.setText(item.title);
        tvTitle.setTextColor(item.priority);

        tvBody.setText(item.body);
        tvBody.setTextColor(ContextCompat.getColor(mContext, R.color.colorBody));

        tvDate.setText(item.dueDate);

        if (item.status == 2) {
            tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.colorDisable));
            tvBody.setTextColor(ContextCompat.getColor(mContext, R.color.colorDisable));
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        try {
            if (sdf.parse(item.dueDate).before(sdf.parse(mCurrentTime))) {
                tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.colorDisable));
                tvBody.setTextColor(ContextCompat.getColor(mContext, R.color.colorDisable));
                item.status = 3;
                tvStatus.setText(mContext.getResources().getString(R.string.expired));
            }
            else {
                tvStatus.setText("");
                if (item.status == 2) {
                    tvStatus.setText(mContext.getResources().getString(R.string.done));
                    tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorDoneStatus));
                }
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private int setColor(int priority) {
        if (priority == 1) {
            return ContextCompat.getColor(mContext, R.color.colorPriorityHigh);
        }
        else if (priority == 2) {
            return ContextCompat.getColor(mContext, R.color.colorPriorityMid);
        }
        else if (priority == 3) {
            return ContextCompat.getColor(mContext, R.color.colorPriorityLow);
        }
        return -1;
    }
}
