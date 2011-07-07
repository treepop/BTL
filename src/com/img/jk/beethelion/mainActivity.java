/* Bug that has been corrected yet.
 * 7 July 54 preview image was wrong orientation but when it was corrected,
 * take picture button was wrong orientation instead. 
 * 7 July 54 I think macro wasn't effect.
 */

package com.img.jk.beethelion;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.Toast;

public class mainActivity extends Activity implements SurfaceHolder.Callback {
	
	private Camera x10Camera;
	private byte[] jpegData;
	private boolean mPreviewRunning = false;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        SurfaceHolder x10SurfaceHolder;
    	SurfaceView x10SurfaceView;
        x10SurfaceView = (SurfaceView)findViewById(R.id.surface);
        x10SurfaceHolder = x10SurfaceView.getHolder();
        x10SurfaceHolder.addCallback(this);
        x10SurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        LayoutInflater mInflater = null;
        mInflater = LayoutInflater.from(this);
        View overlayView = mInflater.inflate(R.layout.cameraoverlay, null);
        this.addContentView(overlayView,
        		new LayoutParams(LayoutParams.FILL_PARENT,
        				LayoutParams.FILL_PARENT));
        Button takePictureBtn;
        takePictureBtn = (Button)findViewById(R.id.button);
        takePictureBtn.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {
				 x10Camera.takePicture(shutterCallback, rawCallback, jpegCallback);
			}
		});
        // This line has bug. The screen has wrong obj orientation.
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
    
    ShutterCallback shutterCallback = new ShutterCallback() {
		
		@Override
		public void onShutter() {}
	};
	
    PictureCallback rawCallback = new PictureCallback() {
		
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {}
	};
	
	PictureCallback jpegCallback = new PictureCallback() {
		
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			if(data != null) {
				jpegData = data;
				done();
			}
		}
	};
	
	void done() {
		finish();
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		x10Camera = Camera.open();
		Camera.Parameters x10Parameters = x10Camera.getParameters();
        
        // Set focus to macro mode.
        List<String> x10FocusMode = x10Parameters.getSupportedFocusModes();
        if(x10FocusMode.contains(Camera.Parameters.FOCUS_MODE_MACRO))
        	x10Parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
        
        // Set picture size.
        /* Don't know how to check whether there is 640.
        List<Camera.Size> x10PicSizeMode = x10Parameters.getSupportedPictureSizes();
        if(x10PicSizeMode.contains(640))
        	x10Parameters.setPictureSize(640, 480);
    	*/
        x10Parameters.setPictureSize(3264, 2448);
        
        // Set preview frame rate.
        List<Integer> x10PreviewFrameRate = x10Parameters
        	.getSupportedPreviewFrameRates();
        if(x10PreviewFrameRate.contains(30))
        	x10Parameters.setPreviewFrameRate(30);
        
        // Set preview size.
        List<Camera.Size> x10PreSizeMode = x10Parameters.getSupportedPreviewSizes();
        x10Parameters.setPreviewSize(1280, 720);
        
        x10Camera.setParameters(x10Parameters);
        
        // For debug.
        // =========
        x10Parameters = x10Camera.getParameters();
        
        String infoFocusMode = x10Parameters.getFocusMode();
        Toast.makeText(this, infoFocusMode, Toast.LENGTH_SHORT).show();
        Size infoPictureSize = x10Parameters.getPictureSize();
        Integer w = infoPictureSize.width;
        Integer h = infoPictureSize.height;
        String strPictureSize = w.toString() + " x " + h.toString();
        Toast.makeText(this, strPictureSize, Toast.LENGTH_SHORT).show();
        Integer infoPreviewFrameRate = x10Parameters.getPreviewFrameRate();
        Toast.makeText(this, infoPreviewFrameRate.toString(),
        		Toast.LENGTH_SHORT).show();
        Size infoPreviewSize = x10Parameters.getPreviewSize();
        w = infoPreviewSize.width;
        h = infoPreviewSize.height;
        String strPreviewSize = w.toString() + " x " + h.toString();
        Toast.makeText(this, strPreviewSize, Toast.LENGTH_SHORT).show();
        // ========= 
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		try {
			if(mPreviewRunning) {
				x10Camera.stopPreview();
				mPreviewRunning = false;
			}
			Camera.Parameters p = x10Camera.getParameters();
			p.setPictureSize(width, height);
			x10Camera.setParameters(p);
			x10Camera.setPreviewDisplay(holder);
			x10Camera.startPreview();
			mPreviewRunning = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		x10Camera.stopPreview();
		mPreviewRunning = false;
		x10Camera.release();
		x10Camera = null;
	}
}