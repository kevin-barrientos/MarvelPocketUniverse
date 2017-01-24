package com.ing_kevin_barrientos.marvelpocketuniverse.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.ing_kevin_barrientos.marvelpocketuniverse.R;
import com.ing_kevin_barrientos.marvelpocketuniverse.data.MarvelContract;
import com.karumi.marvelapiclient.CharacterApiClient;
import com.karumi.marvelapiclient.MarvelApiConfig;
import com.karumi.marvelapiclient.MarvelApiException;
import com.karumi.marvelapiclient.model.CharacterDto;
import com.karumi.marvelapiclient.model.CharactersDto;
import com.karumi.marvelapiclient.model.CharactersQuery;
import com.karumi.marvelapiclient.model.MarvelImage;
import com.karumi.marvelapiclient.model.OrderBy;

import java.util.Vector;

public class MarvelPocketSyncAdapter extends AbstractThreadedSyncAdapter {
    // Interval at which to sync with the marvels api, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    private static final int SYNC_INTERVAL = 60 * 180;
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    private final String LOG_TAG = MarvelPocketSyncAdapter.class.getSimpleName();

    MarvelPocketSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    private static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    private static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MarvelPocketSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");

        MarvelApiConfig marvelApiConfig = new MarvelApiConfig.Builder("108b8323d65486f2e5f68a906e092528", "ba5083e4278cf6757c2284abb2391a24cf94c8e5").debug().build();

        CharacterApiClient client = new CharacterApiClient(marvelApiConfig);

        try {
            CharactersQuery query = CharactersQuery.Builder.create().withOrderBy(OrderBy.MODIFIED).withLimit(100).build();
            CharactersDto charactersDto = client.getAll(query).getResponse();
            insertCharacters(charactersDto);
        } catch (MarvelApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Take the characters dto's and insert them into the database
     */
    private void insertCharacters(CharactersDto charactersDto) {

        // Insert the new weather information into the database
        Vector<ContentValues> cVVector = new Vector<>(charactersDto.getCount());

        for (CharacterDto characterDto : charactersDto.getCharacters()) {
            ContentValues characterValues = new ContentValues();

            characterValues.put(MarvelContract.CharacterEntry.COLUMN_MARVELS_ID, characterDto.getId());
            characterValues.put(MarvelContract.CharacterEntry.COLUMN_NAME, characterDto.getName());
            characterValues.put(MarvelContract.CharacterEntry.COLUMN_DESCRIPTION, characterDto.getDescription());
            characterValues.put(MarvelContract.CharacterEntry.COLUMN_MODIFIED_DATE, characterDto.getModified());
            characterValues.put(MarvelContract.CharacterEntry.COLUMN_THUMBNAIL_PATH, characterDto.getThumbnail().getPath());
            characterValues.put(MarvelContract.CharacterEntry.COLUMN_THUMBNAIL_EXTENSION, characterDto.getThumbnail().getExtension());
            characterValues.put(MarvelContract.CharacterEntry.COLUMN_COMICS, characterDto.getComics().getReturned());
            characterValues.put(MarvelContract.CharacterEntry.COLUMN_IMAGE_FULLSIZE, characterDto.getThumbnail().getImageUrl(MarvelImage.Size.FULLSIZE));

            cVVector.add(characterValues);
        }

        // add to database
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);

            // delete old data so we don't build up an endless history
            getContext().getContentResolver().delete(MarvelContract.CharacterEntry.CONTENT_URI, MarvelContract.CharacterEntry.COLUMN_FAVORITE + " = 0 ", null);

            // insert new characters
            getContext().getContentResolver().bulkInsert(MarvelContract.CharacterEntry.CONTENT_URI, cvArray);
        }

        Log.d(LOG_TAG, "Sync Complete. " + cVVector.size() + " Inserted");
    }
}