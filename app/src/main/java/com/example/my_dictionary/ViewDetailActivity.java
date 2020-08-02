package com.example.my_dictionary;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my_dictionary.data.AddedSearchAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ViewDetailActivity extends AppCompatActivity {
    private static final String TAG= "ViewDetailActivity";

    String word, meaning, word_index, write_result;
    TextView result_word, result_meaning;
    Button btn_add_content, btn_add_search, btn_add_write, btn_submit_write;
    ImageButton btn_submit, btn_option, btn_plus_added_content;
    RadioButton korean, english;
    RadioGroup radioGroup;
    EditText searchView;    // 검색어 입력창
    EditText writeView;     // 직접입력 입력창
    ImageView  btn_open_search, btn_close_search, btn_open_write, btn_close_write;

    LinearLayout added_contents_layout;     // 추가내용을 담고있는 레이아웃
    ViewGroup viewGroup;

    ConstraintLayout add_search, add_write;

    ArrayList<search_item> addedItem_search = new ArrayList<>();
    ArrayList<search_item> addedItem_write = new ArrayList<>();
    ArrayList<search_item> search_list = new ArrayList<>();

    RecyclerView search_recycler, added_recycler_search, added_recycler_write;
    SearchAdapter search_adapter;
    WriteAdapter added_adapter_write;
    AddedSearchAdapter added_adapter_search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_detail);


        Intent intent = getIntent();
        word = intent.getStringExtra("word");
        meaning = intent.getStringExtra("meaning");
        word_index = intent.getStringExtra("word_index");

        get_word_from_server(ViewDetailActivity.this, word_index);

        Log.e(TAG, "added_list size: " + addedItem_search.size());

        result_word = findViewById(R.id.result_word);
        result_meaning = findViewById(R.id.result_definition);
        result_word.setText(word);
        result_meaning.setText(meaning);
//        add_search = findViewById(R.id.add_search);
//        add_write = findViewById(R.id.add_write);
        viewGroup = (ViewGroup) ViewDetailActivity.this.getWindow().getDecorView();

        btn_open_search = findViewById(R.id.btn_open_search);
        btn_close_search = findViewById(R.id.btn_close_search);
        btn_open_write = findViewById(R.id.btn_open_write);
        btn_close_write = findViewById(R.id.btn_close_write);

        /**
         * 사전검색 추가내용 펼치기, 접기 버튼
         */
        btn_open_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_open_search.setVisibility(View.GONE);
                btn_close_search.setVisibility(View.VISIBLE);
                added_recycler_search.setVisibility(View.VISIBLE);
            }
        });

        btn_close_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_open_search.setVisibility(View.VISIBLE);
                btn_close_search.setVisibility(View.GONE);
                added_recycler_search.setVisibility(View.GONE);
            }
        });

        /**
         * 직접입력 추가내용 펼치기, 접기 버튼
         */
        btn_open_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_open_write.setVisibility(View.GONE);
                btn_close_write.setVisibility(View.VISIBLE);
                added_recycler_write.setVisibility(View.VISIBLE);
            }
        });

        btn_close_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_open_write.setVisibility(View.VISIBLE);
                btn_close_write.setVisibility(View.GONE);
                added_recycler_write.setVisibility(View.GONE);
            }
        });


        /**
         * 추가 직접입력 내용을 보여주는 리사이클러뷰
         */
        added_recycler_write = findViewById(R.id.added_recycler2);
        added_adapter_write = new WriteAdapter(ViewDetailActivity.this, addedItem_write);
        added_adapter_write.setOnItemLongClickListener(new WriteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, final int position) {
                if(v.getId() == R.id.btn_option){
                    final PopupMenu popup = new PopupMenu(ViewDetailActivity.this, v);
                    popup.inflate(R.menu.menu_recycler);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            // 직접입력 내용 수정
                            if (item.getItemId() == R.id.menu1) {

                                String added_word_index = addedItem_write.get(position).getIndex_word();
                                String meaning = addedItem_write.get(position).getDefinition();
                                dialogShow(viewGroup, meaning, added_word_index);

                                return true;
                            }
                            // 직접입력 내용 삭제
                            else if(item.getItemId() == R.id.menu2) {
                                Toast.makeText(ViewDetailActivity.this, "선택단어를 삭제했습니다.", Toast.LENGTH_SHORT).show();
                                String addedword_index = addedItem_write.get(position).getIndex_word();
                                delete_word_to_server(ViewDetailActivity.this, addedword_index);
                                get_word_from_server(ViewDetailActivity.this, word_index);
                                added_adapter_write.notifyDataSetChanged();

                            }
                            return false;

                        }
                    });
                    popup.show();
                }
                else {
                    Toast.makeText(ViewDetailActivity.this, "position: " + position, Toast.LENGTH_SHORT).show();
                }
            }
        });
        added_recycler_write.setLayoutManager(new LinearLayoutManager(ViewDetailActivity.this));
        added_recycler_write.setAdapter(added_adapter_write);

        /**
         * 추가 사전 검색 내용 을 보여주는 리사이클러뷰
         */
        added_recycler_search = findViewById(R.id.added_recycler);
        added_adapter_search = new AddedSearchAdapter(ViewDetailActivity.this, addedItem_search);
        added_adapter_search.setOnItemLongClickListener(new AddedSearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, final int position) {
//                String status = addedItem.get(position).getStatus();
                // 리사이클러뷰 사전검색 아이템의 옵션 버튼을 눌렀을 때 (삭제만 됨)

                    final PopupMenu popup = new PopupMenu(ViewDetailActivity.this, v);
                    popup.inflate(R.menu.menu_view_detail_main_word);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.menu1) {

                                Toast.makeText(ViewDetailActivity.this, "선택단어를 삭제했습니다.", Toast.LENGTH_SHORT).show();
                                String addedword_index = addedItem_search.get(position).getIndex_word();
                                delete_word_to_server(ViewDetailActivity.this, addedword_index);
                                get_word_from_server(ViewDetailActivity.this, word_index);
                                added_adapter_search.notifyDataSetChanged();
                                return true;
                            }
                            return false;

                        }
                    });
                    popup.show();

            }
        });
        added_recycler_search.setLayoutManager(new LinearLayoutManager(ViewDetailActivity.this));
        added_recycler_search.setAdapter(added_adapter_search);

        /**
         * 추가내용 plus 버튼을 눌렀을 때
         */
        btn_plus_added_content = findViewById(R.id.btn_plus_added_content);
        btn_plus_added_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plus_content_dialog dialog = new plus_content_dialog(ViewDetailActivity.this, word_index, new plus_content_dialog.CustomDialogClickListener() {

                    @Override
                    public void onSearchClick(String word, String meaning) {
                        add_word_to_server(ViewDetailActivity.this, word_index, word, meaning);
                        get_word_from_server(ViewDetailActivity.this, word_index);
                        added_adapter_search.notifyDataSetChanged();
                    }

                    @Override
                    public void onWriteClick(String write_result) {
                        add_word_to_server(ViewDetailActivity.this, word_index, "", write_result);
                        get_word_from_server(ViewDetailActivity.this, word_index);
                        added_adapter_search.notifyDataSetChanged();
                    }
                });
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                dialog.show();
            }
        });

//        btn_add_search = findViewById(R.id.btn_add_search);
//        btn_add_write = findViewById(R.id.btn_add_write);
//
//        btn_add_content = findViewById(R.id.btn_add_content);
//        btn_add_content.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                btn_add_search.setVisibility(View.VISIBLE);
//                btn_add_write.setVisibility(View.VISIBLE);
//            }
//        });
//
//        btn_add_search.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                add_search.setVisibility(View.VISIBLE);
//                add_write.setVisibility(View.GONE);
//            }
//        });
//
//        btn_add_write.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                add_write.setVisibility(View.VISIBLE);
//                add_search.setVisibility(View.GONE);
//            }
//        });
//
//        radioGroup = findViewById(R.id.radioGroup);
//        korean = findViewById(R.id.korean);
//        english = findViewById(R.id.english);
//        radioGroup.check(korean.getId());
//        searchView = findViewById(R.id.searchView);

        /**
         * 검색 버튼
         */
//        btn_submit = findViewById(R.id.btn_submit);
//        btn_submit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(korean.isChecked()){
//                    Toast.makeText(ViewDetailActivity.this, "한글 검색어: " + searchView.getText().toString(), Toast.LENGTH_SHORT).show();
//                    search_list.clear();
//                    GetXMLTask task = new GetXMLTask();
//                    task.execute(searchView.getText().toString());
//
//                }
//                else{
//                    Toast.makeText(ViewDetailActivity.this, "영어 검색어: " + searchView.getText().toString(), Toast.LENGTH_SHORT).show();
//                    search_list.clear();
//                    JsoupAsyncTask task = new JsoupAsyncTask();
//                    task.execute(searchView.getText().toString());
//                    Log.e(TAG, "영어검색 리스트 사이즈: " + search_list.size());
//
//                }
//                search_adapter.notifyDataSetChanged();
//            }
//        });

        /**
         * 직접입력 제출 버튼
         */
//        writeView = findViewById(R.id.writeView);
//        btn_submit_write = findViewById(R.id.btn_submit_write);
//        btn_submit_write.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                write_result = writeView.getText().toString();
//                Log.e(TAG, "write_result: " + write_result);
//                add_word_to_server(ViewDetailActivity.this, word_index, "", write_result);
//                get_word_from_server(ViewDetailActivity.this, word_index);
//                added_adapter_search.notifyDataSetChanged();
//                writeView.setText(null);
//                Toast.makeText(getApplicationContext(), "내용이 추가되었습니다.", Toast.LENGTH_SHORT).show();
//
//            }
//        });

        /**
         * 검색결과를 보여주는 리사이클러뷰
         */
//        search_recycler = findViewById(R.id.search_recycle);
//        search_adapter = new SearchAdapter(ViewDetailActivity.this, search_list);
//        search_adapter.setOnItemLongClickListener(new SearchAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View v, final int position) {
//                if(v.getId() == R.id.btn_option){
//                    final PopupMenu popup = new PopupMenu(ViewDetailActivity.this, v);
//                    popup.inflate(R.menu.menu_add_word);
//                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                        @Override
//                        public boolean onMenuItemClick(MenuItem item) {
//
//                            if (item.getItemId() == R.id.menu1) {
//                                String word = search_list.get(position).getSearch_word();
//                                String meaning = search_list.get(position).getDefinition();
//                                add_word_to_server(ViewDetailActivity.this, word_index, word, meaning);
//                                get_word_from_server(ViewDetailActivity.this, word_index);
//                                added_adapter_search.notifyDataSetChanged();
////                                dialogShow(container, word, meaning);
//                                return true;
//                            }
//                            return false;
//
//                        }
//                    });
//                    popup.show();
//                }
//            }
//        });
//        search_recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//        search_recycler.setAdapter(search_adapter);

    }

    /**
     * 한글로 검색 시, 검색결과에 대한 XML문서를 파싱해주는 class
     */
    private class GetXMLTask extends AsyncTask<String, Void, Document> {

        @Override
        protected Document doInBackground(String... params) {
            String word = (String) params[0];
            URL url;
            Document doc = null;
            try {
                url = new URL("https://opendict.korean.go.kr/api/search?certkey_no=1635&key=2A216BBB3D312FD5353ED65031BDD921&target_type=search&part=word&q="+word+"&sort=dict&start=1&num=10");
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {
            NodeList nodeList = doc.getElementsByTagName("item");

            for(int i = 0; i< nodeList.getLength(); i++){
                String title = "";
                String content = "";
                search_item item = new search_item();
                Node node = nodeList.item(i);
                Element fstElement = (Element) node;


                NodeList idx = fstElement.getElementsByTagName("word");
                title = idx.item(0).getChildNodes().item(0).getNodeValue() +"\n";
                item.setSearch_word(title);

                NodeList gugun = fstElement.getElementsByTagName("definition");
                for(int j=0; j < gugun.getLength(); j++){
                    content += gugun.item(j).getFirstChild().getNodeValue() +"\n";
                }
                item.setDefinition(content);
                search_list.add(item);
                Log.e(TAG, "list size: " + search_list.size());
            }

            super.onPostExecute(doc);
//            search_recycler.notifyAll();
        }
    }

    /**
     *  영어로 검색 시, 다음 사전에서 결과를 크롤링 해주는 class
     */
    private class JsoupAsyncTask extends AsyncTask<String, Void, org.jsoup.nodes.Document>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected org.jsoup.nodes.Document doInBackground(String... params) {
            String search_word = (String) params[0];
            org.jsoup.nodes.Document doc = null;
            try {

                doc = Jsoup.connect("https://dic.daum.net/search.do?q=" + search_word).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36").get();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(org.jsoup.nodes.Document doc) {


            Elements word = doc.select("div.card_word[data-tiara-layer=word eng] a[data-tiara-action-name=표제어 클릭]");
            Elements meaning = doc.select("div.card_word[data-tiara-layer=word eng] ul.list_search");

            for (int i=0; i < word.size(); i++){
                search_item item = new search_item();
                org.jsoup.nodes.Element eWord = word.get(i);
                org.jsoup.nodes.Element eMeaning = meaning.get(i);
                item.setSearch_word(eWord.text().trim());
                item.setDefinition(eMeaning.text().trim());
                search_list.add(item);
            }
            Log.e(TAG, "list size: " + search_list.size());
            super.onPostExecute(doc);

        }
    }

    /**
     * db에 있는 카테고리에 추가단어를 추가 시킨다.
     */
    public void add_word_to_server(Context context, String word_index, String word, String meaning) {
        GetPost_php task = new GetPost_php(context);
        task.execute("http://192.168.254.129/mysql_android_addedword.php", "word_index", word_index, "word", word, "meaning", meaning);
    }

    /**
     * db에 저장돼 있는 단어를 가져온다.
     */
    public void get_word_from_server(Context context, String word_index){
        addedItem_search.clear();
        addedItem_write.clear();
        try {

            GetPost_php task = new GetPost_php(context);
            task.execute("http://192.168.254.129/mysql_android_addedword.php", "word_index", word_index);

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
                        String word_added_index = jsonObject.getString("word_added_index");

                        item.setSearch_word(word);
                        item.setDefinition(meaning);
                        item.setIndex_word(word_added_index);

                        if(!item.getSearch_word().equals("")){
                            addedItem_search.add(item);
                        }
                        else{
                            addedItem_write.add(item);
                        }

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
     * 유저가 선택한 단어를 db에서 삭제시킨다.
     */
    public void delete_word_to_server(Context context, String word_added_index) {
        GetPost_php task = new GetPost_php(context);
        task.execute("http://192.168.254.129/mysql_android_addedword.php", "word_added_index", word_added_index, "what", "delete");
    }

    /**
     * db에 저장돼 있는 카테고리를 업데이트 시킨다.
     */
    public void update_word_to_server(Context context, String word_added_index, String edited_meaning) {
        GetPost_php task = new GetPost_php(context);
        task.execute("http://192.168.254.129/mysql_android_addedword.php", "word_added_index", word_added_index,"edited_meaning", edited_meaning, "what", "update");
    }

    /**
     * 직접입력 단어의 수정버튼 눌렀을 때 나오는 다이얼로그
     */
    public void dialogShow(final ViewGroup viewGroup, final String meaning, final String added_word_index){
        final LinearLayout linear = (LinearLayout) View.inflate(viewGroup.getContext(), R.layout.dialog_view_detail, null);
        final EditText meaning_edit = (EditText) linear.findViewById(R.id.edit_meaning);

        meaning_edit.setText(meaning);
        AlertDialog.Builder builder = new AlertDialog.Builder(viewGroup.getContext());
        builder.setView(linear);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
//                EditText category_name = (EditText) linear.findViewById(R.id.category);
//                category_name.setText(edit_content);
                String meaning_edited = meaning_edit.getText().toString();

                update_word_to_server(ViewDetailActivity.this, added_word_index, meaning_edited);
                get_word_from_server(ViewDetailActivity.this, word_index);
                dialog.dismiss();
                viewGroup.removeView(linear);

                added_adapter_write.notifyDataSetChanged();

            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
                viewGroup.removeView(linear);
            }
        });
        builder.setCancelable(true);
        builder.create();
        builder.show();
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
}