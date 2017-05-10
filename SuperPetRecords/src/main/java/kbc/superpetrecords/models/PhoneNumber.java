package kbc.superpetrecords.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by kellanbc on 8/11/14.
 package kbc.superpetrecords.models;

 import android.os.*;

 import java.util.TreeMap;

 /**
 * Created by kellanbc on 8/11/14.
 */
public class PhoneNumber extends ContactType {
    int phoneNumber;

    public PhoneNumber(Parcel in) {
        super(in);
        phoneNumber = in.readInt();
    }

    public PhoneNumber(){}

    public PhoneNumber(int id, int type, int number) {
        super(id, type);
        this.phoneNumber = number;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPhone_number(int phoneNumber){this.phoneNumber = phoneNumber;}

    public void setId(int id){super.setId(id);}

    @Override
    public TreeMap<String, Object> getDefaults() {
        TreeMap<String, Object> map = super.getDefaults();
        map.put("Phone Number", "");
        return map;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(phoneNumber);
    }

    public static final Parcelable.Creator<PhoneNumber> CREATOR = new Parcelable.Creator<PhoneNumber>() {
        public PhoneNumber createFromParcel(Parcel in) {
            return new PhoneNumber(in);
        }

        public PhoneNumber[] newArray(int size) {
            return new PhoneNumber[size];
        }
    };

    @Override
    public String toString(){
        return "phoneNumber"+phoneNumber;
    }
}