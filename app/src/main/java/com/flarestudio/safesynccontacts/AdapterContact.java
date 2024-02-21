package com.flarestudio.safesynccontacts;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterContact extends RecyclerView.Adapter<ContactViewHolder> {

    private Context context;
    private ArrayList<ModelContact> contactList;

    public AdapterContact(Context context, ArrayList<ModelContact> contactList) {
        this.context = context;
        this.contactList = contactList;
//        dbHelper = new DbHelper(context);
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_contact_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        ModelContact modelContact = contactList.get(position);

        //get data
        //we need only All data
        String id = modelContact.getId();
        String image = modelContact.getImage();
        String name = modelContact.getName();
        String phone= modelContact.getPhone();
        String email = modelContact.getEmail();
        String note = modelContact.getNote();
        String addedTime = modelContact.getAddedTime();
        String updatedTime = modelContact.getUpdatedTime();

        //set data in view
        holder.row_name.setText(name);
        holder.row_num.setText(phone);
//        if (image.isEmpty()){
//            holder.row_image.setImageResource(R.drawable.icon_transparent_full);
//        }else {
            holder.row_image.setImageURI(Uri.parse(image));
//        }
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }
}
