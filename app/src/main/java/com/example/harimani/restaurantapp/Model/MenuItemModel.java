package com.example.harimani.restaurantapp.Model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * MenuItemModel is the model class that contains the menu item's information.
 */
public class MenuItemModel {
    public String itemName;
    public String itemID;
    public String description;
    public String price;

    /**
     * Constructor method to create a MenuItemModel object from a JSON object.
     * @param jsonObject
     */
    public MenuItemModel(JSONObject jsonObject)
    {
        try {
            this.itemName = jsonObject.getString("name");
            this.itemID = jsonObject.getString("id");
            this.description = jsonObject.getString("description");
            this.price = jsonObject.getString("price");
        }

        catch (Exception ex)
        {
            Log.e("Menu Item Model", "Failed to parse JSON due to: " + ex);
        }
    }

    /**
     * Method to parse a response and get an array of MenuItemModel objects from the response
     * @param stream the InputStream object that contains the server response
     * @return an arraylist of MenuItemModel objects.
     */
    public static ArrayList<MenuItemModel> parseMenuItemsResponse(InputStream stream)
    {
        ArrayList<MenuItemModel> menuArrayList = new ArrayList<MenuItemModel>();

        try
        {
            StringBuilder builder = new StringBuilder();
            BufferedReader b_reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while((line = b_reader.readLine()) != null) {
                builder.append(line);
            }

            JSONObject jso = new JSONObject(builder.toString());
            JSONArray ja = jso.getJSONArray("data");

            for( int i = 0; i < ja.length(); i++ ) {
                MenuItemModel menuObject = new MenuItemModel(ja.getJSONObject(i));
                menuArrayList.add(menuObject);
            }
        }
        catch (Exception ex) {
            Log.e("MenuItem Model", "Failed to parse JSON due to: " + ex);
        }
        return menuArrayList;
    }
}
