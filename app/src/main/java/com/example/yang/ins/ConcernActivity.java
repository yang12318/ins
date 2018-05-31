package com.example.yang.ins;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ajguan.library.EasyRefreshLayout;
import com.ajguan.library.LoadModel;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.yang.ins.Utils.HelloHttp;
import com.example.yang.ins.adapter.ConcernPersonAdapter;
import com.example.yang.ins.bean.Person;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class ConcernActivity extends AppCompatActivity {
    private static int Userid = -10;
    private ImageButton ib_back;
    private List<Person> list;
    private RecyclerView recyclerView;
    private boolean flag = false;
    private ConcernPersonAdapter adapter;
    private EasyRefreshLayout easyRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concern);
        Intent intent = getIntent();
        ib_back = (ImageButton) findViewById(R.id.ib_concern_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        adapter = new ConcernPersonAdapter(R.layout.item_concern, list);
        initView();
        initData();
        adapter.setNewData(list);
        initAdapter();
        adapter.bindToRecyclerView(recyclerView);
        adapter.setEmptyView(R.layout.empty_list);
        adapter.setHeaderFooterEmpty(true, true);
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.rv_concern);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        easyRefreshLayout = (EasyRefreshLayout) findViewById(R.id.easylayout);
        easyRefreshLayout.setLoadMoreModel(LoadModel.NONE);
        easyRefreshLayout.addEasyEvent(new EasyRefreshLayout.EasyEvent() {
            @Override
            public void onLoadMore() {
                //不具备加载更多功能
            }

            @Override
            public void onRefreshing() {
                initData();
                initAdapter();
                easyRefreshLayout.loadMoreComplete(new EasyRefreshLayout.Event() {
                    @Override
                    public void complete() {
                        adapter.setNewData(list);
                        easyRefreshLayout.refreshComplete();
                    }
                }, 500);
            }
        });
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            Userid = intent.getIntExtra("user_id", -1);
        }
        if (Userid == -1) {
            MainApplication app = MainApplication.getInstance();
            Map<String, Integer> mapParam = app.mInfoMap;
            for (Map.Entry<String, Integer> item_map : mapParam.entrySet()) {
                if (item_map.getKey().equals("id")) {
                    Userid = item_map.getValue();
                }
            }
        }
        if (Userid == 0) {
            Toast.makeText(ConcernActivity.this, "全局内存中保存的信息为空", Toast.LENGTH_SHORT).show();
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("user_id", Userid);
            list = new ArrayList<>();
            HelloHttp.sendGetRequest("api/user/lookme", map, new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("ConcernActivity", "FAILURE");
                    Looper.prepare();
                    Toast.makeText(ConcernActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                    Log.d("ConcernActivity", responseData);
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(responseData);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Person person = new Person();
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            person.setId(jsonObject.getInt("user_id"));
                            person.setName(jsonObject.getString("username"));
                            person.setNickname(jsonObject.getString("nickname"));
                            person.setIsFollowed(jsonObject.getBoolean("is_guanzhu"));
                            person.setSrc(jsonObject.getString("profile_picture"));
                            list.add(person);
                        }
                        mHandler.sendEmptyMessageDelayed(1, 0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        try {
                            String result = null;
                            result = new JSONObject(responseData).getString("status");
                            Looper.prepare();
                            Toast.makeText(ConcernActivity.this, result, Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });
            adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, final int position) {
                    int id = list.get(position).getId();
                    Intent intent2 = getIntent();
                    intent2.putExtra("userId", id);
                    Map<String, Object> map2 = new HashMap<>();
                    HelloHttp.sendGetRequest("api/user/checkfollow/" + Integer.toString(id), map2, new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("ConcernActivity", "FAILURE");
                            Looper.prepare();
                            Toast.makeText(ConcernActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseData = response.body().string();
                            Log.d("ConcernActivity", responseData);
                            String result = null;
                            try {
                                result = new JSONObject(responseData).getString("status");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (result != null && result.equals("Yes")) {
                                //这个人你关注了
                                flag = true;
                                setButtonStyle(true, position);
                            } else if (result != null && result.equals("No")) {
                                //这个人你没关注
                                flag = false;
                                setButtonStyle(false, position);
                            } else if (result != null && result.equals("UnknownError")) {
                                Looper.prepare();
                                Toast.makeText(ConcernActivity.this, result, Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            } else {
                                Looper.prepare();
                                Toast.makeText(ConcernActivity.this, result, Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        }
                    });
                }
            });
        }
    }
    private void initAdapter() {

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
                if (view.getId() == R.id.concern_follow) {
                    int userId = list.get(position).getId();
                    if (!flag) {
                        //现在是没关注状态
                        Map<String, Object> map = new HashMap<>();
                        map.put("pk", userId);
                        HelloHttp.sendPostRequest("api/user/followyou", map, new okhttp3.Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.e("ConcernActivity", "FAILURE");
                                setButtonStyle(false,position);
                                Looper.prepare();
                                Toast.makeText(ConcernActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }

                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String responseData = response.body().string();
                                Log.d("UserActivity", responseData);
                                String result = null;
                                try {
                                    result = new JSONObject(responseData).getString("status");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (result != null && result.equals("Success")) {
                                    Looper.prepare();
                                    setButtonStyle(true,position);
                                    flag = true;
                                    Toast.makeText(ConcernActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                } else {
                                    setButtonStyle(false, position);
                                    if (result != null && result.equals("UnknownError")) {
                                        Looper.prepare();
                                        Toast.makeText(ConcernActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    } else if (result != null && result.equals("Failure")) {
                                        Looper.prepare();
                                        Toast.makeText(ConcernActivity.this, "错误：重复的关注请求，已取消关注", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    } else {
                                        Looper.prepare();
                                        Toast.makeText(ConcernActivity.this, result, Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                }
                            }
                        });
                    } else {
                        //现在是关注状态
                        Map<String, Object> map = new HashMap<>();
                        map.put("pk", userId);
                        setButtonStyle(true,position);
                        HelloHttp.sendDeleteRequest("api/user/followyou", map, new okhttp3.Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.e("ConcernActivity", "FAILURE");
                                setButtonStyle(true,position);
                                Looper.prepare();
                                Toast.makeText(ConcernActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }

                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String responseData = response.body().string();
                                Log.d("ConcernActivity", responseData);
                                String result = null;
                                try {
                                    result = new JSONObject(responseData).getString("status");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (result != null && result.equals("Success")) {
                                    Looper.prepare();
                                    setButtonStyle(false,position);
                                    flag = false;
                                    Toast.makeText(ConcernActivity.this, "已取消关注", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                } else {
                                    setButtonStyle(true,position);
                                    if (result != null && result.equals("UnknownError")) {
                                        Looper.prepare();
                                        Toast.makeText(ConcernActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    } else {
                                        Looper.prepare();
                                        Toast.makeText(ConcernActivity.this, result, Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                }
                            }
                        });
                    }
                }
                else if(view.getId() == R.id.concern_head || view.getId() == R.id.concern_nickname || view.getId() == R.id.concern_username) {
                    int userId = list.get(position).getId();
                    Intent intent = new Intent(ConcernActivity.this, UserActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what == 1)
            {
                adapter.setNewData(list);
            }
        }
    };
    private void setButtonStyle(final boolean flag, final int position) {
        runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                if(flag) {
                    Button follow = (Button) adapter.getViewByPosition(recyclerView, position, R.id.concern_follow);
                    //这个人你关注了
                    follow.setText("关注中");
                    follow.setTextColor(Color.BLACK);
                    follow.setBackground(getResources().getDrawable(R.drawable.buttonshape2));
                }
                else {
                    Button follow = (Button) adapter.getViewByPosition(recyclerView, position, R.id.concern_follow);
                    //这个人你没关注
                    follow.setText("关注");
                    follow.setTextColor(Color.WHITE);
                    follow.setBackground(getResources().getDrawable(R.drawable.buttonshape3));
                }
            }
        });
    }
}
