package com.example.my_dictionary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
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
    private Context context;
    ImageButton btn_add_category;

    URLConnector category_task;
    String getData_url = "http://192.168.254.129/mysql_android_pushData.php";  //회원정보를 가지고 있는 url
    String user_id;
    String user_index;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        context = container.getContext();
        SharedClass sharedClass = new SharedClass();
        user_id = sharedClass.loadUserId(context);
        user_index = sharedClass.loadUserIndex(context);
        list = new ArrayList<>();
//
//        Log.e(TAG, "user index: " + user_index);
//        Log.e(TAG, "user id: " + user_id);
        /**
         * 쉐어드에 저장된 유저 id의 category 데이터를 서버에서 가져온다.
         */
        get_category_from_server(container, user_index);

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_category, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.category_recycle);
        adapter = new CategoryAdapter(getActivity(), list);
        adapter.setOnItemLongClickListener(new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final View v, final View itemView, final int position) {
                if(v.getId() == R.id.btn_option){
                    final PopupMenu popup = new PopupMenu(context, v);
                    popup.inflate(R.menu.menu_category);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            String category_index = list.get(position).getCategory_index();
                            Intent intent = new Intent(context, ActivityViewPager.class);
                            switch (item.getItemId()){

                                case R.id.menu1:

                                    Toast.makeText(container.getContext(), "카테고리 인덱스: "+category_index, Toast.LENGTH_SHORT).show();
                                    //handle menu1 click
                                    TextView category_name = itemView.findViewById(R.id.category_name);
                                    String category = category_name.getText().toString();

                                    dialogShow(container, category, category_index);
                                    return true;
                                case R.id.menu2:
                                    //handle menu2 click
                                    list.remove(position);
                                    Toast.makeText(container.getContext(), "list size: " + list.size(), Toast.LENGTH_SHORT).show();

                                    delete_category_to_server(container, category_index, "null");
                                    refresh_fragment();
                                    return true;
                                //수동연습
                                case R.id.menu3:
                                    dialogShow2(container, category_index);

                                    return true;

                                default:
                                    return false;
                            }

                        }
                    });
                    popup.show();
                    Log.e(TAG, "position: " + position);
//                    Toast.makeText(container.getContext(), "아이템"+position +"클릭", Toast.LENGTH_SHORT).show();
                }

                else if(v.getId() == itemView.getId()){
                    String category_index = list.get(position).getCategory_index();
                    String category_name = list.get(position).getCategory_name();
//                    Toast.makeText(container.getContext(), "카테고리 인덱스: "+category_index, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, CategoryActivity.class);
                    intent.putExtra("category_index", category_index);
                    intent.putExtra("category_name", category_name);
                    startActivityForResult(intent, 3);
                }



            }
        });
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

    /**
     * 카테고리 추가 버튼을 누르면 실행되는 다이얼로그
     */
    public void dialogShow(final ViewGroup viewGroup){
        final LinearLayout linear = (LinearLayout) View.inflate(viewGroup.getContext(), R.layout.dialog_category, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(viewGroup.getContext());
        builder.setView(linear);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                EditText url = (EditText) linear.findViewById(R.id.category);
                String value = url.getText().toString();
                String list_size = String.valueOf(list.size() + 1);
                Log.e(TAG, "list_size: " + list_size);
                add_category_to_server(viewGroup, user_index, value);

                list = new ArrayList<>();
                get_category_from_server(viewGroup, user_id);
//                Log.e("list size after: ", String.valueOf(list.size()));
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

    /**
     * 카테고리 수정 버튼을 누르면 실행되는 다이얼로그
     */
    public void dialogShow(final ViewGroup viewGroup, final String edit_content, final String category_index){
        final LinearLayout linear = (LinearLayout) View.inflate(viewGroup.getContext(), R.layout.dialog_category, null);
        final EditText category_name = (EditText) linear.findViewById(R.id.category);
        category_name.setText(edit_content);
        AlertDialog.Builder builder = new AlertDialog.Builder(viewGroup.getContext());
        builder.setView(linear);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = category_name.getText().toString();

                update_category_to_server(viewGroup, category_index, value);

                list = new ArrayList<>();
                get_category_from_server(viewGroup, user_index);
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

    /**
     * 단어 연습 설정을 세팅하는 다이얼로그
     */
    public void dialogShow2(final ViewGroup viewGroup, final String category_index){
        final LinearLayout linear = (LinearLayout) View.inflate(viewGroup.getContext(), R.layout.setting_practice, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(viewGroup.getContext());
        final RadioGroup radioGroup1 = linear.findViewById(R.id.radioGroup1);
        final RadioGroup radioGroup2 = linear.findViewById(R.id.radioGroup2);
        final RadioButton auto = linear.findViewById(R.id.auto);
        final RadioButton manual = linear.findViewById(R.id.manual);
        final RadioButton word_all = linear.findViewById(R.id.all_word);
        final RadioButton word_checked_word = linear.findViewById(R.id.checked_word);
        final Spinner spinner = linear.findViewById(R.id.spinner);
//        radioGroup.check(korean.getId());

        radioGroup1.check(auto.getId());
        radioGroup2.check(word_all.getId());
        builder.setView(linear);

        manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.setVisibility(View.GONE);
            }
        });

        auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.setVisibility(View.VISIBLE);
            }
        });

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent intent = new Intent(context, ActivityViewPager.class);
                if(auto.isChecked()){
                    String time = spinner.getSelectedItem().toString();
                    intent.putExtra("category_index", category_index);
                    intent.putExtra("isAuto", true);
                    intent.putExtra("all_word", word_all.isChecked());
                    intent.putExtra("time", time);
                }
                else{
                    intent.putExtra("category_index", category_index);
                    intent.putExtra("isAuto", false);
                    intent.putExtra("all_word", word_all.isChecked());
                }
                startActivity(intent);
                dialog.dismiss();
                viewGroup.removeView(linear);

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

    public void addItem(String name, String category_index, String num_word){
        CategoryItem item = new CategoryItem();

        item.setCategory_name(name);
        item.setCategory_index(category_index);
        item.setNum_word(num_word);
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
     * 쉐어드에 저장된 유저 index 의 category 데이터를 서버에서 가져온다.
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

                    for(int i=0; i < jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String category = jsonObject.getString("category_name");
                        String category_index = jsonObject.getString("category_index");
                        String num_word = jsonObject.getString("num_word");
                        Log.e(TAG, "category name: " + category);
                        addItem(category, category_index, num_word);
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
                        String num_word = jsonObject.getString("num_word");
                        Log.e(TAG, "category name: " + category);
                        addItem(category, category_index, num_word);
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
     * db에 카테고리를 추가 시킨다.
     */
    public void add_category_to_server(ViewGroup container, String user_index ,String category_name) {
        GetPost_php task = new GetPost_php(container.getContext());
        task.execute("http://192.168.254.129/mysql_android_pushData.php", "user_index", user_index,"category_name", category_name, "what", "add");
    }

    /**
     * db에 저장돼 있는 카테고리를 업데이트 시킨다.
     */
    public void update_category_to_server(ViewGroup container, String user_index, String category_name) {
        GetPost_php task = new GetPost_php(container.getContext());
        task.execute("http://192.168.254.129/mysql_android_pushData.php", "user_index", user_index,"category_name", category_name, "what", "update");
    }

    /**
     * db에 저장돼 있는 카테고리를 삭제시킨다.
     */
    public void delete_category_to_server(ViewGroup container, String category_index, String category_name) {
        GetPost_php task = new GetPost_php(container.getContext());
        task.execute("http://192.168.254.129/mysql_android_pushData.php", "user_index", category_index, "category_name", category_name, "what", "delete");
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
////        Log.e(TAG, "onActivityResult");
//        if (resultCode == 3) {
//            Log.e(TAG, "onActivityResult");
//            Log.e(TAG, "context: ");
////            get_category_from_server(context, user_index);
////            refresh_fragment();
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e(TAG, "resultCode: " + resultCode);
        Log.e(TAG, "onActivityResult");
        refresh_fragment();
    }

    private void refresh_fragment(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();

    }

}


