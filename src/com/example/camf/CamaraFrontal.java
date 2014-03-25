package com.example.camf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * For Android camera about, check the oficial 
 * https://developer.android.com/guide/topics/media/camera.html 
 * and https://developer.android.com/reference/android/hardware/Camera.html
 * 
 * @author afelipe
 *
 */
@SuppressLint("NewApi")
public class CamaraFrontal extends Activity {

	private Camera mCamera;
	private CameraPreview mPreview;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camara_frontal);

		 //if not camera presence, come back and do nothing
		if (!checkCameraHardware(this)) {
			Toast.makeText(this, "No camera in your device, sorry! :D ",
					Toast.LENGTH_SHORT).show();
			return;
		}

		// Create an instance of Camera
		mCamera = getCameraInstance();

		// Create our Preview view and set it as the content of our activity.
		// I set fragmentLaoyout dimentions  to 1dp to 1dp at activity_camara_frontral.xml for no showing user graphics
		//that's not a correctly solution, check how to, withou preview
		//check this StackOver https://stackoverflow.com/questions/10799976/take-picture-without-preview-android
		mPreview = new CameraPreview(this, mCamera);
		 FrameLayout preview = (FrameLayout)
		 findViewById(R.id.camera_preview);
		 preview.addView(mPreview);

		Button captureButton = (Button) findViewById(R.id.button_capture);
		captureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// get an image from the camera
				if(mCamera != null)
					mCamera.takePicture(null, null, mPicture);
				else
				{
					Log.d("Error camera", "No camera started");
					//mCamera = getCameraInstance();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camara_frontal, menu);
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseCamera();   // release the camera immediately on pause event
	}
	@Override
	protected void onResume() {
		super.onResume();
		//here reload camera
	}
    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();  // release the camera for other applications
            mCamera = null;
            mPreview.destroyDrawingCache();
            mPreview = null;
        }
    }
    
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			if (Camera.getNumberOfCameras() == 2)
				c = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT); // attempt to get a Front Camera instance
			else
				c = Camera.open(); // attempt to get a Camera instance, bay
									// default is Back
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

	/** Check if this device has a camera */
	private boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE, "MisFotos");
			if (pictureFile == null) {
				Log.d("Error camera take picture",
						"Error creating media file, check storage permissions: ");
				return;
			}

			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				fos.close();
			} catch (FileNotFoundException e) {
				Log.d("Error saving picture",
						"File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d("Error saving picture",
						"Error accessing file: " + e.getMessage());
			}
		}
	};

	//this method was taken from StackOver answer https://stackoverflow.com/a/17631104/3395146
	private static File getOutputMediaFile(int type, String folder_name) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				folder_name);
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("Error saving picture", "Unable to create directory!");
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile = null;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");
		} else {
			// throw new SaveFileException(TAG, "Unkknown media type!");
		}
		Log.d("saving file", mediaStorageDir.getPath() + File.separator
				+ "IMG_" + timeStamp + ".jpg");
		return mediaFile;
	}

}
