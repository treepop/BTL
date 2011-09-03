package com.img.jk.beethelion;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;



import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class ShowTakenPhoto extends Activity implements OnClickListener,
		OnCompletionListener {
	
	GlobalVar gbV;
	MediaPlayer mediaPlayerBB;
	MediaPlayer mediaPlayerBee;
	private ImageView m_takenPhotoView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showtakenphoto);
		
		
		// Connect all objects in layout.
		m_takenPhotoView = (ImageView)findViewById(R.id.takenPhotoView);
		gbV = (GlobalVar)getApplicationContext();
		m_takenPhotoView.setImageBitmap(gbV.getBmpPhoto());
		ImageButton acceptBtn = (ImageButton)findViewById(R.id.acceptBtn);
		acceptBtn.setOnClickListener(this);
		ImageButton rejectBtn = (ImageButton)findViewById(R.id.rejectBtn);
		rejectBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.acceptBtn:
			// Play sound effect click.
			mediaPlayerBB = MediaPlayer.create(this, R.raw.blackberry);
			mediaPlayerBB.start();
			mediaPlayerBee = MediaPlayer.create(this, R.raw.flowerpark_midi);
			mediaPlayerBee.setLooping(true);
			mediaPlayerBee.start();
			
			saveHistoryPhoto();
			
			Toast.makeText(this, "Call matching module.", Toast.LENGTH_SHORT).show();
			long timerStart,timerStop;
			timerStart = System.currentTimeMillis();
			MatchingLib.jkMatching(gbV.getStrBeeDir());
			timerStop = System.currentTimeMillis();
			gbV.setStrFNameResultMatching(MatchingLib.matchesPath);
			
			mediaPlayerBee.stop();
			mediaPlayerBee.release();
			
			Toast.makeText(this, String.format("จำนวน vector = %d", MatchingLib.numVec),
					Toast.LENGTH_SHORT).show();
			Toast.makeText(this, String.format("ใช้เวลาหา  %.2f วินาที",
					(timerStop - timerStart)/1000.0), Toast.LENGTH_LONG).show();
			
			// Read rank photo and give it to vector.
			File rankFile = new File(gbV.getStrFNameResultMatching());
			FileReader fr = null;
			try {
				fr = new FileReader(rankFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			BufferedReader br = new BufferedReader(fr);
			String line;
			try {
				while((line = br.readLine()) != null)
				{
					// Call function to create unique rank of species.
					gbV.addFlowerRank(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				if(br != null)
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// How to get the flower rank.
			// An example code for Karn.
			/*for(String strFlower:gbV.flowerRank) {
				Integer i = Integer.valueOf(strFlower);
				strFlower = strFlower.concat("_1.jpg");
				Toast.makeText(this, i.toString() + "&" + strFlower.toString(),
						Toast.LENGTH_SHORT).show();
			}*/
			
			// show matching flower
			Intent i = new Intent(this,GardenActivity.class);
			startActivity(i);
			
			this.finish();
//			startActivity(new Intent(
//	                this,
//	                GardenActivity.class));
			break;
			
		case R.id.rejectBtn:
			// Play sound effect click.
			mediaPlayerBB = MediaPlayer.create(this, R.raw.blackberry);
			mediaPlayerBB.start();
			
			this.finish();
			break;
			
		default:
			break;
		}
		Drawable toRecycle= m_takenPhotoView.getDrawable(); 
        if (toRecycle != null) 
        {     
        	((BitmapDrawable)m_takenPhotoView.getDrawable()).getBitmap().recycle(); 
        } 
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
//		How to distinct each mediaPlayer?
//		if(mp == mediaPlayerBB) {
			mediaPlayerBB.stop();
			mediaPlayerBB.release();
//		}		
	}
	
	private void saveHistoryPhoto() {
	// Save flower photo for research further.
		// Obtain running number for naming UNKNOWN_FLOWER.
		// For instance, unknownFlower0001.jpg.
		// ================================================
		File runningNumFile = new File(gbV.getStrBeeDir() +
				gbV.getStrRunningNumberFile());
		Integer runningNum = 0;
		if(runningNumFile.exists()) {
			// Read running number then increase its value with 1.
			// Finally write it back.
			FileReader fr = null;
			try {
				fr = new FileReader(runningNumFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			BufferedReader br = new BufferedReader(fr);
			String line;
			try {
				line = br.readLine();
				runningNum = Integer.valueOf(line);
				runningNum++;
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				if(br != null)
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// After increase its value with 1 then write it back.
			try {
				runningNumFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(runningNumFile.exists() &&
					runningNumFile.canWrite()) {
				FileWriter fw = null;
				try {
					fw = new FileWriter(runningNumFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				BufferedWriter bw = new BufferedWriter(fw);
				try {
					bw.write(runningNum.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
				finally {
					if(bw != null)
					try {
						bw.flush();
						bw.close();
						bw = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			try {
				runningNumFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(runningNumFile.exists() &&
					runningNumFile.canWrite()) {
				FileWriter fw = null;
				try {
					fw = new FileWriter(runningNumFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				BufferedWriter bw = new BufferedWriter(fw);
				try {
					runningNum++;
					bw.write(runningNum.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
				finally {
					if(bw != null)
					try {
						bw.flush();
						bw.close();
						bw = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		// ================================================
		
		// Write unknownFlower0001.jpg for keeping it permanently.
		File historyDir = new File(gbV.getStrBeeDir() + gbV.getStrHistoryPhotoDir());
		if(!historyDir.exists()) {
			historyDir.mkdir();
		}
		File fileUnknownFlowerNum = new File(
				historyDir.getAbsolutePath() + "/" + gbV.getStrUnknownFlower() +
				String.format("%04d", runningNum) + ".jpg");
		try {
			fileUnknownFlowerNum.createNewFile();
		} catch(IOException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			Log.e("jkError", e.getMessage());
		}
		if(fileUnknownFlowerNum.exists() && fileUnknownFlowerNum.canWrite()) {
			BufferedOutputStream fosNum = null;
			try {
				fosNum = new BufferedOutputStream(
						new FileOutputStream(fileUnknownFlowerNum));
				Bitmap rotatedBmp = gbV.getBmpPhoto();
				rotatedBmp.compress(CompressFormat.JPEG, 100, fosNum);
				rotatedBmp.recycle();
				rotatedBmp = null;
			} catch(FileNotFoundException e) {
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
				Log.e("jkError", e.getMessage());
			} finally {
				if(fosNum != null) {
					try {
						fosNum.flush();
						fosNum.close();
						fosNum = null;
					} catch (IOException e) {
						Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
						Log.e("jkError", e.getMessage());
					}
				}
			}
		}
	}
}