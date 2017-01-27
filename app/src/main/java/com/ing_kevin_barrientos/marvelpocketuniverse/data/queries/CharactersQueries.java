package com.ing_kevin_barrientos.marvelpocketuniverse.data.queries;

import android.database.Cursor;
import android.net.Uri;

import com.ing_kevin_barrientos.marvelpocketuniverse.data.MarvelContract;
import com.ing_kevin_barrientos.marvelpocketuniverse.data.MarvelDbHelper;

public class CharactersQueries {

    /**
     * Gets the first 100 records
     * @param mOpenHelper database helper
     * @param projection columns to retrieve
     * @param selection
     * @param selectionArgs @return a cursor
     * @param sortOrder
     */
    public static Cursor getAll(MarvelDbHelper mOpenHelper, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return mOpenHelper.getReadableDatabase().query(MarvelContract.CharacterEntry.TABLE_NAME,
                projection, selection, selectionArgs, null, null, sortOrder, "100");
    }

    /**
     * Search a character by its id
     * @param mOpenHelper database helper
     * @param id marvels api id
     * @param projection columns to retrieve
     * @return cursor with one item max
     */
    public static Cursor find(MarvelDbHelper mOpenHelper, String id, String[] projection) {
        return mOpenHelper.getReadableDatabase().query(MarvelContract.CharacterEntry.TABLE_NAME,
                projection, MarvelContract.CharacterEntry._ID + " = ? ", new String[]{id}, null, null, null, "1");
    }
}
