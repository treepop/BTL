// A way to create global variable technique from web site
// http://stackoverflow.com/questions/708012/android-how-to-declare-global-variables

package com.img.jk.beethelion;

import java.util.LinkedHashSet;
import java.util.Set;

import android.app.Application;
import android.graphics.Bitmap;

public class GlobalVar extends Application {
	
	private Bitmap bmpPhoto;
	private String strBeeDir;
	private String strflowerPicDBDir;
	private String strFNameResultMatching;
	public Set<String> flowerRank = new LinkedHashSet<String>();

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
		flowerRank.add(strFlower.substring(0, strFlower.indexOf('_')));
	}
}