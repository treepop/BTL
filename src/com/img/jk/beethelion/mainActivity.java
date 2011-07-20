/* This program is used to identify type of flower via photo.
 * User takes a photo of unknown flower then this program list top 5 most likely.
 * 
 * Written at 2554-7-4 by Rungkarn Siricharoenchai Project manager.
 * 						  Treepop Sunpetchniyom	   TU Master.
 *  
 * Version 1.3
 * Platform Android
 *  
 * History
 * -------
 * 1.0 Original.
 * 1.1 7 July 54 preview image was wrong orientation but when it was corrected,
 * the take picture button was wrong orientation instead.
 * This bug was solved.
 * 1.2 7 July 54 I think macro wasn't effect.
 * 1.3 20 July 54 Write file to SD CARD.
 * 
 * Future work
 * -----------
 * The saved Image file was still wrong orientation.
 * 
 * Abbreviation for any comment.
 * 
 */

package com.img.jk.beethelion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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
				 x10Camera.takePicture(previewCallback, rawCallback, jpegCallback);
			}
		});
        
        /*This line has bug. The screen has wrong object orientation.
        This command doesn't correct the orientation bug.
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);*/
    }
    
    ShutterCallback previewCallback = new ShutterCallback() {
		
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
				String fNameUnknowFlower = "unknowFlower.jpg";
				File sdDir = Environment.getExternalStorageDirectory();
				if(sdDir.exists()) {
					if(sdDir.canWrite()) {
						File beeDir = new File(sdDir.getAbsolutePath()+"/beeTheLion");
						if(!beeDir.exists()) {
							beeDir.mkdir();
						}
						if(beeDir.exists() && beeDir.canWrite()) {
							File fileUnknowFlower = new File(beeDir.getAbsolutePath()+
									"/"+fNameUnknowFlower);
							try {
								fileUnknowFlower.createNewFile();
							} catch(IOException e) {
								Log.v("jkError", "Can't create file unknowFlower.jpg");
							}
							if(fileUnknowFlower.exists() &&
									fileUnknowFlower.canWrite()) {
								FileOutputStream fos = null;
								try {
									fos = new FileOutputStream(fileUnknowFlower);
									fos.write(jpegData);
								} catch(FileNotFoundException e) {
									
								} catch(IOException e) {
									
								}
								finally {
									if(fos != null) {
										try {
											fos.flush();
											fos.close();
										} catch (IOException e) {
											// swallow
										}
									}
								}
							}
						}
					} else {
						// SD CARD can't write!
					}
				} else {
					// SD CARD not found!
				}
				done();
			}
		}
	};
	
	void done() {
		finish(); // Exit program.
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
        List<Camera.Size> x10PicSizeMode = x10Parameters.getSupportedPictureSizes();
        Iterator<Camera.Size> iterPicSizeMode = x10PicSizeMode.iterator();
        while(iterPicSizeMode.hasNext()) {
        	Camera.Size item = iterPicSizeMode.next();
        	if(item.width == 640 && item.height == 480) {
        		x10Parameters.setPictureSize(640, 480);
        	}
        }
        
        // Set preview frame rate.
        List<Integer> x10PreviewFrameRate = x10Parameters
        	.getSupportedPreviewFrameRates();
        if(x10PreviewFrameRate.contains(30))
        	x10Parameters.setPreviewFrameRate(30);
        
        // Set preview size.
        List<Camera.Size> x10PreSizeMode = x10Parameters.getSupportedPreviewSizes();
        Iterator<Camera.Size> iterPreSizeMode = x10PreSizeMode.iterator();
        while(iterPreSizeMode.hasNext()) {
        	Camera.Size item = iterPreSizeMode.next();
        	if(item.width == 640 && item.height == 480) {
        		x10Parameters.setPreviewSize(640, 480);
        	}
        }
        
        
        // Fix bug wrong orientation.
        if(this.getResources().getConfiguration().orientation !=
        	Configuration.ORIENTATION_LANDSCAPE) {
        	// This is an undocumented although widely known feature.
        	x10Parameters.set("orientation", "portrait");
        	
        	// For Android 2.2 and above
        	//x10Camera.setDisplayOrientation(90);
        	
        	// Uncomment for Android 2.0 and above
        	//x10Parameters.setRotation(90);
        } else {
        	// This is an undocumented although widely known feature.
        	x10Parameters.set("orientation", "landscape");
        	
        	// For Android 2.2 and above
        	//x10Camera.setDisplayOrientation(0);
        	
        	// Uncomment for Android 2.0 and above
        	//x10Parameters.setRotation(0);
        }
        
        x10Camera.setParameters(x10Parameters);
        try {
        	x10Camera.setPreviewDisplay(holder);
		} catch (IOException exception) {
			x10Camera.release();
			Log.v("LOGTAG", exception.getMessage());
		}
		x10Camera.startPreview();
		mPreviewRunning = true;
        
        // For debug.
        // =========
        x10Parameters = x10Camera.getParameters();
        
        String infoFocusMode = x10Parameters.getFocusMode();
        Toast.makeText(this, "Focus = " + infoFocusMode, Toast.LENGTH_SHORT).show();
        Size infoPictureSize = x10Parameters.getPictureSize();
        Integer w = infoPictureSize.width;
        Integer h = infoPictureSize.height;
        String strPictureSize = w.toString() + " x " + h.toString();
        Toast.makeText(this, "Pic size = " + strPictureSize, Toast.LENGTH_SHORT).show();
        Integer infoPreviewFrameRate = x10Parameters.getPreviewFrameRate();
        Toast.makeText(this, "Frame rate = " + infoPreviewFrameRate.toString(),
        		Toast.LENGTH_SHORT).show();
        Size infoPreviewSize = x10Parameters.getPreviewSize();
        w = infoPreviewSize.width;
        h = infoPreviewSize.height;
        String strPreviewSize = w.toString() + " x " + h.toString();
        Toast.makeText(this, "Preview size = " + strPreviewSize, Toast.LENGTH_SHORT)
        	.show();
        // ========= 
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
		if(mPreviewRunning) {
			x10Camera.stopPreview();
			mPreviewRunning = false;
		}
		Camera.Parameters p = x10Camera.getParameters();
		p.setPictureSize(width, height);
		x10Camera.setParameters(p);
		x10Camera.startPreview();
		mPreviewRunning = true;
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		x10Camera.stopPreview();
		mPreviewRunning = false;
		x10Camera.release();
		x10Camera = null;
	}
}