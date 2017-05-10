package kbc.superpetrecords.dialogfragments;

import android.app.*;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.*;
import java.util.*;
import kbc.superpetrecords.*;
import kbc.superpetrecords.views.widgets.ExtendedAutoCompleteTextView;
import kbc.superpetrecords.views.widgets.ExtendedButton;
import kbc.superpetrecords.views.widgets.ExtendedTextView;

/**
 * Created by kellanbc on 8/11/14.
 */
public class NewModelDialog extends DialogFragment {

    int layout_id = R.layout.new_model_dialog;
    int message_id = R.id.new_model_message;
    int content_id = R.id.new_model_content;
    int add_button_id = R.id.add_model_button;
    int cancel_button_id = R.id.cancel_model_button;

    int questions = R.layout.question_answer;
    int question = R.id.theQuestion;
    int answer = R.id.theAnswer;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, 0);
    }

    public static NewModelDialog newInstance(TreeMap<String, Object> defaults, String message) {
        Bundle b = new Bundle();
        ArrayList<String> keys = new ArrayList<>();
        Iterator entries = defaults.entrySet().iterator();

        while (entries.hasNext()) {
            Map.Entry<String, Class> pair = (Map.Entry) entries.next();
            String key = pair.getKey();
            Object value = pair.getValue();

            if (value.getClass() == String.class) {
                b.putString(key, (String) value);
            } else if (value.getClass() == Integer.class) {
                b.putInt(key, (Integer) value);
            } else if (value.getClass() == ArrayList.class) {
                b.putStringArrayList(key, (ArrayList<String>) value);
            }

            keys.add(key);
        }

        b.putStringArrayList("keys", keys);
        b.putString("message", message);

        NewModelDialog d = new NewModelDialog();
        d.setArguments(b);
        return d;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle bundle) {
        super.onCreateView(inflater, parent, bundle);

        Bundle args = this.getArguments();
        String message = args.getString("message");
        final ArrayList<String> keys = args.getStringArrayList("keys");

        Log.d("NewModel", keys.toString());

        View view = inflater.inflate(layout_id, parent, false);
        final QuestionAdapter adapter = new QuestionAdapter(args, keys, questions, question, answer);
        final ListView list = (ListView) view.findViewById(content_id);
        list.setAdapter(adapter);

        ExtendedButton add = (ExtendedButton) view.findViewById(add_button_id);
        ExtendedButton cancel = (ExtendedButton) view.findViewById(cancel_button_id);
        TextView text = (TextView) view.findViewById(message_id);
        ViewGroup content = (ViewGroup) view.findViewById(content_id);

        text.setText(args.getString("message"));

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).removeDialogFragment(getTag());
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle b = new Bundle();
                for (int i = 0; i < adapter.getCount(); i++) {
                    b.putString(adapter.getField(i), adapter.getText(i));
                }
                intent.putExtras(b);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                ((MainActivity) getActivity()).removeDialogFragment(getTag());
            }
        });
        return view;
    }

    class QuestionAdapter extends BaseAdapter {

        int questions_id, question_id, answer_id;
        ArrayList<String> fields = new ArrayList<>();
        ArrayList<String> keys;
        ArrayList<ExtendedAutoCompleteTextView> views = new ArrayList<>();

        public QuestionAdapter(Bundle args, ArrayList<String> keys, int questions_id, int question_id, int answer_id) {
            super();
            this.keys = keys;
            this.questions_id = questions_id;
            this.question_id = question_id;
            this.answer_id = answer_id;
            Iterator<String> iterator = keys.iterator();

            while (iterator.hasNext()) {
                String field = args.getString(iterator.next());
                fields.add(field);
            }
        }

        public String getText(int position) {
            ExtendedTextView field = views.get(position);
            return field.getText().toString();
        }

        @Override
        public int getCount() {
            return keys.size();
        }

        @Override
        public Object getItem(int position) {
            return keys.get(position);
        }

        public String getField(int position) {
            return fields.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                convertView = inflater.inflate(questions_id, parent, false);
                ExtendedTextView label = (ExtendedTextView) convertView.findViewById(question_id);

                ExtendedAutoCompleteTextView field = (ExtendedAutoCompleteTextView) convertView.findViewById(answer_id);

                String labelText = (String) getItem(position);
                label.setText(labelText);

                /*
                Object defaultObject = getDefault(position);
                if (defaultObject.getClass() == String.class) {
                    field.setText((String) defaultObject);
                } else if (defaultObject.getClass() == ArrayList.class) {
                    ArrayList<String> list = (ArrayList) defaultObject;
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), answer_id, answer_id, list);
                    field.setAdapter(adapter);
                } else if (defaultObject.getClass() == Integer.class) {
                    field.setText(defaultObject.toString());
                    //TODO: Find a Way to Load Media Resources from here!
                }
                */

                views.add(field);
            }
            return convertView;
        }
    }

}
