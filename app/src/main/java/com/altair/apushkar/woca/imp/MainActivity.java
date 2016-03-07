
package com.altair.apushkar.woca.imp;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.altair.apushkar.woca.R;
import com.altair.apushkar.woca.api.ILanguageDB;

public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        AbsListView.OnScrollListener {
    private final String LOG_TAG = "[MainActivity]";
    private final String DB_NAME = "vocruen.db";
    private final int maxLines = 50;
    private int currentPage = 0;

    private Spinner esDirection;
    private EditText etWord;
    private ListView presentationList;
    private ILanguageDB langdb;
    private SimpleCursorAdapter scAdapter;


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
        etWord.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.d(LOG_TAG, "onEditorAction ID: " + actionId);
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    String text = v.getText().toString();
                    Log.d(LOG_TAG, "onEditorAction " + text);
                    if (text.length() > 0) {
                        // TODO: replace with String Lang
                        int posID = langdb.getWordID(2, text);
                        if (posID > 0) {
                            currentPage = posID;
                            scAdapter.changeCursor(langdb.getPresentation(currentPage, maxLines));
                            scAdapter.notifyDataSetChanged();
                        }
                    }
                    return true;
                }
                return false;
            }
        });
//        etWord.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                String textToFind = s.toString();
//                Log.d(LOG_TAG, "afterTextChanged " + textToFind);
//                if (textToFind.length() > 0) {
//                    // TODO: replace with String Lang
//                    int posID = langdb.getWordID(2, textToFind);
//                    if (posID > 0) {
//                        scAdapter.changeCursor(langdb.getPresentation(currentPage, maxLines));
//                        scAdapter.notifyDataSetChanged();
//                    }
//                }
//            }
//        });

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

        scAdapter = new SimpleCursorAdapter(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                null,
                new String[] {"LIST_LINE"},
                new int[] {android.R.id.text1},
                0);
        presentationList = (ListView)findViewById(R.id.listView);
        presentationList.setAdapter(scAdapter);
        presentationList.setOnScrollListener(this);

        getSupportLoaderManager().initLoader(0, null, this);

        Log.d(LOG_TAG, "Created activity");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.d(LOG_TAG, "Create menu");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle bundle)
    {
        return new MyCursorLoader(this, langdb);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        scAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    // TODO: when load another data portion show it on the beginning/ending of the list

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        final int threshold = 5;

        if (scrollState == SCROLL_STATE_IDLE) {
            Log.d(LOG_TAG, "last visible position: " + view.getLastVisiblePosition());
            Log.d(LOG_TAG, "first visible position: " + view.getFirstVisiblePosition());
            if (view.getLastVisiblePosition() >= view.getCount() - 1 - threshold) {
                currentPage+=maxLines;
                Log.d(LOG_TAG, "CurrentPage (+): " + currentPage);
                //load more list items:
                scAdapter.changeCursor(langdb.getPresentation(currentPage, maxLines));
                scAdapter.notifyDataSetChanged();
            } else if (view.getFirstVisiblePosition() <= threshold + 1)
            {
                currentPage -= maxLines;
                Log.d(LOG_TAG, "CurrentPage (-): " + currentPage);
                scAdapter.changeCursor(langdb.getPresentation(currentPage, maxLines));
                scAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//        Log.d(LOG_TAG, "onScroll " + firstVisibleItem + ", " +visibleItemCount + ", "+ totalItemCount);
    }

    static class MyCursorLoader extends android.support.v4.content.CursorLoader {
        static final String LOG_TAG = "MyCursorLoader";
        ILanguageDB db;

        public MyCursorLoader(Context context, ILanguageDB db)
        {
            super(context);
            this.db = db;
        }

        @Override
        public Cursor loadInBackground() {
            Log.d(LOG_TAG, "loadInBackground");
            Cursor cursor = db.getPresentation(1, 50);
            return cursor;
        }
    }

}