package com.qkdversion.msgDao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set; 
import com.qkdversion.msgDomain.AckMsg;
import com.qkdversion.msgDomain.Msg;
import com.qkdversion.msgDomain.MsgConstant;
import com.qkdversion.msgDomain.UserMsg;
import com.qkdversion.wssDao.WSSSetupCl;

public class BusyDao {
	private static BusyDao instance = new BusyDao();

	private BusyDao() {
	}

	public static BusyDao getInstance() {
		return instance;
	}
	//�����·ռ�����
	public boolean isLinkBusy(ArrayList<Msg> msgList, ArrayList<AckMsg> linkList,Msg msg) {
		if(msgList.size()==0||linkList.size()==0){//��·��æ���״μ���
			return false;
		}
		if(isWaveDulp(msg)){//�������ظ�,�򷵻�true
			return true;
		}
		 //�ȼ����˼��Ƿ��ͻ
		if (msg.getFlag()==MsgConstant.ONE) {//�������Ǵ���·����
			if(upPath(msgList,linkList,msg))
				return true;
		}else if(msg.getFlag()==MsgConstant.TWO){//�������Ǵ���·����
			 if(downPath(msgList,linkList,msg))
				return true;
		}	 
		 return false;
	}
	private boolean upPath(ArrayList<Msg> msgList, ArrayList<AckMsg> linkList,Msg msg){
	
	    int type=msg.getSwitchType();
	    Set<String> fiberSet=getFiberAck(linkList);
	    switch(type){
	    case MsgConstant.FIBER:
	    	for(Msg msgTmp :msgList){//������ڲ��������������߼����������ܽ���
	    	    if(msgTmp.getSwitchType()==MsgConstant.WAVEBAND){
	    	    	return true;
	    	    }
	    	}
	    	if(msg.getFiberIp()==MsgConstant.IP1){//����ǵ�Ŀ��IP1
	    		if(fiberSet.contains("33")||fiberSet.contains("23")||fiberSet.contains("14")||fiberSet.contains("11")){
	    			return true;
	    		}
	    	}else if(msg.getFiberIp()==MsgConstant.IP2){//����������Ŀ��IP2
	    		if(fiberSet.contains("44")||fiberSet.contains("24")){
	    			return true;
	    		}
	    	}
	    	break;
	    case MsgConstant.WAVEBAND:
	    	if(fiberSet.contains("13")||fiberSet.contains("14")){//��·���ڹ��˼����������ܽ��зַ�
	    		return true;
	    	}
	    	break;
	    case MsgConstant.WAVELENGTH:
	    	if(fiberSet.contains("13")||fiberSet.contains("14")){//��·���ڹ��˼����������ܽ��зַ�
	    		return true;
	    	}
	    	Set<String> bandSet=getBandAck(linkList);
	    	if(bandSet.contains("37")||bandSet.contains("67")){//�Ѿ��в�������·����
	    		return true;
	    	}
	    	break;
	    }
		return  false;
		
	}
    private boolean downPath(ArrayList<Msg> msgList, ArrayList<AckMsg> linkList,Msg msg){
    	int type=msg.getSwitchType();
    	Set<String> fiberSet=getFiberAck(linkList);
	    switch(type){
	    case MsgConstant.FIBER:
	    	for(Msg msgTmp :msgList){//������ڲ��������������߼����������ܽ���
	    	    if(msgTmp.getSwitchType()==MsgConstant.WAVEBAND){
	    	    	return true;
	    	    }
	    	}
	    	if(msg.getFiberIp()==MsgConstant.IP1){//����ǵ�Ŀ��IP1
	    		if(fiberSet.contains("13")||fiberSet.contains("33")){
	    			return true;
	    		}
	    	}else if(msg.getFiberIp()==MsgConstant.IP2){//����������Ŀ��IP2
	    		if(fiberSet.contains("14")||fiberSet.contains("44")||fiberSet.contains("22")||fiberSet.contains("23")){
	    			return true;
	    		}
	    	}
	    	break;
	    case MsgConstant.WAVEBAND:
	    	if(fiberSet.contains("23")||fiberSet.contains("24")){
	    		return true;
	    	}
	    	break;
	    case MsgConstant.WAVELENGTH:
	    	if(fiberSet.contains("23")||fiberSet.contains("24")){//��·���ڹ��˼����������ܽ��зַ�
	    		return true;
	    	}
	    	Set<String> bandSet=getBandAck(linkList);
	    	if(bandSet.contains("37")||bandSet.contains("67")){//�Ѿ��в�������·����
	    		return true;
	    	}
	    	break;
	    }
		
		return  false;
	}
    /**
     * ÿ�θ��ݲ�����Ϣ�Ƿ��ظ����ж��Ƿ��������
     */
    public boolean isWaveDulp(Msg msg){
    	List<UserMsg> userMsgs=msg.getUserMsg();
		int index=0;
		for(UserMsg userMsg:userMsgs){
			index=WSSSetupCl.getInstance().getChannel(userMsg.getSynWave());
			if(WaveDupl.duplWave[index]){
				return true;
			}
			index=WSSSetupCl.getInstance().getChannel(userMsg.getQuanWave());
			if(WaveDupl.duplWave[index]){
				return true;
			}
			index=WSSSetupCl.getInstance().getChannel(userMsg.getClassWave());
			if(WaveDupl.duplWave[index]){
				return true;
			}
		}
    	return false;
    }
    /**
     * ��AckMsg������в��,��ȡ���˼����
     **/
   private  Set<String> getFiberAck(ArrayList<AckMsg> linkList){
	   Set<String> set=new HashSet<String>();
	   String str="";
	   if(linkList.size()<=0)
		   return set;
	   for(AckMsg ackMsg:linkList){
		   String fiberStr=ackMsg.getFiberAck();
		   for(int i=0;i<fiberStr.length();i=i+2){
			   str=fiberStr.substring(i, i+2); 
			   if(!set.contains(str)){
				   set.add(str);
			   }
		   }
	   }
	   return set;
   }
   /**
    * ��AckMsg������в�֣���ò������������
    **/
    private Set<String> getBandAck(ArrayList<AckMsg> linkList){
    	  Set<String> set=new HashSet<String>();
    	  String str="";
    	  if(linkList.size()<=0)
    		  return set;
    	  for(AckMsg ackMsg:linkList){
    		  String bandStr=ackMsg.getBandAck();
    		  for(int i=0;i<bandStr.length();i=i+2){
    			  str=bandStr.substring(i,i+2);
    			  if(!set.contains(str)){
    				  set.add(str);
    			  }
    		  }
    	  }
    	  return set;
    }
}
