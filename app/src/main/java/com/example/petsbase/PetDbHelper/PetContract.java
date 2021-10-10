package com.example.petsbase.PetDbHelper;
import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Pets app.
 */
public final class PetContract {



    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private PetContract() {}
    public static final String CONTENT_AUTHORITY="com.example.petsbase";
    public static final Uri BASE_URI= Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_PETS="pets";

    public static final class PetEntry implements BaseColumns {
        public static final Uri CONTENT_URI=Uri.withAppendedPath(BASE_URI,PATH_PETS);
        //what are mime types @Link{https://stackoverflow.com/questions/7157129/what-is-the-mimetype-attribute-in-data-used-for}
        //and why they are necessary.
        public static final String CONTENT_ITEM_TYPE =  ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;
        public static final String CONTENT_TYPE =ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;



        public final static String TABLE_NAME = "pets";


        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_PET_NAME ="name";


        public final static String COLUMN_PET_BREED = "breed";


        public final static String COLUMN_PET_GENDER = "gender";


        public final static String COLUMN_PET_WEIGHT = "weight";


        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;
    }

}
