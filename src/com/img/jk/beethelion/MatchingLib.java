package com.img.jk.beethelion;

public class MatchingLib {
	static {
		System.loadLibrary("beethelionmatching");
	}
	public static native String jkMatching(String strSDcardPath);
}
