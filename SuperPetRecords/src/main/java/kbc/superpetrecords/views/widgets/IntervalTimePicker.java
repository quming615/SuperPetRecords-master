package kbc.superpetrecords.views.widgets;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.*;

import java.lang.reflect.Field;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import kbc.superpetrecords.R;

/**
 * Created by kellanbc on 8/23/14.
 */
public class IntervalTimePicker extends FrameLayout {

    private int mMinuteInterval, mHourInterval;
    private static final boolean DEFAULT_ENABLED_STATE = true;
    private static final int HOURS_IN_HALF_DAY = 12;

    public int mDigitColor;

    public static final int AM = 1;
    public static final int PM = 2;

    private static final OnTimeChangedListener NO_OP_CHANGE_LISTENER = new OnTimeChangedListener() {
        public void onTimeChanged(IntervalTimePicker view, int hourOfDay, int minute) {
        }
    };

    // state
    private boolean mIs24HourView;
    private int mMeridiem;

    // ui components
    private final NumberPicker mHourSpinner;
    private final NumberPicker mMinuteSpinner;
    private final NumberPicker mAmPmSpinner;

    private final String[] mAmPmStrings;

    private boolean mIsEnabled = DEFAULT_ENABLED_STATE;

    // callbacks
    private OnTimeChangedListener mOnTimeChangedListener;

    private Calendar mTempCalendar;

    private Locale mCurrentLocale;

    private Context mContext;
    private char mHourFormat;

    /**
     * The callback interface used to indicate the time has been adjusted.
     */
    public interface OnTimeChangedListener {

        /**
         * @param view The view associated with this listener.
         * @param hourOfDay The current hour.
         * @param minute The current minute.
         */
        void onTimeChanged(IntervalTimePicker view, int hourOfDay, int minute);
    }

    public IntervalTimePicker(Context context) {
        this(context, null);
    }

    public IntervalTimePicker(Context context, AttributeSet attrs) {
        this(context, attrs, R.layout.interval_time_picker);
    }

    public IntervalTimePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IntervalTimePicker);
        setCurrentLocale(Locale.getDefault());

        int layoutResourceId = a.getResourceId(R.styleable.IntervalTimePicker_internalLayout, R.layout.interval_time_picker);
        int minuteInterval = a.getInt(R.styleable.IntervalTimePicker_minuteInterval, 15);
        int hourInterval = a.getInt(R.styleable.IntervalTimePicker_hourInterval, 1);
        mDigitColor = a.getColor(R.styleable.IntervalTimePicker_digitColor, R.color.black);
        mIs24HourView = a.getBoolean(R.styleable.IntervalTimePicker_is24HourView, false);
        int currentHour = a.getInt(R.styleable.IntervalTimePicker_currentHour, mTempCalendar.get(Calendar.HOUR_OF_DAY));
        int currentMinute = a.getInt(R.styleable.IntervalTimePicker_currentMinute, mTempCalendar.get(Calendar.MINUTE));
        int currentMeridiem = a.getInt(R.styleable.IntervalTimePicker_currentMeridiem, (currentHour % 12) + 1);

        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(layoutResourceId, this, true);

        // hour
        mHourSpinner = (NumberPicker) findViewById(R.id.hour);
        mHourSpinner.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mHourSpinner.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            public void onValueChange(NumberPicker spinner, int oldVal, int newVal) {
                if (!is24HourView()) {
                    if ((oldVal == HOURS_IN_HALF_DAY - 1 && newVal == HOURS_IN_HALF_DAY)
                     || (oldVal == HOURS_IN_HALF_DAY && newVal == HOURS_IN_HALF_DAY - 1)) {
                        mMeridiem = (mMeridiem == AM) ? PM : AM;
                        updateAmPmControl();
                    }
                }
                onTimeChanged();
            }
        });

        mMinuteSpinner = (NumberPicker) findViewById(R.id.minute);
        mMinuteSpinner.setOnLongPressUpdateInterval(100);
        mMinuteSpinner.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mMinuteSpinner.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker spinner, int oldVal, int newVal) {
                int minValue = mMinuteSpinner.getMinValue();
                int maxValue = mMinuteSpinner.getMaxValue();
                if (oldVal == maxValue && newVal == minValue) {
                    int newHour = mHourSpinner.getValue() + 1;
                    if (!is24HourView() && newHour == HOURS_IN_HALF_DAY) {
                        mMeridiem = PM;
                        updateAmPmControl();
                    }
                    mHourSpinner.setValue(newHour);
                } else if (oldVal == minValue && newVal == maxValue) {
                    int newHour = mHourSpinner.getValue() - 1;
                    if (!is24HourView() && newHour == HOURS_IN_HALF_DAY - 1) {
                        mMeridiem = (mMeridiem == AM) ? PM : AM;
                        updateAmPmControl();
                    }
                    mHourSpinner.setValue(newHour);
                }
                onTimeChanged();
            }
        });

        setMinuteInterval(minuteInterval);
        setHourInterval(hourInterval);

        /* Get the localized am/pm strings and use them in the spinner */
        mAmPmStrings = new DateFormatSymbols().getAmPmStrings();

        mAmPmSpinner = (NumberPicker) findViewById(R.id.meridiem);

        //diable keyboards
        mAmPmSpinner.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        mAmPmSpinner.setMinValue(0);
        mAmPmSpinner.setMaxValue(1);

        mAmPmSpinner.setDisplayedValues(mAmPmStrings);

        mAmPmSpinner.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                picker.requestFocus();
                mMeridiem = (mMeridiem == AM) ? PM : AM;
                updateAmPmControl();
                onTimeChanged();
            }
        });

        // update controls to initial state
        updateHourControl();
        updateAmPmControl();
        setOnTimeChangedListener(NO_OP_CHANGE_LISTENER);

        // set to current time
        setCurrentHour(currentHour);
        setCurrentMinute(currentMinute);
        setCurrentMeridiem(currentMeridiem);
        setDigitColor(mDigitColor);

        if (!isEnabled()) {
            setEnabled(false);
        }
        // If not explicitly specified this view is important for accessibility.
        if (getImportantForAccessibility() == IMPORTANT_FOR_ACCESSIBILITY_AUTO) {
            setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
        }

    }

    public int getDigitColor() {
        return mDigitColor;
    }

    public void setDigitColor(int color) {
        mDigitColor = color;
        setTextColor(mAmPmSpinner, mDigitColor);
        setTextColor(mMinuteSpinner, mDigitColor);
        setTextColor(mHourSpinner, mDigitColor);
    }

    private void setTextColor(NumberPicker picker, int color) {
        final int count = picker.getChildCount();
        for(int i = 0; i < count; i++){
            View child = picker.getChildAt(i);
            if(child instanceof EditText){
                try{
                    Field selectorWheelPaintField = picker.getClass().getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);

                    ((Paint)selectorWheelPaintField.get(picker)).setColor(color);

                    ((EditText)child).setTextColor(color);
                    picker.invalidate();
                }
                catch(NoSuchFieldException e){
                    Log.w("setNumberPickerTextColor", e);
                }
                catch(IllegalAccessException e){
                    Log.w("setNumberPickerTextColor", e);
                }
                catch(IllegalArgumentException e){
                    Log.w("setNumberPickerTextColor", e);
                }
            }
        }
    }

    public int getMinuteInterval() {
        return mMinuteInterval;
    }

    public void setMinuteInterval(int minuteInterval) {
        mMinuteInterval = minuteInterval;
        setInterval(mMinuteInterval, mMinuteSpinner);
    }

    public int getHourInterval() {
        return mHourInterval;
    }

    public void setHourInterval(int hourInterval) {
        mHourInterval = hourInterval;
        setInterval(mHourInterval, mHourSpinner);
    }

    private void setInterval(int interval, NumberPicker picker) {

            int max = 0;
            int min = 0;

            ArrayList<String> displayedValues = new ArrayList<>();

            if (picker == mHourSpinner) {
                Log.d("setInterval", "Set Hour Interval(" + Integer.toString(interval) + ")");

                min = 1;
                if (is24HourView()) {
                    max = 25;
                } else {
                    max = 13;
                }
                for (int i = min; i < max; i += interval) {
                    displayedValues.add(String.format("%d", i));
                }

            } else if (picker == mMinuteSpinner) {
                Log.d("setInterval", "Set Minute Interval(" + Integer.toString(interval) + ")");

                min = 0;
                max = 60;

                for (int i = min; i < max; i += interval) {
                    displayedValues.add(String.format("%02d", i));
                }
            }

            Log.d("Min/Max", Integer.toString(min) + "/" + Integer.toString(max) + ", " + Integer.toString(picker.getMinValue()) + "/" + Integer.toString(picker.getMaxValue()));
            String[] display = displayedValues.toArray(new String[0]);

            int maxV = picker.getMaxValue();
            picker.setMinValue(0);
            picker.setValue(0);
            Log.d("Set Min Values", Integer.toString(picker.getMinValue()));
            if (max > maxV){
                picker.setDisplayedValues(display);
                picker.setMaxValue(max/interval - (1 + min));
            } else {
                picker.setMaxValue(max/interval - (1 + min));
                picker.setDisplayedValues(display);
            }
    }


    @Override
    public void setEnabled(boolean enabled) {
        if (mIsEnabled == enabled) {
            return;
        }

        super.setEnabled(enabled);
        mMinuteSpinner.setEnabled(enabled);
        mHourSpinner.setEnabled(enabled);
        mAmPmSpinner.setEnabled(enabled);
        mIsEnabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return mIsEnabled;
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setCurrentLocale(newConfig.locale);
    }

    /**
     * Sets the current locale.
     *
     * @param locale The current locale.
     */
    private void setCurrentLocale(Locale locale) {
        if (locale.equals(mCurrentLocale)) {
            return;
        }
        mCurrentLocale = locale;
        mTempCalendar = Calendar.getInstance(locale);
    }

    /**
     * Used to save / restore state of time picker
     */
    private static class SavedState extends BaseSavedState {

        private final int mHour;

        private final int mMinute;

        private SavedState(Parcelable superState, int hour, int minute) {
            super(superState);
            mHour = hour;
            mMinute = minute;
        }

        private SavedState(Parcel in) {
            super(in);
            mHour = in.readInt();
            mMinute = in.readInt();
        }

        public int getHour() {
            return mHour;
        }

        public int getMinute() {
            return mMinute;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(mHour);
            dest.writeInt(mMinute);
        }

        @SuppressWarnings({"unused", "hiding"})
        public static final Parcelable.Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, getCurrentHour(), getCurrentMinute());
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setCurrentHour(ss.getHour());
        setCurrentMinute(ss.getMinute());
    }

    /**
     * Set the callback that indicates the time has been adjusted by the user.
     *
     * @param onTimeChangedListener the callback, should not be null.
     */
    public void setOnTimeChangedListener(OnTimeChangedListener onTimeChangedListener) {
        mOnTimeChangedListener = onTimeChangedListener;
    }

    /**
     * @return The current hour in the range (0-23).
     */
    public Integer getCurrentHour() {
        int currentHour = mHourSpinner.getValue() * mHourInterval;
        if (is24HourView()) {
            return currentHour;
        } else if (mMeridiem == AM) {
            return currentHour % HOURS_IN_HALF_DAY;
        } else {
            return (currentHour % HOURS_IN_HALF_DAY) + HOURS_IN_HALF_DAY;
        }
    }

    /**
     * Set the current hour.
     */
    public void setCurrentHour(Integer currentHour) {
        setCurrentHour(currentHour, true);
    }

    private void setCurrentHour(Integer currentHour, boolean notifyTimeChanged) {
        if (currentHour == null || currentHour == getCurrentHour()) {
            return;
        }
        if (!is24HourView()) {
            // convert [0,23] ordinal to wall clock display
            if (currentHour >= HOURS_IN_HALF_DAY) {
                mMeridiem = PM;
                if (currentHour > HOURS_IN_HALF_DAY) {
                    currentHour = currentHour - HOURS_IN_HALF_DAY;
                }
            } else {
                mMeridiem = AM;
                if (currentHour == 0) {
                    currentHour = HOURS_IN_HALF_DAY;
                }
            }
            updateAmPmControl();
        }
        mHourSpinner.setValue(currentHour / mHourInterval);
        if (notifyTimeChanged) {
            onTimeChanged();
        }
    }

    /**
     * Set whether in 24 hour or AM/PM mode.
     *
     * @param is24HourView True = 24 hour mode. False = AM/PM.
     */
    public void setIs24HourView(Boolean is24HourView) {
        if (mIs24HourView == is24HourView) {
            return;
        }
        // cache the current hour since spinner range changes and BEFORE changing mIs24HourView!!
        int currentHour = getCurrentHour();
        // Order is important here.
        mIs24HourView = is24HourView;
        updateHourControl();
        // set value after spinner range is updated - be aware that because mIs24HourView has
        // changed then getCurrentHour() is not equal to the currentHour we cached before so
        // explicitly ask for *not* propagating any onTimeChanged()
        setCurrentHour(currentHour, false /* no onTimeChanged() */);
        updateAmPmControl();
    }

    /**
     * @return true if this is in 24 hour view else false.
     */
    public boolean is24HourView() {
        return mIs24HourView;
    }

    /**
     * @return The current minute.
     */
    public Integer getCurrentMinute() {
        return mMinuteSpinner.getValue() * mMinuteInterval;
    }

    public void setCurrentMinute(Integer currentMinute) {
        if (currentMinute/mMinuteInterval == getCurrentMinute()) {
            return;
        }

        mMinuteSpinner.setValue(currentMinute/mMinuteInterval);
        onTimeChanged();
    }

    public int getCurrentMeridiem() {
        return mMeridiem;
    }

    public void setCurrentMeridiem(int meridiem) {
        if (meridiem == AM || meridiem == PM) {
            mMeridiem = meridiem;
        }
    }

    @Override
    public int getBaseline() {
        return mHourSpinner.getBaseline();
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        onPopulateAccessibilityEvent(event);
        return true;
    }

    @Override
    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        super.onPopulateAccessibilityEvent(event);

        int flags = DateUtils.FORMAT_SHOW_TIME;
        if (mIs24HourView) {
            flags |= DateUtils.FORMAT_24HOUR;
        } else {
            flags |= DateUtils.FORMAT_12HOUR;
        }
        mTempCalendar.set(Calendar.HOUR_OF_DAY, getCurrentHour());
        mTempCalendar.set(Calendar.MINUTE, getCurrentMinute());
        String selectedDateUtterance = DateUtils.formatDateTime(mContext, mTempCalendar.getTimeInMillis(), flags);
        event.getText().add(selectedDateUtterance);
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(TimePicker.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(TimePicker.class.getName());
    }

    private void updateHourControl() {
        if (is24HourView()) {
            // 'k' means 1-24 hour
            if (mHourFormat == 'k') {
                mHourSpinner.setMinValue(1);
                mHourSpinner.setMaxValue(24 / mHourInterval);
            } else {
                mHourSpinner.setMinValue(0);
                mHourSpinner.setMaxValue(23 / mHourInterval);
            }
        } else {
            // 'K' means 0-11 hour
            if (mHourFormat == 'K') {
                mHourSpinner.setMinValue(0);
                mHourSpinner.setMaxValue(11 / mHourInterval);
            } else {
                mHourSpinner.setMinValue(1);
                mHourSpinner.setMaxValue(12 / mHourInterval);
            }
        }
    }

    private void updateAmPmControl() {
        if (is24HourView()) {
            mAmPmSpinner.setVisibility(View.GONE);
        } else {
            int index = (mMeridiem == AM) ? Calendar.AM : Calendar.PM;
            mAmPmSpinner.setValue(index);
            mAmPmSpinner.setVisibility(View.VISIBLE);
        }
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
    }

    private void onTimeChanged() {
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
        if (mOnTimeChangedListener != null) {
            mOnTimeChangedListener.onTimeChanged(this, getCurrentHour(), getCurrentMinute());
        }
    }


    public void hideMinuteHand() {
        mMinuteSpinner.setVisibility(NumberPicker.INVISIBLE);
    }

    public void hideHourHand() {
        mHourSpinner.setVisibility(NumberPicker.INVISIBLE);
    }

    public void showMinuteHand() {
        mMinuteSpinner.setVisibility(NumberPicker.VISIBLE);
    }

    public void showHourHand() {
        mHourSpinner.setVisibility(NumberPicker.VISIBLE);
    }

}