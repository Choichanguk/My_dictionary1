package com.example.my_dictionary;

import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG= "LoginActivity";
    Button btn_register, btn_login;
    EditText id, password;

    URLConnector login_task;
    String getData_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        setContentView(R.layout.activity_login);

        Context context = LoginActivity.this;
        Context context1 = getApplicationContext();
        Context context2 = getApplication();
        Context context3 = getParent();
        Context context4 = getBaseContext();
        Context context5 = this;
//        ContextWrapper contextWrapper = new ContextWrapper();     // ContextWrapper 가 Context를 상속
        Log.e(TAG, "LoginActivity context: " + context);
        Log.e(TAG, "getApplicationContext: " + context1);
        Log.e(TAG, "getApplication: " + context2);
        Log.e(TAG, "getParent: " + context3);
        Log.e(TAG, "getBaseContext: " + context4);
        Log.e(TAG, "this: " + context5);



        //shared 확인
        SharedClass sharedClass = new SharedClass();
        String load_id = sharedClass.loadUserId(LoginActivity.this);
        Boolean load_status = sharedClass.loadLoginStatus(LoginActivity.this);

//        Log.e(TAG, load_id);
//        Log.e(TAG, String.valueOf(load_status));



        btn_register = findViewById(R.id.register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        id = findViewById(R.id.id);
        password = findViewById(R.id.password);


        getData_url = "http://192.168.254.129/mysql_android_pushData.php";  //회원정보를 가지고 있는 url

        btn_login = findViewById(R.id.login);
        btn_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String input_id = id.getText().toString();
                String input_pw = password.getText().toString();


                login_task = new URLConnector(getData_url);

                login_task.start();

                boolean can_login = false;

                try {
                    login_task.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                String result = login_task.getResult();

                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(result);

                    for(int i=0; i < jsonArray.length(); i++){

                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String id = jsonObject.getString("ID");
                        String pw = jsonObject.getString("PW");
                        String user_index = jsonObject.getString("user_index");
                        Log.e(TAG, "id: " + id);
                        Log.e(TAG, "pw: " + pw);
                        Log.e(TAG, "user_index: " + user_index);


                        if(input_id.equals(id) && input_pw.equals(pw)){
                            can_login = true;
                            SharedClass sharedClass = new SharedClass();
                            sharedClass.saveUserId(LoginActivity.this, input_id);
                            sharedClass.saveLoginStatus(LoginActivity.this, true);
                            sharedClass.saveUserIndex(LoginActivity.this, user_index);
                            break;

                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                if(can_login){

                    /**
                     * 로그인 성공 시 로그인 상태와 유저 ID를 쉐어드에 저장.
                     */

                    Toast.makeText(getApplicationContext(), "로그인에 성공했습니다.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else{
                    builder.setTitle("로그인").setMessage("아이디, 비밀번호를 확인해주세요.");
                    builder.setNegativeButton("확인", null);
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

    public void push_category_to_server(ViewGroup container, String user_id, String category_name, String category_num) {
        GetPost_php task = new GetPost_php(container.getContext());
        task.execute("http://192.168.254.129/mysql_android_pushData.php");
    }

}

