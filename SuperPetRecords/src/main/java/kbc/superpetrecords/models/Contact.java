package kbc.superpetrecords.models;

import android.os.*;
import java.util.*;
import java.lang.reflect.*;
/**
 * Created by kellanbc on 7/8/14.
 */
public class Contact extends Model {

    int id;
    String name;
    List<PhoneNumber> phoneNumbers = new ArrayList<>();
    List<EmailAddress> emailAddresses = new ArrayList<>();
    List<Location> locations = new ArrayList<>();

    public Contact(Parcel in) {
        super(in);
        name = in.readString();
        in.readTypedList(phoneNumbers, PhoneNumber.CREATOR);
        in.readTypedList(emailAddresses, EmailAddress.CREATOR);
        in.readTypedList(locations, Location.CREATOR);
    }

    public Contact(int id, String name, PhoneNumber phone, EmailAddress email, Location location) {
        super(id);
        this.id = id;
        this.name = name;
        phoneNumbers.add(phone);
        emailAddresses.add(email);
        locations.add(location);
    }

    public Contact(int id, String name, EmailAddress email) {
        super(id);
        this.id = id;
        this.name = name;
        emailAddresses.add(email);
    }

    public Contact(int id, String name, PhoneNumber phone) {
        super(id);
        this.id = id;
        this.name = name;
        phoneNumbers.add(phone);
    }

    public Contact(int id, String name, Location location) {
        super(id);
        this.id = id;
        this.name = name;
        locations.add(location);
    }

    public Contact(int id, String name, List<PhoneNumber> phones, List<EmailAddress> emails, List<Location> locations) {
        super(id);
        this.id = id;
        this.name = name;
        phoneNumbers = phones;
        emailAddresses = emails;
        this.locations = locations;
    }

    public void addLocation(Location location) {
        locations.add(location);
    }

    public void addEmailAddress(EmailAddress email) {
        emailAddresses.add(email);
    }

    public void addPhoneNumber(PhoneNumber number) {
        phoneNumbers.add(number);
    }

    @Override public String toString() {
        return name;
    }

    public TreeMap<String, Object> getDefaults() {
        TreeMap<String, Object> map = super.getDefaults();
        map.put("Name", "");
        map.put("Phone Number", GET_PHONE_NUMBERS);
        map.put("Email Address", GET_EMAIL_ADDRESSES);
        map.put("Locations", GET_LOCATIONS);
        return map;
    }

    public Location getLocation() {
        return getListItem(locations, ContactType.PRIMARY);
    }

    public EmailAddress getEmailAddress() {
        return getListItem(emailAddresses, ContactType.PRIMARY);
    }

    public PhoneNumber getPhoneNumber() {
        return getListItem(phoneNumbers, ContactType.PRIMARY);
    }

    public <T extends ContactType> T getListItem(List<T> list, int type) {
        if(list==null||list.equals("")){
            return null;
        }
        for (int i = 0; i < list.size(); i++) {
            T model = list.get(i);
            if (model.getType() == type) {
                return model;
            }
        }
        return null;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(name);
        dest.writeTypedList(phoneNumbers);
        dest.writeTypedList(emailAddresses);
    }

    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

}

