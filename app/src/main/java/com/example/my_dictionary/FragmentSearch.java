package com.example.my_dictionary;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class FragmentSearch extends Fragment {
    private static final String TAG= "FragmentSearch";
    private Context context;
    RadioButton korean, english;
    RadioGroup radioGroup;
    EditText searchView;
    ImageButton btn_submit;
    RecyclerView recyclerView;
    FragmentSearchAdapter adapter;

    ArrayList<search_item> result_list = new ArrayList<>();
    ArrayList<CategoryItem> category_list = new ArrayList<>();
    String user_id;
    String user_index;

    private TextToSpeech tts_kor, tts_eng;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        context = container.getContext();
        SharedClass sharedClass = new SharedClass();
        user_id = sharedClass.loadUserId(context);
        user_index = sharedClass.loadUserIndex(context);
        category_list.clear();
        get_category_from_server(container, user_index);
        Log.e(TAG, "category_list size: " + category_list.size());

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_searching_word, container, false);
        radioGroup = rootView.findViewById(R.id.radioGroup);
        korean = rootView.findViewById(R.id.korean);
        english = rootView.findViewById(R.id.english);
        radioGroup.check(korean.getId());
        searchView = rootView.findViewById(R.id.searchView);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.search_recycle);
        adapter = new FragmentSearchAdapter(context, result_list);
        adapter.setOnItemLongClickListener(new FragmentSearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final View v, final int position, search_item item) {
                if(v.getId() == R.id.btn_option){
//                    final PopupMenu popup = new PopupMenu(context, v);
//                    popup.inflate(R.menu.menu_search);
//                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                        @Override
//                        public boolean onMenuItemClick(MenuItem item) {
//
//                            if (item.getItemId() == R.id.menu1) {
//
//                                return true;
//                            }
//                            return false;
//
//                        }
//                    });
//                    popup.show();
                    String word = result_list.get(position).getSearch_word();
                    String meaning = result_list.get(position).getDefinition();
                    dialogShow(container, word, meaning);
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
                    Toast.makeText(context, "tts: "+position, Toast.LENGTH_SHORT).show();
                }
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        /**
         * 검색 버튼
         */
        btn_submit = rootView.findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(korean.isChecked()){
                    Toast.makeText(container.getContext(), "한글 검색어: " + searchView.getText().toString(), Toast.LENGTH_SHORT).show();
                    result_list.clear();
                    GetXMLTask task = new GetXMLTask();
                    task.execute(searchView.getText().toString());

                }
                else{
                    Toast.makeText(container.getContext(), "영어 검색어: " + searchView.getText().toString(), Toast.LENGTH_SHORT).show();
                    result_list.clear();
                    JsoupAsyncTask task = new JsoupAsyncTask();
                    task.execute(searchView.getText().toString());
                }

            }
        });

        tts_kor = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // 언어를 선택한다.
                tts_kor.setLanguage(Locale.KOREAN);
            }
        });

        tts_eng= new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // 언어를 선택한다.
                tts_eng.setLanguage(Locale.ENGLISH);
            }
        });

        return rootView;
    }




    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.e(TAG, "onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
    }

    /**
     * 프래그먼트의 onCreatView() 와 액티비티의 onCreate()가 호출되고 나서 호출되는 메서드.
     * 액티비티와 프래그먼트의 뷰가 모두 생성된 상태로 뷰를 변경하는 작업이 가능
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG, "onActivityCreated");
    }

    /**
     * 사용자에게 fragment를 띄어주는 메서드
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
    }

    /**
     * 사용자와 상호작용 가능
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
    }

    /**
     * 프래그먼트의 부모 액티비티가 아닌 액티비티가 나오면 onPause()가 호출, 해당 프래그먼트는 Backstack으로 들어감.
     *
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
    }

    /**
     * 아예 다른 액티비티로 전환 시 onStop() 메서드 호출
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
    }

    /**
     * 프래그먼트의 뷰가 제거되는 단계, 만약 backstack으로부터 프래그먼트가 돌아온다면 onCreateView() 메소드를 호출
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "onDestroyView");
    }

    /**
     * 프래그먼트의 리소스들을 제거
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }

    /**
     * 프래그먼트가 액티비티로부터 떨어지는 단계
     */
    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(TAG, "onDetach");
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
                Toast.makeText(getActivity(), "Parsing Error", Toast.LENGTH_SHORT).show();
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
                result_list.add(item);
            }

            super.onPostExecute(doc);
            refresh_fragment();
        }
    }

    /**
     *  영어로 검색 시, 다음 사전에서 결과를 크롤링 해주는 class
     */
    private class JsoupAsyncTask extends AsyncTask<String, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            String search_word = (String) params[0];
            try {

                org.jsoup.nodes.Document doc = Jsoup.connect("https://dic.daum.net/search.do?q=" + search_word).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36").get();

                Elements word = doc.select("div.card_word[data-tiara-layer=word eng] a[data-tiara-action-name=표제어 클릭]");
                Elements meaning = doc.select("div.card_word[data-tiara-layer=word eng] ul.list_search");

                for (int i=0; i < word.size(); i++){
                    search_item item = new search_item();
                    org.jsoup.nodes.Element eWord = word.get(i);
                    org.jsoup.nodes.Element eMeaning = meaning.get(i);
                    item.setSearch_word(eWord.text().trim());
                    item.setDefinition(eMeaning.text().trim());
                    result_list.add(item);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            refresh_fragment();
        }
    }


    public void addItem(String name, String category_index){
        CategoryItem item = new CategoryItem();

        item.setCategory_name(name);
        item.setCategory_index(category_index);
        category_list.add(item);
    }

    /**
     * 쉐어드에 저장된 유저 id의 category 데이터를 서버에서 가져온다.
     */
    public void get_category_from_server(ViewGroup container, String user_index){
        try {

            GetPost_php task = new GetPost_php(container.getContext());
            task.execute("http://192.168.254.129/mysql_android_pushData.php", "user_index", user_index);

            String callBackValue = task.get();

            if(callBackValue.isEmpty() || callBackValue.equals("") || callBackValue == null || callBackValue.contains("Error")) {
                Toast.makeText(container.getContext(), "등록되지 않은 사용자이거나, 전송오류입니다.", Toast.LENGTH_SHORT).show();
            }

            else {
//                Log.e(TAG, callBackValue);
                JSONArray jsonArray = null;
                try {

                    jsonArray = new JSONArray(callBackValue);
//                    JSONObject jsonObject = jsonArray.getJSONObject(0);
////                    Log.e(TAG, String.valueOf(jsonObject));
//
//                    for(int i =1; i < 11; i++){
//                        if(!jsonObject.isNull("category" + i)){
//                            if(!jsonObject.getString("category" + i).equals("")){
//                                String category = jsonObject.getString("category" + i);
//                                addItem(category);
//                            }
//                        }
//                    }


                    for(int i=0; i < jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String category = jsonObject.getString("category_name");
                        String category_index = jsonObject.getString("category_index");
                        Log.e(TAG, "category name: " + category);
                        addItem(category, category_index);
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
     * db에 있는 카테고리에 단어를 추가 시킨다.
     */
    public void add_word_to_server(ViewGroup container, String category_index, String word, String meaning) {
        GetPost_php task = new GetPost_php(container.getContext());
        task.execute("http://192.168.254.129/mysql_android_word.php", "category_index", category_index, "word", word, "meaning", meaning);
    }

    public void dialogShow(final ViewGroup viewGroup, final String word, final String meaning){
        final LinearLayout linear = (LinearLayout) View.inflate(viewGroup.getContext(), R.layout.dialog_list, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(viewGroup.getContext());
        final ListView listView = (ListView) linear.findViewById(R.id.dialog_list);
        final listAdapter adapter = new listAdapter(context, category_list);
        adapter.setOnItemClickListener(new listAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                String category_index = category_list.get(position).getCategory_index();
                add_word_to_server(viewGroup, category_index, word, meaning);
                Toast.makeText(context, "단어를 카테고리에 추가했습니다." , Toast.LENGTH_SHORT).show();

            }
        });
        listView.setAdapter(adapter);

        builder.setView(linear);
        builder.setCancelable(true);
        builder.create();
        builder.show();
    }

    private void refresh_fragment(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();

    }
}
