/* This program is used to identify type of a flower via taking a photo.
 * User takes a photo of an unknown flower then this program list top 5 most likely.
 * 
 * Written at 2554-7-4 by Rungkarn Siricharoenchai Project manager.
 * 						  Treepop Sunpetchniyom	   TU Master.
 *  
 * Version 1.9
 * Platform Android
 *  
 * History
 * -------
 * 1.0 Original.
 * 1.1 7 July 54 Preview image was wrong orientation but when it was corrected,
 * the take picture button was wrong orientation instead.
 * This bug was solved.
 * 1.2 7 July 54 I think macro wasn't effect.
 * 1.3 20 July 54 Write file to SD CARD.
 * 1.4 21 July 54 Fix bug,the saved image file was still wrong orientation.
 * 1.5 try to use macro focus but not work.
 * 1.6 Rotate saved image.
 * 1.7 24 July 54 Fix bug when rotated photo not align.
 * Edit error handling.
 * 1.8 Don't show the title bar.
 * 1.9 Move icon to left and right. 
 * 
 * Future work
 * -----------
 * When take a photo already then ask user to take photo again or matching photo.
 * 
 * Abbreviation for any comment.
 * 
 */

package com.img.jk.beethelion;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.Toast;

public class mainActivity extends Activity implements SurfaceHolder.Callback,
		OnClickListener,OnCompletionListener {

	// Equal to #define in C++
	// =======================
	private static final String UNKNOWN_FLOWER = "unknownFlower";
	private static final String RUNNING_NUMBER_FILE = "runningNumber.txt";
	private static final String DIR_OF_PROGRAM = "beeTheLion";
	private static final String DIR_OF_HISTORY_PHOTO = "historyPhoto";
	private static final String DB_OF_PROGRAM = "beethelion.db";
	private static final String DIR_OF_DESCRIPTION = "descriptionDB";
	private static final String DIR_OF_FLOWERPIC = "flowerPicDB";
	public static final int LARGEST_WIDTH = 800;
	public static final int LARGEST_HEIGHT = 600;
	
	public static final Boolean RUN_ON_USER_PHONE = false;
	
	GlobalVar gbV;
	private Camera x10Camera;
//	MediaPlayer mediaPlayerBee;
	MediaPlayer mediaPlayerBB;
	
	/*private boolean mPreviewRunning = false;
	private boolean inMacro = false;*/
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	RamLib.ramStatus();
        super.onCreate(savedInstanceState);
        
        // requestWindowFeature(Window.FEATURE_NO_TITLE); //Don't show the title bar.
        
        setContentView(R.layout.main);
        
        // Copy data from .apk to sd-card on first time.
        // =============================================
        String strBeeDir = Environment.getExternalStorageDirectory().getAbsolutePath()
        	+ "/" + DIR_OF_PROGRAM;
        File beeDir = new File(strBeeDir);
        if(!beeDir.exists()) {
        	beeDir.mkdir();
        }
        beeDir = null;
        
        try {
        	String destPath = strBeeDir + "/" + DB_OF_PROGRAM;
        	File f = new File(destPath);
        	if(!f.exists()) {
        		CopyDB(getBaseContext().getAssets().open(DB_OF_PROGRAM),
        				new FileOutputStream(destPath));
        		
        		File descDir = new File(strBeeDir + "/" + DIR_OF_DESCRIPTION);
        		if(!descDir.exists()) {
        			descDir.mkdir();
        		}
        		CopyDB(getBaseContext().getAssets().open(DIR_OF_DESCRIPTION + ".txt"),
        				new FileOutputStream
        					(descDir + ".txt"));
        		File picDir = new File(strBeeDir + "/" + DIR_OF_FLOWERPIC);
        		if(!picDir.exists()) {
        			picDir.mkdir();
        		}
        		CopyDB(getBaseContext().getAssets().open(DIR_OF_FLOWERPIC + ".txt"),
        				new FileOutputStream
        					(picDir + ".txt"));
        		
        		// Read a list of files then write.
        		BufferedReader brDesc = new BufferedReader(new FileReader(descDir
        				+ ".txt"));
        		String line;
        		List<String> descList = new ArrayList<String>();
        		while((line = brDesc.readLine()) != null) {
        			descList.add(line);
        		}
        		for(String s:descList) {
        			CopyDB(getBaseContext().getAssets().open(DIR_OF_DESCRIPTION + "/"
        					+ s), new FileOutputStream(descDir + "/" + s));
        		}
        		
        		BufferedReader brPic = new BufferedReader(new FileReader(picDir
        				+ ".txt"));
        		List<String> picList = new ArrayList<String>();
        		while((line = brPic.readLine()) != null) {
        			picList.add(line);
        		}
        		for(String s:picList) {
        			CopyDB(getBaseContext().getAssets().open(DIR_OF_FLOWERPIC + "/"
        					+ s), new FileOutputStream(picDir + "/" + s));
        		}

        		descList.clear();
        		descList = null;
        		brDesc.close();
        		brDesc = null;
        		descDir = null;
        		
        		picList.clear();
        		picList = null;
        		brPic.close();
        		brPic = null;
        		picDir = null;
        	}
        	f = null;
        } catch (FileNotFoundException e) {
        	e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        }
        // =============================================
        
        // Connect SurfaceView.
        SurfaceView x10SurfaceView;
        SurfaceHolder x10SurfaceHolder;
        x10SurfaceView = (SurfaceView)findViewById(R.id.surface);
        x10SurfaceHolder = x10SurfaceView.getHolder();
        x10SurfaceHolder.addCallback(this);
        x10SurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        // Display overlay.
        LayoutInflater mInflater = null;
        mInflater = LayoutInflater.from(this);
        View overlayView = mInflater.inflate(R.layout.cameraoverlay, null);
        this.addContentView(overlayView,
        		new LayoutParams(LayoutParams.FILL_PARENT,
        				LayoutParams.FILL_PARENT));
        
        ImageButton atfBtn = (ImageButton)findViewById(R.id.macro);
        atfBtn.setOnClickListener(this);
        
        // Connect take picture button.
        ImageButton takePictureBtn;
        takePictureBtn = (ImageButton)findViewById(R.id.cameraBtn);
        takePictureBtn.setOnClickListener(new OnClickListener() {        	
        	
        	@Override
        	public void onClick(View v) {
        		x10Camera.autoFocus(x10AutoFocusCallbackTakePhoto);
			}
		});
        
        ImageButton infoBtn = (ImageButton)findViewById(R.id.infoBtn);
        infoBtn.setOnClickListener(this); // If you new inner class here, it take up
        	// an extra 1KB of memory.
        
        // Connect macro button.
        /*macroBtn = (Button)findViewById(R.id.macroBtn);
        macroBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(inMacro) {
					x10Camera.stopPreview();
					Camera.Parameters x10Parameters = x10Camera.getParameters();
					x10Parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
					x10Camera.setParameters(x10Parameters);
					macroBtn.setText("Auto Focus");
					inMacro = false;
					x10Camera.startPreview();
				} else {
					x10Camera.stopPreview();
					Camera.Parameters x10Parameters = x10Camera.getParameters();
					x10Parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
					x10Camera.setParameters(x10Parameters);
					macroBtn.setText("Macro");
					inMacro = true;
					x10Camera.startPreview();
				}
			}
		});*/

        /*This line has bug. The screen has wrong object orientation.
        This following command doesn't correct the orientation bug.
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);*/
        
        // Create global variable.
        gbV = (GlobalVar)getApplicationContext();
        gbV.setStrUnknownFlower(UNKNOWN_FLOWER);
        gbV.setStrRunningNumberFile(RUNNING_NUMBER_FILE);
        gbV.setStrHistoryPhotoDir(DIR_OF_HISTORY_PHOTO);
    } // End onCreate.
    
    ShutterCallback previewCallback = new ShutterCallback() {
    // For manipulation of preview image.
		
		@Override
		public void onShutter() {}
	};
	
    PictureCallback rawCallback = new PictureCallback() {
    // For manipulation of raw image.
		
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {}
	};
	
	PictureCallback jpegCallback = new PictureCallback() {
	// For manipulation of jpeg image.
		
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			if(data != null) {
				
				// Rotate image.
				Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
				Bitmap rotatedBmp = Bitmap.createBitmap(bmp.getHeight(),
						bmp.getWidth(), bmp.getConfig());
						// Interchange width and height.
				Canvas canvas = new Canvas(rotatedBmp);
				Paint paint = new Paint();
				
				Matrix matrix = new Matrix();
				matrix.setRotate(90);
				matrix.postTranslate(bmp.getHeight(), 0);
				canvas.drawBitmap(bmp, matrix, paint);
				// -------------
				
				// Write image on global variable.
				gbV.setBmpPhoto(rotatedBmp);
				
				String fNameUnknownFlower = UNKNOWN_FLOWER + ".jpg";
				File sdDir = Environment.getExternalStorageDirectory();
				if(sdDir.exists()) {
					if(sdDir.canWrite()) {
						File beeDir = new File(sdDir.getAbsolutePath() + "/" + 
								DIR_OF_PROGRAM);
						if(!beeDir.exists()) {
							beeDir.mkdir();
						}
						// Keep beeDir on global variable.
						gbV.setStrBeeDir(beeDir.getAbsolutePath() + "/");
						if(beeDir.exists() && beeDir.canWrite()) {
							// Write unknownFlower.jpg for used in searching.
							File fileUnknownFlower = new File(beeDir.getAbsolutePath() +
									"/" + fNameUnknownFlower);
							try {
								fileUnknownFlower.createNewFile();
							} catch(IOException e) {
								Toast.makeText(mainActivity.this,
										e.getMessage(),
										Toast.LENGTH_LONG).show();
								Log.e("jkError", e.getMessage());
							}
							if(fileUnknownFlower.exists() &&
									fileUnknownFlower.canWrite()) {
								BufferedOutputStream fos = null;
								try {
									fos = new BufferedOutputStream(
											new FileOutputStream(fileUnknownFlower));
									// fos.write(jpegData);
									rotatedBmp.compress(CompressFormat.JPEG, 100, fos);
								} catch(FileNotFoundException e) {
									Toast.makeText(mainActivity.this,
											e.getMessage(),
											Toast.LENGTH_LONG).show();
									Log.e("jkError", e.getMessage());
								} finally {
									if(fos != null) {
										try {
											fos.flush();
											fos.close();
											fos = null;
										} catch (IOException e) {
											Toast.makeText(mainActivity.this,
													e.getMessage(),
													Toast.LENGTH_LONG).show();
											Log.e("jkError", e.getMessage());
										}
									}
								}
							}
						}
					} else {
						// SD CARD can't write!
						Toast.makeText(mainActivity.this, "SD CARD can't write!",
								Toast.LENGTH_LONG).show();
						Log.e("jkERROR", "SD CARD can't write!");
					}
				} else {
					// SD CARD not found!
					Toast.makeText(mainActivity.this, "SD CARD not found!",
							Toast.LENGTH_LONG).show();
					Log.e("jkERROR", "SD CARD not found!");
				}
				// Free variable.
				matrix = null;
				paint = null;
				canvas = null;
				// rotatedBmp.recycle(); // Enable this cause to "Canvas: trying to
										 // use a recycled bitmap". I understand it,
										 // because gbV.setBmpPhoto(rotatedBmp) still
										 // use this Bitmap.
				rotatedBmp = null;
				bmp.recycle();
				bmp = null;
				
				done();
			}
		}
	};
	
	void done() {
		RamLib.ramStatus();
		Intent i = new Intent(this,ShowTakenPhoto.class);
		startActivity(i);
		// finish(); // Exit program.
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		x10Camera = Camera.open();
		Camera.Parameters x10Parameters = x10Camera.getParameters();
		
		// Display all of supported parameters.
		// Uncommented this for debug.
		/*String strCameraParam = x10Parameters.flatten();
        Log.e("Param of Sony X10", strCameraParam);*/
        
		// Set focus to auto focus mode.
        /*List<String> x10FocusMode = x10Parameters.getSupportedFocusModes();
        if(x10FocusMode.contains(Camera.Parameters.FOCUS_MODE_AUTO))
        	x10Parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);*/
		
		// Set focus to macro mode.
        List<String> x10FocusMode = x10Parameters.getSupportedFocusModes();
        if(x10FocusMode.contains(Camera.Parameters.FOCUS_MODE_MACRO))
        	x10Parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
        
        // Set picture size.
        int bestWidth = 0;
        int bestHeight = 0;
        List<Camera.Size> x10PicSizeMode = x10Parameters.getSupportedPictureSizes();
        Iterator<Camera.Size> iterPicSizeMode = x10PicSizeMode.iterator();
        while(iterPicSizeMode.hasNext()) {
        	Camera.Size item = iterPicSizeMode.next();
        	if(item.width > bestWidth && item.width <= LARGEST_WIDTH
        			&& item.height > bestHeight && item.height <= LARGEST_HEIGHT) {
        		bestWidth = item.width;
        		bestHeight = item.height;
        	}
        }
        if(bestWidth != 0 && bestHeight != 0) {
        	x10Parameters.setPictureSize(bestWidth, bestHeight);
        }
        
        // Set preview frame rate.
        List<Integer> x10PreviewFrameRate = x10Parameters
        	.getSupportedPreviewFrameRates();
        if(x10PreviewFrameRate.contains(30))
        	x10Parameters.setPreviewFrameRate(30);
        
        // Set preview size.
        bestWidth = 0;
        bestHeight = 0;
        List<Camera.Size> x10PreSizeMode = x10Parameters.getSupportedPreviewSizes();
        Iterator<Camera.Size> iterPreSizeMode = x10PreSizeMode.iterator();
        while(iterPreSizeMode.hasNext()) {
        	Camera.Size item = iterPreSizeMode.next();
        	if(item.width > bestWidth && item.width <= LARGEST_WIDTH
        			&& item.height > bestHeight && item.height <= LARGEST_HEIGHT) {
        		bestWidth = item.width;
        		bestHeight = item.height;
        	}
        }
        if(bestWidth != 0 && bestHeight != 0) {
        	x10Parameters.setPreviewSize(bestWidth, bestHeight);
        }
        
        
        // Fix bug wrong orientation.
        if(this.getResources().getConfiguration().orientation !=
        	Configuration.ORIENTATION_LANDSCAPE) {
        	// This is an undocumented although widely known feature.
        	x10Parameters.set("orientation", "portrait");
        	
        	// For Android 2.2 and above
        	x10Camera.setDisplayOrientation(90);
        	
        	// Uncomment for Android 2.0 and above
        	//x10Parameters.setRotation(90);
        } else {
        	// This is an undocumented although widely known feature.
        	x10Parameters.set("orientation", "landscape");
        	
        	// For Android 2.2 and above
        	x10Camera.setDisplayOrientation(0);
        	
        	// Uncomment for Android 2.0 and above
        	//x10Parameters.setRotation(0);
        }
        // --------------------------
        
        x10Camera.setParameters(x10Parameters);
        try {
        	x10Camera.setPreviewDisplay(holder);
		} catch (IOException exception) {
			x10Camera.release();
			Toast.makeText(mainActivity.this, exception.getMessage(), Toast.LENGTH_LONG)
				.show();
			Log.e("jkError", exception.getMessage());
		}
		x10Camera.startPreview();
//		mPreviewRunning = true;
		x10Camera.autoFocus(x10AutoFocusCallbackFirstOpenCamera);
        
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
		
		/*if(mPreviewRunning) {
			x10Camera.stopPreview();
			mPreviewRunning = false;
		}
		Camera.Parameters p = x10Camera.getParameters();
		p.setPictureSize(width, height);
		x10Camera.setParameters(p);
		x10Camera.startPreview();
		mPreviewRunning = true;*/
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		x10Camera.stopPreview();
//		mPreviewRunning = false;
		x10Camera.release();
		x10Camera = null;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.infoBtn:
			// Play sound effect click.
			mediaPlayerBB = MediaPlayer.create(this, R.raw.blackberry);
			mediaPlayerBB.start();
			Intent i = new Intent(this,About.class);
			startActivity(i);
			break;
			
		case R.id.macro:
			// Play sound effect click.
			mediaPlayerBB = MediaPlayer.create(this, R.raw.blackberry);
			mediaPlayerBB.start();
			x10Camera.autoFocus(x10AutoFocusCallback);
			break;
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		/*mediaPlayerBee = MediaPlayer.create(this, R.raw.bee);
		mediaPlayerBee.setLooping(true);
		mediaPlayerBee.start();*/
	}

	@Override
	protected void onStop() {
		super.onStop();
		/*mediaPlayerBee.stop();
		mediaPlayerBee.release();*/
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		mediaPlayerBB.stop();
		mediaPlayerBB.release();
	}

	// Exit program when user press back button.
	// But application still running after exit.
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			System.exit(0); // Exit program.
		}
		return super.onKeyDown(keyCode, event);
	}
	
	AutoFocusCallback x10AutoFocusCallback = new AutoFocusCallback() {
		// The right way, x10AutoFocusCallback and x10AutoFocusCallbackTakePhoto
		// should be combine together but I don't have ability to do that.
		// It should be x10AutoFocusCallback(1 or 2) for selecting those each part.
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			if(success) {
				Toast.makeText(mainActivity.this,"��Ѻ�����´",
						Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	AutoFocusCallback x10AutoFocusCallbackTakePhoto = new AutoFocusCallback() {
		
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			if(success) {
				x10Camera.takePicture(previewCallback, rawCallback, jpegCallback);
			} else {
				// This part will never to reach. I don't know why?
				Toast.makeText(mainActivity.this,"��Ѻ�����´ ��������",
						Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	AutoFocusCallback x10AutoFocusCallbackFirstOpenCamera = new AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			if(success) {
			}
		}
	};
	
	private void CopyDB(InputStream inputStream, OutputStream outputStream)
		throws IOException {
		byte[] buffer = new byte[1024];
		int length;
		while((length = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, length);
		}
		inputStream.close();
		outputStream.flush();
		outputStream.close();
	}
}