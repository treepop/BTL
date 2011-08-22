/*
 * To change this template, choose Tools | Templates
 * && open the template in the editor.
 */
package com.img.jk.beethelion;

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
    final String mUri;
    final String m_info;
//    FruitItem(String name, int image, String uri) {
    FlowerItem(String name, String uri,int id,String info) {
        this.name = name;
       // this.image = image;
        this.mUri = uri;
        this.m_id = id;
        this.m_info = info;
    }

}
