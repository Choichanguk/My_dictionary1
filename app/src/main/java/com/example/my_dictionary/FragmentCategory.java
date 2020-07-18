package com.example.my_dictionary;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FragmentCategory extends Fragment {
    private static final String TAG= "FragmentCategory";

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private ArrayList<CategoryItem> list;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_category, container, false);
        list = new ArrayList<>();
        addItem("기본");
        addItem("영단어");
        addItem("자바 관련 용어");

        recyclerView = (RecyclerView) rootView.findViewById(R.id.category_recycle);
        adapter = new CategoryAdapter(getActivity(), list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);


        return rootView;
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
}


