package kbc.superpetrecords.views.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import kbc.superpetrecords.R;

public class DetailView extends LinearLayout {

    private ExtendedEditText fieldView;
    private ExtendedTextView labelView;
    private Context context;

    private int labelViewId = R.id.label;
    private int fieldViewId = R.id.field;


    public DetailView(Context context) {
        super(context);
        this.context = context;
    }

    public DetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DetailView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.details_view, this);
        fieldView = (ExtendedEditText) ll.findViewById(fieldViewId);
        labelView = (ExtendedTextView) ll.findViewById(labelViewId);

        if (attrs!=null && !this.isInEditMode()) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DetailView);
            setLabelColor(a.getInt(R.styleable.DetailView_labelColor, R.color.white));
            setLabelText(a.getString(R.styleable.DetailView_labelText));
            setFieldText(a.getString(R.styleable.DetailView_fieldText));
        }
    }

    public void setLabelColor(int resid) {
        labelView.setBackgroundColor(resid);
    }

    public void setLabelText(String text) {
        labelView.setText(text);
    }

    public void setFieldText(String text) {
        fieldView.setText(text);
    }

    public String getFieldText() {
        return fieldView.getText().toString();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);


    }
}
