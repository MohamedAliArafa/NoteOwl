package com.zeowls.noteowl;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.zeowls.noteowl.provider.Contract;
import com.zeowls.noteowl.provider.DBHelper;

import java.util.Calendar;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements View.OnClickListener{

    //    Button timePickerButton, datePickerButton;
//    DatePicker datePicker;
//    TimePicker timePicker;
    ListView mListView;
    CursorAdapter cursorAdapter;
    Cursor c;
    String[] mColumns = new String[]{
            Contract.TaskEntry.COLUMN_NAME,
            Contract.TaskEntry.COLUMN_IS_FINISHED,
            Contract.TaskEntry._ID
    };

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);
//        timePickerButton = (Button) v.findViewById(R.id.time_picker_btn);
//        datePickerButton = (Button) v.findViewById(R.id.date_picker_btn);
//        datePicker = (DatePicker) v.findViewById(R.id.date_picker);
//        timePicker = (TimePicker) v.findViewById(R.id.time_picker);
//        timePickerButton.setOnClickListener(this);
//        datePickerButton.setOnClickListener(this);
        mListView = (ListView) v.findViewById(R.id.list_view);
        testTaskTable();
        SQLiteDatabase db = new DBHelper(getActivity()).getWritableDatabase();
        c = db.query(Contract.PATH_TASKS, mColumns, null, null, null, null, null);
        cursorAdapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_2, c, mColumns, new int[]{android.R.id.text1, android.R.id.text2});
        mListView.setAdapter(cursorAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("Main", "Hello");
                TextView tv = (TextView) view.findViewById(android.R.id.text2);
                tv.setText("Clicked: " + tv.getText());
            }
        });
        db.close();
        return v;
    }

    @Override
    public void onClick(View v) {
//        if (v == timePickerButton) {
//            TimePickerFragment newFragment = TimePickerFragment.newInstance(this);
//            newFragment.show(getFragmentManager(), "timePicker");
//        }
//        if (v == datePickerButton) {
//            DatePickerFragment newFragment = DatePickerFragment.newInstance(this);
//            newFragment.show(getFragmentManager(), "datePicker");
//        }
    }

    static ContentValues createListValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(Contract.ListEntry.COLUMN_NAME, "default");
        testValues.put(Contract.ListEntry.COLUMN_NAME, "default");
        testValues.put(Contract.ListEntry.COLUMN_NAME, "default");

        return testValues;
    }

    static ContentValues createTaskValues(long listRowId, String name) {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(Contract.TaskEntry.COLUMN_LIST_ID, listRowId);
        weatherValues.put(Contract.TaskEntry.COLUMN_NAME, name);
        return weatherValues;
    }


    public long insertlist() {
        SQLiteDatabase db = new DBHelper(getActivity()).getWritableDatabase();
        ContentValues valuesData = createListValues();
        return db.insert(Contract.ListEntry.TABLE_NAME, null, valuesData);
    }


    public void testTaskTable() {
        long listRowId = insertlist();
        SQLiteDatabase db = new DBHelper(getActivity()).getWritableDatabase();
        // Create ContentValues of what you want to insert
        // (you can use the createTaskValues TestUtilities function if you wish)
        ContentValues valuesData;
        for (int i = 0; i <= 10; i++) {
            valuesData = createTaskValues(listRowId, "task " + i);
            // Insert ContentValues into database and get a row ID back
            db.insert(Contract.TaskEntry.TABLE_NAME, null, valuesData);
        }
        // Query the database and receive a Cursor back
        db.close();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fargment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_clear) {
            SQLiteDatabase db = new DBHelper(getActivity()).getWritableDatabase();
            db.delete(Contract.PATH_TASKS, null, null);
            c.requery();
            cursorAdapter.swapCursor(c);
            cursorAdapter.notifyDataSetChanged();
            db.close();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //
//    @Override
//    public void onDateSet(Date date) {
//        Calendar c = Calendar.getInstance();
//        c.setTime(date);
//        int year = c.get(Calendar.YEAR);
//        int month = c.get(Calendar.MONTH);
//        int day = c.get(Calendar.DAY_OF_MONTH);
//        Toast.makeText(getActivity(), "Date2:" + (year + "/" + month + "/" + day), Toast.LENGTH_SHORT).show();
//        datePicker.updateDate(year, month , day);
//    }
//
//    @Override
//    public void onTimeSet(Date date) {
//        Calendar c = Calendar.getInstance();
//        c.setTime(date);
//        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
//        int minute = c.get(Calendar.MINUTE);
//        timePicker.setCurrentHour(hourOfDay);
//        timePicker.setCurrentMinute(minute);
//    }
}
