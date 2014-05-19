package com.crowdsight.mobile.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;


public class SignIn extends ActionBarActivity {

    ImageButton newaccountButton;
    ImageButton loginButton;
    ImageButton facebookButton;
    ImageButton plusButton;
    ImageButton twitterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        addListenerButtons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sign_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void addListenerButtons() {

        newaccountButton = (ImageButton) findViewById(R.id.imageButton);
        newaccountButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //TODO: call new account activity
                Toast.makeText(SignIn.this, "New account not available!", Toast.LENGTH_SHORT).show();
            }
        });

        facebookButton = (ImageButton) findViewById(R.id.imageButton3);
        facebookButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //TODO: call new account activity
                Toast.makeText(SignIn.this, "Facebook services not available!", Toast.LENGTH_SHORT).show();
            }
        });

        plusButton = (ImageButton) findViewById(R.id.imageButton4);
        plusButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //TODO: call new account activity
                Toast.makeText(SignIn.this, "Google+ services not available!", Toast.LENGTH_SHORT).show();
            }
        });

        twitterButton = (ImageButton) findViewById(R.id.imageButton5);
        twitterButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //TODO: call new account activity
                Toast.makeText(SignIn.this, "Twitter services not available!", Toast.LENGTH_SHORT).show();
            }
        });

        loginButton = (ImageButton) findViewById(R.id.imageButton2);
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //TODO: check email/password are correct filled
                Intent i = new Intent(SignIn.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
