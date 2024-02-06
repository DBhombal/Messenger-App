package com.example.project.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.Controller.DBController;
import com.example.project.R;
import com.example.project.databinding.FragmentHomeBinding;
import com.example.project.View.recyclerAdapter;

import java.util.ArrayList;

public class HomeFragment extends Fragment
{

    private FragmentHomeBinding binding;
    private DBController dbController;
    RecyclerView recyclerView;
    ImageView img;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.getRoot().findViewById(R.id.notificationsRec);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        dbController = new DBController(getContext());
        SharedPreferences preferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
        ArrayList<String> userArrayList = new ArrayList<>();
        ArrayList<String> lastMsgArrayList = new ArrayList<>();

        int userId = preferences.getInt("userID", -1);

        Cursor cursor = dbController.getAllExistingChats(userId);

        if (cursor != null)
        {
            int rowCount = cursor.getCount();
            Log.d("CursorRowCount", "Row count: " + rowCount);

            if (cursor.moveToFirst())
            {
                do
                {
                    @SuppressLint("Range")
                    byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex("Profile_Picture"));
                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                    @SuppressLint("Range")
                    String username = cursor.getString(cursor.getColumnIndex("Username"));
                    Log.d("otherUsers", "Adding user: " + username);

                    // Check if the username already exists in the list before adding
                    if (!userArrayList.contains(username))
                    {
                        bitmapArrayList.add(imageBitmap);
                        userArrayList.add(username);

                        @SuppressLint("Range")
                        String lastMsg = cursor.getString(cursor.getColumnIndex("Last_Message"));
                        if (lastMsg != null && !lastMsg.isEmpty())
                            lastMsgArrayList.add(lastMsg);
                        else
                            lastMsgArrayList.add("Start a conversation");

                        Log.d("LastMessage", "Adding last message for user: " + username);
                    }
                    else
                    {
                        Log.d("DuplicateUser", "Skipping duplicate user: " + username);
                    }
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        // Check and ensure the sizes match before setting up the adapter
        int sizeBitmap = bitmapArrayList.size();
        int sizeUsers = userArrayList.size();
        int sizeLastMsg = lastMsgArrayList.size();

        int maxSize = Math.max(sizeBitmap, Math.max(sizeUsers, sizeLastMsg));

        // Ensure the sizes match by filling empty lastMsgs if needed
        while (lastMsgArrayList.size() < maxSize)
        {
            lastMsgArrayList.add("Start a conversation");
        }

        Log.d("ArrayListSizes", "Bitmaps: " + sizeBitmap + ", Users: " + sizeUsers + ", Last Messages: " + sizeLastMsg);

        recyclerAdapter adapter = new recyclerAdapter(getContext(), bitmapArrayList, userArrayList, lastMsgArrayList);
        recyclerView.setAdapter(adapter);

        return root;
    }

        @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }
}