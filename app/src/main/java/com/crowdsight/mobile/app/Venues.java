package com.crowdsight.mobile.app;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Venues.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Venues#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class Venues extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Venues.
     */
    // TODO: Rename and change types and number of parameters
    public static Venues newInstance(String param1, String param2) {
        Venues fragment = new Venues();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public Venues() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_venues, container, false);
        ImageButton imagewembley = (ImageButton) view.findViewById(R.id.imageWembley);
        imagewembley.setOnClickListener(this);

        ImageButton imageleft = (ImageButton) view.findViewById(R.id.imageLeft);
        imageleft.setOnClickListener(this);

        ImageButton imageright = (ImageButton) view.findViewById(R.id.imageRight);
        imageright.setOnClickListener(this);

        ImageButton imagebanner = (ImageButton) view.findViewById(R.id.imagebanner);
        imagebanner.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.imageWembley:
                Intent i = new Intent(v.getContext(), inside.class);
                startActivity(i);
                break;
            case R.id.imageLeft:
                Toast.makeText(v.getContext(), "Navigation disabled!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageRight:
                Toast.makeText(v.getContext(), "Navigation disabled!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.imagebanner:
                Toast.makeText(v.getContext(), "Advertisement or sponsorship related to venue above", Toast.LENGTH_LONG).show();
                break;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
