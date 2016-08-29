package com.zeowls.noteowl;

import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.zeowls.noteowl.provider.Contract;
import com.zeowls.noteowl.provider.DBHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * A placeholder fragment containing a simple view.
 */
public class NewTaskActivityFragment extends Fragment implements View.OnClickListener, DatePickerFragment.DatePickerFragmentListener {

    EditText mTaskNameEditText,
            mDatePickerEditText,
            mTaskDetailsEditTest;

    ImageView mSpeechBtn,
            mDatePickerBtn,
            mNewListBtn;

    Spinner mSpinner;

    FloatingActionButton fab;

    SimpleCursorAdapter mCursorAdapter;
    Cursor c;
    String[] mColumns = new String[]{
            Contract.ListEntry.COLUMN_NAME,
            Contract.ListEntry._ID,
    };

    int mDay, mMonth, mYear;
    long mListID;

    SQLiteDatabase db;

    String[] mListNames = new String[]{"Work", "Study", "Shopping", "Wife", "School", "Home",
            "Class", "Sweaty", "Ideas"};
    private Random mRandom;

    public NewTaskActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_task, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTaskNameEditText = (EditText) view.findViewById(R.id.editTaskName);
        mDatePickerEditText = (EditText) view.findViewById(R.id.editDate);
        mTaskDetailsEditTest = (EditText) view.findViewById(R.id.editTaskDetails);
        mSpeechBtn = (ImageView) view.findViewById(R.id.btnSpeech);
        mDatePickerBtn = (ImageView) view.findViewById(R.id.btnDatePicker);
        mNewListBtn = (ImageView) view.findViewById(R.id.btnNewList);
        mSpinner = (Spinner) view.findViewById(R.id.listSpinner);

        mDatePickerBtn.setOnClickListener(this);
        mDatePickerEditText.setOnClickListener(this);
        mSpeechBtn.setOnClickListener(this);
        mNewListBtn.setOnClickListener(this);

        db = new DBHelper(getActivity()).getWritableDatabase();
        c = db.query(Contract.PATH_LISTS, mColumns, null, null, null, null, null);
        mRandom = new Random();

        mCursorAdapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_spinner_item, c, mColumns, new int[]{android.R.id.text1,
                android.R.id.text2});
        mCursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mCursorAdapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                c.moveToPosition(i);
                mListID = c.getInt(c.getColumnIndex(Contract.ListEntry._ID));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mTaskNameEditText.getText().toString();
                if (!name.isEmpty()) {
                    insertTask(createTaskValues(mListID, name,
                            mDay, mMonth, mYear, null));
                    getActivity().finish();
                }else {
                    Toast.makeText(getActivity(), "really!! you will do NOTHING", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        c.close();
        db.close();
    }

    static ContentValues createListValues(String name) {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(Contract.ListEntry.COLUMN_NAME, name);
        return testValues;
    }

    public void insertTask(ContentValues valuesData) {
        SQLiteDatabase db = new DBHelper(getActivity()).getWritableDatabase();
        // Create ContentValues of what you want to insert
        // (you can use the createTaskValues TestUtilities function if you wish)
        // Insert ContentValues into database and get a row ID back
        db.insert(Contract.TaskEntry.TABLE_NAME, null, valuesData);

        // Query the database and receive a Cursor back
        db.close();
    }

    static ContentValues createTaskValues(long listRowId, String name, int day, int month, int year,
                                          String Details) {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(Contract.TaskEntry.COLUMN_LIST_ID, listRowId);
        weatherValues.put(Contract.TaskEntry.COLUMN_NAME, name);
        if (day != 0 && month != 0 && year != 0) {
            weatherValues.put(Contract.TaskEntry.COLUMN_IS_DATED, 1);
            weatherValues.put(Contract.TaskEntry.COLUMN_DAY, day);
            weatherValues.put(Contract.TaskEntry.COLUMN_MONTH, month);
            weatherValues.put(Contract.TaskEntry.COLUMN_YEAR, year);
        }
        return weatherValues;
    }

    public long insertList(ContentValues valuesData) {
        SQLiteDatabase db = new DBHelper(getActivity()).getWritableDatabase();
        return db.insert(Contract.ListEntry.TABLE_NAME, null, valuesData);
    }

//    public void testTaskTable() {
//        long listRowId = insertList();
//        SQLiteDatabase db = new DBHelper(getActivity()).getWritableDatabase();
//        // Create ContentValues of what you want to insert
//        // (you can use the createTaskValues TestUtilities function if you wish)
//        ContentValues valuesData;
//        for (int i = 0; i <= 10; i++) {
//            valuesData = createTaskValues(listRowId, "task " + i);
//            // Insert ContentValues into database and get a row ID back
//            db.insert(Contract.TaskEntry.TABLE_NAME, null, valuesData);
//        }
//        // Query the database and receive a Cursor back
//        db.close();
//    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        DatePickerFragment newFragment = DatePickerFragment.newInstance(this);
        switch (id) {
            case R.id.editDate:
                newFragment.show(getFragmentManager(), DatePickerFragment.class.getSimpleName());
                break;
            case R.id.btnDatePicker:
                newFragment.show(getFragmentManager(), DatePickerFragment.class.getSimpleName());
                break;
            case R.id.btnSpeech:
                break;
            case R.id.btnNewList:
                insertList(createListValues(mListNames[mRandom.nextInt(mListNames.length - 1)]));
                c = db.query(Contract.PATH_LISTS, mColumns, null, null, null, null, null);
                mCursorAdapter.swapCursor(c);
                mCursorAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onDateSet(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String month_name = month_date.format(c.getTime());
        mDay = c.get(Calendar.DAY_OF_MONTH);
        SimpleDateFormat day_date = new SimpleDateFormat("EEE");
        String day_name = day_date.format(c.getTime());
        String dateSelected = day_name + " " + mDay + " " + month_name + " " + mYear;
        mDatePickerEditText.setText(dateSelected);
    }
}
