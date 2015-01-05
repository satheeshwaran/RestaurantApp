package com.example.harimani.restaurantapp.adapter;

/**
 * Created by HariMani on 12/14/2014.
 */

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.harimani.restaurantapp.Model.MenuItemModel;
import com.example.harimani.restaurantapp.R;

import java.util.List;

public class CustomListAdapter extends ArrayAdapter<MenuItemModel> {

    Context context;
    int layoutResourceId;
    List<MenuItemModel> data = null;

    Integer[] imgid={
            R.drawable.item1,
            R.drawable.item2,
            R.drawable.item3,
            R.drawable.item4,
            R.drawable.item5,
            R.drawable.item6,
            R.drawable.item7,
            R.drawable.item8,
            R.drawable.item9,
            R.drawable.item10,
            R.drawable.item11,
            R.drawable.item1,
            R.drawable.item2,
            R.drawable.item3,
            R.drawable.item4
    };
    public CustomListAdapter(Context context, int layoutResourceId, List<MenuItemModel> data) {
        super(context,layoutResourceId,data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MenuItemHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.menu_list_item, parent, false);

            holder = new MenuItemHolder();
            holder.menuItemName = (TextView) row.findViewById(R.id.item);
            holder.menuItemDescription = (TextView) row.findViewById(R.id.textView1);
            holder.menuItemImage = (ImageView) row.findViewById(R.id.icon);
            row.setTag(holder);
        } else {
            holder = (MenuItemHolder) row.getTag();
        }

        MenuItemModel menuItem = data.get(position);
        holder.menuItemName.setText(menuItem.itemName);
        holder.menuItemDescription.setText("Description "+ menuItem.itemName);
        holder.menuItemImage.setImageResource(imgid[position]);

        return row;
    }

    static class MenuItemHolder {
        TextView menuItemName;
        TextView menuItemDescription;
        ImageView menuItemImage;
    }
}
