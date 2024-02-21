package com.flarestudio.safesynccontacts;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFragment extends Fragment {

    SearchView searchView;
    TextView titleText;
    private RecyclerView contactRv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        DbHelper dbHelper = new DbHelper(getContext());

        contactRv = view.findViewById(R.id.recycleVIEW);
        searchView = view.findViewById(R.id.searchVIEW);
        titleText = view.findViewById(R.id.main_TITLE);

        contactRv.setHasFixedSize(true);
        contactRv.setLayoutManager(new LinearLayoutManager(getContext()));

        contactRv.setAdapter(new AdapterContact(getContext(), dbHelper.getAllData()));

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Log.d("searchCLICK", "Serarch focused :: Hiding titlebar");
                    titleText.setVisibility(View.INVISIBLE);
                }
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.d("searchCLICK", "Serarch closed :: Getting TitleBar back");
                titleText.setVisibility(View.VISIBLE);
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchContact(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("searchCLICK", "Serarch clicked :: Triggering onchange");
                titleText.setVisibility(View.INVISIBLE);

                searchContact(newText);
                return false;
            }
        });

        dbHelper.close();
        return view;
    }

    private void searchContact(String newText) {
        DbHelper dbHelper = new DbHelper(getContext());
        contactRv.setAdapter(new AdapterContact(getContext(), dbHelper.getSearchContact(newText)));
        dbHelper.close();
    }
}