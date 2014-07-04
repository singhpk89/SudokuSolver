package com.example.sudokusolver;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * class that manages changes made to surface
 * @author E Wong
 *
 */
public class SurfaceHolderCallback implements SurfaceHolder.Callback {

	private Camera mCamera;
	private Camera.Parameters mParams;
	private boolean mPreviewRunning;
	private PictureCallback mPictureCB;
	String TAG = "preview"; 
	
	public SurfaceHolderCallback(Camera camera, RectangleView rectView, Context c){
		mCamera = camera;
		mPictureCB = new PictureCallback(rectView, this, c);
	}
	// called immediately when surface is first created(initialize camera)
	@Override
	public void surfaceCreated(SurfaceHolder sh) {
		
		try {
			mCamera = Camera.open();	
			mCamera.setDisplayOrientation(90);
			mCamera.setPreviewDisplay(sh);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// called when change made to surface; called at least once immediately
	// after onsurfacecreated
	@Override
	public void surfaceChanged(SurfaceHolder sh, int format, int width,
			int height) {
		startPreview();

	}

	// called immediately after surface destroyed(stop preview and release cam)
	@Override
	public void surfaceDestroyed(SurfaceHolder sh) {
		stopPreview();
		mCamera.release();
		mCamera = null;
	}
	
	public void startPreview(){
		
		if(!mPreviewRunning && mCamera != null){
			startAutoFocus();
			mCamera.startPreview();
			Log.i(TAG, "start preview");
			mPreviewRunning = true;
		}
	}
	
	public void stopPreview(){
		Log.i(TAG, "stop preview");
		if(mPreviewRunning && mCamera != null){
			mCamera.stopPreview();
			stopAutoFocus();
			mPreviewRunning = false;
		}
	}
	
	public void takePicture(){
		mCamera.takePicture(null, null, mPictureCB);
	}

	private void startAutoFocus(){
		if(mParams == null){
			mParams = mCamera.getParameters();
			List<String> modes = mParams.getSupportedFocusModes();
			if(modes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)){
				mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
				Log.i("Focus mode", "Continuous picture mode: success");
			}
			else{
				Log.i("Focus mode", "Continuous picture mode: fail");
			}
			mCamera.setParameters(mParams);
		}
		mCamera.autoFocus(autoFocusCB);
	}
	
	private void stopAutoFocus(){
		mCamera.cancelAutoFocus();
	}
	
	private Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
		
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			if(success) Log.d("Autofocus", "autofocus success");
			else Log.d("Autofocus", "autofocus fail");
			
		}
	};
}
