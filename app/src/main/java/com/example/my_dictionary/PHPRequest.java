package com.example.my_dictionary;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 파라미터로 받는 url로 post 방식으로 데이터를 보내는 class
 */

public class PHPRequest extends Thread{
    String ID, PW, category1;

    @Override
    public void run() {
        super.run();
        push_userInfo(ID, PW, category1);
    }

    private URL url;

    /**
     * 정보를 넘겨주기 위한 php파일로 접근하는 url
     * @param url
     * 아이디 생성 시 기본적으로 저장되는 정보
     * @param ID
     * @param PW
     * @param category1
     * @throws MalformedURLException
     */
    public PHPRequest(String url, String ID, String PW, String category1) throws MalformedURLException {
        this.url = new URL(url);
        this.ID = ID;
        this.PW = PW;
        this.category1 = category1;
    }


    /**
     * StringBuilder class 공부해야함
     * @param in
     * @return
     * @throws IOException
     */
    private String readStream(InputStream in) throws IOException {
        StringBuilder jsonHtml = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line = null;

        while((line = reader.readLine()) != null)
            jsonHtml.append(line);

        reader.close();
        return jsonHtml.toString();
    }

    /**
     * 서버에 POST방식으로 데이터를 전달해주는 method
     * @param ID
     * @param PW
     * @param category1
     * @return
     */
    public String push_userInfo(final String ID, final String PW,final String category1) {
        try {
            String postData = "ID=" + ID + "&" + "PW=" + PW + "&" + "category=" + category1;
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Content-Type이란 request에 실어 보내는 데이터(body)의 type의 정보를 표현
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");   //content type 공부해야함
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);     // 만약 request body를 포함한다면, true값으로 설정해주어야 한다.
            conn.setDoInput(true);

            OutputStream outputStream = conn.getOutputStream();     //outputStream 객체에 write를 함으로써 데이터를 전송한다.
            outputStream.write(postData.getBytes("UTF-8"));

            outputStream.flush();   // 출력 스트림과 버퍼된 출력 바이트를 강제로 쓰게 한다.   -   버퍼란?
                                    //그리고 buffer가 다 차기 전에 프로그램을 종료하면 buffer에 들어있는 내용은 파일에 쓰여지지 않는다.
                                    //그 때 flush()를 호출하면 buffer의 내용이 파일에 쓰여진다.

            outputStream.close();

            String result = readStream(conn.getInputStream());
            conn.disconnect();
            Log.e("PHPRequest", "result");
            Log.e("PHPRequest", result);
            return result;
        }
        catch (Exception e) {
            Log.e("PHPRequest", "request was failed.");
            return null;
        }
    }


}
