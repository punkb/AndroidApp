package com.crowdsight.mobile.app;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends Activity {

    //define Constants
    private static final int  CAMERA_CODE = 1;
    private static final int GALLERY_CODE = 2;

    private static final String BITMAP_STORAGE_KEY = "viewbitmap";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";

    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

    //define widgets
    private Button captureImage, openGallery;
    private ImageView imgPreview;

    //define variables
    private String mCurrentPhotoPath;
    private Bitmap mImageBitmap;

    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.album_name);
    }

    //Method definition to get the storage directory
    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("CrowdsightImages", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }
        return storageDir;
    }

    //Method definition to create imageFile
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    //Method definition to setup photo file
    private File setUpPhotoFile() throws IOException {
        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();
        return f;
    }

    // Method definition to setup picture for size
    private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
        int targetW = imgPreview.getWidth();
        int targetH = imgPreview.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
        //Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		/* Associate the Bitmap to the ImageView */

        imgPreview.setImageBitmap(getScaledBitmap(mCurrentPhotoPath, 800, 800));
        imgPreview.setVisibility(View.VISIBLE);
    }

    //Method definition to add photo to gallery
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    //Method definition to dispatch Camera Intent
    private void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = null;
        try {
            f = setUpPhotoFile();
            mCurrentPhotoPath = f.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            mCurrentPhotoPath = null;
        }
        startActivityForResult(takePictureIntent, actionCode);
    }

    //Method definition to handle photo
    private void handleCameraPhoto() {

        if (mCurrentPhotoPath != null) {
            setPic();
            galleryAddPic();
            mCurrentPhotoPath = null;
        }
    }

    Button.OnClickListener mTakePicSOnClickListener =
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchTakePictureIntent(CAMERA_CODE);
                }
            };

    //On Create Method

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);


        //initialize widgets
        // setBtnListenerOrDisable(captureImage, takePicOnClickListener, MediaStore.ACTION_IMAGE_CAPTURE);

        imgPreview = (ImageView) findViewById(R.id.previewImageView);
        captureImage = (Button) findViewById(R.id.captureImageButton);
        openGallery = (Button) findViewById(R.id.openGalleryButton);

        openGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, GALLERY_CODE);
            }
        });

        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent(CAMERA_CODE);
            }
        });

        //Add actionListener to btnCapture

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
    }

   /* private void setBtnListenerOrDisable(
            Button btn,
            Button.OnClickListener onClickListener,
            String intentName
    ) {
        if (isIntentAvailable(this, intentName)) {
            btn.setOnClickListener(onClickListener);
        } else {
            btn.setText(
                    "Cannot" + " " + btn.getText());
            btn.setClickable(false);
        }
    } */

    /* Button.OnClickListener takePicOnClickListener =
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchTakePictureIntent(CAMERA_CODE);
                }
            }; */


  /*  public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    } */

    /**save the captured image in onActivityResult**/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data )
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_CODE: {
                if (resultCode == RESULT_OK) {
                    handleCameraPhoto();
                }
                break;
            }
            case GALLERY_CODE:{
                if(resultCode == RESULT_OK && null != data){
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    compareDateAndDisplay(picturePath); //compare the dates and display the image
                }
            }
        }
    }

    //Method Definition to compare the dates and display the picture
    public void compareDateAndDisplay(String path){
        File file = new File(path);
        if(file.exists()) {
            long date = file.lastModified();
            Date imageCapturedDate = new Date(date);
            //lets take my date
            String myDate = "2014/07/8 10:10:10";
            //myDate = Calendar.getInstance().getTime();
            Date myFormatedDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            //convert into date object
            try {
                myFormatedDate = sdf.parse(myDate);
                System.out.println(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //Now compare two dates and allow user to display the picture in the time frame only
            if(imageCapturedDate.before(myFormatedDate)){
                Toast.makeText(getApplicationContext(),
                        "This image is before the specified date. Please select another image. ", Toast.LENGTH_SHORT)
                        .show();
            }else{
                imgPreview.setImageBitmap(getScaledBitmap(path, 800, 800));
            }
        }
    }

    private Bitmap getScaledBitmap(String picturePath, int width, int height) {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, sizeOptions);

        int inSampleSize = calculateInSampleSize(sizeOptions, width, height);

        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(picturePath, sizeOptions);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.camera, menu);
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


    // Some lifecycle callbacks so that the image can survive orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
        // outState.putParcelable(VIDEO_STORAGE_KEY, mVideoUri);
        outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null) );
        //  outState.putBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY, (mVideoUri != null) );
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
        //  mVideoUri = savedInstanceState.getParcelable(VIDEO_STORAGE_KEY);
        imgPreview.setImageBitmap(mImageBitmap);
        imgPreview.setVisibility(
                savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ?
                        ImageView.VISIBLE : ImageView.INVISIBLE
        );
        //   mVideoView.setVideoURI(mVideoUri);
        //    mVideoView.setVisibility(
        //            savedInstanceState.getBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY) ?
        //                   ImageView.VISIBLE : ImageView.INVISIBLE
        //  );
    }
}

// Class definitions required for this activity

abstract class AlbumStorageDirFactory {
    public abstract File getAlbumStorageDir(String albumName);
}


class FroyoAlbumDirFactory extends AlbumStorageDirFactory {

    @Override
    public File getAlbumStorageDir(String albumName) {
        return new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                ),
                albumName
        );
    }
}

class BaseAlbumDirFactory extends AlbumStorageDirFactory {

    // Standard storage location for digital camera files
    private static final String CAMERA_DIR = "/dcim/";

    @Override
    public File getAlbumStorageDir(String albumName) {
        return new File (
                Environment.getExternalStorageDirectory()
                        + CAMERA_DIR
                        + albumName
        );
    }
}





