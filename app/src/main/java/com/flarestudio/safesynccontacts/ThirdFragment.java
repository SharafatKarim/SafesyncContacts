package com.flarestudio.safesynccontacts;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

class MiniContact {
    private String name;
    private String number;

    public MiniContact() {

    }

    public MiniContact(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}


public class ThirdFragment extends Fragment {

    List<MiniContact> contacts = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_third, container, false);

        EditText searchText = view.findViewById(R.id.edit_search_box);
        Button searchButton = view.findViewById(R.id.button_search);

        EditText nameText = view.findViewById(R.id.update_name_box);
        EditText numberText = view.findViewById(R.id.update_phone_box);
        Button addButton = view.findViewById(R.id.button_update);

        // Database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("contacts");

        // Data entry
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MiniContact contact = new MiniContact(nameText.getText().toString().trim(), numberText.getText().toString().trim());
                if (duplicateSearch(contact.getName(), contact.getNumber())) {
                    Toast.makeText(getContext(), "Contact already exists", Toast.LENGTH_SHORT).show();
                }
                else {
                    String key = myRef.push().getKey();
                    assert key != null;
                    myRef.child(key).setValue(contact);
                    Toast.makeText(getContext(), "Contact added", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Data search
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = searchText.getText().toString().trim();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(searchContact(search))
                        .setTitle(search);
                builder.setCancelable(true);
                builder.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
                builder.show();
            }
        });

        // Read from the database and auto-update
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MiniContact contact;
                    contact = snapshot.getValue(MiniContact.class);
                    assert contact != null;
                    contacts.add(contact);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("DataReadError", "Failed to read value.", error.toException());
            }
        });

        return view;
    }

    private String searchContact(String search) {
        StringBuilder result = new StringBuilder();
        for (MiniContact contact : contacts) {
            if (contact.getNumber().equals(search)) {
                result.append("\n").append(contact.getName());
            }
        }
        if (result.length() == 0) {
            result.append("No contact found");
        } else {
            result.insert(0, "Available names:");
        }
        return result.toString();
    }

    private boolean duplicateSearch(String name, String number) {
        for (MiniContact contact : contacts) {
            if (contact.getName().equals(name) && contact.getNumber().equals(number)) {
                return true;
            }
        }
        return false;
    }
}