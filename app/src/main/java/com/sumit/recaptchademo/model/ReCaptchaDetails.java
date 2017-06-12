package com.sumit.recaptchademo.model;


/**
 * Created by Sumit on 6/12/2017.
 */

import com.google.android.gms.common.api.Status;

public class ReCaptchaDetails {
    private boolean isValid;
    private boolean isNetworkError;
    private String tokenResult;
    private Status failDetail;

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public boolean isNetworkError() {
        return isNetworkError;
    }

    public void setNetworkError(boolean networkError) {
        isNetworkError = networkError;
    }

    public String getTokenResult() {
        return tokenResult;
    }

    public void setTokenResult(String tokenResult) {
        this.tokenResult = tokenResult;
    }

    public Status getFailDetail() {
        return failDetail;
    }

    public void setFailDetail(Status failDetail) {
        this.failDetail = failDetail;
    }
}
