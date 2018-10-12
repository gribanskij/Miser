package com.gribanskij.miser.dashboard;


import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.gribanskij.miser.R;
import com.gribanskij.miser.utils.NotificationUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationDialog extends DialogFragment {

    public static final int TOKEN_NOTIFY = 145;


    public NotificationDialog() {
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_notification_dialog, null);

        Switch mSwitch = view.findViewById(R.id.notification_switch_id);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Boolean isEnable = sharedPreferences.getBoolean(getString(R.string.pref_notification_key), false);
        mSwitch.setChecked(isEnable);

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(getString(R.string.pref_notification_key), b);
                editor.apply();
                if (b) {
                    NotificationUtils.setAlarm(getActivity());
                    Toast.makeText(getActivity(), R.string.notification_is_on, Toast.LENGTH_SHORT).show();
                } else {

                    NotificationUtils.disableAlarm(getActivity());
                    Toast.makeText(getActivity(), R.string.notification_is_off, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return new AlertDialog.Builder(getActivity()).setView(view).setTitle(R.string.pref_notification_label)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null).create();
    }

}
