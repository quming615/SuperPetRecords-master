package kbc.superpetrecords.fragments;

import android.net.*;
import android.app.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import kbc.superpetrecords.*;
import kbc.superpetrecords.dialogfragments.*;
import kbc.superpetrecords.models.*;

/**
 * Created by kellanbc on 8/15/14.
 */
public class ModelDetailsFragment<T extends Model> extends Fragment {

    T model;
    int layout_id;

    public ModelDetailsFragment() {}

    public static <T extends Model> ModelDetailsFragment newInstance(T model, int layout_id, Bundle bundle) {
        ModelDetailsFragment<T> frag =  new ModelDetailsFragment();
        bundle.putParcelable("model", model);
        bundle.putInt("layout_id", layout_id);
        frag.setArguments(bundle);
        return frag;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle args = getArguments();
        model = args.getParcelable("model");
        layout_id = args.getInt("layout_id");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup view_g, Bundle state) {
        final View layout = inflater.inflate(R.layout.pet_details, view_g, false);

        return layout;
    }

    protected void writeToFields() {}

    protected void save() {}

    protected void edit() {

    }

    protected void add() {

    }
}
