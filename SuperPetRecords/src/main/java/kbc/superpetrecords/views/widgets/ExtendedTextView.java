package kbc.superpetrecords.views.widgets;

import android.content.res.*;
import android.content.*;
import android.util.*;
import android.widget.*;
import android.graphics.*;
import kbc.superpetrecords.R;

/**
 * Created by kellanbc on 7/3/14.
 */
public class ExtendedTextView extends TextView {

    public ExtendedTextView(Context context) {
        super(context, null);
    }

    public ExtendedTextView(Context context, AttributeSet attrs) {
        super(context, attrs, Resources.getSystem().getIdentifier("textViewStyle", "attr", "android"));
        init(attrs);
    }

    public ExtendedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs!=null && !this.isInEditMode()) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ExtendedTextView);
            String fontName = a.getString(R.styleable.ExtendedTextView_fontName);
            if (fontName!=null) {
                setFontName(fontName);
            }
            a.recycle();
        }
    }

    public void setFontName(String name) {
        if (name!=null && !this.isInEditMode()) {
            Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + name);
            setTypeface(myTypeface);
        }
    }

}
