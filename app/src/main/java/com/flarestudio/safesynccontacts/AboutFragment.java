package com.flarestudio.safesynccontacts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class AboutFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        Button github = view.findViewById(R.id.GitHub_REPO);
        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String GITHUB_URL = "https://github.com/SharafatKarim/SafeSyncContacts";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_URL));
                startActivity(intent);
            }});

        Button issue = view.findViewById(R.id.Issue_SUBMISSION);
        issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ISSUE_URL = "https://github.com/SharafatKarim/SafeSyncContacts/issues";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ISSUE_URL));
                startActivity(intent);
            }
        });

        return view;
    }
}