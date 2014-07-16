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
    TextView textLat, textLong, FtextLong, FtextLat, dValue, rText;

    //change the radius to define the limited area
    double radius = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_location);

        textLat = (TextView) findViewById(R.id.textLat);
        textLong = (TextView) findViewById(R.id.textLong);

        FtextLong = (TextView) findViewById(R.id.FtextLong);
        FtextLat = (TextView) findViewById(R.id.FtextLat);
        dValue = (TextView) findViewById(R.id.dValue);
        rText = (TextView) findViewById(R.id.rText);


        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener ll = new myLocationListener();
        //change NETWORK_PROVIDER to GPS_PROVIDER to compile and run on Emulator
        // NETWORK_PROVIDER does not support Emulator.
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);



    }

    private class myLocationListener implements LocationListener   {



        @Override
        public void onLocationChanged(Location userLocation) {
            if (userLocation != null) {
                double pLong = userLocation.getLongitude();
                double pLat = userLocation.getLatitude();

                textLat.setText(Double.toString(pLat));
                textLong.setText(Double.toString(pLong));

                // fixed location values for stadium

                Location fixedLocation = new Location("fLocation");
                // change this value as per your co-ordinates
                // you can get the co-ordinates when you run it 1st time on your device
                // record the coordinates and change accordingly then change the radius above
                fixedLocation.setLatitude(53.3533363);
                fixedLocation.setLongitude(-6.2290848);
                double fLat = fixedLocation.getLatitude();
                double fLong = fixedLocation.getLongitude();

                FtextLat.setText(Double.toString(fLat));
                FtextLong.setText(Double.toString(fLong));

                //calculating the distance

                float Distance = userLocation.distanceTo(fixedLocation);

                dValue.setText(Double.toString(Distance));

                if (Distance > radius){

                    rText.setText("Sorry You are out !");

                } else


                rText.setText("You are in !!");



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
