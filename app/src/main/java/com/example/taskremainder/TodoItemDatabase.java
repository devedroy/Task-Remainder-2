package com.example.taskremainder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class TodoItemDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "todoItem_db";
    private static final int DATABASE_VERSION = 1;

    private static final String TODO_TABLE_NAME = "todoList";
    private static final String SORT_TABLE_NAME = "todoSort";

    private static final String KEY_TODO_ID = "id";
    private static final String KEY_TODO_TITLE = "title";
    private static final String KEY_TODO_BODY = "body";
    private static final String KEY_TODO_PRIORITY = "priority";
    private static final String KEY_TODO_DUE_DATE = "date";
    private static final String KEY_TODO_STATUS = "status";

    private static final String KEY_SORT_ID = "id";
    private static final String KEY_SORT_OPTION = "option";

    private static final String TAG = TodoItemDatabase.class.getName();

    private static TodoItemDatabase sInstance;

    public static synchronized TodoItemDatabase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new TodoItemDatabase(context.getApplicationContext());
        }
        return sInstance;
    }

    public TodoItemDatabase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

//    @Override
//    public void onConfigure(SQLiteDatabase db) {
//        super.onConfigure(db);
//        db.setForeignKeyConstraintsEnabled(true);
//    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE_NAME +
                "(" + KEY_TODO_ID + " INTEGER PRIMARY KEY, " +
                KEY_TODO_TITLE + " TEXT, " +
                KEY_TODO_BODY + " TEXT, " +
                KEY_TODO_PRIORITY + " INTEGER, " +
                KEY_TODO_DUE_DATE + " TEXT, " +
                KEY_TODO_STATUS + " INTEGER" + ")";

        db.execSQL(CREATE_TODO_TABLE);

        String CREATE_SORT_TABLE = "CREATE TABLE " + SORT_TABLE_NAME +
                "(" + KEY_SORT_ID + " INTEGER PRIMARY KEY, " +
                KEY_SORT_OPTION + " INTEGER" + ")";

        db.execSQL(CREATE_SORT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + SORT_TABLE_NAME);
            onCreate(db);
        }
    }

    public ArrayList<TodoItem> getAllItems() {
        ArrayList<TodoItem> items = new ArrayList<TodoItem>();

        String ITEMS_SELECT_QUERY = "SELECT * FROM " + TODO_TABLE_NAME;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(ITEMS_SELECT_QUERY, null);
        try {
            int rows_num = cursor.getCount();
            if (rows_num != 0) {
                cursor.moveToFirst();
                for (int i = 0; i < rows_num; i++) {
                    String title = cursor.getString(cursor.getColumnIndex(KEY_TODO_TITLE));
                    String body = cursor.getString(cursor.getColumnIndex(KEY_TODO_BODY));
                    int priority = cursor.getInt(cursor.getColumnIndex(KEY_TODO_PRIORITY));
                    String dueDate = cursor.getString(cursor.getColumnIndex(KEY_TODO_DUE_DATE));
                    int status = cursor.getInt(cursor.getColumnIndex(KEY_TODO_STATUS));

                    TodoItem item = new TodoItem(title, body, priority, dueDate, status);
                    items.add(item);
                    cursor.moveToNext();
                }
            }
        }
        catch (Exception e) {
            Log.d(TAG, "Error while trying to get items from Database");
        }
        finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return items;
    }

    public void addItems(ArrayList<TodoItem> items) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        db.delete(TODO_TABLE_NAME, null, null);
        try {
            ContentValues values = new ContentValues();
            for (TodoItem item: items) {
                values.put(KEY_TODO_TITLE, item.title);
                values.put(KEY_TODO_BODY, item.body);
                values.put(KEY_TODO_PRIORITY, item.priority);
                values.put(KEY_TODO_DUE_DATE, item.dueDate);
                values.put(KEY_TODO_STATUS, item.status);

                db.insertOrThrow(TODO_TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add or update user");
        } finally {
            db.endTransaction();
        }
    }

    public void updateOption(int option) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        db.delete(SORT_TABLE_NAME, null, null);

        try {
            ContentValues values = new ContentValues();

            values.put(KEY_SORT_OPTION, option);
            db.insertOrThrow(SORT_TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add or update user");
        } finally {
            db.endTransaction();
        }
    }

    public int getOption() {
        String ITEMS_SELECT_QUERY = "SELECT * FROM " + SORT_TABLE_NAME;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(ITEMS_SELECT_QUERY, null);
        int option = 0;
        try {
            int rows_num = cursor.getCount();
            if (rows_num != 0) {
                cursor.moveToFirst();
                for (int i = 0; i < rows_num; i++) {
                    option = cursor.getInt(cursor.getColumnIndex(KEY_SORT_OPTION));
                    cursor.moveToNext();
                }
            }
        }
        catch (Exception e) {
            Log.d(TAG, "Error while trying to items from database");
        }
        finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return option;
    }
}
