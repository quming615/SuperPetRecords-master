package kbc.superpetrecords.models;

import android.os.Parcel;
import android.os.Parcelable;

//import java.lang.reflect.*;
import java.util.TreeMap;

/**
 * Created by kellanbc on 7/8/14.
 */

public abstract class Model implements Parcelable {

    public static final int GET_IMAGE_FILE = 1;
    public static final int GET_CURRENT_DATE = 2;
    public static final int GET_VETS = 3;
    public static final int GET_PETS = 4;
    public static final int GET_APPOINTMENTS = 5;
    public static final int GET_CONTACTS = 6;
    public static final int GET_PROCEDURES = 7;
    public static final int GET_EVENTS = 8;
    public static final int GET_LOCATIONS = 9;
    public static final int GET_PHONE_NUMBERS = 10;
    public static final int GET_EMAIL_ADDRESSES = 11;

    int id;

    public Model() {

    }

    public Model(int id) {
        this.id = id;
    }

    public Model(Parcel in) {
        id = in.readInt();
    }

    public int getId() {
        return id;
    }

    public void setId(int id){this.id = id;}

    @Override
    public String toString() {
        return Model.class.toString() + "{" + "id=" + id + "}";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public TreeMap<String, Object> getDefaults() {
        return new TreeMap<>();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
    }
}
