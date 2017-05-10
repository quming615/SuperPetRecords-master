package kbc.superpetrecords.fragments;

import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import kbc.superpetrecords.*;
import kbc.superpetrecords.models.*;
import kbc.superpetrecords.views.widgets.*;
import android.app.*;
/**
 * Created by kellanbc on 8/15/14.
 */
public class VetDetailsFragment extends Details {
    private Vet vet;
    public VetDetailsFragment() {}

    private int layout_id, email_id, name_id, phone_id, address_id, unit_id, city_id, zip_id, state_id;
    DetailView emailView, nameView, phoneView, addressView, unitView, cityView, zipView, stateView;

    public static VetDetailsFragment newInstance(Vet vet, int layout_id, Bundle bundle) {
        VetDetailsFragment frag = new VetDetailsFragment();
        bundle.putParcelable("vet", vet);
        bundle.putInt("layout_id", layout_id);
        frag.setArguments(bundle);
        return frag;
    }

    public static VetDetailsFragment newInstance(Vet vet) {
        VetDetailsFragment frag = new VetDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("name_id", R.id.name);
        bundle.putInt("email_id", R.id.email);
        bundle.putInt("phone_id", R.id.phone);
        bundle.putInt("address_id", R.id.address);
        bundle.putInt("unit_id", R.id.unit);
        bundle.putInt("city_id", R.id.city);
        bundle.putInt("state_id", R.id.state);
        bundle.putInt("zip_id", R.id.zip);
        bundle.putParcelable("vet", vet);
        bundle.putInt("layout_id", R.layout.vet_details);
        frag.setArguments(bundle);
        return frag;
    }

    @Override public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle args = getArguments();
        layout_id = args.getInt("layout_id");
        vet = args.getParcelable("vet");
        email_id = args.getInt("email_id");
        name_id = args.getInt("name_id");
        phone_id = args.getInt("phone_id");
        address_id = args.getInt("address_id");
        unit_id = args.getInt("unit_id");
        city_id = args.getInt("city_id");
        zip_id = args.getInt("zip_id");
        state_id = args.getInt("state_id");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup view_g, Bundle state) {
        super.onCreateView(inflater, view_g, state);
        final View layout = inflater.inflate(layout_id, view_g, false);
        emailView = (DetailView) layout.findViewById(email_id);
        nameView = (DetailView) layout.findViewById(name_id);
        phoneView = (DetailView) layout.findViewById(phone_id);
        addressView = (DetailView) layout.findViewById(address_id);
        unitView = (DetailView) layout.findViewById(unit_id);
        cityView = (DetailView) layout.findViewById(city_id);
        zipView = (DetailView) layout.findViewById(zip_id);
        stateView = (DetailView) layout.findViewById(state_id);
        return layout;
    }

    protected void writeToFields() {

    }
    
    protected void save() {
        PetContract dbHelper = ((MainActivity) getActivity()).getDatabaseHelper();

        String name = nameView.getFieldText().toString();
        String email = emailView.getFieldText().toString();
        int phone = Integer.parseInt(phoneView.getFieldText().toString());

        String address = addressView.getFieldText().toString();
        String unit = unitView.getFieldText().toString();
        String city = cityView.getFieldText().toString();
        String state = stateView.getFieldText().toString();
        String zip = zipView.getFieldText().toString();


        try {
            EmailAddress emailAddress = dbHelper.insertEmailAddress(EmailAddress.PRIMARY, email);
            //emailAddress.getId()
            PhoneNumber phoneNumber = dbHelper.insertPhoneNumber(PhoneNumber.PRIMARY, phone);
            //phoneNumber.getId()
            Location location = dbHelper.insertLocation(address, unit, city, state, zip);
            //location.getId()
            Contact contact = dbHelper.insertContact(name, phoneNumber, emailAddress, location);
            Vet vet = dbHelper.insertVet(name, contact);
        } catch(PetContract.DatabaseInsertException ex) {
            Log.e("Database Insert Exception", ex.getMessage());
        }
    }

    protected void edit() {

    }

    protected void add() {

    }
}
