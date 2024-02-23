package com.flarestudio.safesynccontacts;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Objects;


public class SecondFragment extends Fragment {

    //permission constant
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 200;
    private static final int IMAGE_FROM_GALLERY_CODE = 300;
    private static final int IMAGE_FROM_CAMERA_CODE = 400;

    private ImageView imageView;
    private EditText name, phone, email, notes;
    private Uri image_uri;
    private DbHelper dbHelper;

    // string array of permission
    private String[] cameraPermission;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        dbHelper = new DbHelper(getContext());

        //init permission
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        imageView = view.findViewById(R.id.circle_image);
        name = view.findViewById(R.id.name_BOX);
        phone = view.findViewById(R.id.phone_BOX);
        email = view.findViewById(R.id.email_BOX);
        notes = view.findViewById(R.id.notes_BOX);

        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showImagePickerDialog();
            }
        });

        Button insert = view.findViewById(R.id.insertButton);
        Button delete_all = view.findViewById(R.id.deleteButton);

        delete_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.deleteAllContact();
            }
        });
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        return view;
    }

    private void saveData() {
        String name_data = name.getText().toString();
        String phone_data = phone.getText().toString();
        String email_data = email.getText().toString();
        String notes_data = notes.getText().toString();
        String image_data;
        if (image_uri != null) {
            image_data = image_uri.toString();
        } else {
            image_data = "";
        }

        String timeStamp = String.valueOf(System.currentTimeMillis());

        if (name_data.isEmpty() || phone_data.isEmpty()) {
            Toast.makeText(getContext(), "Please Enter Name and Phone", Toast.LENGTH_SHORT).show();
        } else {
            long id = dbHelper.insertContact(image_data, name_data, phone_data, email_data, notes_data, timeStamp, timeStamp);
            Toast.makeText(getContext(), "Contact added", Toast.LENGTH_SHORT).show();
        }
    }

    private void showImagePickerDialog() {

        //option for dialog
        String[] options = {"Camera", "Gallery"};

        // Alert dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        //setTitle
        builder.setTitle("Choose An Option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handle item click
                if (which == 0) { //start from 0 index
                    //camera selected
                    Log.d("permissionCHECK", "Camera permission :: init");
                    if (!checkCameraPermission()) {
                        //request camera permission
                        Log.d("permissionCHECK", "Camera permission :: requested");
                        requestCameraPermission();
                    } else {
                        Log.d("permissionCHECK", "Camera permission :: granted");
                        pickFromCamera();
                    }

                } else if (which == 1) {
                    //Gallery selected
                    Log.d("permissionCHECK", "Gallery permission :: init");
                    if (!checkStoragePermission()) {
                        //request storage permission
                        Log.d("permissionCHECK", "Gallery permission :: requested");
                        requestStoragePermission();
                    } else {
                        Log.d("permissionCHECK", "Gallery permission :: granted");
                        pickFromGallery();
                    }

                }
            }
        }).create().show();
    }

    private void pickFromGallery() {
        //intent for taking image from gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*"); // only Image

        startActivityForResult(galleryIntent, IMAGE_FROM_GALLERY_CODE);
//        new Activity().startActivityForResult(galleryIntent, IMAGE_FROM_GALLERY_CODE);
    }

    private void pickFromCamera() {

//       ContentValues for image info
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "IMAGE_TITLE");
        values.put(MediaStore.Images.Media.DESCRIPTION, "IMAGE_DETAIL");

        //save image_uri
        image_uri = requireActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        imageView.setImageURI(image_uri);

        //intent to open camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);

        startActivityForResult(cameraIntent, IMAGE_FROM_CAMERA_CODE);
//        new Activity().startActivityForResult(cameraIntent, IMAGE_FROM_CAMERA_CODE);
    }

    //check camera permission
    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result & result1;
    }

    //request for camera permission
    private void requestCameraPermission() {
        this.requestPermissions(cameraPermission, CAMERA_PERMISSION_CODE); // handle request permission on override method
    }

    //check storage permission
    private boolean checkStoragePermission() {

        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }

    //request for camera permission
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(requireActivity(), cameraPermission, STORAGE_PERMISSION_CODE);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.d("permissionCHECK", "onResult :: accessed");
//        if (resultCode == RESULT_OK || requestCode == IMAGE_FROM_GALLERY_CODE || requestCode == IMAGE_FROM_CAMERA_CODE) {
//
//            assert data != null;
//            image_uri = data.getData();
//            imageView.setImageURI(image_uri);
//        } else {
//            Toast.makeText(getContext(), "Something wrong", Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("CropOut", "accessed :: on result");
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_FROM_GALLERY_CODE){
                // picked image from gallery
                //crop image
                assert data != null;
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(requireActivity());

            }else if (requestCode == IMAGE_FROM_CAMERA_CODE){
                //picked image from camera
                //crop Image
                CropImage.activity(image_uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(requireActivity());
            }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

                //cropped image received
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                assert result != null;
                image_uri = result.getUri();
                imageView.setImageURI(image_uri);

                Log.d("CropOut", "inside the block");
            }
            else {
                //for error handling
                Toast.makeText(getContext(), "Something wrong", Toast.LENGTH_SHORT).show();

                Log.d("CropOut", "final try catch");
            }
        }
    }
}

