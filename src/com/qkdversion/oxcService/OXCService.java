package com.qkdversion.oxcService;


import android.util.Log;


import com.qkdversion.OXCDomian.OXCSetupPara;
import com.qkdversion.msgDomain.Ack;
import com.qkdversion.oxcDao.OXCSetupCl;

/**
 * ����ģʽ�ṩOXC����
 * @author hjj
 *
 */
public final class OXCService {
	 private static final String TAG = "OXCService";
	 private static OXCService oxcService;
	 private OXCService(){
		  
	  } 
	 public static OXCService getOXCService(){
		 if(oxcService==null){
			 oxcService=new OXCService();
		 }
		return oxcService;
	 }
	 /**
	  * ����OXC����
	  * OXC��ӦIP
	  */
	public int  oxcSwitch(OXCSetupCl oxcCl,String oxc_IP,String comm){
		int ack=0;
		oxcCl.sendCommend(comm); //�������������ָ���
		String ackMsg=oxcCl.getOXCAckMsg();//��ȡӦ���ź�
		if (ackMsg.contains(comm)) {
			 Log.i(TAG, ackMsg);
			 ack=getAckFromIp(oxc_IP,0);
			return ack;
		}else{//һ�β��ɹ������ٷ�һ��
			oxcCl.sendCommend(comm); //�������������ָ���
			ackMsg=oxcCl.getOXCAckMsg();//��ȡӦ���ź�
			if (ackMsg.contains(comm)) {
				 Log.i(TAG, ackMsg);
				 ack=getAckFromIp(oxc_IP,0);
				return ack;
			}
		}
		ack=getAckFromIp(oxc_IP,1);
		return ack;
	}
	/**
	 * ����IP�ж����ĸ��⿪��������
	 **/
	private  int getAckFromIp(String oxc_IP,int type){
		if(OXCSetupPara.OXC_1_IP.equals(oxc_IP)){
			if(type==0){
				return Ack.OXCSETUP_1_OK;
			}else{
				return Ack.OXCSETUP_1_ERR;
			}
		}else if(OXCSetupPara.OXC_2_IP.equals(oxc_IP)){
			if(type==0){
				return Ack.OXCSETUP_2_OK;
			}else{
				return Ack.OXCSETUP_2_ERR;
			}
		}else if(OXCSetupPara.OXC_3_IP.equals(oxc_IP)){
			if(type==0){
				return Ack.OXCSETUP_3_OK;
			}else{
				return Ack.OXCSETUP_3_ERR;
			}
		}
		return 0;
	}
	/**
	 * �Ͽ�telnet
	 */
	public void closeTelnet(OXCSetupCl oxcCl){
		oxcCl.disConnectToTelnet();
	}
}
