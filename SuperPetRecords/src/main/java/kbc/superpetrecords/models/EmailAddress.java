package kbc.superpetrecords.models;

import android.os.*;

import java.util.List;
import java.util.TreeMap;

/**
 * Created by kellanbc on 8/11/14.
 */
public class EmailAddress extends ContactType {
    String email;

    public EmailAddress(Parcel in) {
        super(in);
        email = in.readString();
    }
    public EmailAddress() {
    }

    public EmailAddress(int id, int type, String email) {
        super(id, type);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public TreeMap<String, Object> getDefaults() {
        TreeMap<String, Object> map = super.getDefaults();
        map.put("Email", "");
        return map;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(email);
    }

    public static final Parcelable.Creator<EmailAddress> CREATOR = new Parcelable.Creator<EmailAddress>() {
        public EmailAddress createFromParcel(Parcel in) {
            return new EmailAddress(in);
        }

        public EmailAddress[] newArray(int size) {
            return new EmailAddress[size];
        }
    };

    @Override
    public String toString(){
        return "email"+email;
    }

}
