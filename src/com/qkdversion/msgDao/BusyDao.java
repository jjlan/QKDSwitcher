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
	//检查链路占用情况
	public boolean isLinkBusy(ArrayList<Msg> msgList, ArrayList<AckMsg> linkList,Msg msg) {
		if(msgList.size()==0||linkList.size()==0){//链路不忙或首次加载
			return false;
		}
		if(isWaveDulp(msg)){//波长有重复,则返回true
			return true;
		}
		 //先检测光纤级是否冲突
		if (msg.getFlag()==MsgConstant.ONE) {//新来的是从上路过来
			if(upPath(msgList,linkList,msg))
				return true;
		}else if(msg.getFlag()==MsgConstant.TWO){//新来的是从下路过来
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
	    	for(Msg msgTmp :msgList){//如果存在波带级交换，光线级交换均不能进行
	    	    if(msgTmp.getSwitchType()==MsgConstant.WAVEBAND){
	    	    	return true;
	    	    }
	    	}
	    	if(msg.getFiberIp()==MsgConstant.IP1){//如果是到目的IP1
	    		if(fiberSet.contains("33")||fiberSet.contains("23")||fiberSet.contains("14")||fiberSet.contains("11")){
	    			return true;
	    		}
	    	}else if(msg.getFiberIp()==MsgConstant.IP2){//如果到达的是目的IP2
	    		if(fiberSet.contains("44")||fiberSet.contains("24")){
	    			return true;
	    		}
	    	}
	    	break;
	    case MsgConstant.WAVEBAND:
	    	if(fiberSet.contains("13")||fiberSet.contains("14")){//上路存在光纤级交换，则不能进行分发
	    		return true;
	    	}
	    	break;
	    case MsgConstant.WAVELENGTH:
	    	if(fiberSet.contains("13")||fiberSet.contains("14")){//上路存在光纤级交换，则不能进行分发
	    		return true;
	    	}
	    	Set<String> bandSet=getBandAck(linkList);
	    	if(bandSet.contains("37")||bandSet.contains("67")){//已经有波长从上路下来
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
	    	for(Msg msgTmp :msgList){//如果存在波带级交换，光线级交换均不能进行
	    	    if(msgTmp.getSwitchType()==MsgConstant.WAVEBAND){
	    	    	return true;
	    	    }
	    	}
	    	if(msg.getFiberIp()==MsgConstant.IP1){//如果是到目的IP1
	    		if(fiberSet.contains("13")||fiberSet.contains("33")){
	    			return true;
	    		}
	    	}else if(msg.getFiberIp()==MsgConstant.IP2){//如果到达的是目的IP2
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
	    	if(fiberSet.contains("23")||fiberSet.contains("24")){//上路存在光纤级交换，则不能进行分发
	    		return true;
	    	}
	    	Set<String> bandSet=getBandAck(linkList);
	    	if(bandSet.contains("37")||bandSet.contains("67")){//已经有波长从下路下来
	    		return true;
	    	}
	    	break;
	    }
		
		return  false;
	}
    /**
     * 每次根据波长信息是否重复来判断是否信令可用
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
     * 将AckMsg结果进行拆分,获取光纤级结果
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
    * 将AckMsg结果进行拆分，获得波长级交换结果
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
