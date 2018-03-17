package com.liuwanwan.mm.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.liuwanwan.mm.R;
import com.liuwanwan.mm.adapter.MeiziAdapter;
import com.liuwanwan.mm.model.MMInfo;
import com.liuwanwan.mm.model.MainMMList;
import com.liuwanwan.mm.model.Meizi;
import com.liuwanwan.mm.ui.MeiziDetailActivity;
import com.liuwanwan.mm.utils.OnRecyclerItemClickListener;
import com.liuwanwan.mm.utils.Permission;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MeiziFragment extends Fragment {
    private View view = null;
    private int activityIndex = 0;
    private int fragmentIndex = 0;
    private final int MAIN_ACTIVITY = 1;
    private final int COLLECTION_ACTIVITY = 2;
    private static final int LOAD_FIRST_SUCCESS = 1; // 成功
    private String mainUrlsOfMeizi[] = {"http://m.mzitu.com/xinggan/", "http://m.mzitu.com/japan/", "http://m.mzitu.com/taiwan/", "http://m.mzitu.com/mm/", "http://m.mzitu.com/hot/", "http://m.mzitu.com/best"};
    private List<Meizi> meiziList = new ArrayList<>();
    private MeiziAdapter adapter = null;
    private ProgressDialog progressDialog = null;
    private SwipeRefreshLayout swipeRefreshLayout = null;
    private boolean connectResult = true;
    private static boolean firstOpen = true;
    private RecyclerView recyclerView = null;
    private int collectState = -1;
    private int downloadState = -1;

    public static MeiziFragment newInstance(int activityIndex, int fragmentIndex) {
        MeiziFragment instance = new MeiziFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("activityIndex", activityIndex);
        bundle.putInt("fragmentIndex", fragmentIndex);
        instance.setArguments(bundle);
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activityIndex = getArguments().getInt("activityIndex");
        fragmentIndex = getArguments().getInt("fragmentIndex");
        view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        initMeizi(activityIndex, fragmentIndex);
        recyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(recyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder) {
                int adapterPosition = holder.getAdapterPosition();//当前item的位置
                Meizi meizi = meiziList.get(adapterPosition);
                Intent intent = new Intent(getContext(), MeiziDetailActivity.class);
                intent.putExtra(MeiziDetailActivity.MEIZI_TITLE, meizi.getTitleOfMeizi());
                intent.putExtra(MeiziDetailActivity.MEIZI_IMAGE, meizi.getUrlOfMeizi());
                intent.putExtra(MeiziDetailActivity.MEIZI_IMAGE_DETAIL, meizi.getDetailUrlOfMeizi());
                startActivityForResult(intent, 1);
            }

            @Override
            public void onItemLongClick(RecyclerView.ViewHolder holder) {
            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initMeizi(activityIndex, fragmentIndex);
            }
        });
        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        collectState = data.getIntExtra("collectState", 0);//得到新Activity 关闭后返回的数据
        downloadState = data.getIntExtra("downloadState", 0);
        if (activityIndex != MAIN_ACTIVITY && (collectState == 1 || downloadState == 1))//取消收藏或删除图片，且不是主页
            initMeizi(activityIndex, fragmentIndex);
    }

    private void initMeizi(final int activityIndex, final int fragmentIndex) {
        progressDialog = ProgressDialog.show(getContext(), "请稍等...", "获取图片中...", true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getImages(activityIndex, fragmentIndex);
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        if (!connectResult) {
                            Toast.makeText(getActivity(), "图片获取失败!", Toast.LENGTH_SHORT).show();
                        } else {
                            GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
                            recyclerView.setLayoutManager(layoutManager);
                            adapter = new MeiziAdapter(meiziList, 0);
                            recyclerView.setAdapter(adapter);
                        }
                        //更新完列表数据，则关闭对话框
                        progressDialog.dismiss();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void getImages(int activityIndex, int fragmentIndex) {
        meiziList.clear();
        if (firstOpen && MainMMList.firtLoad == LOAD_FIRST_SUCCESS) {//Splash页面已加载数据
            meiziList = MainMMList.mainMMList.get(fragmentIndex);
            firstOpen = false;
        } else {
            if (activityIndex == MAIN_ACTIVITY) {//主页的六大MM
                Document doc = null;
                try {
                    doc = Jsoup.connect(mainUrlsOfMeizi[fragmentIndex]).get();
                } catch (IOException e) {
                    e.printStackTrace();
                    connectResult = false;
                }
                Elements elements = doc.select("figure");
                for (Element e : elements) {
                    String urlPic = e.select("img").attr("data-original").toString();
                    String titlePic = e.select("img").attr("alt").toString();
                    String detaiUrl = e.select("a").attr("href").toString();
                    Meizi meizi = new Meizi(titlePic, urlPic, detaiUrl);
                    meiziList.add(meizi);
                    connectResult = true;
                }
            }
            if (activityIndex == COLLECTION_ACTIVITY) {//收藏和下载页
                switch (fragmentIndex) {
                    case 0://收藏
                        List<MMInfo> mmInfoList = DataSupport.findAll(MMInfo.class);
                        if (mmInfoList != null) {
                            int len = mmInfoList.size();
                            for (int i = 0; i < len; i++) {
                                MMInfo mmInfo = mmInfoList.get(i);
                                String urlPic = mmInfo.getImageUrl();
                                String titlePic = mmInfo.getImageTitle();
                                Meizi meizi = new Meizi(titlePic, urlPic, urlPic);
                                meiziList.add(meizi);
                            }
                            connectResult = true;
                        }
                        break;
                    case 1://下载
                        Permission.verifyReadPermissions(getActivity());
                        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MMStore";
                            File mmFile = new File(filePath);
                            File[] files = mmFile.listFiles();
                            for (File file : files) {
                                String absFile = file.getAbsolutePath();
                                Meizi meizi = new Meizi(absFile.replace(filePath + "/", ""), absFile, "collection");
                                meiziList.add(meizi);
                            }
                            connectResult = true;
                        }
                        break;
                }
            }
        }
    }
}
