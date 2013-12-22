package com.easycaltrain.loader;

import android.content.Context;
import android.database.Cursor;

import com.easycaltrain.model.CSVManager;
import com.easycaltrain.model.CupboardSQLiteOpenHelper;
import com.easycaltrain.model.Stop;

import javax.inject.Inject;

public class StopTimesLoader extends DataLoader<Cursor> {
    @Inject
    CSVManager mCSVManager;
    String mFromStopId;
    String mToStopId;

    public StopTimesLoader(Context context, String fromStopId, String toStopId) {
        super(context);
        mFromStopId = fromStopId;
        mToStopId = toStopId;
    }

    @Override
    public Cursor loadInBackground() {
        Stop fromStop = mCSVManager.getStop(mFromStopId);
        Stop toStop = mCSVManager.getStop(mToStopId);

        CupboardSQLiteOpenHelper sqlHelper = new CupboardSQLiteOpenHelper(getContext(), mCSVManager);

        return sqlHelper.nextTrips(fromStop, toStop);
    }

}
