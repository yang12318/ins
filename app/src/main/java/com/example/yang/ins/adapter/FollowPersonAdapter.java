package com.example.yang.ins.adapter;

import android.graphics.Color;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.yang.ins.R;
import com.example.yang.ins.bean.Person;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowPersonAdapter extends BaseQuickAdapter<Person, BaseViewHolder> {
    public FollowPersonAdapter(int layoutResId, List<Person> list) {
        super(layoutResId, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, Person item) {
        helper.setText(R.id.follow_username, item.getName());
        helper.setText(R.id.follow_nickname, item.getNickname());
        Glide.with(mContext).load("http://ktchen.cn"+item.getSrc()).into((CircleImageView) helper.getView(R.id.follow_head));
        helper.addOnClickListener(R.id.follow_cancel);
        helper.addOnClickListener(R.id.follow_head);
        helper.addOnClickListener(R.id.follow_username);
        helper.addOnClickListener(R.id.follow_nickname);
        if(item.getIsFollowed()) {
            helper.setText(R.id.follow_cancel, "关注中");
            helper.setTextColor(R.id.follow_cancel, Color.BLACK);
            helper.setBackgroundRes(R.id.follow_cancel, R.drawable.buttonshape2);
        }
        else {
            helper.setText(R.id.follow_cancel, "关注");
            helper.setBackgroundRes(R.id.follow_cancel, R.drawable.buttonshape3);
            helper.setTextColor(R.id.follow_cancel, Color.WHITE);
        }
    }
}
