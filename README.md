# Messenger-App
Android Studio based messenger app, it was created as the final project for the second-level mobile app development course offered at my university. The app utilizes MVC architecture, and was to showcase the use of database interactions within the app.

**THIS APP IS STILL INCOMPLETE, I have some future development plans that I was unable to fit into the schedule. I plan to make future changes to this app to improve upon it.**

The app uses user authentication, and allows the sending of messages. 
Currently, everything is stored locally, I have plans of shifting it to Firebase to solve this. 

The chat function is currently broken, I was implementing it to support sending pictures and using sending audio files. It is currently broken due to that until futher notice.

All the Account based features still work

Users are able to set their profile pictures, which gets converted into a bitmap, which is then stored as a BLOB in the DB. This will function the same when sending images in the chat, currently there is no support to send images(in the database), I plan to implement that quite soon, along with a transition away from local storage.

FUTURE SCOPE:
1. Shift from SQLite to Firebase
2. Add support to send audio files and pictures.
3. Implement group chats.
4. Encryption???
