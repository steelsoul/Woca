
package com.altair.apushkar.woca.imp;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.altair.apushkar.woca.R;

public class MainActivity extends Activity implements View.OnClickListener {
    private final String LOG_TAG = "[MainActivity]";

    private Button btnAdd, btnRead, btnClear;
    private EditText etLang, etWord;
    private DBHelper dbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        btnAdd = (Button)findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        btnRead = (Button)findViewById(R.id.btnRead);
        btnRead.setOnClickListener(this);

        btnClear = (Button)findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);

        etLang = (EditText) findViewById(R.id.etLanguage);
        etWord = (EditText) findViewById(R.id.etWord);

        dbHelper = new DBHelper(this);
        Log.d(LOG_TAG, "Created activity");
    }

    @Override
    public void onClick(View view) {
        ContentValues cv = new ContentValues();

        // obtain data from input fields
        String language = etLang.getText().toString();
        if (language.isEmpty()) language = "eng";
        String word = etWord.getText().toString();

        Log.i(LOG_TAG, "language: " + language + "; word: " + word);
        // language ID
        Integer languageID = -1;

        // connect to BD
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = "" + DBHelper.LanguagesTable.Parameters[0] + " = ?";
        Log.i(LOG_TAG, "SELECTION: " + selection);
        String [] selectionArgs = {language};
        //Cursor cu = db.query(DBHelper.LanguagesTable.Name, null, "? = ?", selectionArgs, null, null, null, null);
        Cursor cu = db.query(DBHelper.LanguagesTable.Name,
                null,
                selection,
                selectionArgs, null, null, null);
        if (cu.moveToFirst()) {
            int idColIndex = cu.getColumnIndex(DBHelper.LanguagesTable.Key);
            languageID = cu.getInt(idColIndex);
        }
        cu.close();

        Log.i(LOG_TAG, "language is " + language + " with ID " + languageID);

        switch (view.getId()) {
            case R.id.btnAdd:
                Log.d(LOG_TAG, "Insert in mytable:");

                // prepare data for inserting in a form: language - value
                cv.put(DBHelper.WordsTable.Parameters[0], language);
                cv.put(DBHelper.WordsTable.Parameters[1], word);

                long rowId = db.insert(DBHelper.WordsTable.Name, null, cv);
                Log.d(LOG_TAG, "row with ID = " + rowId);
                break;
            case R.id.btnRead:
                Log.d(LOG_TAG, "Words: ");

                // request all data from table mytable and obtain Cursor
                Cursor c = db.query(DBHelper.WordsTable.Name, null, null, null, null, null, null);

                // set cursor position to a first row
                if (c.moveToFirst()) {
                    int idColIndex = c.getColumnIndex(DBHelper.WordsTable.Key);
                    int langColIndex = c.getColumnIndex(DBHelper.WordsTable.Parameters[0]);
                    int wordColIndex = c.getColumnIndex(DBHelper.WordsTable.Parameters[1]);

                    do {
                        // get values
                        Log.d(LOG_TAG, "ID = " + c.getInt(idColIndex) +
                            ", language = " + c.getString(langColIndex) + ", word = "+ c.getString(wordColIndex));
                    } while (c.moveToNext());
                    c.close();
                } else {
                    Log.d(LOG_TAG, "0 rows!");
                }
                break;
            case R.id.btnClear:
                Log.d(LOG_TAG, "Clear ");
                // remove all records
                int clearCount = db.delete(DBHelper.WordsTable.Name, null, null);
                Log.d(LOG_TAG, "Removed " + clearCount + " records");
                break;
        }
        // close connection to DB
        dbHelper.close();
    }


}