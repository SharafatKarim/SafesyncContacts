package com.flarestudio.safesynccontacts;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EditActivity extends AppCompatActivity {


    //permission constant
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 200;
    private static final int IMAGE_FROM_GALLERY_CODE = 300;
    private static final int IMAGE_FROM_CAMERA_CODE = 400;

    //view
    private EditText nameTv, phoneTv, emailTv, noteTv;
    private String name, image, phone, email, note, addedTimeTv;
    private ImageView imageView;

    private String id;
    private Uri image_uri;
    // DB Helper
    private DbHelper dbHelper;
    private String[] cameraPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //init permission
        cameraPermission = new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //init db
        dbHelper = new DbHelper(this);

        //get data from intent
        Intent intent = getIntent();
        id = intent.getStringExtra("contactId");

        //init view
        nameTv = findViewById(R.id.name_BOX);
        phoneTv = findViewById(R.id.phone_BOX);
        emailTv = findViewById(R.id.email_BOX);
//        addedTimeTv = findViewById(R.id.addtime_BOX);
//        updatedTimeTv = findViewById(R.id.edit_time_BOX);
        noteTv = findViewById(R.id.notes_BOX);
        imageView = findViewById(R.id.circle_image);

        // EVENTS
        // Image view
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showImagePickerDialog();
            }
        });

        // Delete contact
        Button delete = findViewById(R.id.deleteButton);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.deleteContact(id);

                Intent parent_intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(parent_intent);
            }
        });
        loadDataById();

        Button edit = findViewById(R.id.editButton);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name, phone, email, note, current_time;

                String image_data;
                if (image_uri != null) {
                    image_data = image_uri.toString();
                } else {
                    image_data = image;
                }

                name = nameTv.getText().toString();
                phone = phoneTv.getText().toString();
                email = emailTv.getText().toString();
                note = noteTv.getText().toString();
                current_time = String.valueOf(System.currentTimeMillis());
                dbHelper.updateContact(id, image_data, name, phone, email, note, addedTimeTv, current_time);

                finish();
                Intent parent_intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(parent_intent);
            }
        });
    }


    private void loadDataById() {
        //get data from database
        //query for find data by id
        String selectQuery = "SELECT * FROM " + Constants.TABLE_NAME + " WHERE " + Constants.C_ID + " =\"" + id + "\"";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                //get data
                name = "" + cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_NAME));
                image = "" + cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_IMAGE));
                phone = "" + cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_PHONE));
                email = "" + cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_EMAIL));
                note = "" + cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_NOTE));
                addedTimeTv = "" + cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_ADDED_TIME));

                //convert time to dd/mm/yy hh:mm:aa format
//                Calendar calendar = Calendar.getInstance(Locale.getDefault());
//
//                calendar.setTimeInMillis(Long.parseLong(addTime));
//                String timeAdd = "" + DateFormat.format("dd/MM/yy hh:mm:aa", calendar);
//
//                calendar.setTimeInMillis(Long.parseLong(updateTime));
//                String timeUpdate = "" + DateFormat.format("dd/MM/yy hh:mm:aa", calendar);

                //set data
                nameTv.setText(name);
                phoneTv.setText(phone);
                emailTv.setText(email);
                noteTv.setText(note);

//                addedTimeTv.setText(timeAdd);
//                addedTimeTv = timeAdd;
//                updatedTimeTv.setText(timeUpdate);
//                updatedTimeTv = timeUpdate;

                if (image.isEmpty()) {
                    imageView.setImageResource(R.drawable.icon_transparent_full);
                } else {
                    imageView.setImageURI(Uri.parse(image));
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
    }


    private void showImagePickerDialog() {

        //option for dialog
        String[] options = {"Camera", "Gallery"};

        // Alert dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

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
        image_uri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        imageView.setImageURI(image_uri);

        //intent to open camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);

        startActivityForResult(cameraIntent, IMAGE_FROM_CAMERA_CODE);
//        new Activity().startActivityForResult(cameraIntent, IMAGE_FROM_CAMERA_CODE);
    }

    //check camera permission
    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result & result1;
    }

    //request for camera permission
    private void requestCameraPermission() {
        this.requestPermissions(cameraPermission, CAMERA_PERMISSION_CODE); // handle request permission on override method
    }

    //check storage permission
    private boolean checkStoragePermission() {

        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }

    //request for camera permission
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, STORAGE_PERMISSION_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == IMAGE_FROM_GALLERY_CODE || requestCode == IMAGE_FROM_CAMERA_CODE) && data != null) {
//            if (requestCode == IMAGE_FROM_GALLERY_CODE || requestCode == IMAGE_FROM_CAMERA_CODE){

            //cropped image received
//                CropImage.ActivityResult result = CropImage.getActivityResult(data);
//                imageUri = result.getUri();
//                profileIv.setImageURI(imageUri);
            image_uri = data.getData();
            imageView.setImageURI(image_uri);
        } else {
            Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show();
            //for error handling
//                Toast.makeText(getApplicationContext(), "Something wrong", Toast.LENGTH_SHORT).show();
        }
//        }
    }
}