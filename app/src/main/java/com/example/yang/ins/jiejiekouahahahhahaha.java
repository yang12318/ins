package com.example.yang.ins;

import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.yang.ins.Utils.HelloHttp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class jiejiekouahahahhahaha {
    private String TAG;
    private void like() {
        //改pk(动态id)
        int pk = 0;
        Map<String, Object> map = new HashMap<>();
        map.put("pk", pk);
        HelloHttp.sendPostRequest("api/post/dianzan", map, new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "FAILURE");
                Looper.prepare();
                //Toast.makeText(, "服务器未响应", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Log.d(TAG, responseData);
                String result = null;
                try {
                    result = new JSONObject(responseData).getString("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(result.equals("Success")) {
                    Looper.prepare();
                    //Toast.makeText(,"点赞成功", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                else if(result.equals("Failure")) {
                    Looper.prepare();
                    //Toast.makeText(,"记录已存在", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                else if(result.equals("UnknownError")){
                    Looper.prepare();
                    //Toast.makeText(,"未知错误", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        });
    }

    private void deleteLike() {
        //改pk(动态id)
        int pk = 0;
        Map<String, Object> map = new HashMap<>();
        map.put("pk", pk);
        HelloHttp.sendDeleteRequest("api/post/dianzan", map, new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "FAILURE");
                Looper.prepare();
                //Toast.makeText(, "服务器未响应", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Log.d(TAG, responseData);
                String result = null;
                try {
                    result = new JSONObject(responseData).getString("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(result.equals("Success")) {
                    Looper.prepare();
                    //Toast.makeText(,"取消点赞成功", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                else if(result.equals("Failure")) {
                    Looper.prepare();
                    //Toast.makeText(,"记录不存在", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                else if(result.equals("UnknownError")){
                    Looper.prepare();
                    //Toast.makeText(,"未知错误", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        });
    }
}
