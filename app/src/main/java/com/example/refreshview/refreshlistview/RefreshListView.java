package com.example.refreshview.refreshlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ListView;
import android.widget.AbsListView;

import com.example.refreshview.R;

public class RefreshListView extends ListView implements AbsListView.OnScrollListener {
    private static final String TAG = "MyListView";
    /**
     * 下拉刷新的头部
     */
    private MyHeaderView myHeaderView;
    /**
     * 屏幕上，可见的第一个item的index
     */
    private int mFirstVisibleItem;
    /**
     * headerview内部布局，便于动态获取headerView的高度
     */
    private View mHeaderContent;
    /**
     * headerView的高度
     */
    private int mHeaderHeight;
    /**
     * 上次按下的Y轴坐标，方便推测用户意图
     */
    private float mLastY;

    private OnMyListViewListener myListViewListener;
    /**
     * ratio：比率，用于处理用户的滑动距离，使ListView的滑动距离与用户的滑动距离比为：1/1.8
     */
    private float ratio = 1.8f;

    public RefreshListView(Context context) {
        this(context, null);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initView();
        initEvent();
    }

    private void initEvent() {
        this.setOnScrollListener(this);
        /**
         * 给布局添加监听，当视图可见状态改变的时候，回调到onGlobalLayout()
         * 由于内部是将listener移除了，所以这个方法只会调用一次，
         * 目的就是根据mHeaderContent得到mHeaderHeight的高度，便于后面对headerView的状态进行识别
         */
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //动态获取headerView的高度
                Log.d(TAG, "onGlobalLayout: coming in observer ");
                if (mHeaderContent != null) {
                    mHeaderHeight = mHeaderContent.getHeight();
                }
                //移除布局的监听
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void initView() {
        myHeaderView = new MyHeaderView(getContext());
        //给ListView添加headerView
        this.addHeaderView(myHeaderView);
        mHeaderContent = myHeaderView.findViewById(R.id.ll_content);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            Log.d(TAG, "onTouchEvent: MotionEvent.ACTION_DOWN ==> mLastY ==> " + mLastY);
            if (mLastY == -1) {
                //手指按下的时候
                mLastY = ev.getRawY();
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            // 手指滑动的距离 = 滑动的y减去按下的y坐标
            float gapY = ev.getRawY() - mLastY;
            if ((gapY > 0 || myHeaderView.getVisibleHeight() > 0) && mFirstVisibleItem == 0) {
                Log.d(TAG, "onTouchEvent: MotionEvent.ACTION_MOVE == > fapY ==> " + gapY);
                Log.d(TAG, "onTouchEvent: MotionEvent.ACTION_MOVE == >  myHeaderView.getVisibleHeight() ==> " + myHeaderView.getVisibleHeight());
                Log.d(TAG, "onTouchEvent: MotionEvent.ACTION_MOVE == > mFirstVisibleItem ==> " + mFirstVisibleItem);
                updateHeaderHeight(gapY/ratio);
            }
            // 将当前的y坐标赋值给上一次
            //如果不实时更新mLastY，那么list会滑动很快
            mLastY = ev.getRawY();
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            mLastY = -1;
            Log.d(TAG, "onTouchEvent: action == MotionEvent.ACTION_UP ==> mFirstVisibleItem ==> " + mFirstVisibleItem);
            if (mFirstVisibleItem == 0) {
                Log.d(TAG, "onTouchEvent: action == MotionEvent.ACTION_UP ==> myHeaderView.getVisibleHeight() ==> " + myHeaderView.getVisibleHeight());
                Log.d(TAG, "onTouchEvent: action == MotionEvent.ACTION_UP ==> mHeaderHeight ==> " + mHeaderHeight);
                //当headerView的当前高度
                if (myHeaderView.getVisibleHeight() > mHeaderHeight) {
                    myHeaderView.setStatus(MyHeaderView.STATUS_REFRESHING);
                    if (myListViewListener != null) {
                        myListViewListener.setRefreshing();
                    }
                }
                //释放后重置HeaderView
                resetHeaderViewHeight();
            }
        }
        return super.onTouchEvent(ev);
    }

    private void resetHeaderViewHeight() {
        //当第一项不是第0个时，表示只是在上下滑动，而不是控制刷新
        if (mFirstVisibleItem == 0) {
            //如果headerView现在的状态是隐藏的，则不管
            if (myHeaderView.getVisibleHeight() <= 0) {
                return;
            }
            Log.d(TAG, "resetHeaderViewHeight: myHeaderView.getVisibleHeight() ==> " + myHeaderView.getVisibleHeight());
            Log.d(TAG, "resetHeaderViewHeight: mHeaderHeight ==> " + mHeaderHeight);
            //如果headerView的可见高度要大于当前ListView的header高度（即headerView已经超过他内容的高度），就设置headerView为他内容的高度
            if (myHeaderView.getVisibleHeight() > mHeaderHeight) {
                myHeaderView.setVisibleHeight(mHeaderHeight);
            } else {//若headerView没有完全展开，则将他当前的高度设为0（即隐藏）
                myHeaderView.setVisibleHeight(0);
            }
        }
    }

    private void updateHeaderHeight(float gapY) {
        //操作刷新
        if (mFirstVisibleItem == 0) {
            //随着滑动的改变，修改headerView的高度
            myHeaderView.setVisibleHeight((int) (myHeaderView.getVisibleHeight() + gapY));
            //根据滑动的高度，判断headerView的状态
            if (myHeaderView.getVisibleHeight() > mHeaderHeight) {
                myHeaderView.setStatus(MyHeaderView.STATUS_READY);
            } else {
                myHeaderView.setStatus(MyHeaderView.STATUS_NORMAL);
            }
        }
        //设置当前选中的条目索引
        setSelection(0);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        Log.d(TAG, "onScroll: firstVisibleItem ==> " + firstVisibleItem);
        this.mFirstVisibleItem = firstVisibleItem;
    }

    public void setOnMyListViewListener(OnMyListViewListener listener) {
        this.myListViewListener = listener;
    }

    public interface OnMyListViewListener {
        void setRefreshing();
    }

    /**
     * 当刷新结束之后，重置headerView的高度
     */
    public void setRefreshingFinish() {
        resetHeaderViewHeight();
    }

    public void setRefreshingTime() {
        myHeaderView.setHeaderTime();
    }
}
