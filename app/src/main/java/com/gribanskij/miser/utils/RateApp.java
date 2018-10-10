package com.gribanskij.miser.utils;


import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.gribanskij.miser.R;


/**
 * Created by SESA175711 on 26.10.2017.
 */

public class RateApp extends DialogFragment {

    private static final String URL_APP = "https://play.google.com/store/apps/details?id=com.gribanskij.miser";
    private static final String URI_APP = "market://details?id=com.gribanskij.miser";

    public RateApp() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);

        return new AlertDialog.Builder(getActivity()).setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onClickRateThisApp();


                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
    }


    private boolean isActivityStarted(Intent aIntent) {
        try {
            startActivity(aIntent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    private void onClickRateThisApp() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(URI_APP));
        if (!isActivityStarted(intent)) {
            intent.setData(Uri.parse(URL_APP));
            if (!isActivityStarted(intent)) {
                Toast.makeText(getContext(), R.string.market_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
