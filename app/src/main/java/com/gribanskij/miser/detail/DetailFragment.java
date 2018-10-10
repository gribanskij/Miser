package com.gribanskij.miser.detail;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.gribanskij.miser.R;
import com.gribanskij.miser.add.AddActivity;
import com.gribanskij.miser.asynctask.AsyncQueryTask;
import com.gribanskij.miser.categories.CategoryFragment;
import com.gribanskij.miser.edit_screen.EditActivity;
import com.gribanskij.miser.edit_screen.EditFragment;
import com.gribanskij.miser.sql_base.MiserContract;
import com.gribanskij.miser.utils.AbstractActivity;
import com.gribanskij.miser.utils.TimeUtils;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class DetailFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {


    public static final String LOG_TAG = CategoryFragment.class.getSimpleName();
    public static final int CATEGORY_DATA_LOADER = 12;
    public static final int RESERVE = 20;
    private static final int ADD_TYPE = 2;
    private static final String DIALOG_DELETE = "delete";
    private static final int TOKEN_DELETE = 110;
    private static final String ID = "id";
    private Bundle args;
    private int mCategory;
    private int mType;
    private long mDateFrom;
    private long mDateTo;
    private String currency;
    private String categoryName;
    private NumberFormat numberFormat;
    private SimpleDateFormat simpleDateFormat;
    private RecyclerDetailAdapter mAdapter;
    private TextView emptyView;

    private Paint p = new Paint();
    private Snackbar s;

    public DetailFragment() {
    }

    public static DetailFragment newInstance(int param1, int param2, long param3, long param4, String currency, String categoryName) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(TimeUtils.CATEGORY, categoryName);
        args.putInt(TimeUtils.DETAIL, param1);
        args.putInt(TimeUtils.TYPE, param2);
        args.putLong(TimeUtils.DATE_FROM, param3);
        args.putLong(TimeUtils.DATE_TO, param4);
        args.putString(TimeUtils.CURRENCY, currency);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);

        if (getArguments() != null) {
            mCategory = getArguments().getInt(TimeUtils.DETAIL);
            mType = getArguments().getInt(TimeUtils.TYPE);
            mDateFrom = getArguments().getLong(TimeUtils.DATE_FROM);
            mDateTo = getArguments().getLong(TimeUtils.DATE_TO);
            currency = getArguments().getString(TimeUtils.CURRENCY);
            categoryName = getArguments().getString(TimeUtils.CATEGORY);
        }
        if (savedInstanceState != null) {
            mCategory = savedInstanceState.getInt(TimeUtils.DETAIL);
            mType = savedInstanceState.getInt(TimeUtils.TYPE);
            mDateFrom = savedInstanceState.getLong(TimeUtils.DATE_FROM);
            mDateTo = savedInstanceState.getLong(TimeUtils.DATE_TO);
            currency = savedInstanceState.getString(TimeUtils.CURRENCY);
            categoryName = savedInstanceState.getString(TimeUtils.CATEGORY);
        }

        numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        numberFormat.setMaximumFractionDigits(2);
        simpleDateFormat = new SimpleDateFormat("d MMM", Locale.getDefault());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.detail_fragment, container, false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean isAdsDisabled = sharedPreferences.getBoolean(getString(R.string.disable_adMob_key), false);

        if (!isAdsDisabled) {
            FrameLayout ad_container = view.findViewById(R.id.detail_adView);
            AdView adView = new AdView(getActivity());
            adView.setAdSize(AdSize.SMART_BANNER);
            adView.setAdUnitId(getString(R.string.dashboard_banner_ID));
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
            ad_container.addView(adView);
        }

        Toolbar toolbar = view.findViewById(R.id.toolbar_detail);
        emptyView = view.findViewById(R.id.emptyView);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(categoryName);
        activity.getSupportActionBar().setSubtitle(simpleDateFormat.format(new Date(mDateFrom)) + " - " + simpleDateFormat.format(new Date(mDateTo)) + "   " + currency);


        RecyclerView mRecyclerView = view.findViewById(R.id.detail_recycler);
        mAdapter = new RecyclerDetailAdapter(null);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {

                Bundle transaction = (Bundle) viewHolder.itemView.getTag();
                if (swipeDir == ItemTouchHelper.LEFT) {
                    s = Snackbar.make(viewHolder.itemView, R.string.remove_action, Snackbar.LENGTH_LONG);
                    s.setAction(R.string.undo_action, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                            s.dismiss();
                        }
                    });
                    s.addCallback(new SnackBarListener(
                            transaction.getLong(EditFragment._ID)));
                    s.show();

                } else {
                    mAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                    Intent intent = new Intent(getActivity(), EditActivity.class);
                    intent.putExtra(AbstractActivity.TRANSACTION, transaction);
                    startActivity(intent);
                }

            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        p.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_mode_edit_white_24dp);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white_24dp);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(mRecyclerView);


        FloatingActionButton floatingActionButton = view.findViewById(R.id.fab_detail);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddActivity.class);
                intent.putExtra(TimeUtils.TYPE, mType);
                intent.putExtra(TimeUtils.CATEGORY, mCategory);
                startActivityForResult(intent, ADD_TYPE);
            }
        });
        return view;
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
            args.putInt(TimeUtils.DETAIL, savedInstanceState.getInt(TimeUtils.DETAIL));
        } else {
            args.putLong(TimeUtils.DATE_FROM, mDateFrom);
            args.putLong(TimeUtils.DATE_TO, mDateTo);
            args.putInt(TimeUtils.TYPE, mType);
            args.putInt(TimeUtils.DETAIL, mCategory);
        }
        getLoaderManager().initLoader(CATEGORY_DATA_LOADER, args, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //inflater.inflate(R.menu.menu_category, menu);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(TimeUtils.DATE_FROM, mDateFrom);
        outState.putLong(TimeUtils.DATE_TO, mDateTo);
        outState.putInt(TimeUtils.DETAIL, mCategory);
        outState.putInt(TimeUtils.TYPE, mType);
        outState.putString(TimeUtils.CURRENCY, currency);
        outState.putString(TimeUtils.CATEGORY, categoryName);
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
        int category;

        switch (id) {

            case CATEGORY_DATA_LOADER: {
                uri = MiserContract.DataTable.DATA_URI;
                if (args != null) {
                    begin = args.getLong(TimeUtils.DATE_FROM);
                    end = args.getLong(TimeUtils.DATE_TO);
                    dataType = args.getInt(TimeUtils.TYPE);
                    category = args.getInt(TimeUtils.DETAIL);
                } else {
                    begin = 0L;
                    end = 0L;
                    dataType = MiserContract.TYPE_COST;
                    category = 0;
                }
                projection = new String[]{MiserContract.DataTable.Cols._ID, MiserContract.DataTable.Cols.DESCRIPTION,
                        MiserContract.DataTable.Cols.AMOUNT, MiserContract.DataTable.Cols.DATE, MiserContract.DataTable.Cols.TYPE,
                        MiserContract.DataTable.Cols.CATEGORY_ID};
                selection = MiserContract.DataTable.Cols.DATE + " >= ? AND " + MiserContract.DataTable.Cols.DATE +
                        " < ? AND " + MiserContract.DataTable.Cols.CATEGORY_ID +
                        " = ? AND " + MiserContract.DataTable.Cols.TYPE + " = ? ";
                selectionArg = new String[]{Long.toString(begin), Long.toString(end), Integer.toString(category),
                        Integer.toString(dataType)};
                sortOrder = MiserContract.DataTable.Cols.DATE + " DESC";
                break;
            }
            case RESERVE: {
                break;
            }
        }
        return new CursorLoader(getContext(), uri, projection, selection, selectionArg, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch (loader.getId()) {
            case CATEGORY_DATA_LOADER: {
                mAdapter.swapCursor(data);

                if (data != null && data.moveToFirst())
                    emptyView.setVisibility(View.GONE);
                else
                    emptyView.setVisibility(View.VISIBLE);

                Log.i(LOG_TAG, "Cursor added to adapter");
                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.i(LOG_TAG, Integer.toString(loader.getId()) + " cursor was closed");
        mAdapter.swapCursor(null);
    }


    private class RecyclerDetailAdapter extends RecyclerView.Adapter<DetailHolder> {

        private static final int _ID = 0;
        private final int DESCRIPTION = 1;
        private final int AMOUNT = 2;
        private final int DATE = 3;
        private final int TYPE = 4;
        private final int CATEGORY_ID = 5;
        private Cursor cursor;

        RecyclerDetailAdapter(Cursor cursor) {
            this.cursor = cursor;
            setHasStableIds(true);
        }

        public Cursor getCursor() {
            return cursor;
        }


        void swapCursor(Cursor newCursor) {

            if (cursor != newCursor) {
                cursor = newCursor;
                notifyDataSetChanged();
            }
        }

        @Override
        public DetailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View vhView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder, parent, false);
            return new DetailHolder(vhView);
        }

        @Override
        public void onBindViewHolder(DetailHolder holder, int position) {

            if (cursor.moveToPosition(position)) {
                holder.mDescription.setText(cursor.getString(DESCRIPTION));
                holder.mSum.setText(numberFormat.format(cursor.getFloat(AMOUNT)));
                holder.mDate.setText(DateFormat.getDateInstance().format(cursor.getLong(DATE)));
                holder.mid = cursor.getLong(_ID);

                Bundle transaction = new Bundle();

                transaction.putInt(EditFragment.CATEGORY, cursor.getInt(CATEGORY_ID));
                transaction.putInt(EditFragment._TYPE, cursor.getInt(TYPE));
                transaction.putLong(EditFragment._ID, cursor.getLong(_ID));
                transaction.putLong(EditFragment.DATE, cursor.getLong(DATE));
                transaction.putFloat(EditFragment.SUM, cursor.getFloat(AMOUNT));
                transaction.putString(EditFragment.DESCRIPTION, cursor.getString(DESCRIPTION));
                holder.itemView.setTag(transaction);

            } else {
                Log.i(LOG_TAG, "ERROR in Adapter, no position");
            }
        }

        @Override
        public int getItemCount() {
            if (cursor == null) return 0;
            return cursor.getCount();
        }

        @Override
        public long getItemId(int position) {
            if (cursor != null && cursor.moveToPosition(position)) {
                return cursor.getLong(_ID);
            }
            return RecyclerView.NO_ID;
        }
    }


    private class DetailHolder extends RecyclerView.ViewHolder {

        TextView mDescription;
        TextView mSum;
        TextView mDate;
        long mid;

        private DetailHolder(View view) {
            super(view);
            mDescription = view.findViewById(R.id.vh_description);
            mSum = view.findViewById(R.id.vh_sum);
            mDate = view.findViewById(R.id.vh_date);
        }

    }

    private class SnackBarListener extends BaseTransientBottomBar.BaseCallback<Snackbar> {

        private long id;

        private SnackBarListener(long id) {
            this.id = id;
        }

        @Override
        public void onDismissed(Snackbar transientBottomBar, int event) {

            if (event == DISMISS_EVENT_SWIPE || event == DISMISS_EVENT_TIMEOUT) {

                Uri uri = MiserContract.DataTable.DATA_URI;
                String selection = "_id=?";
                String[] arg = new String[]{Long.toString(id)};

                AsyncQueryTask mAsyncTask = new AsyncQueryTask(getContext().getContentResolver(), getContext());
                mAsyncTask.startDelete(TOKEN_DELETE, null, uri, selection, arg);

            }
        }
    }
}
