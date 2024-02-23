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

    private EditText name, phone, email, notes;
    private DbHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        dbHelper = new DbHelper(getContext());

        name = view.findViewById(R.id.name_BOX);
        phone = view.findViewById(R.id.phone_BOX);
        email = view.findViewById(R.id.email_BOX);
        notes = view.findViewById(R.id.notes_BOX);

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
        String image_data = "";

        String timeStamp = String.valueOf(System.currentTimeMillis());

        if (name_data.isEmpty() || phone_data.isEmpty()) {
            Toast.makeText(getContext(), "Please Enter Name and Phone", Toast.LENGTH_SHORT).show();
        } else {
            long id = dbHelper.insertContact(image_data, name_data, phone_data, email_data, notes_data, timeStamp, timeStamp);
            Toast.makeText(getContext(), "Contact added", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveDataWithoutImage() {
        String name_data = name.getText().toString();
        String phone_data = phone.getText().toString();
        String email_data = email.getText().toString();
        String notes_data = notes.getText().toString();
        String image_data = "";

        String timeStamp = String.valueOf(System.currentTimeMillis());

        if (name_data.isEmpty() || phone_data.isEmpty()) {
            Toast.makeText(getContext(), "Please Enter Name and Phone first", Toast.LENGTH_SHORT).show();
        } else {
            long id = dbHelper.insertContact(image_data, name_data, phone_data, email_data, notes_data, timeStamp, timeStamp);

            Intent editIntent = new Intent(getContext(), EditActivity.class);
            editIntent.putExtra("contactId", String.valueOf(id));
            editIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(editIntent);
        }
    }
}

