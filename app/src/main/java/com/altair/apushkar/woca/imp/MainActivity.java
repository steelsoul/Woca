
package com.altair.apushkar.woca.imp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.altair.apushkar.woca.R;

public class MainActivity extends Activity implements View.OnClickListener {
    final String LOG_TAG = "myLogs";

    Button btnAdd, btnRead, btnClear;
    EditText etName, etMail;
    DBHelper dbHelper;

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

        etName = (EditText) findViewById(R.id.etName);
        etMail = (EditText) findViewById(R.id.etEmail);

        dbHelper = new DBHelper(this);
    }

    @Override
    public void onClick(View view) {
        ContentValues cv = new ContentValues();

        // obtain data from input fields
        String name = etName.getText().toString();
        String email = etMail.getText().toString();

        // connect to BD
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (view.getId()) {
            case R.id.btnAdd:
                Log.d(LOG_TAG, "Insert in mytable:");

                // prepare data for inserting in a form: name - value
                cv.put("name", name);
                cv.put("email", email);

                long rowId = db.insert("mytable", null, cv);
                Log.d(LOG_TAG, "row with ID = " + rowId);
                break;
            case R.id.btnRead:
                Log.d(LOG_TAG, "Rows in mytable:");

                // request all data from table mytable and obtain Cursor
                Cursor c = db.query("mytable", null, null, null, null, null, null);

                // set cursor position to a first row
                if (c.moveToFirst()) {
                    int idColIndex = c.getColumnIndex("id");
                    int nameColIndex = c.getColumnIndex("name");
                    int emailColIndex = c.getColumnIndex("email");

                    do {
                        // get values
                        Log.d(LOG_TAG, "ID = " + c.getInt(idColIndex) +
                            ", name = " + c.getString(nameColIndex) +
                            ", email = " + c.getString(emailColIndex));
                    } while (c.moveToNext());
                } else {
                    Log.d(LOG_TAG, "0 rows!");
                }
                c.close();
                break;
            case R.id.btnClear:
                Log.d(LOG_TAG, "Clear mytable");
                // remove all records
                int clearCount = db.delete("mytable", null, null);
                Log.d(LOG_TAG, "Removed " + clearCount + " records");
                break;
        }
        // close connection to DB
        dbHelper.close();
    }


    public class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            super(context, "myDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "Create database");
            db.execSQL("create table mytable ("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "email text"
                + ");"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}