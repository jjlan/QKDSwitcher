package com.qkdversion.view;


import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.security.auth.PrivateCredentialPermission;

import com.example.qkdverson1.R;
import com.qkdversion.constant.ServerConstant;
import com.qkdversion.oxcdao.OXCSetupThread;
import com.qkdversion.service.QkdService;
import com.qkdversion.socketdao.clientThread;
import com.topeet.serialtest.serial;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {
	protected static final String TAG = "MainActivity1";
	private TextView ackView;
	static {
        System.loadLibrary("serialtest");
	}
	/*
	 * ���߳�handler�����ں����߳̽����������߳�UI
	 */
	private Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what==0){
				ackView.append(msg.obj.toString());
    		}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ackView=(TextView) findViewById(R.id.ackView);
		Log.i(TAG,"����MainAActivity");
		//����OXC���߳�
	//	OXCSetupThread oxcSetupThread=new OXCSetupThread();
	//	Thread oxcThread=new Thread(oxcSetupThread);
	//	oxcThread.start();
	//  	testSerial();
	    socketServer();
		Log.i(TAG,"ִ�е�MainAActivity");
   }
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ackView.setText("");
		Intent intent=new Intent(this,QkdService.class);
		stopService(intent);
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	//���ڲ���
/*	private void testSerial(){
		Intent intent=new Intent(MainActivity.this,SerialActivity.class);
		startActivity(intent);
	}*/
	/*
	 * ���������ڷ����п������߳�
	 */
	private void socketServer(){
	  //new Thread(new clientThread()).start();
		Log.i("TAG","socketServer��������");
	 Intent intent=new Intent(this,QkdService.class);
	  startService(intent);
	}
}
