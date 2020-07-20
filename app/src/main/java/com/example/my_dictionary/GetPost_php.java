package com.example.my_dictionary;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetPost_php extends AsyncTask<String, Void, String> {
    private static final String TAG = "InsertTask";
    ProgressDialog progressDialog;

    public Context mContext;

    public GetPost_php(Context mContext) {
        super();

        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

//        progressDialog = ProgressDialog.show(mContext, mContext.getString(R.string.message_progress), null, true, true);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

//        progressDialog.dismiss();
        //mTextViewResult.setText(result);
        Log.d(TAG, "POST response  - " + result);
    }


    @Override
    protected String doInBackground(String... params) {

        // POST 방식으로 데이터 전달시에는 데이터가 주소에 직접 입력되지 않습니다.
        String serverURL = (String) params[0];

        int i = params.length;
        Log.e("params length: ", String.valueOf(i));
        // TODO : 아래 형식처럼 원하는 key과 value를 계속 추가시킬수있다.
        // 1. PHP 파일을 실행시킬 수 있는 주소와 전송할 데이터를 준비합니다.

        String col_name1 = (String) params[1];
        String value1 = (String) params[2];
        String postParameters;


        if(params.length == 3){
            postParameters = col_name1 + "=" + value1;
            Log.e("postParameters", postParameters);
        }


        else{
            String col_name2 = (String) params[3];
            String value2 = (String) params[4];
            String col_name3 = (String) params[5];
            String value3 = (String) params[6];
            postParameters = col_name1 + "=" + value1 + "&" + col_name2 + "=" + value2 + "&" + col_name3 + "=" + value3;
            Log.e("postParameters", postParameters);
        }

        // HTTP 메시지 본문에 포함되어 전송되기 때문에 따로 데이터를 준비해야 합니다.
        // 전송할 데이터는 “이름=값” 형식이며 여러 개를 보내야 할 경우에는 항목 사이에 &를 추가합니다.
        // 여기에 적어준 이름을 나중에 PHP에서 사용하여 값을 얻게 됩니다.

        // TODO : 위에 추가한 형식처럼 아래 postParameters에 key과 value를 계속 추가시키면 끝이다.
        // ex : String postParameters = "name=" + name + "&country=" + country;


        try {
            // 2. HttpURLConnection 클래스를 사용하여 POST 방식으로 데이터를 전송합니다.
            URL url = new URL(serverURL); // 주소가 저장된 변수를 이곳에 입력합니다.

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setReadTimeout(5000); //5초안에 응답이 오지 않으면 예외가 발생합니다.

            httpURLConnection.setConnectTimeout(5000); //5초안에 연결이 안되면 예외가 발생합니다.

            httpURLConnection.setRequestMethod("POST"); //요청 방식을 POST로 합니다.
            httpURLConnection.connect();

            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(postParameters.getBytes("UTF-8")); //전송할 데이터가 저장된 변수를 이곳에 입력합니다.

            outputStream.flush();
            outputStream.close();


            // 응답을 읽습니다.

            int responseStatusCode = httpURLConnection.getResponseCode();
            Log.d(TAG, "POST response code - " + responseStatusCode);

            InputStream inputStream;
            if (responseStatusCode == HttpURLConnection.HTTP_OK) {

                // 정상적인 응답 데이터
                inputStream = httpURLConnection.getInputStream();
            } else {

                // 에러 발생

                inputStream = httpURLConnection.getErrorStream();
            }

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }


            bufferedReader.close();


            return sb.toString();


        } catch (Exception e) {

            Log.d(TAG, "InsertData: Error ", e);

            return new String("Error: " + e.getMessage());
        }

    }
}
