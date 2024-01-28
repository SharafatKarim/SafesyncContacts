package com.flarestudio.safesynccontacts;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

public class MainActivity extends AppCompatActivity {

    protected final int home = 1;
    protected final int second = 2;
    protected final int third = 3;
    protected final int about = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MeowBottomNavigation bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.add(new MeowBottomNavigation.Model(home, R.drawable.baseline_contacts_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(second, R.drawable.baseline_import_contacts_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(third, R.drawable.baseline_connect_without_contact_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(about, R.drawable.baseline_info_24));

        bottomNavigation.show(home, true);

        bottomNavigation.setOnShowListener(model -> {
            switch (model.getId()) {
                case home:
                    replaceFragment(new HomeFragment());
                    break;
                case second:
                    replaceFragment(new SecondFragment());
                    break;
                case third:
                    replaceFragment(new ThirdFragment());
                    break;
                case about:
                    replaceFragment(new AboutFragment());
                    break;
            }
            return null;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}