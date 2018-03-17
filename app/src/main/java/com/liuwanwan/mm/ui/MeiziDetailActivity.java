package com.liuwanwan.mm.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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
import com.liuwanwan.mm.model.MMInfo;
import com.liuwanwan.mm.model.Meizi;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.liuwanwan.mm.R;
import com.liuwanwan.mm.utils.ImageDownload;
import com.liuwanwan.mm.utils.OnRecyclerItemClickListener;
import com.liuwanwan.mm.utils.Permission;
import com.liuwanwan.mm.utils.SuperDialog;

public class MeiziDetailActivity extends AppCompatActivity {
    public static final String MEIZI_TITLE = "meizi_title";
    public static final String MEIZI_IMAGE = "meizi_image";
    public static final String MEIZI_IMAGE_DETAIL = "meizi_image_detail";
    private boolean connectResult = true;
    private ProgressDialog progressDialog = null;
    private MeiziAdapter adapter = null;
    private String titleOfMeizi = null;
    private String urlOfMeizi = null;
    private String detailUrlOfMeizi = null;
    private SwipeRefreshLayout swipeRefresh = null;
    private List<Meizi> detailOfMeiziList = new ArrayList<>();
    private RecyclerView recyclerView = null;
    private FloatingActionButton fab = null;
    private int cancelCollect = 0;
    private int deleteDownload = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_meizi);
        getImageData();
        initUI();
        getImageDetail();
        recyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(recyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder) {
            }

            @Override
            public void onItemLongClick(RecyclerView.ViewHolder holder) {
                int adapterPosition = holder.getAdapterPosition();//当前item的位置
                Meizi meizi = detailOfMeiziList.get(adapterPosition);
                operateImage(meizi);
            }
        });
        initRefresh();
    }

    private void operateImage(final Meizi meizi) {
        final SuperDialog superDialog = new SuperDialog(MeiziDetailActivity.this);
        final ArrayList<SuperDialog.DialogMenuItem> menuItems = new ArrayList<>();
        if (meizi.getUrlOfMeizi().startsWith("http")) {
            if (DataSupport.where("imageUrl=?", meizi.getUrlOfMeizi()).find(MMInfo.class).size() == 0)
                menuItems.add(new SuperDialog.DialogMenuItem("收藏图片", R.drawable.ic_like));
            else
                menuItems.add(new SuperDialog.DialogMenuItem("取消收藏", R.drawable.ic_like));
            menuItems.add(new SuperDialog.DialogMenuItem("下载图片", R.drawable.ic_download));
            superDialog.setContentTextSize(18);
            superDialog.setListener(new SuperDialog.onDialogClickListener() {
                @Override
                public void click(boolean isButtonClick, int position) {
                    switch (position) {
                        case 0:
                            if (menuItems.get(position).itemName.equals("收藏图片")) {
                                if (DataSupport.where("imageUrl=?", meizi.getUrlOfMeizi()).find(MMInfo.class).size() > 0) {
                                    Toast.makeText(MeiziDetailActivity.this, "该图片已收藏，不要重复收藏！", Toast.LENGTH_SHORT).show();
                                } else {
                                    MMInfo mmInfo = new MMInfo();
                                    mmInfo.setImageUrl(meizi.getUrlOfMeizi());
                                    mmInfo.setImageTitle(meizi.getTitleOfMeizi());
                                    mmInfo.setCollectState(1);
                                    mmInfo.save();
                                    Toast.makeText(MeiziDetailActivity.this, "收藏成功！", Toast.LENGTH_SHORT).show();
                                }
                            } else {//取消收藏
                                DataSupport.deleteAll(MMInfo.class, "imageUrl = ?", meizi.getUrlOfMeizi());
                                cancelCollect = 1;
                            }
                            break;
                        case 1:
                            if (checkStore(meizi.getTitleOfMeizi().replace("/", "-") + ".jpg"))//检查是否已下载
                            {//默认图片是jpg格式！！！
                                Toast.makeText(MeiziDetailActivity.this, "该图片已下载，不要重复下载！", Toast.LENGTH_SHORT).show();
                            } else {
                                ImageDownload imageDownload = new ImageDownload(MeiziDetailActivity.this);
                                String imageFileName = meizi.getTitleOfMeizi().replace("/", "-") + ".jpg";
                                imageDownload.saveImage(imageFileName, meizi.getUrlOfMeizi());
                            }
                            break;
                    }
                }
            }).setDialogMenuItemList(menuItems).show();
        } else {//本地图片
            menuItems.add(new SuperDialog.DialogMenuItem("删除图片", R.drawable.ic_delete));
            superDialog.setContentTextSize(18);
            superDialog.setListener(new SuperDialog.onDialogClickListener() {
                @Override
                public void click(boolean isButtonClick, int position) {
                    deleteFile(meizi.getUrlOfMeizi());
                }
            }).setDialogMenuItemList(menuItems).show();
        }
    }

    public boolean deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Toast.makeText(MeiziDetailActivity.this, "文件删除成功!", Toast.LENGTH_SHORT).show();
                deleteDownload = 1;
                return true;
            } else {
                Toast.makeText(MeiziDetailActivity.this, "文件删除失败!", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(MeiziDetailActivity.this, "文件删除失败!该文件不存在!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean checkStore(String title) {
        Permission.verifyReadPermissions(MeiziDetailActivity.this);
        //如果手机已插入sd卡,且app具有读写sd卡的权限
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MMStore";
            File mmFile = new File(filePath);
            File[] files = mmFile.listFiles();
            for (File file : files) {
                if (title.equals(file.getAbsolutePath().replace("/storage/emulated/0/MMStore/", ""))) {
                    return true;
                }
            }
        }
        return false;
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
                        if (!connectResult)
                            Toast.makeText(MeiziDetailActivity.this, "图片获取失败!", Toast.LENGTH_SHORT).show();
                        else {
                            adapter = new MeiziAdapter(detailOfMeiziList, 1);
                            recyclerView.setAdapter(adapter);
                        }
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
        if (isImage(detailUrlOfMeizi) || detailUrlOfMeizi.equals("collection")) {
            Meizi mei = new Meizi(titleOfMeizi, urlOfMeizi, detailUrlOfMeizi);
            detailOfMeiziList.add(mei);
        } else {
            try {
                Document doc = Jsoup.connect(detailUrlOfMeizi).get();
                Elements elements1 = doc.select("figure");
                String detailImage1 = elements1.select("img").attr("src").toString();
                Elements elements2 = doc.select("span.prev-next-page");
                String indexOfMeiziImageDetail = elements2.text().toString();
                int numOfMeiziImageDetail = Integer.parseInt(indexOfMeiziImageDetail.substring(2, indexOfMeiziImageDetail.length() - 1));
                Meizi meizi0 = new Meizi(titleOfMeizi + "0/" + numOfMeiziImageDetail + "页", urlOfMeizi, "");
                detailOfMeiziList.add(meizi0);
                Meizi meizi1 = new Meizi(titleOfMeizi + indexOfMeiziImageDetail, detailImage1, "");
                detailOfMeiziList.add(meizi1);
                for (int i = 2; i <= numOfMeiziImageDetail; i++) {
                    Document doci = Jsoup.connect(detailUrlOfMeizi + "/" + i).get();
                    Elements elements1i = doci.select("figure");
                    String detailImagei = elements1i.select("img").attr("src").toString();
                    Elements elements2i = doci.select("span.prev-next-page");
                    String indexOfMeiziImageDetaili = elements2i.text().toString();
                    Meizi meizi2 = new Meizi(titleOfMeizi + indexOfMeiziImageDetaili, detailImagei, "");
                    detailOfMeiziList.add(meizi2);
                }
            } catch (IOException e) {
                connectResult = false;
            }
        }
    }

    private boolean isImage(String url) {
        String extension = "";
        int i = url.lastIndexOf('.');
        if (i > 0) {
            extension = url.substring(i + 1);
        }
        if (extension.equals("jpg") || extension.equals("JPG") || extension.equals("png") || extension.equals("PNG") || extension.equals("jpeg") || extension.equals("JPEG")) {
            return true;
        } else
            return false;
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
        recyclerView.setLayoutManager(new LinearLayoutManager(MeiziDetailActivity.this));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addOnScrollListener(new MyRecyclerViewScrollListener());
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {//back to top
            @Override
            public void onClick(View view) {
                recyclerView.scrollToPosition(0);
                fab.setVisibility(View.INVISIBLE);
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
                returnData();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void returnData() {
        Intent intent = new Intent();
        intent.putExtra("collectState", cancelCollect);
        intent.putExtra("downloadState", deleteDownload);
        setResult(RESULT_OK, intent);
    }

    public void onBackPressed() {
        returnData();
        finish();
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
