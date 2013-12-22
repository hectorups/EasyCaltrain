package com.easycaltrain.ui;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.easycaltrain.BaseFragment;
import com.easycaltrain.R;
import com.easycaltrain.loader.StopTimesLoader;
import com.easycaltrain.model.CSVManager;
import com.easycaltrain.model.CupboardSQLiteOpenHelper;
import com.easycaltrain.model.StopTime;


import javax.inject.Inject;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class NextStopsFragment extends BaseFragment {
    private static final String TAG = "NextStopsFragment";

    private static final int LOAD_STOPTIMES = 0;
    public static final String ARG_FROM_STOP = "com.easycaltrain.fromStop";
    public static final String ARG_TO_STOP = "com.easycaltrain.toStop";

    @InjectView(android.R.id.list) ListView mStopTimeList;
    @InjectView(android.R.id.empty) TextView mStopTimeListEmpty;
    @Inject CSVManager mCSVManager;

    private CupboardSQLiteOpenHelper mSqlHelper;
    private String mFromStopId;
    private String mToStopId;

    private StopTimesLoaderCallbacks mStopTimesLoaderCallbacks;

    public static NextStopsFragment newInstance(String fromStop, String toStop) {
        Bundle args = new Bundle();
        args.putString(ARG_FROM_STOP, fromStop);
        args.putString(ARG_TO_STOP, toStop);
        NextStopsFragment nsf = new NextStopsFragment();
        nsf.setArguments(args);
        return nsf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        Bundle args = getArguments();
        mFromStopId = args.getString(ARG_FROM_STOP);
        mToStopId = args.getString(ARG_TO_STOP);
        mSqlHelper = new CupboardSQLiteOpenHelper(getActivity(), mCSVManager);

        LoaderManager lm = getLoaderManager();
        mStopTimesLoaderCallbacks = new StopTimesLoaderCallbacks();
        lm.initLoader(LOAD_STOPTIMES, null, mStopTimesLoaderCallbacks);
    }

    @Override
    public void onResume () {
        getLoaderManager().restartLoader(LOAD_STOPTIMES, null, mStopTimesLoaderCallbacks);
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_next_stops, container, false);
        ButterKnife.inject(this, rootView);

        return rootView;
    }

    private void updateUI(Cursor cursor){

        mStopTimeList.setAdapter(new StopTimeCursorAdapter(getActivity(), cursor));

        mStopTimeListEmpty.setText( String.format(getResources().getString(R.string.empty_stop_time_list), mToStopId ) );
        mStopTimeList.setEmptyView(mStopTimeListEmpty);
    }

    private static class StopTimeCursorAdapter extends CursorAdapter {

        private Cursor mRunCursor;

        public StopTimeCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
            mRunCursor = cursor;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            // use a layout inflater to get a row view
            LayoutInflater inflater =
                    (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // get the run for the current row
            StopTime stopTime = cupboard().withCursor(mRunCursor).get(StopTime.class);

            // set up the start date text view
            TextView stopTimeView = (TextView)view;
            stopTimeView.setText(stopTime.getTripId() + " at " + stopTime.getArrivalTime());
        }

    }

    private class StopTimesLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            StopTimesLoader stopTimesLoader = new StopTimesLoader(getActivity(), mFromStopId, mToStopId);
            inject(stopTimesLoader);
            return stopTimesLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor stopTimes) {
            updateUI(stopTimes);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {}
    };
}
