package com.crowdsight.mobile.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class TwitterWebActivity extends ActionBarActivity {

    private Intent mIntent;
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_web);
        mIntent = getIntent();
        String url = (String)mIntent.getExtras().get("URL");
        WebView webView = (WebView)findViewById(R.id.webview);
        webView.setWebViewClient( new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                if( url.contains( getResources().getString( R.string.twitter_callback ) ) )
                {
                    Uri uri = Uri.parse( url );
                    String oauthVerifier = uri.getQueryParameter( "oauth_verifier" );
                    mIntent.putExtra( "oauth_verifier", oauthVerifier );
                    setResult( RESULT_OK, mIntent );
                    finish();
                    return true;
                }
                return false;
            }
        });
        webView.loadUrl(url);
    }
}
