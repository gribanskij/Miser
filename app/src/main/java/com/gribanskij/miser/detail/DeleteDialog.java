package com.gribanskij.miser.detail;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.gribanskij.miser.R;
import com.gribanskij.miser.asynctask.AsyncQueryTask;
import com.gribanskij.miser.sql_base.MiserContract;


public class DeleteDialog extends DialogFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int TOKEN_DELETE = 10;

    private long mId;

    public DeleteDialog() {
    }

    public static DeleteDialog newInstance(long param1, String param2) {
        DeleteDialog deleteDialog = new DeleteDialog();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        deleteDialog.setArguments(args);
        return deleteDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (getArguments() != null) {
            mId = getArguments().getLong(ARG_PARAM1);
        }
        if (savedInstanceState != null) mId = savedInstanceState.getLong(ARG_PARAM1);

        return new AlertDialog.Builder(getActivity()).setTitle(R.string.del_dialog_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selection = MiserContract.DataTable.Cols._ID + " = ? ";
                        String[] arg = new String[]{Long.toString(mId)};
                        AsyncQueryTask mDeleteTask = new AsyncQueryTask(getContext().getContentResolver(), getContext());
                        mDeleteTask.startDelete(TOKEN_DELETE, null, MiserContract.DataTable.DATA_URI, selection, arg);
                    }
                }).setNegativeButton(R.string.cancel, null).create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ARG_PARAM1, mId);
    }
}
