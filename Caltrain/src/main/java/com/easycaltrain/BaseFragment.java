package com.easycaltrain;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.easycaltrain.library.Preferences;

public class BaseFragment extends Fragment {

    protected Preferences mPreferences;

    @Override public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        inject(this);

        mPreferences = new Preferences(getActivity());
    }

    public void inject(Object obj){
        ((CaltrainApplication) getActivity().getApplication()).inject(obj);
    }

}
