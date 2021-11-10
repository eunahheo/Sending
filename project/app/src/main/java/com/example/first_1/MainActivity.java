package com.example.first_1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.kakao.network.NetworkTask;

import java.util.Random;

public class MainActivity extends Activity {
    Random rnd;
    View dialogView;

    String userId, url, Code;
    ContentValues codeName;
    ImageButton dateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent(); /*데이터 수신*/
        userId = intent.getExtras().getString("userId");

        url = "http://18.188.212.207/Code";

        rnd=new Random();

        codeName = new ContentValues();

        codeName.put("userId", userId);

        dateBtn = (ImageButton) findViewById(R.id.dateBtn);
        dateBtn.setImageResource(R.drawable.date);

        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popup= new PopupMenu(getApplicationContext(), view);//v는 클릭된 뷰를 의미
                getMenuInflater().inflate(R.menu.option_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case R.id.m1:
                                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                                dlg.setTitle("방 생성코드");
                                dlg.setIcon(R.drawable.ic_baseline_lock_24);

                                Code = String.valueOf((char) ((int) (rnd.nextInt(26)) + 65))+rnd.nextInt(10)+rnd.nextInt(10)+rnd.nextInt(10);
                                codeName.put("Code", Code);

                                dlg.setMessage(Code); //영어 대문자 1개 + 숫자3개
                                dlg.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        //네트워크 연결
                                        NetworkTask networkTask = new NetworkTask(url, codeName);
                                        networkTask.execute();

                                        Intent intent1 = new Intent(getApplicationContext(), EventActivity.class);
                                        startActivity(intent1);
                                    }
                                });
                                dlg.show();
                                break;

                            case R.id.m2:
                                dialogView =  View.inflate(MainActivity.this, R.layout.code_dialog,null);
                                AlertDialog.Builder dlg1 = new AlertDialog.Builder(MainActivity.this);
                                dlg1.setTitle("코드를 입력하시오.");
                                dlg1.setIcon(R.drawable.key);
                                dlg1.setView(dialogView);
                                dlg1.setPositiveButton("확인",null); //확인버튼 눌렀을때 서버로 넘기는 과정 추가
                                dlg1.setNegativeButton("취소",null);
                                dlg1.show();
                                break;

                            default:
                                break;

                        }
                        return false;
                    }
                });
                popup.show();


            }
        });

        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ThirdActivity.class);
                intent.putExtra("userId",userId);
                startActivity(intent);
            }
        });

    }
    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {

            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

           String result; // 요청 결과를 저장할 변수.
           RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
           result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.

           return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.

        }
    }
}
