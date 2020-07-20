package com.example.my_dictionary;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG= "MainActivity";

    private FragmentManager fragmentManager = getSupportFragmentManager();

    private FragmentCategory fragmentCategory = new FragmentCategory();
    private FragmentSearch fragmentSearch = new FragmentSearch();
    private  FragmentFind fragmentFind = new FragmentFind();

    TextView userID_set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedClass sharedClass = new SharedClass();
        String load_id = sharedClass.loadUserId(MainActivity.this);
        userID_set = findViewById(R.id.user_greeting);
        userID_set.setText(load_id+ "님 환영합니다.");



        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragmentCategory).commitAllowingStateLoss();

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);

//        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                switch (menuItem.getItemId()) {
                    case R.id.category: {
                        transaction.replace(R.id.frameLayout, fragmentCategory).commitAllowingStateLoss();
                        break;
                    }
                    case R.id.search: {
                        transaction.replace(R.id.frameLayout, fragmentSearch).commitAllowingStateLoss();
                        break;
                    }
                    case R.id.find: {
                        transaction.replace(R.id.frameLayout, fragmentFind).commitAllowingStateLoss();
                        break;
                    }
                }

                return true;
            }
        });
    }

//    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
//
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//            FragmentTransaction transaction = fragmentManager.beginTransaction();
//            switch (menuItem.getItemId()){
//                case R.id.category:
//                    transaction.replace(R.id.frameLayout, fragmentCategory).commitAllowingStateLoss();
//                    break;
//
//                case  R.id.search:
//                    transaction.replace(R.id.frameLayout, fragmentSearch).commitAllowingStateLoss();
//                    break;
//
//                case  R.id.find:
//                    transaction.replace(R.id.frameLayout, fragmentFind).commitAllowingStateLoss();
//                    break;
//            }
//            return false;
//        }
//    }

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