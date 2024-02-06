package com.example.project.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.Controller.DBController;
import com.example.project.databinding.ActivityRegistrationBinding;

public class registrationActivity extends AppCompatActivity
{
    private ActivityRegistrationBinding binding;
    EditText userInput, passInput, passConfInput;
    TextView errUser, errUserLength, errPass, errPassMatch;
    Button btnCreate, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Create an instance of DBController
        DBController dbController = new DBController(getApplicationContext());

        String usernameRegex = "^[a-zA-Z0-9]{4,20}$";
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
        int greenColor = Color.parseColor("#00FF00"); // Hexadecimal value for green
        int redColor = Color.parseColor("#FF0000"); // Hexadecimal value for red

        //Instantiate UI objects
        userInput = binding.regUsernameInput;
        passInput = binding.regPasswordInput;
        passConfInput = binding.regPasswordConfInput;
        errUser = binding.regUserErrTV;
        errUserLength = binding.regUserLengthErr;
        errPass = binding.regPassErrTV;
        errPassMatch = binding.regPassMatchErrTV;
        btnCreate = binding.btnCreateAccountConf;
        btnBack = binding.btnBack;

        //Add Listeners for EditTexts
        View.OnKeyListener textListener = new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                //CheckRegexes and update UI indicators from red to green when criteria is met



                //Username in use
                if (dbController.isUsernameInUse(userInput.getText().toString()))
                    errUser.setTextColor(redColor);
                else
                    errUser.setTextColor(greenColor);

                //Username matches regex
                if (userInput.getText().toString().matches(usernameRegex))
                    errUserLength.setTextColor(greenColor);
                else
                    errUserLength.setTextColor(redColor);

                //Password matches regex
                if (passInput.getText().toString().matches(passwordRegex))
                    errPass.setTextColor(greenColor);
                else
                    errPass.setTextColor(redColor);

                //Inputted Passwords Match
                if (passInput.getText().toString().equals(passConfInput.getText().toString()))
                    errPassMatch.setTextColor(greenColor);
                else
                    errPassMatch.setTextColor(redColor);

                return false;
            }
        };

        userInput.setOnKeyListener(textListener);
        passInput.setOnKeyListener(textListener);
        passConfInput.setOnKeyListener(textListener);

        //Set OnClick Listeners
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = userInput.getText().toString();
                String pass = passInput.getText().toString();

                // Create an instance of DBController
                Log.d("ifCheck", String.valueOf(( (userInput.getText().toString().matches(usernameRegex)) && (passInput.getText().toString().matches(passwordRegex)) && (passConfInput.getText().toString().equals(passInput.getText().toString())))));
                //Ensure that all fields are properly filled before creating acc
                if( (userInput.getText().toString().matches(usernameRegex)) && (passInput.getText().toString().matches(passwordRegex)) && (passConfInput.getText().toString().equals(passInput.getText().toString())))
                {
                    //Runs same check as the onKeyListener, but instead of changing colors, will throw a toast if info needs to be added
                    // Call validateLogin using the instance
                    boolean result = dbController.createUser(user, pass);

                    if (result)
                    {
                        Toast.makeText(getApplicationContext(), "Account successfully created", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(registrationActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getApplicationContext(), "Check Input Fields", Toast.LENGTH_SHORT).show();

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(registrationActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
