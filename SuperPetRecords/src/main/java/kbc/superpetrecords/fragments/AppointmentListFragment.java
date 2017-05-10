package kbc.superpetrecords.fragments;

/**
 * Created by kellanbc on 7/8/14.
 */

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import kbc.superpetrecords.R;
import kbc.superpetrecords.models.*;
import kbc.superpetrecords.views.widgets.ExtendedTextView;

/**
 * Created by kellanbc on 6/25/14.
 */
public class AppointmentListFragment extends Fragment {

    ArrayList<Appointment> appointments;

    public AppointmentListFragment() {}

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("AppointmentListFragment", "Attached");
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.d("AppointmentListFragment", "onCreate");
        Bundle pkg = this.getArguments();
        appointments = pkg.getParcelableArrayList("appointments");
        setRetainInstance(true);
    }

    public static AppointmentListFragment newInstance(ArrayList<Appointment> appointments) {
        Bundle b = new Bundle();
        b.putParcelableArrayList("appointments", appointments);
        AppointmentListFragment list = new AppointmentListFragment();
        list.setRetainInstance(true);
        list.setArguments(b);
        return list;
    }

    public View onCreateView(LayoutInflater layout, ViewGroup view_g, Bundle state) {
        Log.d("AppointmentListFragment", "onCreateView");

        LinearLayout ll = (LinearLayout) layout.inflate(R.layout.default_list, view_g, false);

        final ListView list = (ListView) ll.findViewById(R.id.defaultListView);
        Log.d("AppointmentListFragment", "onCreateView:listview processed");

        ExtendedTextView text = (ExtendedTextView) ll.findViewById(R.id.defaultListHeader);
        text.setText("Appointments");

        Log.d("AppointmentListFragment", "onCreateView:textview processed");

        if (!appointments.isEmpty()) {
            Log.d("AppointmentListFragment", "Processing Appointments");
            final AppointmentAdapter adapter = new AppointmentAdapter(appointments);
            list.setAdapter(adapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                /*  @param AdapterView<?> refers to ListView
                *   @param View view refers to clicked element
                *   @param refers to position in ListView
                *   @param id, refers to item id
                */
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final FragmentTransaction trans = getActivity().getFragmentManager().beginTransaction();
                    final AppointmentAdapter adapter = (AppointmentAdapter) parent.getAdapter();
                    Appointment appointment = adapter.getItem(position);

                    trans.replace(parent.getId(), AppointmentDetailsFragment.newInstance(appointment.getEventDate()));

                    view.animate().setDuration(2000).alpha(0).withEndAction(new Runnable() {

                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            trans.commit();
                        }

                    });
                }
            });
        }
        Log.d("AppointmentListFragment", "Not Processing Appointments");
        return ll;
    }

    class AppointmentAdapter extends BaseAdapter {

        ArrayList<Appointment> appointments;

        public AppointmentAdapter(ArrayList<Appointment> appointments) {
            this.appointments = appointments;
        }

        /* @param  */
        @Override public Appointment getItem(int position) {
            return appointments.get(position);
        }

        @Override public long getItemId(int position) {
            return appointments.get(position).getId();
        }

        @Override public View getView(int position, View convertView, ViewGroup parent) {
            Appointment appointment = getItem(position);

            if (convertView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                convertView = inflater.inflate(R.layout.appointment_list_item, parent, false);
                setText(convertView,R.id.Adescription,appointment.getSynopsis());
                setText(convertView,R.id.Apet,appointment.getAppointmentPet());
                setText(convertView,R.id.Adate,appointment.getFormattedAppointmentDate());
                setText(convertView,R.id.Avet,appointment.getVet().toString());
                setText(convertView,R.id.Alocation,appointment.getLocation().toString());
                setText(convertView,R.id.Acost,String.valueOf(appointment.getCost()));
                setText(convertView,R.id.Aprocedure,appointment.getProcedure().toString());
                /*setText(convertView, R.id.defaultListName, appointment.getAppointmentPet());
                setText(convertView, R.id.defaultListTime, appointment.getAppointmentPet());*/


                //setMedia(convertView, R.id.defaultListImage, appointment.getImageResource());
            }
            return convertView;
        }

        private void setText(View view, int resource, String text) {
            ((TextView) view.findViewById(resource)).setText(text);
        }

        private void setMedia(View view, int resource, int media_resource) {
            Bitmap bmp;
            InputStream istream = getActivity().getResources().openRawResource(media_resource);
            bmp = BitmapFactory.decodeStream(istream);
            ((ImageView) view.findViewById(resource)).setImageBitmap(bmp);
        }

        public int getCount() {
            return appointments.size();
        }
    }

}
