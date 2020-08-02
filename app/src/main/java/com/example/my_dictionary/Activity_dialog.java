package com.example.my_dictionary;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Activity_dialog extends Activity {
    private static final String TAG= "Activity_dialog";

    private int REQUEST_CODE = 1;

    RecyclerView recyclerView;
    ShareAdapter adapter;
    ArrayList<CategoryItem> list;
    String user_index;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        Intent intent = getIntent();
        user_index = intent.getStringExtra("user_index");
        user_id = intent.getStringExtra("user_id");
        list = new ArrayList<>();
        get_category_from_server(Activity_dialog.this, user_index);


        recyclerView = findViewById(R.id.category_recycle);
        adapter = new ShareAdapter(Activity_dialog.this, list);
        adapter.setOnItemClickListener(new ShareAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                    String isShared = list.get(position).getIsShared();

                    if(isShared.equals("0")){
                        String category_index = list.get(position).getCategory_index();
                        String category_name = list.get(position).getCategory_name();
                        add_shared_category_to_server(Activity_dialog.this, user_id, category_name, category_index);
                        Toast.makeText(Activity_dialog.this, "버튼 클릭됨", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent();
                        intent.putExtra("result", "yes");
                        setResult(2, intent);
                        finish();
                    }
                    else if(isShared.equals("1")){

                        // 이미 업로드한 카테고리, 업데이트 할 건지 물어봐야함
                        String category_index = list.get(position).getCategory_index();
                        String download_category_index = list.get(position).getDownload_category_index();

                        Intent intent = new Intent(Activity_dialog.this, Activity_dialog2.class);
                        intent.putExtra("category_index", category_index);
                        intent.putExtra("download_category_index", download_category_index);
                        startActivityForResult(intent, REQUEST_CODE);

                    }
//                    else if(isShared.equals("2")){
//                        // 다운받은 카테고리, 공유할 수 없다는 다이얼 로그 띄워줘야 함
//                    }
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(Activity_dialog.this));
    }

    public void addItem(String name, String category_index, String isShared, String download_category_index){
        CategoryItem item = new CategoryItem();

        item.setCategory_name(name);
        item.setCategory_index(category_index);
        item.setIsShared(isShared);
        item.setDownload_category_index(download_category_index);
        list.add(item);
    }

    /**
     * 쉐어드에 저장된 유저 index 의 category 데이터를 서버에서 가져온다.
     */
    public void get_category_from_server(Context context, String user_index){
        try {

            GetPost_php task = new GetPost_php(context);
            task.execute("http://192.168.254.129/mysql_android_pushData.php", "user_index", user_index);

            String callBackValue = task.get();

            if(callBackValue.isEmpty() || callBackValue.equals("") || callBackValue == null || callBackValue.contains("Error")) {
                Toast.makeText(context, "등록되지 않은 사용자이거나, 전송오류입니다.", Toast.LENGTH_SHORT).show();
            }

            else {
//                Log.e(TAG, callBackValue);
                JSONArray jsonArray = null;
                try {

                    jsonArray = new JSONArray(callBackValue);

                    for(int i=0; i < jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String category = jsonObject.getString("category_name");
                        String category_index = jsonObject.getString("category_index");
                        String isShared = jsonObject.getString("isShared");
                        String download_category_index = jsonObject.getString("download_category_index");
                        String num_word_str = jsonObject.getString("num_word");
                        int num_word = Integer.parseInt(num_word_str);

                        // 내가 다운로드 받은 카테고리를 공유하지 못하도록록
                       if(!isShared.equals("2") && num_word > 9){
                            addItem(category, category_index, isShared, download_category_index);
                        }
                        Log.e(TAG, "category name: " + category);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * db에 공유한 카테고리의 정보를 저장한다.
     */
    public void add_shared_category_to_server(Context context, String user_id, String category_name, String category_index) {
        GetPost_php task = new GetPost_php(context);
        task.execute("http://192.168.254.129/mysql_android_share_category.php", "user_id", user_id,"category_name", category_name, "category_index", category_index);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
               if(data.getStringExtra("result").equals("yes")){
                   Intent intent = new Intent();
                   intent.putExtra("result", "yes");
                   setResult(2, intent);
                   finish();
               }
            }
        }
    }



}