package com.ing_kevin_barrientos.marvelpocketuniverse.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages a local database for marvel universe data.
 */
public class MarvelDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "marvelspocket.db";

    public MarvelDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_CHARACTERS_TABLE = "CREATE TABLE " + MarvelContract.CharacterEntry.TABLE_NAME + " (" +
                MarvelContract.CharacterEntry._ID + " INTEGER PRIMARY KEY," +
                MarvelContract.CharacterEntry.COLUMN_MARVELS_ID + " TEXT UNIQUE NOT NULL, " +
                MarvelContract.CharacterEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                MarvelContract.CharacterEntry.COLUMN_DESCRIPTION + " TEXT, " +
                MarvelContract.CharacterEntry.COLUMN_MODIFIED_DATE + " TEXT, " +
                MarvelContract.CharacterEntry.COLUMN_THUMBNAIL_PATH + " TEXT, " +
                MarvelContract.CharacterEntry.COLUMN_THUMBNAIL_EXTENSION + " TEXT" +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_CHARACTERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MarvelContract.CharacterEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
