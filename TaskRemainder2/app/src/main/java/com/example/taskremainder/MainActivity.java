package com.example.taskremainder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private ArrayList<TodoItem> todoItems, item_1, item_2, item_3;
    private TodoItemDatabase dbHelper;
    private int sortingOption;
    private TodoAdapter adapter, adapter2, adapter3;
    private ListView lvItems, lvItems2, lvItems3;
    private TextView sortHeader1, sortHeader2, sortHeader3;
    private Menu mMenu;
    private final int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        populateItemsList();
        setupListViewListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.mMenu = menu;

        if (sortingOption == 1) {
            setOptionColor();
            sortByTitle();
            return true;
        }

        if (sortingOption == 2) {
            setOptionColor();
            sortByPriority();
            return true;
        }

        if (sortingOption == 3) {
            setOptionColor();
            sortByDate();
            return true;
        }
        if (sortingOption == 4) {
            setOptionColor();
            sortByStatus();
            return true;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.icon_button_add) {
            Intent intent = new Intent(MainActivity.this, AddItemActivity.class);

            intent.putExtra("title", "");
            intent.putExtra("body", "");
            intent.putExtra("priority", 1);
            intent.putExtra("date", getCurrentDateTime());
            intent.putExtra("status", 1);

            startActivityForResult(intent, REQUEST_CODE);
            return true;
        }

        if (item.getItemId() == R.id.sort_title) {
            sortingOption = 1;
            setOptionColor();
            sortByTitle();
            writeItemsToDB();
            return true;
        }
        if (item.getItemId() == R.id.sort_priority) {
            sortingOption = 2;
            setOptionColor();
            sortByPriority();
            writeItemsToDB();
            return true;
        }
        if (item.getItemId() == R.id.sort_date) {
            sortingOption = 3;
            setOptionColor();
            sortByDate();
            writeItemsToDB();
            return true;
        }
        if (item.getItemId() == R.id.sort_status) {
            sortingOption = 4;
            setOptionColor();
            sortByStatus();
            writeItemsToDB();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data.hasExtra("itemTitle")) {
                String itemTitle = data.getExtras().getString("itemTitle");
                String itemBody = data.getExtras().getString("itemBody");
                int itemPriority = data.getExtras().getInt("itemPriority");
                String itemDate = data.getExtras().getString("itemDate");
                int itemStatus = data.getExtras().getInt("itemStatus");

                if (data.hasExtra("pos")) {
                    int pos = data.getExtras().getInt("pos", 0);
                    todoItems.set(pos, new TodoItem(itemTitle, itemBody, itemPriority, itemDate, itemStatus));
                } else {
                    todoItems.add(new TodoItem(itemTitle, itemBody, itemPriority, itemDate, itemStatus));
                }
            } else {
                int pos = data.getExtras().getInt("pos", 0);
                todoItems.remove(pos);
            }
            if (sortingOption == 0)
                setAdapterListViewsForTitleAndDate();
            if (sortingOption == 1) {
                sortByTitle();
            }
            if (sortingOption == 2) {
                sortByPriority();
            }
            if (sortingOption == 3) {
                sortByDate();
            }
            if (sortingOption == 4) {
                sortByStatus();
            }
            writeItemsToDB();
        }
//        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sortByStatus() {
        Collections.sort(todoItems, new TodoItemDateComparator());
        Collections.sort(todoItems, new TodoItemStatusComparator());
        setAdapterListViewsForPriorityAndStatus(R.id.sort_status);
    }

    private void sortByDate() {
        Collections.sort(todoItems, new TodoItemPriorityComparator());
        Collections.sort(todoItems, new TodoItemDateComparator());
        setAdapterListViewsForTitleAndDate();
    }

    private void sortByPriority() {
        Collections.sort(todoItems, new TodoItemDateComparator());
        Collections.sort(todoItems, new TodoItemPriorityComparator());
        setAdapterListViewsForPriorityAndStatus(R.id.sort_priority);
    }

    private void sortByTitle() {
        Collections.sort(todoItems, new TodoItemTitleComparator());
        setAdapterListViewsForTitleAndDate();
    }

    private void setAdapterListViewsForTitleAndDate() {
        sortHeader1 = (TextView) findViewById(R.id.tv_sort_header);
        sortHeader2 = (TextView) findViewById(R.id.tv_sort_header2);
        sortHeader3 = (TextView) findViewById(R.id.tv_sort_header3);

        adapter = new TodoAdapter(this, todoItems, getCurrentDateTime());

        lvItems.setAdapter(adapter);
        lvItems2.setAdapter(null);
        lvItems3.setAdapter(null);

        setListViewHeightBasedOnChildren(lvItems3);
        setListViewHeightBasedOnChildren(lvItems2);
        setListViewHeightBasedOnChildren(lvItems);

        sortHeader1.setVisibility(View.GONE);
        sortHeader2.setVisibility(View.GONE);
        sortHeader3.setVisibility(View.GONE);
    }

    private void setOptionColor() {
        for (int i = 0; i < 4; i++) {
            MenuItem item = mMenu.getItem(0).getSubMenu().getItem(i);
            SpannableString s = new SpannableString(item.getTitle());
            s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
            item.setTitle(s);
        }
        MenuItem item = mMenu.getItem(0).getSubMenu().getItem(sortingOption - 1);
        SpannableString s = new SpannableString(item.getTitle());
        s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
        item.setTitle(s);
    }

    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                todoItems.remove(position);

                if (sortingOption == 2) {
                    item_1.remove(position);
                    setAdapterListViewsForPriorityAndStatus(R.id.sort_status);
                }

                if (sortingOption == 4) {
                    item_1.remove(position);
                    setAdapterListViewsForPriorityAndStatus(R.id.sort_status);
                }

                adapter.notifyDataSetChanged();
                writeItemsToDB();
                return true;
            }
        });

        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
                todoItems.remove(pos);
                if (sortingOption == 2) {
                    item_1.remove(pos);
                    setAdapterListViewsForPriorityAndStatus(R.id.sort_priority);
                }
                if (sortingOption == 4) {
                    item_1.remove(pos);
                    setAdapterListViewsForPriorityAndStatus(R.id.sort_status);
                }
                adapter.notifyDataSetChanged();
                writeItemsToDB();
                return true;
            }
        });

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long arg) {
                Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
                intent.putExtra("title", todoItems.get(pos).title);
                intent.putExtra("body", todoItems.get(pos).body);
                intent.putExtra("priority", todoItems.get(pos).priority);
                intent.putExtra("date", todoItems.get(pos).dueDate);
                intent.putExtra("status", todoItems.get(pos).status);
                intent.putExtra("pos", pos);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        lvItems2.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
                        todoItems.remove(pos + item_1.size());
                        item_2.remove(pos);
                        if (sortingOption == 2)
                            setAdapterListViewsForPriorityAndStatus(R.id.sort_priority);
                        if (sortingOption == 4)
                            setAdapterListViewsForPriorityAndStatus(R.id.sort_status);
                        writeItemsToDB();
                        return true;
                    }
                });

        lvItems2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditItemActivity.class);

                intent.putExtra("title", todoItems.get(position + item_1.size()).title);
                intent.putExtra("body", todoItems.get(position + item_1.size()).body);
                intent.putExtra("priority", todoItems.get(position + item_1.size()).priority);
                intent.putExtra("date", todoItems.get(position + item_1.size()).dueDate);
                intent.putExtra("status", todoItems.get(position + item_1.size()).status);
                intent.putExtra("pos", position + item_1.size());

                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        lvItems3.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
                todoItems.remove(pos + item_1.size() + item_2.size());
                item_3.remove(pos);
                if (sortingOption == 2)
                    setAdapterListViewsForPriorityAndStatus(R.id.sort_priority);
                if (sortingOption == 4)
                    setAdapterListViewsForPriorityAndStatus(R.id.sort_status);
                writeItemsToDB();
                return true;
            }
        });

        lvItems3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long arg) {
                Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
                intent.putExtra("title", todoItems.get(pos + item_1.size() + item_2.size()).title);
                intent.putExtra("body", todoItems.get(pos + item_1.size() + item_2.size()).body);
                intent.putExtra("priority", todoItems.get(pos + item_1.size() + item_2.size()).priority);
                intent.putExtra("date", todoItems.get(pos + item_1.size() + item_2.size()).dueDate);
                intent.putExtra("status", todoItems.get(pos + item_1.size() + item_2.size()).status);
                intent.putExtra("pos", pos + item_1.size() + item_2.size());
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    private void writeItemsToDB() {
        dbHelper = TodoItemDatabase.getInstance(this);
        dbHelper.addItems(todoItems);
        dbHelper.updateOption(sortingOption);
    }

    private void setAdapterListViewsForPriorityAndStatus(int id) {
        item_1 = new ArrayList<TodoItem>();
        item_2 = new ArrayList<TodoItem>();
        item_3 = new ArrayList<TodoItem>();

        sortHeader1 = (TextView) findViewById(R.id.tv_sort_header);
        sortHeader2 = (TextView) findViewById(R.id.tv_sort_header2);
        sortHeader1 = (TextView) findViewById(R.id.tv_sort_header3);

        if (id == R.id.sort_priority) {
            for (TodoItem i : todoItems) {
                if (i.priority == 1)
                    item_1.add(i);
                else if (i.priority == 2)
                    item_2.add(i);
                else
                    item_3.add(i);
            }

            if (item_1.size() != 0) {
                sortHeader1.setVisibility(View.VISIBLE);
                sortHeader1.setText(getResources().getString(R.string.high));
                sortHeader1.setBackgroundColor(getResources().getColor(R.color.colorPriorityHigh));
            }
            if (item_1.size() == 0) {
                sortHeader1.setVisibility(View.GONE);
            }

            if (item_2.size() != 0) {
                sortHeader2.setVisibility(View.VISIBLE);
                sortHeader2.setText(getResources().getString(R.string.medium));
                sortHeader2.setBackgroundColor(getResources().getColor(R.color.colorPriorityMid));
            }
            if (item_2.size() == 0) {
                sortHeader2.setVisibility(View.GONE);
            }

            if (item_3.size() != 0) {
                sortHeader3.setVisibility(View.VISIBLE);
                sortHeader3.setText(getResources().getString(R.string.low));
                sortHeader3.setBackgroundColor(getResources().getColor(R.color.colorPriorityLow));
            }
            if (item_3.size() == 0) {
                sortHeader3.setVisibility(View.GONE);
            }
        }
        if (id == R.id.sort_status) {
            for (TodoItem i : todoItems) {
                if (i.status == 1)
                    item_1.add(i);
                else if (i.status == 2)
                    item_2.add(i);
                else
                    item_3.add(i);
            }
            if (item_1.size() != 0) {
                sortHeader1.setVisibility(View.VISIBLE);
                sortHeader1.setText(getResources().getString(R.string.todo));
                sortHeader1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }
            if (item_1.size() == 0) {
                sortHeader1.setVisibility(View.GONE);
            }

            if (item_2.size() != 0) {
                sortHeader2.setVisibility(View.VISIBLE);
                sortHeader2.setText(getResources().getString(R.string.done));
                sortHeader2.setBackgroundColor(getResources().getColor(R.color.colorDoneStatus));
            }
            if (item_2.size() == 0) {
                sortHeader2.setVisibility(View.GONE);
            }

            if (item_3.size() != 0) {
                sortHeader3.setVisibility(View.VISIBLE);
                sortHeader3.setText(getResources().getString(R.string.todo));
                sortHeader3.setBackgroundColor(getResources().getColor(R.color.colorExpiredStatus));
            }
            if (item_3.size() == 0) {
                sortHeader1.setVisibility(View.GONE);
            }
        }

        adapter = new TodoAdapter(this, item_1, getCurrentDateTime());
        adapter2 = new TodoAdapter(this, item_2, getCurrentDateTime());
        adapter3 = new TodoAdapter(this, item_3, getCurrentDateTime());

        lvItems.setAdapter(adapter);
        lvItems2.setAdapter(adapter2);
        lvItems3.setAdapter(adapter3);

        setListViewHeightBasedOnChildren(lvItems3);
        setListViewHeightBasedOnChildren(lvItems2);
        setListViewHeightBasedOnChildren(lvItems);
    }

    private void populateItemsList() {
        todoItems = new ArrayList<TodoItem>();
        readItemsFromDB();

        adapter = new TodoAdapter(this, todoItems, getCurrentDateTime());

        lvItems = (ListView) findViewById(R.id.lvItems);
        lvItems2 = (ListView) findViewById(R.id.lvItems2);
        lvItems3 = (ListView) findViewById(R.id.lvItems3);

        lvItems.setAdapter(adapter);
        setListViewHeightBasedOnChildren(lvItems);

    }

    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);

            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    private String getCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String dateTime = dateFormat.format(calendar.getTime());
        return dateTime;
    }

    private void readItemsFromDB() {
        dbHelper = TodoItemDatabase.getInstance(this);
        todoItems = dbHelper.getAllItems();
        sortingOption = dbHelper.getOption();
    }
}