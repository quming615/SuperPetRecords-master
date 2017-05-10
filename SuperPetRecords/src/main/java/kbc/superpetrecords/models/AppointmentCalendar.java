package kbc.superpetrecords.models;

import android.content.*;
import android.database.*;
import android.net.*;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.*;
import java.io.*;
import android.provider.CalendarContract.*;
import android.util.*;

import java.lang.ref.*;
import java.util.*;

/**
 * Created by kellanbc on 7/4/14.
 */
public class AppointmentCalendar implements Parcelable {

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
    private static final int PROJECTION_ACCOUNT_TYPE_INDEX = 4;

    public static final int ONCE = 0;

    public static final int BY_HOUR = 1;
    public static final int BY_MINUTE = 2;
    public static final int BY_DAY_OF_WEEK = 4;
    public static final int BY_DAY_OF_MONTH = 8;
    public static final int BY_DAY_OF_YEAR = 16;
    public static final int BY_WEEK_OF_MONTH = 32;
    public static final int BY_WEEK_OF_YEAR = 64;
    public static final int BY_MONTH = 128;
    public static final int BY_YEAR = 256;
    public static final int ALL_DAY = 512;

    public static final int SECONDLY = 1; //512;
    public static final int MINUTELY = 2; //1024;
    public static final int HOURLY = 4; //2048;
    public static final int DAILY = 8; //4096;
    public static final int WEEKLY = 16; //8192;
    public static final int MONTHLY = 32; //16384;
    public static final int YEARLY = 64; //32768;

    public static final int FREQUENCY_BIT_FIELD = 10;
    public static final int RECURRENCE_BIT_FIELD = 7;

    public static final int FREQUENCY_BIT_MASK = (1 << FREQUENCY_BIT_FIELD) - 1;
    public static final int RECURRENCE_BIT_MASK = ((1 << RECURRENCE_BIT_FIELD) - 1) << FREQUENCY_BIT_FIELD;

    public static TreeMap<Integer, String> freqConversion = new TreeMap<>();
    static {
        freqConversion.put(SECONDLY, "SECONDLY");
        freqConversion.put(MINUTELY, "MINUTELY");
        freqConversion.put(HOURLY, "HOURLY");
        freqConversion.put(DAILY, "DAILY");
        freqConversion.put(WEEKLY, "WEEKLY");
        freqConversion.put(MONTHLY, "MONTHLY");
        freqConversion.put(YEARLY, "YEARLY");
    };

    public static TreeMap<Integer, String> recurConversion = new TreeMap<>();
    static {
        recurConversion.put(BY_HOUR, "BYHOUR");
        recurConversion.put(BY_MINUTE, "BYMINUTE");
        recurConversion.put(BY_DAY_OF_WEEK, "BYDAY");
        recurConversion.put(BY_DAY_OF_MONTH, "BYMONTHDAY");
        recurConversion.put(BY_DAY_OF_YEAR, "BYYEARDAY");
        recurConversion.put(BY_WEEK_OF_MONTH, "BYWEEKNO");
        recurConversion.put(BY_WEEK_OF_YEAR, "BYWEEKNO");
        recurConversion.put(BY_MONTH, "BYMONTH");
        recurConversion.put(BY_YEAR, "");


    };

    public static TreeMap<Integer, String> weekdayConversion = new TreeMap<>();
    static {
        weekdayConversion.put(0, "SU");
    weekdayConversion.put(1, "MO");
    weekdayConversion.put(2, "TU");
    weekdayConversion.put(3, "WE");
    weekdayConversion.put(4, "TH");
    weekdayConversion.put(5, "FR");
    weekdayConversion.put(6, "SA");
};

    public static final String[] CALENDARS_PROJECTION = new String[] {
        Calendars._ID,
        Calendars.ACCOUNT_NAME,
        Calendars.CALENDAR_DISPLAY_NAME,
        Calendars.OWNER_ACCOUNT,
        Calendars.ACCOUNT_TYPE
    };

    public static final String[] EVENTS_PROJECTION = new String[] {
        Events._ID,
        Events.CALENDAR_ID,         //mandatory for insert
        Events.DTSTART,             //mandatory for insert
        Events.ORGANIZER,
        Events.TITLE,
        Events.EVENT_LOCATION,
        Events.DESCRIPTION,
        Events.DTEND,
        Events.EVENT_TIMEZONE,
        Events.EVENT_END_TIMEZONE,
        Events.DURATION,
        Events.ALL_DAY,
        Events.RDATE,
        Events.RRULE,
    };

    public static final String[] REMINDERS_PROJECTION = new String[]{
        Reminders._ID,
        Reminders.EVENT_ID,
        Reminders.MINUTES,
        Reminders.METHOD
    };

    public static final String[] INSTANCES_PROJECTION = new String[]{
        Instances._ID,
        Instances.BEGIN,
        Instances.END,
        Instances.END_DAY,
        Instances.END_MINUTE,
        Instances.EVENT_ID,
        Instances.START_DAY,
        Instances.START_MINUTE
    };

    private long calId;
    private int month, day, year, days_in_month, first_month_day, month_weeks, day_of_week;
    String display_month;
    Locale locale;

    public AppointmentCalendar(Parcel in) {
        calId = in.readInt();
        day = in.readInt();
        month = in.readInt();
        year = in.readInt();
        days_in_month = in.readInt();
        first_month_day = in.readInt();
        month_weeks = in.readInt();
        display_month = in.readString();
        day_of_week = in.readInt();
        locale = Locale.getDefault();
    }

    public AppointmentCalendar(long calId) {
        this.calId = calId;
        locale = Locale.getDefault();

        Calendar now = GregorianCalendar.getInstance();
        month = now.get(Calendar.MONTH);
        day = now.get(Calendar.DAY_OF_MONTH);
        year = now.get(Calendar.YEAR);

        days_in_month = now.getMaximum(Calendar.DAY_OF_MONTH);

        Calendar first_day = new GregorianCalendar(year, month, 1);
        first_month_day = first_day.get(Calendar.DAY_OF_WEEK);
        month_weeks = now.getMaximum(Calendar.WEEK_OF_MONTH);
        day_of_week = now.get(Calendar.DAY_OF_WEEK);

        display_month = now.getDisplayName(Calendar.MONTH, Calendar.LONG, locale);

        Log.d("GregorianCalendar", "Day(" + day + "), Month(" + month + "), Year(" + year + "), MonthDays(" + days_in_month + "), FirstMonthDay(" + first_month_day + ")");
    }

    /*
    public static AppointmentCalendar createNewCalendar(String calendarName, String accountName, String accountType) {
        Uri target = Uri.parse(CalendarContract.Calendars.CONTENT_URI.toString());
        target = target.buildUpon().appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
            .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
            .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, accountType).build();

        ContentValues values = new ContentValues();
        values.put(Calendars.ACCOUNT_NAME, accountName);
        values.put(Calendars.ACCOUNT_TYPE, accountType);
        values.put(Calendars.NAME, calendarName);
        values.put(Calendars.CALENDAR_DISPLAY_NAME, calendarName);
        values.put(Calendars.CALENDAR_COLOR, 0x00FF00);
        values.put(Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_ROOT);
        values.put(Calendars.OWNER_ACCOUNT, accountName);
        values.put(Calendars.VISIBLE, 1);
        values.put(Calendars.SYNC_EVENTS, 1);
        values.put(Calendars.CALENDAR_TIME_ZONE, "Europe/Rome");
        values.put(Calendars.CAN_PARTIALLY_UPDATE, 1);

        values.put(Calendars.CAL_SYNC1, "https://www.google.com/calendar/feeds/" + accountName + "/private/full");
        values.put(Calendars.CAL_SYNC2, "https://www.google.com/calendar/feeds/default/allcalendars/full/" + accountName);
        values.put(Calendars.CAL_SYNC3, "https://www.google.com/calendar/feeds/default/allcalendars/full/" + accountName);

        values.put(Calendars.CAL_SYNC4, 1);
        values.put(Calendars.CAL_SYNC5, 0);
        values.put(Calendars.CAL_SYNC8, System.currentTimeMillis());

        Uri newCalendar = ctx.getContentResolver().insert(target, values);

        return newCalendar;
    }
    */

    public AppointmentCalendar() {}

    public String getDisplayMonth() {
        return display_month;
    }

    public int getYear() {
        return year;
    }

    public int getDayOfWeek() { return day_of_week; }

    public int getMonth() {
        return month;
    }

    public int getDate() {
        return day;
    }

    public int getDaysInMonth() {
        return days_in_month;
    }


    public int getWeeksInMonth() {
        return month_weeks;
    }

    public int getFirstDayOfMonth() {
        return first_month_day;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(calId);
        dest.writeInt(day);
        dest.writeInt(month);
        dest.writeInt(year);
        dest.writeInt(days_in_month);
        dest.writeInt(first_month_day);
        dest.writeInt(month_weeks);
        dest.writeString(display_month);
        dest.writeInt(day_of_week);
    }

    public void insertAllDayEvent(Context context, GregorianCalendar begin, GregorianCalendar end, String title, String description, String location) {
        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Events.ALL_DAY, 1);

        parseInsertEvent(cr, values, begin, end, title, description, location);
    }

    public void insertRecurringCountEvent(Context context, ArrayList<Integer> rrules, int rrulesKey, int interval, int count, GregorianCalendar begin, GregorianCalendar end, String title, String description, String location) {
        String frequencyString = parseRRules(rrules, rrulesKey, interval, count);
        insertRecurringEvent(context, frequencyString, begin, end, title, description, location);
    }

    public void insertRecurringUntilEvent(Context context, ArrayList<Integer> rrules, int rrulesKey, int interval, GregorianCalendar until, GregorianCalendar begin, GregorianCalendar end, String title, String description, String location) {
        String frequencyString = parseRRules(rrules, rrulesKey, interval, 0);
        //UNTIL=19980404 T070000Z
        insertRecurringEvent(context, frequencyString, begin, end, title, description, location);
    }

    private void insertRecurringEvent(Context context, String frequency, GregorianCalendar begin, GregorianCalendar end, String title, String description, String location) {

        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();

        //values.put(Events.RDATE, )
        //values.put(Events.RRULE, );
        parseInsertEvent(cr, values, null, null, title, description, location);
    }

    public void insertEvent(Context context, GregorianCalendar begin, GregorianCalendar end, String title, String description, String location) {
        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        parseInsertEvent(cr, values, begin, end, title, description, location);
    }

    private void parseInsertEvent(ContentResolver resolver, ContentValues values, GregorianCalendar begin, GregorianCalendar end, String title, String description, String location) {

        String timezone = TimeZone.getDefault().getDisplayName();

        values.put(Events.CALENDAR_ID, calId);
        values.put(Events.EVENT_TIMEZONE, timezone);
        values.put(Events.TITLE, title);
        values.put(Events.DESCRIPTION, description);
        values.put(Events.DTSTART, begin.getTimeInMillis());
        values.put(Events.DTEND, end.getTimeInMillis());
        values.put(Events.DURATION, parseDuration(begin, end));
        values.put(Events.EVENT_LOCATION, location);
        Uri uri = resolver.insert(Events.CONTENT_URI, values);

        int token = 1; //Identifier
        Object cookie = new Appointment(); //Object passed into onQueryComplete

        CalendarInsertListener listener = new CalendarInsertListener() {
            @Override public void onInsertComplete(int token, Object cookie, Uri uri) {
                Log.d("AppointmentCalendar:CalenderInsertListener", "Query Complete: " + uri.toString());

            }
        };

        CalendarQueryHandler handler = new CalendarQueryHandler(resolver, listener);

        handler.startInsert(token, cookie, uri, values);
    }

    private String parseDuration(GregorianCalendar begin, GregorianCalendar end) {
        String duration = "PT";
        long millis = end.compareTo(begin);
        long seconds = (millis / 1000) % 60;
        long minutes = (seconds / 60) % 60;
        long hours = (minutes / 60) % 24;
        //long days = (hours / 24) % 7;
        //long weeks = (days / 7);

        duration += hours + "H" + minutes + "M";
        Log.d("ParseDuration", duration);
        return duration;
    }

    public void updateCalendar(Context context, String column, String new_value, String[] selection, String[] selection_args) {

        CalendarUpdateListener listener = new CalendarUpdateListener() {
            @Override public void onUpdateComplete(int token, Object cookie, int result) {
                Log.d("AppointmentCalendar:CalenderQueryListener", "Query Complete: " + result);
            }
        };
        parseUpdateQuery(context, listener, Calendars.CONTENT_URI, calId, column, new_value, selection, selection_args);
    }

    public void getCalendars(final Context context, CalendarQueryListener listener, String[] selection, String[] selection_args) {
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        parseSelectQuery(context, listener, uri, CALENDARS_PROJECTION, selection, selection_args);
    }

    public void setCalendarId(int id) {
        calId = id;
    }

    private void parseUpdateQuery(Context context, CalendarUpdateListener listener, Uri uri, long calID, String column, String new_value, String[] selection, String[] selection_args) {
        ContentResolver cr = context.getContentResolver();
        CalendarQueryHandler handler = new CalendarQueryHandler(cr, listener);

        ContentValues cValues = new ContentValues();
        cValues.put(column, new_value);
        Uri updateUri = ContentUris.withAppendedId(uri, calID);

        String selection_string = parseSelectionString(selection);

        int token = 1;                              //A token passed to onQueryComplete to identiy the query
        Object cookie = null;               //Object passed into onQueryComplete
        String orderBy = null;                      //SQLite orderBy Query

        handler.startUpdate(token, cookie, updateUri, cValues, selection_string, selection_args);
    }

    private void parseSelectQuery(Context context, CalendarQueryListener listener,  Uri uri, String[] projection, String[] selection, String[] selection_args) {
        ContentResolver cr = context.getContentResolver();
        CalendarQueryHandler handler = new CalendarQueryHandler(cr, listener);

        String selection_string = parseSelectionString(selection);

        int token = 1;                              //A token passed to onQueryComplete to identiy the query
        Object cookie = new Object();               //Object passed into onQueryComplete
        String orderBy = null;                      //SQLite orderBy Query

        handler.startQuery(token, cookie, uri, projection, selection_string, selection_args, orderBy);
    }

    public static String parseSelectionString(String[] selection) {
        String selection_string;

        if (selection != null && selection.length > 0) {
            int selection_count = selection.length;
            selection_string = "((";

            for (int i = 0; i < selection_count; i++) {
                selection_string += selection[i] + " ";
                if (i != selection_count) {
                    selection_string += "= ?) AND (";
                } else {
                    selection_string += "= ?))";
                }
            }
            Log.d("SelectionString:", selection_string);
            return selection_string;
        }
        return null;
    }

    public String parseRRules (ArrayList<Integer> rrules, int key, int interval, int count) {
        String frequency = "";
        String by = "";
        String digits = "";

        if ((key & BY_DAY_OF_WEEK) > 0) {
            for (int i = 0; i < rrules.size(); i++) {
                digits += weekdayConversion.get(rrules.get(i));
                if (i != rrules.size() - 1) {
                    digits += ",";
                }
            }
        } else {
            for (int i = 0; i < rrules.size(); i++) {

                digits += rrules.get(i).toString();

                if (i != rrules.size() - 1) {
                    digits += ",";
                }
            }
        }

        frequency = freqConversion.get(key & FREQUENCY_BIT_MASK);
        by = recurConversion.get((key & RECURRENCE_BIT_MASK) >>> FREQUENCY_BIT_FIELD);

        String fString = "RRULE:FREQ=" + frequency + ";";
        if (digits.length() > 0) {
            fString += by + "=" + digits + ";";
        }

        if (count > 0) {
            fString += "COUNT=" + count + ";";
        } else if (interval > 0) {
            fString += "INTERVAL=" + interval + ";";
        }

        Log.d("FSTRING", fString);
        return fString;
    }

    public interface CalendarQueryListener {
        void onQueryComplete(int token, Object cookie, Cursor cursor);
    }

    public interface CalendarUpdateListener {
        void onUpdateComplete(int token, Object cookie, int result);
    }

    public interface CalendarDeleteListener {
        void onDeleteComplete(int token, Object cookie, int result);
    }

    public interface CalendarInsertListener {
        void onInsertComplete(int token, Object cookie, Uri uri);
    }

    public class CalendarQueryHandler extends AsyncQueryHandler {
        private WeakReference<CalendarQueryListener> qListener;
        private WeakReference<CalendarUpdateListener> uListener;
        private WeakReference<CalendarInsertListener> iListener;
        private WeakReference<CalendarDeleteListener> dListener;

        public CalendarQueryHandler(ContentResolver cr, CalendarUpdateListener listener) {
            super(cr);
            uListener = new WeakReference<CalendarUpdateListener>(listener);
        }

        public CalendarQueryHandler(ContentResolver cr, CalendarQueryListener listener) {
            super(cr);
            qListener = new WeakReference<CalendarQueryListener>(listener);
        }

        public CalendarQueryHandler(ContentResolver cr, CalendarDeleteListener listener) {
            super(cr);
            dListener = new WeakReference<CalendarDeleteListener>(listener);
        }

        public CalendarQueryHandler(ContentResolver cr, CalendarInsertListener listener) {
            super(cr);
            iListener = new WeakReference<CalendarInsertListener>(listener);
        }

        @Override protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            final CalendarQueryListener listener = qListener.get();
            if (listener != null) {
                listener.onQueryComplete(token, cookie, cursor);
            } else if (cursor != null) {
                cursor.close();
            }
        }

        @Override protected void onUpdateComplete(int token, Object cookie, int result) {
            final CalendarUpdateListener listener = uListener.get();
            if (listener != null) {
                listener.onUpdateComplete(token, cookie, result);
            }
        }

        @Override protected void onDeleteComplete(int token, Object cookie, int result) {
            final CalendarDeleteListener listener = dListener.get();
            if (listener != null) {
                listener.onDeleteComplete(token, cookie, result);
            }
        }

        @Override protected void onInsertComplete(int token, Object cookie, Uri uri) {
            final CalendarInsertListener listener = iListener.get();
            if (listener != null) {
                listener.onInsertComplete(token, cookie, uri);
            }
        }

    }

    public static final Parcelable.Creator<AppointmentCalendar> CREATOR = new Parcelable.Creator<AppointmentCalendar>() {
        public AppointmentCalendar createFromParcel(Parcel in) {
            return new AppointmentCalendar(in);
        }

        public AppointmentCalendar[] newArray(int size) {
            return new AppointmentCalendar[size];
        }
    };

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }

    public ArrayList<HashMap<String, Object>> parseCursor(Cursor cursor) {
        ArrayList<HashMap<String, Object>> output = new ArrayList<>();
        int rows = cursor.getCount();
        if (cursor != null && rows > 0) {

            int columns = cursor.getColumnCount();
            cursor.moveToFirst();
            do {
                HashMap<String, Object> map = new HashMap<>();
                output.add(map);

                for (int i = 0; i < columns; i++) {
                    String name = cursor.getColumnName(i);
                    Object value = null;
                    int type = cursor.getType(i);
                    switch (type) {
                        case (Cursor.FIELD_TYPE_BLOB):
                            byte[] bytestream = cursor.getBlob(i);

                            try {
                                value = deserialize(bytestream);
                                Log.d("Cursor:" + name + "(blob)", value.toString());
                            } catch (IOException ex) {
                                Log.d("IOException", ex.getMessage());
                            } catch (ClassNotFoundException ex) {
                                Log.d("ClassNotFound", ex.getMessage());
                            }

                            break;
                        case (Cursor.FIELD_TYPE_INTEGER):
                            value = cursor.getInt(i);
                            Log.d("Cursor:" + name + "(int)", Integer.toString((Integer) value));
                            break;
                        case (Cursor.FIELD_TYPE_FLOAT):
                            value = cursor.getFloat(i);
                            Log.d("Cursor:" + name + "(float)", Float.toString((Float) value));
                            break;
                        case (Cursor.FIELD_TYPE_STRING):
                            value = cursor.getString(i);
                            Log.d("Cursor:" + name + "(String)", (String) value);
                            break;
                        default:
                            value = null;
                            Log.d("Cursor:" + name + "(null)", "Nullstring");
                    }
                    map.put(name, value);
                }
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        } else {
            Log.d("Cursor", "No items in Cursor!");
            return null;
        }
        return output;
    }

}

        /* EXAMPLE QUERY FOR CalendarProvider.Calendars
        String[] selection_array {
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.ACCOUNT_TYPE,
            CalendarContract.Calendars.OWNER_ACCOUNT};

        String[] selectionArgs {
            "kellan@taglinegroup.com",              //ACCOUNT_NAME
            "com.google",                           //ACCOUNT_TYPE
            "kellan@taglinegroup.com"};             //OWNER_ACCOUNT

        */