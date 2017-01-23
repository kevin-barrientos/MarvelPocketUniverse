package com.ing_kevin_barrientos.marvelpocketuniverse.characters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ing_kevin_barrientos.marvelpocketuniverse.R;
import com.ing_kevin_barrientos.marvelpocketuniverse.data.MarvelContract;

class CharactersAdapter extends RecyclerView.Adapter<CharactersAdapter.CharacterViewHolder> {

    private final Cursor mCharactersCursos;
    private Context mContext;
    private OnClickListener mListener;

    CharactersAdapter(Context context, Cursor charactersCursos, OnClickListener listener) {
        this.mContext = context;
        this.mCharactersCursos = charactersCursos;
        this.mListener = listener;
    }

    @Override
    public CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.character_list_content, parent, false);
        return new CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CharacterViewHolder holder, int position) {
        // Move the mCharactersCursos to the position of the item to be displayed
        if (!mCharactersCursos.moveToPosition(position))
            return; // fail if returned null

        holder.bind(mCharactersCursos);
    }

    @Override
    public int getItemCount() {
        return mCharactersCursos.getCount();
    }

    /**
     * Adapters Vieholder inner class. Used to cached the ui elements
     */
    class CharacterViewHolder extends RecyclerView.ViewHolder{
        private TextView descriptionTextView;
        private TextView nameTextView;

        CharacterViewHolder(View itemView) {
            super(itemView);
            this.descriptionTextView = (TextView) itemView.findViewById(R.id.description);
            this.nameTextView = (TextView) itemView.findViewById(R.id.name);;
        }

        void bind(Cursor item){
            final String id = item.getString(CharacterListActivity.CHARACTER_MARVELS_ID);
            String name = item.getString(CharacterListActivity.CHARACTER_NAME);
            String description = item.getString(CharacterListActivity.CHARACTER_DESCRIPTION);

            this.nameTextView.setText(name);
            this.descriptionTextView.setText(description);

            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(id);
                }
            });
        }

    }

    /**
     * Listener to respond to interactions with the items.
     */
    interface OnClickListener{
        void onItemClick(String id);
    }
}
