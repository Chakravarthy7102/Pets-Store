package com.example.petsbase;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class PetCursorAdapter  extends CursorAdapter {


    public PetCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_items,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView petName=view.findViewById(R.id.pet_name);
        TextView petBreed=view.findViewById(R.id.pet_breed);

        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        String breed= cursor.getString(cursor.getColumnIndexOrThrow("breed"));

        petName.setText(name);
        petBreed.setText(breed);
    }

}
