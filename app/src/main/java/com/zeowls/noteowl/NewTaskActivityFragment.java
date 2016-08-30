package com.zeowls.noteowl;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.speech.RecognizerIntent;
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
import java.util.List;
import java.util.Random;

/**
 * A placeholder fragment containing a simple view.
 */
public class NewTaskActivityFragment extends Fragment implements View.OnClickListener,
        DatePickerFragment.DatePickerFragmentListener,
        TimePickerFragment.TimePickerFragmentListener {

    EditText mTaskNameEditText,
            mDatePickerEditText,
            mTaskDetailsEditTest,
            mTimePickerEditText;

    ImageView mSpeechBtn,
            mDatePickerBtn,
            mNewListBtn,
            mTimePickerBtn;

    Spinner mSpinner;

    FloatingActionButton fab;

    SimpleCursorAdapter mCursorAdapter;
    Cursor c;
    String[] mColumns = new String[]{
            Contract.ListEntry.COLUMN_NAME,
            Contract.ListEntry._ID,
    };

    long mDate, mTime;
    long mListID;

    SQLiteDatabase db;

    String[] mListNames = new String[]{"Work", "Study", "Shopping", "Wife", "School", "Home",
            "Class", "Sweaty", "Ideas"};

    private Random mRandom;
    private static final int SPEECH_REQUEST_CODE = 0;

    public NewTaskActivityFragment() {

    }

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mTaskNameEditText.setText("");
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
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
        mTimePickerEditText = (EditText) view.findViewById(R.id.editTime);
        mTaskDetailsEditTest = (EditText) view.findViewById(R.id.editTaskDetails);
        mSpeechBtn = (ImageView) view.findViewById(R.id.btnSpeech);
        mDatePickerBtn = (ImageView) view.findViewById(R.id.btnDatePicker);
        mTimePickerBtn = (ImageView) view.findViewById(R.id.btnTimePicker);
        mNewListBtn = (ImageView) view.findViewById(R.id.btnNewList);
        mSpinner = (Spinner) view.findViewById(R.id.listSpinner);

        mDatePickerBtn.setOnClickListener(this);
        mDatePickerEditText.setOnClickListener(this);
        mTimePickerBtn.setOnClickListener(this);
        mTimePickerEditText.setOnClickListener(this);
        mSpeechBtn.setOnClickListener(this);
        mNewListBtn.setOnClickListener(this);

        if (NewTaskActivity.getSpokenText() != null){
            mTaskNameEditText.setText(NewTaskActivity.getSpokenText());
        }

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
                            mDate, mTime, null));
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
        // Insert ContentValues into database and get a row ID back
        db.insert(Contract.TaskEntry.TABLE_NAME, null, valuesData);
        // Query the database and receive a Cursor back
        db.close();
    }

    static ContentValues createTaskValues(long listRowId, String name, long date, long time,
                                          String details) {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(Contract.TaskEntry.COLUMN_LIST_ID, listRowId);
        weatherValues.put(Contract.TaskEntry.COLUMN_NAME, name);
        if (date != 0) {
            weatherValues.put(Contract.TaskEntry.COLUMN_IS_DATED, 1);
            weatherValues.put(Contract.TaskEntry.COLUMN_DATE, date);
        }
        if (time != 0) {
            weatherValues.put(Contract.TaskEntry.COLUMN_IS_TIMED, 1);
            weatherValues.put(Contract.TaskEntry.COLUMN_TIME, time);
        }
        if (details != null && !details.isEmpty()){
            weatherValues.put(Contract.TaskEntry.COLUMN_DETAILS, details);
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
        DatePickerFragment dateDialogFragment = DatePickerFragment.newInstance(this);
        TimePickerFragment timeDialogFragment = TimePickerFragment.newInstance(this);
        switch (id) {
            case R.id.btnSpeech:
                displaySpeechRecognizer();
                break;
            case R.id.editDate:
                dateDialogFragment.show(getFragmentManager(), DatePickerFragment.class.getSimpleName());
                break;
            case R.id.btnDatePicker:
                dateDialogFragment.show(getFragmentManager(), DatePickerFragment.class.getSimpleName());
                break;
            case R.id.editTime:
                timeDialogFragment.show(getFragmentManager(), TimePickerFragment.class.getSimpleName());
                break;
            case R.id.btnTimePicker:
                timeDialogFragment.show(getFragmentManager(), TimePickerFragment.class.getSimpleName());
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
        mDate = date.getTime();

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String month_name = month_date.format(c.getTime());
        int day = c.get(Calendar.DAY_OF_MONTH);
        SimpleDateFormat day_date = new SimpleDateFormat("EEE");
        String day_name = day_date.format(c.getTime());
        String dateSelected = day_name + " " + day + " " + month_name + " " + year;
        mDatePickerEditText.setText(dateSelected);
    }

    @Override
    public void onTimeSet(Date date) {
        mTime = date.getTime();

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        String timeSelected = hourOfDay + ":" + minute;
        mTimePickerEditText.setText(timeSelected);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            // Do something with spokenText
            mTaskNameEditText.setText(spokenText);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
