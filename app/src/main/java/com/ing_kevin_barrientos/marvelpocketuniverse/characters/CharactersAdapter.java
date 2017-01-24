package com.ing_kevin_barrientos.marvelpocketuniverse.characters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ing_kevin_barrientos.marvelpocketuniverse.R;
import com.ing_kevin_barrientos.marvelpocketuniverse.data.MarvelContract;
import com.nostra13.universalimageloader.core.ImageLoader;

class CharactersAdapter extends RecyclerView.Adapter<CharactersAdapter.CharacterViewHolder> {

    private Cursor mCharactersCursos;
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
        if (mCharactersCursos == null || !mCharactersCursos.moveToPosition(position))
            return; // fail if returned null

        holder.bind(mCharactersCursos);
    }

    @Override
    public int getItemCount() {
        return mCharactersCursos == null ? 0 : mCharactersCursos.getCount();
    }

    public void swapCursor(Cursor data) {
        if(mCharactersCursos != null)
            mCharactersCursos.close();
        mCharactersCursos = data;
    }

    /**
     * Adapters Vieholder inner class. Used to cached the ui elements
     */
    class CharacterViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private TextView nameTextView;

        CharacterViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.image);
            this.nameTextView = (TextView) itemView.findViewById(R.id.name);;
        }

        void bind(Cursor item){
            final String id = item.getString(CharacterListActivity.CHARACTER_MARVELS_ID);
            String name = item.getString(CharacterListActivity.CHARACTER_NAME);

            this.nameTextView.setText(name);
            ImageLoader.getInstance().displayImage(item.getString(CharacterListActivity.CHARACTER_IMAGEURL), this.imageView);

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
