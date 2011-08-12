// A way to create global variable technique from
// http://stackoverflow.com/questions/708012/android-how-to-declare-global-variables

package com.img.jk.beethelion;

import android.app.Application;
import android.graphics.Bitmap;

public class GlobalVar extends Application {
	private Bitmap bmpPhoto;

	public Bitmap getBmpPhoto() {
		return bmpPhoto;
	}

	public void setBmpPhoto(Bitmap bmpPhoto) {
		this.bmpPhoto = bmpPhoto;
	}
}