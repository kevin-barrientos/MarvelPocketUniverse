package com.ing_kevin_barrientos.marvelpocketuniverse.characters;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.ing_kevin_barrientos.marvelpocketuniverse.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewCharacterDialgoFragment extends DialogFragment implements DialogInterface.OnClickListener {


    @BindView(R.id.name)
    EditText mNameEditText;
    @BindView(R.id.description)
    EditText mDescriptionEditText;
    @BindView(R.id.comics)
    EditText mNumberOfCommicsEditText;
    OnNewCharacterDialogClickedListener mListener;

    public static NewCharacterDialgoFragment newInstance() {
        return new NewCharacterDialgoFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_new_character, null);

        ButterKnife.bind(this, view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.create_a_new_character)
                .setView(view)
                .setPositiveButton(R.string.save, this)
                .setNegativeButton(R.string.cancel, this);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnNewCharacterDialogClickedListener)
            mListener = (OnNewCharacterDialogClickedListener) context;
        else
            throw new ClassCastException("Activity must implmente OnNewCharacterDialogClickedListener interface");
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(Dialog.BUTTON_NEGATIVE == which)
            return;

        mListener.onNewCharacterSavedClicked(mNameEditText.getText().toString(),
                mDescriptionEditText.getText().toString(),
                Integer.valueOf(mNumberOfCommicsEditText.getText().toString()),
                null);
    }

    @OnClick(R.id.take_photo)
    public void onTakePhotoButtonClicked(){

    }

    @OnClick(R.id.select_file)
    public void onSelectFileoButtonClicked(){

    }

    public interface OnNewCharacterDialogClickedListener{
        void onNewCharacterSavedClicked(String name, String description, int comics, Object image);
    }
}
