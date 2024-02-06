package com.example.project.View;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;

import java.util.ArrayList;

public class messagesRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<messageModel> chatMessagesList;
    SharedPreferences preferences;

    private static final int SENT_MESSAGE_VIEW = 1;
    private static final int RECEIVED_MESSAGE_VIEW = 2;

    private Context context;


    // Constructor to initialize the context and chat messages list
    public messagesRecyclerAdapter(Context context, ArrayList<messageModel> chatMessagesList) {
        this.context = context;
        this.chatMessagesList = chatMessagesList;
    }

    // Create ViewHolder for sent messages
    public static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView senderTextView;

        public SentMessageViewHolder(View itemView) {
            super(itemView);
            senderTextView = itemView.findViewById(R.id.pictureMessageSent);
        }
    }

    // Create ViewHolder for received messages
    public static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView receiverTextView;

        public ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            receiverTextView = itemView.findViewById(R.id.textMessageReceived);
        }
    }

    // Create ViewHolder for sent images
    public static class SentImageViewHolder extends RecyclerView.ViewHolder
    {
        ImageView sentImage;
        public SentImageViewHolder(View itemView)
        {
            super(itemView);
            sentImage = itemView.findViewById(R.id.pictureMessageSent);
        }
    }

    // Create ViewHolder for received images
    public static class ReceivedImageViewHolder extends RecyclerView.ViewHolder
    {
        ImageView receivedImage;
        public ReceivedImageViewHolder(@NonNull View itemView)
        {
            super(itemView);
            receivedImage = itemView.findViewById(R.id.pictureMessageReceived);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        // Inflate different layouts for sent and received messages
        if (viewType == SENT_MESSAGE_VIEW) {
            View sentView = inflater.inflate(R.layout.msg_sent_model, parent, false);
            return new SentMessageViewHolder(sentView);
        }
        else
        {
            View receivedView = inflater.inflate(R.layout.msg_rec_model, parent, false);
            return new ReceivedMessageViewHolder(receivedView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        Context context = holder.itemView.getContext();
        preferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int sender = chatMessagesList.get(position).getSenderId();

        messageModel message = chatMessagesList.get(position);
        Log.d("ChatmessagesList", String.valueOf(chatMessagesList.get(position).getSenderId()));

        //If user sent the message
        if (holder.getItemViewType() == SENT_MESSAGE_VIEW)
        {
            SentMessageViewHolder sentViewHolder = (SentMessageViewHolder) holder;
            sentViewHolder.senderTextView.setText(message.getMessageText());
        }

        else
        {
            ReceivedMessageViewHolder receivedViewHolder = (ReceivedMessageViewHolder) holder;
            receivedViewHolder.receiverTextView.setText(message.getMessageText());
        }
    }

    @Override
    public int getItemViewType(int position) {
        messageModel message = chatMessagesList.get(position);
        preferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int user = preferences.getInt("userID", -1);

        //Check for text or image

        return user == message.getSenderId() ? SENT_MESSAGE_VIEW : RECEIVED_MESSAGE_VIEW;
    }

    @Override
    public int getItemCount() {
        return chatMessagesList.size();
    }

}

