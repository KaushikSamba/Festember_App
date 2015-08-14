package com.kaushiksamba.festemberapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class WelcomePage extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
        TextView welcomeText = (TextView) findViewById(R.id.welcomeText);
        welcomeText.setText(welcomeText.getText().toString() + Utilities.username);
        Button couponButton = (Button) findViewById(R.id.couponSelect);
        couponButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Coupon.class);
                startActivity(intent);
            }
        });
        if (Utilities.status == 2) {
            couponButton.setVisibility(View.INVISIBLE);
            ImageView qrCodeImage = (ImageView) findViewById(R.id.qr_code_image);
            qrCodeImage.setVisibility(View.VISIBLE);
            //qrCodeImage.setImageResource(R.drawable.festember_logo);
            Bitmap bitmap = new SaveImage(Utilities.username, null).loadFromCacheFile();
            if (bitmap == null) new myAsyncTask().execute();
            else
                qrCodeImage.setImageBitmap(bitmap);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_welcome_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            //Logout
            SharedPreferences.Editor editor = Utilities.prefs.edit();
            editor.putInt("status", 0);
            editor.putString("user_name", null);
            editor.putString("user_pass", null);
            editor.apply();
            Intent intent = new Intent(WelcomePage.this, SplashScreen.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    class myAsyncTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            HttpClient httpclient = new DefaultHttpClient();
            Bitmap image = null;
            HttpPost httppost = new HttpPost(Utilities.url_qr);
            try {
                List nameValuePairs = new ArrayList();
                nameValuePairs.add(new BasicNameValuePair("user_name", Utilities.username));
                nameValuePairs.add(new BasicNameValuePair("user_pass", Utilities.password));
                Log.d("TAG", Utilities.username + " " + Utilities.password);

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                URL url2 = new URL(Utilities.url_qr);

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity httpEntity = null;

                httpEntity = response.getEntity();
                byte[] img = EntityUtils.toByteArray(httpEntity);
                image = BitmapFactory.decodeByteArray(img, 0, img.length);

                //HttpURLConnection connection  = (HttpURLConnection) url2.openConnection();

                //InputStream is = response.getInputStream();
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                image = BitmapFactory.decodeStream(, null,options);


            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }
            return image;
        }

        @Override
        protected void onPostExecute(Bitmap bmp) {
            super.onPostExecute(bmp);
            ImageView show_image = (ImageView) findViewById(R.id.qr_code_image);
            show_image.setImageBitmap(bmp);
            SaveImage save = new SaveImage(Utilities.username, bmp);
            save.saveToCacheFile(bmp);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse(save.getCacheFilename())));
            Toast.makeText(WelcomePage.this, "Image Saved", Toast.LENGTH_SHORT).show();
        }
    }

}
