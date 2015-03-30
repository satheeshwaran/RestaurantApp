package com.example.harimani.restaurantapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.example.harimani.restaurantapp.Model.MenuItemModel;
import com.example.harimani.restaurantapp.adapter.CustomListAdapter;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * MainActivity is a ActionBarActivity subclass that shows the menu list from which the user can select his item for the order.
 */
public class MainActivity extends ActionBarActivity
        implements AdapterView.OnItemClickListener
{
    /**
     * FetchMenuItemsTask is a AsyncTask subclass the handles the menu item fetch from the web service.
     */
    private class FetchMenuItemsTask extends AsyncTask<Void,Void,Void>
    {
        public static final int NET_CONNECT_TIMEOUT_MILLIS = 15000;
        public static final int NET_READ_TIMEOUT_MILLIS = 10000;

        @Override
        protected void onPostExecute(Void result) {
            adapter = new CustomListAdapter(context, R.layout.menu_list_item, menuList);
            menuItemsListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            hideLoading();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                HttpURLConnection httpurlconnection = (HttpURLConnection)(new URL("http://52.10.213.83/api/v1/getlist")).openConnection();
                httpurlconnection.setReadTimeout(10000);
                httpurlconnection.setConnectTimeout(15000);
                httpurlconnection.setRequestMethod("GET");
                httpurlconnection.setDoInput(true);
                httpurlconnection.connect();
                java.io.InputStream inputstream = httpurlconnection.getInputStream();
                menuList = MenuItemModel.parseMenuItemsResponse(inputstream);
                httpurlconnection.disconnect();
            }
            catch (Exception exception)
            {
                System.out.println(exception.toString());
            }
            return null;
        }

    }

    /**
     * String variable to manipulate shared preferences.
     */
    private static final String PROPERTY_APP_VERSION = "appVersion";

    /**
     * String variable to manipulate shared preferences.
     */
    public static final String PROPERTY_REG_ID = "registration_id";

    /**
     * Tag variable used for logging.
     */
    static final String TAG = "GCM Demo";

    /**
     * The main activities context.
     */
    public static Context mainActivityContext;

    /**
     * The GCM sender ID obtained from Google developer console.
     */
    public static final String SENDER_ID = "657567415747";

    /**
     * The list adapter used for the menu list view
     */
    CustomListAdapter adapter;
    Context context;

    /**
     * The GoogleCloudMessaging variable used for registering for GCM push notification to obtain the push token.
     */
    GoogleCloudMessaging gcm;

    /**
     * The list view for showing the menu items.
     */
    ListView menuItemsListView;

    /**
     * The array data source that contains MenuItem objects.
     */
    ArrayList menuList;

    /**
     * The loading indicator shown during menu items fetch.
     */
    private ProgressDialog progress;

    /**
     * The registration id obtained from GCM server.
     */
    String regid;

    /**
     * Method to check for internet connectivity.
     * @return boolean value the specifies the status of the internet connection.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Method to check whether Google Play services is installed on the phone, because push notifications work only on devices with GooglePlayServices installed in them.
     * @return
     */
    private boolean checkPlayServices()
    {
        int i = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (i != 0)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(i))
            {
                GooglePlayServicesUtil.getErrorDialog(i, this, 9000).show();
            } else
            {
                Log.i("GCM Demo", "This device is not supported.");
                finish();
            }
            return false;
        } else
        {
            return true;
        }
    }

    /**
     * Method to get the applications ID from the system, to check for push token if already present.
     * @param context1 the context of the app.
     * @return integer that specifies the app version.
     */
    private static int getAppVersion(Context context1)
    {
        int i;
        try
        {
            i = context1.getPackageManager().getPackageInfo(context1.getPackageName(), 0).versionCode;
        }
        catch (PackageManager.NameNotFoundException namenotfoundexception)
        {
            throw new RuntimeException((new StringBuilder()).append("Could not get package name: ").append(namenotfoundexception).toString());
        }
        return i;
    }

    /**
     * Method to get the shared preferences variable to access push token if present already.
     * @param context1 the context of the app.
     * @return the SharedPreferences obejct.
     */
    private SharedPreferences getGcmPreferences(Context context1)
    {
        return getSharedPreferences(com.example.harimani.restaurantapp.MainActivity.class.getSimpleName(), 0);
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Method to refresh the menu items in the screen.
     */
    private void refreshItems()
    {
        if(isNetworkAvailable()) {
            showLoadingWithMessage("Fetching Menu Items");
            (new FetchMenuItemsTask()).execute();
        }
        else
        {
            //show alert for no internet connection
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setMessage("Refresh Menu Items Needs Internet Connection...");
            builder1.setCancelable(true);
            builder1.setNegativeButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }

    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                // mDisplay.append(msg + "\n");
            }
        }.execute(null, null, null);
    }

    private void sendRegistrationIdToBackend()
    {
    }

    /**
     * Method to store the registration ID in shared preferences.
     * @param context1 the context of the app.
     * @param s string variable to store in the shared preferences.
     */
    private void storeRegistrationId(Context context1, String s)
    {
        SharedPreferences sharedpreferences = getGcmPreferences(context1);
        int i = getAppVersion(context1);
        Log.i("GCM Demo", (new StringBuilder()).append("Saving regId on app version ").append(i).toString());
        android.content.SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("registration_id", s);
        editor.putInt("appVersion", i);
        editor.commit();
    }

    /**
     * Method to hide the loading indicator.
     */
    void hideLoading()
    {
        if (progress != null)
        {
            progress.hide();
        }
    }

    /**
     * Default method to be overriden for showing the activity screen.
     */
    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        context = this;
        mainActivityContext = this;
        setContentView(R.layout.activity_main);
        menuItemsListView = (ListView)findViewById(R.id.menuItemsListView);
        menuItemsListView.setAdapter(adapter);
        menuItemsListView.setOnItemClickListener(this);
        progress = new ProgressDialog(this);
        refreshItems();
        //GCM push intialization..
        if (checkPlayServices())
        {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(this);
            if (regid.isEmpty())
            {
                registerInBackground();
            }
            return;
        } else
        {
            Log.i("GCM Demo", "No valid Google Play Services APK found.");
            return;
        }
    }

    /**
     * Default method to be overriden to shown menu items.
     */
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Method to handle the menu item list view click.
     * @param adapterview the adapter view that contains the listview
     * @param view the container view
     * @param i the item index that was clicked.
     * @param l
     */
    public void onItemClick(AdapterView adapterview, View view, int i, long l)
    {
        MenuItemModel menuitemmodel = (MenuItemModel)menuList.get(i);
        Intent intent = new Intent(this, com.example.harimani.restaurantapp.OrderConfirmationActivity.class);
        intent.putExtra("item_id", menuitemmodel.itemID);
        intent.putExtra("item_name", menuitemmodel.itemName);
        intent.putExtra("item_desc", menuitemmodel.description);
        intent.putExtra("device_token", regid);
        startActivity(intent);
    }

    /**
     * Method to handle the menu item click
     * @param menuitem The menu item that was clicked.
     * @return the boolean variable to be returned by the overridden method.
     */
    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        if (menuitem.getItemId() == R.id.action_settings)
        {
            refreshItems();
            return true;
        } else
        {
            return super.onOptionsItemSelected(menuitem);
        }
    }

    /**
     * Method to show loading indicator with a string label.
     * @param s the string to be shown on the loading indicator.
     */
    void showLoadingWithMessage(String s)
    {
        progress.setMessage(s);
        progress.setProgressStyle(0);
        progress.setIndeterminate(true);
        progress.show();
    }
}