package com.gribanskij.miser.dashboard;


import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.gribanskij.miser.R;
import com.gribanskij.miser.asynctask.AsyncQueryTask;
import com.gribanskij.miser.sql_base.MiserContract;

public class EditDialog extends DialogFragment {

    public static final int TOKEN_CATEGORY_NAME = 50;
    public static final String CATEGORY_NAME = "name";
    private static final String CATEGORY_TYPE = "param1";
    private static final String CATEGORY_ID = "param2";
    private int type;
    private int categoryID;
    private String name;


    public EditDialog() {
    }

    public static EditDialog newInstance(int type, int categoryID, String name) {
        EditDialog fragment = new EditDialog();
        Bundle args = new Bundle();
        args.putInt(CATEGORY_TYPE, type);
        args.putInt(CATEGORY_ID, categoryID);
        args.putString(CATEGORY_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        if (getArguments() != null) {
            type = getArguments().getInt(CATEGORY_TYPE);
            categoryID = getArguments().getInt(CATEGORY_ID);
            name = getArguments().getString(CATEGORY_NAME);
        }

        if (savedInstanceState != null) {
            type = savedInstanceState.getInt(CATEGORY_TYPE);
            categoryID = savedInstanceState.getInt(CATEGORY_ID);
            name = savedInstanceState.getString(CATEGORY_NAME);
        }

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.category_edit, null);
        final EditText editText = view.findViewById(R.id.edittext_name_category);

        return new AlertDialog.Builder(getActivity()).setView(view).setTitle(name).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String newName = editText.getText().toString();

                Uri uri = MiserContract.CategoryTable.CATEGORIES_URI;
                ContentValues contentValues = new ContentValues();
                contentValues.put(MiserContract.CategoryTable.Cols.CATEGORY_NAME, newName);
                String were = MiserContract.CategoryTable.Cols.CATEGORY_ID + " = ? " +
                        " AND " + MiserContract.CategoryTable.Cols.TYPE + " = ? ";
                String[] arg = new String[]{Integer.toString(categoryID), Integer.toString(type)};
                AsyncQueryTask mTask = new AsyncQueryTask(getContext().getContentResolver(), getContext());
                mTask.startUpdate(TOKEN_CATEGORY_NAME, null, uri, contentValues, were, arg);

            }
        })
                .setNegativeButton(android.R.string.cancel, null).create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CATEGORY_NAME, name);
        outState.putInt(CATEGORY_ID, categoryID);
        outState.putInt(CATEGORY_TYPE, type);
    }
}
