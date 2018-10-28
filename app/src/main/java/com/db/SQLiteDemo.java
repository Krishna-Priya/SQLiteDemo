package com.db;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 */
public class SQLiteDemo extends Activity {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "employee.db";
    private static String DATABASE_TABLE = "employee";

    private SQLiteDatabase database;
    private static String col_id = "_id";
    private static String col_name = "name";
    private static String col_age = "age";
    private String[] columns = {col_id, col_name, col_age};
    private String nameString;
    private String ageString;
    private int ageInt;
    private String selection;
    private String[] selectionArgs;
    private String groupBy;
    private String having;
    private String orderBy;

    private Button bt_Show, bt_Insert, bt_Update, bt_Delete, bt_Records;
    private EditText et_Name, et_Age;

    private DatabaseHelper dbHelper;

    private static final String DATABASE_CREATE = "create table if not exists " + DATABASE_TABLE +
            " (" + col_id + " integer primary key autoincrement, " + col_name +
            " varchar(15)," + col_age + " integer);";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("DB-onUpgrade()", "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DatabaseHelper(this);

/*
        Uri uri = Uri.parse( "content://com.empcp/employee" );
//        Cursor cursor = managedQuery( uri, null, null, null, null );
//        Log.d( "SQLiteDemo-onCreate()", "COUNT : " + cursor.getCount() );
//        cursor.moveToFirst();
//        Log.d( "SQLiteDemo-onCreate()", "Name : " + cursor.getString( cursor.getColumnIndex( "name" ) ) +
//                " Age : " + cursor.getString( cursor.getColumnIndex( "age" ) ) );

        ContentValues values = new ContentValues();
        values.put("name", "Shabbir");
        values.put("age", "32");
        getContentResolver().insert( uri, values);

*/

        bt_Show = (Button) findViewById(R.id.bt_Show);
        bt_Update = (Button) findViewById(R.id.bt_Update);
        bt_Insert = (Button) findViewById(R.id.bt_Insert);
        bt_Delete = (Button) findViewById(R.id.bt_Delete);
        bt_Records = (Button) findViewById(R.id.bt_Records);




        open();


        //SHOW ROW

        bt_Show.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                nameString = ((EditText) findViewById(R.id.et_Name)).getText().toString();

                show(nameString);
            }
        });

        // INSERT -----------

        bt_Insert.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                nameString = ((EditText) findViewById(R.id.et_Name)).getText().toString();

                et_Age = (EditText)findViewById(R.id.et_Age);
                ageString = et_Age.getText().toString();
                if(!TextUtils.isEmpty(ageString)) {
                    ageInt = new Integer(ageString).intValue();
                }


                insert(nameString, ageInt);

            }
        });

        // DELETE ----------

        bt_Delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                nameString = ((EditText) findViewById(R.id.et_Name)).getText().toString();
                delete(col_name + "='" + nameString + "'", null);

            }
        });

        // UPDATE -----------

        bt_Update.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                nameString = ((EditText) findViewById(R.id.et_Name)).getText().toString();

                et_Age = (EditText)findViewById(R.id.et_Age);
                ageString = et_Age.getText().toString();
                if(!TextUtils.isEmpty(ageString)) {
                    ageInt = new Integer(ageString).intValue();
                }


                ContentValues updateValues = new ContentValues();
                updateValues.put(col_age, ageString);
                update(updateValues, col_name + "='" + nameString + "'", null);


            }
        });

        //NUMBER OF RECORDS

        bt_Records.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                numberOfRecords();
            }

        });


    }

    public void numberOfRecords() {


        Cursor cursor = database.query(DATABASE_TABLE, columns, selection, selectionArgs, groupBy, having, orderBy);
        Log.d("SQLiteDemo-query()", "Record count : " + cursor.getCount());

        Toast.makeText(this, "Record count : " + cursor.getCount(), Toast.LENGTH_LONG).show();
    }

    public void show(String nameString) throws SQLException {

//        Toast.makeText(this, "Select * from "+DATABASE_TABLE+" where "+col_name+"="+nameString, Toast.LENGTH_LONG).show();



        Cursor cursor = database.query(DATABASE_TABLE, new String[] { col_id,
                        col_name, col_age }, col_name + "=?",
                new String[] { String.valueOf(nameString) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Toast.makeText(this,"name : "+cursor.getString(1)+" age : "+cursor.getString(2),Toast.LENGTH_LONG).show();



    }



    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();

       Log.d("SQLiteDemo-open()", "Database opened");

        Toast.makeText(this, "Database opened", Toast.LENGTH_LONG).show();
    }


    public long insert(String nameString, int ageInt) {
        ContentValues initialValues = new ContentValues();

        initialValues.put(col_name, nameString);
        initialValues.put(col_age, ageInt);

        long returnValue = database.insert(DATABASE_TABLE, null, initialValues);
        if (returnValue > 0) {
           Log.d("SQLiteDemo-insert()", "Record inserted");


            Toast.makeText(this, "Record inserted", Toast.LENGTH_LONG).show();
        } else {
            Log.d("SQLiteDemo-insert()", "Failed to insert record ");
            Toast.makeText(this, "Failed to insert record", Toast.LENGTH_LONG).show();
        }
        return returnValue;
    }

    public int update(ContentValues values, String whereClause, String whereArgs[]) {

        int returnValue = database.update(DATABASE_TABLE, values, whereClause, whereArgs);

        if (returnValue > 0) {
            Log.d("SQLiteDemo-update()", "Record updated");

            Toast.makeText(this, "Record updated", Toast.LENGTH_LONG).show();

        } else {
            Log.d("SQLiteDemo-update()", "Failed to update record ");

            Toast.makeText(this, "Failed to update record", Toast.LENGTH_LONG).show();

        }
        return returnValue;
    }



    public int delete(String whereClause, String whereArgs[]) {
        int returnValue = database.delete(DATABASE_TABLE, whereClause, whereArgs);

        if (returnValue > 0) {
            Log.d("SQLiteDemo-delete()", "Record deleted");

            Toast.makeText(this, "Record deleted", Toast.LENGTH_LONG).show();

        } else {
            Log.d("SQLiteDemo-delete()", "Failed to delete record ");

            Toast.makeText(this, "Failed to delete record", Toast.LENGTH_LONG).show();

        }
        return returnValue;
    }




     /*public void close() {
        dbHelper.close();
//        Log.d("SQLiteDemo-close()", "Database closed");
        Toast.makeText(this, "Database closed", Toast.LENGTH_LONG).show();
    }*/

   /* public void query() {
        Cursor cursor = database.query(DATABASE_TABLE, columns, selection, selectionArgs, groupBy, having, orderBy);
        Log.d("SQLiteDemo-query()", "Record count : " + cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                Log.d("SQLiteDemo-query()", "Name : " + cursor.getString(cursor.getColumnIndex(col_name)) + " Age : "
                        + cursor.getString(cursor.getColumnIndex(col_age)));

                Toast.makeText(this, "Name : " + cursor.getString(cursor.getColumnIndex(col_name)) + " Age : "
                        + cursor.getString(cursor.getColumnIndex(col_age)), Toast.LENGTH_LONG).show();
            }
            while (cursor.moveToNext());
        }
    }
*/


}
