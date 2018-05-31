package com.example.yang.ins;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class AboutFragment extends Fragment{
    private TabLayout mTabLayout;
//    private ViewPager mViewPager;
    private AboutFollowFragment mAboutFollowFragment;
    private AboutMeFragment mAboutMeFragment;
//    private List<String> Title;
//    private FragmentStatePagerAdapter mAdapter;
//    private List<Fragment> flist;
    public static AboutFragment newInstance(String param1) {
        AboutFragment fragment = new AboutFragment();
        Bundle args = new Bundle();
        args.putString("agrs1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    public AboutFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onResume() {
        super.onResume();
        mTabLayout.getTabAt(0).select();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        if(container.getTag()==null){
            view = inflater.inflate(R.layout.fragment_about, container, false);
            //init();
            container.setTag(view);
        }else{
            view = (View) container.getTag();
        }
//        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
//        initData();

//        mTabLayout.getTabAt(0).setText("已关注");
//        mTabLayout.getTabAt(1).setText("你");
        if(mTabLayout.getTabCount()<2) {
            mTabLayout.addTab(mTabLayout.newTab().setText("已关注"), true);
            mTabLayout.addTab(mTabLayout.newTab().setText("你"));
        }
        setDefaultFragment();//设置默认加载项
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.e("TAG", "tab position:" + tab.getPosition());
                FragmentManager fm = AboutFragment.this.getChildFragmentManager();
                //开启事务
                FragmentTransaction transaction = fm.beginTransaction();
                Intent intent;
                switch (tab.getPosition()) {
                    case 0: {
                        if (mAboutFollowFragment == null) {
                            mAboutFollowFragment = new AboutFollowFragment();
                            Bundle bundle = new Bundle();
                            bundle.putInt("id", -1);
                            mAboutFollowFragment.setArguments(bundle);
                        }
                        transaction = fm.beginTransaction();
                        transaction.replace(R.id.ll_tbabout, mAboutFollowFragment);
                        transaction.commit();
                        break;
                    }
                    case 1: {
                        if (mAboutMeFragment == null) {
                            mAboutMeFragment= new AboutMeFragment();
                            Bundle bundle = new Bundle();
                            bundle.putInt("id", -1);
                            mAboutMeFragment.setArguments(bundle);
                        }
                        transaction = fm.beginTransaction();
                        transaction.replace(R.id.ll_tbabout, mAboutMeFragment);
                        transaction.commit();
                        break;
                    }
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        Bundle bundle = getArguments();
        return view;
    }
    private void initData() {
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
//        flist=new ArrayList<Fragment>();
//        flist.add(new AboutFollowFragment());
//        flist.add(new AboutMeFragment());
//        Title = new ArrayList<>();
//        Title.add("已关注");
//        Title.add("你");
//        mTabLayout.addTab(mTabLayout.newTab().setText(Title.get(0)));
//        mTabLayout.addTab(mTabLayout.newTab().setText(Title.get(1)));
//        mAdapter = new FragmentAdapter(getChildFragmentManager(),Title,flist);
//        if(mViewPager.getAdapter() == null)
//            mViewPager.setAdapter(mAdapter);
//        mViewPager.setOffscreenPageLimit(1);
//        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
//        mTabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
//        mTabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来。
//        mTabLayout.setTabsFromPagerAdapter(mAdapter);//给Tabs设置适配器
    }
//        private void init() {
//        if(mAboutFollowFragment == null) {
//            mAboutFollowFragment=new AboutFollowFragment();
//        }
//        if(mAboutMeFragment == null) {
//            mAboutMeFragment=new AboutMeFragment();
//        }
//    }
    private void setDefaultFragment() {
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        mAboutFollowFragment= new AboutFollowFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id", -1);
        mAboutFollowFragment.setArguments(bundle);
        transaction.replace(R.id.ll_tbabout, mAboutFollowFragment);
        transaction.commit();
    }
//    public class FragmentAdapter extends FragmentStatePagerAdapter{
//        private List<Fragment> fragmentList = new ArrayList<>();
//        private List<String> list_Title = new ArrayList<>();
//         public  FragmentAdapter(FragmentManager fm, List<String> list_Title, List<Fragment> list) {
//         super(fm);
//         this.list_Title = list_Title;
//         this.fragmentList = list;
//         }
//         @Override
//         public Fragment getItem(int position) {
//             switch (position) {
//                 case 0:
//                     if (mAboutFollowFragment == null)
//                         mAboutFollowFragment = new AboutFollowFragment();
//                     return mAboutFollowFragment;
//                 case 1:
//                     if (mAboutMeFragment == null)
//                         mAboutMeFragment= new AboutMeFragment();
//                     return mAboutMeFragment;
//             }
//             AboutFollowFragment fragment = AboutFollowFragment.newInstance(list_Title.get(position));
//             AboutMeFragment fragment2 = AboutMeFragment.newInstance(list_Title.get(position));
//             return fragmentList.get(position);
//         }
//         @Override
//         public int getCount() {
//         return 2;
//         }
//         @Override
//         public int getItemPosition(Object object) {
//             return POSITION_UNCHANGED;
//         }
//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        Fragment fragment = null;
//        fragment = (Fragment) super.instantiateItem(container,position);
//        return fragment;
//    }
//
//
//    public List<Fragment> getFragments() {
//        return fragmentList;
//    }
//
//    public void setFragments(List<Fragment> fragments) {
//        fragments = fragments;
//    }
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return list_Title.get(position);
//        }
//        @Override
//        public Parcelable saveState() {
//            return null;
//        }
//    }
}