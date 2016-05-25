package org.neteinstein.pickaname.app;

import org.neteinstein.pickaname.database.DatabaseAdapter;

import android.app.Application;

import de.greenrobot.event.EventBus;

public class PANApp extends Application {

    private DatabaseAdapter adapter = null;
    private EventBus eventBus = null;

    private static PANApp singleton = null;

    public void onCreate() {
        super.onCreate();

        PANApp.singleton = this;

        this.eventBus = EventBus.getDefault();

        this.adapter = new DatabaseAdapter(getApplicationContext());

        validateData();
    }

    private void validateData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                adapter.validateDatabase();
            }
        }).run();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        if (this.adapter != null) {
            this.adapter.close();
            this.adapter = null;
        }

        singleton = null;
    }

    public DatabaseAdapter getAdapter() {
        return this.adapter;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public static PANApp getInstance() {
        return singleton;
    }
}
