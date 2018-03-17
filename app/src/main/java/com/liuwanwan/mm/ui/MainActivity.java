package com.liuwanwan.mm.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.liuwanwan.mm.R;
import com.liuwanwan.mm.adapter.MeiziFragmentPagerAdapter;
import com.liuwanwan.mm.fragment.MeiziFragment;

import org.litepal.tablemanager.Connector;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private String meiziTags[] = {"性感", "日本", "台湾", "清纯", "热门", "推荐"};
    private int numOfTags = meiziTags.length;
    private TabLayout mTabLayout = null;
    private ViewPager mViewPager = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Connector.getDatabase();
        initUI();
        initViewPager();
    }

    private void initViewPager() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        for (int i = 0; i < numOfTags; i++) {
            fragments.add(MeiziFragment.newInstance(1, i));
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

    private void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {//Load more
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CollectionActivity.class);
                startActivity(intent);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);//实例化builder
                builder.setIcon(R.drawable.ic_settings);//设置图标
                builder.setTitle("说明");//设置标题
                builder.setMessage("图片来源于“妹子图”，网络不好时图片加载可能比较慢，首次安装使用时会请求内存读写权限，同意后应用关闭，请再次打开即可正常使用。项目放在我的Github上：https://github.com/liuwanwan/。其他的功能还没想好......");
                //创建对话框
                AlertDialog dialog = builder.create();
                dialog.setButton(DialogInterface.BUTTON_POSITIVE,"确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();//显示对话框
                break;
            default:
        }
        return true;
    }

    private long firstTime = 0;//记录用户首次点击返回键的时间

    public void onBackPressed() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            firstTime = secondTime;
        } else {
            System.exit(0);
        }
    }
    // 让菜单同时显示图标和文字
    /*@Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }*/
}
