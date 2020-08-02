package com.example.my_dictionary;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class plus_content_dialog extends Dialog {
    private Context context;
    private CustomDialogClickListener customDialogClickListener;
    private Button btn_search, btn_write, btn_submit_write;
    private ImageButton btn_submit;
    private ConstraintLayout search_layout;
    private LinearLayout write_layout;

    private RadioButton korean, english;
    private RadioGroup radioGroup;
    private EditText searchView;    // 검색어 입력창
    private EditText writeView;     // 직접입력 입력창

    private RecyclerView search_recycler;
    private SearchAdapter2 search_adapter;
    private SearchAdapter adapter;

    private String word_index;
    ArrayList<search_item> search_list = new ArrayList<>();


    public plus_content_dialog(@NonNull Context context, String word_index, CustomDialogClickListener customDialogClickListener) {
        super(context);
        this.context = context;
        this.customDialogClickListener =customDialogClickListener;
        this.word_index = word_index;
//        this.adapter = adapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plus_content_layout);

        btn_search = findViewById(R.id.btn_add_search);
        btn_write = findViewById(R.id.btn_add_write);
        search_layout = findViewById(R.id.add_search);
        write_layout = findViewById(R.id.add_write);

        radioGroup = findViewById(R.id.radioGroup);
        korean = findViewById(R.id.korean);
        english = findViewById(R.id.english);
        radioGroup.check(korean.getId());
        searchView = findViewById(R.id.searchView);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "검색버튼 클릭", Toast.LENGTH_SHORT).show();
                search_layout.setVisibility(View.VISIBLE);
                write_layout.setVisibility(View.GONE);
            }
        });

        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_layout.setVisibility(View.GONE);
                write_layout.setVisibility(View.VISIBLE);
            }
        });


        search_recycler = findViewById(R.id.search_recycle);
        search_adapter = new SearchAdapter2(context, search_list);
        search_adapter.setOnItemLongClickListener(new SearchAdapter2.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                String word = search_list.get(position).getSearch_word();
                String meaning = search_list.get(position).getDefinition();

                customDialogClickListener.onSearchClick(word, meaning);
                Toast.makeText(context, "단어를 추가했습니다.", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        search_recycler.setLayoutManager(new LinearLayoutManager(context));
        search_recycler.setAdapter(search_adapter);


        /**
         * 검색버튼
         */
        btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(korean.isChecked()){
//                    customDialogClickListener.onSearchClick();
                    search_list.clear();
                    GetXMLTask task = new GetXMLTask();
                    task.execute(searchView.getText().toString());
                }
                else{
                    search_list.clear();
                    JsoupAsyncTask task = new JsoupAsyncTask();
                    task.execute(searchView.getText().toString());
                }

            }
        });

        writeView = findViewById(R.id.writeView);
        btn_submit_write = findViewById(R.id.btn_submit_write);
        btn_submit_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String write_result = writeView.getText().toString();
                customDialogClickListener.onWriteClick(write_result);
                dismiss();
            }
        });
    }

    public interface CustomDialogClickListener{
        void onSearchClick(String word, String meaning);
        void onWriteClick(String write_result);
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
                Toast.makeText(context, "Parsing Error", Toast.LENGTH_SHORT).show();
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
                Log.e("TAG", "list size: " + search_list.size());
            }

            super.onPostExecute(doc);
            search_adapter.notifyDataSetChanged();
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
            Log.e("TAG", "list size: " + search_list.size());
            super.onPostExecute(doc);
            search_adapter.notifyDataSetChanged();
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
//    public void get_word_from_server(Context context, String word_index){
//        addedItem_search.clear();
//        addedItem_write.clear();
//        try {
//
//            GetPost_php task = new GetPost_php(context);
//            task.execute("http://192.168.254.129/mysql_android_addedword.php", "word_index", word_index);
//
//            String callBackValue = task.get();
//
//            if(callBackValue.isEmpty() || callBackValue.equals("") || callBackValue == null || callBackValue.contains("Error")) {
//                Toast.makeText(context, "등록되지 않은 사용자이거나, 전송오류입니다.", Toast.LENGTH_SHORT).show();
//            }
//
//            else {
////                Log.e(TAG, callBackValue);
//                JSONArray jsonArray = null;
//                try {
//
//                    jsonArray = new JSONArray(callBackValue);
//
//                    for(int i=0; i < jsonArray.length(); i++){
//                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//                        search_item item = new search_item();
//
//                        String word = jsonObject.getString("word");
//                        String meaning = jsonObject.getString("meaning");
//                        String word_added_index = jsonObject.getString("word_added_index");
//
//                        item.setSearch_word(word);
//                        item.setDefinition(meaning);
//                        item.setIndex_word(word_added_index);
//
//                        if(!item.getSearch_word().equals("")){
//                            addedItem_search.add(item);
//                        }
//                        else{
//                            addedItem_write.add(item);
//                        }
//
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
}
