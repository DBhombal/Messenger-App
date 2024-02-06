package com.example.project.Controller;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;

import com.example.project.Model.DBModel;

/*
    THIS CLASS IS SUPPOSED TO ACT AS A MIDDLE MAN BETWEEN THE VIEW AND DATABASE
    THIS APP WILL REFERENCE METHODS IN THE DBModel CLASS, IN WHICH ALL DATABASE TRANSACTIONS WILL BE HANDLED
 */
public class DBController
{
    private DBModel dbModel;
    private Context context;

    public DBController(Context context)
    {
        this.context = context;
        dbModel = new DBModel(context);
    }

    // Check if username is in use using the model
    public boolean isUsernameInUse(String username) {
        return dbModel.userExists(username);
    }

    //Check if login credentials are valid
    public boolean validateLogin(String username, String password)
    {
        return dbModel.validateLogin(username, password);
    }

    public boolean createUser(String username, String password)
    {
        return dbModel.createUser(context, username, password);
    }

    public int getUserIdByUsername(String username)
    {
        return dbModel.getUserIdByUsername(username);
    }

    public void deleteAccount(int userID)
    {
        dbModel.deleteAccount(userID);
    }

    public byte[] getUserImage(int userID) {
        return dbModel.getUserImage(userID);
    }

    public void editAccountDetails(int id,String user, String pass)
    {
        dbModel.editAccountDetails(id , user, pass);
    }

    public void changePic(int userID, Bitmap bp)
    {
        dbModel.changePic(userID, bp);
    }

    public Bitmap getUserImageBitmap(int userId)
    {
        return dbModel.getUserImageBitmap(userId);
    }

    public Cursor getAllExistingChats(int userId)
    {
        return dbModel.getAllExistingChats(userId);
    }

    public Cursor getAllPotentialChats(int userId)
    {
        return dbModel.getAllPotentialChats(userId);
    }

    public void sendMessage(int chatID, int senderID, int receiverID, String msg)
    {
        dbModel.sendMessage(chatID, senderID, receiverID, msg);
    }

    public Cursor getChatHistory(int chatID)
    {
        return dbModel.getChatHistory(chatID);
    }

    public void sendImage(int chatId, int senderId, int receiverId, Bitmap bitmap)
    {
        dbModel.sendImage(chatId, senderId, receiverId, bitmap);
    }

    public boolean doesChatExist(int user, int user2)
    {
        return dbModel.doesChatExist(user, user2);
    }

    public int getChatID(int user1, int user2)
    {
        return dbModel.getChatID(user1, user2);
    }

    public void createChat(int user1, int user2)
    {
        dbModel.createChat(user1, user2);
    }
}
