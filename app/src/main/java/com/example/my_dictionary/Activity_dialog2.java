package com.example.my_dictionary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class Activity_dialog2 extends Activity {
    private static final String TAG= "Activity_dialog2";

    Button btn_yes, btn_no;
    String download_category_index, category_index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog2);

        btn_yes = findViewById(R.id.btn_yes);
        btn_no = findViewById(R.id.btn_no);
        Intent intent = getIntent();

        category_index = intent.getStringExtra("category_index");
        download_category_index = intent.getStringExtra("download_category_index");

        Log.e(TAG, "category_index: " + category_index);
        Log.e(TAG, "download_category_index: " + download_category_index);

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent intent = new Intent();
                intent.putExtra("result", "yes");

                reUpdate_shared_category_to_server(Activity_dialog2.this, category_index, download_category_index);

                setResult(RESULT_OK, intent);
                finish();

                // 필요 파라미터 1.download_category_index, 2.category_index
            }
        });

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("result", "no");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    /**
     * 유저가 자신의 카테고리로 해당 카테고리를 업데이트 시켜주는 메서드
     */
    public void reUpdate_shared_category_to_server(Context context, String category_index, String download_category_index) {
        GetPost_php task = new GetPost_php(context);
        task.execute("http://192.168.254.129/mysql_android_share_category.php", "category_index", category_index, "download_category_index", download_category_index, "what", "re_update");
    }
}