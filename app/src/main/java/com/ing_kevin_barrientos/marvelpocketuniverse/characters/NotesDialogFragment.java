package com.ing_kevin_barrientos.marvelpocketuniverse.characters;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ing_kevin_barrientos.marvelpocketuniverse.R;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotesDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    private static final String ARG_NOTES = "notes";
    @BindView(R.id.notes)
    EditText mNotesTextView;
    private OnSaveClickListener mListener;
    private String mNotes;

    public static DialogFragment newInstance() {
        return new NotesDialogFragment();
    }

    public static DialogFragment newInstance(String note) {
        Bundle args = new Bundle();
        args.putString(ARG_NOTES, note);
        DialogFragment instance = new NotesDialogFragment();
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNotes = getArguments() != null ? getArguments().getString(ARG_NOTES) : "";

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_notes, null);

        ButterKnife.bind(this, view);

        mNotesTextView.setText(mNotes);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.add_note)
                .setView(view)
                .setPositiveButton(R.string.save, this)
                .setNegativeButton(R.string.cancel, this);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnSaveClickListener)
            mListener = (OnSaveClickListener) context;
        else
            throw new ClassCastException("Activity must implment OnSaveClickListenr interface");
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(Dialog.BUTTON_POSITIVE == which)
            mListener.onSaveClicked(mNotesTextView.getText().toString());
    }

    public interface OnSaveClickListener {
        void onSaveClicked(String note);
    }
}
