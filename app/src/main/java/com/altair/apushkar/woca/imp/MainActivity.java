
package com.altair.apushkar.woca.imp;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.altair.apushkar.woca.R;
import com.altair.apushkar.woca.api.ILanguageDB;

public class MainActivity extends Activity implements View.OnClickListener {
    private final String LOG_TAG = "[MainActivity]";
    private final String DB_NAME = "myDB";

    private Button btnAdd, btnRead, btnClear;
    private Spinner esLang;
    private EditText etWord;
    private LanguageDB langdb;

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

        esLang = (Spinner) findViewById(R.id.language_spinner);
        esLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_spinner_item,
                cursor,
                new String[] {LanguageDB.LanguagesTable.Parameters[0]},
                new int[] {android.R.id.text1},
                0
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        esLang.setAdapter(adapter);

        Log.d(LOG_TAG, "Created activity");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAdd:
                int selectionPos = esLang.getSelectedItemPosition();
                Log.d(LOG_TAG, "Language selected pos: " + selectionPos);
                if (selectionPos == AdapterView.INVALID_POSITION) {
                    selectionPos = 0;
                }
                ++selectionPos;
                Log.d(LOG_TAG, "Language selected pos corrected: " + selectionPos);
                String word = etWord.getText().toString();
                Log.d(LOG_TAG, "Word: " + word);
                ILanguageDB.eWord_Type wordType = ILanguageDB.eWord_Type.eWT_Unknown;
                langdb.addWord(selectionPos, wordType, word);
                break;
            case R.id.btnRead:
                langdb.printWordTable();
                break;
            case R.id.btnClear:
                langdb.clear();
                break;
        }
    }


}