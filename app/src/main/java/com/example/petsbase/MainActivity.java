package com.example.petsbase;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.petsbase.PetDbHelper.PetContract;
import com.example.petsbase.PetDbHelper.PetDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;



public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private PetDbHelper mDbHelper;
    private PetContract.PetEntry PetEntry;
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final int PET_LOADER = 0;
    PetCursorAdapter mCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDbHelper = new PetDbHelper(this);
        ListView petListView = findViewById(R.id.list_View);

        View emptyView = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);

        mCursorAdapter = new PetCursorAdapter(this, null, 0);
        petListView.setAdapter(mCursorAdapter);

        //setting the click listener on the list to view to open up the editor activity
        petListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent i = new Intent(MainActivity.this, EditorActivity.class);
            Uri uri = ContentUris.withAppendedId(PetContract.PetEntry.CONTENT_URI, id);
            i.putExtra("id",id);
            i.setData(uri);

            startActivity(i);
        });


        petListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Uri uri = ContentUris.withAppendedId(PetContract.PetEntry.CONTENT_URI, id);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Delete this Pet??");
                builder.setPositiveButton("Delete", (dialog, id12) -> getContentResolver().delete(uri, null, null));

                builder.setNegativeButton("Cancel", (dialog, id1) -> {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return true;
            }
        });


        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, EditorActivity.class);
            startActivity(intent);
        });

        //kicking off the Loader
        // getSupportLoaderManager().initLoader(1,null,this);
        LoaderManager.getInstance(this).initLoader(0, null, this);


    }


    //method for inserting the dummy data into the database
    //hardCoding data into the CONTENT VALUES
    private void insertPet() {
        // Gets the database in write mode


        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(PetContract.PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetContract.PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetContract.PetEntry.COLUMN_PET_GENDER, PetContract.PetEntry.GENDER_MALE);
        values.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, 7);
        Uri newUri = getContentResolver().insert(PetContract.PetEntry.CONTENT_URI, values);
        //passing the values into the petProvider..

    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.layout.menu_catalog, menu);
        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                try {
                    insertPet();
                } catch (IllegalArgumentException e) {
                    Log.e(LOG_TAG, "ERROR", e);
                }
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                getContentResolver().delete(PetContract.PetEntry.CONTENT_URI, Bundle.EMPTY);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                PetContract.PetEntry._ID,
                PetContract.PetEntry.COLUMN_PET_NAME,
                PetContract.PetEntry.COLUMN_PET_BREED,
                PetContract.PetEntry.COLUMN_PET_GENDER,
                PetContract.PetEntry.COLUMN_PET_WEIGHT};

        return new CursorLoader(this,
                PetContract.PetEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        mCursorAdapter.swapCursor(null);
    }


}