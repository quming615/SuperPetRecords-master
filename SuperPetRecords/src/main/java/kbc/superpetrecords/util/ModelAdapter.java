package kbc.superpetrecords.util;

import android.view.*;
import android.widget.*;
import java.util.*;
import kbc.superpetrecords.models.*;
import kbc.superpetrecords.views.widgets.ExtendedTextView;

import android.content.*;
import android.util.*;

/**
 * Created by kellanbc on 7/18/14.
 */
public class ModelAdapter<T extends Model> extends BaseAdapter implements Filterable {

    List<T> list;
    TreeMap<T, Integer> positions;
    Context context;
    int layout_id, text_id;

    public ModelAdapter (Context context, List<T> list, int layout_id, int text_id) {
        Comparator<T> comparator = new Comparator<T>() {
            @Override
            public int compare(T lhs, T rhs) {
                if (lhs.getId() > rhs.getId()) {
                    return 1;
                } else if (lhs.getId() < rhs.getId()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        };

        positions = new TreeMap<>(comparator);
        this.context = context;
        this.list = list;
        this.text_id = text_id;
        this.layout_id = layout_id;
        Log.d("in ModelAdapter", "Count: " + getCount());
    }

    public void add(T model) {
        positions.put(model, list.size());
        list.add(model);
        notifyDataSetChanged();
    }

    public int getPosition(T model) {
        return positions.get(model);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public T getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("ModelAdapter:getView", "Position: " + position);
        T model = getItem(position);

        if (convertView == null) {
            positions.put(model, position);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(layout_id, parent, false);
            Log.d("ModelAdapter:getView", "convertView = null(" + view.toString() + ")");

            TextView text = (ExtendedTextView) view.findViewById(text_id);
            text.setText(model.toString());
            convertView = view;
        }

        Log.d("ModelAdapter:getView", "Returning View(" + convertView.toString() + ")");
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new ModelFilter();
    }

    class ModelFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Log.d("ModelFilter:performFiltering", "Here");
            FilterResults filterResults = new FilterResults();
            if(constraint != null)
            {
                filterResults.values = list;
                filterResults.count = getCount();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            Log.d("ModelFilter:publishResults", "Here");
        }
    }


}
