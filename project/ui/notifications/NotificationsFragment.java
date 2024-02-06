package com.example.project.ui.notifications;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.Controller.DBController;
import com.example.project.R;
import com.example.project.View.recyclerAdapter;
import com.example.project.databinding.FragmentNotificationsBinding;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private RecyclerView recyclerView;
    private DBController dbController;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.getRoot().findViewById(R.id.notificationsRec); // Replace with your RecyclerView ID

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        dbController = new DBController(getContext());
        SharedPreferences preferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
        ArrayList<String> userArrayList = new ArrayList<>();
        ArrayList<String> lastMsgArrayList = new ArrayList<>();

        int userId = preferences.getInt("userID", -1);

        // Call the method from your DBController to get data
        Cursor cursor = dbController.getAllPotentialChats(userId);

        if (cursor != null) {
            int rowCount = cursor.getCount();
            Log.d("PotChatsCursorRowCount", "Row count: " + rowCount);

            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range")
                    byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex("Profile_Picture"));
                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    bitmapArrayList.add(imageBitmap);

                    @SuppressLint("Range")
                    String username = cursor.getString(cursor.getColumnIndex("Username"));
                    Log.d("otherUsers", "Adding user: " + username);

                    // Check if the username already exists in the list before adding
                    if (!userArrayList.contains(username)) {
                        userArrayList.add(username);

                        // Assuming 'Last_Message' is the column name for the last message
                        @SuppressLint("Range")
                        String lastMsg = cursor.getString(cursor.getColumnIndex("Last_Message"));
                        if (lastMsg != null && !lastMsg.isEmpty())
                            lastMsgArrayList.add(lastMsg);
                        else
                            lastMsgArrayList.add("Start a conversation");

                        Log.d("LastMessage", "Adding last message for user: " + username);
                    } else {
                        Log.d("DuplicateUser", "Skipping duplicate user: " + username);
                    }
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        // Adapter setup similar to HomeFragment
        Log.d("ArrayListSizesNoti", "Bitmaps: " + bitmapArrayList.size() + ", Users: " + userArrayList.size() + ", Last Messages: " + lastMsgArrayList.size());
        recyclerAdapter adapter = new recyclerAdapter(getContext(), bitmapArrayList, userArrayList, lastMsgArrayList);
        recyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
