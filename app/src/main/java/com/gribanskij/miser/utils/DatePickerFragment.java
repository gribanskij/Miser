package com.gribanskij.miser.utils;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.gribanskij.miser.R;

import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment {

    public static final String EXTRA_DATE = "extra date";
    public static final String EXTRA_DATE_LONG = "long_date";
    private DatePicker mDatePicker = null;

    public DatePickerFragment() {
    }

    public static DatePickerFragment newInstance() {
        Bundle args = new Bundle();
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);
        mDatePicker = v.findViewById(R.id.datePicker);

        return new AlertDialog.Builder(getActivity()).setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = mDatePicker.getYear();
                        int month = mDatePicker.getMonth();
                        int day = mDatePicker.getDayOfMonth();
                        Date date = new GregorianCalendar(year, month, day).getTime();
                        sendResult(Activity.RESULT_OK, date);
                    }
                }).create();
    }

    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() != null) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DATE, date);
            intent.putExtra(EXTRA_DATE_LONG, date.getTime());
            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
        }
    }
}
