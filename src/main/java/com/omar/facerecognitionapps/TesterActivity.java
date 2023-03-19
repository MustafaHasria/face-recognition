package com.omar.facerecognitionapps;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.hihi.twiliosms.TwilioMessage;
import com.twilio.Twilio;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import es.dmoral.toasty.Toasty;
import okhttp3.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.omar.facerecognitionapps.ui.SplashActivity.ACCOUNT_SID;
import static com.omar.facerecognitionapps.ui.SplashActivity.AUTH_TOKEN;

public class TesterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tester);
    }

    public void testOne(View view) {

        HttpClient httpclient = new DefaultHttpClient();

        HttpPost httppost = new HttpPost("https://api.twilio.com/2010-04-01/Accounts/"+ACCOUNT_SID+"/SMS/Messages");
        String base64EncodedCredentials = "Basic " + Base64.encodeToString((ACCOUNT_SID + ":" + AUTH_TOKEN).getBytes(), Base64.NO_WRAP);

        httppost.setHeader("Authorization", base64EncodedCredentials);
        try {

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("From", "+15074073493"));
            nameValuePairs.add(new BasicNameValuePair("To", "+213542105752"));
            nameValuePairs.add(new BasicNameValuePair("Body", "Welcome to Twilio"));

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            System.out.println("Entity post is: " + EntityUtils.toString(entity));


        } catch (IOException e) {
            Toasty.error(TesterActivity.this,""+e.getMessage(),Toasty.LENGTH_LONG).show();
        }
    }



    public void testTwo(View view) {
        new SendMessageTask().execute();
    }

    private class SendMessageTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
                Message message = Message.creator(
                                new PhoneNumber("+213542105752"),
                                new PhoneNumber("+15074073493"),
                                "MESSAGE_BODY")
                        .create();

                return message.getSid();
            } catch (Exception e) {
                Log.e("Twilio Error", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String messageSid) {
            if (messageSid != null) {
                Log.d("Twilio Success", "Message sent successfully with SID: " + messageSid);
            } else {
                Log.e("Twilio Error", "Failed to send message");
            }
        }
    }

    public void testThree(View view) {
        String body = "«Your message here»";
        String from = "+15074073493";
        String to = "+213542105752";
        String base64EncodedCredentials = "Basic" + Base64.encodeToString ((ACCOUNT_SID + ":" + AUTH_TOKEN) .getBytes (), Base64.NO_WRAP);

        com.hihi.twiliosms.Twilio twilio = com.hihi.twiliosms.Twilio.create(TesterActivity.this,ACCOUNT_SID,  AUTH_TOKEN);
        TwilioMessage message = TwilioMessage.create("+213542105752" , "+15074073493" , "String body", null );
        try {
            Log.d("Twilio", "testThree1: "+twilio.send(message).call());
            Log.d("Twilio", "testThree2: "+message.getCallback());
        } catch (Exception e) {
            Log.d("Twilio", "testThree3: "+e.getMessage());
        }
        Toast.makeText(this, "finish", Toast.LENGTH_LONG).show();

    }

    public void testFour(View view) {



    }
    }


