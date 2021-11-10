package com.example.first_1;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class resultActivity extends Activity {
    int sourceNum;
    GridLayout Grid_Lay;
    String userId;
    int btnNum;
    private String gall;
    URL url;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faceresult_activity);

        Intent intent = getIntent(); /*데이터 수신*/
        btnNum = intent.getExtras().getInt("btnNum");
        userId = intent.getExtras().getString("userId");
        sourceNum = intent.getExtras().getInt("sourceNum");

        // xml에 만들어놓은 뷰 id통해 읽어오기
        Grid_Lay = (GridLayout)findViewById(R.id.Grid_Lay);
        url = null;

        Thread mThread = new Thread(){
            @Override
            public void run() {
                try{
                    gall ="/storage/emulated/0/Pictures/";
                    String folderName = "Face" + btnNum;
                    gall = gall+folderName;
                    File dir = new File(gall);

                    if(!dir.exists()){
                        dir.mkdirs();
                    }


                    if(btnNum == sourceNum){
                         url = new URL("http://18.188.212.207:5000/download/h55555r@naver.com/" +"etc");
                    }else{
                         url = new URL("http://18.188.212.207:5000/download/h55555r@naver.com/" +"Source" + btnNum);
                    }

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    unzip(is, gall);

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
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }

    public void unzip(InputStream is, String location) {
        try  {

            ZipInputStream zin = new ZipInputStream(is);
            Log.d("d", zin.toString());
            ZipEntry ze = null;

            while ((ze = zin.getNextEntry())!= null) {
                Log.d("Decompress", "Unzipping " + ze.getName());   //압축이 풀리면서 logcat으로 zip 안에 있던 파일

                if (ze.isDirectory()) {

                    //  _dirChecker(ze.getName());

                } else {

                    FileOutputStream fout = new FileOutputStream(location+"/" +ze.getName());
                    BufferedInputStream in = new BufferedInputStream(zin);  //이렇게 지정하지 않고 unzip을
                    in.mark(1000);

                    BufferedOutputStream out = new BufferedOutputStream(fout);
                    Bitmap bitmap = null;
                    
                    byte b[] = new byte[2000000];
                    int n;
                    while ((n = in.read(b, 0, 2000000)) >= 0) {
                        out.write(b, 0, n);
                        bitmap = BitmapFactory.decodeByteArray(b, 0, n);
                    }                    

                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATA,
                            location+"/" +ze.getName());
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    Space space = new Space(this);
                    Grid_Lay.addView(space,50,200);

                    ImageView iv = new ImageView(this);
                    iv.setImageBitmap(bitmap);
                    Grid_Lay.addView(iv,300,200);

                    zin.closeEntry();
                    fout.close();

                }
                //zin.close();
                Log.d("압축풀기", "완료");


            }
        } catch(Exception e) {
            Log.e("Decompress", "unzip", e);
        }

    }

/**
 //변수 location에 저장된 directory의 폴더를 만듭니다.
 private void _dirChecker(String dir) {
 File f = new File(location+ dir);

 if(!f.isDirectory()) {
 f.mkdirs();
 }
 }
 **/
}
