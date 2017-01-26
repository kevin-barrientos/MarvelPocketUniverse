package com.ing_kevin_barrientos.marvelpocketuniverse.characters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ing_kevin_barrientos.marvelpocketuniverse.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

public class NewCharacterDialgoFragment extends DialogFragment implements DialogInterface.OnClickListener {


    private static final int REQUEST_IMAGE_CAPTURE = 0;
    @BindView(R.id.name)
    EditText mNameEditText;
    @BindView(R.id.description)
    EditText mDescriptionEditText;
    @BindView(R.id.comics)
    EditText mNumberOfCommicsEditText;
    @BindView(R.id.image)
    ImageView mImageView;

    OnNewCharacterDialogClickedListener mListener;
    private Uri mPhotoURI;

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
        if (context instanceof OnNewCharacterDialogClickedListener)
            mListener = (OnNewCharacterDialogClickedListener) context;
        else
            throw new ClassCastException("Activity must implmente OnNewCharacterDialogClickedListener interface");
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (Dialog.BUTTON_NEGATIVE == which)
            return;

        mListener.onNewCharacterSavedClicked(mNameEditText.getText().toString(),
                mDescriptionEditText.getText().toString(),
                Integer.valueOf(mNumberOfCommicsEditText.getText().toString()),
                mPhotoURI);
    }

    @OnClick(R.id.take_photo)
    public void onTakePhotoButtonClicked() {
        try {
            if (!dispatchTakePictureIntent()) {
                Toast.makeText(getActivity(), R.string.error_no_camera_found, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.select_file)
    public void onSelectFileoButtonClicked() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1);//one can be replaced with any action code
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    mImageView.setImageURI(mPhotoURI);
                }

                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    mPhotoURI = imageReturnedIntent.getData();
                    mImageView.setImageURI(mPhotoURI);
                }
                break;
        }
    }

    /**
     * Create a file with a timestamp to store an image.
     * @return a file
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Save a file: path for use with ACTION_VIEW intents
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    /**
     * Dispatch an activy to take a picture from the camera if any
     * @return true if the activy was dispatch and false if not
     * @throws IOException
     */
    private boolean dispatchTakePictureIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

            // Create the File where the photo should be stored
            File photoFile = null;

            photoFile = createImageFile();

            //Save uri to set image path on databse
            mPhotoURI = FileProvider.getUriForFile(getActivity(),
                    "com.ing_kevin_barrientos.fileprovider",
                    photoFile);

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            return true;
        }
        return false;
    }

    public interface OnNewCharacterDialogClickedListener {
        void onNewCharacterSavedClicked(String name, String description, int comics, Uri image);
    }
}
