package kbc.superpetrecords.views.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.content.*;
import android.view.*;

import kbc.superpetrecords.*;

/**
 * Created by kellanbc on 8/9/14.
 */
public class SidewaysPicker extends LinearLayout {

    public interface InputChangeListener {
        public void onInputIncrease(View view, int count);
        public void onInputDecrease(View view, int count);
    }

    private InputChangeListener mInputChangeListener;

    private Context context;
    private ExtendedButton plus, minus;
    private ExtendedTextView field;
    private int count = 0;
    private int minCount, maxCount;

    public SidewaysPicker(Context context) {
        super(context);
        this.context = context;
    }

    public SidewaysPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SidewaysPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        setMaxCount(-1);
        setMinCount(0);

        if (attrs!=null && !this.isInEditMode()) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SidewaysPicker);
            int max = a.getInt(R.styleable.SidewaysPicker_maxCount, -1);
            int min = a.getInt(R.styleable.SidewaysPicker_minCount, 0);
            setMinCount(min);
            setMaxCount(max);
            a.recycle();
        }

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sideways_picker, this, true);
        setOrientation(HORIZONTAL);
        setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        plus = (ExtendedButton) findViewById(R.id.plus);
        minus = (ExtendedButton) findViewById(R.id.minus);
        field = (ExtendedTextView) findViewById(R.id.field);

        field.setText(Integer.toString(getMinCount()));

        setOnInputChangeListener(null);
    }

    public int getMinCount() {
        return minCount;
    }

    public void setMinCount(int minCount) {
        this.minCount = minCount;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public int getCount() {
        return count;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Log.d("SidewaysPicker", "onLayout");

        LayoutParams params = new LayoutParams(getWidth() / 3, getHeight());
        plus.setLayoutParams(params);
        field.setLayoutParams(params);
        minus.setLayoutParams(params);

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(field.getWindowToken(), 0);

    }

    public void setOnInputChangeListener(InputChangeListener listener) {
        if (listener != null) mInputChangeListener = listener;

        plus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count < maxCount || maxCount == -1) {
                    ++count;
                    if (mInputChangeListener != null) mInputChangeListener.onInputIncrease(v, count);

                    Log.d("Plus", Integer.toString(count));
                    field.setText(Integer.toString(count));
                    ViewParent parent = SidewaysPicker.this.getParent();
                    parent.requestLayout();

                    field.invalidate();
                    field.requestLayout();
                    refreshDrawableState();
                } else {
                    Log.d("Plus:maxCount", Integer.toString(maxCount));
                }
            }
        });

        minus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count > minCount || minCount == -1) {
                    --count;
                    if (mInputChangeListener != null) mInputChangeListener.onInputDecrease(v, count);

                    Log.d("Minus", Integer.toString(count));
                    field.setText(Integer.toString(count));
                    ViewParent parent = SidewaysPicker.this.getParent();
                    parent.requestLayout();

                    field.invalidate();
                    field.requestLayout();
                    refreshDrawableState();
                } else {
                    Log.d("Plus:minCount", Integer.toString(minCount));
                }
            }
        });
    }
}
