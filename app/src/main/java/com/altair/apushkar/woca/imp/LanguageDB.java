package com.altair.apushkar.woca.imp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.altair.apushkar.woca.api.ILanguageDB;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by apushkar on 28.05.2015.
 */
public class LanguageDB implements ILanguageDB {
    private final int dbVersion = 18;
    private final String LOG_TAG = "[LanguageDB]";
    private String dbName;
    private String dbFileName;

    public LanguageDB(Context context, String dbName) {
        this.dbName = dbName;
        dbFileName = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/WOCA/" + dbName;
    }

    private SQLiteDatabase openExistingDB() {
        SQLiteDatabase database = null;
        Log.d(LOG_TAG, "Try to open existing database");
        try {
            database = SQLiteDatabase.openDatabase(dbFileName, null, SQLiteDatabase.OPEN_READWRITE);
            Log.d(LOG_TAG, "Opened existing database");
        } catch (SQLiteException sqe) {
            Log.e(LOG_TAG, sqe.toString());
        }
        return database;
    }

    private SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase db = openExistingDB();
        if (db == null)
        {
            Log.d(LOG_TAG, "creating database");
            db = SQLiteDatabase.openDatabase(dbFileName, null, SQLiteDatabase.CREATE_IF_NECESSARY);
            onCreate(db);
        }

        return db;
    }

    @Override
    public long clear() {
        Log.d(LOG_TAG, "Clear ");
        // remove all records
        SQLiteDatabase db = getWritableDatabase();
        int clearCount = db.delete(LanguageDB.WordsTable.Name, null, null);
        Log.d(LOG_TAG, "Removed " + clearCount + " records");
        db.close();
        return clearCount;
    }

    private long addLanguageNoOpen(String lang, SQLiteDatabase db)
    {
        ContentValues cv = new ContentValues();
        cv.put(LanguageDB.LanguagesTable.Parameters[0], lang);

        long result = -1;
        db.beginTransaction();
        try {
            result = db.insert(LanguageDB.LanguagesTable.Name, null, cv);
            Log.d(LOG_TAG, "inserted '" + lang + "' language with result " + result);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return result;
    }

    @Override
    public long addLanguage(String lang) {
        SQLiteDatabase db = getWritableDatabase();
        long result = addLanguageNoOpen(lang, db);
        db.close();
        return result;
    }

    @Override
    public void removeLanguage(String lang) {

    }

    @Override
    public void changeLanguage(String lang) {

    }

    @Override
    public Integer getLanguage(String lang) {
        return null;
    }

    @Override
    public long addWord(Integer langID, eWord_Type wordType, String word) {
        long result = -1;

        ContentValues cv = new ContentValues();

        // prepare data for inserting in a form: language - value
        cv.put(WordsTable.Parameters[0], langID);
        cv.put(WordsTable.Parameters[1], wordType.getID());
        cv.put(WordsTable.Parameters[2], word);

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            result = db.insert(WordsTable.Name, null, cv);
            db.setTransactionSuccessful();
            Log.d(LOG_TAG, "added word '" + word + "' " + langID + "|" + wordType.getID());
        } finally {
            db.endTransaction();
        }

        return result;
    }

    public void printWordTable() {
        Log.d(LOG_TAG, "Words: ");

        SQLiteDatabase db = getWritableDatabase();
        // request all data from table mytable and obtain Cursor
        try {
            Cursor c = db.query(LanguageDB.WordsTable.Name, null, null, null, null, null, null);
            if (c.moveToFirst()) {
                int idColIndex = c.getColumnIndex(LanguageDB.WordsTable.Key);
                int langColIndex = c.getColumnIndex(LanguageDB.WordsTable.Parameters[0]);
                int typeColIndex = c.getColumnIndex(LanguageDB.WordsTable.Parameters[1]);
                int wordColIndex = c.getColumnIndex(LanguageDB.WordsTable.Parameters[2]);

                do {
                    // get values
                    Log.d(LOG_TAG, "ID = " + c.getInt(idColIndex) +
                            ", language = " + c.getString(langColIndex) + ", word = "+ c.getString(wordColIndex) + ", type = " + c.getInt(typeColIndex));
                } while (c.moveToNext());
                c.close();
            } else {
                Log.d(LOG_TAG, "0 rows!");
            }
        } finally {
            db.close();
        }
    }

    @Override
    public Integer getWordID(Integer langID, String word) {
        return null;
    }

    @Override
    public boolean removeWord(Integer wordID) {
        return false;
    }

    @Override
    public Integer addTranslation(Integer fromID, Integer toID) {
        return null;
    }

    @Override
    public boolean removeTranslation(Integer translationID) {
        return false;
    }

    @Override
    public String translateFrom(String from) {
        return null;
    }

    @Override
    public String translateTo(String to) {
        return null;
    }

    public Cursor getLanguagesCursor() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(LanguageDB.LanguagesTable.Name,
                new String[] {LanguageDB.LanguagesTable.Key, LanguageDB.LanguagesTable.Parameters[0]},
                null,
                null,
                null,
                null,
                null);

        return cursor;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlExpression = "create table " + LanguageDB.LanguagesTable.Name + " ("
                + LanguageDB.LanguagesTable.Key + " integer primary key autoincrement, "
                + LanguageDB.LanguagesTable.Parameters[0] + " " + LanguageDB.LanguagesTable.PTypes[0] + ");";
        db.execSQL(sqlExpression);
        Log.i(LOG_TAG, sqlExpression);

        sqlExpression = "create table " + LanguageDB.PresentationTable.Name + " ("
                + LanguageDB.PresentationTable.Key + " integer primary key autoincrement, "
                + LanguageDB.PresentationTable.Parameters[0] + " " + LanguageDB.PresentationTable.PTypes[0] + ", "
                + LanguageDB.PresentationTable.Parameters[1] + " " + LanguageDB.PresentationTable.PTypes[1]+ ");";

        db.execSQL(sqlExpression);
        Log.i(LOG_TAG, sqlExpression);

        sqlExpression = "create table " + LanguageDB.WordsTable.Name + " (" + LanguageDB.WordsTable.Key
                + " integer primary key autoincrement, "
                + LanguageDB.WordsTable.Parameters[0] + " " + LanguageDB.WordsTable.PTypes[0] + ", "
                + LanguageDB.WordsTable.Parameters[1] + " " + LanguageDB.WordsTable.PTypes[1] + ", "
                + LanguageDB.WordsTable.Parameters[2] + " " + LanguageDB.WordsTable.PTypes[2] + ");";
        db.execSQL(sqlExpression);
        Log.i(LOG_TAG, sqlExpression);

        this.addLanguageNoOpen("en", db);
        this.addLanguageNoOpen("ru", db);
        this.addLanguageNoOpen("de", db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db) {
//        db.execSQL("DROP TABLE IF EXISTS " + LanguageDB.PresentationTable.Name);
//        db.execSQL("DROP TABLE IF EXISTS " + LanguageDB.WordsTable.Name);
//        db.execSQL("DROP TABLE IF EXISTS " + LanguageDB.LanguagesTable.Name);
    }

    private boolean importDB(String importFrom, String importTo)
    {
        boolean result = true;
        Log.d(LOG_TAG, "Import " + importFrom + " into " + importTo);
        File externalDir = android.os.Environment.getExternalStorageDirectory();
        File wocaDir = new File(externalDir.getAbsolutePath() + "/WOCA");
        if (wocaDir.mkdirs()) Log.d(LOG_TAG, "Made directory WOCA ok");
        File wocaImport = new File(wocaDir.getAbsolutePath() + "/" + importFrom);
        File dataDir = android.os.Environment.getDataDirectory();
        File dbDataBase = new File(dataDir.getAbsolutePath() + "/data/com.altair.apushkar.woca/databases/" + importTo);

        try {
            byte[] buf = new byte[1024];
            int len;
            InputStream in = new FileInputStream(wocaImport);
            OutputStream out = new FileOutputStream(dbDataBase);

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
            Log.d(LOG_TAG, "Exception: " + e.toString());
        }
        return result;
    }

    public static final class PresentationTable {
        final static String Name = "PRESENTATION";
        final static String Key = "pid";
        final static String[] Parameters = {"WORD_ID", "TRANSLATION_ID"};
        final static String[] PTypes = {"INT", "INT"};
    }

    public static final class LanguagesTable {
        final static String Name = "LANGUAGES";
        final static String Key = "_id";
        final static String[] Parameters = {"LANGUAGE"};
        final static String[] PTypes = {"TEXT"};
    }

    public static final class WordsTable {
        final static String Name = "WORDSTABLE";
        final static String Key = "wid";
        final static String[] Parameters = {"LANG_ID", "TYPE_ID", "WORD_DATA"};
        final static String[] PTypes = {"INT", "INT", "TEXT"};
    }
}