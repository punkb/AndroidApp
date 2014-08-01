package com.crowdsight.mobile.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import twitter4j.Status;
import twitter4j.StatusUpdate;
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
    static String ACCESS_TOKEN = "250951799-XuzNyvEzumHnnc9friA1jS7IRFNiHoPOTCFjTmWq";
    static String ACCESS_TOKEN_SECRET = "xdPeQVa1LL48YTYMcMKgPSfJ1p4tSQsWtkOC2IdUXMGF3";
    static String TWIT_PIC_API = "9d89bf17bc6be90a5e9c19d050640e86";

    static String PREFERENCE_NAME = "twitter_oauth";
    static final String PREF_KEY_SECRET = "oauth_token_secret";
    static final String PREF_KEY_TOKEN = "oauth_token";

    static final String CALLBACK_URL = "oauth://twit2android";

    static final String IEXTRA_AUTH_URL = "auth_url";
    static final String IEXTRA_OAUTH_VERIFIER = "oauth_verifier";
    static final String IEXTRA_OAUTH_TOKEN = "oauth_token";

    private static final String TAG = "TwitAndroidSignIn";

    private static final int GALLERY_CODE = 1;

    private static Twitter twitter;
    private static RequestToken requestToken;
    private static SharedPreferences mSharedPreferences;
    private static TwitterStream twitterStream;
    private boolean running = false;

    //Declare Widgets
    private Button loginTwitter, logoutTwitter, imageUpload, uploadStatus;
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
        imageUpload = (Button) findViewById(R.id.photoUpload);
        messageView = (TextView) findViewById(R.id.message);
        uploadStatus = (Button) findViewById(R.id.statusUpload);

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

        imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnected()){
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, GALLERY_CODE);
                }else{
                    Toast.makeText(getApplicationContext(),"Please Login first to upload your image.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        uploadStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   uploadStatus("This is a random tweet test from android app.");
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

    private void uploadStatus(String tweet) {
        try
        {
            Status status = twitter.updateStatus(tweet);
            //System.out.println("Status updated to [" + status.getText() + "].");
        }
        catch (TwitterException te)
        {
            System.out.println("Error: "+ te.getMessage());
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
            messageView.setText("Please log in to continue.");
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

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey(CONSUMER_KEY);
        cb.setOAuthConsumerSecret(CONSUMER_SECRET);
      //  cb.setOAuthAccessToken(ACCESS_TOKEN);
      //  cb.setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);

        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();
        requestToken = null;

        String callbackURL = getResources().getString(R.string.twitter_callback);
        try{
                requestToken = twitter.getOAuthRequestToken(callbackURL);
        }catch (TwitterException e){
             e.printStackTrace();
        }

        //requestToken = twitter.getOAuthRequestToken(CALLBACK_URL);
        Toast.makeText(this, "Please authorize this app!", Toast.LENGTH_LONG).show();
        this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
        messageView.setText("Logged In");
    }

    /**save the captured image in onActivityResult**/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data )
    {
        super.onActivityResult(requestCode, resultCode, data);

            if(requestCode == GALLERY_CODE){
                if(resultCode == RESULT_OK && null != data){
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    //Toast.makeText(getApplicationContext(), "This is the path of the picture : "+picturePath, Toast.LENGTH_SHORT).show();

                    uploadImage(new File(picturePath)); //compare the dates and display the image
                }
            }
    }

    public void uploadImage(File file){

        if(file.exists()){
            try {
                StatusUpdate status = new StatusUpdate("This is my test status upload");
               status.setMedia(file);
                twitter.updateStatus(status);
            } catch (TwitterException e) {
                Log.d("TAG", "Pic Upload error" + e.getErrorMessage());
            }
        }
    }

    private Bitmap getScaledBitmap(String picturePath, int width, int height) {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, sizeOptions);

        int inSampleSize = calculateInSampleSize(sizeOptions, width, height);

        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(picturePath, sizeOptions);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }


    //clear the shared preferences
    private void disconnectTwitter() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove(PREF_KEY_TOKEN);
        editor.remove(PREF_KEY_SECRET);

        editor.commit();
        }
}
