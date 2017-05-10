package kbc.superpetrecords;

import android.view.*;
import android.view.animation.*;

/**
 * Created by kellanbc on 6/28/14.
 */
public class AnimateLayoutParams extends Animation {
    View view, parent_view;
    int starting_height, target_height, starting_width, target_width;

    public AnimateLayoutParams(View parent, View view, int target_height, int target_width) {
        this.parent_view = parent;
        this.view = view;

        starting_height = view.getLayoutParams().height;
        starting_width = view.getLayoutParams().width;
        this.target_height = target_height;
        this.target_width = target_width;
    }

    public AnimateLayoutParams(View parent, View view, int target_height) {
        this.parent_view = parent;
        this.view = view;

        starting_height = view.getLayoutParams().height;
        //starting_width = view.getLayoutParams().width;
        this.target_height = target_height;
        //this.target_width = starting_width;
    }


    @Override protected void applyTransformation(float time, Transformation transformation) {
        parent_view.getLayoutParams().height = starting_height + (int)((target_height - starting_height) * time);
        //parent_view.getLayoutParams().width = starting_width + (int)((target_width - starting_width) * time);

        view.requestLayout();
    }

    @Override public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override public boolean willChangeBounds() {
        return true;
    }
}
