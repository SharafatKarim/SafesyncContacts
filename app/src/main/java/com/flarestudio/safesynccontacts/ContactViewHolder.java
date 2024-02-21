package com.flarestudio.safesynccontacts;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

public class ContactViewHolder extends ViewHolder {

    ImageView row_image;
    TextView row_name, row_num;

    public ContactViewHolder(@NonNull View itemView) {
        super(itemView);

        row_image = itemView.findViewById(R.id.rowIMAGE);
        row_name = itemView.findViewById(R.id.rowNAME);
        row_num = itemView.findViewById(R.id.rowNUM);
    }
}
