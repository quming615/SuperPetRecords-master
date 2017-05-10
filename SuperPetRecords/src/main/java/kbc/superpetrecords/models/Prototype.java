package kbc.superpetrecords.models;

import android.os.*;
/**
 * Created by kellanbc on 7/8/14.
 */
public class Prototype implements Parcelable {

    public Prototype(Parcel in) {

    }

    public Prototype() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public static final Parcelable.Creator<Prototype> CREATOR = new Parcelable.Creator<Prototype>() {
        public Prototype createFromParcel(Parcel in) {
            return new Prototype(in);
        }

        public Prototype[] newArray(int size) {
            return new Prototype[size];
        }
    };
}

