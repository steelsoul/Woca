package com.altair.apushkar.woca.imp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by apushkar on 25.05.2015.
 */
public class DBHelper extends SQLiteOpenHelper {
    final static String LOG_TAG = "[DBHelper]";

    public static final class PresentationTable {
        final static String Name = "PRESENTATION";
        final static String Key = "ID";
        final static String[] Parameters = {"WORD_ID", "TRANSLATION_ID"};
    }

    public static final class LanguagesTable {
        final static String Name = "LANGUAGES";
        final static String Key = "ID";
        final static String[] Parameters = {"LANGUAGE"};
    }

    public static final class WordsTable {
        final static String Name = "WORDS";
        final static String Key = "ID";
        final static String[] Parameters = {"LANG_ID", "WORD_DATA"};
    }

    public DBHelper(Context context) {
        super(context, "myDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "Create database");
        String sqlExpression = "create table " +  PresentationTable.Name + " ("
                + PresentationTable.Key + " integer primary key autoincrement,"
                + PresentationTable.Parameters[0] + " integer,"
                + PresentationTable.Parameters[1] + " integer"
                + ");";
        db.execSQL(sqlExpression);
        Log.i(LOG_TAG, sqlExpression);

        sqlExpression = "create table " + LanguagesTable.Name + " ("
                + LanguagesTable.Key + " integer primary key autoincrement, "
                + LanguagesTable.Parameters[0] + " text" + ");";
        db.execSQL(sqlExpression);
        Log.i(LOG_TAG, sqlExpression);

        sqlExpression = "create table " + WordsTable.Name  +" (" + WordsTable.Key
                + " integer primary key autoincrement, "
                + WordsTable.Parameters[0] + " integer,"
                + WordsTable.Parameters[1] +  " text" + ");";
        Log.i(LOG_TAG, sqlExpression);


        ContentValues cv = new ContentValues();
        cv.put(LanguagesTable.Parameters[0], "eng");
        long result = db.insert(LanguagesTable.Name, null, cv);
        Log.d(LOG_TAG, "inserted 'eng' language with result " + result);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOG_TAG, "Upgrade database from " + oldVersion + " to " + newVersion);
    }
}