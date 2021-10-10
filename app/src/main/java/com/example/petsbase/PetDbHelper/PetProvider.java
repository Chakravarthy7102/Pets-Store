package com.example.petsbase.PetDbHelper;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.petsbase.MainActivity;

//Always should add a <provider></>
//tag and attributes inside the
//Android Manifestation file
//to enable the service to the other apps in general application itself

public class PetProvider extends ContentProvider {

    public static final String LOG_TAG=PetProvider.class.getName();
    private static final int PETS=100;
    private static final int PET_ID=101;
    //s before the uriMatcher means that the variable is
    //static variable
    //initializing the uriMatcher object;
    private static final UriMatcher sUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS,PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS+"/#",PET_ID);
    }


    private PetDbHelper petDbHelper;
    @Override
    public boolean onCreate() {
        petDbHelper=new PetDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database=petDbHelper.getReadableDatabase();
         Cursor cursor=null;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:

                cursor=database.query(PetContract.PetEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case PET_ID:

                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(PetContract.PetEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        //notifying the listeners that data has changed
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match=sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetContract.PetEntry.CONTENT_TYPE;
            case PET_ID:
                return PetContract.PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("illegal argument");
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match=sUriMatcher.match(uri);
        switch(match){
            case PETS:
                insertPet(uri,values);

            default:
             throw new IllegalArgumentException("EXCEPTION FOUND"+uri);


        }

    }
    private Uri insertPet(Uri uri,ContentValues values){
        //using these blocks of codes we making sure that
        //the user don;t enter any kind of illegal values into the
        //database
        String name=values.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
        if(name==null){
            throw new IllegalArgumentException("THIS PET REQUIRES A NAME");
        }
        String breed=values.getAsString(PetContract.PetEntry.COLUMN_PET_BREED);
        if(breed==null){
            throw new IllegalArgumentException("THIS PET REQUIRES A BREED");
        }
        int weight=values.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT);
        if(weight==0){
            throw new IllegalArgumentException("ENTER SOME WEIGHT");
        }
        SQLiteDatabase database=petDbHelper.getWritableDatabase();
        long id=database.insert(PetContract.PetEntry.TABLE_NAME,null,values);
        if(id==-1){
            Log.e(LOG_TAG,"ERROR OCCURRED IN INSERT PET METHOD"+uri);
            return null;
        }
        //notify all the listeners that the data has changed
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri,id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                                        @Nullable String[] selectionArgs) {

        SQLiteDatabase database=petDbHelper.getWritableDatabase();
        final int match=sUriMatcher.match(uri);
        getContext().getContentResolver().notifyChange(uri,null);

        switch(match){
            case PETS:
                return database.delete(PetContract.PetEntry.TABLE_NAME,selection,selectionArgs);
            case PET_ID:
                selection= PetContract.PetEntry._ID + "=?";
                selectionArgs=new String[]{String.valueOf(ContentUris.parseId(uri))};
                return database.delete(PetContract.PetEntry.TABLE_NAME,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("ILLEGAL ARGUMENT");
        }

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        final int match=sUriMatcher.match(uri);
        switch(match){
            case PETS:
               return updatePet(uri,values,selection,selectionArgs);
            case PET_ID:
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet( uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("illegal argument"+uri);
        }

    }

    private int  updatePet(Uri uri,ContentValues values,String selections,String[] selectionArgs) {
        SQLiteDatabase database=petDbHelper.getWritableDatabase();
        if(values.containsKey(PetContract.PetEntry.COLUMN_PET_NAME)){
            String name=values.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
            if(name==null)
                throw new IllegalArgumentException("cannot be an empty name");
        }
        //if there are no values to update into the database then
        //don't update into the repo instead just terminate
        //
            if(values.size()==0){
                return 0;
            }

        getContext().getContentResolver().notifyChange(uri,null);
        return database.update(PetContract.PetEntry.TABLE_NAME,values,selections,selectionArgs);
    }
}
