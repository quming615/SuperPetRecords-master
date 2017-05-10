package kbc.superpetrecords.helpers;

/**
 * Created by kellanbc on 7/12/14.
 */
import android.content.res.TypedArray;
import android.util.Log;
import android.util.TypedValue;

import java.util.*;

import kbc.superpetrecords.exceptions.BadRelationException;

import android.content.res.*;
import android.os.*;

public class AdapterHelper {

    public static ArrayList<Bundle> parseStringResource(Resources resources, String[] types, int resourceArray) {
        TypedArray list = resources.obtainTypedArray(resourceArray);
        ArrayList<Bundle> mapList = new ArrayList<>();

        for (int i = 0; i < list.length(); i++) {
            TypedValue value = new TypedValue();
            list.getValue(i, value);
            String[] listItems = resources.getStringArray(value.resourceId);
            try {
                if (listItems.length != types.length) {
                    throw new BadRelationException("Columns do not match list items!");
                }
            } catch(BadRelationException ex) {
                Log.e("BadRelationException", ex.getMessage());
            }
            Bundle map = new Bundle();
            for (int j = 0; j < types.length; j++) {
                map.putString(types[j], listItems[j]);
            }
            mapList.add(map);
        }
        return mapList;
    }

}