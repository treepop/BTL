package com.img.jk.beethelion;

import android.util.Log;

public class RamLib {
	// Use this command where ever you want to display ram.
	// RamLib.ramStatus();
	// Another JAVA function for display ram is Runtime.getRuntime().maxMemory();    
	public static void ramStatus() {
		Log.w("JK", String.format("FreeRam = %,d/%,d\nMaxRam = %,d", Runtime.getRuntime().freeMemory()
				, Runtime.getRuntime().totalMemory()
				, Runtime.getRuntime().maxMemory()));
	}
}
