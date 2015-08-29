package com.qkdversion.oxcService;


import android.util.Log;


import com.qkdversion.OXCDomian.OXCSetupPara;
import com.qkdversion.msgDomain.Ack;
import com.qkdversion.oxcDao.OXCSetupCl;

/**
 * 单例模式提供OXC服务
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
	  * 进行OXC交换
	  * OXC对应IP
	  */
	public int  oxcSwitch(OXCSetupCl oxcCl,String oxc_IP,String comm){
		int ack=0;
		oxcCl.sendCommend(comm); //如果正常，进行指令发送
		String ackMsg=oxcCl.getOXCAckMsg();//读取应答信号
		if (ackMsg.contains(comm)) {
			 Log.i(TAG, ackMsg);
			 ack=getAckFromIp(oxc_IP,0);
			return ack;
		}else{//一次不成功，则再发一次
			oxcCl.sendCommend(comm); //如果正常，进行指令发送
			ackMsg=oxcCl.getOXCAckMsg();//读取应答信号
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
	 * 根据IP判断是哪个光开关有问题
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
	 * 断开telnet
	 */
	public void closeTelnet(OXCSetupCl oxcCl){
		oxcCl.disConnectToTelnet();
	}
}
