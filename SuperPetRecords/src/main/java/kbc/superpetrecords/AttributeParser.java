package kbc.superpetrecords;

import android.content.*;
import android.content.res.*;
import android.util.*;
import java.util.*;
import org.xmlpull.v1.*;
import java.io.*;
/**
 * Created by kellanbc on 6/29/14.
 */
public class AttributeParser {


    private static void parseTypedArray(Context context, ArrayList<Object> list, int data_id, String depth) {
        Resources resources = context.getResources();
        TypedArray subitems = resources.obtainTypedArray(data_id);
        String item_entry_name = resources.getResourceEntryName(data_id);
        int item_count = subitems.length();
        int item_index_count = subitems.getIndexCount();

        Log.d("AttributeParser:", "entry_name(" + item_entry_name + "), length(" + item_count + ", " + item_index_count + ")");

        for (int i = 0; i < item_count; i++) {
            TypedValue out_item = new TypedValue();

            subitems.getValue(i, out_item);
            int item_type = out_item.type;
            int item_data = out_item.data;


            if (item_type == TypedValue.TYPE_STRING) {
                list.add(out_item.string.toString());
                Log.d("AttributeParser("  + depth + "." + i + ")", "Type(" + getTypeString(item_type)  + ") value(" + out_item.string  + ") assetCookie(" + out_item.assetCookie + ")");
            } else if (item_type == TypedValue.TYPE_REFERENCE) {
                ArrayList<Object> new_list = new ArrayList<>();
                list.add(new_list);
                Log.d("AttributeParser(" + depth + "." + i + ")", "Type(" + getTypeString(item_type) + ") resource_id(" + Integer.toHexString(out_item.resourceId) + ")");
                parseTypedArray(context, new_list, out_item.resourceId, depth + "." + i);
            } else {
                Log.d("AttributeParser("  + depth + "." + i  + ")", "Type(" + getTypeString(item_type) + "," +  ") data(" + item_data + ")");
            }


        }
    }

    public static void parseTypedArray(Context context,ArrayList<Object> root, int data_id) {
        parseTypedArray(context, root, data_id, "0");
    }

    public static String getTypeString(int type_id) {
        switch (type_id) {
            case (TypedValue.TYPE_REFERENCE):
                return "REFERENCE";
            case (TypedValue.TYPE_STRING):
                return "STRING";
            case (TypedValue.TYPE_NULL):
                return "NULL";
            case (TypedValue.TYPE_ATTRIBUTE):
                return "ATTRIBUTE";
            case (TypedValue.TYPE_FIRST_INT):
                return "FIRST INT";
            default:
                return Integer.toString(type_id);
        }
    }

    public void recursiveParse(ArrayList<Object> list) {
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o.getClass() == String.class) {
                Log.d("Attribute:String", o.toString());
            } else if (o.getClass() == ArrayList.class) {
                recursiveParse((ArrayList<Object>) o);
            }
        }
    }

}
