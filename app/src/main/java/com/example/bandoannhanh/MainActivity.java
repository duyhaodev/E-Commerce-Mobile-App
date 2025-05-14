package com.example.bandoannhanh;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.bandoannhanh.Fragment.AccountFragment;
import com.example.bandoannhanh.Fragment.CartFragment;
import com.example.bandoannhanh.Fragment.ExploreFragment;
import com.example.bandoannhanh.Fragment.FavouriteFragment;
import com.example.bandoannhanh.Fragment.HomeFragment;
import com.example.bandoannhanh.Model.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

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

        replaceFragment(new HomeFragment());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setBackground(null);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.shop)
                replaceFragment(new HomeFragment());
            else if (item.getItemId() == R.id.explore)
                replaceFragment(new ExploreFragment());
            else if (item.getItemId() == R.id.favourite)
                replaceFragment(new FavouriteFragment());
            else if (item.getItemId() == R.id.cart)
                replaceFragment(new CartFragment());
            else if (item.getItemId() == R.id.account)
                replaceFragment(new AccountFragment());
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}