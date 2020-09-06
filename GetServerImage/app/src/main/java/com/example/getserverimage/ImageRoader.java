package com.example.getserverimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ImageRoader {
    Bitmap bitmapImg;
    private final String serverUrl = "http://www.gettyimagesgallery.com/Images/Exhibitions/Poolside/";

    public ImageRoader() {

        new ThreadPolicy();
    }

    public Bitmap getBitmapImg(String imgStr) {

        try {
            URL url = new URL(serverUrl +
                    URLEncoder.encode(imgStr, "utf-8"));
            // Character is converted to 'UTF-8' to prevent broken

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.connect();

            InputStream is = conn.getInputStream();
            bitmapImg = BitmapFactory.decodeStream(is);
            Log.d("사진..", ""+is+"   " +bitmapImg);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return bitmapImg;
    }
}