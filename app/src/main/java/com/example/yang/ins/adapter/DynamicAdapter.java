package com.example.yang.ins.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.yang.ins.HomeFragment;
import com.example.yang.ins.R;
import com.example.yang.ins.bean.Dynamic;

import java.util.List;

import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout;
import de.hdodenhof.circleimageview.CircleImageView;

/*public class DynamicAdapter extends BaseQuickAdapter<Dynamic, BaseViewHolder> {

    public DynamicAdapter(int layoutResId, @Nullable List<Dynamic> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Dynamic item) {
        Glide.with(mContext).load("http://ktchen.cn"+item.getSrc()).into((CircleImageView) helper.getView(R.id.ci_head));
        helper.setText(R.id.tv_username, item.getUsername());
        helper.setText(R.id.tv_like, item.getLikes_num()+"次赞");
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
*/