package com.example.phn.szupost;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;


public class MyFragmentPagerAdapter extends FragmentPagerAdapter {//viewpager 列表适配器

    private final int PAGER_COUNT = 3;
    private ReceiveFragment receiveFragment=null;
    private SendFragment sendFragment=null;
    private HomeFragment homeFragment=null;




    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
       receiveFragment=new ReceiveFragment();
        sendFragment=new SendFragment();
        homeFragment=new HomeFragment();
    }


    @Override
    public int getCount() {
        return PAGER_COUNT;
    }

    @Override
    public Object instantiateItem(ViewGroup vg, int position) {
        return super.instantiateItem(vg, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        System.out.println("position Destory" + position);
        super.destroyItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case HomePage.PAGE_ONE:
                fragment = receiveFragment;
                break;
            case HomePage.PAGE_TWO:
                fragment = sendFragment;
                break;
            case HomePage.PAGE_THREE:
                fragment = homeFragment;
                break;
        }
        return fragment;
    }


}