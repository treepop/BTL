package com.img.jk.beethelion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class ShowTakenPhoto extends Activity implements OnClickListener {
	
	GlobalVar gbV;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showtakenphoto);
		
		// Connect all objects in layout.
		ImageView takenPhotoView = (ImageView)findViewById(R.id.takenPhotoView);
		gbV = (GlobalVar)getApplicationContext();
		takenPhotoView.setImageBitmap(gbV.getBmpPhoto());
		ImageButton acceptBtn = (ImageButton)findViewById(R.id.acceptBtn);
		acceptBtn.setOnClickListener(this);
		ImageButton rejectBtn = (ImageButton)findViewById(R.id.rejectBtn);
		rejectBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.acceptBtn:
			Toast.makeText(this, "Call matching module.", Toast.LENGTH_SHORT).show();
			long timerStart,timerStop;
			timerStart = System.currentTimeMillis();
			gbV.setStrFNameResultMatching(MatchingLib.jkMatching(gbV.getStrBeeDir()));
			timerStop = System.currentTimeMillis();
			Toast.makeText(this, String.format("Used time = %f sec",
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
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// How to get the flower rank.
			// An example code for Karn.
			for(String strFlower:gbV.flowerRank) {
				strFlower = strFlower.concat("-1.jpg");
				Toast.makeText(this, strFlower.toString(), Toast.LENGTH_SHORT).show();
			}
			
			break;
		case R.id.rejectBtn:
			this.finish();
			break;
		default:
			break;
		}
	}
}