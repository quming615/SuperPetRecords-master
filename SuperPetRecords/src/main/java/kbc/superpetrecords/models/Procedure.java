package kbc.superpetrecords.models;

import android.net.Uri;
import android.os.*;

import java.util.TreeMap;

/**
 * Created by kellanbc on 7/8/14.
 */
public class Procedure extends Model {

    int id;
    String name, description;

    public Procedure(Parcel in) {
        super(in);
        name = in.readString();
        description = in.readString();
    }

    public Procedure(int id, String name, String description) {
        super(id);
        this.name = name;
        this.description = description;
    }

    public Procedure(String name) {
        super(0);
        this.name = name;
        this.description = "";
    }

    @Override
    public String toString() {
        return name;
    }

    public String getIdentifier() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(name);
        dest.writeString(description);
    }

    @Override
    public TreeMap<String, Object> getDefaults() {
        TreeMap<String, Object> map = super.getDefaults();
        map.put("Name", "name");
        map.put("Description", "description");
        return map;
    }

    public static final Parcelable.Creator<Procedure> CREATOR = new Parcelable.Creator<Procedure>() {
        public Procedure createFromParcel(Parcel in) {
            return new Procedure(in);
        }

        public Procedure[] newArray(int size) {
            return new Procedure[size];
        }
    };
}

