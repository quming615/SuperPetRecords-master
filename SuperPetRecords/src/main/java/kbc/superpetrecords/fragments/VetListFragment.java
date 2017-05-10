package kbc.superpetrecords.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import kbc.superpetrecords.MainActivity;
import kbc.superpetrecords.R;
import kbc.superpetrecords.models.Model;
import kbc.superpetrecords.models.Pet;
import kbc.superpetrecords.models.Vet;
import kbc.superpetrecords.util.ModelListAdapter;

/**
 * Created by kellanbc on 8/15/14.
 */
public class VetListFragment<T extends Model> extends Fragment {

    protected Activity activity;
    protected ArrayList<T> models;
    protected int layout_id;
    protected Class<T> type;

    public View onCreateView(LayoutInflater layout, ViewGroup view_g, Bundle state) {

        final ListView list = (ListView) layout.inflate(R.layout.model_list,view_g,false);
        //final ListView list = (ListView) super.onCreateView(layout, view_g, state);

        ModelListAdapter.ModelListHelper<kbc.superpetrecords.models.Vet> helper = new ModelListAdapter.ModelListHelper<kbc.superpetrecords.models.Vet>() {
            @Override
            public void setView(View convertView, kbc.superpetrecords.models.Vet vet) {
                setText(convertView, R.id.name, vet.getName());
                setText(convertView, R.id.phone, vet.getPhoneNumber().toString());
                setText(convertView, R.id.email, vet.getEmailAddress().toString());
                setText(convertView, R.id.location, vet.getLocation().toString());
            }
        };

        final ModelListAdapter adapter = new ModelListAdapter(getActivity(), models, R.layout.vet_details_item, helper);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final FragmentTransaction trans = activity.getFragmentManager().beginTransaction();
                final ModelListAdapter adapter = (ModelListAdapter) parent.getAdapter();
                Model model = adapter.getItem(position);
                ((MainActivity) getActivity()).updateVet((kbc.superpetrecords.models.Vet) model);
            }
        });
        return list;
    }


    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle pkg = this.getArguments();
        models = pkg.getParcelableArrayList("models");
    }

    public static <T extends Model> VetListFragment newInstance(ArrayList<T> models, int layout_id) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("models", models);
        bundle.putInt("layout_id", layout_id);
        VetListFragment<T> list = new VetListFragment<>();
        list.setArguments(bundle);
        list.setRetainInstance(true);
        return list;
    }

    //TODO onCreateView重写但是不完整，所以出现空白页
    /*public View onCreateView(LayoutInflater layout, ViewGroup view_g, Bundle state) {
        return layout.inflate(R.layout.model_list, view_g, false);
    }*/

    protected void setText(View view, int resource, String text) {
        ((TextView) view.findViewById(resource)).setText(text);
    }

    protected void setMedia(View view, int resource, Uri media_resource) {
        if (media_resource != Uri.EMPTY) {
            Bitmap bmp;
            try {
                bmp = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), media_resource);
                ((ImageView) view.findViewById(resource)).setImageBitmap(bmp);
            } catch (FileNotFoundException ex) {
                Log.e("FileNotFound", ex.getMessage());
            } catch (IOException ex) {
                Log.e("IO", ex.getMessage());
            }
        }
    }

    protected void setMedia(View view, int resource, int media_resource) {
        Bitmap bmp;
        InputStream istream = activity.getResources().openRawResource(media_resource);
        bmp = BitmapFactory.decodeStream(istream);
        ((ImageView) view.findViewById(resource)).setImageBitmap(bmp);
    }

}
