package com.example.project.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.Controller.DBController;
import com.example.project.R;
import com.example.project.databinding.ActivityChatBinding;
import com.example.project.databinding.ActivityRegistrationBinding;
import com.example.project.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.Locale;

public class chatActivity extends AppCompatActivity
{
    private ActivityChatBinding binding;
    private DBController dbController;
    private SpeechRecognizer speechRecognizer;
    private Intent recognizerIntent;
    private RecognitionListener recognitionListener;
    private boolean isRecording = false;
    private int chatID;
    private static final int REQUEST_CODE_RECORD_AUDIO = 1001;
    private static final int REQUEST_CODE_CAMERA = 1002;
    private static final int REQUEST_IMAGE_CAPTURE = 2; // Changed the value to avoid duplication



    EditText msg;
    Button back, send, rec, voice, pic;
    TextView user;
    RecyclerView recyclerView;
    messageModel message;
    int messageId;
    int senderId ;
    int recipientId;
    byte[] image;
    String messageContent;
    String timestamp;

    @SuppressLint({"ClickableViewAccessibility", "Range"})
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        msg = binding.editTextText;
        back = binding.btnBackChat;
        send = binding.btnSend;
        rec = binding.btnRec;
        user = binding.chatUsername;
        pic = binding.button2;
        voice = binding.btnVoice;
        recyclerView = binding.recMsgs;
        final int originalColor = getResources().getColor(R.color.default_color);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        dbController = new DBController(getApplicationContext());
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);


        // Initialize recognizerIntent
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // Initialize SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        if (speechRecognizer != null)
        {
            speechRecognizer.setRecognitionListener(new RecognitionListener()
            {
                @Override
                public void onReadyForSpeech(Bundle params)
                {
                    // This method is called when the system is ready to start speech recognition.
                    // You might update UI elements to indicate that the system is ready.
                    Log.d("SpeechRecognition", "Ready for speech input");
                    // For example, update UI elements or show a message here
                }

                @Override
                public void onBeginningOfSpeech()
                {
                    // This method is called when the speech recognition system detects the start of speech input.
                    // This indicates that the user has started speaking.
                    Log.d("SpeechRecognition", "Speech input has begun");
                    // You can perform actions like updating UI elements or starting recording here
                }

                @Override
                public void onRmsChanged(float rmsdB)
                {
                    // This method provides information about the RMS (Root Mean Square) dB (decibel) values of the audio input.
                    // It can be used to visualize or track the loudness of the incoming audio.
                    // For example, update a visual representation of the audio level in your UI
                    Log.d("SpeechRecognition", "RMS dB: " + rmsdB);
                }

                @Override
                public void onBufferReceived(byte[] buffer)
                {
                    // This method is called when the system starts to receive audio input.
                    // You can perform processing on the received audio buffer if needed.
                    Log.d("SpeechRecognition", "Buffer received: " + buffer.length + " bytes");
                }

                @Override
                public void onEndOfSpeech()
                {
                    // This method is called when the end of speech input is detected.
                    // It indicates that the user has finished speaking.
                    Log.d("SpeechRecognition", "Speech input has ended");
                    // Perform actions like stopping any recording, processing the recorded audio, etc.
                }

                @Override
                public void onError(int error)
                {
                    Log.e("SpeechRecognition", "Error: " + error);
                    // Handle recognition errors here
                }

                @Override
                public void onResults(Bundle results)
                {
                    Log.d("SpeechRecognition", "onResults");
                    processRecordedAudio(results);
                }

                @Override
                public void onPartialResults(Bundle partialResults)
                {
                    Log.d("SpeechRecognition", "PartialResults");
                    // Handle partial results here if needed
                }

                @Override
                public void onEvent(int eventType, Bundle params)
                {

                }

            });
        }

        rec.setSelected(false); //Default


        // Retrieve Username from intentExtra
        Intent intent = getIntent();
        if (intent != null)
        {
            String username = intent.getStringExtra("USERNAME");
            if (username != null && !username.isEmpty())
            {
                // Do something with the username, for example, set it to the user TextView
                user.setText(username);
            }
        }
        int userID = sharedPreferences.getInt("userID", -1);
        int chatID = dbController.getChatID(userID, dbController.getUserIdByUsername(intent.getStringExtra("USERNAME")));
        Log.d("ChatID", String.valueOf(chatID));

        ArrayList<messageModel> chatMessagesList = new ArrayList<>();
        Cursor chatHistory = dbController.getChatHistory(chatID);

        if (chatHistory != null && chatHistory.moveToFirst())
        {
            StringBuilder messageLog = new StringBuilder();
            do
            {
                @SuppressLint("Range") int messageId = chatHistory.getInt(chatHistory.getColumnIndex("Message_ID"));
                @SuppressLint("Range") int conversationId = chatHistory.getInt(chatHistory.getColumnIndex("Conversation_ID"));
                @SuppressLint("Range") int senderId = chatHistory.getInt(chatHistory.getColumnIndex("Sender_ID"));
                @SuppressLint("Range") int recipientId = chatHistory.getInt(chatHistory.getColumnIndex("Recipient_ID"));
                @SuppressLint("Range") byte[] image = chatHistory.getBlob(chatHistory.getColumnIndex("Message_Image"));
                @SuppressLint("Range") String messageContent = chatHistory.getString(chatHistory.getColumnIndex("Message_Content"));
                @SuppressLint("Range") String timestamp = chatHistory.getString(chatHistory.getColumnIndex("Timestamp"));

                if (messageContent.isEmpty() && image.length > 0)
                {
                    message = new messageModel(senderId, recipientId, image);
                }
                else
                {
                    //Text was sent
                    message = new messageModel(senderId, recipientId, messageContent);
                }


                chatMessagesList.add(message);
            } while (chatHistory.moveToNext());

            Log.d("MessageDetails", messageLog.toString()); // Log the accumulated messages

            chatHistory.close(); // Close the cursor when done
        }

        messagesRecyclerAdapter adapter = new messagesRecyclerAdapter(getApplicationContext(), chatMessagesList);
        recyclerView.setAdapter(adapter);

        //Set OnClick Listener for back and send buttons
        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), LoggedActivity.class);
                // Clear any existing intent extras
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish(); // Finish the current activity
            }
        });

        send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = getIntent();
                if (intent != null)
                {
                    String message = msg.getText().toString();
                    String username = intent.getStringExtra("USERNAME");
                    int sender = sharedPreferences.getInt("userID", -1);
                    int receiver = dbController.getUserIdByUsername(username);

                    if (dbController.doesChatExist(sender, receiver))
                    {
                        Log.d("ChatExists", "true");
                        dbController.sendMessage(chatID, sender, receiver, message);

                        // Add the new message to the dataset and notify the adapter
                        messageModel newMessage = new messageModel(sender, receiver, message);
                        chatMessagesList.add(newMessage);
                        int newPosition = chatMessagesList.size() - 1; // Index of the new item

                        // Notify the adapter that an item is inserted, triggering an animation
                        adapter.notifyItemInserted(newPosition);

                        // Scroll to the newly added message for visibility
                        recyclerView.smoothScrollToPosition(newPosition);
                    }
                    else
                    {
                        //If Chat doesn't exist
                        //Create Chat
                        Log.d("ChatExists", "false");
                        dbController.createChat(sender, receiver);
                        //Send Message
                        dbController.sendMessage(chatID, sender, receiver, message);

                        // Add the new message to the dataset and notify the adapter
                        messageModel newMessage = new messageModel(sender, receiver, message);
                        chatMessagesList.add(newMessage);
                        int newPosition = chatMessagesList.size() - 1; // Index of the new item

                        // Notify the adapter that an item is inserted, triggering an animation
                        adapter.notifyItemInserted(newPosition);

                        // Scroll to the newly added message for visibility
                        recyclerView.smoothScrollToPosition(newPosition);
                    }
                }
                msg.setText("");
            }
        });

        rec.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (rec.isSelected())
                {
                    voice.setVisibility(View.GONE);
                    pic.setVisibility(View.GONE);
                }
                else
                {
                    voice.setVisibility(View.VISIBLE);
                    pic.setVisibility(View.VISIBLE);
                }

                // Toggle the selected state of the button
                rec.setSelected(!rec.isSelected());
            }
        });

        // Set the touch listener for the "voice" button in onCreate or your initialization method
        voice.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    // Change color when pressed
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
                    {
                        // Permission is not granted
                        // Request the permission
                        ActivityCompat.requestPermissions(chatActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_RECORD_AUDIO);
                    }
                    else
                    {
                        // Permission has already been granted
                        // Proceed with using the feature that requires this permission
                        voice.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
                        isRecording = true;
                        startRecording();
                    }

                }
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    isRecording = false;
                    stopRecording();
                    // Results are received asynchronously in the RecognitionListener's onResults() method
                    // Handle the results there
                    // Revert back to the original color when released
                    voice.setBackgroundColor(originalColor);
                }
                return true;
            }
        });

        pic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Check if camera permission is granted, and if not, request it
                if (ContextCompat.checkSelfPermission(chatActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(chatActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
                    openCamera();
                }
                else
                {
                    // Permission is already granted, so open the camera
                    openCamera();
                }

                //captured pic is sent as a message
            }
        });

        recyclerView.smoothScrollToPosition(chatMessagesList.size()-1); //Scrolls down chat
    }

    private void startRecording()
    {
        Log.d("Start rec", "Start");
        try
        {
            speechRecognizer.startListening(recognizerIntent);
        }
        catch (ActivityNotFoundException e)
        {
            Toast.makeText(this, "Speech recognition not supported on your device", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording()
    {
        Log.d("Stop rec", "Stop");
        speechRecognizer.stopListening();
    }

    private void processRecordedAudio(Bundle results)
    {
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (matches != null && matches.size() > 0)
        {
            String recognizedText = matches.get(0); // Get the recognized speech text
            // Do something with the recognized text (e.g., send it to the database)
            msg.setText(recognizedText);
            Log.d("VoiceRecog", recognizedText);
        }
    }
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            // Handle if the device doesn't have a camera app or can't handle the intent
            Toast.makeText(this, "No camera app available", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle the result from the camera intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Get the captured image data
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            // Now, you can save the image to the database or send it as a message
            // Implement your logic here based on your database structure
            // For example, if you're using Firebase, you might upload the image to Firebase Storage and then save the URL to the database
            // Or if you're using SQLite, you might save the image to a specific table with other message details

            // For demonstration, let's assume you have a method to save the image to the database
            dbController.sendImage(chatID, senderId, recipientId, imageBitmap);

        }
    }
}

