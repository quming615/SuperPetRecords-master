package kbc.superpetrecords;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;

/**
 * Created by kellanbc on 6/28/14.
 */
public class AccordianView extends AdapterView {

    private Adapter adapter;
    /**
     * Returns the adapter currently associated with this widget.
     *
     * @return The adapter used to provide this view's content.
     */

    public AccordianView(Context context) {
        super(context);
    }

    public AccordianView(Context context, AttributeSet atts) {
        super(context, atts);
    }

    public AccordianView(Context context, AttributeSet atts, int defStyle) {
        super(context, atts, defStyle);
    }

    @Override
    public Adapter getAdapter() {
        return adapter;
    }

    @Override public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
    }

    /**
     * @return The view corresponding to the currently selected item, or null
     * if nothing is selected
     */
    @Override
    public View getSelectedView() {
        return null;
    }

    /**
     * Sets the currently selected item. To support accessibility subclasses that
     * override this method must invoke the overriden super method first.
     *
     * @param position Index (starting at 0) of the data item to be selected.
     */
    @Override
    public void setSelection(int position) {

    }
}
