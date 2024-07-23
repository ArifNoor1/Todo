package com.example.todo.realmdb

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class TodoApp :Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name("todo.realm")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .allowWritesOnUiThread(true)
            .build()
        Realm.setDefaultConfiguration(config)
    }

}