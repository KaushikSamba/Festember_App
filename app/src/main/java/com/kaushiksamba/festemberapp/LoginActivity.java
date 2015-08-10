package com.kaushiksamba.festemberapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends ActionBarActivity {

    String rollNumber;
    String password;
    EditText rollNumberText, passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        handleButtonClick();
    }

    private void handleButtonClick()
    {
        rollNumberText = (EditText) findViewById(R.id.rollNumber);
        passwordText = (EditText) findViewById(R.id.password);
        Button button = (Button) findViewById(R.id.signInButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                rollNumber = rollNumberText.getText().toString();
                if(rollNumber.length()!=9)
                    //Toast.makeText(getBaseContext(),"Invalid roll number",Toast.LENGTH_SHORT).show();
                    rollNumberText.setError("Invalid roll number");
                else
                {
                    password = passwordText.getText().toString();
                    //Pass rollNumber and password to the server
                    new myAsyncTask().execute();
                }
            }
        });
    }

    class myAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings)
        {
            String error=null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpPost httppost = new HttpPost(Utilities.url_auth);
            JSONObject jsonObject;

            try {
                List nameValuePairs = new ArrayList();
                nameValuePairs.add(new BasicNameValuePair("user_name", rollNumber));
                nameValuePairs.add(new BasicNameValuePair("user_pass", password));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
                HttpResponse response = httpclient.execute(httppost);
                httpEntity = response.getEntity();
                String s = EntityUtils.toString(httpEntity);
                Log.e("TAG1",s);
                try {
                    jsonObject = new JSONObject(s);
                    Log.e("TAG2",s);
                    Utilities.status = jsonObject.getInt("auth");
                    error =  jsonObject.getString("error");
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
            switch (Utilities.status)
            {
                case 0:
                    Toast.makeText(LoginActivity.this,error,Toast.LENGTH_SHORT).show();
                    rollNumberText.setText("");
                    passwordText.setText("");
                    break;
                case 1:case 2:
                    Intent i = new Intent(LoginActivity.this,WelcomePage.class);
                    SharedPreferences.Editor editor = Utilities.prefs.edit();
                    editor.putInt("status",Utilities.status);
                    editor.putString("user_name", rollNumber);
                    Utilities.username=rollNumber;
                    editor.putString("user_pass",password);
                    Utilities.password = password;
                    editor.apply();
                    startActivity(i);
                    break;
            }
            finish();
        }
    }
}
