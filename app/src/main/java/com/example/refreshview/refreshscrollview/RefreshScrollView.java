package com.example.refreshview.refreshscrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;

import com.example.refreshview.refreshlistview.MyHeaderView;

public class RefreshScrollView extends ScrollView {

    private static final String TAG = "RefreshScrollView";

    private LinearLayout mScrollerContainer;
    private MyHeaderView mRefreshHeaderView;
    private int mHeaderHeight;
    private static final float radio = 1.8f;
    private float mLastY;
    private boolean isRefreshing = false;
    private OnRefreshScrollViewListener mListener;

    public RefreshScrollView(Context context) {
        this(context, null);
    }

    public RefreshScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initView();
    }

    private void initView() {
        mRefreshHeaderView = new MyHeaderView(getContext());
        //设置HeaderView的布局参数
        LayoutParams headerLP = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mScrollerContainer = new LinearLayout(getContext());
        //添加headerView到container中去
        mScrollerContainer.addView(mRefreshHeaderView, headerLP);
        mScrollerContainer.setOrientation(LinearLayout.VERTICAL);

        this.addView(mScrollerContainer);
        //得到headerView中的content所占高度，方便后面去判断headerView的状态
        measureView(mRefreshHeaderView);
        //对mRefreshHeaderView进行测量之后，现在就能拿mRefreshHeaderView的数据了
        mHeaderHeight = mRefreshHeaderView.getMeasuredHeight();
        mRefreshHeaderView.setVisibleHeight(mRefreshHeaderView.getMeasuredHeight());
    }

    /**
     * 通知父布局，占用的宽，高
     *
     * @param view
     */
    private void measureView(View view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int width = ViewGroup.getChildMeasureSpec(0, 0, layoutParams.width);
        int height;
        int tempHeight = layoutParams.height;
        if (tempHeight > 0) {
            //由我们给出的尺寸大小和模式生成一个包含这两个信息的int变量
            height = MeasureSpec.makeMeasureSpec(tempHeight, MeasureSpec.EXACTLY);
        } else {
            height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        //按照给定的大小和模式去测量
        view.measure(width, height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            if (mLastY == -1) {
                mLastY = ev.getRawY();
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            float deltaY = ev.getRawY() - mLastY;
            mLastY = ev.getRawY();
            if (isRefreshing && deltaY < 0) {
                //向上滑动并且是刷新状态时
                updateHeader(deltaY);
            } else if (mScrollerContainer.getScrollY() == 0 && (deltaY > 0 || mRefreshHeaderView.getVisibleHeight() > mHeaderHeight)) {
                //
                updateHeader(deltaY / radio);
            }
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {//可能会受到viewpager的影响接收到action_cacel事件
            mLastY = -1;
            if (mScrollerContainer.getScrollY() == 0) {
                if (mRefreshHeaderView.getVisibleHeight() > mHeaderHeight) {
                    mRefreshHeaderView.setStatus(MyHeaderView.STATUS_REFRESHING);
                    if (mListener != null) {
                        mListener.onRefreshing();
                    }
                }
                resetHeaderView();
            }
        }
        return super.onTouchEvent(ev);
    }

    private void resetHeaderView() {
        int visibleHeight = mRefreshHeaderView.getVisibleHeight();
        Log.d(TAG, "resetHeaderView:  mScrollerContainer.getScrollY() ==> " + mScrollerContainer.getScrollY());
        //只有当scrollView滚动为0时（即scrollview滑动到顶部时或没滑动时）
        if (mScrollerContainer.getScrollY() == 0) {
            //如果headerView现在的状态是隐藏的，则不管
            if (visibleHeight <= 0) {
                return;
            }
            //如果headerView的可见高度要大于当前ListView的header高度（即headerView已经超过他内容的高度），就设置headerView为他内容的高度
            if (visibleHeight > mHeaderHeight) {
                mRefreshHeaderView.setVisibleHeight(mHeaderHeight);
            } else {//若headerView没有完全展开，则将他当前的高度设为0（即隐藏）
                mRefreshHeaderView.setVisibleHeight(0);
            }
        }
    }

    private void updateHeader(float deltaY) {
        if (mScrollerContainer.getScrollY() == 0) {
            mRefreshHeaderView.setVisibleHeight((int) (mRefreshHeaderView.getHeight() + deltaY));
            if (mRefreshHeaderView.getVisibleHeight() > mHeaderHeight) {
                mRefreshHeaderView.setStatus(MyHeaderView.STATUS_READY);
            } else {
                mRefreshHeaderView.setStatus(MyHeaderView.STATUS_NORMAL);
            }
        }
        //设置刷新后滑动的位置
        setScrollY(0);
    }

    /**
     * 报错：
     * java.lang.IllegalStateException: ScrollView can host only one direct child
     */
    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        // 因为scrollView只许添加一个子布局，如果在xml中添加子布局，那么肯定会throw
        // java.lang.IllegalStateException:ScrollView can host only one direct child
        this.removeAllViews();
        mScrollerContainer.addView(child, params);
        super.addView(mScrollerContainer, mScrollerContainer.getLayoutParams());
    }

    @Override
    public void computeScroll() {

        super.computeScroll();
    }

    public void setOnRefreshScrollViewListener(OnRefreshScrollViewListener listener) {
        this.mListener = listener;
    }

    public interface OnRefreshScrollViewListener {
        void onRefreshing();
    }

    public void setRefreshingFinish() {
        resetHeaderView();
    }

    public void setRefreshingTime() {
        mRefreshHeaderView.setHeaderTime();
    }
}
