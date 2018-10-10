package com.gribanskij.miser.categories;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.os.CancellationSignal;
import android.support.v4.os.OperationCanceledException;

import com.gribanskij.miser.sql_base.MiserContract;
import com.gribanskij.miser.utils.TimeUtils;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

/**
 * Created by SESA175711 on 31.10.2017.
 */

public class GraphLoaderCategory extends AsyncTaskLoader<BarGraphSeries<DataPoint>> {

    private final ForceLoadContentObserver mObserver;
    private Context context;
    private int type;
    private Cursor mCursor;
    private long from_date;
    private long to_date;
    private BarGraphSeries<DataPoint> barGraphSeries;
    private Uri uri;
    private CancellationSignal mCancellationSignal;


    public GraphLoaderCategory(Context context, int type, long from_date, long to_date, Uri uri) {
        super(context);
        this.context = context;
        this.type = type;
        this.uri = uri;
        this.from_date = from_date;
        this.to_date = to_date;
        mObserver = new ForceLoadContentObserver();
    }


    @Override
    protected void onStartLoading() {
        if (barGraphSeries != null) {
            deliverResult(barGraphSeries);
        }

        if (takeContentChanged() || barGraphSeries == null) {
            forceLoad();
        }

    }

    @Override
    public void cancelLoadInBackground() {
        super.cancelLoadInBackground();
        synchronized (this) {
            if (mCancellationSignal != null) {
                mCancellationSignal.cancel();
            }
        }
    }


    @Override
    public void deliverResult(BarGraphSeries<DataPoint> data) {

        if (isReset()) return;

        if (isStarted()) {
            super.deliverResult(data);
        }

    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(BarGraphSeries<DataPoint> data) {
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();
        if (mCursor != null) {
            mCursor.close();
        }
        onStopLoading();
        barGraphSeries = null;
    }

    @Override
    public BarGraphSeries<DataPoint> loadInBackground() {

        synchronized (this) {
            if (isLoadInBackgroundCanceled()) {
                throw new OperationCanceledException();
            }
            mCancellationSignal = new CancellationSignal();
        }

        try {

            DataPoint[] series = getDataPointSet(type, from_date, to_date);
            barGraphSeries = new BarGraphSeries<>(series);
            return barGraphSeries;

        } finally {
            synchronized (this) {
                mCancellationSignal = null;
            }
        }
    }


    private DataPoint[] getDataPointSet(int type, long from_date, long to_date) {

        DataPoint[] dataPoints = new DataPoint[TimeUtils.MAX_COST_CATEGORIES];
        for (int i = 0; i < TimeUtils.MAX_COST_CATEGORIES; i++) {
            dataPoints[i] = new DataPoint(0, 0);
        }

        String[] projection = new String[]{"SUM" + "(" + MiserContract.DataTable.Cols.AMOUNT + ")"};
        String selection = MiserContract.DataTable.Cols.DATE + " >=  ? AND " +
                MiserContract.DataTable.Cols.DATE + " < ? AND " +
                MiserContract.DataTable.Cols.TYPE + " = ?";
        ContentResolver resolver = context.getContentResolver();

        String[] arg = new String[]{
                Long.toString(0),
                Long.toString(1),
                Integer.toString(type)};

        mCursor = resolver.query(uri, projection, selection, arg, null);
        if (mCursor != null) mCursor.registerContentObserver(mObserver);


        String[] selectionArg = new String[]{
                Long.toString(from_date),
                Long.toString(to_date),
                Integer.toString(type)};
        Cursor cursor = resolver.query(uri, projection, selection, selectionArg, null);

        if (cursor != null && cursor.moveToFirst()) {
            //sum = cursor.getInt(0);
            cursor.close();
        }
        //DataPoint point = new DataPoint(count, sum);
        //dataPoints[count] = point;

        return dataPoints;
    }

}
