package com.easycaltrain;

import android.app.Application;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

public class CaltrainApplication extends Application {

    private ObjectGraph graph;

    @Override public void onCreate() {
        super.onCreate();

        graph = ObjectGraph.create(getModules().toArray());
    }

    protected List<Object> getModules() {
        return Arrays.asList(
                (Object) new CaltrainModule(this)
        );
    }

    public void inject(Object object) {
        getObjectGraph().inject(object);
    }

    public ObjectGraph getObjectGraph() {
        return this.graph;
    }

}
