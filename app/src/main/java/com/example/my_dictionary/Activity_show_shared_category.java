package com.example.my_dictionary;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class Activity_show_shared_category extends AppCompatActivity {
    private static final String TAG= "Activity_show_shared";

    String shared_category_index, category_index, category_name, user_id, num_download;
    String My_id, My_index;
    ArrayList<Shared_word_item> list = new ArrayList<>();
    ArrayList<CategoryItem> user_category_list = new ArrayList<>();
    RecyclerView recyclerView;
    Shared_show_word_adapter adapter;
    ImageView btn_download;

    private TextToSpeech tts_kor, tts_eng;

    TextView user_id_view, category_name_view;
    boolean isContain = false;  // 이미 존재하는 카테고리인지 확인하는 변수
    boolean isDownload = false; // 다운 받으려는 카테고리를 이미 다운 받았는지 확인하는 변수
    String download_category_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_shared_category);

        Intent intent = getIntent();
        shared_category_index = intent.getStringExtra("shared_category_index");
        category_index = intent.getStringExtra("category_index");
        category_name = intent.getStringExtra("category_name");
        user_id = intent.getStringExtra("user_id");
        num_download = intent.getStringExtra("num_download");

        tts_kor = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // 언어를 선택한다.
                tts_kor.setLanguage(Locale.KOREAN);
            }
        });

        tts_eng= new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // 언어를 선택한다.
                tts_eng.setLanguage(Locale.ENGLISH);
            }
        });


        SharedClass sharedClass = new SharedClass();
        My_id = sharedClass.loadUserId(Activity_show_shared_category.this);
        My_index = sharedClass.loadUserIndex(Activity_show_shared_category.this);

        user_id_view = findViewById(R.id.user_id);
        category_name_view = findViewById(R.id.category_name);

        user_id_view.setText(user_id);
        category_name_view.setText(category_name);

        get_word_from_server(Activity_show_shared_category.this, category_index);
        get_category_from_server(Activity_show_shared_category.this, My_index);

        Log.e(TAG, "user_category_list size: " + user_category_list.size());

        for(int i=0; i < user_category_list.size(); i++){
            if(user_category_list.get(i).getDownload_category_index().equals(shared_category_index)){
                isDownload = true;
                category_index = user_category_list.get(i).getCategory_index();
                break;
            }
            else{
                isDownload = false;
            }
        }

//        for(int i=0; i < user_category_list.size(); i++){
//            if(user_category_list.get(i).getCategory_index().equals(category_index)){
//                isContain = true;
//                break;
//            }
//            else{
//                isContain = false;
//            }
//        }
        Log.e(TAG, "isDownload = " + isDownload);

        recyclerView = findViewById(R.id.shared_word_recycler);
        adapter = new Shared_show_word_adapter(Activity_show_shared_category.this, list);
        adapter.setOnItemClickListener(new Shared_show_word_adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                String tts_word = list.get(position).getWord();
                if(tts_word.equals(tts_word.toUpperCase())) {

                    // 한글이 포함된 문자열 (또는 영문 소문자가 포함되어 있지 않은 문자열)
                    tts_kor.speak(tts_word, TextToSpeech.QUEUE_FLUSH, null);
                    Log.e(TAG, "한글 tts");

                } else {

                    // 한글이 포함되지 않은 문자열
                    tts_eng.speak(tts_word, TextToSpeech.QUEUE_FLUSH, null);
                    Log.e(TAG, "영어 tts");

                }
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(Activity_show_shared_category.this));
        recyclerView.setAdapter(adapter);



        btn_download = findViewById(R.id.btn_download);
        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 카테고리를 업로드한 유저가 다시 내려받기를 할 때
                if(My_id.equals(user_id)){

                    // 카테고리가 이미 존재할 때 (업데이트 해야함) / 필요 파라미터 1.category_index 2.shared_category_index
                    // 1. word table에서 category_index값을 가진 단어를 모두 삭제
                    // 2. shared_word table에서 shared_category_index값을 가진 단어를 word table에 모두 추가 (이 때 category_index값은 category_index값으로 달아준다.)
                    if(isDownload){

                        Intent intent = new Intent(Activity_show_shared_category.this, Activity_dialog3.class);
                        intent.putExtra("category_index", category_index);
                        intent.putExtra("shared_category_index", shared_category_index);
                        startActivityForResult(intent, 4);
                        // 카테고리가 이미 존재하고, 업데이트 할 건지 물어보는 다이얼로그 띄워줘야 함.
//                        update_shared_category_to_server(Activity_show_shared_category.this, category_index, shared_category_index);
//                        Toast.makeText(Activity_show_shared_category.this, "기존 카테고리를 다운받은 카테고리로 업데이트 했습니다.", Toast.LENGTH_SHORT).show();

                    }

                    // (새로운 카테고리 만들어야 함)
                    else{
                        // 필요 파라미터 1.user_index, 2. category_name, 3. shared_category_index
                        download_shared_category_to_server(Activity_show_shared_category.this, shared_category_index, num_download, "my_category_download");
                        Toast.makeText(Activity_show_shared_category.this, "단어장을 다운로드 받았습니다.", Toast.LENGTH_SHORT).show();
                    }

                }
                // 다른 유저가 내려받기 할 때
                else{
                    if(isDownload){
                        Intent intent = new Intent(Activity_show_shared_category.this, Activity_dialog3.class);
                        intent.putExtra("category_index", category_index);
                        intent.putExtra("shared_category_index", shared_category_index);
                        startActivityForResult(intent, 4);
                        // 카테고리가 이미 존재하고, 업데이트 할 건지 물어보는 다이얼로그 띄워줘야 함.
//                        update_shared_category_to_server(Activity_show_shared_category.this, category_index, shared_category_index);
//                        Toast.makeText(Activity_show_shared_category.this, "기존 카테고리를 다운받은 카테고리로 업데이트 했습니다.", Toast.LENGTH_SHORT).show();

                    }

                    // (새로운 카테고리 만들어야 함)
                    else{
                        // 필요 파라미터 1.user_index, 2. category_name, 3. shared_category_index
                        download_shared_category_to_server(Activity_show_shared_category.this, shared_category_index, num_download, "other_category_download");
                        Toast.makeText(Activity_show_shared_category.this, "단어장을 다운로드 받았습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
//                Toast.makeText(Activity_show_shared_category.this, "카테고리가 추가되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * db에 저장돼 있는 단어를 가져온다.
     */
    public void get_word_from_server(Context context, String category_index){
        list.clear();
        try {

            GetPost_php task = new GetPost_php(context);
            task.execute("http://192.168.254.129/mysql_android_share_category.php", "shared_category_index", shared_category_index);

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
                        Shared_word_item item = new Shared_word_item();

                        String word = jsonObject.getString("word");
                        String meaning = jsonObject.getString("meaning");

                        item.setWord(word);
                        item.setMeaning(meaning);

                        list.add(item);

                    }
//                    Log.e(TAG, "서버에서 가져온 list size: " + list.size());

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
                        CategoryItem item = new CategoryItem();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String category = jsonObject.getString("category_name");
                        String category_index = jsonObject.getString("category_index");
                        String num_word = jsonObject.getString("num_word");
                        String download_category_index = jsonObject.getString("download_category_index");

                        item.setCategory_name(category);
                        item.setCategory_index(category_index);
                        item.setDownload_category_index(download_category_index);

                        user_category_list.add(item);

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
     * 유저가 자신의 카테고리를 해당 카테고리를 업데이트 시켜주는 메서드
     */
    public void update_shared_category_to_server(Context context, String category_index, String shared_category_index) {
        GetPost_php task = new GetPost_php(context);
        task.execute("http://192.168.254.129/mysql_android_share_category.php", "category_index", category_index, "shared_category_index", shared_category_index, "what", "update");
    }

    /**
     * 유저가 자신의 카테고리를 해당 카테고리를 업데이트 시켜주는 메서드
     */
    public void download_shared_category_to_server(Context context, String shared_category_index, String num_download, String what) {
        GetPost_php task = new GetPost_php(context);
        task.execute("http://192.168.254.129/mysql_android_share_category.php", "user_index", My_index, "category_name", category_name, "shared_category_index", shared_category_index, "num_download", num_download, "what", what);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 4){
            finish();
        }
    }
}