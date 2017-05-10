package kbc.superpetrecords.dialogfragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by kellanbc on 7/9/14.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        Calendar calendar = new GregorianCalendar();
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        return new DatePickerDialog((Context) getActivity(), this, year, month, date);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Intent i = new Intent();
        Log.d("DatePickerFragment:onDateSet", dayOfMonth + "/" + monthOfYear + "/" + year);
        i.putExtra("year", year);
        i.putExtra("month", monthOfYear);
        i.putExtra("dayOfMonth", dayOfMonth);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
    }


}
