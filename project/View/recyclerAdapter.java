package com.example.project.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.Controller.DBController;
import com.example.project.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.ViewHolder>
{

    Context context;
    ArrayList<Bitmap> imageArray;
    ArrayList<String> usernameArray, lastMsgArray;
    public recyclerAdapter(Context context, ArrayList<Bitmap> arrayList, ArrayList<String> usernames, ArrayList<String> lastMsg){
        this.context=context;
        this.imageArray =arrayList;
        this.usernameArray = usernames;
        this.lastMsgArray = lastMsg;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(context).inflate(R.layout.contact_modal,parent,false);
        // Layout:layout that we want to display
        // parent – The ViewGroup into which the new View will be added
        // after it is bound to an adapter position.
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // User has an image set as their picture, display that instead of the default
        Bitmap image = imageArray.get(position);
        if (image != null) {
            holder.image.setImageBitmap(image);
        } else {
            // Get the drawable from the resource ID
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.default_user_pfp);
            byte[] pic = convertDrawableToByteArray(drawable);
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(pic, 0, pic.length);
            holder.image.setImageBitmap(imageBitmap);


        }
        holder.usernameTV.setText(usernameArray.get(position));

        if (!lastMsgArray.isEmpty() && position < lastMsgArray.size()) {
            holder.lastmsgTV.setText(lastMsgArray.get(position));
        } else {
            // Handle the case where lastMsgArray is empty or position is out of bounds
            holder.lastmsgTV.setText("Start a conversation");
            holder.lastmsgTV.setTextColor(Integer.parseInt("#a5a4a4"));
        }
    }

    // Method to convert a drawable to a byte array
    private byte[] convertDrawableToByteArray(Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }



    @Override
    public int getItemCount()
    {
        Log.d("imageArraySize", String.valueOf(imageArray.size()));
        return imageArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircularImageView image;
        TextView usernameTV, lastmsgTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Get the image view using the correct ID from your item layout.
            image = itemView.findViewById(R.id.imgView);
            usernameTV = itemView.findViewById(R.id.usernameModalTV);
            lastmsgTV = itemView.findViewById(R.id.lastMsgModalTV);

            // Set OnClickListener for the entire item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Retrieve the username from the TextView and display it in a Snackbar
                        String username = usernameTV.getText().toString();
                        Snackbar.make(v, username, Snackbar.LENGTH_SHORT).show();

                        // Switch to the chat activity, passing necessary data (e.g., username)
                        Intent intent = new Intent(itemView.getContext(), chatActivity.class);
                        intent.putExtra("USERNAME", username); // Pass the username to the chat activity
                        itemView.getContext().startActivity(intent);
                    }
                }
            });
        }
    }
}
