package com.liuwanwan.mm.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.liuwanwan.mm.adapter.MeiziAdapter;
import com.liuwanwan.mm.model.Meizi;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.liuwanwan.mm.R;
import com.liuwanwan.mm.utils.ScrollSpeedLinearLayoutManger;

/**
 * Created by Administrator on 2018/3/11 0011.
 */

public class MeiziDetailActivity extends AppCompatActivity {
    public static final String MEIZI_TITLE = "meizi_title";
    public static final String MEIZI_IMAGE = "meizi_image";
    public static final String MEIZI_IMAGE_DETAIL = "meizi_image_detail";
    private ProgressDialog progressDialog = null;
    private MeiziAdapter adapter = null;
    private String titleOfMeizi = null;
    private String urlOfMeizi = null;
    private String detailUrlOfMeizi = null;
    private SwipeRefreshLayout swipeRefresh = null;
    private List<Meizi> detailOfMeiziList = new ArrayList<>();
    private RecyclerView recyclerView = null;
    private FloatingActionButton fab = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_meizi);
        getImageData();
        initUI();
        getImageDetail();
        initRefresh();
    }

    private void initRefresh() {
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getImageDetail();
            }
        });
    }

    private void getImageDetail() {
        progressDialog = ProgressDialog.show(MeiziDetailActivity.this, "请稍等...", "获取图片中...", true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getImages();
                runOnUiThread(new Runnable() {
                    public void run() {

                        adapter = new MeiziAdapter(detailOfMeiziList, 1);
                        recyclerView.setAdapter(adapter);
                        //更新完列表数据，则关闭对话框
                        progressDialog.dismiss();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void getImages() {
        detailOfMeiziList.clear();

        try {
            Document doc = Jsoup.connect(detailUrlOfMeizi).get();
            Elements elements1 = doc.select("figure");
            String detailImage1 = elements1.select("img").attr("src").toString();
            Elements elements2 = doc.select("span.prev-next-page");
            String indexOfMeiziImageDetail = elements2.text().toString();
            int numOfMeiziImageDetail = Integer.parseInt(indexOfMeiziImageDetail.substring(2, indexOfMeiziImageDetail.length() - 1));
            Meizi meizi0 = new Meizi("0/" + numOfMeiziImageDetail + "页", urlOfMeizi, "");
            detailOfMeiziList.add(meizi0);
            Meizi meizi1 = new Meizi(indexOfMeiziImageDetail, detailImage1, "");
            detailOfMeiziList.add(meizi1);
            for (int i = 2; i <= numOfMeiziImageDetail; i++) {
                Document doci = Jsoup.connect(detailUrlOfMeizi + "/" + i).get();
                Elements elements1i = doci.select("figure");
                String detailImagei = elements1i.select("img").attr("src").toString();
                Elements elements2i = doci.select("span.prev-next-page");
                String indexOfMeiziImageDetaili = elements2i.text().toString();
                Meizi meizi2 = new Meizi(indexOfMeiziImageDetaili, detailImagei, "");
                detailOfMeiziList.add(meizi2);
            }
        } catch (IOException e) {
            Toast.makeText(MeiziDetailActivity.this, "can not get images!", Toast.LENGTH_SHORT).show();
        }
    }

    private void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);//返回箭头
        }
        getSupportActionBar().setTitle(titleOfMeizi);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new ScrollSpeedLinearLayoutManger(MeiziDetailActivity.this));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addOnScrollListener(new MyRecyclerViewScrollListener());
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {//Load more
            @Override
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(0);
            }
        });
        fab.setVisibility(View.INVISIBLE);
    }

    private void getImageData() {
        Intent intent = getIntent();
        titleOfMeizi = intent.getStringExtra(MEIZI_TITLE);
        urlOfMeizi = intent.getStringExtra(MEIZI_IMAGE);
        detailUrlOfMeizi = intent.getStringExtra(MEIZI_IMAGE_DETAIL);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class MyRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int firstVisibleItemPosition = manager.findFirstVisibleItemPosition();
            // 当不滚动时
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                // 判断是否滚动超过一屏
                if (firstVisibleItemPosition == 0) {
                    fab.setVisibility(View.INVISIBLE);
                } else {
                    fab.setVisibility(View.VISIBLE);
                }

            } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {//拖动中
                fab.setVisibility(View.INVISIBLE);
            }
        }
    }
}
