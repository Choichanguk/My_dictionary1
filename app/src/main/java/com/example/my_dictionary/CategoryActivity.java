package com.example.my_dictionary;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class CategoryActivity extends AppCompatActivity{
    private static final String TAG= "CategoryActivity";
    android.widget.SearchView searchView;
//    SearchView ;
    TextView category;
    RecyclerView recyclerView;
    SearchAdapter adapter;
    String category_index, category_name;
    ArrayList<search_item> list = new ArrayList<>();

    private TextToSpeech tts_kor, tts_eng;

    Spinner spinner;
    String[] list_spinner = {"전체 보기", "★표시 단어만 보기", "최신 순", "오래된 순"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Intent intent = getIntent();
        category_index = intent.getStringExtra("category_index");
        category_name = intent.getStringExtra("category_name");

        category = findViewById(R.id.category_name);
        category.setText(category_name);

        get_word_from_server(CategoryActivity.this, category_index);
        Log.e(TAG, "list size: " + list.size());
        recyclerView = findViewById(R.id.word_recycler);
        adapter = new SearchAdapter(CategoryActivity.this, list);
        adapter.setOnItemLongClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, final int position, search_item item) {
                final String word_index = list.get(position).getIndex_word();
                final String word = list.get(position).getSearch_word();
                final String meaning = list.get(position).getDefinition();

                if(v.getId() == R.id.btn_option){
                    final PopupMenu popup = new PopupMenu(getApplicationContext(), v);
                    popup.inflate(R.menu.menu_view_word);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()){
                                case R.id.menu1:
                                    // 단어 상세보기 로직
                                    Intent intent = new Intent(CategoryActivity.this, ViewDetailActivity.class);
                                    intent.putExtra("word", word);
                                    intent.putExtra("word_index", word_index);
                                    intent.putExtra("meaning", meaning);

                                    startActivity(intent);
//                                    Toast.makeText(getApplicationContext(), "수정 단어 인덱스: "+word_index, Toast.LENGTH_SHORT).show();
                                    return true;

                                case R.id.menu2:
                                    // 단어 삭제 로직
                                    delete_word_to_server(getApplicationContext(), word_index, category_index);
                                    get_word_from_server(getApplicationContext(), category_index);
                                    adapter.notifyDataSetChanged();
//                                    Toast.makeText(getApplicationContext(), "삭제 단어 인덱스: "+word_index, Toast.LENGTH_SHORT).show();
                                    return true;

                                default:
                                    return false;
                            }
                        }
                    });
                    popup.show();
                }
                else if(v.getId() == R.id.btn_tts){
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
                    Toast.makeText(getApplicationContext(), "tts: "+position, Toast.LENGTH_SHORT).show();
                }
                else if(v.getId() == R.id.btn_bookmark){

                    if(item.isCheck){
                        item.setCheck(false);
                        add_isCheck_to_server(CategoryActivity.this, item.getIndex_word(), "0");
                    }
                    else{
                        item.setCheck(true);
                        add_isCheck_to_server(CategoryActivity.this, item.getIndex_word(), "1");
                    }
                    adapter.notifyDataSetChanged();

                }
                else{
                    Intent intent = new Intent(CategoryActivity.this, ViewDetailActivity.class);
                    intent.putExtra("word", word);
                    intent.putExtra("word_index", word_index);
                    intent.putExtra("meaning", meaning);

                    startActivity(intent);
                }
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(CategoryActivity.this));
        recyclerView.setAdapter(adapter);

        searchView = findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });

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


        /**
         * 단어를 정렬하는 로직
         */
        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override   // position 으로 몇번째 것이 선택됬는지 값을 넘겨준다
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(list_spinner[position].equals("최신 순")){
                    MiniComparator comparator = new MiniComparator("내림차순");
                    Collections.sort(list, comparator);
                    adapter.notifyDataSetChanged();
                }
                else if(list_spinner[position].equals("오래된 순")){
                    MiniComparator comparator = new MiniComparator("오름차순");
                    Collections.sort(list, comparator);
                    adapter.notifyDataSetChanged();
                }else if(list_spinner[position].equals("★표시 단어만 보기")){
                    Log.e(TAG, "★표시 단어만 보기");
//                    ArrayList<search_item> new_array = new ArrayList<>();
//                    for(int i=0; i < list.size(); i++){
//                        if(list.get(i).isCheck()){
//                            new_array.add(list.get(i));
//                        }
//                    }
//                    list = new_array;
                    get_word_from_server(getApplicationContext(), category_index, "isCheck");
                    Log.e(TAG, "list size: " + list.size());
                    adapter.notifyDataSetChanged();
                }
                else if(list_spinner[position].equals("전체 보기")){
                    Log.e(TAG, "전체 보기");
                    get_word_from_server(getApplicationContext(), category_index);
                    adapter.notifyDataSetChanged();
                }
//                Log.e(TAG, list_spinner[position] + "선택되었습니다");

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    @Override

    protected void onDestroy() {

        super.onDestroy();

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
                    Log.e(TAG, "서버에서 가져온 list size: " + list.size());

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
     * db에 저장돼 있는 북마크된 단어만 가져온다.
     */
    public void get_word_from_server(Context context, String category_index, String what){
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
                            list.add(item);
                        }


                    }
                    Log.e(TAG, "서버에서 가져온 list size: " + list.size());

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
     * 유저가 선택한 단어를 db에서 삭제시킨다.
     */
    public void delete_word_to_server(Context context, String word_index, String category_index) {
        GetPost_php task = new GetPost_php(context);
        task.execute("http://192.168.254.129/mysql_android_word.php", "word_index", word_index, "what", "delete", "category_index", category_index);
    }

    /**
     * 단어의 북마크 체크여부를 db에 저장시키는 메서드
     */
    public void add_isCheck_to_server(Context context, String word_index, String isCheck) {
        GetPost_php task = new GetPost_php(context);
        task.execute("http://192.168.254.129/mysql_android_word.php", "word_index", word_index, "isCheck", isCheck, "what", "isCheck");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.e(TAG, "onBackPressed");
        Intent intent = new Intent();
        setResult(3, intent);
        finish();
    }

    /**
     * 객체가 담긴 리스트를 정렬해주는 class
     */
    class MiniComparator implements Comparator<search_item> {
        String what;
        public MiniComparator(String what) {
            this.what = what;
        }

        @Override
        public int compare(search_item first_item, search_item second_item) {
            int firstValue = Integer.parseInt(first_item.getIndex_word());
            int secondValue = Integer.parseInt(second_item.getIndex_word());

            if(what.equals("내림차순")){
                // 내림차순
                if(firstValue > secondValue){
                    return -1;
                }
                else if(firstValue < secondValue){
                    return 1;
                }
                else{
                    return 0;
                }
            }
            else{
                if(firstValue > secondValue){
                    return 1;
                }
                else if(firstValue < secondValue){
                    return -1;
                }
                else{
                    return 0;
                }
            }
        }
    }
}