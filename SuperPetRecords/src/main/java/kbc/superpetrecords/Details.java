package kbc.superpetrecords;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by kellanbc on 7/2/14.
 */
abstract public class Details extends Fragment {

    public static final int SELECT_PICTURE = 1;
    public static final int SELECT_DATE = 2;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    public boolean setButtonClickListener(View view, final Method method) throws NoSuchMethodException {
        view.setOnClickListener(new Button.OnClickListener() {
            @Override public void onClick(View view) {
                try {
                    method.invoke(Details.this, (Object[]) null);
                } catch(IllegalAccessException ex) {
                    Log.e("IllegalAccessException", ex.getMessage());
                } catch(InvocationTargetException ex) {
                    Log.e("InvocationTargetException", ex.getMessage());
                }
            }
        });
        return true;
    }

    public void requestImageResource() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        String title = getActivity().getResources().getString(R.string.chooser_title);
        Intent chooser = Intent.createChooser(intent, title);
        Log.d("requestImageResource", "Requesting Image Resource");
        // Verify the intent will resolve to at least one activity
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            //Toast.makeText((Context) getActivity(), "Starting activitiy", Toast.LENGTH_SHORT);
            startActivityForResult(chooser, SELECT_PICTURE);

        } else {
            //Toast.makeText((Context) getActivity(), "Failed to find intent-filters", Toast.LENGTH_SHORT);
        }
    }

    public void requestDate() {
        Intent intent = new Intent();
    }

    public Bitmap getImageBitmap(Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
        } catch (FileNotFoundException ex) {
            Log.d("FileNotFoundException", ex.getMessage());
        } catch (IOException ex) {
            Log.d("IOException", ex.getMessage());
        }
        return null;
    }

    public String getAbsolutePath(Uri uri) {
        if( uri == null ) {
            return null;
        }

        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };

        Cursor cursor = MediaStore.Images.Media.query(getActivity().getContentResolver(), uri, projection);
        if( cursor != null ){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.details_action_menu, menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            /*case(R.id.edit_action_menu_item):
                Toast.makeText(getActivity(), "Edit action.", Toast.LENGTH_SHORT).show();
                edit();
                return true;*/
            case(R.id.new_action_menu_item):
                Toast.makeText(getActivity(), "New Pet action.", Toast.LENGTH_SHORT).show();
                add();
                return true;
            case(R.id.save_action_menu_item):
            { Toast.makeText(getActivity(), "Save action.", Toast.LENGTH_SHORT).show();
                save();
                return true;}
        }
        return true;
        //return super.onOptionsItemSelected(item);
    }

    protected abstract void save();
    protected abstract void edit();
    protected abstract void add();

    public Bitmap getBitmapFromResource(int resource_id) {
        InputStream istream = getActivity().getResources().openRawResource(resource_id);
        return convertBitmapToGrayscale(BitmapFactory.decodeStream(istream));
    }

    public Bitmap convertBitmapToGrayscale(int resource_id) {
        return convertBitmapToGrayscale(getBitmapFromResource(resource_id));
    }

    public Bitmap convertBitmapToGrayscale(Bitmap bmpOriginal) {
        int height = bmpOriginal.getHeight();
        int width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();

        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);

        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }
}
