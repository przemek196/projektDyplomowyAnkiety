package com.example.projektdyplomowyankiety;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MenuDrawerAdapter extends BaseAdapter {


    List<MenuDrawerItem> mItems;
    Context mContext;

    public MenuDrawerAdapter(Context context,List<MenuDrawerItem> mItems) {
        this.mItems = mItems;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_menu_layout, null);
        }

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.img_drawer_menu_item_icon);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_drawer_menu_item_text);

        MenuDrawerItem item = mItems.get(position);

        imgIcon.setImageResource(item.getIcon());
        tvTitle.setText(item.getText());

        return convertView;
    }














}
