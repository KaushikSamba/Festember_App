package com.kaushiksamba.festemberapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ConfirmPage extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_page);
        setTextOnScreen();
        handleButtonClick();
    }

    private void setTextOnScreen() {
        TextView rollNumber = (TextView) findViewById(R.id.rollNumber);
        rollNumber.setText(Utilities.username);
        TextView coupon = (TextView) findViewById(R.id.coupon);
        coupon.setText(Integer.toString(Utilities.amount));
        if (Utilities.amount == 700) {
            LinearLayout genderLayout = (LinearLayout) findViewById(R.id.genderLayout);
            genderLayout.setVisibility(View.VISIBLE);
            LinearLayout sizeLayout = (LinearLayout) findViewById(R.id.sizeLayout);
            sizeLayout.setVisibility(View.VISIBLE);
            TextView gender = (TextView) findViewById(R.id.gender);
            gender.setText(Utilities.gender.toUpperCase());
            TextView shirtSize = (TextView) findViewById(R.id.shirtSize);
            shirtSize.setText(Utilities.shirtSize);
        }
    }

    private void handleButtonClick() {
        Button button = (Button) findViewById(R.id.confirm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Send the data to the server
                new myAsyncTask().execute();
            }
        });
    }

    class myAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpPost httppost = new HttpPost(Utilities.url_reg);
            JSONObject jsonObject;
            String error = null;
            try {
                List nameValuePairs = new ArrayList();
                nameValuePairs.add(new BasicNameValuePair("user_name", Utilities.username));
                nameValuePairs.add(new BasicNameValuePair("user_pass", Utilities.password));
                nameValuePairs.add(new BasicNameValuePair("user_amount", Integer.toString(Utilities.amount)));
                nameValuePairs.add(new BasicNameValuePair("user_gender", Utilities.gender));
                nameValuePairs.add(new BasicNameValuePair("user_tshirt_size", Utilities.shirtSize));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                HttpResponse response = httpclient.execute(httppost);
                httpEntity = response.getEntity();
                String s = EntityUtils.toString(httpEntity);
                try {
                    jsonObject = new JSONObject(s);
                    Utilities.status = jsonObject.getInt("auth") + 1;
                    error = jsonObject.getString("error");

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }
            return error;
        }

        @Override
        protected void onPostExecute(String error) {
            super.onPostExecute(error);
            switch (Utilities.status) {
                case 0:
                    Toast.makeText(ConfirmPage.this, error, Toast.LENGTH_SHORT).show();
                    //userText.setText("Username");
                    //passText.setText("Password");
                    break;
                case 1:
                case 2:
                    //Intent i = new Intent(ConfirmPage.this, WelcomePage.class);
                    SharedPreferences prefs = Utilities.prefs;
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("status", Utilities.status);
                    editor.apply();
                    //startActivity(i);
                    finish();
                    break;
            }
        }
    }
}
