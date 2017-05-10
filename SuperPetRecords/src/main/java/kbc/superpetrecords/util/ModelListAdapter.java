package kbc.superpetrecords.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import kbc.superpetrecords.R;
import kbc.superpetrecords.models.Model;

/**
 * Created by kellanbc on 8/15/14.
 */
public class ModelListAdapter<T extends Model> extends BaseAdapter {

    public interface ModelListHelper<T> {
        public void setView(View convertView, T model);
    }

    protected ArrayList<T> models;
    protected Activity activity;
    protected ModelListHelper helper;
    protected int layout_id;

    public ModelListAdapter(Activity activity, ArrayList<T> models, int layout_id, ModelListHelper helper) {
        this.activity = activity;
        this.models = models;
        this.helper = helper;
        this.layout_id = layout_id;
    }

    /* @param  */
    @Override public T getItem(int position) {
        return models.get(position);
    }

    @Override public long getItemId(int position) {
        return models.get(position).getId();
    }

    @Override public int getCount() {
        return models.size();
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        T model = getItem(position);
        Log.d("ModelListAdapter", model.toString());
        if (convertView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(layout_id, parent, false);
            helper.setView(convertView, model);

        }
        return convertView;
    }
}