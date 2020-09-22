package com.example.refreshview.refreshscrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class RefreshScrollView extends ScrollView {
    public RefreshScrollView(Context context) {
        this(context,null);
    }

    public RefreshScrollView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RefreshScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

    }
}
