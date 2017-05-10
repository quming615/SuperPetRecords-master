package kbc.superpetrecords.models;

/**
 * Created by kellanbc on 8/22/14.
 */
import android.os.*;

import java.util.TreeMap;

public class ContactType extends Model {
    int type;

    public static final int PRIMARY = 1;
    public static final int HOME = 2;
    public static final int MOBILE = 4;
    public static final int WORK = 8;
    public static final int EMERGENCY = 16;

    public ContactType() {}

    public ContactType(Parcel in) {
        super(in);
        type = in.readInt();
    }

    public ContactType(int id, int type) {
        super(id);
        this.type = type;
    }

    @Override public TreeMap<String, Object> getDefaults() {
        TreeMap<String, Object> map = super.getDefaults();
        map.put("Type", "type");
        return map;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(type);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
