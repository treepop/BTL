package com.img.jk.beethelion;

public class MatchingLib {
	public static String matchesPath;
	public static int numVec;
	
	static {
		System.loadLibrary("beethelionmatching");
	}
	
	public static native void jkMatching(String strSDcardPath);
}
