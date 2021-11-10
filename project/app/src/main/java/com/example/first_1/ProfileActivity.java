package com.example.first_1;

import android.app.Activity;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileActivity extends Activity implements AutoPermissionsListener {
    ////서버~~!!
    private static final Pattern IP_ADDRESS
            = Pattern.compile(
            "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                    + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                    + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                    + "|[1-9][0-9]|[0-9]))");

    final int SELECT_MULTIPLE_IMAGES = 1;
    final int SELECT_SOURCE_IMAGE = 5;

    ArrayList<String> selectedImagesPaths = new ArrayList<>(); // Paths of the image(s) selected by the user.
    ArrayList<Uri> SourceURI = new ArrayList<>();

    boolean imagesSelected = false; // Whether the user selected at least an image or not.
    ///

    /////UI
/////
    CircleImageView profile;
    Button uploadBtn, cameraBtn, saveBtn;
    TextView idText;
    String mCurrentPhotoPath, userId;
    Uri imageURI;
    Uri photoURI, albumURI;
    Intent intent1;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        final Intent intent = getIntent(); /*데이터 수신*/
        userId = intent.getExtras().getString("userId");

        intent1 = new Intent(getApplicationContext(),MainActivity.class);
        intent1.putExtra("userId", userId);


        AutoPermissions.Companion.loadAllPermissions(this,101);

        profile = (CircleImageView) findViewById(R.id.profileIV);
        profile.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_account_circle_24));
        uploadBtn = (Button) findViewById(R.id.uploadBtn);
        cameraBtn = (Button) findViewById(R.id.cameraBtn);
        saveBtn =(Button) findViewById(R.id.saveBtn);
        idText = (TextView) findViewById(R.id.idText);

        idText.setText(userId);

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("*/*");
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, SELECT_MULTIPLE_IMAGES);

            }
        });

/**
 Button button1 = (Button) findViewById(R.id.picBtn0);
 button1.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
Intent gallIntent = new Intent(Intent.ACTION_GET_CONTENT);
gallIntent.setType("image/*");
startActivityForResult(Intent.createChooser(gallIntent, "Select Picture"), PICK_IMAGE);
}
});

 Button button2 = (Button) findViewById(R.id.call);
 button2.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
Intent intent = new Intent(ThirdActivity.this,folderActivity.class);
startActivity(intent);
}
});
 **/
    }


    public void connectServer(View v) {

        // TextView responseText = findViewById(R.id.responseText);
        if (imagesSelected == false) { // This means no image is selected and thus nothing to upload.
            Toast.makeText(this,"프로필 사진을 선택해주세요.",Toast.LENGTH_SHORT);
            return;
        }

        System.out.println("Sending the Files. Please Wait ...");

        //  EditText ipv4AddressView = findViewById(R.id.IPAddress);
        String ipv4Address = "18.188.212.207";
        // EditText portNumberView = findViewById(R.id.portNumber);
        String portNumber = "5000";

        Matcher matcher = IP_ADDRESS.matcher(ipv4Address);

        if (!matcher.matches()) {
            System.out.println("Invalid IPv4 Address. Please Check Your Inputs.");
            return;
        }

        String postUrl = "http://" + ipv4Address + ":" + portNumber + "/Info/" + userId;

        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);


        //Source이미지 비트맵으로 변환
        for (int i = 0; i < selectedImagesPaths.size(); i++) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            try {
                // Read BitMap by file path.
                Bitmap bitmap = BitmapFactory.decodeFile(selectedImagesPaths.get(i), options);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            }catch(Exception e){
                //responseText.setText("Please Make Sure the Selected File is an Image.");
                return;
            }
            byte[] byteArray = stream.toByteArray();

            multipartBodyBuilder.addFormDataPart("image" + i, "Profile" + i + ".jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray));
        }

        RequestBody postBodyImage = multipartBodyBuilder.build();

//        RequestBody postBodyImage = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("image", "androidFlask.jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray))
//                .build();

        postRequest(postUrl, postBodyImage);
    }

    void postRequest(String postUrl, RequestBody postBody) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();
                Log.d("FAIL", e.getMessage());

                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //TextView responseText = findViewById(R.id.responseText);
                        System.out.println("Failed to Connect to Server. Please Try Again.");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TextView responseText = findViewById(R.id.responseText);
                        startActivity(intent1);

                     try {
                            System.out.println("Server's Response\n" + response.body().string());
                         } catch (IOException e) {
                         e.printStackTrace();
                         }
                    }
                });
            }
        });
    }

    public void selectImage(View v) {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_MULTIPLE_IMAGES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == SELECT_MULTIPLE_IMAGES && resultCode == RESULT_OK && null != data) {
                // When a single image is selected.
                String currentImagePath;
                selectedImagesPaths = new ArrayList<>();

                if (data.getData() != null) {
                    Uri uri = data.getData();
                    profile.setImageURI(uri);
                    currentImagePath = getPath(getApplicationContext(), uri);
                    Log.d("ImageDetails", "Single Image URI : " + uri);
                    Log.d("ImageDetails", "Single Image Path : " + currentImagePath);
                    selectedImagesPaths.add(currentImagePath);
                    imagesSelected = true;
                } else {
                    // When multiple images are selected.
                    // Thanks tp Laith Mihyar for this Stackoverflow answer : https://stackoverflow.com/a/34047251/5426539
                    if (data.getClipData() != null) {
                        ClipData clipData = data.getClipData();
                        for (int i = 0; i < clipData.getItemCount(); i++) {

                            ClipData.Item item = clipData.getItemAt(i);
                            Uri uri = item.getUri();

                            currentImagePath = getPath(getApplicationContext(), uri);
                            selectedImagesPaths.add(currentImagePath);
                            Log.d("ImageDetails", "Image URI " + i + " = " + uri);
                            Log.d("ImageDetails", "Image Path " + i + " = " + currentImagePath);
                            imagesSelected = true;
                        }
                    }
                }
            } else {
                Toast.makeText(this, "You haven't Picked any Image.", Toast.LENGTH_LONG).show();
            }
            //Toast.makeText(getApplicationContext(), selectedImagesPaths.size() + " Image(s) Selected.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Something Went Wrong.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // Implementation of the getPath() method and all its requirements is taken from the StackOverflow Paul Burke's answer: https://stackoverflow.com/a/20559175/5426539
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    @Override
    public void onDenied(int i, String[] strings) {
    }

    @Override
    public void onGranted(int i, String[] strings) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
        Toast.makeText(this, "갤러리 접근 권한 획득", Toast.LENGTH_SHORT).show();
    }

}
