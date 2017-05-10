package kbc.superpetrecords.models;

import android.net.Uri;
import android.os.*;

import java.util.Calendar;
import java.util.TreeMap;

/**
 * Created by kellanbc on 7/8/14.
 */
public class Reminder extends Model implements Parcelable {

    String title, description;
    EventDate date;
    Pet pet;
    int id, priority;

    public Reminder(Parcel in) {
        super(in);
        title = in.readString();
        description = in.readString();
        date = in.readParcelable(EventDate.class.getClassLoader());
        pet = in.readParcelable(Pet.class.getClassLoader());
        priority = in.readInt();
    }

    public Reminder(int id, String title, String description, EventDate date, Pet pet, int priority) {
        super(id);
        this.title = title;
        this.description = description;
        this.date = date;
        this.pet = pet;
        this.priority = priority;
    }

    @Override
    public TreeMap<String, Object> getDefaults() {
        TreeMap<String, Object> map = super.getDefaults();
        map.put("Title", "");
        map.put("Description", "");
        map.put("Date", "");
        map.put("Pet", "");
        map.put("Priority", 0);
        return map;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeParcelable(date, flags);
        dest.writeParcelable(pet, flags);
        dest.writeInt(priority);
    }

    @Override public String toString() {
        return date.getFormattedDate();
    }

    public static final Parcelable.Creator<Reminder> CREATOR = new Parcelable.Creator<Reminder>() {
        public Reminder createFromParcel(Parcel in) {
            return new Reminder(in);
        }

        public Reminder[] newArray(int size) {
            return new Reminder[size];
        }
    };
}

