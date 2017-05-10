package kbc.superpetrecords.models;


import android.location.Address;
import android.os.*;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by kellanbc on 7/8/14.
 */
public class Location extends ContactType {
    String address, unit, city, state, zip;


    public Location(Parcel in) {
        super(in);
        address = in.readString();
        unit = in.readString();
        city = in.readString();
        state = in.readString();
        zip = in.readString();
    }

    public Location(int id, String address, String unit, String city, String state, String zip) {
        super(id, PRIMARY);
        this.address = address;
        this.unit = unit;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }

    public Location(int id, String address, String unit, String city, String state, String zip, int type) {
        super(id, type);
        this.address = address;
        this.unit = unit;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }

    public Location(String address) {
        super();
        this.address = address;
        this.unit = "";
        this.city = "";
        this.state = "";
        this.zip = "";
        this.type = PRIMARY;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    @Override
    public String toString() {
        /*+"unit"+unit+"city"+ city+"state"+state+"zip"+ zip*/
        return "address"+address;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(address);
        dest.writeString(unit);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(zip);
    }

    @Override
    public TreeMap<String, Object> getDefaults() {
        TreeMap<String, Object> map = super.getDefaults();
        map.put("Address", "address");
        map.put("Unit", "unit");
        map.put("City", "city");
        map.put("State", "state");
        map.put("Zipcode", "zip");
        return map;
    }

    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        public Location[] newArray(int size) {
            return new Location[size];
        }
    };
}
