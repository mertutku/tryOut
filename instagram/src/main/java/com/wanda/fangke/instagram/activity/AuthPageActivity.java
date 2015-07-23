package com.wanda.fangke.instagram.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wanda.fangke.instagram.R;
import com.wanda.fangke.instagram.instagramConstants.Constants;

import org.jinstagram.Instagram;
import org.jinstagram.auth.AccessTokenExtractor;
import org.jinstagram.auth.InstagramAuthService;
import org.jinstagram.auth.model.Token;
import org.jinstagram.auth.model.Verifier;
import org.jinstagram.auth.oauth.InstagramService;
import org.jinstagram.entity.common.ImageData;
import org.jinstagram.entity.common.Images;
import org.jinstagram.entity.common.Location;
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.exceptions.InstagramException;
import org.jinstagram.model.QueryParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AuthPageActivity extends ActionBarActivity {
    private static final Token EMPTY_TOKEN = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_page);

        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                URLChecker task = new URLChecker();
                task.execute(new String[]{url});
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http://www.mertutku.com")) {
                    return true;
                }
                return false;
            }
        };

        InstagramService service = new InstagramAuthService()
                .apiKey(Constants.CLIENT_ID)
                .apiSecret(Constants.CLIENT_SECRET)
                .callback(Constants.REDIRECT_URI)
                .build();


        String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN); // EMPTY_TOKEN = null
        WebView wv = (WebView) findViewById(R.id.webView);
        wv.setVerticalScrollBarEnabled(false);
        wv.setHorizontalScrollBarEnabled(false);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebViewClient(webViewClient);
        wv.loadUrl(authorizationUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_auth_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class URLChecker extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String url = urls[0];
            if (url.startsWith("http://www.mertutku.com")) {
                int index = url.indexOf("code=");
                if (index != -1) {

                    String verificationCode = url.substring(index + 5);
                    InstagramService service = new InstagramAuthService()
                            .apiKey(Constants.CLIENT_ID)
                            .apiSecret(Constants.CLIENT_SECRET)
                            .callback(Constants.REDIRECT_URI)
                            .build();
                    Verifier verifier = new Verifier(verificationCode);
                    Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.putExtra("ACCESS_TOKEN", accessToken);
                    startActivity(i);
                    finish();
                } else {
                    finish(); //access denied therefore return to login activity
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }


}
