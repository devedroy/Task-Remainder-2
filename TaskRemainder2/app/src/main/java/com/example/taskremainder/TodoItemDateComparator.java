package com.example.taskremainder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

public class TodoItemDateComparator implements Comparator<TodoItem> {
    @Override
    public int compare(TodoItem o1, TodoItem o2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        try {
            if (sdf.parse(o1.dueDate).before(sdf.parse(o2.dueDate)))
                return -1;
            else if (sdf.parse(o1.dueDate).after(sdf.parse(o2.dueDate)))
                return 1;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
