package com.gribanskij.miser.add;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.gribanskij.miser.R;
import com.gribanskij.miser.asynctask.AsyncQueryTask;
import com.gribanskij.miser.sql_base.MiserContract;
import com.gribanskij.miser.utils.DatePickerFragment;
import com.gribanskij.miser.utils.TimeUtils;

import java.text.DateFormat;
import java.util.Date;


public class AddFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = AddFragment.class.getSimpleName();

    public static final String TYPE = "type";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQEST_DATE = 0;
    private static final String DIALOG_DATE = "DialogDate";
    private static final int TOKEN_INSERT = 20;
    private static final int ACCOUNT_CATEGORY_NAME_LOADER = 19;
    private static final int INCOME_CATEGORY_NAME_LOADER = 29;
    private static final int COST_CATEGORY_NAME_LOADER = 39;


    private Date mDate;
    private int mType;
    private int mCategory;
    private SimpleCursorAdapter adapter_cost;
    private SimpleCursorAdapter adapter_incom;
    private SimpleCursorAdapter adapter_account;
    private TextView textView_date;
    private Spinner category_spinner;
    private Spinner type_spinner;
    private Spinner account_spinner;
    private EditText sum_edittext;
    private EditText descrip_edittext;


    public AddFragment() {
    }


    public static AddFragment newInstance(int param1, int param2) {
        AddFragment fragment = new AddFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(TimeUtils.TYPE, mType);
        outState.putInt(TimeUtils.CATEGORY, mCategory);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mType = getArguments().getInt(ARG_PARAM1, TimeUtils.TYPE_COST);
            mCategory = getArguments().getInt(ARG_PARAM2, TimeUtils.DEFAULT_CATEGORY);
        }

        if (savedInstanceState != null) {
            mType = savedInstanceState.getInt(TimeUtils.TYPE);
            mCategory = savedInstanceState.getInt(TimeUtils.CATEGORY);
        }

        adapter_cost = new SimpleCursorAdapter(getContext(), android.R.layout.simple_spinner_item, null, new String[]{"name"}, new int[]{android.R.id.text1}, 0);
        adapter_cost.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapter_incom = new SimpleCursorAdapter(getContext(), android.R.layout.simple_spinner_item, null, new String[]{"name"}, new int[]{android.R.id.text1}, 0);
        adapter_incom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapter_account = new SimpleCursorAdapter(getContext(), android.R.layout.simple_spinner_item, null, new String[]{"name"}, new int[]{android.R.id.text1}, 0);
        adapter_account.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(ACCOUNT_CATEGORY_NAME_LOADER, null, this);
        getLoaderManager().initLoader(INCOME_CATEGORY_NAME_LOADER, null, this);
        getLoaderManager().initLoader(COST_CATEGORY_NAME_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.add_main, container, false);
        //ImageButton cancel_button = v.findViewById(R.id.cancel_button);
        ImageButton ok_button = v.findViewById(R.id.save_button);
        sum_edittext = v.findViewById(R.id.sum_edittext_);
        descrip_edittext = v.findViewById(R.id.descrip_edittext);
        textView_date = v.findViewById(R.id.calendar_textview);
        type_spinner = v.findViewById(R.id.type_spiner);
        category_spinner = v.findViewById(R.id.category_spiner);
        account_spinner = v.findViewById(R.id.account_spiner);


        Toolbar toolbar = v.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        ActionBar bar = activity.getSupportActionBar();
        if (bar != null) {
            bar.setTitle(R.string.creating);
            bar.setDisplayHomeAsUpEnabled(true);
        }


        mDate = new Date();
        textView_date.setText(DateFormat.getDateInstance().format(mDate));
        type_spinner.setSelection(mType);

        account_spinner.setAdapter(adapter_account);

        if (mType == TimeUtils.TYPE_COST) {
            category_spinner.setAdapter(adapter_cost);
        } else {
            category_spinner.setAdapter(adapter_incom);
        }

        textView_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance();
                dialog.setTargetFragment(AddFragment.this, REQEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == TimeUtils.TYPE_INCOM) {
                    category_spinner.setAdapter(adapter_incom);
                } else {
                    category_spinner.setAdapter(adapter_cost);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int mType = type_spinner.getSelectedItemPosition();
                int mCategory = category_spinner.getSelectedItemPosition();
                int mAccount = account_spinner.getSelectedItemPosition();
                Long mDate_ = mDate.getTime();
                float mSum;

                if (sum_edittext.getText().toString().equals("")) {
                    mSum = 0F;
                } else {
                    mSum = Float.valueOf(sum_edittext.getText().toString());
                }
                String mDescription = descrip_edittext.getText().toString();
                if (mDescription.length() == 0) {
                    mDescription = getResources().getString(R.string.no_comments);
                }
                ContentValues contentValues = new ContentValues();
                contentValues.put(MiserContract.DataTable.Cols.TYPE, mType);
                contentValues.put(MiserContract.DataTable.Cols.CATEGORY_ID, mCategory);
                contentValues.put(MiserContract.DataTable.Cols.DESCRIPTION, mDescription);
                contentValues.put(MiserContract.DataTable.Cols.AMOUNT, mSum);
                contentValues.put(MiserContract.DataTable.Cols.DATE, mDate_);

                AsyncQueryTask mAsyncTask = new AsyncQueryTask(getContext().getContentResolver(), getContext());
                mAsyncTask.startInsert(TOKEN_INSERT, null, MiserContract.DataTable.DATA_URI, contentValues);

                Thread mThread = new AddThread(getContext().getContentResolver(), mAccount, mSum, mType);
                mThread.start();

                //getActivity().setResult(Activity.RESULT_OK, null);
                getActivity().finish();
            }
        });

        /*
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setResult(Activity.RESULT_CANCELED, null);
                getActivity().finish();
            }
        });
        */

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQEST_DATE) {
            mDate = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            textView_date.setText(DateFormat.getDateInstance().format(mDate));
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(getActivity());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        Uri uri = null;
        String[] projection = null;
        String selection = null;
        String sortOrder = null;
        String[] selectionArg = null;
        Long begin;
        Long end;
        int dataType;

        switch (id) {
            case ACCOUNT_CATEGORY_NAME_LOADER: {

                uri = MiserContract.CategoryTable.CATEGORIES_URI;
                projection = new String[]{MiserContract.CategoryTable.Cols._ID, MiserContract.CategoryTable.Cols.CATEGORY_NAME};
                selection = MiserContract.CategoryTable.Cols.TYPE + " = ? ";
                selectionArg = new String[]{Integer.toString(MiserContract.TYPE_ACCOUNTS)};
                sortOrder = null;
                break;
            }
            case INCOME_CATEGORY_NAME_LOADER: {

                uri = MiserContract.CategoryTable.CATEGORIES_URI;
                projection = new String[]{MiserContract.CategoryTable.Cols._ID, MiserContract.CategoryTable.Cols.CATEGORY_NAME};
                selection = MiserContract.CategoryTable.Cols.TYPE + " = ? ";
                selectionArg = new String[]{Integer.toString(MiserContract.TYPE_INCOM)};
                sortOrder = null;
                break;
            }
            case COST_CATEGORY_NAME_LOADER: {

                uri = MiserContract.CategoryTable.CATEGORIES_URI;
                projection = new String[]{MiserContract.CategoryTable.Cols._ID, MiserContract.CategoryTable.Cols.CATEGORY_NAME};
                selection = MiserContract.CategoryTable.Cols.TYPE + " = ? ";
                selectionArg = new String[]{Integer.toString(MiserContract.TYPE_COST)};
                sortOrder = null;
                break;
            }
        }

        return new CursorLoader(getContext(), uri, projection, selection, selectionArg, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch (loader.getId()) {
            case ACCOUNT_CATEGORY_NAME_LOADER: {
                adapter_account.swapCursor(data);
                break;
            }
            case INCOME_CATEGORY_NAME_LOADER: {
                adapter_incom.swapCursor(data);
                if (mType == MiserContract.TYPE_INCOM) {
                    category_spinner.post(new Runnable() {
                        @Override
                        public void run() {
                            category_spinner.setSelection(mCategory);
                        }
                    });
                    Log.i(LOG_TAG, "SET SELECTION in INCOM");
                }

                break;
            }
            case COST_CATEGORY_NAME_LOADER: {
                adapter_cost.swapCursor(data);
                if (mType == MiserContract.TYPE_COST) {
                    category_spinner.post(new Runnable() {
                        @Override
                        public void run() {
                            category_spinner.setSelection(mCategory);
                        }
                    });

                    Log.i(LOG_TAG, "SET SELECTION IN COST");
                }

                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case ACCOUNT_CATEGORY_NAME_LOADER: {
                adapter_account.swapCursor(null);
                break;
            }
            case INCOME_CATEGORY_NAME_LOADER: {
                adapter_incom.swapCursor(null);
                break;
            }
            case COST_CATEGORY_NAME_LOADER: {
                adapter_cost.swapCursor(null);
                break;
            }
        }
    }

    private static class AddThread extends Thread {

        private static final String LOG_TAG = AddThread.class.getSimpleName();
        private final int ACCOUNT_SUM = 0;
        private ContentResolver contentResolver;
        private int accountId;
        private float sum;
        private int type;


        private AddThread(ContentResolver contentResolver, int accountId, float sum, int type) {
            this.contentResolver = contentResolver;
            this.accountId = accountId;
            this.sum = sum;
            this.type = type;
        }

        @Override
        public void run() {
            double oldAccountSum;
            double newAccountSum;
            Uri uri = MiserContract.AccountTable.ACCOUNTS_URI;

            String[] projection = new String[]{MiserContract.AccountTable.Cols.ACCOUNT_AMOUNT};
            String selection = MiserContract.AccountTable.Cols.CATEGORY_ID + " = ? ";
            String[] selectionArgs = new String[]{Integer.toString(accountId)};

            Log.i(LOG_TAG, "START query database");

            Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                oldAccountSum = cursor.getDouble(ACCOUNT_SUM);
                cursor.close();

            } else {
                return;
            }

            if (type == TimeUtils.TYPE_COST) {
                newAccountSum = oldAccountSum - sum;
                if (newAccountSum < 0) {
                    newAccountSum = 0;
                }
            } else {
                newAccountSum = oldAccountSum + sum;
            }

            selectionArgs = new String[]{Integer.toString(accountId)};
            String where = MiserContract.AccountTable.Cols.CATEGORY_ID + " = ? ";
            ContentValues contentValues = new ContentValues();
            contentValues.put(MiserContract.DataTable.Cols.AMOUNT, newAccountSum);
            Log.i(LOG_TAG, "START update database");
            int a = contentResolver.update(uri, contentValues, where, selectionArgs);
            if (a == 0) Log.i(LOG_TAG, "ERROR of database");
        }
    }
}
