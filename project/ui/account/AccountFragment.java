package com.example.project.ui.account;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.project.Controller.DBController;
import com.example.project.R;
import com.example.project.View.CircularImageView;
import com.example.project.View.LoggedActivity;
import com.example.project.View.MainActivity;
import com.example.project.databinding.FragmentAccountBinding;
import com.google.android.material.snackbar.Snackbar;

public class AccountFragment extends Fragment
{

    private FragmentAccountBinding binding;
    CircularImageView pfp;
    TextView accountEditDetailsTV, accountChangePfpTV, accountDeleteTV, accountLogOutTV;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        AccountViewModel dashboardViewModel =
                new ViewModelProvider(this).get(AccountViewModel.class);

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String usernameRegex = "^[a-zA-Z0-9]{4,20}$";
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
        int greenColor = Color.parseColor("#00FF00"); // Hexadecimal value for green
        int redColor = Color.parseColor("#FF0000"); // Hexadecimal value for red

        //Instantiate UI Objects
        accountEditDetailsTV = binding.getRoot().findViewById(R.id.editAccountDetailsTextView);
        accountChangePfpTV = binding.getRoot().findViewById(R.id.changePFPTextView);
        accountDeleteTV = binding.getRoot().findViewById(R.id.DeleteAccountTextView);
        accountLogOutTV = binding.getRoot().findViewById(R.id.LogOutTV);
        pfp = binding.getRoot().findViewById(R.id.accountPFP);

        // Create an instance of DBController
        DBController dbController = new DBController(requireContext().getApplicationContext());


        SharedPreferences preferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        byte[] userImageBytes  = dbController.getUserImage(preferences.getInt("userID", -1));
        // Convert the byte array to a Bitmap
        Bitmap userImageBitmap = dbController.getUserImageBitmap(preferences.getInt("userID", -1));

        // Set the Bitmap to your CircularImageView
        pfp.setBitmap(userImageBitmap);

        //Capture Intent and ActivityResultLauncher for taking image
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        ActivityResultLauncher<Intent> imageCaptureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), //Use Start Activity for result contract
                new ActivityResultCallback<ActivityResult>() { //Define an ActivityResultCallback to handle the results
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Bundle extras = result.getData().getExtras();
                            if (extras != null) {
                                Bitmap bp = (Bitmap) extras.get("data");

                                // Transform the bitmap into a circular shape using Glide
                                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bp);
                                circularBitmapDrawable.setCircular(true);

                                //Set ImageView to pic
                                pfp.setBitmap(bp);

                                //Now set the bitmap to save in the database
                                dbController.changePic(preferences.getInt("userID", -1), bp);
                            }
                        }
                    }

                });

        accountEditDetailsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Edit Account Details");



                // Create the layout for the dialog
                LinearLayout layout = new LinearLayout(requireContext());
                layout.setOrientation(LinearLayout.VERTICAL);

                // Add EditText fields for username and password
                EditText usernameEditText = new EditText(requireContext());
                usernameEditText.setHint("Enter new username");
                layout.addView(usernameEditText);

                EditText passwordEditText = new EditText(requireContext());
                passwordEditText.setHint("Enter new password");
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                layout.addView(passwordEditText);

                EditText passwordConfEditText = new EditText(requireContext());
                passwordConfEditText.setHint("Confirm password");
                passwordConfEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                layout.addView(passwordConfEditText);

                // Add three TextViews at the bottom
                TextView textView1 = new TextView(requireContext());
                textView1.setText("Username is already in use");
                textView1.setTextColor(redColor);
                layout.addView(textView1);

                TextView textView2 = new TextView(requireContext());
                textView2.setText("Username must be between 4-20 chars");
                textView2.setTextColor(redColor);
                layout.addView(textView2);

                TextView textView3 = new TextView(requireContext());
                textView3.setText("Password has 8+ chars, 1 Uppercase, 1 Lowercase and a number");
                textView3.setTextColor(redColor);
                layout.addView(textView3);

                TextView textView4 = new TextView(requireContext());
                textView4.setText("Passwords match");
                textView4.setTextColor(redColor);
                layout.addView(textView4);

                builder.setView(layout);

                // Add OK and Cancel buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Retrieve the entered username, password, and confirm password
                        String newUsername = usernameEditText.getText().toString();
                        String newPassword = passwordEditText.getText().toString();
                        String newPasswordConf = passwordConfEditText.getText().toString();

                        //Check if criteria are met
                        if((!dbController.isUsernameInUse(newUsername)) && newUsername.matches(usernameRegex) && newPassword.matches(passwordRegex) && newPassword.equals(newPasswordConf))
                        {
                            //Only Perform change If this goes through
                            dbController.editAccountDetails(preferences.getInt("userID", -1) ,newUsername, newPassword);
                        }

                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel the dialog
                        dialog.cancel();
                    }
                });

                builder.setOnKeyListener(new DialogInterface.OnKeyListener()
                {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
                    {
                        //Username in use
                        if(!dbController.isUsernameInUse(usernameEditText.getText().toString()))
                            textView1.setTextColor(greenColor);
                        else
                            textView1.setTextColor(redColor);

                        //Username matches Regex (4-20 chars)
                        if(usernameEditText.getText().toString().matches(usernameRegex))
                            textView2.setTextColor(greenColor);
                        else
                            textView2.setTextColor(redColor);

                        //Password matches Regex (8+ chars, 1 Uppercase, 1 Lowercase and 1 number)
                        if(passwordEditText.getText().toString().matches(passwordRegex))
                            textView3.setTextColor(greenColor);
                        else
                            textView3.setTextColor(redColor);

                        //Passwords match
                        if(passwordEditText.getText().toString().equals(passwordConfEditText.getText().toString()))
                            textView4.setTextColor(greenColor);
                        else
                            textView4.setTextColor(redColor);
                        return false;
                    }
                });

                // Show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });





        accountChangePfpTV.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                imageCaptureLauncher.launch(captureIntent);
            }
        });

        accountDeleteTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure you want to delete your account?");

                // Add a checkbox to the dialog
                final CheckBox checkBox = new CheckBox(requireContext());
                checkBox.setText("Confirm deletion");
                builder.setView(checkBox);

                // Add a positive button to confirm deletion
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Check if the checkbox is checked
                        if (checkBox.isChecked()) {
                            // Perform the deletion action

                            dbController.deleteAccount(preferences.getInt("userID", -1));

                            // Show a toast message indicating successful deletion
                            Toast.makeText(requireContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show();

                            // Return to the login screen after deletion
                            Intent intent = new Intent(requireContext(), MainActivity.class);
                            startActivity(intent);
                            requireActivity().finish(); // Close the current activity
                        } else {
                            // Show a message indicating the need to confirm deletion
                            Toast.makeText(requireContext(), "Please confirm deletion", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                // Add a negative button to cancel deletion
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Dismiss the dialog if cancellation is selected
                    }
                });

                // Show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        accountLogOutTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.clear(); // Clear all the preferences or specific ones related to user data
                editor.apply();

                // Show a toast message indicating successful logout
                Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();

                // Return to the login screen
                Intent intent = new Intent(requireContext(), MainActivity.class);
                startActivity(intent);
                requireActivity().finish(); // Close the current activity to prevent going back to it using the back button
            }
        });

        return root;
    }



    // Method to convert bitmap to a circular shape
    private Bitmap getRoundedBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }
}