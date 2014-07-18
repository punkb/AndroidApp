package com.crowdsight.mobile.app;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class TrackLocationFragment extends Fragment {

    Button tl;
    TextView textLat, textLong, FtextLong, FtextLat, dValue, rText;

    double radius = 1;

    public TrackLocationFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textLat = (TextView) getActivity().findViewById(R.id.textLat);
        textLong = (TextView) getActivity().findViewById(R.id.textLong);

        FtextLong = (TextView) getActivity().findViewById(R.id.FtextLong);
        FtextLat = (TextView) getActivity().findViewById(R.id.FtextLat);
        dValue = (TextView) getActivity().findViewById(R.id.dValue);
        rText = (TextView) getActivity().findViewById(R.id.rText);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.track_location_fragment,
                container, false);
        return view;







    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener ll = new myLocationListener();
        //change NETWORK_PROVIDER to GPS_PROVIDER to compile and run on Emulator
        // NETWORK_PROVIDER does not support Emulator.
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);


    }

    private class myLocationListener implements LocationListener {


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

                if (Distance > radius) {

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
}
