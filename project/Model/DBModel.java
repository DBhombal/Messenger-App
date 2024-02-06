package com.example.project.Model;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import com.example.project.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;

/*
    THIS CLASS WILL HANDLE ALL THE REFERENCING TO THE DATABASE.
    IT WILL NEVER BE CALLED DIRECTLY IN THE APP.
    IT SHOULD ONLY EVER BE CALLED FROM THE DBController CLASS
 */
public class DBModel extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MessagingAppDB";
    private static final int DATABASE_VERSION = 1;

    // Table names and column names for User table
    private static final String TABLE_USER = "User";
    private static final String COLUMN_USER_ID = "User_ID";
    private static final String COLUMN_USERNAME = "Username";
    private static final String COLUMN_PHONE_NUMBER = "Phone_Number";
    private static final String COLUMN_PASSWORD = "Password";
    private static final String COLUMN_PROFILE_PICTURE = "Profile_Picture";
    private static final String COLUMN_LAST_ACTIVE = "Last_Active";
    private static final String COLUMN_OTHER_USER_DETAILS = "Other_User_Details";

    // Table names and column names for Conversation table
    private static final String TABLE_CONVERSATION = "Conversation";
    private static final String COLUMN_CONVERSATION_ID = "Conversation_ID";
    private static final String COLUMN_PARTICIPANT_1_ID = "Participant_1_ID";
    private static final String COLUMN_PARTICIPANT_2_ID = "Participant_2_ID";
    private static final String COLUMN_LAST_MESSAGE = "Last_Message";
    private static final String COLUMN_TIMESTAMP = "Timestamp";
    private static final String COLUMN_OTHER_CONVERSATION_DETAILS = "Other_Conversation_Details";

    // Table names and column names for Message table
    private static final String TABLE_MESSAGE = "Message";
    private static final String COLUMN_MESSAGE_ID = "Message_ID";
    private static final String COLUMN_MESSAGE_CONVERSATION_ID = "Conversation_ID";
    private static final String COLUMN_MESSAGE_SENDER_ID = "Sender_ID";
    private static final String COLUMN_MESSAGE_RECIPIENT_ID = "Recipient_ID";
    private static final String COLUMN_MESSAGE_CONTENT = "Message_Content";
    private static final String COLUMN_MESSAGE_IMAGE = "Message_Image";
    private static final String COLUMN_MESSAGE_TIMESTAMP = "Timestamp";
    private static final String COLUMN_MESSAGE_STATUS = "Message_Status";

    public DBModel(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create User table
        String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + " ("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_PROFILE_PICTURE + " BLOB,"
                + COLUMN_LAST_ACTIVE + " TEXT"
                + ")";
        db.execSQL(CREATE_USER_TABLE);


        // Create Conversation table
        String CREATE_CONVERSATION_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CONVERSATION + " ("
                + COLUMN_CONVERSATION_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_PARTICIPANT_1_ID + " INTEGER,"
                + COLUMN_PARTICIPANT_2_ID + " INTEGER,"
                + COLUMN_LAST_MESSAGE + " INTEGER,"
                + COLUMN_TIMESTAMP + " TEXT,"
                + COLUMN_OTHER_CONVERSATION_DETAILS + " TEXT,"
                + " FOREIGN KEY (" + COLUMN_PARTICIPANT_1_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "),"
                + " FOREIGN KEY (" + COLUMN_PARTICIPANT_2_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + ")"
                + ")";
        db.execSQL(CREATE_CONVERSATION_TABLE);

        // Create Message table
        String CREATE_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_MESSAGE + " ("
                + COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_MESSAGE_CONVERSATION_ID + " INTEGER,"
                + COLUMN_MESSAGE_SENDER_ID + " INTEGER,"
                + COLUMN_MESSAGE_RECIPIENT_ID + " INTEGER,"
                + COLUMN_MESSAGE_CONTENT + " TEXT," // For text messages
                + COLUMN_MESSAGE_IMAGE + " BLOB,"  // For images (BLOB type)
                + COLUMN_MESSAGE_TIMESTAMP + " TEXT,"
                + COLUMN_MESSAGE_STATUS + " TEXT,"
                + " FOREIGN KEY (" + COLUMN_MESSAGE_CONVERSATION_ID + ") REFERENCES " + TABLE_CONVERSATION + "(" + COLUMN_CONVERSATION_ID + "),"
                + " FOREIGN KEY (" + COLUMN_MESSAGE_SENDER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "),"
                + " FOREIGN KEY (" + COLUMN_MESSAGE_RECIPIENT_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + ")"
                + ")";
        db.execSQL(CREATE_MESSAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONVERSATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE);
        onCreate(db);
    }

    // Method to convert Drawable resource to byte array (BLOB)
    private byte[] convertDrawableToByteArray(Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public boolean userExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_USERNAME + "=?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        Log.d("Model.userExists", String.valueOf(exists));
        return exists;
    }

    //Creates user account if account does not exist
    public boolean createUser(Context context, String username, String password)
    {
        if (!userExists(username)) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, username);
            values.put(COLUMN_PASSWORD, password);

            // Get the default profile picture (Assuming it's a Drawable resource)
            Drawable defaultProfilePicture = context.getResources().getDrawable(R.drawable.default_user_pfp);

            // Convert the Drawable to byte[] (BLOB)
            byte[] defaultProfilePictureBytes = convertDrawableToByteArray(defaultProfilePicture);

            values.put(COLUMN_PROFILE_PICTURE, defaultProfilePictureBytes);

            db.insert(TABLE_USER, null, values);
            db.close();
            return true; // User created successfully
        } else {
            return false; // User already exists
        }
    }

    public boolean validateLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USER,
                new String[]{COLUMN_USER_ID},
                COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?",
                new String[]{username, password},
                null,
                null,
                null
        );

        boolean isValid = cursor != null && cursor.moveToFirst();

        if (cursor != null) {
            cursor.close();
        }

        Log.d("Model.validateLogin()", String.valueOf(isValid));
        return isValid;
    }

    // Method to get user ID by username
    @SuppressLint("Range")
    public int getUserIdByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        int userId = -1; // Default ID if not found

        Cursor cursor = db.query(
                TABLE_USER,
                new String[]{COLUMN_USER_ID},
                COLUMN_USERNAME + "=?",
                new String[]{username},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID));
            cursor.close();
        }

        return userId;
    }
    public Cursor getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USER, null, COLUMN_USER_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null);
    }

    public void deleteAccount(int userID) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Define the column name for user ID (assuming the column name is 'userID')
        String userIDColumn = "userID"; // Replace 'userID' with your actual column name

        try {
            // Perform the deletion based on the provided userID
            db.delete(TABLE_USER, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userID)});

            // Log deletion success or perform other actions if needed
            Log.d("DeleteAccount", "Account with ID " + userID + " deleted successfully");
        } catch (Exception e) {
            // Handle any exceptions or errors that might occur during deletion
            Log.e("DeleteAccount", "Error deleting account: " + e.getMessage());
        } finally {
            // Close the database connection after use
            db.close();
        }
    }

    @SuppressLint("Range")
    public byte[] getUserImage(int userID) {
        SQLiteDatabase db = this.getReadableDatabase();
        byte[] imageBytes = null;

        Cursor cursor = db.rawQuery("SELECT "+ COLUMN_PROFILE_PICTURE + " FROM "+ TABLE_USER + " WHERE user_id=?", new String[]{String.valueOf(userID)});
        if (cursor.moveToFirst()) {
            imageBytes = cursor.getBlob(cursor.getColumnIndex(COLUMN_PROFILE_PICTURE));
        }

        cursor.close();
        db.close();

        return imageBytes;
    }

    public void editAccountDetails(int userId, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);

        int rowsAffected = db.update(TABLE_USER, values, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});
        if (rowsAffected > 0) {
            Log.d("DBModel", "Account details updated successfully");
        } else {
            Log.e("DBModel", "Failed to update account details");
        }

        db.close();
    }

    public void changePic(int userID, Bitmap bp) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Convert Bitmap to byte array
        byte[] imageBytes = convertBitmapToByteArray(bp);

        ContentValues values = new ContentValues();
        values.put(COLUMN_PROFILE_PICTURE, imageBytes);

        int rowsAffected = db.update(TABLE_USER, values, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userID)});
        if (rowsAffected > 0) {
            Log.d("DBModel", "Profile picture updated successfully");
        } else {
            Log.e("DBModel", "Failed to update profile picture");
        }

        db.close();
    }

    public void sendImage(int chatId, int senderId, int receiverId, Bitmap bitmap) {
        // Convert the Bitmap to a byte array
        byte[] imageByteArray = convertBitmapToByteArray(bitmap);

        // Open the database for writing
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a ContentValues object to store column-value pairs
        ContentValues values = new ContentValues();
        values.put(COLUMN_MESSAGE_CONVERSATION_ID, chatId);
        values.put(COLUMN_MESSAGE_SENDER_ID, senderId);
        values.put(COLUMN_MESSAGE_RECIPIENT_ID, receiverId);
        values.put(COLUMN_MESSAGE_IMAGE, imageByteArray); // Store the image byte array

        // Insert the image data into the Messages table
        long newRowId = db.insert(TABLE_MESSAGE, null, values);

        // Check if the insertion was successful
        if (newRowId != -1) {
            // Image data inserted successfully
            Log.d("Database", "Image data inserted into Messages table with row ID: " + newRowId);
        } else {
            // Failed to insert image data
            Log.e("Database", "Failed to insert image data into Messages table");
        }

        // Close the database connection
        db.close();
    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    @SuppressLint("Range")
    public Bitmap getUserImageBitmap(int userID) {
        SQLiteDatabase db = this.getReadableDatabase();
        byte[] imageBytes = null;

        Cursor cursor = db.rawQuery("SELECT " + COLUMN_PROFILE_PICTURE + " FROM " + TABLE_USER + " WHERE " + COLUMN_USER_ID + "=?", new String[]{String.valueOf(userID)});
        if (cursor.moveToFirst()) {
            imageBytes = cursor.getBlob(cursor.getColumnIndex(COLUMN_PROFILE_PICTURE));
        }

        cursor.close();
        db.close();

        if (imageBytes != null) {
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        } else {
            return null; // Return null if no image is found
        }
    }

    public Cursor getAllExistingChats(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT DISTINCT u.Username, u.Profile_Picture, c.Last_Message " +
                "FROM User u " +
                "INNER JOIN Conversation c ON (u.User_ID = c.Participant_1_ID OR u.User_ID = c.Participant_2_ID) " +
                "WHERE u.User_ID != ? AND " +
                "(c.Participant_1_ID = ? OR c.Participant_2_ID = ?)";

        return db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(userId), String.valueOf(userId)});
    }

    public Cursor getAllPotentialChats(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT DISTINCT u.Username, u.Profile_Picture, NULL AS Last_Message " +
                "FROM User u " +
                "LEFT JOIN Conversation c ON (u.User_ID = c.Participant_1_ID OR u.User_ID = c.Participant_2_ID) " +
                "AND (c.Participant_1_ID = ? OR c.Participant_2_ID = ?) " +
                "WHERE u.User_ID != ? AND c.Conversation_ID IS NULL";

        return db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(userId), String.valueOf(userId)});
    }

    public void sendMessage(int conversationId, int senderId, int recipientId, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_MESSAGE_CONVERSATION_ID, conversationId);
        values.put(COLUMN_MESSAGE_SENDER_ID, senderId);
        values.put(COLUMN_MESSAGE_RECIPIENT_ID, recipientId);
        values.put(COLUMN_MESSAGE_CONTENT, content);
        values.put(COLUMN_MESSAGE_IMAGE, "");
        values.put(COLUMN_MESSAGE_TIMESTAMP, "CURRENT_TIMESTAMP");
        values.put(COLUMN_MESSAGE_STATUS, "sent");

        // Insert the message into the Message table
        db.insert(TABLE_MESSAGE, null, values);
        db.close();
    }



    public void createChat(int userId1, int userId2) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Add user IDs to the conversation
        values.put(COLUMN_PARTICIPANT_1_ID, userId1);
        values.put(COLUMN_PARTICIPANT_2_ID, userId2);

        // Insert the conversation into the Conversation table
        db.insert(TABLE_CONVERSATION, null, values);
        db.close();
        Log.d("createChat", "Created Chat");
    }

    public Cursor getLastMsg(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String conversationQuery = "SELECT DISTINCT " + COLUMN_CONVERSATION_ID +
                " FROM " + TABLE_CONVERSATION +
                " WHERE " + COLUMN_PARTICIPANT_1_ID + " = ? OR " + COLUMN_PARTICIPANT_2_ID + " = ?";

        Cursor conversationCursor = db.rawQuery(conversationQuery, new String[]{String.valueOf(userId), String.valueOf(userId)});

        ArrayList<Integer> chatIds = new ArrayList<>();
        if (conversationCursor != null && conversationCursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int chatId = conversationCursor.getInt(conversationCursor.getColumnIndex(COLUMN_CONVERSATION_ID));
                chatIds.add(chatId);
                Log.d("ChatIDS", String.valueOf(chatId));
            } while (conversationCursor.moveToNext());
            conversationCursor.close();
        }

        StringBuilder selectQuery = new StringBuilder("SELECT ");
        selectQuery.append(COLUMN_LAST_MESSAGE).append(" FROM ").append(TABLE_CONVERSATION)
                .append(" WHERE ").append(COLUMN_CONVERSATION_ID).append(" IN (")
                .append(TextUtils.join(",", Collections.nCopies(chatIds.size(), "?"))).append(")")
                .append(" ORDER BY ").append(COLUMN_TIMESTAMP).append(" DESC");

        String[] chatIdStrings = new String[chatIds.size()];
        for (int i = 0; i < chatIds.size(); i++) {
            chatIdStrings[i] = String.valueOf(chatIds.get(i));
        }

        return db.rawQuery(selectQuery.toString(), chatIdStrings);
    }

    public boolean doesChatExist(int userId1, int userId2) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean chatExists = false;

        try {
            String query = "SELECT * FROM " + TABLE_CONVERSATION +
                    " WHERE (" + COLUMN_PARTICIPANT_1_ID + " = ? AND " + COLUMN_PARTICIPANT_2_ID + " = ?) OR " +
                    "(" + COLUMN_PARTICIPANT_2_ID + " = ? AND " + COLUMN_PARTICIPANT_1_ID + " = ?)";

            cursor = db.rawQuery(query, new String[]{
                    String.valueOf(userId1), String.valueOf(userId2),
                    String.valueOf(userId2), String.valueOf(userId1)
            });

            // If the cursor has a count greater than zero, the chat exists
            chatExists = cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return chatExists;
    }

    @SuppressLint("Range")
    public int getChatID(int user1, int user2) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int chatID = -1; // Default value if no chat ID found

        try {
            String query = "SELECT " + COLUMN_CONVERSATION_ID +
                    " FROM " + TABLE_CONVERSATION +
                    " WHERE (" + COLUMN_PARTICIPANT_1_ID + " = ? AND " +
                    COLUMN_PARTICIPANT_2_ID + " = ?) OR (" +
                    COLUMN_PARTICIPANT_1_ID + " = ? AND " +
                    COLUMN_PARTICIPANT_2_ID + " = ?)";

            cursor = db.rawQuery(query, new String[]{
                    String.valueOf(user1), String.valueOf(user2),
                    String.valueOf(user2), String.valueOf(user1)
            });

            if (cursor != null && cursor.moveToFirst()) {
                chatID = cursor.getInt(cursor.getColumnIndex(COLUMN_CONVERSATION_ID));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (cursor != null)
                cursor.close();
        }

        return chatID;
    }


    public Cursor getChatHistory(int chatID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try
        {
            String query = "SELECT * FROM " + TABLE_MESSAGE +
                    " WHERE " + COLUMN_MESSAGE_CONVERSATION_ID + " = " + chatID +
                    " ORDER BY " + COLUMN_MESSAGE_TIMESTAMP + " ASC";

            cursor = db.rawQuery(query, null);

            // Optionally, log the cursor's row count
            if (cursor != null) {
                int rowCount = cursor.getCount();
                Log.d("CursorRowCount", "Row count: " + rowCount);
            }

            // Return the cursor to be handled further by the calling method
            return cursor;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // Return null if an exception occurs or if no cursor is available
        return null;
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}

