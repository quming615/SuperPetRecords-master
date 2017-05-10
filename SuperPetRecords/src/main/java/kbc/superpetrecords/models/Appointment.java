package kbc.superpetrecords.models;

import android.os.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * Created by kellanbc on 7/8/14.
 */
public class Appointment extends Model {

    private Vet vet;
    private Pet pet;
    private Location location;
    private EventDate date;
    private Procedure procedure;
    private float cost;
    private String synopsis;

    public Appointment(Parcel in) {
        super(in);
        vet = in.readParcelable(Vet.class.getClassLoader());
        pet = in.readParcelable(Pet.class.getClassLoader());
        location = in.readParcelable(Location.class.getClassLoader());
        date = in.readParcelable(EventDate.class.getClassLoader());
        procedure = in.readParcelable(Procedure.class.getClassLoader());
        cost = in.readFloat();
        synopsis = in.readString();
    }

    public Appointment() {}

    public Appointment(int id, Vet vet, Pet pet, EventDate date, Location location, Procedure procedure, float cost, String synopsis) {
        super(id);
        this.vet = vet;
        this.pet = pet;
        this.date = date;
        this.location = location;
        this.procedure = procedure;
        this.cost = cost;
        this.synopsis = synopsis;
    }

    public TreeMap<String, Object> getDefaults() {
        TreeMap<String, Object> map = super.getDefaults();
        map.put("Vet", GET_VETS);
        map.put("Pet", GET_PETS);
        map.put("Location", GET_LOCATIONS);
        map.put("EventDate", GET_EVENTS);
        map.put("Procedure", GET_PROCEDURES);
        map.put("Cost", 0);
        map.put("Synopsis", "");
        return map;
    }

    public String getAppointmentPet() {
        return pet.getName();
    }

    public String getFormattedAppointmentDate() {
        return date.getFormattedDate();
    }

    public int getYear() {
        return date.getYear();
    }

    public EventDate getEventDate() {
        return date;
    }

    public int getMonth() {
        return date.getMonth();
    }

    public int getDate() {
        return date.getDate();
    }

    public int getStartTime() {
        return date.getStartTime();
    }

    public int getEndTime() {
        return date.getEndTime();
    }

    public Procedure getProcedure() {
        return procedure;
    }

    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }

    public float getCost() {
        return cost;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public void setDate(EventDate date) {
        this.date = date;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public Vet getVet() {
        return vet;
    }

    public void setVet(Vet vet) {
        this.vet = vet;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override public String toString() {
        return date.getFormattedDate();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(vet, flags);
        dest.writeParcelable(pet, flags);
        dest.writeParcelable(location, flags);
        dest.writeParcelable(date, flags);
        dest.writeParcelable(procedure, flags);
        dest.writeFloat(cost);
        dest.writeString(synopsis);
    }

    public static final Parcelable.Creator<Appointment> CREATOR = new Parcelable.Creator<Appointment>() {
        public Appointment createFromParcel(Parcel in) {
            return new Appointment(in);
        }

        public Appointment[] newArray(int size) {
            return new Appointment[size];
        }
    };
}
