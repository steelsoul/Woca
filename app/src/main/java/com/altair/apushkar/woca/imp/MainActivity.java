
package com.altair.apushkar.woca.imp;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.altair.apushkar.woca.R;
import com.altair.apushkar.woca.api.ILanguageDB;

public class MainActivity extends Activity {
    private final String LOG_TAG = "[MainActivity]";
    private final String DB_NAME = "a5.db";

    private Spinner esDirection;
    private EditText etWord;
    private ILanguageDB langdb;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        esDirection = (Spinner) findViewById(R.id.language_spinner);
        esDirection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        etWord = (EditText) findViewById(R.id.etWord);

        langdb = new LanguageDB(this, DB_NAME);

        Cursor cursor = langdb.getLanguagesCursor();
        if (cursor.moveToFirst()) {
            Log.i(LOG_TAG, "CS: " + cursor.toString());
        } else {
            Log.d(LOG_TAG, "Cursor is EMPTY!");
        }
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                cursor,
                new String[] {"LRES"},
                new int[] {android.R.id.text1},
                0
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        esDirection.setAdapter(adapter);

        Log.d(LOG_TAG, "Created activity");
    }
}