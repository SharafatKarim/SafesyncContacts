package com.flarestudio.safesynccontacts;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


public class SecondFragment extends Fragment {

    private static final int REQUEST_READ_CONTACTS_PERMISSION = 1;
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
        Button import_phone = view.findViewById(R.id.importPHONE);
        ImageView imageView = view.findViewById(R.id.circle_image);

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
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataWithoutImage();
            }
        });
        import_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhoneContacts();
            }
        });

        return view;
    }

    private void addPhoneContacts() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS_PERMISSION);
        } else {
            readContacts();
        }
    }

    private void readContacts() {
        ContentResolver contentResolver = requireActivity().getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor != null) {
            try {
                Log.i("CONTACT_PROVIDER_DEMO", "TOTAL # of Contacts  ::: " + cursor.getCount());
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int emailIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
                int noteIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE);
                int imageIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_URI);

                while (cursor.moveToNext()) {
                    // Check if column indices are valid
                    if (nameIndex != -1 && numberIndex != -1 && emailIndex != -1 && noteIndex != -1) {
                        String contactName = cursor.getString(nameIndex);
                        String contactNumber = cursor.getString(numberIndex);
                        String image_uri = cursor.getString(imageIndex);
                        String timeStamp = String.valueOf(System.currentTimeMillis());

                        if (image_uri == null) image_uri = "";

                        dbHelper.insertContact(image_uri, contactName, contactNumber, "", "", timeStamp, timeStamp);

                        Log.i("CONTACT_PROVIDER_DEMO", "Contact Name ::: " + contactName + " Ph #  ::: " + contactNumber + " image ::: " + image_uri);
                    } else {
                        Log.e("CONTACT_PROVIDER_DEMO", "Column index not found");
                    }
                }
            } finally {
                cursor.close();
            }
        }
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

