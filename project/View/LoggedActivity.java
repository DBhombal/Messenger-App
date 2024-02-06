package com.example.project.View;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;


import com.example.project.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.project.databinding.ActivityLoggedBinding;
import com.google.android.material.snackbar.Snackbar;

public class LoggedActivity extends AppCompatActivity {

    private ActivityLoggedBinding binding;
    TextView accountEditDetailsTV, accountChangePfpTV, accountDeleteTV, accountLogOutTV;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoggedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final int navHome = R.id.navigation_home;
        final int navNotification = R.id.navigation_notifications;
        final int navAccount = R.id.navigation_account;

        // Retrieve the userID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int userID = sharedPreferences.getInt("userID", -1);
        Log.d("LoggedUser", String.valueOf(userID));

        if (userID != -1)
        {
            boolean isFirstTimeLogin = sharedPreferences.getBoolean("isFirstTimeLogin", true);

            if (isFirstTimeLogin) {
                // Show Snackbar for the first time
                Snackbar.make(findViewById(android.R.id.content), "Login Successful", Snackbar.LENGTH_SHORT).show();

                // Update SharedPreferences to indicate that it's not the first time anymore
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isFirstTimeLogin", false);
                editor.apply();
            }

            BottomNavigationView navView = findViewById(R.id.nav_view);
            navView.getMenu().getItem(0).setChecked(true); //Sets the home tab to be selected by default
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_account, R.id.navigation_notifications).build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_logged);
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(binding.navView, navController);

            navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    NavController navController = Navigation.findNavController(LoggedActivity.this, R.id.nav_host_fragment_activity_logged);
                    int itemId = item.getItemId();
                    if (itemId == navHome)
                    {
                        // Home
                        navController.navigate(R.id.navigation_home);
                        toolbar.setTitle(R.string.title_home);
                        return true;
                    }
                    else if (itemId == navNotification)
                    {
                        // Notifications
                        navController.navigate(R.id.navigation_notifications);
                        toolbar.setTitle(R.string.title_notifications);
                        return true;
                    }
                    else if (itemId == navAccount)
                    {
                        // Account
                        navController.navigate(R.id.navigation_account);
                        toolbar.setTitle(R.string.title_account);

                        return true;
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), String.valueOf(itemId), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            });
        }
        else
        {
            // Redirect to login page or handle unauthorized access
            Snackbar.make(findViewById(android.R.id.content), "Unauthorized access", Snackbar.LENGTH_LONG).show();

            Intent intent = new Intent(LoggedActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Finish the current activity to prevent going back
        }
    }



}
