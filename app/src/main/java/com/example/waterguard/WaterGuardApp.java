package com.example.waterguard;

import android.app.Application;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class WaterGuardApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize Realm
        Realm.init(this);
        
        // Create the default Realm configuration
        RealmConfiguration config = new RealmConfiguration.Builder()
            .name("waterguard.realm")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded() // During development, if schema changes, delete old data
            .build();
        
        // Set this as the default configuration
        Realm.setDefaultConfiguration(config);
    }
} 