package com.qkdversion.oxcDao;

import android.util.Log;

import com.qkdversion.OXCDomian.OXCAckPara;
import com.qkdversion.OXCDomian.OXCSetupPara;
/*
 * ��OXC����ͨ���߳�
 */
import com.qkdversion.oxcService.OXCService;

public class OXCSetupThread implements Runnable{

	private static final String TAG = "OXCSetupThread";
	@Override
	public void run() {
		// TODO Auto-generated method stub
		synchronized (Thread.class) {
//			//����һ��OXC������
//			OXCSetupCl oxcCl=new OXCSetupCl();
//			//�ж�OXC�Ƿ��������������������0������������1
//		    int isConnect=oxcCl.connectToTelnet(OXCSetupPara.OXC_2_IP);
//			Log.i(TAG, String.valueOf(isConnect));
//			if(isConnect==OXCAckPara.CONNECT_OK){ 
//		    //�������������ָ���
//			oxcCl.sendCommend("SET 1 2 4 3 5 6 7 8"); 
//			//��ȡӦ���ź�
//			String ackMsg=oxcCl.getOXCAckMsg();
//			//�ر���
//		    oxcCl.close();
//		    //�Ͽ�����
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
				//֪ͨ���ܣ����Ӳ���	
		//	}
		}   
	}
}
