package com.altair.apushkar.woca.imp;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.altair.apushkar.woca.api.ILanguageDB;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class LanguageDB implements ILanguageDB {
    private final int dbVersion = 18;
    private final String LOG_TAG = "[LanguageDB]";
    private String dbFileName;
    private AssetManager assetManager;

    public LanguageDB(Context context, String dbName) {
        dbFileName = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/WOCA/" + dbName;
        assetManager = context.getAssets();
    }

    private SQLiteDatabase openExistingDB() {
        SQLiteDatabase database = null;
        Log.d(LOG_TAG, "Try to open existing database");
        try {
            database = SQLiteDatabase.openDatabase(dbFileName, null, SQLiteDatabase.OPEN_READWRITE);
            database.setVersion(dbVersion);
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
            Log.d(LOG_TAG, "Get writable database");
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
/*
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
*/
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

    @Override
    public Cursor getLanguagesCursor() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "select DIR.ID as _id, (L1.LANG || ' - ' || L2.LANG) as LRES\n" +
                "    from DIRECTION as DIR\n" +
                "    inner join LANGUAGES as L1\n" +
                "    inner join LANGUAGES as L2\n" +
                "    on L1.ID=DIR.ID_FROM and L2.ID=DIR.ID_TO;";
        return db.rawQuery(query, new String[]{});
    }

    @Override
    public Cursor getPresentation(Integer directionID)
    {
        SQLiteDatabase db = getWritableDatabase();
        String query = "select PRES.ID as _id, (WT1.WORD_DATA || '-' ||  WT2.WORD_DATA) as LIST_LINE\n" +
                "   from PRESENTATION as PRES\n" +
                "   inner join WORDSTABLE as WT1\n" +
                "   inner join WORDSTABLE as WT2\n" +
                "   on WT1.ID=PRES.ID_WORD and WT2.ID=PRES.ID_TRANSL;";
        return db.rawQuery(query, new String[]{});
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.setVersion(dbVersion);
        try {
            Log.d(LOG_TAG, "Create database");
            String sqlCreateDB = "createBD.sql";
            InputStream is = assetManager.open(sqlCreateDB);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String command = "";
            String line;
            while ((line = br.readLine()) != null) {
                Log.d(LOG_TAG, "Read: " + line);
                line += " ";
                if (!line.contains(";")) command += line;
                else {
                    command += line;
                    db.execSQL(command);
                    command = "";
                }
            }
        }
        catch (IOException e) {
            Log.e(LOG_TAG, "Error: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + LanguageDB.PresentationTable.Name);
        db.execSQL("DROP TABLE IF EXISTS " + LanguageDB.WordsTable.Name);
        db.execSQL("DROP TABLE IF EXISTS " + LanguageDB.LanguagesTable.Name);
        db.execSQL("DROP TABLE IF EXISTS " + LanguageDB.DirectionTable.Name);
    }

    public static final class PresentationTable {
        final static String Name = "PRESENTATION";
        final static String Key = "ID";
        final static String[] Parameters = {"ID_DIRECT", "ID_WORD", "ID_TRANSL"};
        final static String[] PTypes = {"INT", "INT", "INT"};
    }

    public static final class LanguagesTable {
        final static String Name = "LANGUAGES";
        final static String Key = "ID";
        final static String[] Parameters = {"LANG"};
        final static String[] PTypes = {"TEXT"};
    }

    public static final class DirectionTable {
        final static String Name = "DIRECTION";
        final static String Key = "ID";
        final static String[] Parameters = {"ID_FROM", "ID_TO"};
        final static String[] PTypes = {"INT", "INT"};
    }

    public static final class WordsTable {
        final static String Name = "WORDSTABLE";
        final static String Key = "ID";
        final static String[] Parameters = {"ID_LANG", "ID_TYPE", "WORD_DATA"};
        final static String[] PTypes = {"INT", "INT", "TEXT"};
    }
}
