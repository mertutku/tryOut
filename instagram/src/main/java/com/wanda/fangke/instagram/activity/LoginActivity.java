package com.wanda.fangke.instagram.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.wanda.fangke.instagram.R;
import com.wanda.fangke.instagram.instagramConstants.Constants;

import org.jinstagram.auth.InstagramAuthService;
import org.jinstagram.auth.model.Token;
import org.jinstagram.auth.oauth.InstagramService;

public class LoginActivity extends Activity {
    private static final Token EMPTY_TOKEN = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                InstagramService service = new InstagramAuthService()
                        .apiKey(Constants.CLIENT_ID)
                        .apiSecret(Constants.CLIENT_SECRET)
                        .callback(Constants.REDIRECT_URI)
                        .build();


                String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);
                Intent i = new Intent(getApplicationContext(), AuthPageActivity.class);
                i.putExtra("URL", authorizationUrl);
                startActivity(i);
            }
        });


        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .build();
        ImageLoader il = ImageLoader.getInstance();
        il.init(config);
//        il.displayImage(Constants.IMAGES[0], (ImageView) findViewById(R.id.profilePhoto));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
}
