package com.ing_kevin_barrientos.marvelpocketuniverse.characters;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.BoolRes;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ing_kevin_barrientos.marvelpocketuniverse.R;
import com.ing_kevin_barrientos.marvelpocketuniverse.data.MarvelContract;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A fragment representing a single character detail screen.
 * This fragment is either contained in a {@link CharacterListActivity}
 * in two-pane mode (on tablets) or a {@link CharacterDetailActivity}
 * on handsets.
 */
public class CharacterDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    /**
     * Corresponding index for each column
     *
     * @see #CHARACTER_COLUMNS
     */
    public static final int CHARACTER_MARVELS_ID = 0,
            CHARACTER_NAME = 1,
            CHARACTER_DESCRIPTION = 2,
            CHARACTER_MODIFIED = 3,
            CHARACTER_IMAGEURL = 4,
            CHARACTER_COMICS = 5,
            CHARACTER_FAVORITE = 6,
            CHARACTER_NOTES = 7;
    /**
     * Loaders ID, each loader must have a unique id
     */
    private static final int CHARACTER_LOADER = 1;
    /**
     * Characters columns to be projected on a query
     */
    private static final String[] CHARACTER_COLUMNS = {
            MarvelContract.CharacterEntry.COLUMN_MARVELS_ID,
            MarvelContract.CharacterEntry.COLUMN_NAME,
            MarvelContract.CharacterEntry.COLUMN_DESCRIPTION,
            MarvelContract.CharacterEntry.COLUMN_MODIFIED_DATE,
            MarvelContract.CharacterEntry.COLUMN_IMAGE_FULLSIZE,
            MarvelContract.CharacterEntry.COLUMN_COMICS,
            MarvelContract.CharacterEntry.COLUMN_FAVORITE,
            MarvelContract.CharacterEntry.COLUMN_NOTE,
    };
    @BindView(R.id.image)
    ImageView mImageView;
    @BindView(R.id.character_detail)
    TextView mDescriptionTextView;
    @BindView(R.id.character_comics)
    TextView mComicsTextView;
    @BindView(R.id.character_notes)
    TextView mNotesTextView;

    /**
     * Character's id
     */
    private String mCharacterId;
    private FloatingActionButton mMarkAsFavoritFab;

    // Determine if the character is mark as favorite
    private boolean mIsFavorite;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CharacterDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mCharacterId = getArguments().getString(ARG_ITEM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.character_detail, container, false);

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get the fav reference. It could be from either CharacterDetailActiviy or CharactersListActiviy.
        // since the both have in theirs view a FAB called fab, it wont matter wich one we are on.
        mMarkAsFavoritFab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        mMarkAsFavoritFab.setOnClickListener(this);

        getLoaderManager().initLoader(CHARACTER_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                MarvelContract.CharacterEntry.buildCharacterUri(mCharacterId),
                CHARACTER_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() == 1) {
            data.moveToFirst();
            setUpView(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //do nothing
    }

    @Override
    public void onClick(View v) {
        mIsFavorite = !mIsFavorite;
        new UpdateCharacterStatus().execute(mCharacterId, mIsFavorite ? "1" : "0");
    }

    @OnClick(R.id.character_notes)
    public void onClick(){
        NotesDialogFragment.newInstance(mNotesTextView.getText().toString()).show(getFragmentManager(), NotesDialogFragment.class.getSimpleName());
    }

    private void setUpView(Cursor data) {
        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);

        // The appBarLayout will exist only if we are in a single pane. That means
        // it only exists on CharacterDetailActivity
        if (appBarLayout != null) {
            appBarLayout.setTitle(data.getString(CHARACTER_NAME));
        }

        mDescriptionTextView.setText(data.getString(CHARACTER_DESCRIPTION));
        mComicsTextView.setText(getActivity().getString(R.string.number_of_comics, data.getInt(CHARACTER_COMICS)));
        mNotesTextView.setText(data.getString(CHARACTER_NOTES));

        //set up image view
        ImageLoader.getInstance().displayImage(data.getString(CHARACTER_IMAGEURL), mImageView);

        // Update Fab drawable
        mIsFavorite = data.getInt(CHARACTER_FAVORITE) == 1;
        toogleFabStar(mIsFavorite);
    }

    /**
     * Sets the FAB's drawable to an empty or full star whenever it is favorite.
     * @param isFav
     */
    private void toogleFabStar(boolean isFav) {
        if (isFav)
            mMarkAsFavoritFab.setImageDrawable(ContextCompat.getDrawable(getContext(), android.R.drawable.btn_star_big_on));
        else
            mMarkAsFavoritFab.setImageDrawable(ContextCompat.getDrawable(getContext(), android.R.drawable.btn_star_big_off));
    }

    /**
     * Executes {@link UpdateNote}
     * @param note any string
     */
    public void saveNote(String note) {
        new UpdateNote().execute(mCharacterId, note);
    }

    /**
     * Updates characters status. The required params are: [marvels_id, status].
     * If it was mark as favorite, it will trigger a dialog to enter an
     * optional note.
     */
    class UpdateCharacterStatus extends AsyncTask<String, String, Boolean>{
        @Override
        protected Boolean doInBackground(String... params) {
            String marvels_id = params[0];
            String favorite = params[1];

            ContentValues values = new ContentValues(1);
            values.put(MarvelContract.CharacterEntry.COLUMN_FAVORITE, favorite);
            int updatedRows = getActivity().getContentResolver().update(MarvelContract.CharacterEntry.buildCharacterUri(marvels_id), values, null, null);

            return updatedRows == 1;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if(result && mIsFavorite){
                NotesDialogFragment.newInstance().show(getFragmentManager(), NotesDialogFragment.class.getSimpleName());
            }
        }
    }

    /**
     * Updates the note field. The required params are: [marvels_id, note]
     */
    class UpdateNote extends AsyncTask<String, String, Boolean>{

        @Override
        protected Boolean doInBackground(String... params) {
            String marvels_id = params[0];
            String note = params[1];

            ContentValues values = new ContentValues(1);
            values.put(MarvelContract.CharacterEntry.COLUMN_NOTE, note);
            int updatedRows = getActivity().getContentResolver().update(MarvelContract.CharacterEntry.buildCharacterUri(marvels_id), values, null, null);

            return updatedRows == 1;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if(result)
                Toast.makeText(getActivity(), R.string.saved, Toast.LENGTH_SHORT).show();
        }
    }
}
