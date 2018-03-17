package com.liuwanwan.mm.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class MeiziFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragmentList = null;

    public void setFragments(ArrayList<Fragment> fragments) {
        mFragmentList = fragments;
    }

    public MeiziFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = mFragmentList.get(position);
        return fragment;
    }

    public void destroyItem(ViewGroup group, int position, Object object) {

    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
