// A way to create global variable technique from web site
// http://stackoverflow.com/questions/708012/android-how-to-declare-global-variables

package com.img.jk.beethelion;

import java.util.LinkedHashSet;
import java.util.Set;

import android.app.Application;
import android.graphics.Bitmap;

public class GlobalVar extends Application {
	
	private Bitmap bmpPhoto;
	private String strBeeDir; // Ex. /mnt/sdcard/beeTheLion/
	private String strflowerPicDBDir; // Ex. /mnt/sdcard/beeTheLion/flowerPicDB/
	private String strHistoryPhotoDir; // Ex. historyPhoto
	private String strFNameResultMatching; // Ex. /mnt/sdcard/beeTheLion/Matches.txt
	private String strUnknownFlower; // Ex. unknownFlower
	private String strRunningNumberFile; // Ex. runningNumber.txt
	public Set<String> flowerRank = new LinkedHashSet<String>();
	private Set<String> cacheFlowerRank = new LinkedHashSet<String>();

	public Bitmap getBmpPhoto() {
		return bmpPhoto;
	}
	
	public void setBmpPhoto(Bitmap bmpPhoto) {
		this.bmpPhoto = bmpPhoto;
	}

	// -------------------------------------
	
	public String getStrBeeDir() {
		return strBeeDir;
	}

	public void setStrBeeDir(String strBeeDir) {
		this.strBeeDir = strBeeDir;
		this.strflowerPicDBDir = strBeeDir + "flowerPicDB/";
	}

	// -------------------------------------
	
	public String getStrflowerPicDBDir() {
		return strflowerPicDBDir;
	}

	// -------------------------------------
	
	public String getStrFNameResultMatching() {
		return strFNameResultMatching;
	}

	public void setStrFNameResultMatching(String strFNameResultMatching) {
		this.strFNameResultMatching = strFNameResultMatching;
	}

	// -------------------------------------
	
	public void addFlowerRank(String strFlower) {
		if(!cacheFlowerRank.contains(strFlower.substring(0, strFlower.indexOf('_')))) {
			cacheFlowerRank.add(strFlower.substring(0, strFlower.indexOf('_')));
			flowerRank.add(strFlower);
		}
	}
	
	// -------------------------------------
	
	public void clearAll() {
		bmpPhoto.recycle();
		bmpPhoto = null;
		flowerRank.clear();
		cacheFlowerRank.clear();
	}
	
	// -------------------------------------

	public String getStrHistoryPhotoDir() {
		return strHistoryPhotoDir;
	}

	public void setStrHistoryPhotoDir(String strHistoryPhotoDir) {
		this.strHistoryPhotoDir = strHistoryPhotoDir;
	}

	// -------------------------------------
	
	public String getStrUnknownFlower() {
		return strUnknownFlower;
	}

	public void setStrUnknownFlower(String strUnknownFlower) {
		this.strUnknownFlower = strUnknownFlower;
	}
	
	// -------------------------------------

	public String getStrRunningNumberFile() {
		return strRunningNumberFile;
	}

	public void setStrRunningNumberFile(String strRunningNumberFile) {
		this.strRunningNumberFile = strRunningNumberFile;
	}
}