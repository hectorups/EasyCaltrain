package com.easycaltrain.ui;

/**
 * Created by hectormonserrate on 11/12/13.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.easycaltrain.BaseFragment;
import com.easycaltrain.R;
import com.easycaltrain.library.Preferences;
import com.easycaltrain.loader.StopsLoader;
import com.easycaltrain.model.CSVManager;
import com.easycaltrain.model.Stop;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends BaseFragment {
    private static final String TAG = "MainFragment";

    private static final int LOAD_STOPS = 0;
    @InjectView(R.id.stopList) ListView mStopList;
    @Inject CSVManager mCSVManager;

    private StopAdapter mStopAdapter;

    private String mFromStopId;

    private StopsLoaderCallbacks mStopLoaderCallbacks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        LoaderManager lm = getLoaderManager();
        mStopLoaderCallbacks = new StopsLoaderCallbacks();
        lm.initLoader(LOAD_STOPS, null, mStopLoaderCallbacks);

        mFromStopId = mPreferences.getLastFromStation();
    }

    @Override
    public void onResume () {
        getLoaderManager().restartLoader(LOAD_STOPS, null, mStopLoaderCallbacks);
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, rootView);

        return rootView;
    }

    private void updateUI(ArrayList<Stop> stops){
        mStopAdapter = new StopAdapter(stops);
        mStopList.setAdapter(mStopAdapter);
        setupListViewListener();
    }

    private void setupListViewListener(){
        mStopList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> aView, View item, int pos, long id) {
                Stop clickedStop = mStopAdapter.getItem(pos);
                String clickedStopId = clickedStop.getStopId();
                Log.d(TAG, "Long click on Stop = " + clickedStopId);

                if( mFromStopId == null ){
                    mFromStopId = clickedStopId;
                } else if( mFromStopId.compareTo(clickedStopId) == 0 ){
                    mFromStopId = null;
                } else {
                    // Show results
                    Intent i = new Intent(getActivity(), NextStopsActivity.class);
                    i.putExtra(NextStopsFragment.ARG_FROM_STOP, mFromStopId);
                    i.putExtra(NextStopsFragment.ARG_TO_STOP, clickedStopId);
                    startActivity(i);
                }

                mPreferences.setLastFromStation(mFromStopId);
                mStopAdapter.notifyDataSetChanged();

                return true;
            }
        });
    }

    private class StopAdapter extends ArrayAdapter<Stop>{
        public StopAdapter(ArrayList<Stop> stops){
            super(getActivity(), 0, stops);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater =
                    (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);

            TextView text = (TextView) convertView.findViewById(android.R.id.text1);
            Stop stop = getItem(position);
            text.setText(stop.getName());

            if( mFromStopId != null && stop.getStopId().compareTo(mFromStopId) == 0 ){
                convertView.setBackgroundColor(getResources().getColor(R.color.selected_gray));
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }

            return convertView;
        }
    }

    private class StopsLoaderCallbacks implements LoaderManager.LoaderCallbacks<ArrayList<Stop>> {

        @Override
        public Loader<ArrayList<Stop>> onCreateLoader(int id, Bundle args) {
            StopsLoader stopsLoader = new StopsLoader(getActivity());
            inject(stopsLoader);
            return stopsLoader;
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Stop>> loader, ArrayList<Stop> stops) {
            updateUI(stops);
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Stop>> loader) {}
    };


}
