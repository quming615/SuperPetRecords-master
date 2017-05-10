package kbc.superpetrecords;

import android.database.Cursor;
import android.database.sqlite.*;
import android.content.*;

import java.io.File;
import java.lang.reflect.*;

import android.net.Uri;
import android.provider.*;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.Toast;

import java.util.*;

import kbc.superpetrecords.models.*;

public final class PetContract extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 10;
    public static final String DATABASE_NAME = "super_pet_records.db";
    private SQLiteDatabase db;
    //private SQLiteDatabase db1 = SQLiteDatabase.openOrCreateDatabase();


    Class tables[] = {Pets.class, Vets.class, PhoneNumbers.class, EmailAddresses.class, ContactEmailAddresses.class,
            ContactPhoneNumbers.class, ContactLocations.class, Hours.class, Contacts.class, EventDates.class,
            Appointments.class, Procedures.class, Reminders.class,
            Immunizations.class, Bills.class, BillItems.class, ImmunizationRecords.class, PetNotes.class,
            Organizations.class, Locations.class, Adoptions.class, VetNotes.class,
            PetProcedures.class, UserOptions.class
    };

    public PetContract(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getReadableDatabase();
    }

    /*private static boolean mainTmpDirSet = false;
    @Override
    public SQLiteDatabase getReadableDatabase() {
        if (!mainTmpDirSet) {
            boolean rs = new File("/data/data/kbc.superpetrecords/databases/main").mkdir();
            Log.d("ahang", rs + "");
            super.getReadableDatabase().execSQL("PRAGMA temp_store_directory = '/data/data/kbc.superpetrecords/databases/main'");
            mainTmpDirSet = true;
            return super.getReadableDatabase();
        }
        return super.getReadableDatabase();
    }*/

    public interface DatabaseQueryHandler<E> {
        public void updateList(Cursor cursor, ArrayList<E> list);
    }

    /**
     * 创建表格
     * db：SQLiteDatabase对象
     */
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        for (int i = 0; i < tables.length; i++) {
            Field table_field, columns_field;
            String table;
            String query;

            String[][] columns;

            try {
                table_field = tables[i].getField("TABLE");
                columns_field = tables[i].getField("COLUMNS");

                table = (String) table_field.get(null);
                columns = (String[][]) columns_field.get(null);

                query = "CREATE TABLE " + table + " (id INTEGER PRIMARY KEY AUTOINCREMENT, ";

                for (int j = 0; j < columns.length; j++) {
                    for (int k = 0; k < columns[j].length; k++) {
                        query += columns[j][k];
                        if (k < columns[j].length - 1) {
                            query += " ";
                        } else {
                            query += ", ";
                        }
                    }
                }

                query = query.substring(0, query.length() - 2);
                query += ")";

                //Log.d("SQLite Query", query);
                db.execSQL(query);
                //db.execSQL("insert into person(name, age) values(?,?)",new Object[]{"测试数据",4});

            } catch (NoSuchFieldException nsfe) {
                System.out.println(nsfe);
            } catch (IllegalAccessException iae) {
                System.out.println(iae);
            }
        }
    }


    /**
     * 数据库更新
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("Upgrading the Database", "OldVersion(" + oldVersion + "), NewVersion(" + newVersion + ")");
        for (int i = 0; i < tables.length; i++) {
            Field table_field;
            String table;

            try {
                table_field = tables[i].getField("TABLE");
                table = (String) table_field.get(null);
                db.execSQL("DROP TABLE IF EXISTS " + table);

            } catch (NoSuchFieldException ex) {
                Log.e("NoSuchField", ex.getMessage());
            } catch (IllegalAccessException ex) {
                Log.e("IllegalAccessException", ex.getMessage());
            }
        }
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private <T extends Model> ArrayList<T> makeList(DatabaseQueryHandler<T> handler, Cursor cursor) {
        int i = 0;
        ArrayList<T> list = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                handler.updateList(cursor, list);
            } while (cursor.moveToNext());
        }
        return list;
    }

    private <E extends Model> ArrayList<E> populateList(String table, DatabaseQueryHandler<E> handler) {
        if (!db.isOpen()) {
            db = getReadableDatabase();
        }

        //查询整个表格的数据
        Cursor cursor = db.rawQuery("SELECT * FROM " + table, null);
        //将整个表格的数据列表化
        ArrayList<E> list = makeList(handler, cursor);

        return list;
    }

    public long insertOne(String table, ContentValues values) throws DatabaseInsertException {
        if (!db.isOpen()) {
            db = getWritableDatabase();
        } else if (db.isReadOnly()) {
            db.close();
            db = getWritableDatabase();
        }

        long id = db.insert(table, null, values);
        if (id != -1) {
            return id;
        } else {
            throw new DatabaseInsertException("Unable to insert into database.");
        }
    }

    public long updateOne(String table, ContentValues values, long id) {
        if (!db.isOpen()) {
            db = getWritableDatabase();
        } else if (db.isReadOnly()) {
            db.close();
            db = getWritableDatabase();
        }
        String[] args = {Long.toString(id)};
        return db.update(table, values, "id = ?", args);
    }

    private Cursor selectWhere(String table, String field, int id) {
        return selectWhere(table, field, Integer.toString(id));
    }

    private Cursor selectWhere(String table, String field, String id) {
        String[] args = {id};
        String[] fields = {field};
        return selectWhere(table, fields, args);
    }

    private Cursor selectWhere(String table, String[] fields, String[] args) {
        if (!db.isOpen()) {
            db = getReadableDatabase();
        }

        String query = "SELECT * FROM " + table + " WHERE";

        for (int i = 0; i < fields.length; i++) {
            query += String.format(" %s = ? AND", fields[i]);
        }

        query = query.substring(0, query.length() - 4);
        Log.d("Query", query);

        Cursor cursor = db.rawQuery(query, args);
        return cursor;
    }


    private Cursor doRawQuery(String query, String[] args) {
        if (!db.isOpen()) {
            db = getWritableDatabase();
        } else if (db.isReadOnly()) {
            db.close();
            db = getWritableDatabase();
        }

        return db.rawQuery(query, args);
    }

    //TODO 存在问题  parseQuery
    public <E extends Model> ArrayList<E> parseQuery(Class clz, ArrayList<E> list, Cursor cursor) {
        int count = cursor.getColumnCount();


        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            E newItem;
            try {
                newItem = (E) clz.newInstance();
                list.add(newItem);

                while (!cursor.isAfterLast()) {
                    String[] cols = cursor.getColumnNames();
                    for (int i = 0; i < count; i++) {
                        int type_id = cursor.getType(i);
                        String methodName = "set" + cols[i].substring(0, 1).toUpperCase() + cols[i].substring(1);
                        Method method = clz.getMethod(methodName);

                        switch (type_id) {
                            case (Cursor.FIELD_TYPE_INTEGER):
                                int int_param = cursor.getInt(i);
                                method.invoke(newItem, int_param);
                                break;
                            case (Cursor.FIELD_TYPE_FLOAT):
                                float float_param = cursor.getFloat(i);
                                method.invoke(newItem, float_param);
                                break;
                            case (Cursor.FIELD_TYPE_STRING):
                                String string_param = cursor.getString(i);
                                method.invoke(newItem, string_param);
                                break;
                            case (Cursor.FIELD_TYPE_BLOB):
                                break;
                            default:
                        }

                    }
                }
            } catch (InstantiationException ex) {
                Log.e("Instantiation Exception", ex.getMessage());
            } catch (IllegalAccessException ex) {
                Log.e("Illegal Access Exception", ex.getMessage());
            } catch (NoSuchMethodException ex) {
                Log.e("No Such Field", ex.getMessage());
            } catch (InvocationTargetException ex) {
                Log.e("Invocation Target", ex.getMessage());
            }
            cursor.moveToNext();
        }

        return list;
    }


    private Cursor selectOne(String table, int id) {
        if (!db.isOpen()) {
            db = getReadableDatabase();
        }

        String query = "SELECT * FROM " + table + " WHERE id = ?";
        String[] args = {Integer.toString(id)};
        Cursor cursor = db.rawQuery(query, args);
        Log.d("Cursor", cursor.toString());
        return cursor;
    }

    private String selectUniqueString(String table, String return_field, String[] option_fields, String[] option_args) {
        if (!db.isOpen()) {
            db = getReadableDatabase();
        }

        String query_string = "SELECT " + return_field + " FROM " + table;
        int numConditions = option_fields.length;

        if (numConditions > 0) {
            query_string += " WHERE";
            for (int i = 0; i < numConditions; i++) {
                query_string += " " + option_fields[i] + " = ?";
                if (i != numConditions - 1) {
                    query_string += " AND";
                }
            }
        }

        Cursor cursor = db.rawQuery(query_string, option_args);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursor.getString(0);
        }
        return null;
    }

    public Uri parseUri(String uri) {
        if (uri != null) {
            return Uri.parse(uri);
        } else {
            return Uri.EMPTY;
        }

    }
    /* User Options */

    public String getUserOption(String option_name) {
        String[] option_args = {option_name};
        String[] option_fields = {"option_name"};
        return selectUniqueString("user_options", "option_value", option_fields, option_args);
    }


    /* Pets */

    public ArrayList<Pet> getPets() {
        DatabaseQueryHandler<Pet> handler = new DatabaseQueryHandler<Pet>() {
            @Override
            public void updateList(Cursor cursor, ArrayList<Pet> list) {
                list.add(getPet(cursor));
            }
        };
        return populateList("pets", handler);
    }

    public Pet insertPet(String name, String breed, String species, EventDate birthdate, EventDate adoptionDate, String media) throws DatabaseInsertException {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("breed", breed);
        values.put("species", species);

        if (birthdate != null) {
            values.put("birthdate", birthdate.getId());
        }
        if (adoptionDate != null) {
            values.put("adoption_date", adoptionDate.getId());
        }

        values.put("resource_uri", media);
        long id;
        Cursor cursor = selectWhere("pets", "name", name);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getInt(0);
            updateOne("pets", values, id);
        } else {
            id = insertOne("pets", values);
        }

        return new Pet((int) id, name, breed, species, birthdate, adoptionDate, Uri.parse(media));
    }

    public Pet getPet(int id) {
        Cursor cursor = selectOne("pets", id);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return getPet(cursor);
        } else {
            return null;
        }
    }

    public Pet getPet(Cursor cursor) {
        if (cursor.getCount() > 0) {
            int id;

            String name, breed, species;
            EventDate birthdate, adoptionDate;
            Uri media;

            id = cursor.getInt(0);
            name = cursor.getString(1);
            breed = cursor.getString(2);
            Log.d("Pet(" + id + ": " + name + ")", "Breed: " + breed);

            species = cursor.getString(3);
            Log.d("Pet(" + id + ": " + name + ")", "Species: " + species);

            birthdate = getEventDate(cursor.getInt(4));
            //Log.d("Pet(" + id + ": " + name + ")", "Birthdate: " + birthdate.getFormattedDate());

            adoptionDate = getEventDate(cursor.getInt(5));
            String media_string = cursor.getString(6);
            Log.d("Pet(" + id + ": " + name + ")", "Uri: " + media_string);

            media = parseUri(media_string);

            return new Pet(id, name, breed, species, birthdate, adoptionDate, media);
        }
        return null;
    }

    /**
     * 通过eventDateMap查出所有的PetMap
     *
     * @param eventDateMap
     * @return
     */
    private Map<Integer, Pet> getPetMap(Map<Integer, EventDate> eventDateMap) {
        String query = "select * from pets";
        Cursor cursor = db.rawQuery(query, null);
        Map<Integer, Pet> petMap = new HashMap<>();
        if (cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                int id;

                String name, breed, species;
                EventDate birthdate, adoptionDate;
                Uri media;

                id = cursor.getInt(0);
                name = cursor.getString(1);
                breed = cursor.getString(2);
                Log.d("Pet(" + id + ": " + name + ")", "Breed: " + breed);

                species = cursor.getString(3);
                Log.d("Pet(" + id + ": " + name + ")", "Species: " + species);

                birthdate = eventDateMap.get(cursor.getInt(4));
                //birthdate = getEventDate(cursor.getInt(4));
                //Log.d("Pet(" + id + ": " + name + ")", "Birthdate: " + birthdate.getFormattedDate());

                adoptionDate = eventDateMap.get(cursor.getInt(5));
                String media_string = cursor.getString(6);
                Log.d("Pet(" + id + ": " + name + ")", "Uri: " + media_string);

                media = parseUri(media_string);

                petMap.put(id, new Pet(id, name, breed, species, birthdate, adoptionDate, media));
            }
        }
        return petMap;
    }

    /* Events */

    public EventDate insertAllDayEvent(int day, int month, int year, String rrule) throws DatabaseInsertException {
        return insertDurationEvent(day, month, year, 0, 240000, rrule);
    }

    public EventDate insertDurationEvent(int day, int month, int year, int dtstart, int dtend, String rrule) throws DatabaseInsertException {
        Calendar calendar = new GregorianCalendar(year, month, day);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        ContentValues values = new ContentValues();
        values.put("date", day);
        values.put("month", month);
        values.put("year", year);
        values.put("rrule", rrule);
        values.put("dtstart", dtstart);
        values.put("dtend", dtend);
        values.put("all_day_event", (dtstart == 0 && dtend == 240000) ? 0 : 1);
        long id = insertOne("event_dates", values);

        if (id != -1) {
            EventDate new_one = new EventDate((int) id, day, month, year, dtstart, dtend, rrule);
            return new_one;
        } else {
            throw new DatabaseInsertException("Unable to insert new Event Date");
        }
    }

    public EventDate getEventDate(int id) {
        Cursor cursor = selectOne("event_dates", id);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int day = cursor.getInt(1);
            int month = cursor.getInt(2);
            int year = cursor.getInt(3);
            int allDay = cursor.getInt(4);
            int dtstart = cursor.getInt(5);
            int dtend = cursor.getInt(6);
            String rrule = cursor.getString(7);


            Log.d("getDate", day + "/" + month + "/" + year);

            if (allDay == 1) {
                return new EventDate(id, day, month, year, rrule);
            } else {
                return new EventDate(id, day, month, year, dtstart, dtend, rrule);
            }
        } else {
            Log.d("getDate", "No Date For:" + id);
            return new EventDate(id);
        }

    }

    private Map<Integer, EventDate> getEventDateMap() {
        String queryEventDate = "SELECT * FROM event_dates";
        Cursor cursor = db.rawQuery(queryEventDate, null);
        Map<Integer, EventDate> eventDateMap = new HashMap<>();
        if (cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                int id = cursor.getInt(0);
                int day = cursor.getInt(1);
                int month = cursor.getInt(2);
                int year = cursor.getInt(3);
                int allDay = cursor.getInt(4);
                int dtstart = cursor.getInt(5);
                int dtend = cursor.getInt(6);
                String rrule = cursor.getString(7);
                if (allDay == 1) {
                    eventDateMap.put(id, new EventDate(id, day, month, year, rrule));
                } else {
                    eventDateMap.put(id, new EventDate(id, day, month, year, dtstart, dtend, rrule));
                }
            }
        }
        return eventDateMap;
    }

    /* Locations */

    public Location getLocation(int id) {
        if (!db.isOpen()) {
            db = getReadableDatabase();
        }

        Cursor cursor = selectOne("locations", id);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            return getLocation(cursor);
        } else {
            return null;
        }
    }

    public int[] getLocationList() {
        ArrayList<Location> locations = getLocations();
        int[] location_ids = new int[locations.size()];
        for (int i = 0; i < locations.size(); i++) {
            location_ids[i] = locations.get(i).getId();
        }
        return location_ids;
    }

    public ArrayList<Location> getLocations() {
        DatabaseQueryHandler<Location> handler = new DatabaseQueryHandler<Location>() {
            @Override
            public void updateList(Cursor cursor, ArrayList<Location> list) {
                list.add(getLocation(cursor));
            }
        };
        return populateList("locations", handler);
    }

    public Location getLocation(Cursor cursor) {

        int id;
        String address, unit, city, state, zip;

        id = cursor.getInt(0);
        address = cursor.getString(1);
        unit = cursor.getString(2);
        city = cursor.getString(3);
        state = cursor.getString(4);
        zip = cursor.getString(5);

        return new Location(id, address, unit, city, state, zip);
    }

    private Map<Integer, Location> getLocationMap() {
        String query = "select * from locations";
        Cursor cursor = db.rawQuery(query, null);
        Map<Integer, Location> locationMap = new HashMap<>();
        if (cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                int id;
                String address, unit, city, state, zip;

                id = cursor.getInt(0);
                address = cursor.getString(1);
                unit = cursor.getString(2);
                city = cursor.getString(3);
                state = cursor.getString(4);
                zip = cursor.getString(5);
                locationMap.put(id, new Location(id, address, unit, city, state, zip));
            }
        }
        return locationMap;
    }

    public Location insertLocation(String address, String unit, String city, String state, String zip) throws DatabaseInsertException {
        ContentValues values = new ContentValues();

        values.put("address", address);
        values.put("unit", unit);
        values.put("city", city);
        values.put("state", state);
        values.put("zip", zip);

        long id;
        String[] conditions = {"address", "unit", "zip"};
        String[] args = {address, unit, zip};
        Cursor cursor = selectWhere("locations", conditions, args);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getInt(0);
            updateOne("locations", values, id);
        } else {
            id = insertOne("locations", values);
        }

        return new Location((int) id, address, unit, city, state, zip);
    }

    /* Vets */

    private Map<Integer, Vet> getVetMap() {
        Map<Integer, Contact> contactMap = queryAllContact();
        Cursor cursor = db.rawQuery("SELECT * FROM vets", null);
        Map<Integer, Vet> vetMap = new HashMap<>();
        if (cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                int contactId = cursor.getInt(2);
                Contact contact = contactMap.get(contactId);
                vetMap.put(id, new Vet(id, name, contact));
            }
        }
        return vetMap;
    }

    public ArrayList<Vet> getVets() {
        DatabaseQueryHandler<Vet> handler = new DatabaseQueryHandler<Vet>() {
            @Override
            public void updateList(Cursor cursor, ArrayList<Vet> list) {
                list.add(getVet(cursor));
            }
        };
        return populateList("vets", handler);
    }

    public ArrayList<Vet> getVetss() {
        Map<Integer, Contact> contactMap = queryAllContact();
        ArrayList<Vet> vetList = new ArrayList<Vet>();
        Cursor cursor = db.rawQuery("SELECT * FROM vets", null);
        if (cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                int contactId = cursor.getInt(2);
                Contact contact = contactMap.get(contactId);
                vetList.add(new Vet(id, name, contact));
            }
        }
        return vetList;
    }

    public Vet getVetById(int id) {
        Map<Integer, Contact> contactMap = queryAllContact();
        Cursor cursor = selectOne("vets", id);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int ids = cursor.getInt(0);
            String name = cursor.getString(1);
            int contactId = cursor.getInt(2);
            Contact contact = contactMap.get(contactId);
            return new Vet(ids, name, contact);
        }
        return null;
    }


    public Vet getVet(int id) {
        Cursor cursor = selectOne("vets", id);
        return getVet(cursor);
    }

    public Vet getVet(Cursor cursor) {
        String name;
        Contact contact;
        int id;

        if (cursor != null) {
            cursor.moveToFirst();
            id = cursor.getInt(0);
            name = cursor.getString(1);
            contact = getContact(cursor.getInt(2));

            return new Vet(id, name, contact);
        } else {
            return null;
        }
    }

    /**
     * 插入兽医操作
     *
     * @param name
     * @param contact
     * @return （数据库插入异常捕获）
     */
    public Vet insertVet(String name, Contact contact) throws DatabaseInsertException {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("contact_id", contact.getId());

        long id;
        String[] conditions = {"name", "contact_id"};
        String[] args = {name, Integer.toString(contact.getId())};
        Cursor cursor = selectWhere("vets", conditions, args);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getInt(0);
            updateOne("vets", values, id);
        } else {
            /*try{

            }catch (DatabaseInsertException e){
                id=-1;
                Log.e("id=-1","插入错误，DatabaseInsertException");
            }*/
            id = insertOne("vets", values);
        }
        return new Vet((int) id, name, contact);
    }
    /* Procedures */

    public ArrayList<Procedure> getProcedures() {
        DatabaseQueryHandler<Procedure> handler = new DatabaseQueryHandler<Procedure>() {
            @Override
            public void updateList(Cursor cursor, ArrayList<Procedure> list) {
                list.add(getProcedure(cursor));
            }
        };
        return populateList("procedures", handler);
    }

    public Procedure getProcedure(int id) {
        Cursor cursor = selectOne("procedures", id);
        return getProcedure(cursor);
    }

    public Procedure getProcedure(Cursor cursor) {
        int id;
        String name, description;

        cursor.moveToFirst();
        id = cursor.getInt(0);
        name = cursor.getString(1);
        description = cursor.getString(2);

        return new kbc.superpetrecords.models.Procedure(id, name, description);
    }

    private Map<Integer, Procedure> getProcedureMap() {
        Map<Integer, Procedure> procedureMap = new HashMap<>();
        Cursor cursor = db.rawQuery("SELECT * FROM procedures", null);
        if (cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                int id;
                String name, description;

                cursor.moveToFirst();
                id = cursor.getInt(0);
                name = cursor.getString(1);
                description = cursor.getString(2);
                procedureMap.put(id, new kbc.superpetrecords.models.Procedure(id, name, description));
            }
        }
        return procedureMap;
    }

    public Procedure insertProcedure(String name, String description) throws DatabaseInsertException {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("description", description);

        long id;
        Cursor cursor = selectWhere("procedures", "name", name);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getInt(0);
            updateOne("procedures", values, id);
        } else {
            id = insertOne("procedures", values);
        }


        return new Procedure((int) id, name, description);
    }


    /* Phone Numbers */

    public ArrayList<PhoneNumber> getPhoneNumbers(int contact_id) {
        DatabaseQueryHandler<PhoneNumber> handler = new DatabaseQueryHandler<PhoneNumber>() {
            @Override
            public void updateList(Cursor cursor, ArrayList<PhoneNumber> list) {
                list.add(getPhoneNumber(cursor));
            }
        };
        return makeList(handler, selectWhere("phone_numbers", "contact_id", contact_id));
    }

    public PhoneNumber getPhoneNumber(Cursor cursor) {

        int id, phone, type;
        id = cursor.getInt(0);
        type = cursor.getInt(1);
        phone = cursor.getInt(2);
        Contact contact = getContact(cursor.getInt(3));
        return new PhoneNumber(id, type, phone);
    }

    public PhoneNumber getPhoneNumber(int id) {
        Cursor cursor = selectOne("phone_numbers", id);

        int type, number;

        if (cursor != null) {
            cursor.moveToFirst();
            type = cursor.getInt(1);
            number = cursor.getInt(2);
            return new PhoneNumber(id, type, number);
        } else {
            return null;
        }
    }

    public PhoneNumber insertPhoneNumber(int type, int phone) throws DatabaseInsertException {
        ContentValues values = new ContentValues();
        values.put("type", type);
        values.put("phone_number", phone);
        long id;

        String table = "phone_numbers";
        Cursor cursor = selectWhere(table, "phone_number", phone);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getInt(0);
            updateOne(table, values, id);
        } else {
            id = insertOne(table, values);
        }

        return new PhoneNumber((int) id, type, phone);
    }

    /* Email */

    public ArrayList<EmailAddress> getEmailAddresses(int contact_id) {
        DatabaseQueryHandler<EmailAddress> handler = new DatabaseQueryHandler<EmailAddress>() {
            @Override
            public void updateList(Cursor cursor, ArrayList<EmailAddress> list) {
                list.add(getEmailAddress(cursor));
            }
        };
        return makeList(handler, selectWhere("email_addresses", "contact_id", contact_id));
    }

    public EmailAddress getEmailAddress(Cursor cursor) {

        int id, type;
        String email;

        id = cursor.getInt(0);
        type = cursor.getInt(1);
        email = cursor.getString(2);
        return new EmailAddress(id, type, email);
    }

    public EmailAddress getEmailAddress(int id) {
        Cursor cursor = selectOne("emailAddresses", id);

        String name;
        int type;

        if (cursor != null) {
            cursor.moveToFirst();
            type = cursor.getInt(1);
            name = cursor.getString(2);
            return new EmailAddress(id, type, name);
        } else {
            return null;
        }
    }

    public EmailAddress insertEmailAddress(int type, String email) throws DatabaseInsertException {
        ContentValues values = new ContentValues();
        values.put("type", type);
        values.put("email_address", email);

        long id;
        String table = "email_addresses";
        Cursor cursor = selectWhere(table, "email_address", email);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getInt(0);
            updateOne(table, values, id);
        } else {
            id = insertOne(table, values);
        }
        return new EmailAddress((int) id, type, email);
    }


    /* Appointments */

    public ArrayList<Appointment> getAppointments() {
        DatabaseQueryHandler<Appointment> handler = new DatabaseQueryHandler<Appointment>() {
            @Override
            public void updateList(Cursor cursor, ArrayList<Appointment> list) {
                list.add(getAppointment(cursor));
            }
        };
        return populateList("appointments", handler);
    }

    public ArrayList<Appointment> getAppointmentsList() {
        String query = "SELECT * FROM appointments";
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Appointment> appointmentList = new ArrayList<>();
        Map<Integer, EventDate> eventDateMap = getEventDateMap();
        Map<Integer, Vet> vetMap = getVetMap();
        Map<Integer, Pet> petMap = getPetMap(eventDateMap);
        Map<Integer, Location> locationMap = getLocationMap();
        Map<Integer, Procedure> procedureMap = getProcedureMap();
        if (cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                int id = cursor.getInt(0);
                Vet vet = vetMap.get(cursor.getInt(1));
                Pet pet = petMap.get(cursor.getInt(2));

                EventDate date = eventDateMap.get(cursor.getInt(3));
                Location loc = locationMap.get(cursor.getInt(4));
                Procedure pro = procedureMap.get(cursor.getInt(5));
                float cost = cursor.getFloat(6);
                String synopsis = cursor.getString(7);
                appointmentList.add(new Appointment(id, vet, pet, date, loc, pro, cost, synopsis));
            }
        }
        return appointmentList;
    }


    public Appointment getAppointment(Cursor cursor) {
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int id = cursor.getInt(0);
            Vet vet = getVetById(cursor.getInt(1));
            /*Map<Integer,EventDate> eventDateMap=getEventDateMap();
            EventDate evenDate = eventDateMap.get(cursor.getInt(3));*/
            Pet pet = getPet(cursor.getInt(2));

            EventDate date = getEventDate(cursor.getInt(3));
            Location loc = getLocation(cursor.getInt(4));
            Procedure pro = getProcedure(cursor.getInt(5));
            float cost = cursor.getFloat(6);
            String synopsis = cursor.getString(7);
            return new Appointment(id, vet, pet, date, loc, pro, cost, synopsis);
        }
        return null;
    }

    public Appointment insertAppointment(Vet vet, Pet pet, EventDate date, Location loc, Procedure pro, float cost, String synoposis) throws DatabaseInsertException {
        ContentValues values = new ContentValues();
        values.put("vet_id", vet.getId());
        values.put("pet_id", pet.getId());
        values.put("date_id", date.getId());
        values.put("location_id", loc.getId());
        values.put("procedure_id", pro.getId());
        values.put("cost", cost);
        values.put("synoposis", synoposis);

        long id = insertOne("appointments", values);
        Appointment appointment = new Appointment((int) id, vet, pet, date, loc, pro, cost, synoposis);
        return appointment;
    }


    /* Contact */

    //TODO 存储contact时，需要在contact_phone_numbers、contact_locations、contact_email_addresses存储ID
    public Contact insertContact(String name, PhoneNumber number, EmailAddress address, Location location) throws DatabaseInsertException {
        ContentValues values = new ContentValues();
        values.put("name", name);
        long contactId = insertOne("contacts", values);
        long contactEmailAddressId = insertContactEmail((int) contactId, address.getId());
        long contactPhoneNumberId = insertContactPhoneNumber((int) contactId, number.getId());
        long contactLocationId = insertContactLocation((int) contactId, location.getId());
        Contact contact = new Contact((int) contactId, name, number, address, location);
        return contact;
    }

    public Contact getContactByMap(Map<Integer, Contact> contactMap, int id) {
        if (contactMap == null || contactMap.isEmpty()) {
            return null;
        }
        Contact contact = contactMap.get(id);
        return contact;
    }

    /**
     * 查询出所有的Contact,组成Map
     *
     * @return
     */
    public Map<Integer, Contact> queryAllContact() {
        String query = "  select * from contacts left join (select contact_phone_numbers.contact_id,contact_phone_numbers.phone_number_id,phone_numbers.type,phone_numbers.phone_number from contact_phone_numbers,phone_numbers where contact_phone_numbers.phone_number_id=phone_numbers.id ) tab1 on contacts.id=tab1.contact_id left join (select contact_email_addresses.contact_id,contact_email_addresses.email_address_id,email_addresses.email_address from contact_email_addresses,email_addresses where contact_email_addresses.email_address_id= email_addresses.id) tab2 on contacts.id=tab2.contact_id  left join (select contact_locations.contact_id,contact_locations.location_id,locations.* from contact_locations,locations where contact_locations.location_id=locations.id ) tab3 on contacts.id= tab3.contact_id";
        Cursor cursor = doRawQuery(query, null);
        Map<Integer, Contact> contactMap = new HashMap<>();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                int idIn = cursor.getColumnIndexOrThrow("id");
                int nameId = cursor.getColumnIndexOrThrow("name");
                int ph = cursor.getColumnIndexOrThrow("phone_number");
                int ty = cursor.getColumnIndex("type");
                int phId = cursor.getColumnIndexOrThrow("phone_number_id");

                int email = cursor.getColumnIndexOrThrow("email_address");
                int emId = cursor.getColumnIndexOrThrow("email_address_id");

                int loId = cursor.getColumnIndexOrThrow("location_id");
                int address = cursor.getColumnIndexOrThrow("address");
                int unit = cursor.getColumnIndexOrThrow("unit");
                int city = cursor.getColumnIndexOrThrow("city");
                int state = cursor.getColumnIndexOrThrow("state");
                int zip = cursor.getColumnIndexOrThrow("zip");

                int ty1 = cursor.getInt(ty);

                PhoneNumber phoneNumber = new PhoneNumber(cursor.getInt(phId), 1, cursor.getInt(ph));
                EmailAddress emailAddress = new EmailAddress(cursor.getInt(emId), 1, cursor.getString(email));
                Location location = new Location(cursor.getInt(loId), cursor.getString(address), cursor.getString(unit), cursor.getString(city), cursor.getString(state), cursor.getString(zip));

                List<PhoneNumber> phoneNumberList = new ArrayList<>();
                phoneNumberList.add(phoneNumber);
                List<EmailAddress> emailAddressList = new ArrayList<>();
                emailAddressList.add(emailAddress);
                List<Location> locationList = new ArrayList<>();
                locationList.add(location);

                Contact contact = new Contact(cursor.getInt(idIn), cursor.getString(nameId), phoneNumberList, emailAddressList, locationList);

                //Contact contact =new Contact(cursor.getInt(idIn),cursor.getString(nameId),phoneNumber,emailAddress,location);
                contactMap.put(cursor.getInt(idIn), contact);
            } while (cursor.moveToNext());
        }
        return contactMap;
    }


    public Contact getContact(int id) {
        Cursor cursor = selectOne("contacts", id);
        String name;
        new ArrayList<>();
        new ArrayList<>();

        if (cursor != null) {
            cursor.moveToFirst();
            name = cursor.getString(1);


            ArrayList<PhoneNumber> phoneNumbers = new ArrayList<>();
            //phoneNumbers = getContactInformation(PhoneNumber.class, phoneNumbers, "phone_number_id", id, "phone_numbers");
            phoneNumbers = getPhoneNumberList(PhoneNumber.class, phoneNumbers, "phone_number_id", id, "phone_numbers");

            ArrayList<EmailAddress> emailAddresses = new ArrayList<>();
            //emailAddresses = getContactInformation(EmailAddress.class, emailAddresses, "email_address_id", id, "email_addresses");
            emailAddresses = getEmailAddressList(EmailAddress.class, emailAddresses, "email_address_id", id, "email_addresses");

            ArrayList<Location> locations = new ArrayList<>();
            locations = getLocationList(Location.class, locations, "location_id", id, "locations");
            //locations = getContactInformation(Location.class, locations, "location_id", id, "locations");

            return new Contact(id, name, phoneNumbers, emailAddresses, locations);
        } else {
            return null;
        }

    }

    /**
     * 获取phoneNumber的list
     *
     * @param clz
     * @param list
     * @param field
     * @param id
     * @param ftable
     * @return
     */
    private ArrayList<PhoneNumber> getPhoneNumberList(Class clz, ArrayList<PhoneNumber> list, String field, int id, String ftable) {
        Cursor cursor = getContactCursor(field, id, ftable);
        int ph = cursor.getColumnIndexOrThrow("phone_number");
        int ty = cursor.getColumnIndex("type");
        int idIn = cursor.getColumnIndexOrThrow("id");
        cursor.moveToFirst();

        //PhoneNumber(int id, int type, int number)
        PhoneNumber phoneNumber = new PhoneNumber(cursor.getInt(idIn), cursor.getInt(ty), cursor.getInt(ph));
        list.add(phoneNumber);
        return list;
    }

    /**
     * 获取emailAddress的List
     *
     * @param clz
     * @param list
     * @param field
     * @param id
     * @param ftable
     * @return
     */
    private ArrayList<EmailAddress> getEmailAddressList(Class clz, ArrayList<EmailAddress> list, String field, int id, String ftable) {
        Cursor cursor = getContactCursor(field, id, ftable);

        int email = cursor.getColumnIndexOrThrow("email_address");
        int ty = cursor.getColumnIndex("type");
        int idIn = cursor.getColumnIndexOrThrow("email_address_id");

        cursor.moveToFirst();
        EmailAddress emailAddress = new EmailAddress(cursor.getInt(idIn), cursor.getInt(ty), cursor.getString(email));
        list.add(emailAddress);
        return list;
    }

    /**
     * 获取location的List
     *
     * @param clz
     * @param list
     * @param field
     * @param id
     * @param ftable
     * @return
     */
    private ArrayList<Location> getLocationList(Class clz, ArrayList<Location> list, String field, int id, String ftable) {
        Cursor cursor = getContactCursor(field, id, ftable);
        if (cursor.getCount() == 0) {
            Location location = new Location(0, "", "", "", "", "");
            list.add(location);
            return list;
        }
        int idIn = cursor.getColumnIndexOrThrow("location_id");
        int address = cursor.getColumnIndexOrThrow("address");
        int unit = cursor.getColumnIndexOrThrow("unit");
        int city = cursor.getColumnIndexOrThrow("city");
        int state = cursor.getColumnIndexOrThrow("state");
        int zip = cursor.getColumnIndexOrThrow("zip");

        //unit, String city, String state, String zip
        cursor.moveToFirst();
        Location location = new Location(cursor.getInt(idIn), cursor.getString(address), cursor.getString(unit), cursor.getString(city), cursor.getString(state), cursor.getString(zip));
        list.add(location);

        return list;
    }

    /**
     * 查询出某类型的Cursor
     *
     * @param field
     * @param id
     * @param ftable
     * @return
     */
    private Cursor getContactCursor(String field, int id, String ftable) {
        String relationship_table = "contact_" + ftable;
        String query = String.format("SELECT * FROM %s tbl INNER JOIN %s con ON con.%s = tbl.id INNER JOIN contacts ON con.contact_id = contacts.id WHERE contacts.id = ?", ftable, relationship_table, field);
        String[] args = {Integer.toString(id)};
        Cursor cursor = doRawQuery(query, args);
        return cursor;
    }

    private <E extends Model> ArrayList<E> parseContactQuery(Class clz, ArrayList<E> list, Cursor cursor) {

        return null;
    }

    private <E extends Model> ArrayList<E> getContactInformation(Class clz, ArrayList<E> list, String field, int id, String ftable) {
        /*try {
            Field tableField = clz.getField("TABLE");
            //TODO
            String table = (String) tableField.get(null);

            String relationship_table = "contact_" + table;
            String query = String.format("SELECT * FROM %s tbl INNER JOIN %s con ON con.%s = tbl.id INNER JOIN contacts ON con.contact_id = contacts.id WHERE contacts.id = ?", table, relationship_table, field);
            String[] args = {Integer.toString(id)};
            Cursor cursor = doRawQuery(query, args);

            //TODO
            return parseQuery(clz, list, cursor);

        } catch (NoSuchFieldException ex) {
            Log.e("No Such Field", ex.getMessage());
        } catch (IllegalAccessException ex) {
            Log.e("Illegal Access Exception", ex.getMessage());
        }
        return null;*/


        String relationship_table = "contact_" + ftable;
        String query = String.format("SELECT * FROM %s tbl INNER JOIN %s con ON con.%s = tbl.id INNER JOIN contacts ON con.contact_id = contacts.id WHERE contacts.id = ?", ftable, relationship_table, field);
        String[] args = {Integer.toString(id)};
        Cursor cursor = doRawQuery(query, args);


        //TODO
        return parseQuery(clz, list, cursor);
    }

    /**
     * 插入数据到contact_email_address
     *
     * @param contactId
     * @param emailAddressId
     * @return
     * @throws DatabaseInsertException
     */
    public long insertContactEmail(int contactId, int emailAddressId) throws DatabaseInsertException {
        ContentValues values = new ContentValues();
        values.put("contact_id", contactId);
        values.put("email_address_id", emailAddressId);

        long id;
        String table = "contact_email_addresses";
        Cursor cursor = selectWhere(table, "contact_id", contactId);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getInt(0);
            updateOne(table, values, id);
        } else {
            id = insertOne(table, values);
        }
        return id;

    }

    /**
     * 插入到contact_phone_numbers
     *
     * @param contactId
     * @param phoneNumberId
     * @return
     * @throws DatabaseInsertException
     */
    public long insertContactPhoneNumber(int contactId, int phoneNumberId) throws DatabaseInsertException {
        ContentValues values = new ContentValues();
        values.put("contact_id", contactId);
        values.put("phone_number_id", phoneNumberId);

        long id;
        String table = "contact_phone_numbers";
        Cursor cursor = selectWhere(table, "contact_id", contactId);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getInt(0);
            updateOne(table, values, id);
        } else {
            id = insertOne(table, values);
        }
        return id;
    }

    /**
     * 插入到contact_locations
     *
     * @param contactId
     * @param locationId
     * @return
     * @throws DatabaseInsertException
     */
    public long insertContactLocation(int contactId, int locationId) throws DatabaseInsertException {
        ContentValues values = new ContentValues();
        values.put("contact_id", contactId);
        values.put("location_id", locationId);

        long id;
        String table = "contact_locations";
        Cursor cursor = selectWhere(table, "contact_id", contactId);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getInt(0);
            updateOne(table, values, id);
        } else {
            id = insertOne(table, values);
        }
        return id;

    }


    public class DatabaseInsertException extends Exception {
        public DatabaseInsertException(String message) {
            super(message);
        }
    }

    public static abstract class Column implements BaseColumns {
        public static String TABLE;
        public static String[][] COLUMNS;

        public static void onCreate() {
        }

        public static void onUpgrade() {
        }

        public static String[] getColumnProjection() {
            String[] projection = new String[COLUMNS.length];
            for (int i = 0; i < COLUMNS.length; i++) {
                projection[i] = COLUMNS[i][0];
            }
            return projection;
        }


    }

    public static class Pets extends Column {
        public static final String TABLE = "pets";

        public static final String[][] COLUMNS = {
                {"name", "TEXT", "UNIQUE"},
                {"breed", "TEXT"},
                {"species", "TEXT"},
                {"birthdate", "INTEGER"},           //date_id type
                {"adoption_date", "INTEGER"},        //data_id type
                {"resource_uri", "TEXT"}
        };
    }

    public static class EventDates extends Column {
        public static final String TABLE = "event_dates";
        public static final String[][] COLUMNS = {
                {"date", "INTEGER"},
                {"month", "INTEGER"},
                {"year", "INTEGER"},
                {"rrule", "STRING"},
                {"all_day_event", "INTEGER"},
                {"dtstart", "INTEGER"},
                {"dtend", "INTEGER"},
        };
    }

    public static abstract class Vets implements BaseColumns {
        public static final String TABLE = "vets";
        public static final String[][] COLUMNS = {
                {"name", "TEXT"},
                {"contact_id", "INTEGER"}
        };

        public static void onCreate() {
        }

        public static void onUpgrade() {
        }
    }


    public static abstract class Hours implements BaseColumns {
        public static final String TABLE = "hours";
        public static final String[][] COLUMNS = {
                {"open", "REAL"},
                {"closed", "REAL"},
                {"day_of_week", "TEXT"},
                {"location_id", "INTEGER"}
        };

        public static void onCreate() {
        }

        public static void onUpgrade() {
        }
    }

    public static abstract class Contacts implements BaseColumns {
        public static final String TABLE = "contacts";
        public static final String[][] COLUMNS = {
                {"name", "TEXT"}
        };

        public static void onCreate() {
        }

        public static void onUpgrade() {
        }
    }

    public static abstract class ContactPhoneNumbers implements BaseColumns {
        public static final String TABLE = "contact_phone_numbers";
        public static final String[][] COLUMNS = {
                {"contact_id", "INTEGER"},
                {"phone_number_id", "INTEGER"},
        };

        public static void onCreate() {
        }

        public static void onUpgrade() {
        }
    }

    public static abstract class ContactEmailAddresses implements BaseColumns {
        public static final String TABLE = "contact_email_addresses";
        public static final String[][] COLUMNS = {
                {"contact_id", "INTEGER"},
                {"email_address_id", "INTEGER"},
        };

        public static void onCreate() {
        }

        public static void onUpgrade() {
        }
    }

    public static abstract class ContactLocations implements BaseColumns {
        public static final String TABLE = "contact_locations";
        public static final String[][] COLUMNS = {
                {"contact_id", "INTEGER"},
                {"location_id", "INTEGER"},
        };

        public static void onCreate() {
        }

        public static void onUpgrade() {
        }
    }

    public static abstract class Appointments implements BaseColumns {
        public static final String TABLE = "appointments";
        public static final String[][] COLUMNS = {
                {"vet_id", "INTEGER"},
                {"pet_id", "INTEGER"},
                {"date_id", "INTEGER"},
                {"location_id", "INTEGER"},
                {"procedure_id", "INTEGER"},
                {"cost", "REAL"},
                {"synoposis", "STRING"}
        };

        public static void onCreate() {
        }

        public static void onUpgrade() {
        }
    }

    public static abstract class Procedures implements BaseColumns {
        public static final String TABLE = "procedures";
        public static final String[][] COLUMNS = {
                {"name", "TEXT", "UNIQUE"},
                {"description", "TEXT"}
        };

        public static void onCreate() {
        }

        public static void onUpgrade() {
        }
    }


    public static abstract class Reminders implements BaseColumns {
        public static final String TABLE = "reminders";
        public static final String[][] COLUMNS = {
                {"title", "TEXT"},
                {"description", "TEXT"},
                {"date_id", "INTEGER"},
                {"priority", "INTEGER"},
                {"pet_id", "INTEGER"}
        };

        public static void onCreate() {
        }

        public static void onUpgrade() {
        }
    }

    public static abstract class PhoneNumbers implements BaseColumns {
        public static final String TABLE = "phone_numbers";
        public static final String[][] COLUMNS = {
                {"type", "INTEGER"},
                {"phone_number", "INTEGER"},
                {"contact_id", "INTEGER"}
        };

        public static void onCreate() {
        }

        public static void onUpgrade() {
        }
    }

    public static abstract class EmailAddresses implements BaseColumns {
        public static final String TABLE = "email_addresses";
        public static final String[][] COLUMNS = {
                {"type", "INTEGER"},
                {"email_address", "INTEGER"},
                {"contact_id", "INTEGER"}
        };

        public static void onCreate() {
        }

        public static void onUpgrade() {
        }
    }


    public static abstract class Immunizations implements BaseColumns {
        public static final String TABLE = "immunizations";
        public static final String[][] COLUMNS = {
                {"name", "TEXT"},
                {"description", "TEXT"}
        };

        public static void onCreate() {
        }

        public static void onUpgrade() {
        }
    }

    public static abstract class Bills implements BaseColumns {
        public static final String TABLE = "bills";
        public static final String[][] COLUMNS = {
                {"total_cost", "REAL"},
                {"vetrinary_clinic_id", "INTEGER"},
                {"vet_id", "INTEGER"},
                {"date_id", "INTEGER"},
                {"resource_id", "INTEGER"}
        };

        public static void onCreate() {
        }

        public static void onUpgrade() {
        }
    }

    public static abstract class BillItems implements BaseColumns {
        public static final String TABLE = "bill_items";
        public static final String[][] COLUMNS = {
                {"bill_id", "INTEGER"},
                {"item", "TEXT"},
                {"itemized_description", "TEXT"},
                {"cost", "REAL"},
                {"quantity", "INTEGER"}
        };

        public static void onCreate() {
        }

        public static void onUpgrade() {
        }
    }

    public static abstract class ImmunizationRecords implements BaseColumns {
        public static final String TABLE = "immunization_records";
        public static final String[][] COLUMNS = {
                {"vet_id", "INTEGER"},
                {"pet_id", "INTEGER"},
                {"date_id", "INTEGER"},
                {"location_id", "INTEGER"},
                {"immunization_id", "INTEGER"},
                {"resource_id", "INTEGER"}
        };

        public static void onCreate() {
        }

        public static void onUpgrade() {
        }
    }

    public static abstract class PetNotes implements BaseColumns {
        public static final String TABLE = "pet_notes";
        public static final String[][] COLUMNS = {
                {"pet_id", "INTEGER"},
                {"note", "TEXT"},
                {"date_id", "INTEGER"}
        };

        public static void onCreate() {
        }

        public static void onUpgrade() {
        }
    }

    public static abstract class Organizations implements BaseColumns {
        public static final String TABLE = "organizations";
        public static final String[][] COLUMNS = {
                {"organization", "TEXT"},
                {"location_id", "INTEGER"},
                {"contact_id", "INTEGER"}
        };

        public static void onCreate() {
        }

        public static void onUpgrade() {
        }
    }


    public static abstract class Locations implements BaseColumns {
        public static final String TABLE = "locations";
        public static final String[][] COLUMNS = {
                {"address", "TEXT"},
                {"unit", "TEXT"},
                {"city", "TEXT"},
                {"state", "TEXT"},
                {"zip", "TEXT"},
                {"type", "INTEGER"}
        };

        public static void onCreate() {
        }

        public static void onUpgrade() {
        }
    }

    public static abstract class Adoptions implements BaseColumns {
        public static final String TABLE = "adoptions";
        public static final String[][] COLUMNS = {
                {"pet_id", "INTEGER"},
                {"location_id", "INTEGER"},
                {"organization_id", "INTEGER"},
                {"date_id", "INTEGER"}
        };

        public static void onCreate() {
        }

        public static void onUpgrade() {
        }
    }

    public static abstract class VetNotes implements BaseColumns {
        public static final String TABLE = "vet_notes";
        //reference id can refer to "procedure", "immunization", or "appointment" and reference id references that class
        public static final String[][] COLUMNS = {
                {"note", "TEXT"},
                {"vet_id", "INTEGER"},
                {"reference_type", "TEXT"},
                {"reference_id", "INTEGER"}
        };

        public static void onCreate() {
        }

        public static void onUpgrade() {
        }
    }

    public static abstract class PetProcedures implements BaseColumns {
        public static final String TABLE = "pet_procedures";
        public static final String[][] COLUMNS = {
                {"pet_id", "INTEGER"},
                {"vet_id", "INTEGER"},
                {"date_id", "INTEGER"},
                {"procedure_id", "INTEGER"},
                {"bill_id", "INTEGER"}
        };

        public static void onCreate() {
        }

        public static void onUpgrade() {
        }
    }

    public static abstract class UserOptions implements BaseColumns {
        public static final String TABLE = "user_options";
        public static final String[][] COLUMNS = {
                {"option_name", "TEXT", "UNIQUE"},
                {"option_value", "TEXT"}
        };

        public static void onCreate() {
            //row: allow_pop_up_notifications

        }

        public static void onUpgrade() {
        }
    }

}
