package com.example.yang.ins;
import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ajguan.library.EasyRefreshLayout;
import com.ajguan.library.LoadModel;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.yang.ins.Utils.HelloHttp;
import com.example.yang.ins.bean.Dynamic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity;
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Response;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class HomeFragment extends Fragment implements EasyPermissions.PermissionCallbacks, BGANinePhotoLayout.Delegate{

    private int last_post_id = 0;
    private List<Dynamic> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private DynamicAdapter adapter;
    private EasyRefreshLayout easyRefreshLayout;
    BGANinePhotoLayout mCurrentClickNpl;
    private static final int PRC_PHOTO_PREVIEW = 1;
    private View view;
    public static HomeFragment newInstance(String param1) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("agrs1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        //Bundle bundle = getArguments();
        adapter = new DynamicAdapter(R.layout.item_dynamic, list);
        initView();
        initData();
        adapter.setNewData(list);
        initAdapter();
        adapter.bindToRecyclerView(recyclerView);
        adapter.setEmptyView(R.layout.empty_home);
        adapter.setHeaderFooterEmpty(true, true);
        return view;
    }

    private void initView() {
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_like);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        easyRefreshLayout = (EasyRefreshLayout) view.findViewById(R.id.easylayout);
        easyRefreshLayout.setLoadMoreModel(LoadModel.NONE);
        easyRefreshLayout.addEasyEvent(new EasyRefreshLayout.EasyEvent() {
            @Override
            public void onLoadMore() {
                Map<String, Object> map = new HashMap<>();
                map.put("page", "0");
                map.put("post_id", last_post_id);
                HelloHttp.sendGetRequest("api/dynamic", map, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("FollowActivity", "FAILURE");
                        Looper.prepare();
                        Toast.makeText(getContext(), "服务器错误", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();
                        Log.d("HomeFragment", responseData);
                        try{
                            JSONObject jsonObject1 = new JSONObject(responseData);
                            String result = jsonObject1.getString("status");
                            if(result.equals("Success")) {
                                JSONArray jsonArray = jsonObject1.getJSONArray("result");
                                for(int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    final Dynamic dynamic = new Dynamic();
                                    dynamic.setUsername(jsonObject.getString("username"));
                                    dynamic.setIntroduction(jsonObject.getString("introduction"));
                                    dynamic.setPub_time(jsonObject.getString("Pub_time"));
                                    dynamic.setSrc(jsonObject.getString("profile_picture"));
                                    dynamic.setLikes_num(jsonObject.getInt("likes_num"));
                                    dynamic.setCom_num(jsonObject.getInt("com_num"));
                                    dynamic.setIs_collect(jsonObject.getBoolean("is_shoucang"));
                                    dynamic.setIs_like(jsonObject.getBoolean("is_dianzan"));
                                    int postId = jsonObject.getInt("post_id");
                                    dynamic.setId(postId);
                                    dynamic.setUserId(jsonObject.getInt("user_id"));
                                    Map<String, Object> map2 = new HashMap<>();
                                    map2.put("postid", postId);
                                    HelloHttp.sendGetRequest("api/photoList", map2, new okhttp3.Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            Log.e("DetailActivity", "FAILURE");
                                            Looper.prepare();
                                            Toast.makeText(getContext(), "服务器错误", Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            String responseData = response.body().string();
                                            Log.d("DetailActivity", responseData);
                                            try {
                                                JSONObject jsonObject1 = new JSONObject(responseData);
                                                JSONArray jsonArray = jsonObject1.getJSONArray("result");
                                                ArrayList<String> arrayList = new ArrayList<>();
                                                ArrayList<String> thumbList = new ArrayList<>();
                                                for (int i = 0; i < jsonArray.length(); i++) {
                                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                                    arrayList.add("http://ktchen.cn"+jsonObject.getString("photo"));
                                                    thumbList.add("http://ktchen.cn"+jsonObject.getString("photo_thumbnail"));
                                                }
                                                dynamic.setPhotos(arrayList);
                                                dynamic.setThumbnails(thumbList);
                                                mHandler.sendEmptyMessageDelayed(1, 0);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                String result = null;
                                                try {
                                                    result = new JSONObject(responseData).getString("status");
                                                    Looper.prepare();
                                                    Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                                                    Looper.loop();
                                                } catch (JSONException e1) {
                                                    e1.printStackTrace();
                                                }
                                            }
                                        }
                                    });
                                }
                                last_post_id = list.get(list.size()).getId();
                            }
                            else {
                                Looper.prepare();
                                Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onRefreshing() {
                initData();
                last_post_id = 0;
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
        Map<String, Object> map = new HashMap<>();
        map.put("page", "1");
        HelloHttp.sendGetRequest("api/dynamic", map, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("FollowActivity", "FAILURE");
                Looper.prepare();
                Toast.makeText(getContext(), "服务器错误", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Log.d("HomeFragment", responseData);
                try{
                    JSONObject jsonObject1 = new JSONObject(responseData);
                    String result = jsonObject1.getString("status");
                    if(result.equals("Success")) {
                        JSONArray jsonArray = jsonObject1.getJSONArray("result");
                        for(int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            final Dynamic dynamic = new Dynamic();
                            dynamic.setUsername(jsonObject.getString("username"));
                            dynamic.setIntroduction(jsonObject.getString("introduction"));
                            dynamic.setPub_time(jsonObject.getString("Pub_time"));
                            dynamic.setSrc(jsonObject.getString("profile_picture"));
                            dynamic.setLikes_num(jsonObject.getInt("likes_num"));
                            dynamic.setCom_num(jsonObject.getInt("com_num"));
                            dynamic.setIs_collect(jsonObject.getBoolean("is_shoucang"));
                            dynamic.setIs_like(jsonObject.getBoolean("is_dianzan"));
                            int postId = jsonObject.getInt("post_id");
                            dynamic.setId(postId);
                            dynamic.setUserId(jsonObject.getInt("user_id"));
                            Map<String, Object> map2 = new HashMap<>();
                            map2.put("postid", postId);
                            HelloHttp.sendGetRequest("api/photoList", map2, new okhttp3.Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.e("DetailActivity", "FAILURE");
                                    Looper.prepare();
                                    Toast.makeText(getContext(), "服务器错误", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String responseData = response.body().string();
                                    Log.d("DetailActivity", responseData);
                                    try {
                                        JSONObject jsonObject1 = new JSONObject(responseData);
                                        JSONArray jsonArray = jsonObject1.getJSONArray("result");
                                        ArrayList<String> arrayList = new ArrayList<>();
                                        ArrayList<String> thumbList = new ArrayList<>();
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            arrayList.add("http://ktchen.cn"+jsonObject.getString("photo"));
                                            thumbList.add("http://ktchen.cn"+jsonObject.getString("photo_thumbnail"));
                                        }
                                        dynamic.setPhotos(arrayList);
                                        dynamic.setThumbnails(thumbList);
                                        list.add(dynamic);
                                        mHandler.sendEmptyMessageDelayed(1, 0);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        String result = null;
                                        try {
                                            result = new JSONObject(responseData).getString("status");
                                            Looper.prepare();
                                            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                        } catch (JSONException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                }
                            });
                        }
                        Log.d("HomeFragment", Integer.toString(list.size()));
                        //last_post_id = list.get(list.size()-1).getId();
                    }
                    else {
                        Looper.prepare();
                        Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initAdapter() {
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

    @Override
    public void onClickNinePhotoItem(BGANinePhotoLayout ninePhotoLayout, View view, int position, String model, List<String> models) {
        mCurrentClickNpl = ninePhotoLayout;
        photoPreviewWrapper();
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == PRC_PHOTO_PREVIEW) {
            Toast.makeText(getContext(), "您拒绝了图片预览所需要的相关权限!", Toast.LENGTH_SHORT).show();
        }
    }

    @AfterPermissionGranted(PRC_PHOTO_PREVIEW)
    private void photoPreviewWrapper() {
        if (mCurrentClickNpl == null) {
            return;
        }
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            File downloadDir = new File(Environment.getExternalStorageDirectory(), "InsDownload");
            BGAPhotoPreviewActivity.IntentBuilder photoPreviewIntentBuilder = new BGAPhotoPreviewActivity.IntentBuilder(getActivity())
                    .saveImgDir(downloadDir); // 保存图片的目录，如果传 null，则没有保存图片功能
            if (mCurrentClickNpl.getItemCount() == 1) {
                // 预览单张图片
                photoPreviewIntentBuilder.previewPhoto(mCurrentClickNpl.getCurrentClickItem());
            } else if (mCurrentClickNpl.getItemCount() > 1) {
                // 预览多张图片
                photoPreviewIntentBuilder.previewPhotos(mCurrentClickNpl.getData())
                        .currentPosition(mCurrentClickNpl.getCurrentClickItemPosition()); // 当前预览图片的索引
            }
            startActivity(photoPreviewIntentBuilder.build());
        } else {
            EasyPermissions.requestPermissions(this, "图片预览需要以下权限:\n\n1.访问设备上的照片", PRC_PHOTO_PREVIEW, perms);
        }
    }

    class DynamicAdapter extends BaseQuickAdapter<Dynamic, BaseViewHolder> {

        public DynamicAdapter(int layoutResId, @Nullable List<Dynamic> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, Dynamic item) {
            Glide.with(mContext).load("http://ktchen.cn"+item.getSrc()).into((CircleImageView) helper.getView(R.id.ci_head));
            helper.setText(R.id.tv_username, item.getUsername());
            helper.setText(R.id.tv_like2, item.getLikes_num()+"次赞");
            if (TextUtils.isEmpty(item.getIntroduction())) {
                helper.setGone(R.id.tv_detail, false);
            }
            else {
                helper.setVisible(R.id.tv_detail, true);
                helper.setText(R.id.tv_detail, item.getIntroduction());
            }
            helper.setText(R.id.tv_comment, "查看全部"+item.getCom_num()+"条评论");
            helper.setText(R.id.tv_time, item.getPub_time());
            helper.addOnClickListener(R.id.ib_like);
            helper.addOnClickListener(R.id.ib_comment);
            helper.addOnClickListener(R.id.ib_collect);
            helper.addOnClickListener(R.id.tv_comment);
            helper.addOnClickListener(R.id.ib_menu);
            BGANinePhotoLayout ninePhotoLayout = helper.getView(R.id.npl_item_moment_photos);
            ninePhotoLayout.setDelegate(HomeFragment.this);
            ninePhotoLayout.setData(item.getThumbnails());
        }
    }
}
