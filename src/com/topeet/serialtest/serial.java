package com.topeet.serialtest;

import android.R.integer;

//利用JNI调用底层串口
public class serial {
	//新增一个端口区分标志，flag=0，flag=1;
	public native int 	Open(int Port,int Rate,int flag);
	public native int 	Close(int flag);
	public native int[]	Read(int flag);
	public native int	Write(int[] buffer,int len,int flag);

} 
 