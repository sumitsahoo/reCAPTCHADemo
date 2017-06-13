package com.sumit.recaptchademo.util;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.sumit.recaptchademo.MainActivity;
import com.sumit.recaptchademo.model.ReCaptchaDetails;


/**
 * Created by Sumit on 6/12/2017.
 */

public class ReCaptchaVerification implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Context context;
    private GoogleApiClient mGoogleApiClient;

    public ReCaptchaVerification(Context context) {
        this.context = context;
        initReCaptcha();
    }

    private void initReCaptcha() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(SafetyNet.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        verifyUser();
    }

    private void verifyUser() {
        SafetyNet.SafetyNetApi.verifyWithRecaptcha(mGoogleApiClient, Util.SITE_KEY)
                .setResultCallback(
                        new ResultCallback<SafetyNetApi.RecaptchaTokenResult>() {
                            @Override
                            public void onResult(SafetyNetApi.RecaptchaTokenResult result) {
                                Status status = result.getStatus();
                                if ((status != null) && status.isSuccess()) {
                                    handleSuccess(result.getTokenResult());
                                } else {
                                    handleError(status);
                                }
                            }
                        });
    }

    private void handleError(Status status) {
        // This means user is a robot lol :P

        ReCaptchaDetails reCaptchaDetails = new ReCaptchaDetails();
        reCaptchaDetails.setValid(false);
        reCaptchaDetails.setFailDetail(status);

        // Do not let activity receive the call back if it is not in visible state

        if (context != null && MainActivity.isActivityVisible)
            ((MainActivity) context).updateReCaptchaStatus(reCaptchaDetails);

    }

    private void handleSuccess(String tokenResult) {
        // This means user is a human

        ReCaptchaDetails reCaptchaDetails = new ReCaptchaDetails();
        reCaptchaDetails.setValid(true);
        reCaptchaDetails.setTokenResult(tokenResult);

        ((MainActivity) context).updateReCaptchaStatus(reCaptchaDetails);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Network error or some problem with play services

        ReCaptchaDetails reCaptchaDetails = new ReCaptchaDetails();
        reCaptchaDetails.setValid(false);
        reCaptchaDetails.setNetworkError(true);

        ((MainActivity) context).updateReCaptchaStatus(reCaptchaDetails);
    }

    public interface ReCaptchaStatus {
        void updateReCaptchaStatus(ReCaptchaDetails reCaptchaDetails);
    }

    public void stopVerification() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.disconnect();
        }
    }
}
