package com.example.taskremainder;

import java.util.Comparator;

public class TodoItemTitleComparator implements Comparator<TodoItem> {
    @Override
    public int compare(TodoItem o1, TodoItem o2) {
        return o1.title.compareTo(o2.title);
    }
}
