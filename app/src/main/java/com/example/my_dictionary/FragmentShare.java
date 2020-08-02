package com.example.my_dictionary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;

public class FragmentShare extends Fragment {
    private static final String TAG= "FragmentShare";
    private int REQUEST_CODE = 2;

    private ArrayList<CategoryItem> list;
    private ArrayList<Shared_item> shared_list;
    private Context context;
    RecyclerView recyclerView;
    SharedAdapter adapter;
    Button btn_share;

    String user_index, user_id;
    SearchView searchView;
    Spinner spinner;
    String[] list_spinner = {"전체 보기", "다운로드 많은 순", "다운로드 적은 순"};

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        final SharedClass sharedClass = new SharedClass();
        user_index = sharedClass.loadUserIndex(context);
        user_id = sharedClass.loadUserId(context);
        list = new ArrayList<>();
        shared_list = new ArrayList<>();


        get_shared_category_from_server();

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_share, container, false);

        /**
         * 카테고리 공유 버튼
         */
        btn_share = rootView.findViewById(R.id.btn_shared);
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Activity_dialog.class);
                intent.putExtra("user_index", user_index);
                intent.putExtra("user_id", user_id);
                startActivityForResult(intent, 2);
            }
        });

        recyclerView = rootView.findViewById(R.id.shared_recycler);
        adapter = new SharedAdapter(context, shared_list);
        adapter.setOnItemClickListener(new SharedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if(v.getId() == R.id.btn_delete){
                    String shared_category_index = shared_list.get(position).getShared_category_index();
                    String category_index = shared_list.get(position).getCategory_index();
                    delete_shared_category_to_server(container, category_index, shared_category_index);
                    get_shared_category_from_server();
                    Log.e("창욱", "어댑터 notify 완료");

                    Toast.makeText(context, "삭제버튼 클릭됨", Toast.LENGTH_SHORT).show();
                }
                else{
                Toast.makeText(context, "포지션 값: " + position, Toast.LENGTH_SHORT).show();
                String shared_category_index = shared_list.get(position).getShared_category_index();
                String category_index = shared_list.get(position).getCategory_index();
                String user_id = shared_list.get(position).getUser_id();
                String category_name = shared_list.get(position).getCategory_name();
                String num_download = shared_list.get(position).getNum_download();


                Intent intent = new Intent(context, Activity_show_shared_category.class);
                intent.putExtra("category_index", category_index);
                intent.putExtra("user_id", user_id);
                intent.putExtra("category_name", category_name);
                intent.putExtra("shared_category_index", shared_category_index);
                intent.putExtra("num_download", num_download);
                startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        searchView = rootView.findViewById(R.id.searchView);
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

//        MiniComparator comparator = new MiniComparator();
//        Collections.sort(shared_list, comparator);


        spinner = rootView.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override   // position 으로 몇번째 것이 선택됬는지 값을 넘겨준다
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(list_spinner[position].equals("다운로드 많은 순")){
                    MiniComparator comparator = new MiniComparator("내림차순");
                    Collections.sort(shared_list, comparator);
                    adapter.notifyDataSetChanged();
                }
                else if(list_spinner[position].equals("다운로드 적은 순")){
                    MiniComparator comparator = new MiniComparator("오름차순");
                    Collections.sort(shared_list, comparator);
                    adapter.notifyDataSetChanged();
                }
//                Log.e(TAG, list_spinner[position] + "선택되었습니다");

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return rootView;

    }


    /**
     * 쉐어드에 저장된 유저 index 의 category 데이터를 서버에서 가져온다.
     */
//    public void get_category_from_server(ViewGroup container, String user_index){
//        try {
//
//            GetPost_php task = new GetPost_php(container.getContext());
//            task.execute("http://192.168.254.129/mysql_android_pushData.php", "user_index", user_index);
//
//            String callBackValue = task.get();
//
//            if(callBackValue.isEmpty() || callBackValue.equals("") || callBackValue == null || callBackValue.contains("Error")) {
//                Toast.makeText(container.getContext(), "등록되지 않은 사용자이거나, 전송오류입니다.", Toast.LENGTH_SHORT).show();
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
//                        String category = jsonObject.getString("category_name");
//                        String category_index = jsonObject.getString("category_index");
//                        String num_download = jsonObject.getString("num_download");
//                        String num_word = jsonObject.getString("num_word");
//                        Log.e(TAG, "category name: " + category);
//                        addItem(category, category_index, num_download, num_word);
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

    /**
     * 유저들이 업로드한 카테고리 목록을 가져온다.
     */
    public void get_shared_category_from_server(){
        shared_list = new ArrayList<>();
        URLConnector login_task = new URLConnector("http://192.168.254.129/mysql_android_share_category.php");

        login_task.start();

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
                Shared_item item = new Shared_item();

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String category_index = jsonObject.getString("category_index");
                String shared_category_index = jsonObject.getString("shared_category_index");
                String category_name = jsonObject.getString("category_name");
                String user_id = jsonObject.getString("user_id");
                String num_download = jsonObject.getString("num_download");
                String num_word = jsonObject.getString("num_word");

                item.setCategory_index(category_index);
                item.setShared_category_index(shared_category_index);
                item.setCategory_name(category_name);
                item.setNum_download(num_download);
                item.setUser_id(user_id);
                item.setNum_word(num_word);

                shared_list.add(item);

                Log.e(TAG, "shared_category_index: " + shared_category_index);
                Log.e(TAG, "category_name: " + category_name);
                Log.e(TAG, "user_id: " + user_id);
                Log.e(TAG, "num_download: " + num_download);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("창욱", "공유 카테고리 목록 가져오기 완료");
    }

    /**
     * db에 저장돼 업로드 된 카테고리를 삭제시킨다.
     */
    public void delete_shared_category_to_server(ViewGroup container, String category_index, String shared_category_index) {
        GetPost_php task = new GetPost_php(container.getContext());
        task.execute("http://192.168.254.129/mysql_android_share_category.php", "category_index", category_index, "shared_category_index", shared_category_index, "what", "delete");
    }

    private void refresh_fragment(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 2) {
            Log.e(TAG, "onActivityResult");
            get_shared_category_from_server();
            refresh_fragment();
        }
    }

    /**
     * 객체가 담긴 리스트를 정렬해주는 class
     */
    class MiniComparator implements Comparator<Shared_item>{
        String what;
        public MiniComparator(String what) {
            this.what = what;
        }

        @Override
        public int compare(Shared_item first_item, Shared_item second_item) {
            int firstValue = Integer.parseInt(first_item.getNum_download());
            int secondValue = Integer.parseInt(second_item.getNum_download());

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
