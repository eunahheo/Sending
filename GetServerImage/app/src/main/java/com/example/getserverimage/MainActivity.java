package com.example.getserverimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;


import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button getBtn;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);

        getBtn = (Button) findViewById(R.id.getBtn);


        Thread mThread = new Thread(){
            @Override
            public void run() {
                try{

                    URL url = new URL("http://3.131.183.209:5000/download/Source1.jpg");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);


                }
                catch (MalformedURLException e){
                    e.printStackTrace();

                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        };

        mThread.start();

        try {
            mThread.join();
            if(bitmap == null){

                Log.d("왜 NULL인데? ", "단순한 죽을 것 ");

            }else{

                imageView.setImageBitmap(bitmap);

            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }

//        imageView.setImageBitmap(new ImageRoader().getBitmapImg("01.jpg"));


    }
}