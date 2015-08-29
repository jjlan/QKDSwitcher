package com.qkdversion.msgDao;
//消息范例："1$0$3$10.108.0.1,1566.723,1566.314,1565.905,10.109.0.1,0$10.108.0.2,1565.496,1565.087,1564.679,10.109.0.2,0$+
//10.108.0.3,1564.271,1563.863,1563.455,10.109.0.1,10.109.0.2$&";

import java.io.UnsupportedEncodingException;
import java.util.List;


import android.R.integer;
import android.util.Log;

import com.qkdversion.OXCDomian.LengthType;
import com.qkdversion.msgDomain.Ack;
import com.qkdversion.msgDomain.AckMsg;
import com.qkdversion.msgDomain.Msg;
import com.qkdversion.msgDomain.MsgConstant;
import com.qkdversion.msgDomain.UserMsg;
import com.qkdversion.oxcService.LengthDao;
import com.qkdversion.socketDao.clientThread;
import com.qkdversion.utils.MsgUtil;
/**
 * 类型：msg的控制层
 * @author Ljj
 *
 */
public final class MsgDao {
	private static final String TAG = "MsgDao";
	private static MsgDao instance = new MsgDao();

	private MsgDao() {
	}

	public static MsgDao getInstance() {
		return instance;
	}
    /** 
     *  将接收到的字符串转换成Msg
     **/
	public Msg getMSg(byte[] bytes) {
		Msg msg = new Msg();
		String str=null;
		try {
			str = new String(bytes,"ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] strs = str.split("\\$");
		msg.setSwitchType(Integer.valueOf(strs[0]));
		msg.setFlag(Integer.valueOf(strs[1]));
		msg.setFiberIp(strs[2]);
		msg.setUserNum(Integer.valueOf(strs[3]));
		List<UserMsg> userList = msg.getUserMsg();
		int Usernum = msg.getUserNum();
		for (int i = 4; i < 4 + Usernum; i++) {
			userList.add(setUserMsg(strs[i]));
		}
		byte[] byteId=new byte[4];
		int len=bytes.length;
		Log.i(TAG,"len:"+len);
	    byteId[3]=bytes[len-1];
	    Log.i(TAG," byteId[3]:"+ byteId[3]);
	    byteId[2]=bytes[len-2];
	    Log.i(TAG," byteId[2]:"+ byteId[2]);
	    byteId[1]=bytes[len-3];
	    Log.i(TAG," byteId[1]:"+ byteId[1]);
	    byteId[0]=bytes[len-4];
	    Log.i(TAG," byteId[0]:"+ byteId[0]);
	    msg.setId(MsgUtil.ByteToInt(byteId));
		Log.i(TAG,"ID号为："+msg.getId());
		return msg;
	}
	/**
     *  设置用户信息
     **/
	private UserMsg setUserMsg(String str) {
		String[] userStrs = str.split(",");
		UserMsg userMsg = new UserMsg();
		userMsg.setIpSource(userStrs[0]);
		userMsg.setSynWave(Double.valueOf(userStrs[1]));
		userMsg.setQuanWave(Double.valueOf(userStrs[2]));
		userMsg.setClassWave(Double.valueOf(userStrs[3]));
		userMsg.setIpDes1(userStrs[4]);
		userMsg.setIpDes2(userStrs[5]);
		return userMsg;
	}
	/**
	 * 将AckMsg数据包解析成应答字符串
	 * @throws UnsupportedEncodingException 
	 **/
	public byte[] getAckStr(AckMsg ackMsg) {
		StringBuilder  sb =new StringBuilder();
		sb.append("$");
		sb.append(ackMsg.getFiberAck()+"$");
		sb.append(ackMsg.getBandAck()+"$");
		sb.append(ackMsg.getLengthAck());
		byte[] byte1 = null;
		try {
			byte1 = sb.toString().getBytes();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int len=byte1.length;
		byte[] bytes=new byte[len+5];
		bytes[0]=ackMsg.getAck();
		byte[] byteId=MsgUtil.IntToByte(ackMsg.getId());
		for(int i=1;i<5;i++){
			bytes[i]=byteId[i-1];
		}
		for(int i=5;i<bytes.length;i++){
			bytes[i]=byte1[i-5];
		}
		return bytes;
	}
	/**
	 * 判断服务器发来的消息是否是四个字节，是的话，即为断开信令
	 **/
	public boolean isDisconnect(byte[] bytes){
		return bytes.length==4;
	}
	
	/**
	 *  封装光纤级分配结果
	 **/
	public  AckMsg makeFiberAck(int ack,Msg msg,List<AckMsg> list){
		AckMsg ackMsg=new AckMsg();
		byte ackFiber=(byte)ack;//转换前转成一个字节
		ackMsg.setAck(ackFiber);
		//波长级分配成功
		if(ack==Ack.FIBER_OK){
			ackMsg.setId(msg.getId());
		    if(MsgConstant.IP1.equals(msg.getFiberIp())){
		    	if(msg.getFlag()==MsgConstant.ONE){
		    		ackMsg.setFiberAck("13");
		    	}else if(msg.getFlag()==MsgConstant.TWO){
		    		ackMsg.setFiberAck("23");	
		    	}
		    }else if(MsgConstant.IP2.equals(msg.getFiberIp())){
		    	if(msg.getFlag()==MsgConstant.ONE){
		    		ackMsg.setFiberAck("14");
		    	}else if(msg.getFlag()==MsgConstant.TWO){
		    		ackMsg.setFiberAck("24");	
		    	}
		    }
		    ackMsg.setBandAck("0");
			ackMsg.setLengthAck("0");
			list.add(ackMsg);//结果有效，保存起来
		}else{
			ackMsg.setId(msg.getId());
			ackMsg.setFiberAck("0");
			ackMsg.setBandAck("0");
			ackMsg.setLengthAck("0");
		}
		return ackMsg;
	}
	/**
	 * 封装波带级分配结果
	 **/
	public AckMsg makeBandAck(int ack,Msg msg,List<AckMsg> list){
		AckMsg ackMsg=new AckMsg();
		byte ackBand=(byte)ack;//发送前转换成一个字节
		ackMsg.setAck(ackBand);
		if(ack==Ack.BAND_OK){//波带级分配成功
			ackMsg.setId(msg.getId());
			if(msg.getFlag()==MsgConstant.ONE){//如果是从第一路输进来的信号
				ackMsg.setFiberAck("113344");
				ackMsg.setBandAck("1124");
			}else if(msg.getFlag()==MsgConstant.TWO){//如果是从第二路输进来的信号
				ackMsg.setFiberAck("223344");
				ackMsg.setBandAck("4255");
			}
			ackMsg.setLengthAck("0");
			list.add(ackMsg);//结果有效，保存起来
		}else{
			ackMsg.setId(msg.getId());
			ackMsg.setFiberAck("0");
			ackMsg.setBandAck("0");
			ackMsg.setLengthAck("0");
		}
		return ackMsg; 
	}
	/**
	 * 封装波长级分配结果
	 **/	
	public AckMsg makeLengthAck(int lengthType,int ack,Msg msg,List<AckMsg> list){
		AckMsg ackMsg=new AckMsg();
		byte ackLength=(byte)ack;//发送前转换成一个字节
		ackMsg.setAck(ackLength);
		if(ack==Ack.LEGNTH_OK){
			ackMsg.setId(msg.getId());
			switch (lengthType) {
			case LengthType.NOLOCAL:
				//光线级4*4配置
				FiberChoose(msg,ackMsg);
				//波带级8*8配置(区分一下走光纤级的同时，波带级的用户可能性)
				bandChoose1(msg,ackMsg);
				//波长级8*8配置
				lengthChoose1(msg,ackMsg);
				list.add(ackMsg);
				break;
			case LengthType.ONLYLOCAL:
				//光线级4*4配置 
				
				FiberChoose(msg,ackMsg);
				//波带级8*8配置(区分一下走光纤级的同时，波带级的用户可能性)
				bandChoose2(msg,ackMsg);
				//波长级8*8配置
				lengthChoose2(msg,ackMsg);
				list.add(ackMsg);
				break;
			default:
				break;
			}
		}else{
			ackMsg.setId(msg.getId());
			ackMsg.setFiberAck("0");
			ackMsg.setBandAck("0");
			ackMsg.setLengthAck("0");
		}
		return ackMsg;
	}
	/**
	 *  生成光纤级4*4的返回码
	 **/
	public void FiberChoose(Msg msg,AckMsg ackMsg){
		String str="";
		int ack=LengthDao.getInstance().isContains34(msg);
		//光纤级4*4配置
		if(msg.getFlag()==MsgConstant.ONE){//如果是从第一路输进来的信号
			str="11";
			switch (ack) {
			case 0:
				str+="33";
				break;
			case 1:
				str+="44";
				break;
			case 2:
				str+="3344";
				break;
			case 3:
				break;
			default:
				break;
			}
		}else if(msg.getFlag()==MsgConstant.TWO){//如果是从第二路输进来的信号
			str="22";
			switch (ack) {
			case 0:
				str+="33";
				break;
			case 1:
				str+="44";
				break;
			case 2:
				str+="3344";
				break;
			case 3:
				break;
			default:
				break;
			}
			
		}
		ackMsg.setFiberAck(str);
	}
	
	/**
	 * 当NOLOCAL时,生成波带级8*8的返回码
	 **/
	public void bandChoose1(Msg msg,AckMsg ackMsg){
		List<UserMsg> userList = msg.getUserMsg();
		boolean flag1=false;
		boolean flag2=false;
		String str="";
		if(msg.getFlag()==MsgConstant.ONE){
			for(UserMsg user : userList){
				if(user.getIpDes1().equals(MsgConstant.IP1)&&user.getIpDes2().equals("0")){
					flag1=true;
				}
				if(user.getIpDes1().equals(MsgConstant.IP2)&&user.getIpDes2().equals("0")){
					flag2=true;
				}
			}
			if(flag1&&flag2){//两路都有
				str="112437";
			}else if(flag1==true){//只有到目的IP1的
				str="1137";	
			}else if(flag2==true){//只有到目的IP2的
				//ackMsg.setBandAck("24377386");
				str="2437";
			}
		}else {
			for(UserMsg user : userList){
				if(user.getIpDes1().equals(MsgConstant.IP1)&&user.getIpDes2().equals("0")){
					flag1=true;
				}
				if(user.getIpDes1().equals(MsgConstant.IP2)&&user.getIpDes2().equals("0")){
					flag2=true;
				}
			}
			if(flag1&&flag2){//两路都有
				str="425567";
			}else if(flag1==true){//只有到目的IP1的
				str="4267";	
			}else if(flag2==true){//只有到目的IP2的
				str="5567";
			}
		}
		int lengthAck=LengthDao.getInstance().LengthDes1(msg);//判断下情况一下的几种小类型
		switch (lengthAck) {
		case 1://Q4,S4到达的是目的IP1，D4到达的目的IP2
			str=str+"7386";
			break;
		case 2://Q4,S4到达的是目的IP2，D4到达目的IP1
			str=str+"7386";
			break;
		case 4:	//Q4,S4下到本地，D4到目的IP1
			str=str+"73";
			break;
		case 5:	//Q4,S4下到本地，D4到目的IP2
			str=str+"86";
			break;
		case 6:	//Q4,S4下到本地，D4到目的IP2
			str=str+"73";
			break;
		case 7:	//Q4,S4下到本地，D4到目的IP2
			str=str+"86";
			break;
		default:
			break;
		}
		//设置情况一下波带级交换的光开关应答状态
		ackMsg.setBandAck(str);
	}
	/**
	 * 当NOLOCAL时,生成波长级8*8的返回码
	 **/
	public void lengthChoose1(Msg msg,AckMsg ackMsg){
		String str="";
		int lengthAck=LengthDao.getInstance().LengthDes1(msg);//判断下情况一下的几种小类型
		switch (lengthAck) {
		case 1://Q4,S4到达的是目的IP1，D4到达的目的IP2
			str="112234";
			break;
		case 2://Q4,S4到达的是目的IP2，D4到达目的IP1
			str="142533";
			break;
		case 3://三个都去本地
			str="162738";
			break;
		case 4:	//Q4,S4下到本地，D4到目的IP1
			str="162733";
			break;
		case 5:	//Q4,S4下到本地，D4到目的IP2
			str="162734";
			break;
		case 6:	//Q4,S4去往IP1，D4到达本地
			str="112238";
			break;
		case 7:	//Q4,S4去往IP2，D4到达本地
			str="142538";
			break;
		default:
			break;
		}
		//设置情况一下波长级交换的光开关应答状态
		ackMsg.setLengthAck(str);
	}
	/**
	 * 当ONLYLOCAL时,生成波带级8*8的返回码
	 **/
	public void bandChoose2(Msg msg,AckMsg ackMsg){
		List<UserMsg> userList = msg.getUserMsg();
		boolean flag1=false;
		boolean flag2=false;
		String str="";
		if(msg.getFlag()==MsgConstant.ONE){
			for(UserMsg user : userList){
				if(user.getIpDes1().equals(MsgConstant.IP1)&&user.getIpDes2().equals("0")){
					flag1=true;
				}
				if(user.getIpDes1().equals(MsgConstant.IP2)&&user.getIpDes2().equals("0")){
					flag2=true;
				}
			}
			if(flag1&&flag2){//两路都有
				str="1124";
			}else if(flag1==true){//只有到目的IP1的
				str="11";	
			}else if(flag2==true){//只有到目的IP2的
				//ackMsg.setBandAck("24377386");
				str="24";
			}
		}else {
			for(UserMsg user : userList){
				if(user.getIpDes1().equals(MsgConstant.IP1)&&user.getIpDes2().equals("0")){
					flag1=true;
				}
				if(user.getIpDes1().equals(MsgConstant.IP2)&&user.getIpDes2().equals("0")){
					flag2=true;
				}
			}
			if(flag1&&flag2){//两路都有
				str="4255";
			}else if(flag1==true){//只有到目的IP1的
				str="42";	
			}else if(flag2==true){//只有到目的IP2的
				str="55";
			}
		}
		int lengthAck=LengthDao.getInstance().LengthDes2(msg);//判断下情况一下的几种小类型
		switch (lengthAck) {
		case 1://本地用户与本地接收用户进行交互
			break;
		case 2://本地用户Q4,S4去往IP1,D4去往IP2
			str=str+"7386";
			break;
		case 3:	//本地用户Q4,S4去往IP1,D4去往本地
			str=str+"73";
			break;
		case 4:	//本地用户Q4,S4去往IP2,D4去往IP1
			str=str+"7386";
			break;
		case 5:	 //本地用户Q4,S4去往IP2,D4去往本地
			str=str+"86";
			break;
		case 6:	//本地用户Q4,S4去往本地,D4去往IP1
			str=str+"73";
			break;
		case 7:	//本地用户Q4,S4去往本地,D4去往IP2
			str=str+"86";
			break;
		case 8:	//本地用户Q4,S4,D4都去往IP1
			str=str+"73";
			break;
		default:
			break;
		}
		//设置情况一下波带级交换的光开关应答状态
		ackMsg.setBandAck(str);
		
	}
	/**
	 * 当ONLYLOCAL时,生成波长级8*8的返回码
	 **/
	public void lengthChoose2(Msg msg,AckMsg ackMsg){
		String str="";
		int lengthAck=LengthDao.getInstance().LengthDes2(msg);//判断下情况一下的几种小类型
		switch (lengthAck) {

		case 1://本地用户与本地接收用户进行交互
			str="667788";
			break;
		case 2://本地用户Q4,S4去往IP1,D4去往IP2
			str="617284";
			break;
		case 3:	//本地用户Q4,S4去往IP1,D4去往本地
			str="617288";
			break;
		case 4:	//本地用户Q4,S4去往IP2,D4去往IP1
			str="647583";
			break;
		case 5:	 //本地用户Q4,S4去往IP2,D4去往本地
			str="647588";
			break;
		case 6:	//本地用户Q4,S4去往本地,D4去往IP1
			str="647588";
			break;
		case 7:	//本地用户Q4,S4去往本地,D4去往IP2
			str="667784";
			break;
		case 8:	//本地用户Q4,S4,D4都去往IP1
			str="647588";
			break;
		default:
			break;
		}
		//设置情况一下波长级交换的光开关应答状态
		ackMsg.setLengthAck(str);
	}
}
