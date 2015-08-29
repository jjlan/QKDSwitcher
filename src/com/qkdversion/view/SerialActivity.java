package com.qkdversion.view;
import android.R.integer;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.qkdverson1.R;
import com.qkdversion.OXCDomian.OXCAckPara;
import com.qkdversion.WSSDomian.WSSCmdPara;
import com.qkdversion.oxcDao.OXCSetupCl;
import com.qkdversion.wssDao.WSSSetupCl;
import com.topeet.serialtest.Com;
import com.topeet.serialtest.serial;

public class SerialActivity extends Activity {
	static {
        System.loadLibrary("serialtest");
	}
	/****************************************/
	String rxIdCode = "";
	String tag = "serialtest";
	private EditText ET1;
	private Button RECV;
	private Button SEND;
	private serial com3;
	/****************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.serialtest);
		//********************************************//*
		ET1 = (EditText)findViewById(R.id.edit1);
        RECV = (Button)findViewById(R.id.recv1);
        SEND = (Button)findViewById(R.id.send1);
        RECV.setOnClickListener(new manager());             
        com3 = new serial();
		 com3.Open(3, 115200,Com.WSS1);
        SEND.setOnClickListener(new manager());
		//*********************************************//*
	}
	/***********************************************/
	 class manager implements OnClickListener{
		private static final String TAG = "manager";

		public void onClick(View v) {
			String rxIdCode = "";
			String str;
			int i;
			switch (v.getId()) {
			//recvive
			case R.id.recv1:
				break;
			//send
			case R.id.send1:
		        
                break;				
			}
		}
	}
	
		@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
			com3.Close(Com.WSS1);
		}

}
