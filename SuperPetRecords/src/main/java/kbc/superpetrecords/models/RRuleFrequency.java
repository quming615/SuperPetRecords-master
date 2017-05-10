package kbc.superpetrecords.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kellanbc on 8/9/14.
 */
public class RRuleFrequency implements Parcelable {
    private int id;
    private String text;

    public RRuleFrequency(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public RRuleFrequency(Parcel in) {
        id = in.readInt();
        text = in.readString();
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text.toString();
    }

    @Override public String toString() {
        super.toString();
        return getText();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(text);
    }

    public static final Parcelable.Creator<RRuleFrequency> CREATOR = new Parcelable.Creator<RRuleFrequency>() {
        public RRuleFrequency createFromParcel(Parcel in) {
            return new RRuleFrequency(in);
        }

        public RRuleFrequency[] newArray(int size) {
            return new RRuleFrequency[size];
        }
    };

}
