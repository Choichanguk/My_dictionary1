package com.example.my_dictionary;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class ActivityViewPager extends AppCompatActivity {
    private final static String TAG = "ActivityViewPager";
    private ViewPager2 viewPager2;
    ArrayList<search_item> list = new ArrayList<>();
    String category_index;
    ViewPagerAdapter adapter;
    private TextToSpeech tts_kor, tts_eng;

    boolean isAuto, all_word;
    int currentPage = 0;
    Timer timer;
    TimerTask task;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    long PERIOD_MS = 3000; // time in milliseconds between successive task executions.
    String time;
    int time_int;
    Handler handler;
    Runnable Update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        Intent intent = getIntent();
        category_index = intent.getStringExtra("category_index");
        isAuto = intent.getBooleanExtra("isAuto", false);
        all_word = intent.getBooleanExtra("all_word", true);
        if(isAuto){
            time = intent.getStringExtra("time");
            time_int = Integer.parseInt(time.replaceAll("[^0-9]", ""));
            PERIOD_MS = time_int * 1000;
        }
        Log.e(TAG, "isAuto: " + isAuto);
        Log.e(TAG, "time: " + PERIOD_MS);


        get_word_from_server(ActivityViewPager.this, category_index);

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

        viewPager2 = findViewById(R.id.pager);
        adapter = new ViewPagerAdapter(list);
        adapter.setOnItemClickListener(new ViewPagerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position, search_item item) {
                if(v.getId() == R.id.btn_tts){
                    String tts_word = item.getSearch_word();
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
                else if(v.getId() == R.id.btn_bookmark){
                    if(item.isCheck){
                        item.setCheck(false);
                        add_isCheck_to_server(ActivityViewPager.this, item.getIndex_word(), "0");
                    }
                    else{
                        item.setCheck(true);
                        add_isCheck_to_server(ActivityViewPager.this, item.getIndex_word(), "1");
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        viewPager2.setAdapter(adapter);

        if(isAuto){
             handler = new Handler();
             Update = new Runnable() {
                @Override
                public void run() {

                    if(currentPage == list.size()) {
                        task.cancel();
                        OnClickHandler();
                    }
                    else{
                        String tts_word = list.get(currentPage).getSearch_word();
                        if(tts_word.equals(tts_word.toUpperCase())) {
                            // 한글이 포함된 문자열 (또는 영문 소문자가 포함되어 있지 않은 문자열)
                            tts_kor.speak(tts_word, TextToSpeech.QUEUE_FLUSH, null);
                            Log.e(TAG, "한글 tts");

                        } else {
                            // 한글이 포함되지 않은 문자열
                            tts_eng.speak(tts_word, TextToSpeech.QUEUE_FLUSH, null);
                            Log.e(TAG, "영어 tts");
                        }
                        viewPager2.setCurrentItem(currentPage++, true);
                    }

                }
            };

            timer = new Timer();

            // timer task 정의
            task = new TimerTask() {
                @Override
                public void run() {
                    handler.post(Update);

                }
            };

            timer.schedule(task, DELAY_MS, PERIOD_MS);

        }
    }

    /**
     * db에 저장돼 있는 단어를 가져온다.
     */
    public void get_word_from_server(Context context, String category_index){
        list.clear();
        try {

            GetPost_php task = new GetPost_php(context);
            task.execute("http://192.168.254.129/mysql_android_word.php", "category_index", category_index);

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
                        if(all_word){
                            search_item item = new search_item();

                            String word = jsonObject.getString("word");
                            String meaning = jsonObject.getString("meaning");
                            String word_index = jsonObject.getString("word_index");
                            String isCheck = jsonObject.getString("bookmark");

                            item.setSearch_word(word);
                            item.setDefinition(meaning);
                            item.setIndex_word(word_index);
                            if(isCheck.equals("1")){
                                item.setCheck(true);
                            }

                            list.add(item);
                        }
                        else {
                            if(jsonObject.getString("bookmark").equals("1")){
                                search_item item = new search_item();

                                String word = jsonObject.getString("word");
                                String meaning = jsonObject.getString("meaning");
                                String word_index = jsonObject.getString("word_index");
                                String isCheck = jsonObject.getString("bookmark");

                                item.setSearch_word(word);
                                item.setDefinition(meaning);
                                item.setIndex_word(word_index);
                                if(isCheck.equals("1")){
                                    item.setCheck(true);
                                }

                                list.add(item);
                            }
                        }


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
     * 단어의 북마크 체크여부를 db에 저장시키는 메서드
     */
    public void add_isCheck_to_server(Context context, String word_index, String isCheck) {
        GetPost_php task = new GetPost_php(context);
        task.execute("http://192.168.254.129/mysql_android_word.php", "word_index", word_index, "isCheck", isCheck, "what", "isCheck");
    }

    /**
     * 단어 한바퀴 돈 다음 재시작 할 건지 묻는 다이얼로그
     */
    public void OnClickHandler()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("단어 연습").setMessage("연습이 모두 끝났습니다. 재시작 하시겠습니까?");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                currentPage = 0;

                timer = new Timer();

                // timer task 정의
                task = new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(Update);

                    }
                };
                timer.schedule(task, DELAY_MS, PERIOD_MS);
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                finish();
            }
        });


        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }
}

