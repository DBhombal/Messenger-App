package com.example.project.View;

import android.graphics.Bitmap;

public class modal {
    Bitmap img;
    String username;
    String lastmsg;

    public modal()
    {
        this.img = null; //No picture set
        this.username = "";
        this.lastmsg = "";
    }
    public modal(Bitmap img, String username, String lastmsg)
    {
        this.img = img;
        this.username = username;
        this.lastmsg = lastmsg;
    }
}
