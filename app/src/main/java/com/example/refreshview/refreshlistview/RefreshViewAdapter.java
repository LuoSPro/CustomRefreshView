package com.example.refreshview.refreshlistview;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RefreshViewAdapter extends ArrayAdapter<String> {

    private List<String> mData = new ArrayList<>();

    public RefreshViewAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        mData = objects;
    }

    static class ViewHolder{
        private TextView textView;

        ViewHolder(TextView textView) {
            this.textView = textView;
        }
    }

    public void setData(List<String> data){
        this.mData.clear();
        this.mData.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LinearLayout linearLayout;
        ViewHolder viewHolder;
        if (convertView == null){
            linearLayout = new LinearLayout(parent.getContext());
            LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setOverScrollMode(View.OVER_SCROLL_NEVER);
            TextView textView = new TextView(parent.getContext());
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
            textView.setLayoutParams(params);
            textView.setGravity(Gravity.CENTER);
            linearLayout.addView(textView);
            viewHolder = new ViewHolder(textView);
            linearLayout.setTag(viewHolder);
        }else{
            linearLayout = (LinearLayout) convertView;
            viewHolder = (ViewHolder) linearLayout.getTag();
        }

        String item = mData.get(position);
        if (item != null){
            viewHolder.textView.setText(item);
        }
        return linearLayout;
    }
}
