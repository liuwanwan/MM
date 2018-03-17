package com.liuwanwan.mm.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.liuwanwan.mm.R;
import com.liuwanwan.mm.adapter.MeiziFragmentPagerAdapter;
import com.liuwanwan.mm.fragment.MeiziFragment;

import java.util.ArrayList;

public class CollectionActivity extends AppCompatActivity {
    private String meiziTags[] = {"我的收藏", "我的下载"};
    private int numOfTags = meiziTags.length;
    private TabLayout mTabLayout = null;
    private ViewPager mViewPager = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_meizi);
        initUI();
        initViewPager();
    }

    private void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);//返回箭头
        }
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
    }

    private void initViewPager() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        for (int i = 0; i < numOfTags; i++) {
            fragments.add(MeiziFragment.newInstance(2, i));
            mTabLayout.addTab(mTabLayout.newTab());
        }
        MeiziFragmentPagerAdapter myPagerAdapter = new MeiziFragmentPagerAdapter(getSupportFragmentManager());
        myPagerAdapter.setFragments(fragments);
        mViewPager.setAdapter(myPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        for (int i = 0; i < numOfTags; i++) {
            mTabLayout.getTabAt(i).setText(meiziTags[i]);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
