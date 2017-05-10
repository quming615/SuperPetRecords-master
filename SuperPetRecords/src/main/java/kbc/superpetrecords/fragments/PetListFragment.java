package kbc.superpetrecords.fragments;

import android.app.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.*;
import android.provider.MediaStore;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import kbc.superpetrecords.MainActivity;
import kbc.superpetrecords.R;
import kbc.superpetrecords.models.Model;
import kbc.superpetrecords.models.Pet;
import kbc.superpetrecords.util.ModelListAdapter;

/**
 * Created by kellanbc on 6/25/14.
 */
public class PetListFragment<T extends Model> extends Fragment {

    protected Activity activity;
    protected ArrayList<T> models;
    protected int layout_id;
    protected Class<T> type;


    public View onCreateView(LayoutInflater layout, ViewGroup view_g, Bundle state) {
        final ListView list=(ListView) layout.inflate(R.layout.model_list, view_g, false);
        //final ListView list = (ListView) super.onCreateView(layout, view_g, state);
        //Log.d("ModelListFragment:","11233312333"+list.toString());
        ModelListAdapter.ModelListHelper<Pet> helper = new ModelListAdapter.ModelListHelper<Pet>() {
            @Override
            public void setView(View convertView, Pet pet) {
                setText(convertView, R.id.petListName, pet.getName());
                setText(convertView, R.id.petListBreed, pet.getBreed());
                setText(convertView, R.id.petListSpecies, pet.getSpecies());
                setText(convertView, R.id.petListBirthdate, pet.getFormattedBirthdate());
                setMedia(convertView, R.id.petListImage, pet.getImageResource());
            }
        };

        final ModelListAdapter adapter = new ModelListAdapter(getActivity(), models, R.layout.pet_list_item, helper);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            /*  @param AdapterView<?> refers to ListView
            *   @param View view refers to clicked element
            *   @param refers to position in ListView
            *   @param id, refers to item id
            */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final FragmentTransaction trans = activity.getFragmentManager().beginTransaction();
                final ModelListAdapter adapter = (ModelListAdapter) parent.getAdapter();
                Model model = adapter.getItem(position);
                ((MainActivity) getActivity()).updatePet((Pet) model);
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

    public static <T extends Model> PetListFragment newInstance(ArrayList<T> models, int layout_id) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("models", models);
        bundle.putInt("layout_id", layout_id);
        PetListFragment<T> list = new PetListFragment<>();
        list.setArguments(bundle);
        list.setRetainInstance(true);
        return list;
    }

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