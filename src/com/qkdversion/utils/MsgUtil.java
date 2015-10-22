package com.qkdversion.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.net.nntp.NewGroupsOrNewsQuery;

import android.R.integer;

/**
 * 功能：数据类型转换类
 * @author Ljj
 */
public final class MsgUtil {
	/*
	 * 将int型数据拆分成byte数组
	 */
	public static byte[] IntToByte(int n){
		byte[] b=new byte[4];
		b[3]=(byte)(n>>24);
		b[2]=(byte)(n>>16);
		b[1]=(byte)(n>>8);
		b[0]=(byte)n;
		return b;
	}
	/*
	 * 将byte[]型数据合成int型数据
	 * java中移位byte的时候先会将byte装换成int，最终得到的是int型的数据
	 */
	public static int ByteToInt(byte[] b){
		return  b[0]& 0xff|(b[1]&0xff)<<8|(b[2]&0xff)<<16|(b[3]&0xff)<<24;
	}
	/*
	 * 将byte[]型数据转换成short型数据
	 */
	 public static short byteToShort(byte b[]) {
	        return (short) (b[1] & 0xff | (b[0] & 0xff) << 8) ;
	 }
	 /*
	  * 将IP地址转化成字节数组
	  */
	 public static int[] IpToByte(String strIp){
		 int[] ips = new int[4];  
	   //先找到IP地址字符串中.的位置   
        int position1 = strIp.indexOf(".");   
        int position2 = strIp.indexOf(".", position1 + 1);   
        int position3 = strIp.indexOf(".", position2 + 1);   
        //将每个.之间的字符串转换成整型   
        ips[0] = Integer.parseInt(strIp.substring(0, position1));   
        ips[1] = Integer.parseInt(strIp.substring(position1+1, position2));   
        ips[2] = Integer.parseInt(strIp.substring(position2+1, position3));   
        ips[3] = Integer.parseInt(strIp.substring(position3+1));             
        return ips;         
	 }
	 /*
	  * 将字节数组转化成IP
	  */
	 public static String ByteToIp(int[] ips){
		 StringBuilder sb=new StringBuilder();
		 sb.append(ips[0]);
		 sb.append(".");
		 sb.append(ips[1]);
		 sb.append(".");
		 sb.append(ips[2]);
		 sb.append(".");
		 sb.append(ips[3]);
		 return sb.toString();
	 }
	 /*
	  * 获取当前时间
	  */
	 public static String getCurTime(){
		 SimpleDateFormat df=new SimpleDateFormat("HHmmss");
		 return df.format(new Date());
	 }

}
