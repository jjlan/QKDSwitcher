package com.topeet.serialtest;

import android.R.integer;

//����JNI���õײ㴮��
public class serial {
	//����һ���˿����ֱ�־��flag=0��flag=1;
	public native int 	Open(int Port,int Rate,int flag);
	public native int 	Close(int flag);
	public native int[]	Read(int flag);
	public native int	Write(int[] buffer,int len,int flag);

} 
 