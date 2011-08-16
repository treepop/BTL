package com.img.jk.beethelion;

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
			break;
		case R.id.rejectBtn:
			this.finish();
			break;
		default:
			break;
		}
	}
}