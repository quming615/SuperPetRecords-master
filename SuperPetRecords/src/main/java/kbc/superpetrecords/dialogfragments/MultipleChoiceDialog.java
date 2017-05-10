package kbc.superpetrecords.dialogfragments;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.util.*;

import kbc.superpetrecords.MainActivity;
import kbc.superpetrecords.R;

/**
 * Created by kellanbc on 7/12/14.
 */
public class MultipleChoiceDialog extends DialogFragment {

    Parcelable data;

    public static MultipleChoiceDialog newInstance(String message, Parcelable data, int dialogLayoutId, int listViewId, int listItemLayoutId, ArrayList<Bundle> map, String[] types, int[] viewIds, int[] responses) {
        Bundle bundle = new Bundle();
        bundle.putIntArray("responses", responses);
        bundle.putString("message", message);
        bundle.putInt("dialogLayoutId", dialogLayoutId);
        bundle.putInt("listViewId", listViewId);
        bundle.putInt("listItemLayoutId", listItemLayoutId);
        bundle.putParcelable("data", data);
        bundle.putParcelableArrayList("map", map);
        bundle.putStringArray("types", types);
        bundle.putIntArray("viewIds", viewIds);
        MultipleChoiceDialog fragment = new MultipleChoiceDialog();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, 0);
        Bundle args = this.getArguments();
        data = args.getParcelable("data");
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle bundle) {
        super.onCreateView(inflater, parent, bundle);

        Bundle args = this.getArguments();
        Log.d("MultipleChoice::CreateView", args.toString());
        View view = inflater.inflate(args.getInt("dialogLayoutId"), parent, false);
        ListView list = (ListView) view.findViewById(args.getInt("listViewId"));
        TextView text = (TextView) view.findViewById(R.id.multiple_choice_header_text);
        text.setText(args.getString("message"));
        String[] types = args.getStringArray("types");
        int[] viewIds = args.getIntArray("viewIds");
        int listItemLayoutId = args.getInt("listItemLayoutId");

        ArrayList<Bundle> bundle_list = args.getParcelableArrayList("map");
        ArrayList<Map<String, String>> mapList = new ArrayList<>();
        for (Bundle b : bundle_list) {
            Set<String> keys = b.keySet();
            Map<String, String> map = new HashMap<>();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = b.getString(key);
                map.put(key, value);
            }
            mapList.add(map);
        }

        int[] responses = args.getIntArray("responses");
        MultipleChoiceAdapter adapter = new MultipleChoiceAdapter(listItemLayoutId, viewIds, mapList, types, responses);
        list.setAdapter(adapter);
        return view;
    }

    public class MultipleChoiceAdapter extends BaseAdapter {
        List<Map<String, String>> mapList;
        String[] types;
        int[] viewIds;
        int layoutId;
        int[] responses;

        public MultipleChoiceAdapter(int layoutId, int[] viewIds, List<Map<String, String>> mapList, String[] types, int[] responses) {
            Log.d("MultipleChoiceAdapter::__constructor", "loading adapter");
            this.layoutId =layoutId;
            this.mapList = mapList;
            this.types = types;
            this.viewIds = viewIds;
            this.responses = responses;
        }

        @Override public int getCount() {
            return mapList.size();
        }

        @Override
        public Map<String, String> getItem(int position) {
            return mapList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return viewIds[position];
        }

        public int getResponseId(int position) {
            return responses[position];
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                convertView = inflater.inflate(layoutId, parent, false);

                Map<String, String> the_map = getItem(position);

                for (int i = 0; i < types.length; i++) {
                    String value = the_map.get(types[i]);
                    int layout = viewIds[i];
                    Button button = (Button) convertView.findViewById(layout);
                    button.setText(value);
                    button.setLongClickable(false);
                    final Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("data", data);
                    intent.putExtras(bundle);

                    button.setOnClickListener(new Button.OnClickListener() {
                        @Override public void onClick(View v) {
                            String tag = MultipleChoiceDialog.this.getTag();
                            Log.d("MultipleChoiceFragment::button::setOnClickListener", "tag(" + tag + ")");
                            int x=getResponseId(position);
                            getTargetFragment().onActivityResult(getResponseId(position), Activity.RESULT_OK, intent);
                            ((MainActivity) getActivity()).removeDialogFragment(tag);
                        }
                    });
                }

            }
            return convertView;
        }
    }
}
