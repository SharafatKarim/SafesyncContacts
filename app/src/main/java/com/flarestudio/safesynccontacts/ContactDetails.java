package com.flarestudio.safesynccontacts;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactDetails extends AppCompatActivity {

    //view
    private TextView nameTv, phoneTv, emailTv, addedTimeTv, updatedTimeTv, noteTv;
    private CircleImageView profileIv;

    private String id;
    // DB Helper
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //init db
        dbHelper = new DbHelper(this);

        //get data from intent
        Intent intent = getIntent();
        id = intent.getStringExtra("contactId");

        //init view
        nameTv = findViewById(R.id.name_BOX);
        phoneTv = findViewById(R.id.phone_BOX);
        emailTv = findViewById(R.id.email_BOX);
        addedTimeTv = findViewById(R.id.addtime_BOX);
        updatedTimeTv = findViewById(R.id.edit_time_BOX);
        noteTv = findViewById(R.id.notes_BOX);
        profileIv = findViewById(R.id.circle_image);

        // EVENTS
        // Delete contact
        Button delete = findViewById(R.id.deleteButton);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.deleteContact(id);

                finish();
                Intent parent_intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(parent_intent);
            }
        });
        loadDataById();

        // Call and message
        Button call = findViewById(R.id.callButton);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phoneTv.getText()));
                startActivity(callIntent);
            }
        });

        Button message = findViewById(R.id.messageButton);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneTv.getText()));
                startActivity(smsIntent);
            }
        });

        Button edit = findViewById(R.id.editButton);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(getBaseContext(), EditActivity.class);
                editIntent.putExtra("contactId", id);
                editIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(editIntent);
                finish();
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
                String name = "" + cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_NAME));
                String image = "" + cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_IMAGE));
                String phone = "" + cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_PHONE));
                String email = "" + cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_EMAIL));
                String note = "" + cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_NOTE));
                String addTime = "" + cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_ADDED_TIME));
                String updateTime = "" + cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_UPDATED_TIME));

                //convert time to dd/mm/yy hh:mm:aa format
                Calendar calendar = Calendar.getInstance(Locale.getDefault());

                calendar.setTimeInMillis(Long.parseLong(addTime));
                String timeAdd = "" + DateFormat.format("dd/MM/yy hh:mm:aa", calendar);

                calendar.setTimeInMillis(Long.parseLong(updateTime));
                String timeUpdate = "" + DateFormat.format("dd/MM/yy hh:mm:aa", calendar);

                //set data
                nameTv.setText(name);
                phoneTv.setText(phone);
                emailTv.setText(email);
                noteTv.setText(note);
                addedTimeTv.setText(timeAdd);
                updatedTimeTv.setText(timeUpdate);

                if (image.isEmpty()) {
                    profileIv.setImageResource(R.drawable.icon_transparent_full);
                } else {
                    profileIv.setImageURI(Uri.parse(image));
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
    }
}