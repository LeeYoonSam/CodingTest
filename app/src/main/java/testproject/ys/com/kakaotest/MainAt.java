package testproject.ys.com.kakaotest;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import testproject.ys.com.kakaotest.adapter.GridImageAUILAdapter;
import testproject.ys.com.kakaotest.adapter.GridImageAdapter;
import testproject.ys.com.kakaotest.common.CommonAPI;
import testproject.ys.com.kakaotest.common.CommonData;
import testproject.ys.com.kakaotest.helper.NetworkHelper;

public class MainAt extends Activity implements OnScrollListener {

    final static int MAX_RETRY_DATA = 10;
    final static int PAGE_COUNT = 30;

    ProgressBar pbLoading;

    SwipeRefreshLayout mSwipeRefreshLayout;

    GridView gvImage;

    GridImageAdapter gvAdapterType1;
    GridImageAUILAdapter gvAdapterType2;

    static ArrayList<String> alImageList = new ArrayList<String>();

    int retryCount = 0;
    int page = 0;

    boolean isLoadEnd = false;

    int gridType = 1;
    int lastPosition = 999;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        lastPosition = gvImage.getFirstVisiblePosition();
        outState.putInt("lastPosition", gvImage.getFirstVisiblePosition());
        outState.putInt("page", page);
        outState.putInt("gridType", gridType);
        outState.putStringArrayList("currentList", alImageList);

        CommonData.saveDataUtil(outState);
    }

    // 메모리에서 데이터를 날렸을 경우 이 메서드를 호출해서 데이터를 복구하고 리스트를 마지막에 보고있던 포지션으로 이동한다
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Log.d("onRestoreInstanceState", "onRestoreInstanceState");

        if(savedInstanceState != null)
        {
            CommonData.restoreDataUtil(savedInstanceState);

            lastPosition = savedInstanceState.getInt("lastPosition");
            page = savedInstanceState.getInt("page");
            gridType = savedInstanceState.getInt("gridType");
            alImageList = savedInstanceState.getStringArrayList("currentList");
            Log.d("onRestoreInstanceState", "onRestore - lastPosition : " + lastPosition);
            Log.d("onRestoreInstanceState", "onRestore - page : " + page);
            Log.d("onRestoreInstanceState", "onRestore - gridType : " + gridType);
            Log.d("onRestoreInstanceState", "onRestore - alImageList size : " + alImageList.size());


            try
            {
                // jericho + picasso
                if(gridType == 1)
                {
                    gvAdapterType1 = new GridImageAdapter(getBaseContext(), alImageList);
                    gvImage.setAdapter(gvAdapterType1);
                }
                // jsoup + AUIL
                else
                {
                    gvAdapterType2 = new GridImageAUILAdapter(getBaseContext(), alImageList);
                    gvImage.setAdapter(gvAdapterType2);
                }

                // 마지막 보던 리스트로 이동
                gvImage.setSelection(lastPosition);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    // 가로/세로 전환시 그리드 형태 변경
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.d("onConfigurationChanged", "onConfigurationChanged");
        // 마지막 위치 기억
        int lastPosition = gvImage.getFirstVisiblePosition();

        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                CommonData.getDisplayInfo(MainAt.this, true);
                gvImage.setNumColumns(2);	// 세로모드 - 2개 컬럼
                break;

            case Configuration.ORIENTATION_LANDSCAPE:
                CommonData.getDisplayInfo(MainAt.this, false);
                gvImage.setNumColumns(3);	// 세로모드 - 3개 컬럼
                break;
        }

        // 그리드를 새로고침하고 마지막 위치로 다시 이동
        gvImage.invalidateViews();
        gvImage.setSelection(lastPosition);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("onCreate", "onCreate");
        setContentView(R.layout.activity_main);

        gridType = getIntent().getIntExtra("gridType", 1);

        pbLoading = (ProgressBar)findViewById(R.id.pbLoading);

        // 당겨서 새로고침
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 데이터 로드
                initData();
            }
        });

        gvImage = (GridView)findViewById(R.id.gvImage);

        //		// jericho + picasso
        //		if(gridType == 1)
        //		{
        //			gvAdapterType1 = new GridImageAdapter(getBaseContext(), alImageList);
        //			gvImage.setAdapter(gvAdapterType1);
        //		}
        //		// jsoup + AUIL
        //		else
        //		{
        //			gvAdapterType2 = new GridImageAUILAdapter(getBaseContext(), alImageList);
        //			gvImage.setAdapter(gvAdapterType2);
        //		}

        // 스크롤 리스너를 등록, onScroll에 추가구현을 해줍니다.
        gvImage.setOnScrollListener(this);

        if(savedInstanceState == null)
            // 이미 받아온 리스트 페이징으로 담기
            initData();

        //		// 데이터 받아오기
        //		getInitData();
    }

    public void initData()
    {
        page = 0;
        lastPosition = 999;

        isLoadEnd = false;
        mIsLoadingMore = false;

        getPagingData();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        setGridView();
    }

    public void setGridView()
    {
        // 현재 화면에 따라 가로/세로모드 적
        Configuration config = getResources().getConfiguration();
        if(config.orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            CommonData.getDisplayInfo(MainAt.this, true);
            gvImage.setNumColumns(2);	// 세로모드 - 2개 컬럼
        }
        else
        {
            CommonData.getDisplayInfo(MainAt.this, false);
            gvImage.setNumColumns(3);	// 가로 모드 - 3개 컬럼
        }

        // jericho + picasso
        if(gridType == 1)
        {
            gvAdapterType1 = new GridImageAdapter(getBaseContext(), alImageList);
            gvImage.setAdapter(gvAdapterType1);
        }
        // jsoup + AUIL
        else
        {
            gvAdapterType2 = new GridImageAUILAdapter(getBaseContext(), alImageList);
            gvImage.setAdapter(gvAdapterType2);
        }

        if(lastPosition != 999)
            // 마지막 보던 리스트로 이동
            gvImage.setSelection(lastPosition);
    }

    /**
     * LoadMore
     *
     */
    private boolean mIsLoadingMore = false;		// 로딩중인지 판단하는 변수
    private int mCurrentScrollState;			// 현재 상태

    @Override
    public void onScrollStateChanged(final AbsListView view, final int scrollState) {
        // 스크롤의 변화가 생기면 현재 스크롤 상태를 저장한다.
        mCurrentScrollState = scrollState;
    }

    @Override
    public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {

        if(alImageList.size() < page * PAGE_COUNT)
            return;

        // 리스트의 끝에왔는지 판단
        if (firstVisibleItem + visibleItemCount < totalItemCount)
            return;

        // totalItemCount와 비교하여 끝에 도달했는지 체크
        boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;

        // 더보기 조건에 해당하면 데이터 로드
        if (!mIsLoadingMore && loadMore && mCurrentScrollState != SCROLL_STATE_IDLE)
        {
            mIsLoadingMore = true;

            page ++;
            getPagingData();
        }
    }

    Handler handler = new Handler()
    {
        public void handleMessage(Message msg) {
            switch(msg.what)
            {
                case 0:
                    if(retryCount > MAX_RETRY_DATA)
                    {
                        Log.d("RetryCount", "RetryCount : " + retryCount);
                        Toast.makeText(MainAt.this, "네트워크 에러", Toast.LENGTH_SHORT).show();
                        pbLoading.setVisibility(View.GONE);
                        finish();
                    }
                    else
                    {
                        // 너무 빨리 끝나서 1.5초 지연
                        Handler mHandler = new Handler();
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getPagingData();
                            }
                        }, 1500);
                    }
                    break;
            }
        }
    };

    // page 단위로 데이터 가져오기
    public void getPagingData()
    {
        if(isLoadEnd)
            return;

        pbLoading.setVisibility(View.VISIBLE);

        if(CommonData._alAllImageList.size() > 0)
        {
            retryCount = 0;

            try
            {
                // page가 0이면 처음이라 생각하고 이미지리스트 클리어
                if(page == 0)
                {
                    alImageList.clear();
                }

                // 페이지수만큼 데이터 가져오기
                for(int i = page * PAGE_COUNT; i < PAGE_COUNT * (page+1); i ++)
                {
                    // 추가한 데이터가 없으면 더이상 로드할 페이지가 없다고 판단하고 더이상 진행하지 않음 / 추가된 데이터가 있을때만 adapter 새로고침
                    if(alImageList.size() == CommonData._alAllImageList.size())
                    {
                        isLoadEnd = true;
                        break;
                    }
                        

                    alImageList.add(CommonData._alAllImageList.get(i));
                }

                try
                {
                    if(gridType == 1)
                        gvAdapterType1.notifyDataSetChanged();
                    else
                        gvAdapterType2.notifyDataSetChanged();
                }
                catch (Exception e)
                {
                    e.printStackTrace();

                    // 혹시 타이밍 문제로 adapter가 세팅되지 않았을때 예외처리.
                    setGridView();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            mIsLoadingMore = false;
            mSwipeRefreshLayout.setRefreshing(false);

            pbLoading.setVisibility(View.GONE);
        }
        else
        {
            if(!NetworkHelper.isConnect(MainAt.this))
            {
                Toast.makeText(MainAt.this, "네트워크 연결상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            retryCount ++;
            getInitData();
        }
    }


    // 게티이미지갤러리에서 이미지 가져오기(HTML 파싱) - 인트로에서 데이터를 받지 못했을경우 다시 받아오는 처리
    public void getInitData()
    {
        pbLoading.setVisibility(View.VISIBLE);

        try
        {
            Thread thread = new Thread(new Runnable() {
                public void run() {

                    CommonAPI.getJsoupParsing();
                    handler.sendEmptyMessage(0);

                }
            });
            thread.start();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        // 그리드뷰 이미지 객체 리소스 초기화 (onResume에서 복구)
        CommonData.unbindDrawables(gvImage);
        System.gc();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        CommonData.unbindDrawables(gvImage);
        System.gc();
        super.onDestroy();
    }
}
