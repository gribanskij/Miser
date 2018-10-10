package com.gribanskij.miser.categories;

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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.gribanskij.miser.R;
import com.gribanskij.miser.add.AddActivity;
import com.gribanskij.miser.dashboard.CurrencyDialog;
import com.gribanskij.miser.dashboard.HelpActivity;
import com.gribanskij.miser.dashboard.NameActivity;
import com.gribanskij.miser.detail.DetailFragment;
import com.gribanskij.miser.sql_base.MiserContract;
import com.gribanskij.miser.utils.TimeUtils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import static android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN;


public class CategoryFragment extends Fragment implements DatePickerDialog.OnDateSetListener, android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {


    private static final String LOG_TAG = CategoryFragment.class.getSimpleName();
    private static final int ADD_TYPE = 1;
    private static final int CATEGORY_NAME_LOADER = 11;
    private static final int CATEGORY_SUM_LOADER = 21;
    private static final String URL_APP = "https://play.google.com/store/apps/details?id=com.gribanskij.miser";
    private static final String URI_APP = "market://details?id=com.gribanskij.miser";
    private static final String DIALOG_CURRENCY = "currency";
    private Bundle args;
    private int type_view;
    private List<Categories> mCategories;
    private long from_date;
    private long to_date;
    private RecycleAdapter adapter;
    private NumberFormat numberFormat;
    private SimpleDateFormat simpleDateFormat;
    private String currency;
    private PieChart structure_graph;
    private int[] colors = new int[TimeUtils.MAX_COST_CATEGORIES];
    private List<PieEntry> entries;


    public CategoryFragment() {
    }

    public static CategoryFragment newInstance(int type, long param1, long param2, String currency) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putInt(TimeUtils.TYPE, type);
        args.putLong(TimeUtils.DATE_FROM, param1);
        args.putLong(TimeUtils.DATE_TO, param2);
        args.putString(TimeUtils.CURRENCY, currency);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        colors[0] = ContextCompat.getColor(getActivity(), R.color.colorRed);
        colors[1] = ContextCompat.getColor(getActivity(), R.color.colorGreen);
        colors[2] = ContextCompat.getColor(getActivity(), R.color.colorDeepPurple);
        colors[3] = ContextCompat.getColor(getActivity(), R.color.colorCyan);
        colors[4] = ContextCompat.getColor(getActivity(), R.color.colorPink);
        colors[5] = ContextCompat.getColor(getActivity(), R.color.colorIndigo);
        colors[6] = ContextCompat.getColor(getActivity(), R.color.colorTeal);
        colors[7] = ContextCompat.getColor(getActivity(), R.color.colorPurple);
        colors[8] = ContextCompat.getColor(getActivity(), R.color.colorLime);
        colors[9] = ContextCompat.getColor(getActivity(), R.color.colorYellow);
        colors[10] = ContextCompat.getColor(getActivity(), R.color.colorOrange);
        colors[11] = ContextCompat.getColor(getActivity(), R.color.colorBrown);
        colors[12] = ContextCompat.getColor(getActivity(), R.color.colorBlueGray);
        colors[13] = ContextCompat.getColor(getActivity(), R.color.colorDeepOrange);

        entries = new ArrayList<>();
        for (int i = 0; i < TimeUtils.MAX_COST_CATEGORIES; i++) {
            entries.add(new PieEntry(0f, ""));
        }


        numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        numberFormat.setMaximumFractionDigits(2);
        simpleDateFormat = new SimpleDateFormat("d MMM", Locale.getDefault());


        from_date = TimeUtils.getBegin_month();
        to_date = TimeUtils.getEnd_month();
        type_view = MiserContract.TYPE_COST;
        currency = "$?";

        if (getArguments() != null) {
            type_view = getArguments().getInt(TimeUtils.TYPE);
            from_date = getArguments().getLong(TimeUtils.DATE_FROM);
            to_date = getArguments().getLong(TimeUtils.DATE_TO);
            currency = getArguments().getString(TimeUtils.CURRENCY);
        }

        if (savedInstanceState != null) {
            type_view = savedInstanceState.getInt(TimeUtils.TYPE);
            from_date = savedInstanceState.getLong(TimeUtils.DATE_FROM);
            to_date = savedInstanceState.getLong(TimeUtils.DATE_TO);
            currency = savedInstanceState.getString(TimeUtils.CURRENCY);
        }

        if (mCategories == null) mCategories = new ArrayList<>();
        mCategories.clear();

        if (type_view == MiserContract.TYPE_COST) {
            for (int i = 0; i < TimeUtils.MAX_COST_CATEGORIES; i++) {
                mCategories.add(new Categories("", 0));
            }
        } else {
            for (int i = 0; i < TimeUtils.MAX_INCOM_CATEGORIES; i++) {
                mCategories.add(new Categories("", 0));
            }
        }
        if (adapter == null) {
            adapter = new RecycleAdapter(mCategories);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (args == null) args = new Bundle();
        args.clear();

        if (savedInstanceState != null) {
            args.putLong(TimeUtils.DATE_FROM, savedInstanceState.getLong(TimeUtils.DATE_FROM));
            args.putLong(TimeUtils.DATE_TO, savedInstanceState.getLong(TimeUtils.DATE_TO));
            args.putInt(TimeUtils.TYPE, savedInstanceState.getInt(TimeUtils.TYPE));
        } else {
            args.putLong(TimeUtils.DATE_FROM, from_date);
            args.putLong(TimeUtils.DATE_TO, to_date);
            args.putInt(TimeUtils.TYPE, type_view);
        }
        getLoaderManager().initLoader(CATEGORY_SUM_LOADER, args, this);
        getLoaderManager().initLoader(CATEGORY_NAME_LOADER, args, this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.category_fragment, container, false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean isAdsDisabled = sharedPreferences.getBoolean(getString(R.string.disable_adMob_key), false);

        if (!isAdsDisabled) {
            FrameLayout layout = v.findViewById(R.id.admob_container);
            AdView adView = new AdView(getActivity());
            adView.setAdSize(AdSize.SMART_BANNER);
            adView.setAdUnitId(getString(R.string.dashboard_banner_ID));
            layout.addView(adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
            FrameLayout buffer = v.findViewById(R.id.frame);
            AdView empty = new AdView(getActivity());
            empty.setAdSize(AdSize.SMART_BANNER);
            buffer.addView(empty);
        }


        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);

        if (type_view == TimeUtils.TYPE_COST) {
            activity.getSupportActionBar().setTitle(getResources().getString(R.string.title_cost));
        } else {
            activity.getSupportActionBar().setTitle(getResources().getString(R.string.title_incom));
        }
        upDateActionBar(from_date, to_date);


        RecyclerView recyclerView = v.findViewById(R.id.category_recycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);


        final CategoryFragment categoryFragment = this;
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
                        DatePickerDialog dpd = DatePickerDialog.newInstance(
                                categoryFragment,
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
                        from_date = TimeUtils.getBegin_day();
                        to_date = TimeUtils.getEnd_day();
                        restartLoader();
                        upDateActionBar(from_date, to_date);
                        break;
                    }
                    case R.id.week_radioButton: {
                        from_date = TimeUtils.getBegin_week();
                        to_date = TimeUtils.getEnd_week();
                        restartLoader();
                        upDateActionBar(from_date, to_date);
                        break;
                    }
                    case R.id.month_radioButton: {
                        from_date = TimeUtils.getBegin_month();
                        to_date = TimeUtils.getEnd_month();
                        restartLoader();
                        upDateActionBar(from_date, to_date);
                        break;
                    }
                }
            }
        };

        set.setOnClickListener(range_button_listener);
        week.setOnClickListener(range_button_listener);
        day.setOnClickListener(range_button_listener);
        month.setOnClickListener(range_button_listener);
        set.setChecked(true);

        FloatingActionButton fab = v.findViewById(R.id.action_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddActivity.class);
                intent.putExtra(TimeUtils.TYPE, type_view);
                intent.putExtra(TimeUtils.CATEGORY, TimeUtils.DEFAULT_CATEGORY);
                startActivityForResult(intent, ADD_TYPE);
            }
        });

        structure_graph = v.findViewById(R.id.structure_graphView);
        PieDataSet sett = new PieDataSet(entries, "");
        sett.setValueTextSize(10);
        sett.setValueFormatter(new MyFormatter());
        sett.setColors(colors);
        sett.setValueTextColor(ContextCompat.getColor(getActivity(), R.color.colorGrayGraph));
        PieData dataa = new PieData(sett);
        structure_graph.setData(dataa);
        structure_graph.getLegend().setEnabled(false);
        structure_graph.setCenterTextColor(ContextCompat.getColor(getActivity(), R.color.colorGrayGraph));
        structure_graph.getDescription().setText("");
        structure_graph.setUsePercentValues(true);
        structure_graph.setDrawCenterText(true);
        structure_graph.setDrawEntryLabels(false);
        structure_graph.setTransparentCircleRadius(50);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_category, menu);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TimeUtils.TYPE, type_view);
        outState.putLong(TimeUtils.DATE_FROM, from_date);
        outState.putLong(TimeUtils.DATE_TO, to_date);
        outState.putString(TimeUtils.CURRENCY, currency);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {

        Date begin_date_range = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime();
        Date end_date_range = new GregorianCalendar(yearEnd, monthOfYearEnd, dayOfMonthEnd).getTime();
        from_date = begin_date_range.getTime();
        to_date = end_date_range.getTime();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(getString(R.string.begin_date_for_calendar), from_date);
        editor.putLong(getString(R.string.end_date_for_calendar), to_date);
        editor.apply();

        restartLoader();
        upDateActionBar(from_date, to_date);
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
        int dataType;


        switch (id) {

            case CATEGORY_SUM_LOADER: {
                uri = MiserContract.DataTable.DATA_CATEGORY_SUM_URI;
                if (args != null) {
                    begin = args.getLong(TimeUtils.DATE_FROM);
                    end = args.getLong(TimeUtils.DATE_TO);
                    dataType = args.getInt(TimeUtils.TYPE);
                } else {
                    begin = 0L;
                    end = 0L;
                    dataType = MiserContract.TYPE_COST;
                }
                projection = new String[]{"SUM" + "(" + MiserContract.DataTable.Cols.AMOUNT + ")", MiserContract.DataTable.Cols.CATEGORY_ID};
                selection = MiserContract.DataTable.Cols.DATE +
                        " >= ? AND " + MiserContract.DataTable.Cols.DATE +
                        " < ? AND " + MiserContract.DataTable.Cols.TYPE +
                        " = ?";
                selectionArg = new String[]{Long.toString(begin),
                        Long.toString(end), Integer.toString(dataType)};
                sortOrder = MiserContract.DataTable.Cols.CATEGORY_ID + " ASC";
                break;
            }
            case CATEGORY_NAME_LOADER: {

                uri = MiserContract.CategoryTable.CATEGORIES_URI;

                if (args != null) dataType = args.getInt(TimeUtils.TYPE);
                else dataType = MiserContract.TYPE_COST;

                projection = new String[]{MiserContract.CategoryTable.Cols.CATEGORY_NAME, MiserContract.CategoryTable.Cols.CATEGORY_ID};
                selection = MiserContract.CategoryTable.Cols.TYPE + " = ?";
                selectionArg = new String[]{Integer.toString(dataType)};
                sortOrder = MiserContract.CategoryTable.Cols.CATEGORY_ID + " ASC";
                break;
            }
        }

        return new CursorLoader(getContext(), uri, projection, selection, selectionArg, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        final int SUM = 0;
        final int NAME = 0;
        final int CATEGORY_ID = 1;
        final float DEFAULT_SUM = 0F;
        final String DEFAULT_NAME = "?";
        float categories_sum = 0;
        String category_sum_graph;


        switch (loader.getId()) {
            case CATEGORY_SUM_LOADER: {

                for (PieEntry entry : entries) {
                    entry.setY(categories_sum);
                }

                for (Categories category : mCategories) {
                    category.setCategory_sum(DEFAULT_SUM);
                }

                if (data.moveToFirst())

                    do {
                        Categories category = mCategories.get(data.getInt(CATEGORY_ID));
                        category.setCategory_sum(data.getFloat(SUM));
                        PieEntry entry = entries.get(data.getInt(CATEGORY_ID));
                        entry.setY(data.getFloat(SUM));
                        categories_sum = categories_sum + data.getFloat(SUM);
                    } while (data.moveToNext());

                adapter.notifyDataSetChanged();

                if (categories_sum == 0) {
                    category_sum_graph = getString(R.string.no_data_graph);
                } else {
                    category_sum_graph = numberFormat.format(categories_sum);
                }

                structure_graph.setCenterText(category_sum_graph);
                structure_graph.notifyDataSetChanged();
                structure_graph.animateY(3000, Easing.EasingOption.EaseOutBack);
                structure_graph.invalidate();
                break;
            }

            case CATEGORY_NAME_LOADER: {

                for (Categories category : mCategories) {
                    category.setCategory_name(DEFAULT_NAME);
                }

                if (data.moveToFirst())
                    do {
                        Categories category = mCategories.get(data.getInt(CATEGORY_ID));
                        category.setCategory_name(data.getString(NAME));
                    } while (data.moveToNext());

                adapter.notifyDataSetChanged();
                break;
            }
            default: {
                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.i(LOG_TAG, Integer.toString(loader.getId()) + " RESET");
        switch (loader.getId()) {
            case CATEGORY_NAME_LOADER: {
                //swapCursor(nameCursor, null);
                break;
            }
            case CATEGORY_SUM_LOADER: {
                //swapCursor(sumCursor, null);
                break;
            }
            default: {
                break;
            }
        }
    }

    private void restartLoader() {
        if (args == null) args = new Bundle();
        args.clear();
        args.putLong(TimeUtils.DATE_FROM, from_date);
        args.putLong(TimeUtils.DATE_TO, to_date);
        args.putInt(TimeUtils.TYPE, type_view);
        getLoaderManager().restartLoader(CATEGORY_SUM_LOADER, args, this);
    }

    private void upDateActionBar(Long start_date, Long end_date) {

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar == null) return;
        actionBar.setSubtitle(simpleDateFormat.format(new Date(start_date)) + " - " + simpleDateFormat.format(new Date(end_date)) + "   " + currency);
    }

    private class RecycleAdapter extends RecyclerView.Adapter<CategoryHolder> {
        private List<Categories> list;

        private RecycleAdapter(List<Categories> list) {
            super();
            this.list = list;
        }

        @Override
        public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CardView v = (CardView) LayoutInflater.from(getContext()).inflate(R.layout.view_holder_category, parent, false);
            return new CategoryHolder(v);
        }

        @Override
        public void onBindViewHolder(CategoryHolder holder, int position) {

            Categories category = list.get(position);
            holder.mName.setText(category.getCategory_name());
            holder.mSum.setText(numberFormat.format(category.getCategory_sum()));
            holder.mColor.setBackgroundColor(colors[position]);
            holder.category = position;
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    private class CategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mName;
        TextView mSum;
        View mColor;
        int category;

        private CategoryHolder(CardView view) {
            super(view);
            mName = view.findViewById(R.id.category_name);
            mSum = view.findViewById(R.id.category_amount);
            mColor = view.findViewById(R.id.color_view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String categoryName = mName.getText().toString();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment fragment = DetailFragment.newInstance(category, type_view, from_date, to_date, currency, categoryName);
            ft.replace(R.id.activity_fragment, fragment);
            ft.addToBackStack(null);
            ft.setTransition(TRANSIT_FRAGMENT_OPEN);
            ft.commit();

        }
    }
}
