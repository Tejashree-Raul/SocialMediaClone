package com.example.app1;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ActionBar actionBar;
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        actionBar = getSupportActionBar();

        // Set default title for the action bar
        actionBar.setTitle("Home");

        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // If the user is not logged in, redirect to login screen
        if (firebaseUser == null) {
            // Redirect to LoginActivity (You need to add this intent in your code)
            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            finish();
        }

        // Initialize BottomNavigationView and set listener
        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        // Show HomeFragment by default when app is first opened
        if (savedInstanceState == null) {
            // Fragment should be shown only once
            showFragment(new HomeFragment(), "homeFragment");
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;
            String fragmentTag = "";

            if (menuItem.getItemId() == R.id.nav_home) {
                actionBar.setTitle("Home");
                selectedFragment = new HomeFragment();
                fragmentTag = "homeFragment";
            } else if (menuItem.getItemId() == R.id.nav_profile) {
                actionBar.setTitle("Profile");
                selectedFragment = new ProfileFragment();
                fragmentTag = "profileFragment";
            } else if (menuItem.getItemId() == R.id.nav_users) {
                actionBar.setTitle("Users");
                selectedFragment = new UsersFragment();
                fragmentTag = "usersFragment";
            } else if (menuItem.getItemId() == R.id.nav_chat) {
                actionBar.setTitle("Chats");
                selectedFragment = new ChatListFragment();
                fragmentTag = "chatFragment";
            } else if (menuItem.getItemId() == R.id.nav_addblogs) {
                actionBar.setTitle("Add Blogs");
                selectedFragment = new AddBlogsFragment();
                fragmentTag = "addBlogsFragment";
            }

            // Show the selected fragment if not already shown
            if (selectedFragment != null) {
                showFragment(selectedFragment, fragmentTag);
            }

            return true;
        }
    };

    private void showFragment(Fragment fragment, String tag) {
        // Check if the fragment is already in the fragment manager
        if (getSupportFragmentManager().findFragmentByTag(tag) == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content, fragment, tag);
            fragmentTransaction.commit();
        }
    }
}
