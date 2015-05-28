package com.altair.apushkar.woca.imp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.altair.apushkar.woca.api.ILanguageDB;

/**
 * Created by apushkar on 25.05.2015.
 */
public class DBHelper extends SQLiteOpenHelper {
    final static int dbVersion = 17;
    final static String LOG_TAG = "[DBHelper]";

    private ILanguageDB langdbctrl = null;

    public DBHelper(Context context, String dbName, int version, ILanguageDB langDB) {
        super(context, dbName, null, version);
        langdbctrl = langDB;
    }

    public DBHelper(Context context, String dbName, int version) {
        super(context, dbName, null, version);
    }

    public DBHelper(Context context, String dbName) {
        super(context, dbName, null, dbVersion);
    }

    public DBHelper(Context context) {
        super(context, "myDB", null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "Create database");

        if (langdbctrl != null) { langdbctrl.onCreate(db); }
        String sqlExpression = null;

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOG_TAG, "Upgrade database from " + oldVersion + " to " + newVersion);
        if (langdbctrl != null) { langdbctrl.onUpgrade(db); }
        onCreate(db);
    }
}