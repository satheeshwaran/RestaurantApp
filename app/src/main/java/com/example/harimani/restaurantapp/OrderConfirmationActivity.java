package com.example.harimani.restaurantapp;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.nio.charset.Charset;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * OrderConfirmationActivity is the activity to handle to the user image capture and the order upload process.
 */
public class OrderConfirmationActivity extends ActionBarActivity
{
    /**
     * A string variable to store the device's GCM push token to be sent for the order, for the server to send push notifications for the order.
     */
    String device_token;
    /**
     * A string variable to store the full file path of the user's picture that was snapped by this activity.
     */
    String fullPath;
    /**
     * A string variable to store the description of the order.
     */
    String itemDescription;

    /**
     * A string variable to store the item ID of the item selected for the order.
     */
    String itemID;
    /**
     * A string variable to store the name of the item of the item selected for the order.
     */
    String itemName;

    /**
     * The loading dialog to show the progress of the order upload status.
     */
    private ProgressDialog pDialog;

    /**
     * The image view used to show the user picture.
     */
    ImageView userImageView;

    /**
     * PostPicture is an AsyncTask subclass that is used to post the order details to the server. Uploads the user picture and the order details.
     */
    private class PostPicture extends AsyncTask<Void,Void,Void>
    {
        /**
         * Boolean variable to track the status of the order.
         */
        boolean order_status;

        @Override
        protected void onPostExecute(Void s)
        {
            pDialog.dismiss();
            if (order_status)
            {
                Toast.makeText(OrderConfirmationActivity.this, "Your Order Has Been Placed Successfully, We Will Notify You When Your Order Is Ready!", Toast.LENGTH_SHORT).show();
                finish();
            }

            else
            {
                Toast.makeText(OrderConfirmationActivity.this, "Sorry We Could Not Place Your Order, Please Try Again!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pDialog = new ProgressDialog(OrderConfirmationActivity.this);
            pDialog.setMessage("Placing Your Order");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                DefaultHttpClient defaulthttpclient;
                HttpPost httppost;
                defaulthttpclient = new DefaultHttpClient();
                httppost = new HttpPost("http://52.10.213.83/api/v1/putorder");
                String s;
                JSONObject jsonobject;
                MultipartEntity multipartentity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                FileBody filebody = new FileBody(new File(fullPath), "image/jpeg");
                multipartentity.addPart("customer_name", new StringBody(String.valueOf(((EditText) findViewById(R.id.customer_name_text)).getText()), Charset.forName("UTF-8")));
                multipartentity.addPart("order_id", new StringBody(itemID, Charset.forName("UTF-8")));
                multipartentity.addPart("device_id", new StringBody(device_token, Charset.forName("UTF-8")));
                multipartentity.addPart("customer_img", filebody);
                httppost.setEntity(multipartentity);
                s = EntityUtils.toString(defaulthttpclient.execute(httppost).getEntity());
                jsonobject = new JSONObject(s);
                if (jsonobject.getString("status").equals("EMSE01")) {
                    order_status = true;
                    Log.d("Response", s);
                }
                else if (!jsonobject.getString("status").equals("EMSE11"))
                    order_status = false;
            }
            catch (Exception exception) {
                Log.e("Exception ", exception.getMessage(), exception);
                return null;
            }
            return null;
        }

    }

    /**
     * Method to return the last captured image's ID from gallery.
     * @return the integer id of the image.
     */
    private int getLastImageId()
    {
        String as[] = {
                "_id", "_data"
        };
        Cursor cursor = managedQuery(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, as, null, null, "_id DESC");
        boolean flag = cursor.moveToFirst();
        int i = 0;
        if (flag)
        {
            i = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
            fullPath = cursor.getString(cursor.getColumnIndex("_data"));
            Log.d("pff", (new StringBuilder()).append("getLastImageId: :id ").append(i).toString());
            Log.d("pff", (new StringBuilder()).append("getLastImageId: :path ").append(fullPath).toString());
        }
        return i;
    }

    protected void onActivityResult(int i, int j, Intent intent)
    {
        super.onActivityResult(i, j, intent);
        try {
            Bitmap bitmap = (Bitmap) intent.getExtras().get("data");
            userImageView.setImageBitmap(bitmap);
            userImageView.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
        }
        catch (Exception ex) {
            Toast.makeText(this, "Couldn't load photo", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.activity_order_confirmation);
        itemName = getIntent().getStringExtra("item_name");
        itemDescription = getIntent().getStringExtra("item_desc");
        itemID = getIntent().getStringExtra("item_id");
        device_token = getIntent().getStringExtra("device_token");
        ((EditText)findViewById(R.id.item_name_text)).setText(itemName);
        ((EditText)findViewById(R.id.item_description_text)).setText(itemDescription);
        userImageView = (ImageView)findViewById(R.id.userImage);
        userImageView.setOnClickListener(new android.view.View.OnClickListener() {

            public void onClick(View view)
            {
                showCamera();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_order_confirmation, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
            getLastImageId();
            (new PostPicture()).execute();
            return true;
    }

    /**
     * Method to show the camera view for the user to capture his image.
     */
    public void showCamera()
    {
        try
        {
            startActivityForResult(new Intent("android.media.action.IMAGE_CAPTURE"), 1);
            return;
        }
        catch (ActivityNotFoundException activitynotfoundexception)
        {
            Toast.makeText(this, "Whoops - your device doesn't support capturing images!", Toast.LENGTH_SHORT).show();
        }
    }
}