package org.qianhu.app.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;
import org.qianhu.R;
import org.qianhu.bean.App;
import org.qianhu.utils.HttpCallbackListener;
import org.qianhu.utils.HttpUtil;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static java.sql.DriverManager.println;

/**
 * Created by fred on 2017/5/26.
 */

public class NotificationFragment extends LazyLoadFragment {
    TextView tv_get_result;
    @Override
    public void lazyLoad() {
        Log.i("notificationFragment","lazyLoad");
        tv_get_result = (TextView) findViewById(R.id.tv_get_result);
        Button btn_get = (Button) findViewById(R.id.btn_get);
        btn_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestWithHttpURLConnection();
            }
        });
    }

    private void sendRequestWithHttpURLConnection() {
       HttpUtil .sendOkHttpGETRequest("http://172.16.82.6/get_data.json", new Callback() {
           @Override
           public void onFailure(Call call, IOException e) {

           }

           @Override
           public void onResponse(Call call, Response response) throws IOException {
               parseJSONWithGson(response.body().toString());
           }
       });

    }

    private void parseJSONWithGson(String responseData) {
        Gson gson = new Gson();
        List<App> appList = gson.fromJson(responseData, new TypeToken<List<App>>(){}.getType());
        for (App app : appList){
            Log.i("gson","id"+app.getId());
            Log.i("gson","name"+app.getName());
            Log.i("gson","version"+app.getVersion());
        }
    }

    private void parseJSONWithJSONObject(String responseData) {
        try{
            JSONArray jsonArray = new JSONArray(responseData);
            for (int i = 0; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("name");
                String version = jsonObject.getString("version");
                Log.i("json",id);
                Log.i("json",name);
                Log.i("json",version);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void parseXMLWithPull(String responseData) {
        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(responseData));
            int eventType = xmlPullParser.getEventType();
            String id = "";
            String name = "";
            String version = "";
            while(eventType != XmlPullParser.END_DOCUMENT){
                String nodeName = xmlPullParser.getName();
                switch (eventType){
                    //开始解析节点
                    case XmlPullParser.START_TAG:{
                        if ("id".equals(nodeName)){
                            id = xmlPullParser.nextText();
                        } else if ("name".equals(nodeName)){
                            name = xmlPullParser.nextText();
                        } else if ("version".equals(nodeName)){
                            version = xmlPullParser.nextText();
                        }
                        break;
                    }

                    case XmlPullParser.END_TAG: {
                        if ("app".equals(nodeName)) {
                            Log.i("okhttp", "id is" + id);
                            Log.i("okhttp", "name is" + name);
                            Log.i("okhttp", "version is" + version);
                        }
                        break;
                    }
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showResponse(final String s) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_get_result.setText(s);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        Log.i("notificationFragment","getLayoutId");
        return R.layout.fragment_notification;
    }

    @Override
    public void stopLoad() {
        super.stopLoad();
        Log.i("notificationFragment","stopLoad");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("notificationFragment","DestroyView");
    }




}
