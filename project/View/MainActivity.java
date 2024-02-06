package com.example.project.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.project.Controller.DBController;
import com.example.project.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity
{
    //Declare binding and UI objects
    private ActivityMainBinding binding;
    Button loginBtn, createAcctBtn;
    EditText usernameInput, passwordInput;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Instantiate binding and set content view
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Instantiate UI objects
        loginBtn = binding.btnLogin;
        createAcctBtn = binding.btnCreateAccount;
        usernameInput = binding.usernameInput;
        passwordInput = binding.passwordInput;

        //Set OnClick Listeners
        loginBtn.setOnClickListener(new View.OnClickListener() {
            //Checks whether login credentials are valid
            @Override
            public void onClick(View v) {
                // Create an instance of DBController
                DBController dbController = new DBController(getApplicationContext());

                String inputUser, inputPass;
                inputUser = usernameInput.getText().toString();
                inputPass = passwordInput.getText().toString();

                int userId = dbController.getUserIdByUsername(inputUser);

                Log.d("validateIf", String.valueOf(dbController.validateLogin(inputUser, inputPass)));

                if (dbController.validateLogin(inputUser, inputPass)) {
                    // Save user ID to Shared Preferences
                    SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("userID", userId);
                    editor.apply();


                    Intent intent1 = new Intent(MainActivity.this, LoggedActivity.class);
                    startActivity(intent1);
                } else {
                    // Login Unsuccessful Snackbar
                    Log.d("LoginValidation", "Login unsuccessful");
                    Snackbar.make(findViewById(android.R.id.content), "Invalid Login", Snackbar.LENGTH_SHORT).show();
                }
            }
        });


        //Redirects to the registration page
        createAcctBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Redirect to new intent with account creation form
                Intent intent = new Intent(MainActivity.this, registrationActivity.class);
                startActivity(intent);
            }
        });
    }
}
