package com.crowdsight.mobile.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterLoginActivity extends Activity {

    static String CONSUMER_KEY = "CV3otV0fHcA99rMaUzjZWAMb5";
    static String CONSUMER_SECRET = "2YO1m4Q6kMGf8SLG1J8nW6DqPRSDnetjMpNv76QYI3cvX2mnys";

    static String PREFERENCE_NAME = "twitter_oauth";
    static final String PREF_KEY_SECRET = "oauth_token_secret";
    static final String PREF_KEY_TOKEN = "oauth_token";

    static final String CALLBACK_URL = "oauth://twit2android";

    static final String IEXTRA_AUTH_URL = "auth_url";
    static final String IEXTRA_OAUTH_VERIFIER = "oauth_verifier";
    static final String IEXTRA_OAUTH_TOKEN = "oauth_token";

    private static final String TAG = "TwitAndroidSignIn";

    private static Twitter twitter;
    private static RequestToken requestToken;
    private static SharedPreferences mSharedPreferences;
    private static TwitterStream twitterStream;
    private boolean running = false;

    //Declare Widgets
    private Button loginTwitter, logoutTwitter;
    private TextView messageView;
    private EditText messageBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_login);

        //This will force the main thread to allow the network operations, but this is not the right way.
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mSharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);

        loginTwitter = (Button) findViewById(R.id.login_button);
        logoutTwitter = (Button) findViewById(R.id.buttonTwitterLogout);
        messageView = (TextView) findViewById(R.id.message);

        loginTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageView.setText("Logging in to twitter....");
                loginToTwitter();
            }

        });

        logoutTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disconnectTwitter();
                Toast.makeText(getApplicationContext(), "Successfully Logged out from Twitter.", Toast.LENGTH_SHORT).show();
                messageView.setText("Logged Out.");
            }
        });

        //Handling the call back
        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(CALLBACK_URL)) {
            String verifier = uri.getQueryParameter(IEXTRA_OAUTH_VERIFIER);
            try {
                AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
                SharedPreferences.Editor e = mSharedPreferences.edit();
                e.putString(PREF_KEY_TOKEN, accessToken.getToken());
                e.putString(PREF_KEY_SECRET, accessToken.getTokenSecret());
                e.commit();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    //Method definition for onresume
    protected void onResume() {
        super.onResume();

        if (isConnected()) {
            String oauthAccessToken = mSharedPreferences.getString(PREF_KEY_TOKEN, "");
            String oAuthAccessTokenSecret = mSharedPreferences.getString(PREF_KEY_SECRET, "");

            ConfigurationBuilder confbuilder = new ConfigurationBuilder();
            Configuration conf = confbuilder
                  .setOAuthConsumerKey(CONSUMER_KEY)
                  .setOAuthConsumerSecret(CONSUMER_SECRET)
                  .setOAuthAccessToken(oauthAccessToken)
                  .setOAuthAccessTokenSecret(oAuthAccessTokenSecret)
                  .build();
            twitterStream = new TwitterStreamFactory(conf).getInstance();

        } else {
            messageView.setText("Logged In");
        }
    }

    //check if the user is already logged in
    private boolean isConnected() {
        return mSharedPreferences.getString(PREF_KEY_TOKEN, null) != null;
    }

    //Method definition for twitter login
    private void loginToTwitter(){
        if(isConnected()){
            //disconnectTwitter();
            messageView.setText("Logged in");
            Toast.makeText(getApplicationContext(), "You are already Logged In.",Toast.LENGTH_SHORT).show();
        }else{
            askOAuth();
        }

    }

    //Ask User Authorization here

    private void askOAuth() {
        try {
            ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
            configurationBuilder.setOAuthConsumerKey(CONSUMER_KEY);
            configurationBuilder.setOAuthConsumerSecret(CONSUMER_SECRET);
            Configuration configuration = configurationBuilder.build();
            twitter = new TwitterFactory(configuration).getInstance();


            //Toast.makeText(this, "You are requesting authority", Toast.LENGTH_LONG).show();
            //Toast.makeText(this, "You are Here inside askOuth", Toast.LENGTH_LONG).show();
            requestToken = twitter.getOAuthRequestToken(CALLBACK_URL);
            Toast.makeText(this, "Please authorize this app!", Toast.LENGTH_LONG).show();
            this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
            messageView.setText("Logged In");
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    //clear the shared preferences
    private void disconnectTwitter() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove(PREF_KEY_TOKEN);
        editor.remove(PREF_KEY_SECRET);

        editor.commit();
        }
}
