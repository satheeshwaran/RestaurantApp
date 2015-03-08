package com.example.harimani.restaurantapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.harimani.restaurantapp.Model.MenuItemModel;

/**
 * ShopLocationPickActivity is the activity that shows the list of stores to be picked from.
 */
public class ShopLocationPickActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    /**
     * The listview that shows the list of shop locations.
     */
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_location_pick);

        listView = (ListView) findViewById(R.id.shopListView);
        // Defined Array values to show in ListView
        String[] values = new String[] { "Guindy",
                "Adayar",
                "Velachery",
                "Pallavaram",
                "Medavakkam",
                "Parrys",
                "Thiruneermalai",
                "Sholinganallur"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);


        // Assign adapter to ListView
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);

    }
    public void onItemClick(AdapterView adapterview, View view, int i, long l)
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shop_location_pick, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.send_feedback) {
            //launch new mail activity
            /* Create the Intent */
            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

/* Fill it with Data */
            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"feedback@restarurantapp.com"});
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Feedback about the Restaurant App");
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "***Please Enter Your Feedback Below***");

/* Send it off to the Activity-Chooser */
            this.startActivity(Intent.createChooser(emailIntent, "Send feedback..."));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
