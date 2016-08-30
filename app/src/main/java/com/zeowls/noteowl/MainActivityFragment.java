package com.zeowls.noteowl;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.zeowls.noteowl.provider.Contract;
import com.zeowls.noteowl.provider.DBHelper;

import java.text.SimpleDateFormat;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements View.OnClickListener {

//    Button timePickerButton, datePickerButton;
//    DatePicker datePicker;
//    TimePicker timePicker;

    private static final SQLiteQueryBuilder sTaskWithListNameQueryBuilder;

    static {
        sTaskWithListNameQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sTaskWithListNameQueryBuilder.setTables(
                Contract.TaskEntry.TABLE_NAME + " INNER JOIN " +
                        Contract.ListEntry.TABLE_NAME +
                        " ON " + Contract.TaskEntry.TABLE_NAME +
                        "." + Contract.TaskEntry.COLUMN_LIST_ID +
                        " = " + Contract.ListEntry.TABLE_NAME +
                        "." + Contract.ListEntry._ID);
    }

    ListView mListView;
    CursorAdapter cursorAdapter;
    Cursor c;
    String[] mColumns = new String[]{
            Contract.TaskEntry.COLUMN_NAME,
            Contract.TaskEntry.COLUMN_DATE,
            Contract.ListEntry.COLUMN_NAME,
            Contract.TaskEntry.COLUMN_IS_FINISHED,
            Contract.TaskEntry._ID
    };

    SQLiteDatabase db;

    public MainActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);

        db = new DBHelper(getActivity()).getWritableDatabase();
        mListView = (ListView) v.findViewById(R.id.list_view);
        SQLiteDatabase db = new DBHelper(getActivity()).getWritableDatabase();
        c = sTaskWithListNameQueryBuilder.query(db, null, null, null, null, null, null);
        cursorAdapter = new MySimpleCursorAdapter(getActivity(),
                R.layout.home_list_item, c, mColumns, new int[]{R.id.tvTaskName, R.id.tvTaskDate, R.id.tvTaskList});
        mListView.setAdapter(cursorAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
        db.close();
        return v;
    }

    public static String getDate(Long milliSeconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(milliSeconds);
    }

    private class MySimpleCursorAdapter extends SimpleCursorAdapter {
        public MySimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
            super(context, layout, c, from, to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);

            c.moveToPosition(position);
            long date = c.getLong(c.getColumnIndex(Contract.TaskEntry.COLUMN_DATE));
            if (date == 0){
                v.findViewById(R.id.imRepeat).setVisibility(View.GONE);
                v.findViewById(R.id.tvTaskDate).setVisibility(View.GONE);
            }
            return v;
        }

//        @Override
//        public void setViewBinder(ViewBinder viewBinder) {
//            ViewBinder vb = new ViewBinder() {
//                @Override
//                public boolean setViewValue(View view, Cursor cursor, int i) {
//                    if (view.getId() == R.id.imRepeat) {
//                        ImageView IV = (ImageView) view;
//                        if (cursor.getLong(cursor.getColumnIndex(Contract.TaskEntry.COLUMN_DATE)) == 0) {
//                            IV.setVisibility(View.GONE);
//                        }
//                    }
//                    return false;
//                }
//            };
//            super.setViewBinder(vb);
//        }

        @Override
        public void setViewText(TextView v, String text) {
            if (v.getId() == R.id.tvTaskDate) {
                // Make sure it matches your time field
                // You may want to try/catch with NumberFormatException in case `text` is not a numeric value
                try {
                    text = MainActivityFragment.getDate(Long.parseLong(text), "dd. MMM yyyy");
                } catch (NumberFormatException e) {
                    if (e == null){
                        Log.e(this.getClass().getSimpleName(), "Null Exception");
                    }else {
                        e.printStackTrace();
                    }
                }
            }
            v.setText(text);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        c = sTaskWithListNameQueryBuilder.query(db, null, null, null, null, null, null);
        cursorAdapter.swapCursor(c);
        cursorAdapter.notifyDataSetChanged();
        mListView.setAdapter(cursorAdapter);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fargment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_clear) {
            db.delete(Contract.PATH_TASKS, null, null);
            db.delete(Contract.PATH_LISTS, null, null);
            c = db.query(Contract.PATH_TASKS, mColumns, null, null, null, null, null);
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
