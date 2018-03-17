package com.liuwanwan.mm.ui;

import android.animation.Animator;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.liuwanwan.mm.R;
import com.liuwanwan.mm.model.MainMMList;
import com.liuwanwan.mm.model.Meizi;
import com.liuwanwan.mm.utils.NetState;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import yanzhikai.textpath.SyncTextPathView;
import yanzhikai.textpath.TextPathAnimatorListener;
import yanzhikai.textpath.painter.ArrowPainter;
import yanzhikai.textpath.painter.PenPainter;

public class SplashActivity extends AppCompatActivity {
    private static final int SHOW_TIME_MIN = 6000;
    private static final int FAILURE = 0; // 失败
    private static final int SUCCESS = 1; // 成功
    private static final int OFFLINE = 2; // 如果支持离线阅读，进入离线模式
    private boolean netState = false;
    private TextView versionName;
    private String mainUrlsOfMeizi[] = {"http://m.mzitu.com/xinggan/", "http://m.mzitu.com/japan/", "http://m.mzitu.com/taiwan/", "http://m.mzitu.com/mm/", "http://m.mzitu.com/hot/", "http://m.mzitu.com/best"};
    private SyncTextPathView appNameTextPathView;
    private SyncTextPathView appRemarkTextPathView;;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initUI();
        netState = getNetState();
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                int result;
                long startTime = System.currentTimeMillis();
                result = loadingCache(netState);
                MainMMList.firtLoad = result;
                long loadingTime = System.currentTimeMillis() - startTime;
                if (loadingTime < SHOW_TIME_MIN) {
                    try {//有可能这个后台操作用时很短,这样直接跳转的话,太快导致有一种闪一下的感觉,所以定义一个最短显示时间,取值800ms.
                        Thread.sleep(SHOW_TIME_MIN - loadingTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(Integer result) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                //两个参数分别表示进入的动画,退出的动画
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            ;
        }.execute(new Void[]{});
    }

    private void initUI() {
        versionName = (TextView) findViewById(R.id.version_name);
        versionName.setText("版本号: v " + getVersion());

        appNameTextPathView = findViewById(R.id.ani_app_title);
        appNameTextPathView.setTextPainter(new PenPainter());
        //设置动画播放完后填充颜色
        appNameTextPathView.setAnimatorListener(new TextPathAnimatorListener(){
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!isCancel) {
                    appNameTextPathView.showFillColorText();
                }
            }
        });
        appNameTextPathView.startAnimation(0,1);

        appRemarkTextPathView = findViewById(R.id.ani_remark);
        appRemarkTextPathView.setTextPainter(new ArrowPainter());
        appRemarkTextPathView.startAnimation(0,1);
    }

    private boolean getNetState() {
        if (NetState.getNetWorkConnectionType(this) == 0) {
            Toast.makeText(this, "当前没有网络连接，请确保你已经打开网络！", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (NetState.getNetWorkConnectionType(this) == 1)
                Toast.makeText(this, "当前WiFi连接可用", Toast.LENGTH_SHORT).show();
            if (NetState.getNetWorkConnectionType(this) == 2)
                Toast.makeText(this, "当前移动网络连接可用", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    private String getVersion() {
        //用来管理手机的APK(包管理器)
        PackageManager pm = getPackageManager();
        try {
            //得到指定APK的功能清单文件
            PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private int loadingCache(boolean netState) {
        if (netState) {
            Document doc;
            for (String url : mainUrlsOfMeizi) {
                try {
                    doc = Jsoup.connect(url).get();
                } catch (IOException e) {
                    e.printStackTrace();
                    return FAILURE;
                }
                Elements elements = doc.select("figure");
                ArrayList<Meizi> meiziList = new ArrayList<>();
                for (Element e : elements) {
                    String urlPic = e.select("img").attr("data-original").toString();
                    String titlePic = e.select("img").attr("alt").toString();
                    String detaiUrl = e.select("a").attr("href").toString();
                    Meizi meizi = new Meizi(titlePic, urlPic, detaiUrl);
                    meiziList.add(meizi);
                }
                MainMMList.mainMMList.add(meiziList);
            }
            return SUCCESS;
        }
        return OFFLINE;
    }

}
