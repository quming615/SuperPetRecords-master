package kbc.superpetrecords;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;

import android.app.*;
import android.content.*;
import android.database.sqlite.*;
import android.graphics.*;
import android.os.*;
import android.provider.*;
import android.util.*;
import android.view.*;
import android.support.v4.widget.DrawerLayout;
import android.widget.*;


import com.baidu.mapapi.SDKInitializer;

import kbc.superpetrecords.dialogfragments.SpinnerDialog;
import kbc.superpetrecords.fragments.*;

import java.util.*;

import kbc.superpetrecords.models.*;

public class MainActivity extends Activity implements SpinnerDialog.OptionListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    private NavigationDrawerFragment drawerFragment;
    private FrameLayout mainFrame, sideFrame;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    public static final int PORTRAIT = 1;
    public static final int LANDSCAPE = 2;

    public static final int MAIN_CONTAINER = R.id.main_fragment_container;
    public static final int SIDE_CONTAINER = R.id.side_fragment_container;


    private CharSequence mTitle;
    private PetContract dbHelper;

    int gesture_threshold_dp;
    float density;
    int displayMode;
    Point displaySize;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        Log.d("MainActivity", "OnCreate");
        gesture_threshold_dp = ViewConfiguration.get(this).getScaledTouchSlop();
        density = getResources().getDisplayMetrics().density;
        displaySize = getDisplaySize();

        setContentView(R.layout.activity_main);

        FragmentManager fm = getFragmentManager();
        drawerFragment = (NavigationDrawerFragment) fm.findFragmentById(R.id.navigation_drawer);
        drawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        mTitle = getTitle();


        dbHelper = new PetContract(this);

        //new SetUpGoogleCalendarAPITask().execute();
        //new BuildDatabaseTask().execute();
        if (displaySize.x > displaySize.y) {
            displayMode = LANDSCAPE;
            Log.d("MainActivity", "In LANDSCAPE Mode: x(" + displaySize.x + "), y(" + displaySize.y + ")");
        } else {
            displayMode = PORTRAIT;
            Log.d("MainActivity", "In PORTRAIT Mode: x(" + displaySize.x + "), y(" + displaySize.y + ")");
        }
        Log.d("OnCreate", "displayMode:" + displayMode);
        getCalendar(null);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


    public int getPixels(int dp) {
        return (int) dp * (int) (density / 160);
    }

    public SQLiteDatabase getDatabase() {
        return dbHelper.getWritableDatabase();
    }

    public PetContract getDatabaseHelper() {
        return dbHelper;
    }

    /* PETS */

    public void updatePet(Pet pet) {
        attachFragment(PetDetailsFragment.newInstance(pet), "Clicked: AddNewPets");
    }

    public void addNewPet(MenuItem item) {
        attachFragment(PetDetailsFragment.newInstance(null), "Clicked: AddNewPets");
    }

    public void updatePets(MenuItem item) {
        Toast.makeText(this, "updatePets", Toast.LENGTH_LONG).show();
        attachFragment(PetListFragment.newInstance(dbHelper.getPets(), R.layout.model_list), "updatePets");
    }

    /* VETS */

    public void updateVet(Vet vet) {
        attachFragment(VetDetailsFragment.newInstance(vet), "Clicked: AddNewVets");
    }

    public void addNewVet(MenuItem item) {
        attachFragment(VetDetailsFragment.newInstance(new Vet()), "Clicked: AddNewVets");
    }

    public void updateVets(MenuItem item) {
        attachFragment(VetListFragment.newInstance(dbHelper.getVetss(), R.layout.model_list), "updateVets");
    }

    public void searchNearbyVet(MenuItem item){
        attachFragmentToContainer (VetNearbyFragment.newInstance(),MainActivity.MAIN_CONTAINER,"查找附近宠物医院");
        //attachFragmentToContainer(SearchVetFragment.newInstance(getApplicationContext()),MainActivity.MAIN_CONTAINER,"xxxx");
    }



    /* CALENDAR */

    private void loadCalendar(long calId, int containerId) {
        AppointmentCalendar calendar = new AppointmentCalendar(calId);

        HashMap<String, Integer> layout_ids = new HashMap<>();
        layout_ids.put("month_text", R.id.appointment_calendar_month_text);
        layout_ids.put("year_text", R.id.appointment_calendar_year_text);
        layout_ids.put("days_of_month_grid", R.id.appointment_calendar_days_of_month);
        AppointmentCalendarFragment fragment = AppointmentCalendarFragment.newInstance(calendar, R.layout.appointment_calendar_layout, R.layout.appointment_calendar_date, layout_ids);
        attachFragmentToContainer(fragment, containerId, "getCalendar");
    }

    public void addNewAppointment(MenuItem item) {
        addNewAppointment();
    }

    public void updateAppointments(MenuItem item) {
        updateAppointments(MAIN_CONTAINER);
    }

    public void addNewAppointment() {
        Calendar cal = Calendar.getInstance();
        EventDate date = new EventDate(0, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR), "");
        AppointmentDetailsFragment frag = AppointmentDetailsFragment.newInstance(date);
        attachFragmentToContainer(frag, MainActivity.MAIN_CONTAINER, "new_appointment");
    }

    public void updateAppointments(int containerId) {
        AppointmentListFragment fragment = AppointmentListFragment.newInstance(dbHelper.getAppointmentsList());
        attachFragmentToContainer(fragment, containerId, "Clicked: getCalendar");
    }

    public void getCalendar(MenuItem item) {
        if (displayMode == LANDSCAPE) {
            getCalendarById(R.id.main_fragment_container);
            updateAppointments(R.id.side_fragment_container);
        } else {
            displayMode = PORTRAIT;
            getCalendarById(R.id.main_fragment_container);
        }
    }

    public void getCalendarById(int containerId) {
        String calIdString = dbHelper.getUserOption("calId");
        if (calIdString != null) {
            Toast.makeText(this, "Calendar Assigned to Account:" + calIdString, Toast.LENGTH_LONG).show();
            int calId = Integer.parseInt(calIdString);
            loadCalendar(calId, containerId);
        } else {

            AppointmentCalendar calendar = new AppointmentCalendar();
            Log.d("Calendar", "Currently no calendar has been assigned to account");

            final String[] CALENDARS_PROJECTION = new String[]{
                    CalendarContract.Calendars._ID,
                    CalendarContract.Calendars.OWNER_ACCOUNT,
                    CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            };

            int[] layouts = {
                    R.id.calendars_owner_account,
                    R.id.calendars_calendar_display_name,
            };

            String tag = "calendar_dialog";

            //TODO 初始化界面的选择日期
            final SpinnerDialog dialog = SpinnerDialog.newInstance(
                    tag,
                    R.layout.option_dialog,
                    R.id.option_spinner,
                    R.id.option_header_text,
                    R.layout.calendar_records_spinner,
                    layouts,
                    getResources().getString(R.string.no_calendar_message),
                    CalendarContract.Calendars.CONTENT_URI,
                    CALENDARS_PROJECTION
            );
            attachDialogFragment(dialog, tag);
        }
    }

    public void addNewReminder(MenuItem item) {
        addNewAppointment();
    }

    public void updateReminders(MenuItem item) {
        updateAppointments(MAIN_CONTAINER);
    }


    public void attachDialogFragment(DialogFragment fragment, String tag) {
        //Log.d("MainActivity::attachDialogFragment", "Attaching Dialog");

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction trans = fragmentManager.beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(tag);

        if (prev != null) {
            trans.remove(prev);
        }

        fragment.show(trans, tag);
        drawerFragment.close();
    }

    public void attachFragment(Fragment fragment, String tag) {
        attachFragmentToContainer(fragment, R.id.main_fragment_container, tag);
    }

    /**
     * 两个fragment相互切换
     *
     * @param in
     * @param out
     */
    public void swapFragments(Fragment in, Fragment out) {
        attachFragmentToContainer(in, R.id.main_fragment_container, "切换Fragment");

    }

    public void replaceFragment(Fragment fragment, int container_id, String tag) {
        FragmentManager fragmentManager = getFragmentManager();
        final FragmentTransaction trans = fragmentManager.beginTransaction();
        trans.replace(container_id, fragment);
        trans.addToBackStack(null);
        trans.commit();
        drawerFragment.close();

        /*
        View view = findViewById(container_id);
        view.animate().setDuration(2000).alpha(0).withEndAction(new Runnable() {
            @Override public void run() {
            }
        });
        */

    }

    public void attachFragmentToContainer(Fragment fragment, int container_id, String tag) {
        FragmentManager fragmentManager = getFragmentManager();
        final FragmentTransaction trans = fragmentManager.beginTransaction();
        trans.replace(container_id, fragment);
        trans.addToBackStack(null);
        trans.commit();
        drawerFragment.close();
    }

    public void removeDialogFragment(String tag) {
        FragmentManager manager = getFragmentManager();
        final Fragment frag = manager.findFragmentByTag(tag);
        final FragmentTransaction trans = manager.beginTransaction();

        View view = frag.getView();
        float alpha = view.getAlpha();
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", alpha, 0).setDuration(100);
        Log.d("removeDialogFragment", "before Object Animator Start");
        animator.addListener(new Animator.AnimatorListener() {
                                 @Override
                                 public void onAnimationStart(Animator animation) {
                                     Log.d("removeDialogFragment", "animation has started");
                                 }

                                 @Override
                                 public void onAnimationEnd(Animator animation) {
                                     Log.d("removeDialogFragment", "animation has ended");
                                     trans.remove(frag);
                                     trans.commit();
                                 }

                                 @Override
                                 public void onAnimationCancel(Animator animation) {
                                     Log.d("removeDialogFragment", "animation has been canceled");
                                 }

                                 @Override
                                 public void onAnimationRepeat(Animator animation) {
                                     Log.d("removeDialogFragment", "animation is repeating");
                                 }
                             }
        );
        animator.start();

        /*
        view.animate().setDuration(100).alpha(0).withEndAction(new Runnable() {
            @Override public void run() {
                trans.remove(frag);
                trans.commit();
            }
        });
        */
    }


    public void onSectionAttached(int number) {
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!drawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void addNewOption(View view) {

        //AppointmentCalendar calendar = AppointmentCalendar.createCalendar();
    }

    @Override
    public void selectOption(View view, String tag, long id) {
        SQLiteDatabase db = getDatabase();
        ContentValues values = new ContentValues();
        String table = "user_options";

        if (tag == "calendar_dialog") {
            values.put("option_value", id);
            values.put("option_name", "calId");
            long row_id = db.insert(table, null, values);
            Toast.makeText(this, tag + " results for " + id + ": " + row_id, Toast.LENGTH_LONG).show();
            if (row_id >= 0) {
                removeDialogFragment(tag);
                if (displayMode == LANDSCAPE) {
                    loadCalendar(row_id, R.id.main_fragment_container);
                    updateAppointments(R.id.side_fragment_container);
                } else {
                    loadCalendar(row_id, R.id.main_fragment_container);
                }
            } else {
                //TODO: Handle Error Scenario

            }
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult:Main", "RequestCode(" + requestCode + "), resultCode(" + resultCode + ")");
    }

    public Point getDisplaySize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public int getOrientation() {
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        return display.getRotation();
    }

    /* Listeners */
    /*

    private class SetUpGoogleCalendarAPITask extends AsyncTask<Void, Integer, Void> {
        protected Void doInBackground(Void ... param) {
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute() {
            Toast.makeText(MainActivity.this, "Database Built", Toast.LENGTH_SHORT);
        }
    }
    private class BuildDatabaseTask extends AsyncTask<Void, Integer, Void> {
        protected Void doInBackground(Void ... param) {
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute() {
            Toast.makeText(MainActivity.this, "Database Built", Toast.LENGTH_SHORT);
        }
    }

    public void setUp() throws IOException {
        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();

        // The clientId and clientSecret can be found in Google Developers Console
        String clientId = "729621114132-ht6s7mm8fqp8bn1nekp7gur2l74tqk12.apps.googleusercontent.com";
        String clientSecret = "YOUR_CLIENT_SECRET";

        // Or your redirect URL for web based applications.
        String redirectUrl = "urn:ietf:wg:oauth:2.0:oob";
        String scope = "https://www.googleapis.com/auth/calendar";

        // Step 1: Authorize -->
        String authorizationUrl = new GoogleAuthorizationRequestUrl(clientId, redirectUrl, scope)
            .build();

        // Point or redirect your user to the authorizationUrl.
        System.out.println("Go to the following link in your browser:");
        System.out.println(authorizationUrl);

        // Read the authorization code from the standard input stream.
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("What is the authorization code?");
        String code = in.readLine();
        // End of Step 1 <--

        // Step 2: Exchange -->
        AccessTokenResponse response = new GoogleAuthorizationCodeGrant(httpTransport, jsonFactory,
            clientId, clientSecret, code, redirectUrl).execute();
        // End of Step 2 <--

        GoogleAccessProtectedResource accessProtectedResource = new GoogleAccessProtectedResource(
            response.accessToken, httpTransport, jsonFactory, clientId, clientSecret,
            response.refreshToken);

        Calendar service = new Calendar(httpTransport, accessProtectedResource, jsonFactory);
        service.setApplicationName("YOUR_APPLICATION_NAME");
    }
    */

}
