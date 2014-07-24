package com.crowdsight.mobile.app.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.crowdsight.mobile.app.adapters.items.PhotoItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pankaj on 23/07/14.
 */
public class PhotoGalleryImageProvider {

    // Consts
    public static final int IMAGE_RESOLUTION = 15;

    // Buckets where we are fetching images from.
    public static final String CAMERA_IMAGE_BUCKET_NAME =
            Environment.getExternalStorageDirectory().toString()
                    + "/DCIM/Camera";
    public static final String CAMERA_IMAGE_BUCKET_ID =
            getBucketId(CAMERA_IMAGE_BUCKET_NAME);

    /**
     * Fetch both full sized images and thumbnails via a single query.
     * Returns all images not in the Camera Roll.
     * @param context
     * @return
     */
    public static List<PhotoItem> getAlbumThumbnails(Context context){

        final String[] projection = {MediaStore.Images.Thumbnails.DATA,MediaStore.Images.Thumbnails.IMAGE_ID};

        Cursor thumbnailsCursor = context.getContentResolver().query( MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                projection, // Which columns to return
                null,       // Return all rows
                null,
                null);

        // Extract the proper column thumbnails
        int thumbnailColumnIndex = thumbnailsCursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA);
        ArrayList<PhotoItem> result = new ArrayList<PhotoItem>(thumbnailsCursor.getCount());

        if (thumbnailsCursor.moveToFirst()) {
            do {
                // Generate a tiny thumbnail version.
                int thumbnailImageID = thumbnailsCursor.getInt(thumbnailColumnIndex);
                String thumbnailPath = thumbnailsCursor.getString(thumbnailImageID);
                Uri thumbnailUri = Uri.parse(thumbnailPath);
                Uri fullImageUri = uriToFullImage(thumbnailsCursor,context);

                // Create the list item.
                PhotoItem newItem = new PhotoItem(thumbnailUri,fullImageUri);
                result.add(newItem);
            } while (thumbnailsCursor.moveToNext());
        }
        thumbnailsCursor.close();
        return result;
    }

    /**
     * Get the path to the full image for a given thumbnail.
     */
    private static Uri uriToFullImage(Cursor thumbnailsCursor, Context context){
        String imageId = thumbnailsCursor.getString(thumbnailsCursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID));

        // Request image related to this thumbnail
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor imagesCursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, filePathColumn, MediaStore.Images.Media._ID + "=?", new String[]{imageId}, null);

        if (imagesCursor != null && imagesCursor.moveToFirst()) {
            int columnIndex = imagesCursor.getColumnIndex(filePathColumn[0]);
            String filePath = imagesCursor.getString(columnIndex);
            imagesCursor.close();
            return Uri.parse(filePath);
        } else {
            imagesCursor.close();
            return Uri.parse("");
        }
    }

    /**
     * Render a thumbnail photo and scale it down to a smaller size.
     * @param path
     * @return
     */
    private static Bitmap bitmapFromPath(String path){
        File imgFile = new  File(path);
        Bitmap imageBitmap = null;

        if(imgFile.exists()){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = IMAGE_RESOLUTION;
            imageBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
        }
        return imageBitmap;
    }

    /**
     * Matches code in MediaProvider.computeBucketValues. Should be a common
     * function.
     */
    public static String getBucketId(String path) {
        return String.valueOf(path.toLowerCase().hashCode());
    }
}