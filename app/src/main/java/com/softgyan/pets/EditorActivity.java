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


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.softgyan.pets.data.PetContract.FeedEntry;
import com.softgyan.pets.models.PetsModels;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity {
    public static final String PET_MODELS = "petModels";
    private static final String TAG = "my_tag";
    private EditText mNameEditText;
    private EditText mBreedEditText;
    private EditText mWeightEditText;
    private Spinner mGenderSpinner;
    private PetsModels mPetsModels;

    private String curdOperation;
    private int index;

    private int mGender = FeedEntry.GENDER_UNKNOWN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_pet_name);
        mBreedEditText = findViewById(R.id.edit_pet_breed);
        mWeightEditText = findViewById(R.id.edit_pet_weight);
        mGenderSpinner = findViewById(R.id.spinner_gender);

        setupSpinner();

        if (getIntent() != null) {
            curdOperation = getIntent().getStringExtra(CatalogActivity.CURD_OPERATION);
            if (CatalogActivity.UPDATE.equals(curdOperation)) {
                mPetsModels = getIntent().getParcelableExtra(PET_MODELS);
                index = getIntent().getIntExtra(CatalogActivity.INDEX, -1);
                setupData(mPetsModels);
            }
        }
    }

    private void setupData(PetsModels models) {
        mNameEditText.setText(models.getName());
        mBreedEditText.setText(models.getBread());
        mWeightEditText.setText(String.valueOf(models.getWeight()));
        if (models.getGender() == 0) {
            mGenderSpinner.setPrompt("gender_unknown");
        } else if (models.getGender() == 1) {
            mGenderSpinner.setPrompt("gender_male");
        } else {
            mGenderSpinner.setPrompt("gender_female");
        }
    }


    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        final ArrayAdapter<CharSequence> genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = FeedEntry.GENDER_MALE;
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = FeedEntry.GENDER_FEMALE;
                    } else {
                        mGender = FeedEntry.GENDER_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = FeedEntry.GENDER_UNKNOWN;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                if (CatalogActivity.UPDATE.equals(curdOperation)) {
                    update();
                } else {
                    insertData();
                }
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                delete();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void delete() {
        int delete = getContentResolver().delete(
                Uri.withAppendedPath(FeedEntry.CONTENT_URI, String.valueOf(mPetsModels.getId())),
                null,
                null
        );
        if (delete == 0) {
            Toast.makeText(this, "data can't deleted", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "delete ");

        updateCatalog(CatalogActivity.DELETE, null, index);
    }

    private void update() {
        PetsModels pm = getPetModels();
        pm.setId(mPetsModels.getId());

        int update = getContentResolver().update(
                Uri.withAppendedPath(FeedEntry.CONTENT_URI, String.valueOf(mPetsModels.getId())),
                getContentValue(pm),
                null,
                null
        );

        if (update == 0) {
            Toast.makeText(this, "not updated", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "update: " + pm.toString());

        updateCatalog(CatalogActivity.UPDATE, pm, index);
    }

    private void insertData() {
        PetsModels p = getPetModels();

        ContentValues values = getContentValue(p);

        Uri newUri = getContentResolver().insert(FeedEntry.CONTENT_URI, values);

        if (newUri == null) {
            Toast.makeText(this, getString(R.string.editor_insert_pet_failed), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.editor_insert_pet_successful), Toast.LENGTH_SHORT).show();
            p.setId(ContentUris.parseId(newUri));
        }

        Log.d(TAG, "inserted : " + p.toString());
        updateCatalog(CatalogActivity.INSERT, p, -1);

    }

    private void updateCatalog(String curdOperation, PetsModels p, int index) {
        Intent intent = new Intent();
        if (!CatalogActivity.DELETE.equals(curdOperation)) {
            intent.putExtra(PET_MODELS, p);
        }
        if (!CatalogActivity.INSERT.equals(curdOperation)) {
            intent.putExtra(CatalogActivity.INDEX, index);
        }
        intent.putExtra(CatalogActivity.CURD_OPERATION, curdOperation);
        setResult(RESULT_OK, intent);
        finish();
    }


    private ContentValues getContentValue(PetsModels p) {
        ContentValues values = new ContentValues();

        values.put(FeedEntry.COLUMN_PET_NAME, p.getName());
        values.put(FeedEntry.COLUMN_PET_BREED, p.getBread());
        values.put(FeedEntry.COLUMN_PET_GENDER, p.getGender());
        values.put(FeedEntry.COLUMN_PET_WEIGHT, p.getWeight());
        return values;
    }

    private PetsModels getPetModels() {
        final PetsModels models = new PetsModels();
        models.setName(mNameEditText.getText().toString().trim());
        models.setBread(mBreedEditText.getText().toString().trim());
        models.setWeight(Integer.parseInt(mWeightEditText.getText().toString().trim()));
        models.setGender(mGender);

        return models;

    }
}