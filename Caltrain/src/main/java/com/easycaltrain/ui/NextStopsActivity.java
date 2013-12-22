package com.easycaltrain.ui;

import android.support.v4.app.Fragment;

import com.easycaltrain.SingleFragmentActivity;
import com.easycaltrain.ui.NextStopsFragment;

public class NextStopsActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        String fromStopId = (String)getIntent().getSerializableExtra(NextStopsFragment.ARG_FROM_STOP);
        String toStopId = (String)getIntent().getSerializableExtra(NextStopsFragment.ARG_TO_STOP);

        return NextStopsFragment.newInstance(fromStopId, toStopId);
    }


}
