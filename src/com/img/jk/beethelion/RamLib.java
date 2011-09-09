package com.img.jk.beethelion;

import android.util.Log;

public class RamLib {
	public static void ramStatus() {
		Log.w("JK", String.format("FreeRam = %,d/%,d", Runtime.getRuntime().freeMemory()
				, Runtime.getRuntime().totalMemory()));
	}
}
