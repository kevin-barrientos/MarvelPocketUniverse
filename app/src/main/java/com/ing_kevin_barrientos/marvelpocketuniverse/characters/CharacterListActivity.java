package com.ing_kevin_barrientos.marvelpocketuniverse.characters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
        NotesDialogFragment.OnSaveClickListener{

    /**
     * Characters columns to be projected on a query
     */
    private static final String[] CHARACTERS_COLUMNS = {
            MarvelContract.CharacterEntry.COLUMN_MARVELS_ID,
            MarvelContract.CharacterEntry.COLUMN_NAME,
            MarvelContract.CharacterEntry.COLUMN_IMAGE_FULLSIZE,
    };

    /**
     * Corresponding index for each column
     * @see #CHARACTERS_COLUMNS
     */
    public static final int CHARACTER_MARVELS_ID = 0,
            CHARACTER_NAME = 1,
            CHARACTER_IMAGEURL = 2;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private CharactersAdapter mCharactersAdapter;
    private static final String FAVORITES_KEY = "favorites";

    //flag to avoid loops
    private boolean isFirstime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_list);

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

        if(!mTwoPane)
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
        if(id == R.id.show_all){
            getSupportLoaderManager().restartLoader(1, null, this);
        }else if(id == R.id.show_favorites){
            Bundle args = new Bundle();
            args.putBoolean(FAVORITES_KEY, true);
            getSupportLoaderManager().restartLoader(1, args, this);
        }

        return true;
    }

    @Override
    public void onItemClick(String id) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(CharacterDetailFragment.ARG_ITEM_ID, id);

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
        if(args == null)
            return new CursorLoader(this, MarvelContract.CharacterEntry.CONTENT_URI, CHARACTERS_COLUMNS, null, null, null);
        return new CursorLoader(this, MarvelContract.CharacterEntry.CONTENT_URI, CHARACTERS_COLUMNS, MarvelContract.CharacterEntry.COLUMN_FAVORITE + " = 1", null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(data != null && (data.getCount() > 0 || !isFirstime)){
            mCharactersAdapter.swapCursor(data);
            mCharactersAdapter.notifyDataSetChanged();
        }else if(isFirstime){
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
}
