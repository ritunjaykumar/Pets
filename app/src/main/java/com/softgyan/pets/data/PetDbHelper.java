package com.softgyan.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.softgyan.pets.data.PetContract.FeedEntry;

public class PetDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "shelter.db";
    public static final int DATABASE_VERSION = 1;
    private static final String TAG = "my_tag";


    public PetDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "PetDbHelper: helper constructor");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: created");
        createTable(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createTable(SQLiteDatabase db) {
        //field name (id, name, weight, bread, gender)

        String SQL_CREATE_PET_TABLE = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " %s TEXT NOT NULL, %s TEXT, %s INTEGER NOT NULL, %s INTEGER NOT NULL DEFAULT 0);",
                FeedEntry.TABLE_NAME, FeedEntry._ID, FeedEntry.COLUMN_PET_NAME, FeedEntry.COLUMN_PET_BREED,
                FeedEntry.COLUMN_PET_GENDER, FeedEntry.COLUMN_PET_WEIGHT);

        db.execSQL(SQL_CREATE_PET_TABLE);
        Log.d(TAG, "createTable: table created");
    }
}
