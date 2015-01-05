package com.example.harimani.restaurantapp.Model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by aceldesign on 04/01/15.
 */
public class MenuItemModel {
    public String itemName;
    public String itemID;

    public MenuItemModel(JSONObject jsonObject)
    {
        try {
            this.itemName = jsonObject.getString("name");
            this.itemID = jsonObject.getString("id");
        }

        catch (Exception ex)
        {
            Log.e("Menu Item Model", "Failed to parse JSON due to: " + ex);
        }
    }

    public static ArrayList<MenuItemModel> parseMenuItemsResponse(InputStream stream)
    {
        ArrayList<MenuItemModel> boilerArrayList = new ArrayList<MenuItemModel>();

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
                MenuItemModel boilerObject = new MenuItemModel(ja.getJSONObject(i));
                boilerArrayList.add(boilerObject);
            }
        }
        catch (Exception ex) {
            Log.e("Boiler Model", "Failed to parse JSON due to: " + ex);
        }
        return boilerArrayList;
    }
}
