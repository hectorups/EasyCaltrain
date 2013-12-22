package com.easycaltrain;

import com.easycaltrain.loader.StopTimesLoader;
import com.easycaltrain.loader.StopsLoader;
import com.easycaltrain.model.CSVManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import com.easycaltrain.ui.MainFragment;
import com.easycaltrain.ui.NextStopsFragment;

@Module(
        injects = { MainFragment.class, NextStopsFragment.class, StopTimesLoader.class,  StopsLoader.class },
        library = true
)
public class CaltrainModule {

    private final CaltrainApplication application;

    public CaltrainModule(CaltrainApplication app) {
        this.application = app;
    }

    @Provides @Singleton
    CSVManager provideCSVManager() {
        return new CSVManager(application.getResources());
    }
}