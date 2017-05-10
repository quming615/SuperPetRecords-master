package kbc.superpetrecords.dialogfragments;

import android.app.*;
import android.content.Intent;
import android.os.*;
import android.view.*;
import android.widget.*;

import kbc.superpetrecords.*;
import kbc.superpetrecords.views.widgets.*;
import kbc.superpetrecords.models.*;
import java.util.*;

import android.util.*;

/**
 * Created by kellanbc on 8/9/14.
 */
public class RRuleDialog extends DialogFragment {

    ArrayList<Integer> timesOfDay;
    ArrayList<Integer> months;
    ArrayList<Integer> daysOfWeek;
    ArrayList<Integer> daysOfMonth;
    ArrayList<Integer> weeksOfMonth;
    ArrayList<Integer> daysOfYear;
    ArrayList<Integer> weeksOfYear;
    ArrayList<Integer> years;

    int month = 0;
    int dayOfWeek = 0;
    int dayOfMonth = 0;
    int dayOfYear = 0;
    int weekOfYear = 0;
    int weekOfMonth = 0;
    int year = 0;
    int daysInMonth = 30;
    int timeOfDay = 0;
    int daysInYear = 365;
    int weeksInMonth = 4;

    int selectedMinute = 0;
    int selectedHour = 12;
    int selectedMeridiem = 1;
    Spinner frequencySpinner;
    ArrayAdapter frequencyAdapter;

    RRuleController controller;
    Bundle response;
    ExtendedButton adder, destroyer;
    Spinner selector;
    GridLayout checkbox_container;
    ArrayList<String> frequencies;

    IntervalTimePicker timePicker;

    private boolean hourHandIsHidden = false;
    private boolean minuteHandIsHidden = false;


    static TreeMap<String, Integer> frequencyConversions = new TreeMap<>();
    static TreeMap<Integer, String> frequencySingular = new TreeMap<>();
    static TreeMap<Integer, String> frequencyPlural = new TreeMap<>();

    static {
        frequencyConversions.put("Secondly", AppointmentCalendar.SECONDLY);
        frequencyConversions.put("Minutely", AppointmentCalendar.MINUTELY);
        frequencyConversions.put("Hourly", AppointmentCalendar.HOURLY);
        frequencyConversions.put("Daily", AppointmentCalendar.DAILY);
        frequencyConversions.put("Weekly", AppointmentCalendar.WEEKLY);
        frequencyConversions.put("Monthly", AppointmentCalendar.MONTHLY);
        frequencyConversions.put("Yearly", AppointmentCalendar.YEARLY);
    };

    static {
        frequencyPlural.put(AppointmentCalendar.SECONDLY, "Seconds");
        frequencyPlural.put(AppointmentCalendar.MINUTELY, "Minutes");
        frequencyPlural.put(AppointmentCalendar.HOURLY, "Hours");
        frequencyPlural.put(AppointmentCalendar.DAILY, "Days");
        frequencyPlural.put(AppointmentCalendar.WEEKLY, "Weeks");
        frequencyPlural.put(AppointmentCalendar.MONTHLY, "Months");
        frequencyPlural.put(AppointmentCalendar.YEARLY, "Years");
    };

    static {
        frequencySingular.put(AppointmentCalendar.SECONDLY, "Second");
        frequencySingular.put(AppointmentCalendar.MINUTELY, "Minute");
        frequencySingular.put(AppointmentCalendar.HOURLY, "Hour");
        frequencySingular.put(AppointmentCalendar.DAILY, "Day");
        frequencySingular.put(AppointmentCalendar.WEEKLY, "Week");
        frequencySingular.put(AppointmentCalendar.MONTHLY, "Month");
        frequencySingular.put(AppointmentCalendar.YEARLY, "Year");
    };

    public final static String[] DAYS_OF_WEEK = {
        "Sun", "Mon", "Tues", "Weds", "Thurs", "Fri", "Sat"
    };

    public final static String[] MONTHS = {
        "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"
    };

    int recurrence;
    int frequency;
    ViewGroup container;

    public RRuleDialog() {

    }

    public static RRuleDialog newInstance(int frequency, Calendar selectedDate, int layout_id, TreeMap<String, Integer> map, String message) {
        RRuleDialog rrule = new RRuleDialog();
        Bundle b = new Bundle();

        b.putInt("recurrence", frequency);
        b.putInt("layout_id", layout_id);
        b.putInt("destroyer_id", map.get("destroyer_id"));
        b.putInt("container_id", map.get("container_id"));
        b.putInt("closer_id", map.get("closer_id"));
        b.putInt("adder_id", map.get("adder_id"));
        b.putInt("selector_id", map.get("selector_id"));
        b.putInt("counter_id", map.get("counter_id"));
        b.putInt("repeater_id", map.get("repeater_id"));
        b.putInt("frequency_id", map.get("frequency_id"));

        b.putInt("checkbox_content_id", map.get("checkbox_content_id"));

        b.putInt("spinner_layout", map.get("spinner_layout"));
        b.putInt("spinner_item_id", map.get("spinner_item_id"));

        b.putInt("month", selectedDate.get(Calendar.MONTH));
        b.putInt("year", selectedDate.get(Calendar.YEAR));
        b.putInt("dayOfWeek", selectedDate.get(Calendar.DAY_OF_WEEK));
        b.putInt("dayOfMonth", selectedDate.get(Calendar.DAY_OF_MONTH));
        b.putInt("dayOfYear", selectedDate.get(Calendar.DAY_OF_YEAR));
        b.putInt("weekOfMonth", selectedDate.get(Calendar.WEEK_OF_MONTH));
        b.putInt("weekOfYear", selectedDate.get(Calendar.WEEK_OF_YEAR));
        b.putInt("timeOfDay", selectedDate.get(Calendar.HOUR_OF_DAY) * 60 + selectedDate.get(Calendar.MINUTE));
        b.putInt("daysInMonth", selectedDate.getActualMaximum(Calendar.DAY_OF_MONTH));
        b.putInt("daysInYear", selectedDate.getActualMaximum(Calendar.DAY_OF_YEAR));
        b.putInt("weeksInMonth", selectedDate.getActualMaximum(Calendar.WEEK_OF_MONTH));

        rrule.setArguments(b);
        return rrule;
    }


    public static RRuleDialog newInstance(int frequency, Calendar selectedDate, int layout_id, String message) {
       TreeMap<String, Integer> map = new TreeMap<>();

        map.put("container_id", R.id.rrule_content);
        map.put("closer_id", R.id.rrule_closer);
        map.put("adder_id", R.id.rrule_adder);
        map.put("selector_id", R.id.rrule_selector);
        map.put("counter_id", R.id.rrule_counter);
        map.put("repeater_id", R.id.rrule_repeater);
        map.put("destroyer_id", R.id.rrule_destroyer);
        map.put("frequency_id", R.id.rrule_frequency);
        map.put("checkbox_content_id", R.id.rrule_checkbox_content);
        map.put("spinner_layout", R.layout.rrule_spinner_item);
        map.put("spinner_item_id", R.id.item);

        return newInstance(frequency, selectedDate, layout_id, map, message);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_FRAME, 0);
        Bundle args = getArguments();
        Log.d("RRuleDialog:onCreate", args.toString());
        dayOfWeek = args.getInt("dayOfWeek");
        dayOfMonth = args.getInt("dayOfMonth");
        dayOfYear = args.getInt("dayOfYear");
        weekOfMonth = args.getInt("weekOfMonth");
        weekOfYear = args.getInt("weekOfYear");
        month = args.getInt("month");
        year = args.getInt("year");
        timeOfDay = args.getInt("timeOfDay");
        daysInMonth = args.getInt("daysInMonth");
        daysInYear = args.getInt("daysInYear");
        weeksInMonth = args.getInt("weeksInMonth");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        super.onCreateView(inflater, group, savedInstanceState);
        Bundle args = getArguments();

        int layout_id = args.getInt("layout_id");
        int container_id = args.getInt("container_id");
        int closer_id = args.getInt("closer_id");
        int adder_id = args.getInt("adder_id");
        int repeater_id = args.getInt("repeater_id");
        int counter_id = args.getInt("counter_id");
        int selector_id = args.getInt("selector_id");
        int destroyer_id = args.getInt("destroyer_id");
        final int frequency_id = args.getInt("frequency_id");

        int checkbox_content_id = args.getInt("checkbox_content_id");
        int spinner_layout = args.getInt("spinner_layout");
        int spinner_item = args.getInt("spinner_item_id");

        response = new Bundle();
        recurrence = args.getInt("recurrence");

        ViewGroup root = (ViewGroup) inflater.inflate(layout_id, group, false);
        container = (ViewGroup) root.findViewById(container_id);
        checkbox_container = (GridLayout) root.findViewById(checkbox_content_id);
        ExtendedButton close_button = (ExtendedButton) root.findViewById(closer_id);
        adder = (ExtendedButton) root.findViewById(adder_id);
        selector = (Spinner) root.findViewById(selector_id);
        destroyer = (ExtendedButton) root.findViewById(destroyer_id);
        controller = new RRuleController(recurrence);

        destroyer.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Log.d("Destroy", "onClick");
                controller.removeFocusedView();
            }
        });

        frequencySpinner = (Spinner) root.findViewById(frequency_id);

        frequencies = new ArrayList<>();

        frequencyAdapter = new ArrayAdapter<>(getActivity(), spinner_layout, spinner_item, frequencies);
        frequencySpinner.setAdapter(frequencyAdapter);

        frequencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String fString = (String) frequencyAdapter.getItem(position);
                frequency = frequencyConversions.get(fString);
                if (hourHandIsHidden) showHourHand();
                if (minuteHandIsHidden) showMinuteHand();

                switch(frequency) {
                    case(AppointmentCalendar.HOURLY):
                        hideHourHand();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        switch (recurrence) {
            case(AppointmentCalendar.BY_MINUTE):
                setFrequencyAdapter("Hourly", "Daily", "Weekly", "Monthly", "Yearly");
                setTimePicker(5, true, true, false);
                break;
            case(AppointmentCalendar.BY_HOUR):
                setFrequencyAdapter("Daily", "Weekly", "Monthly", "Yearly");
                setTimePicker(5, true, true, false);
                break;
            case(AppointmentCalendar.BY_DAY_OF_WEEK):
                //Log.d("AppointmentCalendar", "By Day of Week");
                setFrequencyAdapter("Weekly", "Monthly", "Yearly");
                setCheckBoxes("daysOfWeek", DAYS_OF_WEEK, 2);
                break;
            case(AppointmentCalendar.BY_DAY_OF_MONTH):
                setFrequencyAdapter("Monthly", "Yearly");
                Log.d("AppointmentCalendar", "By Day of Month");
                setNumberPicker("daysOfMonth", 1, daysInMonth);
                break;
            case(AppointmentCalendar.BY_DAY_OF_YEAR):
                setFrequencyAdapter("Yearly");
                Log.d("AppointmentCalendar", "By Day of Year");
                setNumberPicker("daysOfYear", 1, daysInYear);
                break;
            case(AppointmentCalendar.BY_WEEK_OF_MONTH):
                setFrequencyAdapter("Monthly", "Yearly");
                Log.d("AppointmentCalendar", "By Week of Month");
                setNumberPicker("weeksOfMonth", 1, weeksInMonth);
                break;
            case(AppointmentCalendar.BY_WEEK_OF_YEAR):
                setFrequencyAdapter("Yearly");
                Log.d("AppointmentCalendar", "By Week of Year");
                setNumberPicker("weeksOfYear", 1, 52);
                break;
            case(AppointmentCalendar.BY_MONTH):
                setFrequencyAdapter("Yearly");
                Log.d("AppointmentCalendar", "By Month");
                setCheckBoxes("months", MONTHS, 2);
                break;
            case(AppointmentCalendar.BY_YEAR):
                Log.d("AppointmentCalendar", "By Year");
                setNumberPicker("years", year, -1);
                break;
            default:
        }

        final SidewaysPicker counter = (SidewaysPicker) root.findViewById(counter_id);
        final SidewaysPicker repeater = (SidewaysPicker) root.findViewById(repeater_id);

        counter.setOnInputChangeListener(new SidewaysPicker.InputChangeListener() {
            @Override
            public void onInputIncrease(View view, int count) {

            }

            @Override
            public void onInputDecrease(View view, int count) {

            }
        });

        repeater.setOnInputChangeListener(new SidewaysPicker.InputChangeListener() {
            @Override
            public void onInputIncrease(View view, int count) {
                if (count <= 1) {
                    //frequency = frequencySingular.get(frequency_id);
                }
            }

            @Override
            public void onInputDecrease(View view, int count) {

            }
        });

        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                response.putInt("count", counter.getCount());
                response.putInt("interval", repeater.getCount());
                response.putInt("key", (recurrence << AppointmentCalendar.FREQUENCY_BIT_FIELD)|frequency);
                response.putIntegerArrayList("rrules", controller.getRRules());
                response.putStringArrayList("descriptions", controller.getDescriptions());
                intent.putExtras(response);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                ((MainActivity) getActivity()).removeDialogFragment(RRuleDialog.this.getTag());

            }
        });


        return root;
    }

    public void setFrequencyAdapter(String ... vals) {
        for (int i = 0; i < vals.length; i++) {
            frequencies.add(vals[i]);
        }
        frequencyAdapter.notifyDataSetChanged();
    }

    public void setTimePicker(int minuteInterval, final boolean showMinutes, final boolean showHours, final boolean is24HourView) {
        //Log.d("AppointmentCalendar", "By Hour Minute");
        timePicker = new IntervalTimePicker(getActivity());

        if (!showMinutes) timePicker.hideMinuteHand();
        if (!showHours) timePicker.hideMinuteHand();
        if (is24HourView) timePicker.setIs24HourView(true);

        timePicker.setMinuteInterval(minuteInterval);
        selectedHour = timePicker.getCurrentHour();
        selectedMinute = timePicker.getCurrentMinute();
        selectedMeridiem = timePicker.getCurrentMeridiem();

        timePicker.setOnTimeChangedListener(new IntervalTimePicker.OnTimeChangedListener() {
            public void onTimeChanged(IntervalTimePicker view, int hourOfDay, int minute) {
                //Log.d("OnTimeChangedListener", Integer.toString(hourOfDay) + " : " + minute);
                selectedMeridiem = view.getCurrentMeridiem();
                selectedHour = hourOfDay;
                selectedMinute = minute;
            }
        });

        TimePicker.LayoutParams lp = new TimePicker.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        timePicker.setLayoutParams(lp);
        timePicker.setPadding(0, 0, 0, 0);

        adder.setText("Add Time");
        checkbox_container.addView(timePicker);
        adder.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                int time = 0;
                String text = "";
                if (!minuteHandIsHidden && !hourHandIsHidden) {
                    time = selectedMinute + (selectedHour * 60);
                    if (!is24HourView) {
                        text = String.format("%d:%02d %s", (selectedHour % 12 == 0) ? 12 : selectedHour % 12, selectedMinute, selectedMeridiem == IntervalTimePicker.AM ? "AM" : "PM");
                    } else {
                        text = String.format("%02d:%02d", selectedHour, selectedMinute);
                    }
                } else if (!minuteHandIsHidden && hourHandIsHidden) {
                    time = selectedMinute;
                    text = String.format("%02d", selectedMinute);
                } else if (minuteHandIsHidden && !hourHandIsHidden) {
                    time = selectedHour;

                    if (!is24HourView) {
                        text = String.format("%d %s", (selectedHour % 12 == 0) ? 12 : selectedHour % 12, selectedMeridiem == IntervalTimePicker.AM ? "AM" : "PM");
                    } else {
                        text = String.format("%02d", selectedHour);
                    }

                } else {
                    return;
                }

                controller.addView(time, text);
            }
        });
    }

    private void hideMinuteHand() {
        if (timePicker != null && !minuteHandIsHidden) {
            timePicker.hideMinuteHand();
            minuteHandIsHidden = true;
        }
    }

    private void hideHourHand() {
        if (timePicker != null && !hourHandIsHidden) {
            timePicker.hideHourHand();
            hourHandIsHidden = true;
        }
    }

    private void showMinuteHand() {
        if (timePicker != null && minuteHandIsHidden) {
            timePicker.showMinuteHand();
            minuteHandIsHidden = false;
        }
    }

    private void showHourHand() {
        if (timePicker != null && hourHandIsHidden) {
            timePicker.showHourHand();
            hourHandIsHidden = false;
        }
    }

    public void setNumberPicker(final String key, int minValue, int maxValue) {
        final ArrayList<Integer> list = new ArrayList<>();
        final NumberPicker numberPicker = new NumberPicker(getActivity());
        NumberPicker.LayoutParams params = new NumberPicker.LayoutParams(NumberPicker.LayoutParams.WRAP_CONTENT, NumberPicker.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        numberPicker.setLayoutParams(params);

        if (maxValue != -1) {
            numberPicker.setMaxValue(maxValue);
        }

        numberPicker.setMinValue(minValue);

        final TextView selected = new TextView(getActivity());

        container.addView(numberPicker);
        container.addView(selected);

        adder.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                int digit = numberPicker.getValue();
                controller.addView(digit, Integer.toString(digit));
            }
        });
    }


    public void setCheckBoxes(final String key, final String[] options, int columns) {
        final CheckBox[] boxes = new CheckBox[options.length];
        ((GridLayout) checkbox_container).setColumnCount(columns);
        //GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        //params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        //params.width = GridLayout.LayoutParams.WRAP_CONTENT;
        //params.setMargins(0, 30, 0, 30);
        //container.setLayoutParams(params);
        //((GridLayout) container).setOrientation(GridLayout.VERTICAL);

        adder.setVisibility(View.INVISIBLE);
        destroyer.setVisibility(View.INVISIBLE);

        for (int i = 0; i < options.length; i++) {
            boxes[i] = new CheckBox(getActivity());
            boxes[i].setText(options[i]);
            boxes[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = ((CheckBox) v).getText().toString();
                    int index = 0;
                    for (int i = 0; i < options.length; i++) {
                        if (text == options[i]) {
                            index = i;
                            break;
                        }
                    }

                    if (!((CheckBox) v).isChecked()) {
                        controller.removeView(index);
                    } else {
                        controller.addView(index, text);
                    }
                }
            });
            checkbox_container.addView(boxes[i]);
        }
    }

    public class RRuleController {
        ArrayList<Integer> rrules;
        ArrayList<String> strings;

        TreeMap<Integer, String> selectedItems;
        TreeMap<String, Integer> selectedItemsByView;
        ArrayAdapter adapter;

        int frequency;

        public RRuleController(int frequency) {
            this.frequency = frequency;
            selectedItems = new TreeMap<>();
            selectedItemsByView = new TreeMap<>();
            strings = new ArrayList<>();
            rrules = new ArrayList<>();

            adapter = new ArrayAdapter(getActivity(), R.layout.frequency_spinner_item, strings);
            selector.setAdapter(adapter);
        }

        public ArrayList<Integer> getRRules() {
            return rrules;
        }

        public ArrayList<String> getDescriptions() {
            return strings;
        }

        public void removeFocusedView() {
            String selectedItem = (String) selector.getSelectedItem();
            if (selectedItem != null) {
                //Integer text = child.getText();
                int id = selectedItemsByView.get(selectedItem);
                Log.d("RemoveFocusedView", selectedItem + ": " + id);
                removeView(id, selectedItem);
            }
        }

        private void removeView(int id, String child) {
            selectedItemsByView.remove(child);
            selectedItems.remove(id);
            adapter.remove(child);
            rrules.remove(rrules.indexOf(id));
            adapter.notifyDataSetChanged();
        }

        public void removeView(int id) {
            String child = selectedItems.get(id);
            removeView(id, child);
        }
        public void addView(int id, String text) {
            Log.d("addView(" + id + ")", text);
            if (selectedItems.get(id) == null) {
                adapter.add(text);
                selectedItems.put(id, text);
                selectedItemsByView.put(text, id);
                rrules.add(id);
                adapter.notifyDataSetChanged();
            }
        }

    }
}
