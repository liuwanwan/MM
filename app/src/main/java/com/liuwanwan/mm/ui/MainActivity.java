package com.liuwanwan.mm.ui;

import android.app.ProgressDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.liuwanwan.mm.R;
import com.liuwanwan.mm.adapter.MeiziAdapter;
import com.liuwanwan.mm.model.Meizi;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String URL_PIC = "http://m.mm131.com/";
    private String URL_P = "http://m.mzitu.com/mm/";
    private List<Meizi> meiziList = new ArrayList<>();
    private MeiziAdapter adapter = null;
    private SwipeRefreshLayout swipeRefresh = null;
    private ProgressDialog progressDialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        initMeizi();
        initRefresh();
    }

    private void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {//Load more
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void initRefresh() {
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMeizi();
            }
        });
    }

    private void refreshMeizi() {
        initMeizi();
        adapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }

    private void initMeizi() {
        progressDialog = ProgressDialog.show(MainActivity.this, "请稍等...", "获取图片中...", true);
            new Thread(new Runnable() {
            @Override
            public void run() {
                getImages();

                runOnUiThread(new Runnable() {
                    public void run() {
                        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
                        GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 2);
                        recyclerView.setLayoutManager(layoutManager);
                        adapter = new MeiziAdapter(meiziList,0);
                        recyclerView.setAdapter(adapter);
                        //更新完列表数据，则关闭对话框
                        progressDialog.dismiss();
                    }
                });
            }
        }).start();
    }

    private void getImages() {
        meiziList.clear();
        try {
            /*Document doc=Jsoup.connect(URL_PIC).get();
			 Elements elements=doc.select("img");
			 Log.v("A","A="+elements.toString());
			 for(Element e:elements){
			 String urlPic=e.attr("data-img").toString();
			 Log.v("A","B="+urlPic);
			 String titlePic=e.attr("alt").toString();
			 Log.v("A","C="+titlePic);
			 Fruit fruit=new Fruit(titlePic,urlPic);
			 fruitList.add(fruit);
			 }*/
            Document doc = Jsoup.connect(URL_P).get();
            Elements elements = doc.select("figure");
            for (Element e : elements) {
                String urlPic = e.select("img").attr("data-original").toString();
                String titlePic = e.select("img").attr("alt").toString();
                String detaiUrl = e.select("a").attr("href").toString();
                Meizi meizi = new Meizi(titlePic, urlPic, detaiUrl);
                meiziList.add(meizi);
            }
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "can not get images!", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.backup:
                Toast.makeText(this, "You clicked Backup", Toast.LENGTH_SHORT).show();
                break;
            case R.id.delete:
                Toast.makeText(this, "You clicked Delete", Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings:
                Toast.makeText(this, "You clicked Settings", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
    }

}
