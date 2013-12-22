package com.easycaltrain.loader;

import android.content.Context;

import com.easycaltrain.model.CSVManager;
import com.easycaltrain.model.Stop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.inject.Inject;

/**
 * Created by hectormonserrate on 22/12/13.
 */
public class StopsLoader extends DataLoader<ArrayList<Stop>> {
    @Inject CSVManager mCSVManager;

    public StopsLoader(Context context) {
        super(context);
    }

    @Override
    public ArrayList<Stop> loadInBackground() {
        ArrayList<Stop> stops = mCSVManager.getStops();
        Collections.sort(stops, new Comparator<Stop>() {
            public int compare(Stop e1, Stop e2) {
                return e2.compareTo(e1);
            }
        });

        return stops;
    }
}
