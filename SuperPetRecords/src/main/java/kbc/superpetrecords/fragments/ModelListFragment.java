package kbc.superpetrecords.fragments;

import android.app.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import java.io.*;
import java.util.*;

import kbc.superpetrecords.*;
import kbc.superpetrecords.models.*;
import kbc.superpetrecords.util.*;

/**
 * Created by kellanbc on 8/15/14.
 */
public class ModelListFragment<T extends Model> extends Fragment {
    protected Activity activity;
    protected ArrayList<T> models;
    protected int layout_id;
    protected Class<T> type;

    public ModelListFragment() {
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

    public static <T extends Model> ModelListFragment newInstance(ArrayList<T> models, int layout_id) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("models", models);
        bundle.putInt("layout_id", layout_id);
        ModelListFragment<T> list = new ModelListFragment<>();
        list.setArguments(bundle);
        list.setRetainInstance(true);
        return list;
    }

    //TODO onCreateView重写但是不完整，所以出现空白页
    public  View onCreateView(LayoutInflater layout, ViewGroup view_g, Bundle state) {
        return layout.inflate(R.layout.model_list, view_g, false);
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
