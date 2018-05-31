package com.example.yang.ins.adapter;

import android.graphics.Color;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.yang.ins.R;
import com.example.yang.ins.bean.Person;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConcernPersonAdapter extends BaseQuickAdapter<Person, BaseViewHolder> {
    public ConcernPersonAdapter(int layoutResId, List<Person> list) {
        super(layoutResId, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, Person item) {
        helper.setText(R.id.concern_username, item.getName());
        helper.setText(R.id.concern_nickname, item.getNickname());
        Glide.with(mContext).load("http://ktchen.cn"+item.getSrc()).into((CircleImageView) helper.getView(R.id.concern_head));
        helper.addOnClickListener(R.id.concern_follow);
        helper.addOnClickListener(R.id.concern_head);
        helper.addOnClickListener(R.id.concern_nickname);
        helper.addOnClickListener(R.id.concern_username);
        if(item.getIsFollowed()) {
            helper.setText(R.id.concern_follow, "关注中");
            helper.setTextColor(R.id.concern_follow, Color.BLACK);
            helper.setBackgroundRes(R.id.concern_follow, R.drawable.buttonshape2);
        }
        else {
            helper.setText(R.id.concern_follow, "关注");
            helper.setBackgroundRes(R.id.concern_follow, R.drawable.buttonshape3);
            helper.setTextColor(R.id.concern_follow, Color.WHITE);
        }
    }
}
