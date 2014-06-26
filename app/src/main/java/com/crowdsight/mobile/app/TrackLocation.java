package com.crowdsight.mobile.app;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class TrackLocation extends ActionBarActivity {

    Button tl;
    TextView textLat, textLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_location);

        tl = (Button) findViewById(R.id.button_tl);
        textLat = (TextView) findViewById(R.id.textLat);
        textLong = (TextView) findViewById(R.id.textLong);
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener ll = new myLocationListener();
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);

//        tl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//
//
//            }
//        });


    }

    private class myLocationListener implements LocationListener{
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                double pLong = location.getLongitude();
                double pLat = location.getLatitude();

                textLat.setText(Double.toString(pLat));
                textLong.setText(Double.toString(pLong));
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.track_location, menu);
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

}
