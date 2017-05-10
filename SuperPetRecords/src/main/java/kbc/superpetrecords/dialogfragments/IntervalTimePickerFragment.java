package kbc.superpetrecords.dialogfragments;

/**
 * Created by kellanbc on 7/19/14.
 */

import android.annotation.*;
import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.util.*;
import java.lang.reflect.*;

import kbc.superpetrecords.MainActivity;
import kbc.superpetrecords.R;
import kbc.superpetrecords.dialogs.IntervalTimePickerDialog;
import kbc.superpetrecords.views.widgets.ExtendedButton;
import kbc.superpetrecords.views.widgets.IntervalTimePicker;

public class IntervalTimePickerFragment extends DialogFragment implements IntervalTimePicker.OnTimeChangedListener {

    private int mMinute, mHour;

    public IntervalTimePickerFragment() {}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, 0);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle bundle) {
        super.onCreateView(inflater, parent, bundle);

        Calendar calendar = new GregorianCalendar();
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);

        View view = inflater.inflate(R.layout.interval_time_picker_dialog, null);
        ExtendedButton ok = (ExtendedButton) view.findViewById(R.id.ok);
        IntervalTimePicker mIntervalTimePicker = (IntervalTimePicker) view.findViewById(R.id.interval_time_picker);
        mIntervalTimePicker.setIs24HourView(false);
        mIntervalTimePicker.setCurrentHour(mMinute);
        mIntervalTimePicker.setCurrentMinute(mHour);
        mIntervalTimePicker.setOnTimeChangedListener(this);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                Log.d("DatePickerFragment:onDateSet", mHour + ":" + mMinute);
                i.putExtra("hourOfDay", mHour);
                i.putExtra("minute", mMinute);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                ((MainActivity) getActivity()).removeDialogFragment(getTag());
            }
        });
        return view;
    }

    @Override
    public void onTimeChanged(IntervalTimePicker view, int hourOfDay, int minute) {
        mHour = hourOfDay;
        mMinute = minute;
    }
}
