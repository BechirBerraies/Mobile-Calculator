package com.example.calculatrice;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MyDataBase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Calculations.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "calculations";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_EXPRESSION = "expression";
    private static final String COLUMN_RESULT = "result";

    public MyDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query =
                "CREATE TABLE " + TABLE_NAME + "( "
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_EXPRESSION + " TEXT, " +
                        COLUMN_RESULT + " TEXT); ";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addCalculation(String expression, String resultValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_EXPRESSION, expression);
        cv.put(COLUMN_RESULT, resultValue);
        try {
            long insertResult = db.insert(TABLE_NAME, null, cv);
            Log.d("MyDataBase", "addCalculation insertResult: " + insertResult);
        } catch (Exception e) {
            Log.e("MyDataBase", "Error adding calculation", e);
        } finally {
            db.close();
        }
    }

    public ArrayList<String> getAllOperations() {
        ArrayList<String> operationsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor != null) {
            try {
                // Print column names for debugging
                String[] columnNames = cursor.getColumnNames();
                for (String columnName : columnNames) {
                    Log.d("MyDataBase", "Column Name: " + columnName);
                }

                // Rest of your code to retrieve data from cursor
                if (cursor.moveToFirst()) {
                    do {
                        String expression = cursor.getString(cursor.getColumnIndex(COLUMN_EXPRESSION));
                        String result = cursor.getString(cursor.getColumnIndex(COLUMN_RESULT));
                        String calculations = expression + " = " + result;
                        operationsList.add(calculations);
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                Log.e("MyDataBase", "Error getting all operations", e);
            } finally {
                cursor.close();
                db.close();
            }
        } else {
            Log.e("MyDataBase", "Cursor is null");
        }
        return operationsList;
    }


    public void clearDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_NAME, null, null);
            Log.d("MyDataBase", "Cleared the database");
        } catch (Exception e) {
            Log.e("MyDataBase", "Error clearing the database", e);
        } finally {
            db.close();
        }
    }
}