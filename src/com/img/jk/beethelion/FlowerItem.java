/*
 * To change this template, choose Tools | Templates
 * && open the template in the editor.
 */
package com.img.jk.beethelion;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * <p>
 * </p><p>
 * Created on 11 Aug 2010
 * </p>
 *
 * @author Jason Morris
 */
class FlowerItem {

    final String name;
    final int m_id;	
  //  final int image;
    final String m_info;
    Bitmap m_bmp;
   
//    FruitItem(String name, int image, String uri) {
//------------------------------------------------------
//uri	path image file name
//dw   Display width
//dh   Display height
//------------------------------------------------------
    FlowerItem(String name, String uri,int id,String info,int dw,int dh) {
        this.name = name;
       // this.image = image;
        this.m_id = id;
        this.m_info = info;
        
        //Load up the image's dimension not the image itself
        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        this.m_bmp = BitmapFactory.decodeFile(uri, bmpFactoryOptions);
        int heightRatio = (int)Math.ceil(bmpFactoryOptions.outHeight/(float)dh);
        int widthRatio = (int)Math.ceil(bmpFactoryOptions.outWidth/(float)dw);
        
        //If both the ratios are grater than 1,
        //one of the sides of the image is greater than the screen
        if(heightRatio > 1 && widthRatio > 1)
        {
        	if(heightRatio > widthRatio)
        	{
        		//Height ratio is larger,scale according to it
        		bmpFactoryOptions.inSampleSize = widthRatio;        		
        	}
        }
        //Decode it for real
        bmpFactoryOptions.inJustDecodeBounds = false;
        this.m_bmp = BitmapFactory.decodeFile(uri, bmpFactoryOptions);
        // Joe found this line still error.
        // java.lang.OutOfMemoryError: bitmap size exceeds VM budget.
    }

}
