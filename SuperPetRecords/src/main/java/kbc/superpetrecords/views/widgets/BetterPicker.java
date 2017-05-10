package kbc.superpetrecords.views.widgets;

/**
 * Created by kellanbc on 8/8/14.
 */
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import kbc.superpetrecords.R;

public class BetterPicker extends View {

    private String mFontName, mText;
    private int mPlusTextColor, mPlusColor, mMinusTextColor, mMinusColor, mFieldTextColor, mFieldColor;

    private float mTextHeight;
    private Rect mPlusOne, mMinusOne, mTextBox;
    private Paint mPlusTextPaint, mFieldTextPaint, mMinusTextPaint, mPlusPaint, mMinusPaint, mFieldPaint;
    private Typeface mTypeface;


    public BetterPicker(Context context) {
        super(context);
    }

    public BetterPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public BetterPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public void setFont(String fontName) {
        mFontName = fontName;
        mTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + mFontName);
        if (mTextHeight == 0) {
            mTextHeight = mFieldTextPaint.getTextSize();
        } else {
            mFieldTextPaint.setTextSize(mTextHeight);
        }

        invalidate();
        requestLayout();
    }

    public void setFieldTextColor(int color) {
        mFieldTextColor = color;
        mFieldTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFieldTextPaint.setColor(mFieldTextColor);
        invalidate();
        requestLayout();
    }

    private void setPlusTextColor(int color) {
        mPlusTextColor = color;
        mPlusTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPlusTextPaint.setColor(mPlusTextColor);
        invalidate();
        requestLayout();
    }

    private void setMinusTextColor(int color) {
        mMinusTextColor = color;
        mMinusTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMinusTextPaint.setColor(mMinusTextColor);
        invalidate();
        requestLayout();
    }

    public void setFieldColor(int color) {
        mFieldColor = color;
        mFieldPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFieldPaint.setColor(mFieldColor);
        invalidate();
        requestLayout();
    }

    private void setPlusColor(int color) {
        mPlusColor = color;
        mPlusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPlusPaint.setColor(mPlusColor);
        invalidate();
        requestLayout();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Account for padding
        float xpad = (float)(getPaddingLeft() + getPaddingRight());
        float ypad = (float)(getPaddingTop() + getPaddingBottom());

        // Account for the label
        if (mPlusOne != null) xpad += mPlusOne.width();
        if (mTextBox != null) xpad += mTextBox.width();
        if (mMinusOne != null) xpad += mMinusOne.width();

        float ww = (float)w - xpad;
        float hh = (float)h - ypad;

        // Figure out how big we can make the pie.
        float diameter = Math.min(ww, hh);

    }

    private void setMinusColor(int color) {
        mMinusColor = color;
        mMinusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMinusPaint.setColor(mMinusColor);
        invalidate();
        requestLayout();
    }

    private void init(AttributeSet attrs) {
        if (attrs!=null && !this.isInEditMode()) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BetterPicker);
            Log.d("BetterPicker Style", attrs.toString());
            //mFontName = a.getString(R.styleable.BetterPicker_fontName);
            //setFont(mFontName);
            //a.getResources().getColor(R.color.white);


            mPlusTextColor = a.getColor(R.styleable.BetterPicker_plusTextColor, R.color.white);
            mMinusTextColor = a.getColor(R.styleable.BetterPicker_minusTextColor, R.color.black);
            mFieldTextColor = a.getColor(R.styleable.BetterPicker_fieldTextColor, R.color.black);
            mFieldColor = a.getColor(R.styleable.BetterPicker_fieldColor, R.color.black);
            mMinusColor = a.getColor(R.styleable.BetterPicker_minusColor, R.color.bluegrassish);
            mPlusColor = a.getColor(R.styleable.BetterPicker_plusColor, R.color.bluegrassish);
            a.recycle();

            Rect bounds = new Rect();
            getDrawingRect(bounds);
            int w = getMeasuredWidth();
            mPlusOne = new Rect(bounds.left, bounds.top, bounds.left + (w * 1 / 3), bounds.bottom);
            mMinusOne = new Rect(bounds.left + (w * 2 / 3), bounds.top, bounds.right, bounds.bottom);
            mTextBox = new Rect(bounds.left + (w * 1 / 3), bounds.top, bounds.left + (w * 2 / 3), bounds.bottom);
            setFieldTextColor(mFieldTextColor);
            setMinusTextColor(mMinusTextColor);
            setPlusTextColor(mPlusTextColor);
            setFieldColor(mFieldColor);
            setMinusColor(mMinusColor);
            setPlusColor(mPlusColor);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(mPlusOne, mPlusPaint);
        canvas.drawText("+", mPlusOne.centerX(), mPlusOne.centerY(), mPlusPaint);

        canvas.drawRect(mTextBox, mFieldPaint);

        canvas.drawRect(mMinusOne, mMinusPaint);
        canvas.drawText("-", mMinusOne.centerX(), mMinusOne.centerY(), mMinusPaint);
    }
}
