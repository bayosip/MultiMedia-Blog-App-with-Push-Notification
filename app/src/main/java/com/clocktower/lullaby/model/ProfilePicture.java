package com.clocktower.lullaby.model;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.clocktower.lullaby.R;
import com.clocktower.lullaby.model.utilities.Constants;
import com.clocktower.lullaby.model.utilities.GeneralUtil;
import com.clocktower.lullaby.view.activities.Home;
import com.clocktower.lullaby.view.fragments.home.MusicSelectorDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;

public class ProfilePicture {
    private static Uri profileUri;
    private static String username, imageURL;


    public ProfilePicture() {
    }

    public ProfilePicture(String username) {
        if(!TextUtils.isEmpty(username.trim())) {
            //this.profileUri = profileUri;
            this.username = username;
        }
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        ProfilePicture.imageURL = imageURL;
    }

    public Uri getProfileUri() {
        return profileUri;
    }

    public void setProfileUri(Uri profileUri) {
        ProfilePicture.profileUri = profileUri;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        ProfilePicture.username = username;
    }



    public void changeProfilePic(final Fragment fragment){
        final Dialog change = new Dialog(fragment.getContext());
        change.requestWindowFeature(Window.FEATURE_NO_TITLE);
        change.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        change.setContentView(R.layout.picture_option_popup);
        change.setCancelable(true);

        Button camera = change.findViewById(R.id.buttonCam);
        Button gallery = change.findViewById(R.id.buttonGallery);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fragment.getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                    if (Build.VERSION.SDK_INT >=23) {
                        dispatchTakePictureIntent(fragment);
                    }
                    else dispatchTakePictureIntentAPILow(fragment);
                }
                else {
                    GeneralUtil.message("Oops! No camera found.");
                }
                change.dismiss();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageFileChooser(fragment);
                change.dismiss();
            }
        });
        change.show();
    }

    private void BringImagePicker(final Fragment fragment) {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(fragment.getActivity());

    }


    private void openImageFileChooser(final Fragment fragment){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            Dexter.withActivity(fragment.getActivity())
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            BringImagePicker(fragment);
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
                                                                       PermissionToken token) {
                            token.cancelPermissionRequest();
                        }
                    }).check();
        }
    }

    private void dispatchTakePictureIntent(final Fragment fragment) {
        File mainDirectory = null;
        try {
            mainDirectory = getOutputMediaFile(fragment);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(mainDirectory!= null) {
            Uri uriFilePath = FileProvider.getUriForFile(fragment.getContext(),
                    fragment.getContext().getPackageName() + ".provider", mainDirectory);
            Dexter.withActivity(fragment.getActivity())
                    .withPermission(Manifest.permission.CAMERA)
                    .withListener(new PermissionListener() {

                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (intent.resolveActivity(fragment.getActivity().getPackageManager()) != null) {
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                //intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFilePath);
                                intent.putExtra("android.intent.extra.quickCapture", true);
                                fragment.startActivityForResult(intent, Constants.REQUEST_IMAGE_CAPTURE);
                            }
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
                                                                       PermissionToken token) {
                            token.cancelPermissionRequest();
                        }
                    }).check();
        }
    }

    private void dispatchTakePictureIntentAPILow(Fragment fragment) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(fragment.getActivity().getPackageManager()) != null) {
            fragment.startActivityForResult(takePictureIntent, Constants.REQUEST_IMAGE_CAPTURE);
        }
    }

    public static File getOutputMediaFile(Fragment fragment) throws IOException {
        File mediaStorageDir = fragment.getActivity().getExternalFilesDir(
                Environment.DIRECTORY_PICTURES);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
            File image = File.createTempFile("COZA_IMG", ".png", mediaStorageDir);
            return image;
    }

}
