package com.qkdversion.oxcDao;

import android.util.Log;

import com.qkdversion.OXCDomian.OXCAckPara;
import com.qkdversion.OXCDomian.OXCSetupPara;
/*
 * 与OXC进行通信线程
 */
import com.qkdversion.oxcService.OXCService;

public class OXCSetupThread implements Runnable{

	private static final String TAG = "OXCSetupThread";
	@Override
	public void run() {
		// TODO Auto-generated method stub
		synchronized (Thread.class) {
//			//创建一个OXC控制器
//			OXCSetupCl oxcCl=new OXCSetupCl();
//			//判断OXC是否工作正常，如果正常返回0，不正常返回1
//		    int isConnect=oxcCl.connectToTelnet(OXCSetupPara.OXC_2_IP);
//			Log.i(TAG, String.valueOf(isConnect));
//			if(isConnect==OXCAckPara.CONNECT_OK){ 
//		    //如果正常，进行指令发送
//			oxcCl.sendCommend("SET 1 2 4 3 5 6 7 8"); 
//			//读取应答信号
//			String ackMsg=oxcCl.getOXCAckMsg();
//			//关闭流
//		    oxcCl.close();
//		    //断开连接
//		    oxcCl.disConnectToTelnet(); 
//		    Log.i(TAG, ackMsg)
			OXCSetupCl oxcCl=new OXCSetupCl();
			int isConnect=oxcCl.connectToTelnet(OXCSetupPara.OXC_2_IP);
			OXCSetupCl oxcCl1=new OXCSetupCl();
			int isConnect1=oxcCl1.connectToTelnet(OXCSetupPara.OXC_1_IP);
		    while(true){
			//String oxcAck=OXCService.getOXCService().oxcSwitch(oxcCl,OXCSetupPara.OXC_2_IP,"IP");
			//Log.i(TAG,oxcAck+"*********");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
			//oxcAck=OXCService.getOXCService().oxcSwitch(oxcCl1,OXCSetupPara.OXC_1_IP,"POS");
			//Log.i(TAG,oxcAck+"***********2"); 
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    }
			//}else{
				//通知网管，连接不上	
		//	}
		}   
	}
}
