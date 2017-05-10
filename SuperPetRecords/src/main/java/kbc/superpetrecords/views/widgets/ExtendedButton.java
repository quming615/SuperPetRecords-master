package kbc.superpetrecords.views.widgets;

import android.content.*;
import android.util.*;
import android.view.accessibility.*;
import android.widget.*;
import android.view.*;

import kbc.superpetrecords.R;

/**
 * Created by kellanbc on 8/10/14.
 */
public class ExtendedButton extends ExtendedTextView {
    public ExtendedButton(Context context) {
        this(context, null);
    }

    public ExtendedButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.extendedButtonStyle);
    }

    public ExtendedButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(ExtendedButton.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(ExtendedButton.class.getName());
    }
}
