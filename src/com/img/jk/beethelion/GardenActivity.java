/*
 * To change this template, choose Tools | Templates
 * && open the template in the editor.
 */
package com.img.jk.beethelion;


import java.io.File;
import java.util.Iterator;


import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * <p>
 * </p><p>
 * Created on 10 Aug 2010
 * </p>
 *
 * @author Jason Morris
 */
public class GardenActivity extends Activity implements
AdapterView.OnItemClickListener,  View.OnClickListener  {
	private GridView m_flowerList;
    private ViewGroup m_container;
    private LinearLayout m_flowerLayout;
    private ImageView m_imageView;
    private FlowerAdapter m_flowerAdapt;
    private TextView m_textView;
    //database variable
    private static String[] FROM = { "name","sciname","generalInfo", };
    public static final String TABLE_NAME = "FlowerInfo";
    private SQLiteDatabase m_dataBase;
    String m_strFlowerName; //get name from current cursor
    private int m_prePosition;
    
    GlobalVar gbV;
    MediaPlayer m_mediaPlayerFlowerPark;

    @Override
    protected void onCreate(final Bundle istate) {
        super.onCreate(istate);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE); // Don't show the title bar.

        setContentView(R.layout.garden);

        //initail position click
        m_prePosition = -1;
        gbV = (GlobalVar)getApplicationContext();
        m_flowerList = (GridView) findViewById(android.R.id.list);
       m_container = (ViewGroup) findViewById(R.id.container);
       m_imageView = (ImageView) findViewById(R.id.picture);
       m_flowerLayout = (LinearLayout) findViewById(R.id.infoLayout);
       m_textView = (TextView) findViewById(R.id.flowerInfo);
      
       
       //get correct path of sd card
       File sdDir = Environment.getExternalStorageDirectory();
       String strPath = sdDir.getAbsolutePath()+"/beeTheLion/";
       String strDBPath = strPath + "beethelion.db"; //database path
       int flowerID;
       //connect database
       m_dataBase = SQLiteDatabase.openDatabase(strDBPath, null, SQLiteDatabase.OPEN_READONLY|
    	       SQLiteDatabase.NO_LOCALIZED_COLLATORS); 
       
    // How to get the flower rank. // Hang this line.
       String strFlowerDBPath= gbV.getStrflowerPicDBDir();
       String strUnkFlowerPath = gbV.getStrBeeDir();
       //หากไม่มีข้อมูล ใน database
       String strTmpInfo;
       strTmpInfo = getString(R.string.flowerInfo);
//       CharSequence strInfo;
//       strInfo =  getText(R.string.flowerInfo);
       
		String strFlower;
		String strInfo;
		final int dw; // width display
		final int dh; // height display
        Display currentDisplay = getWindowManager().getDefaultDisplay();
		dw = currentDisplay.getWidth();
		dh = (int) Math.floor(currentDisplay.getHeight()/2);
		
		// Prepare the ListView flower       
		m_flowerAdapt = new FlowerAdapter();
		m_flowerAdapt.add(new FlowerItem("ดอกอะไร?",strUnkFlowerPath + "/unknownFlower.jpg",0,"",dw,dh));
        
        int numflower = gbV.flowerRank.size()>17?17:gbV.flowerRank.size();
	    Iterator<String> iter=gbV.flowerRank.iterator();
		for(int idx=0;idx<numflower;idx++)
		{
			strFlower=(String) iter.next();
			//get flower id
			flowerID = Integer.valueOf(strFlower.substring(0, strFlower.indexOf('_')));  
			// Toast.makeText(this, strFlower.toString(), Toast.LENGTH_SHORT).show();
			 Cursor cursor = getFlower(flowerID); 
		     strInfo = showEvents(cursor); 
		     if(strInfo.length()==0)
		    	 m_flowerAdapt.add(new FlowerItem(m_strFlowerName,strFlowerDBPath+ strFlower,flowerID,strTmpInfo,dw,dh));
		     else
		    	 m_flowerAdapt.add(new FlowerItem(m_strFlowerName,strFlowerDBPath+ strFlower,flowerID,strInfo,dw,dh));
		      
		}
		
		 m_dataBase.close();
		         
//		view.setAdapter(flowerAdapt);
//		setContentView(view);
//
		m_flowerList.setAdapter(m_flowerAdapt);
		m_flowerList.setOnItemClickListener(this);
       
       // Prepare the flower information
       m_flowerLayout.setClickable(true);
       m_flowerLayout.setFocusable(true);
        m_flowerLayout.setOnClickListener(this);
       
      
        // Since we are caching large views, we want to keep their cache
        // between each animation
        
        m_container.setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);
        applyInfoRotation(-1, 180, 90);
//		Log.d("Test","clicked on " + i);
       
        // Play flower park song.
        m_mediaPlayerFlowerPark = MediaPlayer.create(this, R.raw.flowerpark);
        m_mediaPlayerFlowerPark.setLooping(true);
        m_mediaPlayerFlowerPark.start();
    }
    
    @Override
    protected void onDestroy()
    {
        Log.e("JK","onDestoy  GardenActivity start");
        //start to destroy bitmap in Grid Adapter
        //first need to get viewgroup. 2 get id image from such viewgroup
        //3 get Drawable from imageview and recycle;
//    	 int idx;
//    	 for(idx=0;idx<m_flowerList.getCount();idx++)
//    	 {
//    	 ViewGroup myview = (ViewGroup) m_flowerAdapt.getView(idx,m_flowerList.getChildAt(idx), m_flowerList);
//    	 final ImageView image = ((ImageView)myview.findViewById(R.id.icon));
//    	
//	         Drawable toRecycle= image.getDrawable(); 
//	         if (toRecycle != null) 
//	         {     
//	         	((BitmapDrawable)image.getDrawable()).getBitmap().recycle(); 
//	          	Log.e("karnx", "onDestroy Garden clear gridview "+idx );
//
//	         } 
//    	 }
        
       	 m_flowerAdapt.clear();
    	 m_flowerAdapt = null;
    	 
    	 m_mediaPlayerFlowerPark.stop();
 		m_mediaPlayerFlowerPark.release();
    	 Log.e("JK","onDestoy  GardenActivity end");
    	 super.onDestroy();
    }
    //-----------------------------
    //Name:getFlower
    //Desc: query flower information from database
    //-----------------------------
    private Cursor getFlower(int id) {
        
       Cursor cursor = m_dataBase.query(TABLE_NAME, FROM,"id=" + id, null, null, null, null);
        //Cursor cursor = m_dataBase.rawQuery( "select name, generalInfo from InfoFlower ", null ); 
       if (cursor.getCount() < 1)
       {
    	   Toast.makeText(this, "flower is empty",
    	   Toast.LENGTH_LONG).show();
    	   return null;
       }
       startManagingCursor(cursor);
       return cursor;
     }
    private String showEvents(Cursor cursor) {
        // Stuff them all into a big string
        StringBuilder builder = new StringBuilder();
        while (cursor.moveToNext()) { 
           // Could use getColumnIndexOrThrow() to get indexes
           m_strFlowerName = cursor.getString(0); 
           String sciname = cursor.getString(1);
           String genInfo = cursor.getString(2);
           builder.append("<b>ชื่อทั่วไป:</b>").append(m_strFlowerName); 
           builder.append("<br><b>ชื่อวิทยาศาสตร์:</b><i>").append(sciname).append("</i>"); 
           builder.append("<br><b>ข้อมูลทั่วไป:</b>").append(genInfo);
        }
        cursor.close();
        // return str 
        return builder.toString();
        
     }
    @Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
		// TODO Auto-generated method stub
		if(position != 0  )
		{
			if( m_prePosition != position)
			{
				FlowerItem item  = (FlowerItem) m_flowerAdapt.getItem(position);
				m_textView.setText(Html.fromHtml(item.m_info));
				m_imageView.setImageBitmap(item.m_bmp);
				Log.e("itemclick","pos:"+ position);
			}
			applyRotation(position, 0, 90);
		}
		m_prePosition = position;
		//return true;
	}
    @Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		 applyRotation(-1, 360, 270);
	}
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    if(keyCode == KeyEvent.KEYCODE_BACK) {
    //Implement a custom back function//Return true to consume the event
    	if(m_flowerList.getVisibility()== View.GONE)
    	{
    	  this.onClick(m_flowerLayout);
    	  return true;
    	}
    }
    // Clear global variable for next taking photo.
    gbV.clearAll();
    finish();
    //Pass other events along their way up the chain
    return super.onKeyDown(keyCode, event);
    }   
  
    /**
     * Setup a new 3D rotation on the container view.
     *
     * @param position the item that was clicked to show a picture, or -1 to show the list
     * @param start the start angle at which the rotation must begin
     * @param end the end angle of the rotation
     */
    private void applyRotation(int position, float start, float end) {
        // Find the center of the container
        final float centerX = m_container.getWidth() / 2.0f;
        final float centerY = m_container.getHeight() / 2.0f;

        // Create a new 3D rotation with the supplied parameter
        // The animation listener is used to trigger the next animation
        final Rotate3dAnimation rotation =
                new Rotate3dAnimation(start, end, centerX, centerY, 310.0f, true);
        rotation.setDuration(500);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new DisplayNextView(position));

        m_container.startAnimation(rotation);
    }
    private void applyInfoRotation(int position, float start, float end) {
        // Find the center of the container
        final float centerX = m_container.getWidth() / 2.0f;
        final float centerY = m_container.getHeight() / 2.0f;

        // Create a new 3D rotation with the supplied parameter
        // The animation listener is used to trigger the next animation
        final Rotate3dAnimation rotation =
                new Rotate3dAnimation(start, end, centerX, centerY, 310.0f, true);
       
        //rotation.setFillAfter(true);
        //rotation.setInterpolator(new AccelerateInterpolator());
        //rotation.setAnimationListener(new DisplayNextView(position));

        m_flowerLayout.startAnimation(rotation);
    }
    /**
     * This class listens for the end of the first half of the animation.
     * It then posts a new action that effectively swaps the views when the container
     * is rotated 90 degrees and thus invisible.
     */
    private final class DisplayNextView implements Animation.AnimationListener {
        private final int mPosition;

        private DisplayNextView(int position) {
            mPosition = position;
        }

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            m_container.post(new SwapViews(mPosition));
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }

    /**
     * This class is responsible for swapping the views and start the second
     * half of the animation.
     */
    private final class SwapViews implements Runnable {
        private final int mPosition;

        public SwapViews(int position) {
            mPosition = position;
        }

        public void run() {
            final float centerX = m_container.getWidth() / 2.0f;
            final float centerY = m_container.getHeight() / 2.0f;
            Rotate3dAnimation rotation;
            
            if (mPosition > -1) {
                m_flowerList.setVisibility(View.GONE);
                m_flowerLayout.setVisibility(View.VISIBLE);
                m_flowerLayout.requestFocus();

                rotation = new Rotate3dAnimation(270, 360, centerX, centerY, 310.0f, false);
              
            } else {
                m_flowerLayout.setVisibility(View.GONE);
                m_flowerList.setVisibility(View.VISIBLE);
                m_flowerList.requestFocus();

                rotation = new Rotate3dAnimation(90, 0, centerX, centerY, 310.0f, false);
            }

            rotation.setDuration(500);
            rotation.setFillAfter(true);
            rotation.setInterpolator(new DecelerateInterpolator());

            m_container.startAnimation(rotation);
        }
    }

	@Override
	protected void onStop() {
		super.onStop();
		m_mediaPlayerFlowerPark.pause();//.stop();
		//m_mediaPlayerFlowerPark.release();
	}
	@Override
	protected void onRestart() {
		super.onRestart();
		m_mediaPlayerFlowerPark.start();
		
	}
}
