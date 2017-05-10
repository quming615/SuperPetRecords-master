package kbc.superpetrecords.dialogfragments;
import android.content.*;
import android.app.*;
import android.database.Cursor;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.net.*;

import kbc.superpetrecords.R;
import kbc.superpetrecords.views.widgets.ExtendedButton;

/**
 * Created by kellanbc on 7/5/14.
 */
public class SpinnerDialog extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    OptionsAdapter adapter;

    Spinner spinner;
    String tag;

    OptionListener optionListener;

    private long selected_id;

    public interface OptionListener {
        public void addNewOption(View view);
        public void selectOption(View view, String tag, long id);
    }

    public static SpinnerDialog newInstance(String tag, int layout_id, int spinner_id, int header_id, int spinner_layout_id, int[] item_layouts, String message, Uri loader_uri, String[] projections) {
        Log.d("OptionDialog::newInstance", "layout_id(" + layout_id + "), spinner_layout_id(" + spinner_id + "), Uri(" + loader_uri.toString() + "), projections(" + projections.toString() + ")");
        Bundle b = new Bundle();
        SpinnerDialog d = new SpinnerDialog();
        b.putInt("layout_id", layout_id);
        b.putInt("spinner_id", spinner_id);
        b.putInt("header_id", header_id);
        b.putString("tag", tag);
        b.putInt("spinner_layout_id", spinner_layout_id);
        b.putIntArray("item_layouts", item_layouts);

        b.putString("message", message);
        b.putStringArray("projections", projections);
        b.putParcelable("loader_uri", loader_uri);
        d.setArguments(b);
        return d;
    }


    @Override
    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(activity, attrs, savedInstanceState);
        Log.d("OptionDialog::onInflate", attrs.toString());
    }

    @Override public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.d("OptionDialog::onCreate", "Creating Option Dialog Instance");
        tag = getArguments().getString("tag");
        this.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        Log.d("OptionDialog::onActivityCreated", args.toString());

        int[] item_layout_ids = args.getIntArray("item_layouts");

        adapter = new OptionsAdapter(
            getActivity().getApplicationContext(),
            args.getInt("spinner_layout_id"),
            null,
            args.getStringArray("projections"),
            args.getIntArray("item_layouts")
            );

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            optionListener = (OptionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement AddNewOptionListener & SelectOptionListener");
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle bundle) {
        super.onCreateView(inflater, parent, bundle);
        Bundle args = getArguments();
        Log.d("OptionDialog::onCreateView", args.toString());

        View view = inflater.inflate(args.getInt("layout_id"), parent, false);
        spinner = (Spinner) view.findViewById(args.getInt("spinner_id"));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                //String selected_val= spinner.getSelectedItem().toString();
                selected_id = id;
                Toast.makeText(getActivity(), "Selected ID(" + selected_id + ")", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        TextView text = (TextView) view.findViewById(args.getInt("header_id"));
        text.setText(args.getString("message"));

        ExtendedButton select_button = (ExtendedButton) view.findViewById(R.id.option_select);
        select_button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                optionListener.selectOption(v, tag, selected_id);
            }
        });

        ExtendedButton add_button = (ExtendedButton) view.findViewById(R.id.option_addnew);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                addNewOption(v);
            }
        });

        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        Bundle args = getArguments();
        Log.d("OptionDialog::onCreateLoader", args.toString());

        CursorLoader cursorLoader = new CursorLoader(
            getActivity(),
            (Uri) args.getParcelable("loader_uri"),
            args.getStringArray("projections"),
            null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("OptionDialog::onCreateLoader", data.toString());
        adapter.swapCursor(data);
        spinner.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    public void setOption(View view) {
        //SQLiteDatabase db = ((MainActivity) getActivity()).getDatabase();
        Toast.makeText(getActivity(), "setOption:Selected ID(" + selected_id + ")", Toast.LENGTH_SHORT).show();
        optionListener.selectOption(view, tag, selected_id);
    }

    public void addNewOption(View view) {
        Toast.makeText(getActivity(), "addNewOption:Selected ID(" + selected_id + ")", Toast.LENGTH_SHORT).show();
        optionListener.addNewOption(view);
    }

    public class OptionsAdapter extends CursorAdapter {

        private Cursor cursor;
        int spinnerLayoutId;
        Context context;
        String[] projections;
        int[] itemLayouts;

        public OptionsAdapter(Context context, int spinnerLayoutId, Cursor cursor, String[] projections, int[] itemLayouts) {
            super(context, cursor, 0);
            this.context = context;
            this.spinnerLayoutId = spinnerLayoutId;
            this.cursor = cursor;
            this.projections = projections;
            this.itemLayouts = itemLayouts;
        }

        long getId(int position) {
            cursor.moveToPosition(position);
            return cursor.getLong(cursor.getColumnIndex("_id"));
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            return inflater.inflate(spinnerLayoutId, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            if (cursor != null) {
                if (cursor.getPosition() == 0) {
                    selected_id = cursor.getLong(cursor.getColumnIndex("_id"));
                }

                for (int i = 0; i < projections.length - 1; i++) {
                    TextView text = (TextView) view.findViewById(itemLayouts[i]);
                    String textString = cursor.getString(cursor.getColumnIndex(projections[i + 1]));
                    text.setText(textString);
                    Log.d("CursorAdapter:bindView", textString);
                }
            }
        }
    }

}
