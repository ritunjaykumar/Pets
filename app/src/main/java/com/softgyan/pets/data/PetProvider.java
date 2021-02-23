package com.softgyan.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.softgyan.pets.data.PetContract.FeedEntry;

public class PetProvider extends ContentProvider {

    private static final String TAG = "my_tag";
    private PetDbHelper mPetDbHelper;
    private static final int PET = 100;
    private static final int PET_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(FeedEntry.CONTENT_AUTHORITY, FeedEntry.PATH_PETS, PET);
        sUriMatcher.addURI(FeedEntry.CONTENT_AUTHORITY, FeedEntry.PATH_PETS + "/#", PET_ID);
    }


    public PetProvider() {
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        /*
         * create and initialize a PetDbHelper object to gain access to the pet database
         */
        mPetDbHelper = new PetDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = mPetDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PET: {
                cursor = database.query(FeedEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case PET_ID: {
                selection = FeedEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(FeedEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Not yet implemented");
            }
        }

        // TODO: Implement this to handle query requests from clients.
        return cursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        final int match = sUriMatcher.match(uri);
        if (match == PET) {
            return insertPet(uri, values);
        }
        throw new UnsupportedOperationException("Not yet implemented");

    }

    private Uri insertPet(Uri uri, ContentValues values) {
        //check for name
        String name = values.getAsString(FeedEntry.COLUMN_PET_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }
        //check for gender

        Integer gender = values.getAsInteger(FeedEntry.COLUMN_PET_GENDER);
        if (gender == null || !isValidGender(gender)) {
            throw new IllegalArgumentException("Pet requires valid gender");
        }

        //check for weight

        Integer weight = values.getAsInteger(FeedEntry.COLUMN_PET_WEIGHT);
        if (weight == null || weight < 0) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }


        SQLiteDatabase database = mPetDbHelper.getWritableDatabase();
        long id = database.insert(FeedEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(TAG, "Failed to insert row for " + uri);
            return null;
        }

        return ContentUris.withAppendedId(uri, id);
    }

    private boolean isValidGender(Integer gender) {
        return gender == FeedEntry.GENDER_FEMALE || gender == FeedEntry.GENDER_MALE || gender == FeedEntry.GENDER_UNKNOWN;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mPetDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PET: {
                return database.delete(FeedEntry.TABLE_NAME, selection, selectionArgs);
            }
            case PET_ID: {
                selection = FeedEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return database.delete(FeedEntry.TABLE_NAME, selection, selectionArgs);
            }
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PET:
                return FeedEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return FeedEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PET: {
                return updatePet(values, selection, selectionArgs);
            }
            case PET_ID: {
                selection = FeedEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(values, selection, selectionArgs);
            }
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updatePet(ContentValues values, String selection, String[] selectionArgs) {
        //check for name
        String name = values.getAsString(FeedEntry.COLUMN_PET_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }
        //check for gender

        Integer gender = values.getAsInteger(FeedEntry.COLUMN_PET_GENDER);
        if (gender == null || !isValidGender(gender)) {
            throw new IllegalArgumentException("Pet requires valid gender");
        }

        //check for weight

        Integer weight = values.getAsInteger(FeedEntry.COLUMN_PET_WEIGHT);
        if (weight == null || weight < 0) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }

        //if contentValues is empty i mean there is no any key/values pairs
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mPetDbHelper.getWritableDatabase();
        return database.update(FeedEntry.TABLE_NAME, values, selection, selectionArgs);

    }
}