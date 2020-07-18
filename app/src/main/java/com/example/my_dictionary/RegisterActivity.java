package com.example.my_dictionary;

import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG= "RegisterActivity";

    Button btn_certificate_duple_id, btn_register;
    EditText register_id, password, password_re;

    String getData_url, pushData_url;
    URLConnector task;
    PHPRequest request;

    boolean isCertificate = false;
    TabHost mTabHost = null;
    String myResult;

    private static String IP_ADDRESS = "http://192.168.254.129/mysql_android_getData.php";
    private static String TAG1 = "phptest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        setContentView(R.layout.activity_register);

        register_id = findViewById(R.id.register_id);
        password = findViewById(R.id.password);
        password_re = findViewById(R.id.password_re);
        /**
         *
         *
         */

        getData_url = "http://192.168.254.129/mysql_android_pushData.php";  // 유저 전체 id 받아오기 위한 url



        btn_certificate_duple_id = findViewById(R.id.certificate_duple);  // 아이디 중복확인 버튼

        btn_certificate_duple_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * url을 파라미터로 받아서 json 형식의 string 결과값을 반환해주는 class
                 */
                task = new URLConnector(getData_url);
                task.start();
                try{

                    task.join();    // task thread가 끝날때까지 기다려준다. -> 동기처리
                    System.out.println("waiting... for result");
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }

                String result = task.getResult();
                JSONArray json_array = null;
                ArrayList id_list = new ArrayList();    // 유저 id를 담을 list 생성
                try {
                    json_array = new JSONArray(result);     //db로부터 받아온 결과값을 json array 형태로 바꿔준다.

                    /**
                     * json array 안에 있는 json object들 중 key가 ID인 value 값을 String 변수 'id'에 담아준다.
                     * 유저 id를 담는 list에 해당 id String을 추가해준다.
                     */
                    for(int i = 0 ; i<json_array.length(); i++){
                        JSONObject jsonObject = json_array.getJSONObject(i);
                        String id = jsonObject.getString("ID");
                        id_list.add(id);

                    }
                    Log.e("array_id", String.valueOf(id_list));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "err");
                }

                /**
                 * 입력받은 id 중복확인하는 로직
                 */



                String input_id = register_id.getText().toString();

                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);

//                alertDialog.show();

                if(!id_list.contains(input_id)){
                    Log.e("result", "available");

                    builder.setTitle("아이디 중복확인").setMessage("사용가능한 아이디입니다. 사용하시겠습니까?");
                    builder.setPositiveButton("사용", yesButtonClickListener);
                    builder.setNegativeButton("취소", noButtonClickListener);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else{
                    Log.e("result", " non available");

                    builder.setTitle("아이디 중복확인").setMessage("이미 사용중인 아이디입니다.");
                    builder.setNegativeButton("확인", noButtonClickListener);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });


        /**
         * 회원가입 버튼  로직
         */


        btn_register = findViewById(R.id.register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ID = register_id.getText().toString();
                String pw = password.getText().toString();
                String pw_re = password_re.getText().toString();

                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                if(!isCertificate){

                    builder.setTitle("회원가입").setMessage("아이디 중복체크를 해주세요.");
                    builder.setNegativeButton("확인", null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else if(TextUtils.isEmpty(pw) || TextUtils.isEmpty(pw_re)){

                    builder.setTitle("회원가입").setMessage("비밀번호를 입력해주세요.");
                    builder.setNegativeButton("확인", null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else if(!pw.equals(pw_re)){

                    builder.setTitle("회원가입").setMessage("비밀번호가 일치하지 않습니다.");
                    builder.setNegativeButton("확인", null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else {
                    /**
                     * 서버안에 있는 php파일로 post 방식으로 유저 정보를 보낸다.
                     */
                    try {
                        request = new PHPRequest(IP_ADDRESS, String.valueOf(register_id.getText()), String.valueOf(password.getText()), "basic");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    request.start();
                    try {
                        request.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


//
                    builder.setTitle("회원가입").setMessage("회원가입에 성공했습니다. 로그인 화면으로 이동합니다.");
                    builder.setNegativeButton("확인", successButtonClickListener);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });



    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
    }

    /**
     * 다이얼로그 버튼 처리 이벤트 class
     */
    private DialogInterface.OnClickListener yesButtonClickListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            isCertificate = true;
        }
    };

    private DialogInterface.OnClickListener noButtonClickListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            isCertificate = false;
        }
    };

    private DialogInterface.OnClickListener successButtonClickListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    };


    /**
     * mySQL로 데이터 보내는 AsyncTask
     */
//    class InsertData extends AsyncTask<String, Void, String> {
//        ProgressDialog progressDialog;
//
//
//        // UI작업을 수행
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            progressDialog = ProgressDialog.show(RegisterActivity.this,
//                    "Please Wait", null, true, true);
//        }
//
//
//        // 실제 비즈니스 로직이 처리될 메소드(Thread 부분이라고 생각하면 됨)
//        @Override
//        protected String doInBackground(String... params) {
//
//            String ID = (String)params[1];
//            String PW = (String)params[2];
//            String serverURL = (String)params[0];
//            String postParameters = "ID=" + ID + "&PW=" + PW;
//
//            Log.e("task", ID);
//            Log.e("task", PW);
//            Log.e("task", serverURL);
//            Log.e("task", postParameters);
//
//            try {
//
//                URL url = new URL(serverURL);
//                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//
//                httpURLConnection.setConnectTimeout(20000);
//                httpURLConnection.setReadTimeout(20000);
//                httpURLConnection.setRequestMethod("POST");
//                httpURLConnection.setDoInput(true);     //
//                httpURLConnection.setDoOutput(true);    //
//
//                httpURLConnection.connect();
//                Log.e(TAG, "305");
//
//                OutputStream outputStream = httpURLConnection.getOutputStream();
//                outputStream.write(postParameters.getBytes("UTF-8"));
//                outputStream.flush();
//                outputStream.close();
//                Log.e(TAG, "311");
//
//                int responseStatusCode = httpURLConnection.getResponseCode();
//                Log.e(TAG, "POST response code - " + responseStatusCode);
//
//                InputStream inputStream;
//                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
//                    inputStream = httpURLConnection.getInputStream();
//                    Log.e("task", "319");
//                }
//                else{
//                    inputStream = httpURLConnection.getErrorStream();
//                    Log.e("task", "323");
//                }
//
//
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//
//                StringBuilder sb = new StringBuilder();
//                String line = null;
//
//                while((line = bufferedReader.readLine()) != null){
//                    sb.append(line);
//                }
//
//
//                bufferedReader.close();
//
//
//                return sb.toString();
//
//
//            } catch (Exception e) {
//
//                Log.e(TAG, "InsertData: Error ", e);
//
//                return new String("Error: " + e.getMessage());
//            }
//
//        }
//
//        // 모든 작업이 끝난 후 처리되는 메소드
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//
//            progressDialog.dismiss();
////            mTextViewResult.setText(result);
//            Log.e(TAG, "POST response  - " + result);
//        }
//    }

//    public class PostRequestHandler extends AsyncTask<>



}
