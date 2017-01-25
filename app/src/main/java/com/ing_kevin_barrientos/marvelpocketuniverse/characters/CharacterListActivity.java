package com.ing_kevin_barrientos.marvelpocketuniverse.characters;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.ing_kevin_barrientos.marvelpocketuniverse.R;
import com.ing_kevin_barrientos.marvelpocketuniverse.data.MarvelContract;
import com.ing_kevin_barrientos.marvelpocketuniverse.sync.MarvelPocketSyncAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * An activity representing a list of characters. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link CharacterDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class CharacterListActivity extends AppCompatActivity implements CharactersAdapter.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor>,
        NotesDialogFragment.OnSaveClickListener,
        NewCharacterDialgoFragment.OnNewCharacterDialogClickedListener {

    /**
     * Corresponding index for each column
     *
     * @see #CHARACTERS_COLUMNS
     */
    public static final int _ID = 0,
            CHARACTER_MARVELS_ID = 1,
            CHARACTER_NAME = 2,
            CHARACTER_IMAGEURL = 3,
            CHARACTER_ORIGIN = 4;
    /**
     * Characters columns to be projected on a query
     */
    private static final String[] CHARACTERS_COLUMNS = {
            MarvelContract.CharacterEntry._ID,
            MarvelContract.CharacterEntry.COLUMN_MARVELS_ID,
            MarvelContract.CharacterEntry.COLUMN_NAME,
            MarvelContract.CharacterEntry.COLUMN_IMAGE_FULLSIZE,
            MarvelContract.CharacterEntry.COLUMN_ORIGIN,
    };
    private static final String FAVORITES_KEY = "favorites";
    @BindView(R.id.frameLayout)
    View mFrameLayout;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private CharactersAdapter mCharactersAdapter;
    //flag to avoid loops
    private boolean isFirstime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_list);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (findViewById(R.id.character_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        View recyclerView = findViewById(R.id.character_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        MarvelPocketSyncAdapter.initializeSyncAdapter(this);

        getSupportLoaderManager().initLoader(1, null, this);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mCharactersAdapter = new CharactersAdapter(this, null, this);
        recyclerView.setAdapter(mCharactersAdapter);

        if (!mTwoPane)
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.characters_list_filters, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.show_all) {
            getSupportLoaderManager().restartLoader(1, null, this);
        } else if (id == R.id.show_favorites) {
            Bundle args = new Bundle();
            args.putBoolean(FAVORITES_KEY, true);
            getSupportLoaderManager().restartLoader(1, args, this);
        }

        return true;
    }

    @Override
    public void onItemClick(long id) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putLong(CharacterDetailFragment.ARG_ITEM_ID, id);

            CharacterDetailFragment fragment = new CharacterDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.character_detail_container, fragment, CharacterDetailFragment.class.getSimpleName())
                    .commit();
        } else {
            Intent intent = new Intent(this, CharacterDetailActivity.class);
            intent.putExtra(CharacterDetailFragment.ARG_ITEM_ID, id);

            startActivity(intent);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sort = MarvelContract.CharacterEntry.COLUMN_NAME;
        if (args == null)
            return new CursorLoader(this,
                    MarvelContract.CharacterEntry.CONTENT_URI, CHARACTERS_COLUMNS,
                    null,
                    null,
                    sort);
        return new CursorLoader(this,
                MarvelContract.CharacterEntry.CONTENT_URI, CHARACTERS_COLUMNS,
                MarvelContract.CharacterEntry.COLUMN_FAVORITE + " = 1",
                null,
                sort);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null && (data.getCount() > 0 || !isFirstime)) {
            mCharactersAdapter.swapCursor(data);
            mCharactersAdapter.notifyDataSetChanged();
        } else if (isFirstime) {
            MarvelPocketSyncAdapter.syncImmediately(this);
        }

        isFirstime = false;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCharactersAdapter.swapCursor(null);
        mCharactersAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveClicked(String note) {
        CharacterDetailFragment fragment = (CharacterDetailFragment) getSupportFragmentManager().findFragmentByTag(CharacterDetailFragment.class.getSimpleName());
        fragment.saveNote(note);
    }

    @OnClick(R.id.create_character_fab)
    public void onCreateCharacterFabClicked() {
        DialogFragment dialog = NewCharacterDialgoFragment.newInstance();
        dialog.show(getSupportFragmentManager(), NewCharacterDialgoFragment.class.getSimpleName());
    }

    @Override
    public void onNewCharacterSavedClicked(String name, String description, int comics, Object image) {
        new SaveCharacterAsync().execute(name, description, String.valueOf(comics), "");
    }

    private class SaveCharacterAsync extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            ContentValues values = new ContentValues();
            values.put(MarvelContract.CharacterEntry.COLUMN_NAME, params[0]);
            values.put(MarvelContract.CharacterEntry.COLUMN_DESCRIPTION, params[1]);
            values.put(MarvelContract.CharacterEntry.COLUMN_COMICS, params[2]);
            values.put(MarvelContract.CharacterEntry.COLUMN_IMAGE_FULLSIZE, params[3]);
            values.put(MarvelContract.CharacterEntry.COLUMN_ORIGIN, "1");

            return null != getContentResolver().insert(MarvelContract.CharacterEntry.CONTENT_URI, values);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result)
                Snackbar.make(mFrameLayout, R.string.character_saved, Snackbar.LENGTH_SHORT).show();
            else
                Snackbar.make(mFrameLayout, R.string.error_character_not_saved, Snackbar.LENGTH_LONG).show();

        }
    }
}
