package com.gribanskij.miser.graph_screen;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gribanskij.miser.R;
import com.gribanskij.miser.sql_base.MiserContract;
import com.gribanskij.miser.utils.GraphLoader;
import com.gribanskij.miser.utils.LabelFormatter;
import com.gribanskij.miser.utils.TimeUtils;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;


/**
 * Created by santy on 07.10.2017.
 */

public class GraphFragment extends Fragment implements LoaderManager.LoaderCallbacks<BarGraphSeries<DataPoint>> {


    private static final int GRAPH_LOADER = 70;

    private int mType;
    private GraphView graphView;

    public GraphFragment() {
    }


    public static GraphFragment newInstance(int type) {
        GraphFragment fragment = new GraphFragment();
        Bundle args = new Bundle();
        args.putInt(TimeUtils.TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mType = savedInstanceState.getInt(TimeUtils.TYPE);
        } else {
            if (getArguments() != null) {
                mType = getArguments().getInt(TimeUtils.TYPE);
            }
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.graph_screen_main, container, false);

        graphView = v.findViewById(R.id.graphView_screen);
        DefaultLabelFormatter formatter = new LabelFormatter(getActivity());
        graphView.getGridLabelRenderer().setLabelFormatter(formatter);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMaxX(6);
        graphView.getGridLabelRenderer().setHighlightZeroLines(false);
        graphView.getGridLabelRenderer().setHorizontalLabelsColor(ContextCompat.getColor(getContext(), R.color.colorGray));
        graphView.getGridLabelRenderer().setVerticalLabelsColor(ContextCompat.getColor(getContext(), R.color.colorGray));
        graphView.getLegendRenderer().setVisible(true);
        graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(GRAPH_LOADER, null, this);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TimeUtils.TYPE, mType);
    }

    @Override
    public Loader<BarGraphSeries<DataPoint>> onCreateLoader(int id, Bundle args) {

        return new GraphLoader(getContext(), mType, MiserContract.DataTable.DATA_URI);
    }

    @Override
    public void onLoadFinished(Loader<BarGraphSeries<DataPoint>> loader, BarGraphSeries<DataPoint> data) {

        int id = loader.getId();

        switch (id) {
            case GRAPH_LOADER: {
                graphView.removeAllSeries();
                data.setOnDataPointTapListener(new DataSeriesListener());
                if (mType == MiserContract.TYPE_COST) {
                    data.setTitle(getString(R.string.cost_6));
                } else {
                    data.setTitle(getString(R.string.income_6));
                }
                graphView.addSeries(data);
                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<BarGraphSeries<DataPoint>> loader) {
        graphView.removeAllSeries();
    }

    private class DataSeriesListener implements OnDataPointTapListener {

        @Override
        public void onTap(Series series, DataPointInterface dataPoint) {
            Toast.makeText(getActivity(), Double.toString(dataPoint.getY()), Toast.LENGTH_SHORT).show();
        }
    }

}
