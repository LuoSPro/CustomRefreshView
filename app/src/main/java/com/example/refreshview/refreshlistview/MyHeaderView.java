package com.example.refreshview.refreshlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.refreshview.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MyHeaderView extends LinearLayout {

    private ImageView mHeaderArrow;
    private ProgressBar mHeadProgress;
    private TextView mHeaderHintText;
    private TextView mHeaderTime;
    private RotateAnimation mAnimationUp;
    private RotateAnimation mAnimationDown;
    private View mHeaderView;

    /**
     * 初始状态
     */
    public static final int STATUS_NORMAL = 1;
    /**
     * 准备刷新状态
     */
    public static final int STATUS_READY = 2;
    /**
     * 正在刷新状态
     */
    public static final int STATUS_REFRESHING = 3;
    /**
     * 当前状态
     */
    private int mCurStatus = STATUS_NORMAL;


    public MyHeaderView(Context context) {
        this(context,null);
    }

    public MyHeaderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyHeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initView();
        initAnimation();
        //将headerView添加到ListView去
        this.addView(mHeaderView);
    }

    private void initAnimation() {
        mAnimationDown = new RotateAnimation(0,-180, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        mAnimationDown.setDuration(500);//动画时间0.5s
        mAnimationDown.setFillAfter(true);//保持View结束时的状态

        mAnimationUp = new RotateAnimation(-180,0,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        mAnimationUp.setDuration(500);//动画时间0.5s
        mAnimationUp.setFillAfter(true);//保持View结束时的状态
    }

    private void initView() {
        mHeaderView = View.inflate(getContext(), R.layout.header_view, null);
        mHeaderArrow = mHeaderView.findViewById(R.id.iv_head_view_arrow);
        mHeadProgress = mHeaderView.findViewById(R.id.pd_head_view);
        mHeaderTime = mHeaderView.findViewById(R.id.tv_view_header_time);
        mHeaderHintText = mHeaderView.findViewById(R.id.tv_view_header_title);
    }

    /**
     * 设置当前刷新的状态
     */
    public void setStatus(int status){
        //设置的状态和当前状态相同时，直接return
        if (status == mCurStatus){
            return;
        }
        //先清除当前的状态
        if (status == STATUS_REFRESHING){
            mHeadProgress.setVisibility(View.VISIBLE);
            mHeaderArrow.setVisibility(INVISIBLE);
            //清除当前动画，为下面的动画做准备
            mHeaderArrow.clearAnimation();
        }else{
            mHeadProgress.setVisibility(View.INVISIBLE);
            mHeaderArrow.setVisibility(INVISIBLE);
        }
        //重置状态
        if (status == STATUS_NORMAL&&mCurStatus == STATUS_READY){//下拉状态
            mHeaderHintText.setText("下拉刷新...");
            mHeaderArrow.startAnimation(mAnimationDown);//设置下拉动画
        }else if(status == STATUS_READY){
            mHeaderHintText.setText("松开刷新");
            mHeaderArrow.startAnimation(mAnimationUp);
        }else if(status == STATUS_REFRESHING){
            mHeaderHintText.setText("正在刷新");
        }
        //更新当前状态
        mCurStatus = status;
    }

    public void setVisibleHeight(int height){
        LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        //TODO:this.setLayoutParams(layoutParams);的结果
        mHeaderView.setLayoutParams(layoutParams);
    }

    public int getVisibleHeight(){
        if (mHeaderView.getLayoutParams() != null){
            return mHeaderView.getLayoutParams().height;
        }
        return 0;
        //TODO:mHeaderView.height;结果
    }

    public void setHeaderTime(){
        mHeaderTime.setText("上次刷新时间    "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
    }

}
