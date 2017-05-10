package kbc.superpetrecords.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import kbc.superpetrecords.Details;
import kbc.superpetrecords.MainActivity;
import kbc.superpetrecords.PetContract;
import kbc.superpetrecords.R;
import kbc.superpetrecords.dialogfragments.DatePickerFragment;
import kbc.superpetrecords.models.EventDate;
import kbc.superpetrecords.models.Pet;

/**
 * Created by kellanbc on 6/27/14.
 */
public class PetDetailsFragment extends Details {

    private enum Species {
      BIRD, CAT, DOG, MYSTERY, RAT, FISH
    }

    private final static int SELECTION_MASK =   0b00000011;
    private final static int GRAYSCALE_IMAGE =  0b00000001;
    private final static int COLOR_IMAGE =      0b00000010;

    private ImageButton imageReceiver, dobButton;
    private EditText breed, name, species;
    private TextView dob;
    String media_uri_path;
    int dob_month, dob_year, dob_date;

    HashMap<Species, HashMap<Integer, Bitmap>> speciesBitmaps = new HashMap<>();
    HashMap<Species, Integer> speciesSelector = new HashMap<>();

    private Pet pet;
    //intent flags
    public PetDetailsFragment() {}

    public static PetDetailsFragment newInstance(Pet pet) {
        Bundle bundle = new Bundle();
        PetDetailsFragment frag = new PetDetailsFragment();
        bundle.putParcelable("pet", pet);
        frag.setArguments(bundle);
        return frag;
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Activity act = getActivity();
        Toast.makeText(act, "Back in the Fragment(OnActivityResult): RequestCode(" + requestCode + "), ResultCode(" + resultCode + ")", Toast.LENGTH_LONG).show();
        if (resultCode == Activity.RESULT_OK) {
            switch(requestCode) {
                case(SELECT_PICTURE):
                    //Toast.makeText(getActivity(), "SELECT_PICTURE(" + SELECT_PICTURE + ")", Toast.LENGTH_LONG).show();
                    Uri selectedImageUri = data.getData();
                    media_uri_path = getAbsolutePath(selectedImageUri);
                    Log.d("Media URI PATH", media_uri_path);
                    //Toast.makeText(act, media_uri_path, Toast.LENGTH_SHORT);
                    imageReceiver.setImageBitmap(getImageBitmap(selectedImageUri));
                    break;
                case(SELECT_DATE):
                    //Toast.makeText(getActivity(), "SELECT_DATE(" + SELECT_DATE + ")", Toast.LENGTH_LONG).show();
                    Bundle b = data.getExtras();
                    dob_date = b.getInt("dayOfMonth");
                    dob_month = b.getInt("month");
                    dob_year = b.getInt("year");
                    dob.setText((dob_month + 1) + "/" + dob_date + "/" + dob_year);
                    break;
                default:
                    //Toast.makeText(getActivity(), "DEFAULT", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        //pet = bundle.getParcelable("pet");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup view_g, Bundle state) {
        final View layout = inflater.inflate(R.layout.pet_details, view_g, false);

        imageReceiver = (ImageButton) layout.findViewById(R.id.petImageButton);
        name = (EditText) layout.findViewById(R.id.nameEditText);
        breed = (EditText) layout.findViewById(R.id.breedEditText);
        species = (EditText) layout.findViewById(R.id.speciesEditText);
        dob = (TextView) layout.findViewById(R.id.birthdateEditText);
        dobButton = (ImageButton) layout.findViewById(R.id.birthdateButton);

        dobButton.setOnClickListener(new Button.OnClickListener() {
            @Override public void onClick(View v) {
                Log.d("PetDetails::onCreateView...Button.OnClickListener.OnClick", v.toString());
                DatePickerFragment picker = new DatePickerFragment();
                picker.setTargetFragment(PetDetailsFragment.this, SELECT_DATE);
                ((MainActivity) getActivity()).attachDialogFragment(picker, "DatePickerForPetDetails");
            }
        });

        writeToFields();
        //dob = (DatePicker) layout.findViewById(R.id.birthdatePicker);
        /*
        speciesGrid = (GridLayout) layout.findViewById(R.id.speciesGrid);
        initializeSpeciesBitmaps(layout, R.id.birdBadgeImage, R.drawable.bird_badge, R.drawable.bird_badge_gray, Species.BIRD);
        initializeSpeciesBitmaps(layout, R.id.catBadgeImage, R.drawable.cat_badge, R.drawable.cat_badge_gray, Species.CAT);
        initializeSpeciesBitmaps(layout, R.id.dogBadgeImage, R.drawable.dog_badge, R.drawable.dog_badge_gray, Species.DOG);
        initializeSpeciesBitmaps(layout, R.id.ratBadgeImage, R.drawable.rat_badge, R.drawable.rat_badge_gray, Species.RAT);
        initializeSpeciesBitmaps(layout, R.id.mysteryBadgeImage, R.drawable.mystery_badge, R.drawable.mystery_badge_gray, Species.MYSTERY);
        initializeSpeciesBitmaps(layout, R.id.fishBadgeImage, R.drawable.fish_badge, R.drawable.fish_badge_gray, Species.FISH);
        */
        try {
            setButtonClickListener(imageReceiver, this.getClass().getMethod("requestImageResource"));

            //setButtonClickListener(addButton, this.getClass().getMethod("add"));
        } catch (NoSuchMethodException ex) {
            Log.d("NoSuchMethodException", ex.getMessage());
        }

        return layout;
    }

    protected void writeToFields() {
        Bundle b = getArguments();
        if (b.containsKey("pet")) {
            pet = b.getParcelable("pet");
            if (pet != null) {
                if (pet.getName() != null) name.setText(pet.getName());
                if (pet.getBreed() != null) breed.setText(pet.getBreed());
                if (pet.getSpecies() != null) species.setText(pet.getSpecies());
                if (pet.getFormattedBirthdate() != null) dob.setText(pet.getFormattedBirthdate());
                if (pet.getImageResource() != null) {
                    Uri path = Uri.parse("file://" + pet.getImageResource());
                    imageReceiver.setImageBitmap(getImageBitmap(path));
                }

            }
        }
    }

    private void initializeSpeciesBitmaps(View layout, int viewId, int resourceId, int resourceIdOff, final Species species) {
        ImageButton badge = (ImageButton) layout.findViewById(viewId);
        Bitmap img = getBitmapFromResource(resourceId);
        Bitmap grayscaleImg = getBitmapFromResource(resourceIdOff); //convertBitmapToGrayscale(img);
        badge.setImageBitmap(grayscaleImg);

        HashMap<Integer, Bitmap> imgs = new HashMap<>();
        imgs.put(GRAYSCALE_IMAGE, grayscaleImg);
        imgs.put(COLOR_IMAGE, img);
        speciesBitmaps.put(species, imgs);
        speciesSelector.put(species, GRAYSCALE_IMAGE);

        badge.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view) {
                int selector = speciesSelector.get(species);
                ((ImageButton) view).setImageBitmap(speciesBitmaps.get(species).get(selector ^ SELECTION_MASK));
            }
        });
    }

    //TODO 编辑新建保存按钮操作，可删除，继承自
    @Override
    public boolean onOptionsItemSelected( MenuItem item){
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
            default:
                return true;
        }
        //return true;
        //return super.onOptionsItemSelected(item);
    }

    protected void save() {
        PetContract dbHelper = ((MainActivity) getActivity()).getDatabaseHelper();
        String petName = name.getText().toString();
        String petBreed = breed.getText().toString();
        String petSpecies = species.getText().toString();
        String rrule = "FREQ=YEARLY;BYMONTH=" + dob_month + ";BYMONTHDAY=" + dob_month + ";";
        try {
            EventDate date = dbHelper.insertAllDayEvent(dob_date, dob_month, dob_year, rrule);
            Toast.makeText(getActivity(), date.toString(), Toast.LENGTH_LONG).show();
            Pet pet = dbHelper.insertPet(petName, petBreed, petSpecies, date, date, media_uri_path);
            Toast.makeText(getActivity(), pet.toString(), Toast.LENGTH_LONG).show();

        } catch(PetContract.DatabaseInsertException ex) {
            Log.e("Database Insert Exception", ex.getMessage());
        }
        Toast.makeText(getActivity(), petName + " is a cute cuddly " + petBreed + " who was born on " + dob_date + "/" + dob_month + "/" + dob_year + ".", Toast.LENGTH_LONG);
    }

    protected void edit() {

    }

    protected void add() {

    }

}