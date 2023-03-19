package com.omar.facerecognitionapps.utils;

import android.util.Log;
import com.hihi.twiliosms.TwilioMessage;
import com.omar.facerecognitionapps.helpers.TwilioApi;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

import static com.omar.facerecognitionapps.ui.SplashActivity.ACCOUNT_SID;
import static com.omar.facerecognitionapps.ui.SplashActivity.AUTH_TOKEN;

public class TwilioService {
    public static void sendSms(String phoneNumber,String message){
        OkHttpClient client = new OkHttpClient.Builder().build();
        client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .addHeader("Authorization", Credentials.basic(ACCOUNT_SID, AUTH_TOKEN))
                                .build();
                        return chain.proceed(request);
                    }
                })
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.twilio.com")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TwilioApi twilioApi = retrofit.create(TwilioApi.class);
        Call<TwilioMessage> call = twilioApi.sendSms(
                ACCOUNT_SID,
                "+"+phoneNumber,
                "+15074073493",
                message
        );
        call.enqueue(new Callback<TwilioMessage>() {
            @Override
            public void onResponse(Call<TwilioMessage> call, Response<TwilioMessage> response) {
                Log.d("TWILIO API", "onResponse: " + response);
            }

            @Override
            public void onFailure(Call<TwilioMessage> call, Throwable t) {
                Log.d("TWILIO API", "onResponse: " + t.getMessage());

            }
        });

    }
}
