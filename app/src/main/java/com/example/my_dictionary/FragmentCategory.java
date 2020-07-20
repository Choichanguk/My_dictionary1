package com.example.my_dictionary;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class FragmentCategory extends Fragment {
    private static final String TAG= "FragmentCategory";

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private ArrayList<CategoryItem> list;

    ImageButton btn_add_category;

    URLConnector category_task;
    String getData_url = "http://192.168.254.129/mysql_android_pushData.php";  //회원정보를 가지고 있는 url
    String user_id;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");

        SharedClass sharedClass = new SharedClass();
        user_id = sharedClass.loadUserId(container.getContext());
        list = new ArrayList<>();

        /**
         * 쉐어드에 저장된 유저 id의 category 데이터를 서버에서 가져온다.
         */
        get_category_from_server(container, user_id);

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_category, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.category_recycle);
        adapter = new CategoryAdapter(getActivity(), list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);


        btn_add_category = rootView.findViewById(R.id.btn_add_category);
        btn_add_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogShow(container);

            }

        });


        return rootView;
    }

    public void dialogShow(final ViewGroup viewGroup){
        final LinearLayout linear = (LinearLayout) View.inflate(viewGroup.getContext(), R.layout.dialog_category, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(viewGroup.getContext());
        builder.setView(linear);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                EditText url = (EditText) linear.findViewById(R.id.category);
                String value = url.getText().toString();
                String list_size = String.valueOf(list.size() + 1);

                push_category_to_server(viewGroup, user_id, value, list_size);

                list = new ArrayList<>();
                get_category_from_server(viewGroup, user_id);
                Log.e("list size after: ", String.valueOf(list.size()));
                dialog.dismiss();
                viewGroup.removeView(linear);

                refresh_fragment();
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

    public void addItem(String name){
        CategoryItem item = new CategoryItem();

        item.setCategory_name(name);

        list.add(item);
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
        adapter.notifyDataSetChanged();
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
     * 쉐어드에 저장된 유저 id의 category 데이터를 서버에서 가져온다.
     */
    public void get_category_from_server(ViewGroup container, String user_id){
        try {

            GetPost_php task = new GetPost_php(container.getContext());
            task.execute("http://192.168.254.129/mysql_android_pushData.php", "ID", user_id);

            String callBackValue = task.get();

            if(callBackValue.isEmpty() || callBackValue.equals("") || callBackValue == null || callBackValue.contains("Error")) {
                Toast.makeText(container.getContext(), "등록되지 않은 사용자이거나, 전송오류입니다.", Toast.LENGTH_SHORT).show();
            }

            else {
                Log.e(TAG, callBackValue);
                JSONArray jsonArray = null;
                try {

                    jsonArray = new JSONArray(callBackValue);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    Log.e(TAG, String.valueOf(jsonObject));

                    for(int i =1; i < 11; i++){
                        if(!jsonObject.isNull("category" + i)){
                            String category = jsonObject.getString("category" + i);
                            addItem(category);
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
    public void push_category_to_server(ViewGroup container, String user_id, String category_name, String category_num) {
        GetPost_php task = new GetPost_php(container.getContext());
        task.execute("http://192.168.254.129/mysql_android_pushData.php", "ID", user_id, "category_name", category_name, "category_num", category_num);
    }

    private void refresh_fragment(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();

    }
}


