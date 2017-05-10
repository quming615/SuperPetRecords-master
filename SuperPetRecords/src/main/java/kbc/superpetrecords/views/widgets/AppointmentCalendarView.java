package kbc.superpetrecords.views.widgets;

import android.content.*;
import android.util.*;
import android.view.*;
import java.util.*;

/**
 * Created by kellanbc on 7/4/14.
 */
public class AppointmentCalendarView extends View {

    private Context context;
    private GregorianCalendar calendar;

    public AppointmentCalendarView(Context context) {
        super(context);
        init(context);
    }

    public AppointmentCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AppointmentCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        //calendar = new GregorianCalendar();
    }
}
