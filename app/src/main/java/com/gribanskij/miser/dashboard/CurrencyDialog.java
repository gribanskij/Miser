package com.gribanskij.miser.dashboard;

import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.gribanskij.miser.R;
import com.gribanskij.miser.asynctask.AsyncQueryTask;
import com.gribanskij.miser.sql_base.MiserContract;

/**
 * Created by sesa175711 on 27.02.2017.
 */
public class CurrencyDialog extends DialogFragment {

    public static final int TOKEN_CURRENCY = 100;
    private Spinner spinner;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.currency_dialog, null);
        spinner = view.findViewById(R.id.currency_spinner);

        String[] currency = getResources().getStringArray(R.array.currency);

        ArrayAdapter adapter = new Adapter(getContext(), android.R.layout.simple_spinner_item, currency);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        return new AlertDialog.Builder(getActivity()).setView(view).setTitle(R.string.title_dialog_currency).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String newCurrency = (String) spinner.getSelectedItem();
                AsyncQueryHandler mTask = new AsyncQueryTask(getContext().getContentResolver(), getContext());

                Uri uri = MiserContract.CategoryTable.CATEGORIES_URI;
                ContentValues contentValues = new ContentValues();
                contentValues.put(MiserContract.CategoryTable.Cols.SYSTEM_CURRENCY, newCurrency);
                mTask.startUpdate(TOKEN_CURRENCY, null, uri, contentValues, null, null);

            }
        })
                .setNegativeButton(android.R.string.cancel, null).create();
    }


    private class Adapter extends ArrayAdapter {

        private int textViewResourceId;
        private Context context;
        private String[] categories;

        Adapter(Context context, int resource, String[] objects) {
            super(context, resource, objects);
            this.textViewResourceId = resource;
            this.context = context;
            this.categories = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = View.inflate(context, textViewResourceId, null);
            TextView tv = (TextView) convertView;
            tv.setText(categories[position]);
            return convertView;
        }
    }
}



