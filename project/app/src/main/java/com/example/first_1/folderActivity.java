package com.example.first_1;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Space;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class folderActivity extends Activity {

    int sourceNum;
    GridLayout container;
    String userId;
    int btnNum;
    private String gall;
    Intent intent2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.folder);

        Intent intent = getIntent(); /*데이터 수신*/
        sourceNum = intent.getExtras().getInt("sourceNum");
        userId = intent.getExtras().getString("userId");

        intent2 = new Intent(getApplicationContext(), resultActivity.class);
        intent2.putExtra("userId", userId);
        intent2.putExtra("sourceNum", sourceNum);


        // xml에 만들어놓은 뷰 id통해 읽어오기
        container = (GridLayout) findViewById(R.id.Grid_Lay);


        btnNum = 0;

        for (int i = 0; i <= sourceNum; i++) {

            final ImageButton button = new ImageButton(this);
            button.setImageResource(R.drawable.folder);
            button.setBackgroundColor(Color.parseColor("#FCFCFC"));


            // 뷰에 추가하기
            container.addView(button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    button.setId(btnNum);
                    intent2.putExtra("btnNum", btnNum);

                    if (btnNum < sourceNum) {
                        btnNum++;
                    }
                    startActivity(intent2);

                }
            });


        }
    }
}


