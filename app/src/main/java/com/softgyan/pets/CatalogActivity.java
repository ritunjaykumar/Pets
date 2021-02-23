/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.softgyan.pets;


import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.softgyan.pets.adapter.PetsAdapter;
import com.softgyan.pets.data.PetContract;
import com.softgyan.pets.data.SqliteQuery;
import com.softgyan.pets.models.PetsModels;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    public static final String UPDATE = "_update";
    public static final String DELETE = "_delete";
    public static final String INSERT = "_insert";
    public static final String CURD_OPERATION = "_curd";
    public static final int CALL_EDITOR_REQUEST_CODE = 101;
    private static final String TAG = "my_tag";
    public static final String INDEX = "index";


    private TextView noData;
    private List<PetsModels> allData;


    private RecyclerView recyclerView;
    private PetsAdapter petsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        noData = findViewById(R.id.textView);
        recyclerView = findViewById(R.id.recycler_view);
        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
            intent.putExtra(INSERT, false);
            startActivityForResult(intent, CALL_EDITOR_REQUEST_CODE);
        });

        getAllData();
        initRecyclerView();

    }



    //debug code using content provider
    public void getAllData() {

        String[] projections = {
                PetContract.FeedEntry._ID,
                PetContract.FeedEntry.COLUMN_PET_NAME,
                PetContract.FeedEntry.COLUMN_PET_BREED,
                PetContract.FeedEntry.COLUMN_PET_GENDER,
                PetContract.FeedEntry.COLUMN_PET_WEIGHT
        };

        Cursor cursor = getContentResolver().query(
                PetContract.FeedEntry.CONTENT_URI,
                projections,
                null,
                null,
                null
        );


        allData = new ArrayList<>();

        while (cursor.moveToNext()) {

            final PetsModels petsModels = new PetsModels();

            petsModels.setId(cursor.getInt(cursor.getColumnIndex(PetContract.FeedEntry._ID)));
            petsModels.setName(cursor.getString(cursor.getColumnIndex(PetContract.FeedEntry.COLUMN_PET_NAME)));
            petsModels.setBread(cursor.getString(cursor.getColumnIndex(PetContract.FeedEntry.COLUMN_PET_BREED)));
            petsModels.setWeight(cursor.getInt(cursor.getColumnIndex(PetContract.FeedEntry.COLUMN_PET_WEIGHT)));
            petsModels.setGender(cursor.getInt(cursor.getColumnIndex(PetContract.FeedEntry.COLUMN_PET_GENDER)));

            allData.add(petsModels);
            Log.d(TAG, "getAllData: " + allData.toString());

        }

        cursor.close();

    }


    @SuppressLint("NotifyDataSetChanged")
    private void initRecyclerView() {
        if (allData != null) {
            petsAdapter = new PetsAdapter(this, allData);
            recyclerView.setAdapter(petsAdapter);
            petsAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertDummyData();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
               deleteAllData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllData() {
        int delete = getContentResolver().delete(PetContract.FeedEntry.CONTENT_URI, null, null);
        if(delete == 0){
            Toast.makeText(this, "can't delete", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
            allData.clear();
            showInfo(allData.size());
            petsAdapter.notifyDataSetChanged();

        }
    }

    private void insertDummyData() {
        ContentValues petContent = new ContentValues();
        petContent.put(PetContract.FeedEntry.COLUMN_PET_NAME, "Tommy");
        petContent.put(PetContract.FeedEntry.COLUMN_PET_BREED, "Indian pet");
        petContent.put(PetContract.FeedEntry.COLUMN_PET_GENDER, 1);
        petContent.put(PetContract.FeedEntry.COLUMN_PET_WEIGHT, 5);

        Uri insert = getContentResolver().insert(PetContract.FeedEntry.CONTENT_URI, petContent);
        if (insert == null) {
            Toast.makeText(this, "data can't inserted", Toast.LENGTH_SHORT).show();
            return;
        }
        final PetsModels pm = new PetsModels();
        pm.setId(ContentUris.parseId(insert));
        pm.setName("Tommy");
        pm.setBread("Indian pet");
        pm.setGender(1);
        pm.setWeight(5);
        allData.add(pm);
        petsAdapter.notifyItemInserted(allData.size() - 1);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CALL_EDITOR_REQUEST_CODE && resultCode == RESULT_OK) {
            curdOperation(data);
        }
    }

    private void curdOperation(Intent data) {
        if (data != null) {

            String curd = data.getStringExtra(CURD_OPERATION);

            if (DELETE.equals(curd)) {
                int index = data.getIntExtra(INDEX, -1);
                if (index != -1) {
                    allData.remove(index);
                    petsAdapter.notifyItemRemoved(index);
                    Log.d(TAG, "curdOperation: deleted ");
                } else {
                    Log.d(TAG, "curdOperation: not deleted");
                }
            } else {

                PetsModels petsModels = data.getParcelableExtra(EditorActivity.PET_MODELS);
                if (petsModels != null && allData != null) {
                    if (INSERT.equals(curd)) {
                        allData.add(petsModels);
                        petsAdapter.notifyItemInserted(allData.size() - 1);
                        Log.d(TAG, "curdOperation: data inserted");

                    } else if (UPDATE.equals(curd)) {
                        int index = data.getIntExtra(INDEX, -1);
                        if (index != -1) {
                            allData.set(index, petsModels);
                            petsAdapter.notifyItemChanged(index);
                            Log.d(TAG, "curdOperation: data updated");
                        } else {
                            Log.d(TAG, "curdOperation: not deleted");
                        }
                    }
                }
            }
            if (allData != null) {
                showInfo(allData.size());
            }
        }
    }

    private void showInfo(int size) {
        if (size == 0) {
            noData.setVisibility(View.VISIBLE);
        } else {
            noData.setVisibility(View.GONE);
        }
    }
}
