package com.softgyan.pets.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.softgyan.pets.data.PetContract.FeedEntry;
import com.softgyan.pets.models.PetsModels;

import java.util.ArrayList;
import java.util.List;

public class SqliteQuery {
    private static final String TAG = "my_tag";
    private static SqliteQuery sqlitQuery;
    private static PetDbHelper dbHelper = null;

    private SqliteQuery(Context context) {
        dbHelper = new PetDbHelper(context);
    }

    public static SqliteQuery getInstance(Context context) {
        if (sqlitQuery == null) {
            sqlitQuery = new SqliteQuery(context);
            Log.d(TAG, "getInstance: create first time");
        }
        return sqlitQuery;
    }

    public PetsModels insertPetData(PetsModels p) {
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_PET_NAME, p.getName());
        values.put(FeedEntry.COLUMN_PET_BREED, p.getBread());
        values.put(FeedEntry.COLUMN_PET_GENDER, p.getGender());
        values.put(FeedEntry.COLUMN_PET_WEIGHT, p.getWeight());

        SQLiteDatabase databaseWrite = dbHelper.getWritableDatabase();
        long insert = databaseWrite.insert(FeedEntry.TABLE_NAME, null, values);
        // if values is not inserted into database return null
        if (insert == -1) return null;
        // if values is inserted into database add id and return values
        p.setId(insert);
        return p;
    }

    public List<PetsModels> getAllData() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] projections = {
                FeedEntry._ID,
                FeedEntry.COLUMN_PET_NAME,
                FeedEntry.COLUMN_PET_BREED,
                FeedEntry.COLUMN_PET_GENDER,
                FeedEntry.COLUMN_PET_WEIGHT
        };
        final List<PetsModels> petsModelsList = new ArrayList<>();


        Cursor cursor = db.query(
                FeedEntry.TABLE_NAME,
                projections,
                null,
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {

            final PetsModels petsModels = new PetsModels();

            petsModels.setId(cursor.getInt(cursor.getColumnIndex(FeedEntry._ID)));
            petsModels.setName(cursor.getString(cursor.getColumnIndex(FeedEntry.COLUMN_PET_NAME)));
            petsModels.setBread(cursor.getString(cursor.getColumnIndex(FeedEntry.COLUMN_PET_BREED)));
            petsModels.setWeight(cursor.getInt(cursor.getColumnIndex(FeedEntry.COLUMN_PET_WEIGHT)));
            petsModels.setGender(cursor.getInt(cursor.getColumnIndex(FeedEntry.COLUMN_PET_GENDER)));

            petsModelsList.add(petsModels);
        }


        cursor.close();


        return petsModelsList;
    }

    public PetsModels updatePetData(PetsModels p) {
        SQLiteDatabase databaseWrite = dbHelper.getWritableDatabase();

        final ContentValues values = new ContentValues();

        values.put(FeedEntry.COLUMN_PET_NAME, p.getName());
        values.put(FeedEntry.COLUMN_PET_BREED, p.getBread());
        values.put(FeedEntry.COLUMN_PET_GENDER, p.getGender());
        values.put(FeedEntry.COLUMN_PET_WEIGHT, p.getWeight());

        final String whereClause = FeedEntry._ID + "=?";
        final String[] whereArgs = {String.valueOf(p.getId())};

        int update = databaseWrite.update(
                FeedEntry.TABLE_NAME,
                values,
                whereClause,
                whereArgs
        );

        if (update == 0) {
            Log.d(TAG, "updatePetData: updating failed");
            return null;
        }
        Log.d(TAG, "updatePetData: updated");
        return p;
    }

    public boolean deleteSingleRecord(int id) {
        SQLiteDatabase databaseWrite = dbHelper.getWritableDatabase();

        String whereClause = FeedEntry._ID + "=?";
        String[] whereArgs = {String.valueOf(id)};
        int delete = databaseWrite.delete(
                FeedEntry.TABLE_NAME,
                whereClause,
                whereArgs
        );

        if (delete == 0) {
            Log.d(TAG, "deleteSingleRecord: data is not deleted");
            return false;
        }

        Log.d(TAG, "deleteSingleRecord: data is deleted");
        return true;

    }

    public boolean deleteAllRecords() {

        SQLiteDatabase databaseWrite = dbHelper.getWritableDatabase();
        int delete = databaseWrite.delete(
                FeedEntry.TABLE_NAME,
                "1",
                null
        );

        if (delete == 0) {
            Log.d(TAG, "deleteAllRecords: data not deleted");
            return false;
        }
        Log.d(TAG, "deleteAllRecords: data deleted");
        return true;
    }

}
