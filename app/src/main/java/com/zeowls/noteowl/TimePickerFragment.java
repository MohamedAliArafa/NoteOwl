package com.zeowls.noteowl;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private TimePickerFragmentListener timePickerListener;
    public interface TimePickerFragmentListener {
        public void onTimeSet(Date date);
    }

    public TimePickerFragmentListener getTimePickerListener() {
        return this.timePickerListener;
    }

    public void setTimePickerListener(TimePickerFragmentListener listener) {
        this.timePickerListener = listener;
    }

    protected void notifyTimePickerListener(Date date) {
        if(this.timePickerListener != null) {
            this.timePickerListener.onTimeSet(date);
        }
    }

    public static TimePickerFragment newInstance(TimePickerFragmentListener listener) {
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setTimePickerListener(listener);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        Date date = c.getTime();

        // Here we call the listener and pass the date back to it.
        notifyTimePickerListener(date);
    }
}
