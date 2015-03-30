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

/**
 * CustomListAdapter is an ArrayAdapter subclass that acts as an adapter to show MenuItemModel objects.
 */
public class CustomListAdapter extends ArrayAdapter<MenuItemModel> {

    /**
     * The context to be used by the adapter.
     */
    Context context;

    /**
     * The id of the list view item resource
     */
    int layoutResourceId;

    /**
     * The array that contains MenuItemModel objects and used for populating the list view.
     */
    List<MenuItemModel> data = null;

    /**
     * The list of images to be used by the adapter.
     */
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

    /**
     * Custom constructor to initialize the list adapter.
     * @param context the context object to be used by the adapter.
     * @param layoutResourceId the resource id of the list view item.
     * @param data the array of data to be used.
     */
    public CustomListAdapter(Context context, int layoutResourceId, List<MenuItemModel> data) {
        super(context,layoutResourceId,data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    /**
     * Default method to be overridden to implement each list items elements.
     * */
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
            holder.menuItemPrice = (TextView) row.findViewById(R.id.priceTextView);
            holder.menuItemImage = (ImageView) row.findViewById(R.id.icon);
            row.setTag(holder);
        } else {
            holder = (MenuItemHolder) row.getTag();
        }

        MenuItemModel menuItem = data.get(position);
        holder.menuItemName.setText(menuItem.itemName);
        holder.menuItemDescription.setText("Description "+ menuItem.itemName);
        holder.menuItemPrice.setText("Price Per Item Rs. "+ (menuItem.price==null?"35.50":menuItem.price));
        holder.menuItemImage.setImageResource(imgid[position]);

        return row;
    }

    /**
     * A static class for handling the listitem view object efficiently.
     */
    static class MenuItemHolder {
        TextView menuItemName;
        TextView menuItemDescription;
        TextView menuItemPrice;
        ImageView menuItemImage;
    }
}
