package kbc.superpetrecords.models;

import android.os.*;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by kellanbc on 7/8/14.
 */
public class Vet extends Model {

    int id;
    String name;
    Contact contact;

    public Vet(Parcel in) {
        super(in);
        name = in.readString();
        contact = in.readParcelable(Contact.class.getClassLoader());
    }

    public Vet() {
        super();
    }

    public Vet(int id, String name, Contact contact) {
        super(id);
        this.name = name;
        this.contact = contact;
    }

    public Vet(String name) {
        super(0);
        this.name = name;
        this.contact = null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EmailAddress getEmailAddress() {
        return contact.getEmailAddress();
    }

    public PhoneNumber getPhoneNumber() {
        return contact.getPhoneNumber();
    }

    public Location getLocation() {
        return contact.getLocation();
    }

    public String getName() {
        return name;
    }


    @Override public String toString() {
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
        dest.writeParcelable(contact, flags);
    }

    @Override
    public TreeMap<String, Object> getDefaults() {
        TreeMap<String, Object> map = super.getDefaults();
        map.put("Name", "name");
        map.put("Email Address", "email_address");
        map.put("Phone Number", "phone_number");
        map.put("Address","address");
        map.put("Unit","unit");
        map.put("City","city");
        map.put("State","state");
        map.put("Zip","zip");
        return map;
    }

    public static final Parcelable.Creator<Vet> CREATOR = new Parcelable.Creator<Vet>() {
        public Vet createFromParcel(Parcel in) {
            return new Vet(in);
        }

        public Vet[] newArray(int size) {
            return new Vet[size];
        }
    };
}