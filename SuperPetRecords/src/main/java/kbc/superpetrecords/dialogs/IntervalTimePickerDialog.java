package kbc.superpetrecords.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import kbc.superpetrecords.R;
import kbc.superpetrecords.views.widgets.*;

/**
 * Created by kellanbc on 8/24/14.
 */
public class IntervalTimePickerDialog extends AlertDialog implements DialogInterface.OnClickListener, IntervalTimePicker.OnTimeChangedListener {


    public interface OnTimeSetListener {
        void onTimeSet(IntervalTimePicker view, int hourOfDay, int minute);
    }

    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";
    private static final String IS_24_HOUR = "is24hour";
    private static final String MINUTE_INTERVAL = "minuteInterval";

    private final IntervalTimePicker mIntervalTimePicker;
    private final OnTimeSetListener mCallback;

    int mInitialHourOfDay;
    int mInitialMinute;
    boolean mIs24HourView;

    public IntervalTimePickerDialog(Context context, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView, int minuteInterval) {
        this(context, 0, callBack, hourOfDay, minute, is24HourView, minuteInterval);
    }

    public IntervalTimePickerDialog(Context context, int theme, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView, int minuteInterval) {
        super(context, theme);
        mCallback = callBack;
        mInitialHourOfDay = hourOfDay;
        mInitialMinute = minute;
        mIs24HourView = is24HourView;

        setIcon(context.getResources().getDrawable(R.drawable.alarmclock_badge));
        setTitle(R.string.interval_time_picker_dialog_title);

        Context themeContext = getContext();

        setButton(BUTTON_POSITIVE, themeContext.getText(R.string.date_time_done), this);

        LayoutInflater inflater = (LayoutInflater) themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.interval_time_picker_dialog, null);
        setView(view);
        mIntervalTimePicker = (IntervalTimePicker) view.findViewById(R.id.interval_time_picker);

        mIntervalTimePicker.setIs24HourView(mIs24HourView);
        mIntervalTimePicker.setCurrentHour(mInitialHourOfDay);
        mIntervalTimePicker.setCurrentMinute(mInitialMinute);
        mIntervalTimePicker.setMinuteInterval(minuteInterval);
        mIntervalTimePicker.setOnTimeChangedListener(this);
    }

    public void onClick(DialogInterface dialog, int which) {
        tryNotifyTimeSet();
    }

    public void updateTime(int hourOfDay, int minutOfHour) {
        mIntervalTimePicker.setCurrentHour(hourOfDay);
        mIntervalTimePicker.setCurrentMinute(minutOfHour);
    }

    private void tryNotifyTimeSet() {
        if (mCallback != null) {
            mIntervalTimePicker.clearFocus();
            mCallback.onTimeSet(mIntervalTimePicker, mIntervalTimePicker.getCurrentHour(), mIntervalTimePicker.getCurrentMinute());
        }
    }

    @Override
    protected void onStop() {
        tryNotifyTimeSet();
        super.onStop();
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(HOUR, mIntervalTimePicker.getCurrentHour());
        state.putInt(MINUTE, mIntervalTimePicker.getCurrentMinute());
        state.putBoolean(IS_24_HOUR, mIntervalTimePicker.is24HourView());
        state.putInt(MINUTE_INTERVAL, mIntervalTimePicker.getMinuteInterval());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int hour = savedInstanceState.getInt(HOUR);
        int minute = savedInstanceState.getInt(MINUTE);
        int minuteInterval = savedInstanceState.getInt(MINUTE_INTERVAL);
        mIntervalTimePicker.setIs24HourView(savedInstanceState.getBoolean(IS_24_HOUR));
        mIntervalTimePicker.setCurrentHour(hour);
        mIntervalTimePicker.setCurrentMinute(minute);
        mIntervalTimePicker.setMinuteInterval(minuteInterval);
    }

    @Override
    public void onTimeChanged(IntervalTimePicker view, int hourOfDay, int minute) {
        /* Set By User */
    }
}
