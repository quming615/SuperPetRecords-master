package kbc.superpetrecords;

import java.util.*;
/**
 * Created by kellanbc on 7/7/14.
 */
public class Toolbox {

    public static <T> Map<String, T> listToMap(List<T> array, String[] keys) {
        Map<String, T> map = new TreeMap<String, T>();

        for (int i = 0; i < array.size(); i++) {
                map.put(keys[i], array.get(i));
        }
        return map;
    }

    public static List<Map<String, String>> stringArrayToListOfMaps(String[] array, String[] keys) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for (int i = 0; i < array.length; i++) {
            Map<String, String> map = new TreeMap<String, String>();
            for (int j=0; j < keys.length; j++) {
                map.put(keys[j], array[i]);
            }
            list.add(map);
        }

        return list;
    }


}
