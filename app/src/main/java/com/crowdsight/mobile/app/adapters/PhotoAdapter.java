package com.crowdsight.mobile.app.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.crowdsight.mobile.app.R;
import com.crowdsight.mobile.app.adapters.items.PhotoItem;
import com.etsy.android.grid.util.DynamicHeightImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by pankaj on 23/07/14.
 */
public class PhotoAdapter extends ArrayAdapter<PhotoItem> {


    private static final String TAG = "PhotoAdaptor";
    private Context context;
    private int resourceId;
    private final Random mRandom;

    private static final SparseArray<Double> sPositionHeightRatios = new SparseArray<Double>();


    public PhotoAdapter(Context context, int resourceId,
                        List<PhotoItem> items, boolean useList) {
        super(context, resourceId, items);
        mRandom = new Random();
        this.context = context;
        this.resourceId = resourceId;
    }

    /**
     * The "ViewHolder" pattern is used for speed.
     *
     * Reference: http://www.javacodegeeks.com/2013/09/android-viewholder-pattern-example.html
     */
    private class ViewHolder {
        DynamicHeightImageView photoImageView;
    }

    /**
     * Populate the view holder with data.
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        PhotoItem photoItem = getItem(position);
        View viewToUse = null;

        // This block exists to inflate the photo list item conditionally based on whether
        // we want to support a grid or list view.
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            holder = new ViewHolder();
            viewToUse = mInflater.inflate(resourceId, null);
            holder.photoImageView = (DynamicHeightImageView) viewToUse.findViewById(R.id.galleryImageView);
            viewToUse.setTag(holder);
        } else {
            viewToUse = convertView;
            holder = (ViewHolder) viewToUse.getTag();
        }

        double positionHeight = getPositionRatio(position);

        // Set the thumbnail
        holder.photoImageView.setImageURI(photoItem.getThumbnailUri());
        holder.photoImageView.setHeightRatio(positionHeight);

        return viewToUse;
    }

    private double getPositionRatio(final int position) {
        double ratio = sPositionHeightRatios.get(position, 0.0);
        // if not yet done generate and stash the columns height
        // in our real world scenario this will be determined by
        // some match based on the known height and width of the image
        // and maybe a helpful way to get the column height!
        if (ratio == 0) {
            ratio = getRandomHeightRatio();
            sPositionHeightRatios.append(position, ratio);
            Log.d(TAG, "getPositionRatio:" + position + " ratio:" + ratio);
        }
        return ratio;
    }

    private double getRandomHeightRatio() {
        return (mRandom.nextDouble() / 2.0) + 1.0; // height will be 1.0 - 1.5 the width
    }
}
