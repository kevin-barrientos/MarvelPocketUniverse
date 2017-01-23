package com.ing_kevin_barrientos.marvelpocketuniverse.characters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
public class CharacterListActivity extends AppCompatActivity implements CharactersAdapter.OnClickListener {

    /**
     * Characters columns to be projected on a query
     */
    private static final String[] CHARACTERS_COLUMNS = {
            MarvelContract.CharacterEntry.COLUMN_MARVELS_ID,
            MarvelContract.CharacterEntry.COLUMN_NAME,
            MarvelContract.CharacterEntry.COLUMN_DESCRIPTION,
    };

    /**
     * Corresponding index for each column
     * @see #CHARACTERS_COLUMNS
     */
    public static final int CHARACTER_MARVELS_ID = 0,
            CHARACTER_NAME = 1,
            CHARACTER_DESCRIPTION = 2;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private Cursor mCharactersCurrsor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        View recyclerView = findViewById(R.id.character_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.character_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        MarvelPocketSyncAdapter.initializeSyncAdapter(this);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mCharactersCurrsor = getContentResolver().query(MarvelContract.CharacterEntry.CONTENT_URI, CHARACTERS_COLUMNS, null, null, null);
        recyclerView.setAdapter(new CharactersAdapter(this, mCharactersCurrsor, this));
    }

    @Override
    public void onItemClick(String id) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(CharacterDetailFragment.ARG_ITEM_ID, id);

            CharacterDetailFragment fragment = new CharacterDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.character_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, CharacterDetailActivity.class);
            intent.putExtra(CharacterDetailFragment.ARG_ITEM_ID, id);

            startActivity(intent);
        }
    }
}
