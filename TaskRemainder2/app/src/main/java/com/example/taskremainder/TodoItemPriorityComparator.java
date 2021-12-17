package com.example.taskremainder;

import java.util.Comparator;

public class TodoItemPriorityComparator implements Comparator<TodoItem> {
    @Override
    public int compare(TodoItem o1, TodoItem o2) {
        return o1.priority - o2.priority;
    }
}
