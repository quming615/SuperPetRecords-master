package kbc.superpetrecords.fragments;

import android.app.*;
import android.os.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kbc.superpetrecords.MainActivity;
import kbc.superpetrecords.PetContract;
import kbc.superpetrecords.exceptions.BadRelationException;
import kbc.superpetrecords.helpers.*;
import kbc.superpetrecords.dialogfragments.*;
import kbc.superpetrecords.models.*;
import kbc.superpetrecords.R;
import kbc.superpetrecords.Toolbox;
import kbc.superpetrecords.views.widgets.*;

import android.util.*;
import android.content.*;

import java.util.HashMap;

import android.widget.*;
import java.util.*;

/**
 * Created by kellanbc on 7/4/14.
 */

public class AppointmentCalendarFragment extends Fragment {

    AppointmentCalendar calendar;
    Context context;

    public static final int CANCEL = -1;
    public static final int NEW_APPOINTMENT = 1;
    public static final int NEW_REMINDER = 2;
    public static final int REGISTER_NEW_APPOINTMENT = 4;

    public AppointmentCalendarFragment() {
        //Log.d("AppointmentCalendarFragment", "Constructor");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.d("AppointmentCalendarFragment::OnActivityResult", "Request(" + requestCode + "), Result(" + resultCode + ")");

        Bundle bundle = data.getExtras();

        if (resultCode == Activity.RESULT_OK) {
            switch(requestCode) {
                case(CANCEL):
                    //do nothing
                    break;
                case(NEW_APPOINTMENT):
                    //Toast.makeText(getActivity(), "New Appointment", Toast.LENGTH_SHORT).show();
                    Log.d("EventDate", bundle.toString());
                    EventDate date = bundle.getParcelable("data");
                    AppointmentDetailsFragment frag = AppointmentDetailsFragment.newInstance(date);
                    frag.setTargetFragment(AppointmentCalendarFragment.this, REGISTER_NEW_APPOINTMENT);
                    ((MainActivity) getActivity()).attachFragmentToContainer(frag, MainActivity.MAIN_CONTAINER, "new_appointment");
                    break;
                case(NEW_REMINDER):
                    Toast.makeText(getActivity(), "New Rminder", Toast.LENGTH_SHORT).show();
                    PetContract helpers = ((MainActivity) getActivity()).getDatabaseHelper();
                    AppointmentListFragment fragment = AppointmentListFragment.newInstance(helpers.getAppointmentsList());
                    ((MainActivity) getActivity()).attachFragmentToContainer(fragment, MainActivity.MAIN_CONTAINER, "appointment_list");
                    break;
                case(REGISTER_NEW_APPOINTMENT):
                    Bundle b = data.getExtras();
                    int interval = b.getInt("interval");
                    int count = b.getInt("count");
                    ArrayList<Integer> rrules = b.getIntegerArrayList("rrules");
                    int rrulesKey = b.getInt("frequency");

                    int startYear = b.getInt("startYear");
                    int startMonth = b.getInt("startMonth");
                    int startDate = b.getInt("startDate");
                    int startHour = b.getInt("startHour");
                    int startMinute = b.getInt("startMinute");

                    int endYear = b.getInt("endYear");
                    int endMonth = b.getInt("endMonth");
                    int endDate = b.getInt("endDate");
                    int endHour = b.getInt("endHour");
                    int endMinute = b.getInt("endMinute");

                    String description = b.getString("description");
                    String costs =  b.getString("cost");
                    float cost=Float.parseFloat(b.getString("cost"));

                    Location location = b.getParcelable("location");
                    Procedure procedure = b.getParcelable("procedure");
                    Vet vet = b.getParcelable("vet");
                    Pet pet = b.getParcelable("pet");
                    int x=pet.getId();
                    int y=location.getId();

                    String titleString = pet.toString() + ", " + procedure.toString() + ", " + vet.toString();
                    String locationString = location.getAddress() + " " + location.getUnit() + ", " + location.getCity() + ", " + location.getState() + " " + location.getZip();
                    String rrule ="once" ;

                    PetContract helper = ((MainActivity) getActivity()).getDatabaseHelper();

                    //helper.insertAppointment(vet, pet, startDate, loc)
                   /* if (rrulesKey == AppointmentCalendar.ONCE) {
                        GregorianCalendar begin = new GregorianCalendar(startYear, startMonth, startDate, startHour, startMinute);
                        GregorianCalendar end = new GregorianCalendar(endYear, endMonth, endDate, endHour, endMinute);
                        calendar.insertEvent(context, begin, end, titleString, description, locationString);
                        rrule="once";
                    } else if ((rrulesKey & AppointmentCalendar.ALL_DAY) > 0) {
                        GregorianCalendar begin = new GregorianCalendar(startYear, startMonth, startDate, 0, 0);
                        GregorianCalendar end = new GregorianCalendar(endYear, endMonth, endDate, 23, 59);
                        calendar.insertAllDayEvent(context, begin, end, titleString, description, locationString);
                        rrule="allDay";
                    } else {
                        GregorianCalendar begin = new GregorianCalendar(startYear, startMonth, startDate, startHour, startMinute);
                        GregorianCalendar end = new GregorianCalendar(endYear, endMonth, endDate, endHour, endMinute);

                        rrule="other";
                        if (count > 0) {
                            calendar.insertRecurringCountEvent(context, rrules, rrulesKey, interval, count, begin, end, titleString, description, locationString);
                        } else {
                            //TODO Add until field
                            GregorianCalendar until = new GregorianCalendar(endYear, endMonth, endDate, endHour, endMinute);
                            calendar.insertRecurringUntilEvent(context, rrules, rrulesKey, interval, until, begin, end, titleString, description, locationString);
                        }
                    }*/
                    GregorianCalendar begins = new GregorianCalendar(startYear, startMonth, startDate, startHour, startMinute);
                    GregorianCalendar ends = new GregorianCalendar(endYear, endMonth, endDate, endHour, endMinute);

                    try {
                        EventDate eventDate = helper.insertDurationEvent(startDate,startMonth,startYear,(int)begins.getTimeInMillis(),(int)ends.getTimeInMillis(),rrule) ;
                        helper.insertAppointment(vet,pet,eventDate,location,procedure,cost,description);
                    }catch(PetContract.DatabaseInsertException dataInsertException){
                        Log.d("DatabaseInsertException:","insertEventException");
                        Toast.makeText(context,"DatabaseInsertException:"+"时间选择错误！",Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(context,"insertSuccess",Toast.LENGTH_SHORT).show();
                    break;
                default:
            }
        }


    }

    public static AppointmentCalendarFragment newInstance(AppointmentCalendar calendar, int calendar_layout_id, int day_layout_id, HashMap<String, Integer> layout_ids) {
        Bundle bundle = new Bundle();
        AppointmentCalendarFragment frag = new AppointmentCalendarFragment();
        bundle.putInt("month_text", layout_ids.get("month_text"));
        bundle.putInt("year_text", layout_ids.get("year_text"));
        bundle.putInt("days_of_month_grid", layout_ids.get("days_of_month_grid"));

        bundle.putParcelable("calendar", calendar);
        bundle.putInt("calendar_layout_id", calendar_layout_id);
        bundle.putInt("day_layout_id", day_layout_id);
        frag.setArguments(bundle);
        frag.setRetainInstance(true);
        //Log.d("AppointmentCalendarFragment::newInstance", bundle.toString());

        return frag;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        //Log.d("AppointmentCalendarFragment::onAttach", "onAttach");
    }

    @Override public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle pkg = this.getArguments();
        //Log.d("AppointmentCalendarFragment::onCreate--Null Bundle" , pkg.toString());
        calendar = pkg.getParcelable("calendar");
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        super.onCreateView(inflater, container, bundle);
        Bundle args = getArguments();

        View view = inflater.inflate(args.getInt("calendar_layout_id"), container, false);
        //Log.d("AppointmentCalendarFragment::onActivityCreated", "Bundle");

        GridView daysOfWeek = (GridView) view.findViewById(R.id.dayOfWeekView);

        String[] objectProjection = {"day"};
        int[] viewProjection = {R.id.date_text};
        String[] days = getResources().getStringArray(R.array.weekdays_short);
        List<Map<String, String>> daysList = Toolbox.stringArrayToListOfMaps(days, objectProjection);
        SimpleAdapter weekdaysAdapter = new SimpleAdapter(getActivity(), daysList, args.getInt("day_layout_id"), objectProjection, viewProjection);
        daysOfWeek.setAdapter(weekdaysAdapter);

        ExtendedTextView month_text = (ExtendedTextView) view.findViewById(args.getInt("month_text"));
        month_text.setText(calendar.getDisplayMonth());

        ExtendedTextView year_text = (ExtendedTextView) view.findViewById(args.getInt("year_text"));
        year_text.setText(Integer.toString(calendar.getYear()));

        GridView days_list = (GridView) view.findViewById(args.getInt("days_of_month_grid"));
        AppointmentCalendarGridAdapter adapter = new AppointmentCalendarGridAdapter(args.getInt("day_layout_id"));
        days_list.setAdapter(adapter);

        return view;
    }

    public class AppointmentCalendarGridAdapter extends BaseAdapter {

        int grid_item_id;
        public AppointmentCalendarGridAdapter(int grid_item_id) {
            this.grid_item_id = grid_item_id;
        }

        @Override public int getCount() {
            return calendar.getWeeksInMonth() * 7;
        }

        @Override public EventDate getItem(int position) {
            int firstDay = calendar.getFirstDayOfMonth();
            if (position < firstDay - 1 || position > calendar.getDaysInMonth() + firstDay - 2) {
                return new EventDate();
            } else {
                return new EventDate(0, position - firstDay + 2, calendar.getMonth(), calendar.getYear(), "");
            }
        }

        @Override public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final EventDate date = getItem(position);

            if (convertView == null) {

                LayoutInflater inflater = getActivity().getLayoutInflater();
                convertView = inflater.inflate(grid_item_id, parent, false);
                ImageButton button = (ImageButton) convertView.findViewById(R.id.date_image_button);

                if (date.getDate() != 0) {
                    TextView text = (TextView) convertView.findViewById(R.id.date_text);
                    text.setText(Integer.toString(date.getDate()));
                    if (date.getDate() >= calendar.getDate()) {
                        if (date.getDate() == calendar.getDate()) {
                            button.setBackground(getResources().getDrawable(R.drawable.green_border));
                            button.setAlpha(.8f);
                            text.setTextColor(getResources().getColor(R.color.white));
                        } else  {
                            button.setBackground(getResources().getDrawable(R.drawable.date_selector));
                        }

                        final String[] types = new String[]{"buttton"};
                        final int[] layouts = new int[]{R.id.multipleChoiceButton};
                        final ArrayList<Bundle> map = AdapterHelper.parseStringResource(getResources(), types, R.array.appointmentCalendarDialogButtons);
                        final int[] response_codes = {NEW_APPOINTMENT, NEW_REMINDER};

                        button.setOnClickListener(new Button.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MultipleChoiceDialog frag = MultipleChoiceDialog.newInstance(
                                    "选择一个操作在 " + date.getFormattedDate(),
                                    date,
                                    R.layout.multiple_choice_dialog,
                                    R.id.multipleChoiceDialogList,
                                    R.layout.multiple_choice_button,
                                    map,
                                    types,
                                    layouts,
                                    response_codes
                                );
                                frag.setTargetFragment(AppointmentCalendarFragment.this, 0);
                                ((MainActivity) getActivity()).attachDialogFragment(frag, "multiple_choice_dialog");
                            }
                        });
                    } else {
                        button.setBackground(getResources().getDrawable(R.drawable.gray_border));
                        //button.setAlpha(.3f);
                    }

                } else {
                    button.setBackgroundColor(getResources().getColor(R.color.black));
                    button.setAlpha(.5f);
                }
            }
            return convertView;
        }
    }



}
