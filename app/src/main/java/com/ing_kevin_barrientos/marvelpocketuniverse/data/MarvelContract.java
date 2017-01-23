package com.ing_kevin_barrientos.marvelpocketuniverse.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines tables and columns names for Marvel's database
 */
public class MarvelContract {

    // The "Content authority" is a name for the entire content provider
    public static final String CONTENT_AUTHORITY = "com.ing_kevin_barrientos.app";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_CHARACTERS = "characters";

    /* Inner class that defines the table contents of the characters table */
    public static final class CharacterEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CHARACTERS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CHARACTERS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CHARACTERS;

        // Table name
        public static final String TABLE_NAME = "characters";

        public static final String COLUMN_MARVELS_ID = "marvelsId";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_MODIFIED_DATE = "modified_date";
        public static final String COLUMN_THUMBNAIL_PATH = "thumbnail_path";
        public static final String COLUMN_THUMBNAIL_EXTENSION = "thumbnail_extension";
        public static final String COLUMN_COMICS = "comics";
        public static final String COLUMN_IMAGE_FULLSIZE = "image_fullsize";

        public static Uri buildCharacterUri(String marvelsId) {
            return CONTENT_URI.buildUpon().appendPath(marvelsId).build();
        }
    }
}
