package com.sumit.recaptchademo.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.sumit.recaptchademo.util.Util;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by Sumit on 6/12/2017.
 */


public class TokenVerificationApi {

    private static final String SITE_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    public static boolean verifyReCaptchaUserToken(String tokenToVerify, String deviceIp){

        RequestBody formBody = new FormBody.Builder()
                .add("secret", Util.SECRET_KEY)
                .add("response", tokenToVerify)
                .add("remoteip", deviceIp)
                .build();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(SITE_VERIFY_URL)
                .post(formBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if(response != null){
                JsonObject responseObject = new JsonParser().parse(response.body().string()).getAsJsonObject();

                boolean isSuccess = responseObject.get("success").getAsBoolean();
                String timeStamp = responseObject.get("challenge_ts").getAsString();
                String apkPackageName = responseObject.get("apk_package_name").getAsString();

                return isSuccess;
            }
        } catch (IOException | JsonParseException | IllegalStateException e) {
            e.printStackTrace();
        }

        return false;
    }
}
