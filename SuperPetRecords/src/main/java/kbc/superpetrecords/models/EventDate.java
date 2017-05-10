package kbc.superpetrecords.models;

import android.os.*;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.TreeMap;

/**
 * Created by kellanbc on 6/25/14.
 */

public class EventDate extends Model {

    private int id;
    private int day;
    private int year;
    private int month;

    String rrule;

    private boolean allDay;
    private int dtstart;
    private int dtend;

    private EventDate(Parcel in) {
        super(in);
        day = in.readInt();
        month = in.readInt();
        year = in.readInt();
        dtstart = in.readInt();
        dtend = in.readInt();
        allDay = (in.readInt() == 1) ? true : false;
        rrule = in.readString();
    }

    public EventDate() {
        super();
    }
    public EventDate(int id) {
        super(id);
    }

    public EventDate(int id, int day, int month, int year, String rrule) {
        super(id);
        this.day = day;
        this.month = month;
        this.year = year;
        this.dtstart = 0;
        this.dtend = 0;
        this.allDay = true;
        this.rrule = rrule;
    }

    public EventDate(int id, int day, int month, int year, int dtstart, int dtend, String rrule) {
        super(id);
        this.day = day;
        this.month = month;
        this.year = year;
        this.dtstart = dtstart;
        this.dtend = dtend;
        this.allDay = dtstart == 0 && dtend == 240000;
        this.rrule = rrule;
    }

    @Override
    public TreeMap<String, Object> getDefaults() {
        TreeMap<String, Object> map = super.getDefaults();
        map.put("Day", "");
        map.put("Month", "");
        map.put("Year", "");
        map.put("Start Time", "");
        map.put("End Time", "");
        return map;
    }

    public int getDate() { return day; }
    public int getMonth() { return month; }
    public int getYear() { return year; }
    public int getStartTime() { return dtstart; }
    public int getEndTime() { return dtend; }

    public boolean isAllDayEvent() {return allDay; }

    public String getFormattedDate() {
        //GregorianCalendar date = new GregorianCalendar(year, month, day);
        return month + "/" + day + "/" + year;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(day);
        out.writeInt(month);
        out.writeInt(year);
        out.writeInt(dtstart);
        out.writeInt(dtend);
        out.writeInt((allDay) ? 1 : 0);
        out.writeString(rrule);
    }

    @Override
    public String toString() {
        return getFormattedDate();
    }

    public static final Parcelable.Creator<EventDate> CREATOR = new Parcelable.Creator<EventDate>() {
        public EventDate createFromParcel(Parcel in) {
            return new EventDate(in);
        }

        public EventDate[] newArray(int size) {
            return new EventDate[size];
        }
    };
}
