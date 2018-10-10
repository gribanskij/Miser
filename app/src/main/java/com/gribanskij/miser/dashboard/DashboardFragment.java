package com.gribanskij.miser.dashboard;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.gribanskij.miser.R;
import com.gribanskij.miser.add.AddActivity;
import com.gribanskij.miser.categories.CategoryFragment;
import com.gribanskij.miser.graph_screen.GraphActivity;
import com.gribanskij.miser.sql_base.MiserContract;
import com.gribanskij.miser.utils.GraphLoader;
import com.gribanskij.miser.utils.LabelFormatter;
import com.gribanskij.miser.utils.TimeUtils;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN;

public class DashboardFragment extends Fragment implements DatePickerDialog.OnDateSetListener, android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {


    private static final String LOG_TAG = DashboardFragment.class.getSimpleName();
    private static final int DATA_SUM_INCOM = 0;
    private static final int DATA_SUM_COST = 10;
    private static final int ACCOUNT_AMOUNT = 20;
    private static final int ACCOUNT_NAME = 30;
    private static final int BARGRAPH_DATA_SET_COST = 50;
    private static final int BARGRAPH_DATA_SET_INCOME = 55;
    private static final int ADD_TYPE = 0;
    private static final String DIALOG_CURRENCY = "currency";
    private static final String URL_APP = "https://play.google.com/store/apps/details?id=com.gribanskij.miser";
    private static final String URI_APP = "market://details?id=com.gribanskij.miser";

    private final String ACCOUNT_1 = "account1";
    private final String ACCOUNT_2 = "account2";
    private final String ACCOUNT_3 = "account3";
    private final String ACCOUNT_4 = "account4";
    private long start_date;
    private long end_date;
    private Bundle args;
    private TextView view_cost;
    private TextView view_income;
    private NumberFormat numberFormat;
    private SimpleDateFormat simpleDateFormat;
    private ArrayList<TextView> list_sum;
    private ArrayList<TextView> list_category;
    private Cursor incomeCursor;
    private Cursor costCursor;
    private Cursor accountSumCursor;
    private Cursor accountNameCursor;
    private String currency;
    private GraphView cost_graph;
    private GraphView income_graph;

    public DashboardFragment() {
    }

    public static DashboardFragment newInstance(long param1, long param2) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putLong(TimeUtils.DATE_FROM, param1);
        args.putLong(TimeUtils.DATE_TO, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (args == null) args = new Bundle();
        args.clear();

        if (savedInstanceState != null) {
            args.putLong(TimeUtils.DATE_FROM, savedInstanceState.getLong(TimeUtils.DATE_FROM));
            args.putLong(TimeUtils.DATE_TO, savedInstanceState.getLong(TimeUtils.DATE_TO));
        } else {
            args.putLong(TimeUtils.DATE_FROM, start_date);
            args.putLong(TimeUtils.DATE_TO, end_date);
        }
        getLoaderManager().initLoader(DATA_SUM_COST, args, this);
        getLoaderManager().initLoader(DATA_SUM_INCOM, args, this);
        getLoaderManager().initLoader(ACCOUNT_AMOUNT, null, this);
        getLoaderManager().initLoader(ACCOUNT_NAME, null, this);

        GraphLoaderCallBack callback = new GraphLoaderCallBack();
        getLoaderManager().initLoader(BARGRAPH_DATA_SET_COST, null, callback);
        getLoaderManager().initLoader(BARGRAPH_DATA_SET_INCOME, null, callback);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        numberFormat.setMaximumFractionDigits(2);
        simpleDateFormat = new SimpleDateFormat("d MMM", Locale.getDefault());


        if (getArguments() != null) {
            start_date = getArguments().getLong(TimeUtils.DATE_FROM);
            end_date = getArguments().getLong(TimeUtils.DATE_TO);
        }
        if (savedInstanceState != null) {
            start_date = savedInstanceState.getLong(TimeUtils.DATE_FROM);
            end_date = savedInstanceState.getLong(TimeUtils.DATE_TO);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dashboard_main, container, false);
        FloatingActionButton fab = v.findViewById(R.id.action_button);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean isAdsDisabled = sharedPreferences.getBoolean(getString(R.string.disable_adMob_key), false);


        if (!isAdsDisabled) {

            FrameLayout layout = v.findViewById(R.id.admob_container);
            AdView adView = new AdView(getActivity());
            adView.setAdSize(AdSize.SMART_BANNER);
            //adView.setAdUnitId(getString(R.string.test_banner_id));
            adView.setAdUnitId(getString(R.string.dashboard_banner_ID));
            layout.addView(adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }

        cost_graph = v.findViewById(R.id.cost_graphView);
        income_graph = v.findViewById(R.id.income_graphView);

        DefaultLabelFormatter formatter = new LabelFormatter(getActivity());
        cost_graph.getGridLabelRenderer().setLabelFormatter(formatter);
        cost_graph.getGridLabelRenderer().setHorizontalLabelsColor(
                ContextCompat.getColor(getContext(), R.color.colorGray));
        income_graph.getGridLabelRenderer().setLabelFormatter(formatter);
        income_graph.getGridLabelRenderer().setHorizontalLabelsColor(
                ContextCompat.getColor(getContext(), R.color.colorGray));
        income_graph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        cost_graph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        cost_graph.getGridLabelRenderer().setHighlightZeroLines(false);
        income_graph.getGridLabelRenderer().setHighlightZeroLines(false);

        income_graph.getViewport().setMinX(6);
        income_graph.getViewport().setMaxX(12);
        income_graph.getViewport().setXAxisBoundsManual(true);

        cost_graph.getViewport().setMinX(6);
        cost_graph.getViewport().setMaxX(12);
        cost_graph.getViewport().setXAxisBoundsManual(true);

        //CardView graphCost = v.findViewById(R.id.cardView3);
        //CardView graphIncome = v.findViewById(R.id.cardGraphIncome);

        cost_graph.setOnClickListener(new GraphListener());
        income_graph.setOnClickListener(new GraphListener());


        Toolbar toolbar = v.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(R.string.dashboard);
        }

        upDateActionBar(start_date, end_date);


        view_cost = v.findViewById(R.id.cost_sum_textView);
        view_income = v.findViewById(R.id.income_sum_textView);


        list_category = new ArrayList<>();
        list_sum = new ArrayList<>();

        list_category.add(v.findViewById(R.id.account_name1));
        list_sum.add(v.findViewById(R.id.account_sum1));

        list_category.add(v.findViewById(R.id.account_name2));
        list_sum.add(v.findViewById(R.id.account_sum2));

        list_category.add(v.findViewById(R.id.account_name3));
        list_sum.add(v.findViewById(R.id.account_sum3));

        list_category.add(v.findViewById(R.id.account_name4));
        list_sum.add(v.findViewById(R.id.account_sum4));


        CardView income_card = v.findViewById(R.id.cardView_income);
        income_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment fragment = CategoryFragment.newInstance(MiserContract.TYPE_INCOM, start_date, end_date, currency);
                ft.replace(R.id.activity_fragment, fragment);
                ft.addToBackStack(null);
                ft.setTransition(TRANSIT_FRAGMENT_OPEN);
                ft.commit();

            }
        });
        CardView cost_card = v.findViewById(R.id.cardView_costs);
        cost_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment fragment = CategoryFragment.newInstance(MiserContract.TYPE_COST, start_date, end_date, currency);
                ft.replace(R.id.activity_fragment, fragment);
                ft.addToBackStack(null);
                ft.setTransition(TRANSIT_FRAGMENT_OPEN);
                ft.commit();

            }
        });
        final CardView account_1 = v.findViewById(R.id.cardView4);
        account_1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final int CATEGORY_ID = 0;
                String name = String.valueOf(list_category.get(CATEGORY_ID).getText());
                FragmentManager manager = getFragmentManager();
                DialogFragment fragment = AccountDialog.create(CATEGORY_ID, name);
                fragment.show(manager, ACCOUNT_1);
                return true;
            }
        });
        final CardView account_2 = v.findViewById(R.id.cardView5);
        account_2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final int CATEGORY_ID = 1;
                String name = String.valueOf(list_category.get(CATEGORY_ID).getText());

                FragmentManager manager = getFragmentManager();
                DialogFragment fragment = AccountDialog.create(CATEGORY_ID, name);
                fragment.show(manager, ACCOUNT_2);
                return true;
            }
        });
        final CardView account_3 = v.findViewById(R.id.cardView7);
        account_3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final int CATEGORY_ID = 2;
                String name = String.valueOf(list_category.get(CATEGORY_ID).getText());

                FragmentManager manager = getFragmentManager();
                DialogFragment fragment = AccountDialog.create(CATEGORY_ID, name);
                fragment.show(manager, ACCOUNT_3);
                return true;
            }
        });
        final CardView account_4 = v.findViewById(R.id.cardView6);
        account_4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final int CATEGORY_ID = 3;
                String name = String.valueOf(list_category.get(CATEGORY_ID).getText());

                FragmentManager manager = getFragmentManager();
                DialogFragment fragment = AccountDialog.create(CATEGORY_ID, name);
                fragment.show(manager, ACCOUNT_4);
                return true;
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), AddActivity.class);
                intent.putExtra(TimeUtils.TYPE, TimeUtils.TYPE_COST);
                intent.putExtra(TimeUtils.CATEGORY, TimeUtils.DEFAULT_CATEGORY);
                startActivityForResult(intent, ADD_TYPE);
            }
        });

        final DashboardFragment dashboardFragment = this;

        RadioButton set = v.findViewById(R.id.set_radioButton);
        RadioButton day = v.findViewById(R.id.day_radioButton);
        RadioButton week = v.findViewById(R.id.week_radioButton);
        RadioButton month = v.findViewById(R.id.month_radioButton);


        View.OnClickListener range_button_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.set_radioButton: {
                        Date date = new Date();
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                        long beginDate = sharedPreferences.getLong(getString(R.string.begin_date_for_calendar), date.getTime());
                        long endDate = sharedPreferences.getLong(getString(R.string.end_date_for_calendar), date.getTime());


                        Calendar begin = Calendar.getInstance();
                        begin.setTimeInMillis(beginDate);
                        Calendar end = Calendar.getInstance();
                        end.setTimeInMillis(endDate);
                        DatePickerDialog dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                                dashboardFragment,
                                begin.get(Calendar.YEAR),
                                begin.get(Calendar.MONTH),
                                begin.get(Calendar.DAY_OF_MONTH),
                                end.get(Calendar.YEAR),
                                end.get(Calendar.MONTH),
                                end.get(Calendar.DAY_OF_MONTH)
                        );
                        dpd.setAutoHighlight(true);
                        dpd.vibrate(false);
                        dpd.setStartTitle(getResources().getString(R.string.start_title));
                        dpd.setEndTitle(getResources().getString(R.string.end_title));
                        dpd.show(getActivity().getFragmentManager(), "date_range");
                        break;

                    }
                    case R.id.day_radioButton: {
                        start_date = TimeUtils.getBegin_day();
                        end_date = TimeUtils.getEnd_day();
                        restartLoaderSum();
                        upDateActionBar(start_date, end_date);
                        break;
                    }
                    case R.id.week_radioButton: {
                        start_date = TimeUtils.getBegin_week();
                        end_date = TimeUtils.getEnd_week();
                        restartLoaderSum();
                        upDateActionBar(start_date, end_date);
                        break;
                    }
                    case R.id.month_radioButton: {
                        start_date = TimeUtils.getBegin_month();
                        end_date = TimeUtils.getEnd_month();
                        restartLoaderSum();
                        upDateActionBar(start_date, end_date);
                        break;
                    }
                }
            }
        };

        set.setOnClickListener(range_button_listener);
        week.setOnClickListener(range_button_listener);
        day.setOnClickListener(range_button_listener);
        month.setOnClickListener(range_button_listener);
        month.setChecked(true);

        return v;
    }

    private void restartLoaderSum() {
        if (args == null) args = new Bundle();
        args.clear();
        args.putLong(TimeUtils.DATE_FROM, start_date);
        args.putLong(TimeUtils.DATE_TO, end_date);
        getLoaderManager().restartLoader(DATA_SUM_COST, args, this);
        getLoaderManager().restartLoader(DATA_SUM_INCOM, args, this);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(TimeUtils.DATE_FROM, start_date);
        outState.putLong(TimeUtils.DATE_TO, end_date);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_activity, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_settings:
                Intent intent = new Intent(getActivity(), NameActivity.class);
                startActivity(intent);
                return true;

            case R.id.menu_item_currency:
                FragmentManager manager = this.getFragmentManager();
                DialogFragment dialog = new CurrencyDialog();
                dialog.show(manager, DIALOG_CURRENCY);
                return true;

            case R.id.menu_item_info:
                Intent intent_m = new Intent(getActivity(), HelpActivity.class);
                startActivity(intent_m);
                return true;
            case R.id.menu_item_rating:
                onClickRateThisApp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {

        Date begin_date_range = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime();
        Date end_date_range = new GregorianCalendar(yearEnd, monthOfYearEnd, dayOfMonthEnd).getTime();


        start_date = begin_date_range.getTime();
        end_date = end_date_range.getTime();
        restartLoaderSum();
        upDateActionBar(start_date, end_date);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(getString(R.string.begin_date_for_calendar), start_date);
        editor.putLong(getString(R.string.end_date_for_calendar), end_date);
        editor.apply();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri uri = null;
        String[] projection = null;
        String selection = null;
        String sortOrder = null;
        String[] selectionArg = null;
        Long begin;
        Long end;

        switch (id) {
            case DATA_SUM_COST: {
                if (args != null) {
                    begin = args.getLong(TimeUtils.DATE_FROM);
                    end = args.getLong(TimeUtils.DATE_TO);
                } else {
                    begin = 0L;
                    end = 0L;
                }
                uri = MiserContract.DataTable.DATA_URI;
                projection = new String[]{"SUM" + "(" + MiserContract.DataTable.Cols.AMOUNT + ")"};
                selection = MiserContract.DataTable.Cols.DATE + " >= ? AND " +
                        MiserContract.DataTable.Cols.DATE + " < ? AND " +
                        MiserContract.DataTable.Cols.TYPE +
                        " = ?";
                selectionArg = new String[]{Long.toString(begin), Long.toString(end), Integer.toString(MiserContract.TYPE_COST)};
                break;

            }
            case DATA_SUM_INCOM: {
                if (args != null) {
                    begin = args.getLong(TimeUtils.DATE_FROM);
                    end = args.getLong(TimeUtils.DATE_TO);
                } else {
                    begin = 0L;
                    end = 0L;
                }
                uri = MiserContract.DataTable.DATA_URI;
                projection = new String[]{"SUM" + "(" + MiserContract.DataTable.Cols.AMOUNT + ")"};
                selection = MiserContract.DataTable.Cols.DATE + " >= ? AND " +
                        MiserContract.DataTable.Cols.DATE +
                        " < ? AND " + MiserContract.DataTable.Cols.TYPE +
                        " = ?";
                selectionArg = new String[]{Long.toString(begin), Long.toString(end), Integer.toString(MiserContract.TYPE_INCOM)};
                break;
            }

            case ACCOUNT_AMOUNT: {
                uri = MiserContract.AccountTable.ACCOUNTS_URI;
                projection = new String[]{MiserContract.AccountTable.Cols.ACCOUNT_AMOUNT};
                selection = MiserContract.AccountTable.Cols.CATEGORY_ID + " < ?";
                selectionArg = new String[]{Integer.toString(TimeUtils.MAX_ACCOUNTS)};
                break;
            }
            case ACCOUNT_NAME: {
                uri = MiserContract.CategoryTable.CATEGORIES_URI;
                projection = new String[]{MiserContract.CategoryTable.Cols.CATEGORY_NAME, MiserContract.CategoryTable.Cols.SYSTEM_CURRENCY};
                selection = MiserContract.CategoryTable.Cols.TYPE +
                        " = ? AND " + MiserContract.CategoryTable.Cols.CATEGORY_ID +
                        " < ?";
                selectionArg = new String[]{Integer.toString(MiserContract.TYPE_ACCOUNTS), Integer.toString(
                        TimeUtils.MAX_ACCOUNTS)};
                break;
            }
        }
        return new CursorLoader(getContext(), uri, projection, selection, selectionArg, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Float sum;
        final int SUM = 0;
        final int NAME = 0;
        final int CURRENCY = 1;
        final float NO_AMOUNT = 0F;


        switch (loader.getId()) {

            case DATA_SUM_COST: {

                //swapCursor(costCursor, data);
                costCursor = data;

                if (data.moveToFirst()) {
                    sum = data.getFloat(SUM);
                    view_cost.setText(numberFormat.format(sum));
                    Log.i(LOG_TAG, "SUM_COST - view OK");
                } else {
                    view_cost.setText(numberFormat.format(NO_AMOUNT));
                }
                break;
            }
            case DATA_SUM_INCOM: {

                //swapCursor(incomeCursor, data);
                incomeCursor = data;

                if (data.moveToFirst()) {
                    sum = data.getFloat(SUM);
                    view_income.setText(numberFormat.format(sum));
                    Log.i(LOG_TAG, "SUM_INCOME - view OK");
                } else {
                    view_income.setText(numberFormat.format(NO_AMOUNT));
                }
                break;
            }

            case ACCOUNT_AMOUNT: {

                //swapCursor(accountSumCursor, data);
                accountSumCursor = data;

                data.moveToFirst();
                for (int i = 0; i < TimeUtils.MAX_ACCOUNTS; i++) {
                    list_sum.get(i).setText(numberFormat.format(data.getDouble(SUM)));
                    data.moveToNext();
                }
                Log.i(LOG_TAG, "ACCOUNT_AMOUNT - view OK");
                break;
            }
            case ACCOUNT_NAME: {

                //swapCursor(accountNameCursor, data);
                accountNameCursor = data;

                data.moveToFirst();
                currency = data.getString(CURRENCY);
                for (int i = 0; i < TimeUtils.MAX_ACCOUNTS; i++) {
                    list_category.get(i).setText(data.getString(NAME));
                    data.moveToNext();
                }
                Log.i(LOG_TAG, "ACCOUNT_NAME - view OK");
                upDateActionBar(start_date, end_date);
                break;
            }
            default: {
                data.close();
                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.i(LOG_TAG, Integer.toString(loader.getId()) + " RESET");

        switch (loader.getId()) {
            case DATA_SUM_COST: {
                //swapCursor(costCursor, null);
                costCursor = null;
            }
            case DATA_SUM_INCOM: {
                //swapCursor(incomeCursor, null);
                incomeCursor = null;
            }
            case ACCOUNT_AMOUNT: {
                //swapCursor(accountSumCursor, null);
                accountSumCursor = null;
            }
            case ACCOUNT_NAME: {
                //swapCursor(accountNameCursor, null);
                accountNameCursor = null;
            }
        }
    }

    private void swapCursor(Cursor oldCursor, Cursor data) {
        if (oldCursor != null && oldCursor != data) oldCursor.close();
    }


    private void upDateActionBar(Long start_date, Long end_date) {

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar == null) return;
        actionBar.setSubtitle(simpleDateFormat.format(new Date(start_date)) +
                " - " + simpleDateFormat.format(new Date(end_date)) + "   " + currency);
    }


    private class GraphLoaderCallBack implements android.support.v4.app.LoaderManager.LoaderCallbacks<BarGraphSeries<DataPoint>> {

        @Override
        public Loader<BarGraphSeries<DataPoint>> onCreateLoader(int id, Bundle args) {

            int type = 0;
            switch (id) {
                case BARGRAPH_DATA_SET_COST: {
                    type = MiserContract.TYPE_COST;
                    break;
                }
                case BARGRAPH_DATA_SET_INCOME: {
                    type = MiserContract.TYPE_INCOM;
                    break;
                }
            }

            return new GraphLoader(getContext(), type, MiserContract.DataTable.DATA_URI);
        }

        @Override
        public void onLoadFinished(Loader<BarGraphSeries<DataPoint>> loader, BarGraphSeries<DataPoint> series) {

            int id = loader.getId();

            switch (id) {

                case BARGRAPH_DATA_SET_COST: {
                    cost_graph.removeAllSeries();
                    series.setSpacing(5);
                    series.setAnimated(true);
                    series.setColor(ContextCompat.getColor(getContext(), R.color.colorSeries));
                    cost_graph.addSeries(series);
                    break;
                }

                case BARGRAPH_DATA_SET_INCOME: {
                    income_graph.removeAllSeries();
                    series.setSpacing(5);
                    series.setAnimated(true);
                    series.setColor(ContextCompat.getColor(getContext(), R.color.colorSeries));
                    income_graph.addSeries(series);
                    break;
                }
            }
        }


        @Override
        public void onLoaderReset(Loader<BarGraphSeries<DataPoint>> loader) {
            cost_graph.removeAllSeries();
            income_graph.removeAllSeries();
        }
    }

    private class GraphListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            Intent intent = new Intent(getActivity(), GraphActivity.class);

            if (view.getId() == R.id.income_graphView) {
                intent.putExtra(TimeUtils.TYPE, MiserContract.TYPE_INCOM);
            } else {
                intent.putExtra(TimeUtils.TYPE, MiserContract.TYPE_COST);
            }
            startActivity(intent);
        }
    }
}


