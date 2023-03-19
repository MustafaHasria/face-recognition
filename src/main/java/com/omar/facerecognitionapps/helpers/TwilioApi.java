package com.omar.facerecognitionapps.helpers;

import com.hihi.twiliosms.TwilioMessage;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TwilioApi {
    @FormUrlEncoded
    @POST("/2010-04-01/Accounts/{accountSid}/Messages.json")
    Call<TwilioMessage> sendSms(
            @Path("accountSid") String accountSid,
            @Field("To") String to,
            @Field("From") String from,
            @Field("Body") String body
    );
}