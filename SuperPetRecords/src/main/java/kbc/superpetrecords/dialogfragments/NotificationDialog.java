package kbc.superpetrecords.dialogfragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import kbc.superpetrecords.MainActivity;
import kbc.superpetrecords.R;

/**
 * Created by kellanbc on 8/6/14.
 */
public class NotificationDialog extends DialogFragment {

    int layout_id = R.layout.notification_dialog;
    int message_item_id = R.id.notification_header_text;
    int button_id = R.id.ok_button;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, 0);
    }

    public static NotificationDialog newInstance(String message) {
        Bundle b = new Bundle();
        b.putString("message", message);
        NotificationDialog d = new NotificationDialog();
        d.setArguments(b);
        return d;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle bundle) {
        super.onCreateView(inflater, parent, bundle);

        Bundle args = this.getArguments();

        View view = inflater.inflate(layout_id, parent, false);
        Button button = (Button) view.findViewById(button_id);
        TextView text = (TextView) view.findViewById(message_item_id);
        text.setText(args.getString("message"));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).removeDialogFragment(getTag());
            }
        });
        return view;
    }


}

