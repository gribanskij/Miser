package com.gribanskij.miser.edit_screen;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.gribanskij.miser.R;
import com.gribanskij.miser.asynctask.AsyncQueryTask;
import com.gribanskij.miser.sql_base.MiserContract;
import com.gribanskij.miser.utils.AbstractActivity;
import com.gribanskij.miser.utils.DatePickerFragment;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;


public class EditFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = EditFragment.class.getSimpleName();


    public static final String _ID = "_idm";
    public static final String DATE = "date";
    public static final String SUM = "sum";
    public static final String DESCRIPTION = "discr";
    public static final String _TYPE = "_type";
    public static final String CATEGORY = "category";
    public static final String ACCOUNT = "account";


    private static final int REQEST_DATE = 0;
    private static final int TOKEN_UPDATE = 21;
    private static final int TOKEN_UPDATE_ACCOUNT = 22;
    private static final int ACCOUNT_CATEGORY_NAME_LOADER = 10;
    private static final int INCOME_CATEGORY_NAME_LOADER = 20;
    private static final int COST_CATEGORY_NAME_LOADER = 30;

    private long mDate;


    private Bundle transaction;

    private SimpleCursorAdapter adapter_category_cost;
    private SimpleCursorAdapter adapter_category_income;
    private SimpleCursorAdapter adapter_category_account;
    private TextView textView_date;
    private Spinner category_spinner;
    private Spinner type_spinner;
    private Spinner account_spinner;
    private EditText sum_edittext;
    private EditText descrip_edittext;
    private NumberFormat numberFormat;


    public EditFragment() {
    }


    public static Fragment newInstance(Bundle transaction) {
        Fragment fragment = new EditFragment();
        fragment.setArguments(transaction);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBundle(AbstractActivity.TRANSACTION, transaction);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            transaction = savedInstanceState.getBundle(AbstractActivity.TRANSACTION);
        } else {
            if (getArguments() != null) {
                transaction = getArguments();
            }
        }

        numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        numberFormat.setMaximumFractionDigits(2);

        adapter_category_cost = new SimpleCursorAdapter(getContext(), android.R.layout.simple_spinner_item,
                null, new String[]{MiserContract.CategoryTable.Cols.CATEGORY_NAME}, new int[]{android.R.id.text1}, 0);
        adapter_category_cost.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapter_category_income = new SimpleCursorAdapter(getContext(), android.R.layout.simple_spinner_item,
                null, new String[]{MiserContract.CategoryTable.Cols.CATEGORY_NAME}, new int[]{android.R.id.text1}, 0);
        adapter_category_income.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapter_category_account = new SimpleCursorAdapter(getContext(), android.R.layout.simple_spinner_item,
                null, new String[]{MiserContract.CategoryTable.Cols.CATEGORY_NAME}, new int[]{android.R.id.text1}, 0);
        adapter_category_account.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

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

        View v = inflater.inflate(R.layout.edit_main, container, false);
        ImageButton ok_button = v.findViewById(R.id.save_button);
        sum_edittext = v.findViewById(R.id.sum_edittext_);
        descrip_edittext = v.findViewById(R.id.descrip_edittext);
        textView_date = v.findViewById(R.id.calendar_textview);
        type_spinner = v.findViewById(R.id.type_spiner);
        category_spinner = v.findViewById(R.id.category_spiner);
        account_spinner = v.findViewById(R.id.account_spiner);
        Toolbar toolbar = v.findViewById(R.id.toolbar);


        type_spinner.setEnabled(false);
        account_spinner.setEnabled(false);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        ActionBar bar = activity.getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }


        descrip_edittext.setText(transaction.getString(DESCRIPTION));

        //String sum = numberFormat.format(transaction.getFloat(SUM));
        String sum = Float.toString(transaction.getFloat(SUM));
        sum_edittext.setText(sum);
        mDate = transaction.getLong(DATE);
        textView_date.setText(DateFormat.getDateInstance().format(new Date(transaction.getLong(DATE))));
        type_spinner.setSelection(transaction.getInt(_TYPE));
        account_spinner.setAdapter(adapter_category_account);

        if (transaction.getInt(_TYPE) == MiserContract.TYPE_COST) {
            category_spinner.setAdapter(adapter_category_cost);
        } else {
            category_spinner.setAdapter(adapter_category_income);
        }

        textView_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = new DatePickerFragment();
                dialog.setTargetFragment(EditFragment.this, REQEST_DATE);
                dialog.show(manager, LOG_TAG);
            }
        });

        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int mCategory = category_spinner.getSelectedItemPosition();

                float mSum;

                if (sum_edittext.getText().toString().length() == 0) {
                    mSum = 0F;
                } else {
                    mSum = Float.valueOf(sum_edittext.getText().toString());
                }
                String mDescription = descrip_edittext.getText().toString();
                if (mDescription.length() == 0) {
                    mDescription = getResources().getString(R.string.no_comments);
                }


                ContentValues contentValues = new ContentValues();
                contentValues.put(MiserContract.DataTable.Cols.CATEGORY_ID, mCategory);
                contentValues.put(MiserContract.DataTable.Cols.DESCRIPTION, mDescription);
                contentValues.put(MiserContract.DataTable.Cols.AMOUNT, mSum);
                contentValues.put(MiserContract.DataTable.Cols.DATE, mDate);


                Uri uri = MiserContract.DataTable.DATA_URI;

                String selection = "_id=?";
                String[] arg = new String[]{Long.toString(transaction.getLong(_ID))};

                AsyncQueryTask mAsyncTask = new AsyncQueryTask(getContext().getContentResolver(), getContext());
                mAsyncTask.startUpdate(TOKEN_UPDATE, null, uri, contentValues, selection, arg);


                getActivity().finish();
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQEST_DATE) {
            mDate = data.getLongExtra(DatePickerFragment.EXTRA_DATE_LONG, 0);
            textView_date.setText(DateFormat.getDateInstance().format(new Date(mDate)));
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        Uri uri = MiserContract.CategoryTable.CATEGORIES_URI;


        String[] projection = new String[]{MiserContract.CategoryTable.Cols._ID, MiserContract.CategoryTable.Cols.CATEGORY_NAME};
        String selection = MiserContract.CategoryTable.Cols.TYPE + " = ? ";
        String[] selectionArg = null;


        switch (id) {
            case ACCOUNT_CATEGORY_NAME_LOADER: {

                selectionArg = new String[]{Integer.toString(MiserContract.TYPE_ACCOUNTS)};
                break;
            }
            case INCOME_CATEGORY_NAME_LOADER: {

                selectionArg = new String[]{Integer.toString(MiserContract.TYPE_INCOM)};
                break;
            }
            case COST_CATEGORY_NAME_LOADER: {

                selectionArg = new String[]{Integer.toString(MiserContract.TYPE_COST)};
                break;
            }
        }

        return new CursorLoader(getContext(), uri, projection, selection, selectionArg, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch (loader.getId()) {
            case ACCOUNT_CATEGORY_NAME_LOADER: {
                adapter_category_account.swapCursor(data);
                account_spinner.post(new Runnable() {
                    @Override
                    public void run() {
                        account_spinner.setSelection(transaction.getInt(ACCOUNT));
                    }
                });
                break;
            }
            case INCOME_CATEGORY_NAME_LOADER: {
                adapter_category_income.swapCursor(data);
                if (transaction.getInt(_TYPE) == MiserContract.TYPE_INCOM) {
                    category_spinner.post(new Runnable() {
                        @Override
                        public void run() {
                            category_spinner.setSelection(transaction.getInt(CATEGORY));
                        }
                    });
                }

                break;
            }
            case COST_CATEGORY_NAME_LOADER: {
                adapter_category_cost.swapCursor(data);
                if (transaction.getInt(_TYPE) == MiserContract.TYPE_COST) {
                    category_spinner.post(new Runnable() {
                        @Override
                        public void run() {
                            category_spinner.setSelection(transaction.getInt(CATEGORY));
                        }
                    });
                }

                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case ACCOUNT_CATEGORY_NAME_LOADER: {
                adapter_category_account.swapCursor(null);
                break;
            }
            case INCOME_CATEGORY_NAME_LOADER: {
                adapter_category_income.swapCursor(null);
                break;
            }
            case COST_CATEGORY_NAME_LOADER: {
                adapter_category_cost.swapCursor(null);
                break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getActivity().onBackPressed();
            //NavUtils.navigateUpFromSameTask(getActivity());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
