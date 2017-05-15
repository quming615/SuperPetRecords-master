package kbc.superpetrecords.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kbc.superpetrecords.MainActivity;
import kbc.superpetrecords.PetContract;
import kbc.superpetrecords.R;
import kbc.superpetrecords.dialogfragments.DatePickerFragment;
import kbc.superpetrecords.dialogfragments.IntervalTimePickerFragment;
import kbc.superpetrecords.dialogfragments.NewModelDialog;
import kbc.superpetrecords.dialogfragments.NotificationDialog;
import kbc.superpetrecords.dialogfragments.RRuleDialog;
import kbc.superpetrecords.models.Pet;
import kbc.superpetrecords.util.ModelAdapter;

import android.widget.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import kbc.superpetrecords.models.*;
import kbc.superpetrecords.views.widgets.ExtendedButton;
import kbc.superpetrecords.views.widgets.ExtendedEditText;

import java.util.*;

/**
 * Created by kellanbc on 7/8/14.
 */
public class AppointmentDetailsFragment extends Fragment {

    ExtendedEditText titleField, costField;
    Spinner frequencySpinner, petSpinner, selectedFrequencies, locationSpinner, procedureSpinner, vetSpinner;
    ModelAdapter frequencyAdapter, petAdapter, locationAdapter, procedureAdapter, vetAdapter;
    ArrayList<Integer> rrules = new ArrayList<>();

    ArrayList<Vet> vets;
    ArrayList<Pet> pets;
    ArrayList<Procedure> procedures;
    ArrayList<Location> locations;

    Location location;
    Procedure procedure;
    Vet vet;
    Pet pet;

    int hourStart, hourEnd, minuteStart, minuteEnd, dayEnd, yearEnd, monthEnd, dayStart, monthStart, yearStart, count, interval, frequency;
    String meridiemStart, meridiemEnd;
    TextView startDay, startMonth, startYear, endDay, endMonth, endYear, startHour, startMinute, startMeridiem, endHour, endMinute, endMeridiem;

    RelativeLayout startLayout, endLayout;

    boolean frequencySpinnerLoaded = false;
    boolean vetSpinnerLoaded = false;
    boolean locationSpinnerLoaded = false;
    boolean procedureSpinnerLoaded = false;

    public final static int SET_START_DATE_ACTION = 1;
    public final static int SET_END_DATE_ACTION = 2;
    public final static int SET_START_TIME_ACTION = 4;
    public final static int SET_END_TIME_ACTION = 8;
    public final static int ADD_NEW_EVENT = 16;
    public final static int SET_FREQUENCY = 32;
    public final static int ADD_LOCATION = 64;
    public final static int ADD_VET = 128;
    public final static int ADD_PROCEDURE = 256;


    public final RRuleFrequency[] FREQUENCY_OPTIONS = {
            new RRuleFrequency(AppointmentCalendar.ONCE, "Once"),
            new RRuleFrequency(AppointmentCalendar.ALL_DAY, "All Day"),
            new RRuleFrequency(AppointmentCalendar.BY_MINUTE, "By Minute"),
            new RRuleFrequency(AppointmentCalendar.BY_HOUR, "By Hour"),
            new RRuleFrequency(AppointmentCalendar.BY_DAY_OF_WEEK, "By Day of Week"),
            new RRuleFrequency(AppointmentCalendar.BY_DAY_OF_MONTH, "By Day of Month"),
            new RRuleFrequency(AppointmentCalendar.BY_DAY_OF_YEAR, "By Day of Year"),
            new RRuleFrequency(AppointmentCalendar.BY_WEEK_OF_MONTH, "By Week of Month"),
            new RRuleFrequency(AppointmentCalendar.BY_WEEK_OF_YEAR, "By Week of Year"),
            new RRuleFrequency(AppointmentCalendar.BY_MONTH, "By Month"),
            new RRuleFrequency(AppointmentCalendar.BY_YEAR, "By Year")
    };

    public static AppointmentDetailsFragment newInstance() {
        Bundle b = new Bundle();
        AppointmentDetailsFragment frag = new AppointmentDetailsFragment();
        frag.setArguments(b);
        return frag;
    }

    public static AppointmentDetailsFragment newInstance(EventDate date) {
        Bundle b = new Bundle();
        b.putParcelable("date", date);
        AppointmentDetailsFragment frag = new AppointmentDetailsFragment();
        frag.setArguments(b);
        frag.setRetainInstance(true);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Bundle args = getArguments();
        EventDate eventDate = args.getParcelable("date");
        Log.d("Event Date", eventDate.toString());

        dayStart = dayEnd = eventDate.getDate();
        monthStart = monthEnd = eventDate.getMonth() + 1;
        yearStart = yearEnd = eventDate.getYear();

        GregorianCalendar calendar = new GregorianCalendar(yearStart, monthStart, dayStart);

        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.appointment_details_fragment_layout, container, false);
        //description后的输入框title
        titleField = (ExtendedEditText) ll.findViewById(R.id.eventTitleField);
        //location后的选择框
        locationSpinner = (Spinner) ll.findViewById(R.id.eventLocationField);

        //查询出所有的可用地址
        locations = ((MainActivity) getActivity()).getDatabaseHelper().getLocations();
        locations.add(new Location(""));
        locations.add(new Location("Add New Location"));

        locationAdapter = new ModelAdapter<>(getActivity(), locations, R.layout.event_spinner_item, R.id.EventSpinnerItem);

        locationSpinner.setAdapter(locationAdapter);

        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                location = (Location) locationAdapter.getItem(position);
                if (location.getId() == 0) {
                    if (locationSpinnerLoaded) {
                        Log.d("New Dialog", location.toString());
                        NewModelDialog dialog = NewModelDialog.newInstance(location.getDefaults(), "Create New Location");
                        dialog.setTargetFragment(AppointmentDetailsFragment.this, ADD_LOCATION);
                        ((MainActivity) getActivity()).attachDialogFragment(dialog, "add_new_location");
                    } else {
                        locationSpinnerLoaded = true;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //TODO: Add an import from Contacts Option
        vetSpinner = (Spinner) ll.findViewById(R.id.eventVetField);
        vets = ((MainActivity) getActivity()).getDatabaseHelper().getVetss();
        vets.add(new Vet(""));
        vets.add(new Vet("Add New Vet"));
        vetAdapter = new ModelAdapter<>(getActivity(), vets, R.layout.event_spinner_item, R.id.EventSpinnerItem);
        vetSpinner.setAdapter(vetAdapter);

        vetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vet = (Vet) vetAdapter.getItem(position);
                if (vet.getId() == 0) {
                    if (vetSpinnerLoaded) {
                        NewModelDialog dialog = NewModelDialog.newInstance(vet.getDefaults(), "Add New Vet");
                        dialog.setTargetFragment(AppointmentDetailsFragment.this, ADD_VET);
                        ((MainActivity) getActivity()).attachDialogFragment(dialog, "add_new_vet");
                    } else {
                        vetSpinnerLoaded = true;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        procedureSpinner = (Spinner) ll.findViewById(R.id.eventProcedureField);
        procedures = ((MainActivity) getActivity()).getDatabaseHelper().getProcedures();
        procedures.add(new Procedure(""));
        procedures.add(new Procedure("Add New Procedure"));
        procedureAdapter = new ModelAdapter<>(getActivity(), procedures, R.layout.event_spinner_item, R.id.EventSpinnerItem);
        procedureSpinner.setAdapter(procedureAdapter);

        procedureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                procedure = (Procedure) procedureAdapter.getItem(position);
                if (procedure.getId() == 0) {
                    if (procedureSpinnerLoaded) {
                        NewModelDialog dialog = NewModelDialog.newInstance(procedure.getDefaults(), "Add New Procedure");
                        dialog.setTargetFragment(AppointmentDetailsFragment.this, ADD_PROCEDURE);
                        ((MainActivity) getActivity()).attachDialogFragment(dialog, "add_new_procedure");
                    } else {
                        procedureSpinnerLoaded = true;
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        LinearLayout startDateField = (LinearLayout) ll.findViewById(R.id.startDateField);
        startLayout = (RelativeLayout) ll.findViewById(R.id.start);
        startDay = (TextView) ll.findViewById(R.id.startDateDay);
        startDay.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        startMonth = (TextView) ll.findViewById(R.id.startDateMonth);
        startMonth.setText(String.valueOf(calendar.get(Calendar.MONTH)));
        startYear = (TextView) ll.findViewById(R.id.startDateYear);
        startYear.setText(String.valueOf(calendar.get(Calendar.YEAR)));

        startDateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment frag = new DatePickerFragment();
                frag.setTargetFragment(AppointmentDetailsFragment.this, SET_START_DATE_ACTION);
                ((MainActivity) getActivity()).attachDialogFragment(frag, "start_time");
            }
        });

        LinearLayout endDateField = (LinearLayout) ll.findViewById(R.id.endDateField);


        endLayout = (RelativeLayout) ll.findViewById(R.id.end);
        endDay = (TextView) ll.findViewById(R.id.endDateDay);
        endDay.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        endMonth = (TextView) ll.findViewById(R.id.endDateMonth);
        endMonth.setText(String.valueOf(calendar.get(Calendar.MONTH)));
        endYear = (TextView) ll.findViewById(R.id.endDateYear);
        endYear.setText(String.valueOf(calendar.get(Calendar.YEAR)));

        endDateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment frag = new DatePickerFragment();
                frag.setTargetFragment(AppointmentDetailsFragment.this, SET_END_DATE_ACTION);
                ((MainActivity) getActivity()).attachDialogFragment(frag, "start_time");
            }
        });


        LinearLayout startField = (LinearLayout) ll.findViewById(R.id.startTimeField);

        startHour = (TextView) ll.findViewById(R.id.eventStartHour);
        startMinute = (TextView) ll.findViewById(R.id.eventStartMinute);
        startMeridiem = (TextView) ll.findViewById(R.id.eventStartMeridiem);

        startField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntervalTimePickerFragment frag = new IntervalTimePickerFragment();
                frag.setTargetFragment(AppointmentDetailsFragment.this, SET_START_TIME_ACTION);
                ((MainActivity) getActivity()).attachDialogFragment(frag, "start_time");
            }
        });

        LinearLayout endField = (LinearLayout) ll.findViewById(R.id.endTimeField);

        endHour = (TextView) ll.findViewById(R.id.eventEndHour);
        endMinute = (TextView) ll.findViewById(R.id.eventEndMinute);
        endMeridiem = (TextView) ll.findViewById(R.id.eventEndMeridiem);

        endField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntervalTimePickerFragment frag = new IntervalTimePickerFragment();
                frag.setTargetFragment(AppointmentDetailsFragment.this, SET_END_TIME_ACTION);
                ((MainActivity) getActivity()).attachDialogFragment(frag, "end_time");
            }
        });


        petSpinner = (Spinner) ll.findViewById(R.id.eventPetField);
        List<Pet> pets = ((MainActivity) getActivity()).getDatabaseHelper().getPets();

        Log.d("Pets", pets.toString());

        petAdapter = new ModelAdapter<>(getActivity(), pets, R.layout.event_spinner_item, R.id.EventSpinnerItem);
        petSpinner.setAdapter(petAdapter);

        petSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pet = (Pet) petAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Log.d("PetField Spinner", pets.toString());

        frequencySpinner = (Spinner) ll.findViewById(R.id.eventFrequencyField);
        final ArrayAdapter freqAdapter = new ArrayAdapter(getActivity(), R.layout.event_spinner_item, FREQUENCY_OPTIONS);
        frequencySpinner.setAdapter(freqAdapter);

        selectedFrequencies = (Spinner) ll.findViewById(R.id.eventFrequencies);

        frequencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (frequencySpinnerLoaded) {
                    RRuleFrequency mSelected = (RRuleFrequency) parent.getItemAtPosition(position);
                    int frequency = Integer.parseInt(Long.toString(mSelected.getId()));

                    switch (frequency) {
                        case (AppointmentCalendar.ALL_DAY):

                            break;
                        case (AppointmentCalendar.ONCE):
                            break;
                        default:
                            Log.d("Frequency:onItemSelectedListener", Integer.toString(frequency));
                            GregorianCalendar cal = new GregorianCalendar(yearStart, monthStart, dayStart, hourStart, minuteStart);
                            RRuleDialog dialog = RRuleDialog.newInstance(frequency, cal, R.layout.rrule_dialog, mSelected.getText());
                            dialog.setTargetFragment(AppointmentDetailsFragment.this, SET_FREQUENCY);
                            ((MainActivity) getActivity()).attachDialogFragment(dialog, "rrule");
                    }
                } else {
                    frequencySpinnerLoaded = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        costField = (ExtendedEditText) ll.findViewById(R.id.eventPriceField);

        ExtendedButton addNew = (ExtendedButton) ll.findViewById(R.id.addNewEvent);
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                Bundle b = new Bundle();

                if (dayStart != 0 && monthStart != 0 && yearStart != 0) {

                    //b.putIntegerArrayList("rrules", rrules);
                    b.putInt("startHour", hourStart);
                    b.putInt("endHour", hourEnd);

                    b.putInt("startMinute", minuteStart);
                    b.putInt("endMinute", minuteEnd);

                    b.putString("startMeridiem", meridiemStart);
                    b.putString("endMeridiem", meridiemEnd);

                    b.putInt("startDate", dayStart);
                    b.putInt("startYear", yearStart);
                    b.putInt("startMonth", monthStart);

                    b.putInt("endDate", dayEnd);
                    b.putInt("endYear", yearEnd);
                    b.putInt("endMonth", monthEnd);

                    b.putString("description", titleField.getText().toString());
                    b.putString("cost", costField.getText().toString());

                    b.putParcelable("location", location);
                    b.putParcelable("procedure", procedure);
                    b.putParcelable("vet", vet);

                    b.putParcelable("pet", pet);

                    b.putInt("count", count);
                    b.putInt("interval", interval);
                    b.putIntegerArrayList("rrules", rrules);
                    b.putInt("frequency", frequency);
                    data.putExtras(b);

                    int x = getTargetRequestCode();
                    if (x == 0) {
                        x = ADD_NEW_EVENT;
                    }
                    int y = Activity.RESULT_OK;
                    Fragment f = getTargetFragment();
                    if (f == null) {
                        f = getParentFragment();
                        Bundle b1 = data.getExtras();
                        int interval = b1.getInt("interval");
                        int count = b1.getInt("count");
                        ArrayList<Integer> rrules = b1.getIntegerArrayList("rrules");
                        int rrulesKey = b1.getInt("frequency");

                        int startYear = b1.getInt("startYear");
                        int startMonth = b1.getInt("startMonth");
                        int startDate = b1.getInt("startDate");
                        int startHour = b1.getInt("startHour");
                        int startMinute = b1.getInt("startMinute");

                        int endYear = b1.getInt("endYear");
                        int endMonth = b1.getInt("endMonth");
                        int endDate = b1.getInt("endDate");
                        int endHour = b1.getInt("endHour");
                        int endMinute = b1.getInt("endMinute");

                        String descriptionSS = b1.getString("description");
                        String costs = b1.getString("cost");
                        float cost = Float.parseFloat(b1.getString("cost"));

                        Location location = b1.getParcelable("location");
                        Procedure procedure = b1.getParcelable("procedure");
                        Vet vet = b1.getParcelable("vet");
                        Pet pet = b1.getParcelable("pet");


                        String titleString = pet.toString() + ", " + procedure.toString() + ", " + vet.toString();
                        String locationString = location.getAddress() + " " + location.getUnit() + ", " + location.getCity() + ", " + location.getState() + " " + location.getZip();
                        String rrule = "once";

                        PetContract helpers = ((MainActivity) getActivity()).getDatabaseHelper();


                        GregorianCalendar begins = new GregorianCalendar(startYear, startMonth, startDate, startHour, startMinute);
                        GregorianCalendar ends = new GregorianCalendar(endYear, endMonth, endDate, endHour, endMinute);

                        try {
                            EventDate eventDate = helpers.insertDurationEvent(startDate, startMonth, startYear, (int) begins.getTimeInMillis(), (int) ends.getTimeInMillis(), rrule);
                            helpers.insertAppointment(vet, pet, eventDate, location, procedure, cost, descriptionSS);
                        } catch (PetContract.DatabaseInsertException dataInsertException) {
                            Log.d("DatabaseInsertException:", "insertEventException");
                            Toast.makeText(getActivity(), "DatabaseInsertException:" + "时间选择错误！", Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(getActivity(), "insertSuccess", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    getTargetFragment().onActivityResult(x, Activity.RESULT_OK, data);
                    ((MainActivity) getActivity()).swapFragments(getTargetFragment(), AppointmentDetailsFragment.this);
                } else {
                    NotificationDialog n = NotificationDialog.newInstance("Please enter all required fields to continue");
                    ((MainActivity) getActivity()).attachDialogFragment(n, "ok_dialog");
                }
            }
        });
        return ll;
    }

    private void addVet(Vet vet) {
        vetAdapter.add(vet);
        vetSpinner.setSelection(vetAdapter.getPosition(vet));
    }

    private void addProcedure(Procedure procedure) {
        procedureAdapter.add(procedure);
        procedureSpinner.setSelection(procedureAdapter.getPosition(procedure));
    }

    private void addLocation(Location location) {
        locationAdapter.add(location);
        locationSpinner.setSelection(locationAdapter.getPosition(location));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == -1) {
            Bundle bundle = data.getExtras();
            PetContract helper = ((MainActivity) getActivity()).getDatabaseHelper();
            switch (requestCode) {
                case (SET_END_TIME_ACTION):
                    hourEnd = bundle.getInt("hourOfDay");
                    minuteEnd = bundle.getInt("minute");
                    meridiemEnd = (hourEnd % 12 == 0) ? "AM" : "PM";
                    endHour.setText(Integer.toString(hourEnd % 12));

                    String minuteEndString = "00" + Integer.toString(minuteEnd);
                    endMinute.setText(minuteEndString.substring(minuteEndString.length() - 2));

                    endMeridiem.setText((hourEnd % 12 == 0) ? "AM" : "PM");
                    break;
                case (SET_START_TIME_ACTION):
                    hourStart = bundle.getInt("hourOfDay");
                    minuteStart = bundle.getInt("minute");
                    meridiemStart = (hourStart % 12 == 0) ? "AM" : "PM";
                    startHour.setText(Integer.toString(hourStart % 12));

                    String minuteStartString = "00" + Integer.toString(minuteStart);
                    startMinute.setText(minuteStartString.substring(minuteStartString.length() - 2));

                    startMeridiem.setText(meridiemStart);
                    break;
                case (SET_START_DATE_ACTION):
                    yearStart = bundle.getInt("year");
                    monthStart = bundle.getInt("month");
                    dayStart = bundle.getInt("dayOfMonth");

                    startDay.setText(Integer.toString(dayStart));
                    startMonth.setText(Integer.toString(monthStart + 1));
                    startYear.setText(Integer.toString(yearStart));

                    break;
                case (SET_END_DATE_ACTION):
                    yearEnd = bundle.getInt("year");
                    monthEnd = bundle.getInt("month");
                    dayEnd = bundle.getInt("dayOfMonth");

                    endDay.setText(Integer.toString(dayEnd));
                    endMonth.setText(Integer.toString(monthEnd + 1));
                    endYear.setText(Integer.toString(yearEnd));
                    break;

                case (ADD_NEW_EVENT):

                    break;

                case (SET_FREQUENCY):
                    Log.d("SET_FREQUENCY", bundle.toString());
                    frequency = bundle.getInt("key");
                    count = bundle.getInt("count");
                    if (count > 0) {
                        startLayout.setVisibility(View.INVISIBLE);
                        endLayout.setVisibility(View.INVISIBLE);
                    } else {
                        startLayout.setVisibility(View.VISIBLE);
                        endLayout.setVisibility(View.VISIBLE);
                    }

                    interval = bundle.getInt("interval");
                    ArrayList<String> descriptions = bundle.getStringArrayList("descriptions");
                    rrules = bundle.getIntegerArrayList("rrules");

                    ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.event_spinner_item, descriptions);
                    selectedFrequencies.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    break;
                case (ADD_LOCATION):
                    String address = bundle.getString("address");
                    String unit = bundle.getString("unit");
                    String city = bundle.getString("city");
                    String state = bundle.getString("state");
                    String zip = bundle.getString("zip");
                    try {
                        location = helper.insertLocation(address, unit, city, state, zip);
                        addLocation(location);
                    } catch (PetContract.DatabaseInsertException ex) {
                        Log.e("Database Insert Exception", ex.getMessage());
                    }
                    break;
                case (ADD_PROCEDURE):
                    String name = bundle.getString("name");
                    String description = bundle.getString("description");
                    try {
                        procedure = helper.insertProcedure(name, description);
                        addProcedure(procedure);
                    } catch (PetContract.DatabaseInsertException ex) {
                        Log.e("Database Insert Exception", ex.getMessage());
                    }

                    break;
                case (ADD_VET):
                    try {
                        String vetName = bundle.getString("name");
                        String vetEmail = bundle.getString("email_address");
                        int vetPhone = bundle.getInt("phone_number");
                        String vaddress = bundle.getString("address");
                        String vunit = bundle.getString("unit");
                        String vcity = bundle.getString("city");
                        String vstate = bundle.getString("state");
                        String vzip = bundle.getString("zip");
                        PhoneNumber phoneNumber = helper.insertPhoneNumber(ContactType.PRIMARY, vetPhone);
                        EmailAddress emailAddress = helper.insertEmailAddress(ContactType.PRIMARY, vetEmail);
                        Location locations = helper.insertLocation(vaddress, vunit, vcity, vstate, vzip);
                        Contact contact = helper.insertContact(vetName, phoneNumber, emailAddress, locations);
                        vet = helper.insertVet(vetName, contact);
                        addVet(vet);
                    } catch (PetContract.DatabaseInsertException ex) {
                        Log.e("Database Insert Exception", ex.getMessage());
                    }
                    break;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
}
