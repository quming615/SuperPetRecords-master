package kbc.superpetrecords.models;

import java.util.*;

import android.net.Uri;
import android.os.*;

/**
 * Created by kellanbc on 6/25/14.
 */
public class Pet extends Model {

    protected int id;
    protected String name;
    protected String breed;
    protected String species;
    protected EventDate date_of_birth;
    protected EventDate adoption_date;
    protected Uri media;

    protected List<EventDate> appointments = new ArrayList<>();
    //protected ArrayList<Procedure> procedures;

    public Pet(int id, String name, String breed, String species, EventDate dob, EventDate ad, Uri media) {
        super(id);
        this.name = name;
        this.breed = breed;
        this.species = species;
        this.date_of_birth = dob;
        this.adoption_date = ad;
        this.media = media;
    }

    @Override
    public TreeMap<String, Object> getDefaults() {
        TreeMap<String, Object> map = super.getDefaults();
        map.put("Name", "name");
        map.put("Breed", "breed");
        map.put("Species", "species");
        map.put("Date of Birth", GET_CURRENT_DATE);
        map.put("Adoption Date", GET_CURRENT_DATE);
        map.put("Media", GET_IMAGE_FILE);
        return map;
    }

    public String getBreed() {
        return breed;
    }

    public String getSpecies() {
        return species;
    }

    public String getFormattedBirthdate() {
        return date_of_birth.getFormattedDate();
    }

    public String getFormattedAdoptionDate() {
        return adoption_date.getFormattedDate();
    }

    private Pet(Parcel in) {
        super(in);
        name = in.readString();
        breed = in.readString();
        species = in.readString();
        date_of_birth = in.readParcelable(EventDate.class.getClassLoader());
        adoption_date = in.readParcelable(EventDate.class.getClassLoader());
        media = Uri.parse(in.readString());
        appointments = new ArrayList<>(Arrays.asList((EventDate[]) in.readParcelableArray(EventDate.class.getClassLoader())));
    }

    /*public int getId() {
        return id;
    }*/
    @Override public int describeContents() {
        return 0;
    }

    @Override public String toString() {
        return name;
    }

    @Override public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeString(name);
        out.writeString(breed);
        out.writeString(species);
        out.writeParcelable(date_of_birth, flags);
        out.writeParcelable(adoption_date, flags);
        out.writeString(media.toString());
        Parcelable[] dates = (Parcelable[]) appointments.toArray();
        out.writeParcelableArray(dates, flags);
    }

    public static final Parcelable.Creator<Pet> CREATOR = new Parcelable.Creator<Pet>() {
        public Pet createFromParcel(Parcel in) {
            return new Pet(in);
        }

        public Pet[] newArray(int size) {
            return new Pet[size];
        }
    };

    public String getName() {
        return name;
    }

    public Uri getImageResource() {
        return media;
    }

}
