package com.sumit.recaptchademo;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.sumit.recaptchademo.api.TokenVerificationApi;
import com.sumit.recaptchademo.model.ReCaptchaDetails;
import com.sumit.recaptchademo.util.ReCaptchaVerification;
import com.sumit.recaptchademo.util.Util;
import com.tapadoo.alerter.Alerter;


/**
 * Created by Sumit on 6/12/2017.
 */

public class MainActivity extends AppCompatActivity implements ReCaptchaVerification.ReCaptchaStatus {

    private CoordinatorLayout coordinatorLayout;
    private Context context;
    private LottieAnimationView lottieAnimationView;
    private TextView textViewMessage;
    private ReCaptchaVerification reCaptchaVerification;
    private TokenVerificationTask tokenVerificationTask;

    public static boolean isActivityVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        initViews();
        playDefaultAnimation();
    }

    private void playDefaultAnimation() {
        lottieAnimationView.setAnimation("vr_animation.json");
        lottieAnimationView.loop(true);
        lottieAnimationView.playAnimation();
    }

    private void initViews() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        textViewMessage = (TextView) findViewById(R.id.text_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lottieAnimationView = (LottieAnimationView) findViewById(R.id.animation_view);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initReCaptcha();
            }
        });
    }

    private void initReCaptcha() {

        if (Util.isNetworkAvailable(context)) {
            reCaptchaVerification = new ReCaptchaVerification(context);
        } else {

            Alerter.create((MainActivity) context)
                    .setTitle(getString(R.string.alert))
                    .setIcon(R.drawable.ic_no_internet)
                    .setBackgroundColor(R.color.colorPrimary)
                    .setText(getString(R.string.no_network))
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {

            Alerter.create((MainActivity) context)
                    .setTitle(getString(R.string.about))
                    .setIcon(R.drawable.ic_user_verify)
                    .setBackgroundColor(R.color.colorPrimary)
                    .setText(getString(R.string.about_app))
                    .show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void playLottieAnimation(boolean isSuccess) {
        if (isSuccess) {
            lottieAnimationView.setAnimation("star.json");
        } else {
            lottieAnimationView.setAnimation("shrug.json");
        }

        lottieAnimationView.loop(true);
        lottieAnimationView.playAnimation();
    }

    @Override
    public void updateReCaptchaStatus(ReCaptchaDetails reCaptchaDetails) {
        if (reCaptchaDetails.isValid()) {
            playLottieAnimation(true);
            textViewMessage.setText(getString(R.string.success_message));

            tokenVerificationTask = new TokenVerificationTask();
            tokenVerificationTask.execute(reCaptchaDetails.getTokenResult());

        } else {
            playLottieAnimation(false);
            textViewMessage.setText(getString(R.string.failure_message));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityVisible = true;
    }

    @Override
    protected void onStop() {
        super.onStop();

        isActivityVisible = false;

        // Disconnect Google API Client

        if (reCaptchaVerification != null) {
            reCaptchaVerification.stopVerification();
        }

        if (tokenVerificationTask != null) {
            tokenVerificationTask.cancel(true);
        }
    }

    private class TokenVerificationTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            if(Util.isNetworkAvailable(context))
                return TokenVerificationApi.verifyReCaptchaUserToken(params[0], Util.getIPAddress(true));
            else return false;
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);

            String messageToDisplay;
            int iconToDisplay;
            int alertBackgroundColor;

            if (isSuccess) {
                messageToDisplay = getString(R.string.token_verification_success);
                iconToDisplay = R.drawable.ic_verification_success;
                alertBackgroundColor = R.color.green_600;
            } else {
                messageToDisplay = getString(R.string.token_verification_failed);
                iconToDisplay = R.drawable.ic_verification_failed;
                alertBackgroundColor = R.color.red_600;
            }

            // Show Message

            Alerter.create((MainActivity) context)
                    .setTitle(isSuccess ? getString(R.string.success) : getString(R.string.error))
                    .setIcon(iconToDisplay)
                    .setBackgroundColor(alertBackgroundColor)
                    .setText(messageToDisplay)
                    .show();

        }
    }
}
