package com.qkdversion.msgDao;
//��Ϣ������"1$0$3$10.108.0.1,1566.723,1566.314,1565.905,10.109.0.1,0$10.108.0.2,1565.496,1565.087,1564.679,10.109.0.2,0$+
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
 * ���ͣ�msg�Ŀ��Ʋ�
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
     *  �����յ����ַ���ת����Msg
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
		Log.i(TAG,"ID��Ϊ��"+msg.getId());
		return msg;
	}
	/**
     *  �����û���Ϣ
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
	 * ��AckMsg���ݰ�������Ӧ���ַ���
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
	 * �жϷ�������������Ϣ�Ƿ����ĸ��ֽڣ��ǵĻ�����Ϊ�Ͽ�����
	 **/
	public boolean isDisconnect(byte[] bytes){
		return bytes.length==4;
	}
	
	/**
	 *  ��װ���˼�������
	 **/
	public  AckMsg makeFiberAck(int ack,Msg msg,List<AckMsg> list){
		AckMsg ackMsg=new AckMsg();
		byte ackFiber=(byte)ack;//ת��ǰת��һ���ֽ�
		ackMsg.setAck(ackFiber);
		//����������ɹ�
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
			list.add(ackMsg);//�����Ч����������
		}else{
			ackMsg.setId(msg.getId());
			ackMsg.setFiberAck("0");
			ackMsg.setBandAck("0");
			ackMsg.setLengthAck("0");
		}
		return ackMsg;
	}
	/**
	 * ��װ������������
	 **/
	public AckMsg makeBandAck(int ack,Msg msg,List<AckMsg> list){
		AckMsg ackMsg=new AckMsg();
		byte ackBand=(byte)ack;//����ǰת����һ���ֽ�
		ackMsg.setAck(ackBand);
		if(ack==Ack.BAND_OK){//����������ɹ�
			ackMsg.setId(msg.getId());
			if(msg.getFlag()==MsgConstant.ONE){//����Ǵӵ�һ·��������ź�
				ackMsg.setFiberAck("113344");
				ackMsg.setBandAck("1124");
			}else if(msg.getFlag()==MsgConstant.TWO){//����Ǵӵڶ�·��������ź�
				ackMsg.setFiberAck("223344");
				ackMsg.setBandAck("4255");
			}
			ackMsg.setLengthAck("0");
			list.add(ackMsg);//�����Ч����������
		}else{
			ackMsg.setId(msg.getId());
			ackMsg.setFiberAck("0");
			ackMsg.setBandAck("0");
			ackMsg.setLengthAck("0");
		}
		return ackMsg; 
	}
	/**
	 * ��װ������������
	 **/	
	public AckMsg makeLengthAck(int lengthType,int ack,Msg msg,List<AckMsg> list){
		AckMsg ackMsg=new AckMsg();
		byte ackLength=(byte)ack;//����ǰת����һ���ֽ�
		ackMsg.setAck(ackLength);
		if(ack==Ack.LEGNTH_OK){
			ackMsg.setId(msg.getId());
			switch (lengthType) {
			case LengthType.NOLOCAL:
				//���߼�4*4����
				FiberChoose(msg,ackMsg);
				//������8*8����(����һ���߹��˼���ͬʱ�����������û�������)
				bandChoose1(msg,ackMsg);
				//������8*8����
				lengthChoose1(msg,ackMsg);
				list.add(ackMsg);
				break;
			case LengthType.ONLYLOCAL:
				//���߼�4*4���� 
				
				FiberChoose(msg,ackMsg);
				//������8*8����(����һ���߹��˼���ͬʱ�����������û�������)
				bandChoose2(msg,ackMsg);
				//������8*8����
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
	 *  ���ɹ��˼�4*4�ķ�����
	 **/
	public void FiberChoose(Msg msg,AckMsg ackMsg){
		String str="";
		int ack=LengthDao.getInstance().isContains34(msg);
		//���˼�4*4����
		if(msg.getFlag()==MsgConstant.ONE){//����Ǵӵ�һ·��������ź�
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
		}else if(msg.getFlag()==MsgConstant.TWO){//����Ǵӵڶ�·��������ź�
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
	 * ��NOLOCALʱ,���ɲ�����8*8�ķ�����
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
			if(flag1&&flag2){//��·����
				str="112437";
			}else if(flag1==true){//ֻ�е�Ŀ��IP1��
				str="1137";	
			}else if(flag2==true){//ֻ�е�Ŀ��IP2��
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
			if(flag1&&flag2){//��·����
				str="425567";
			}else if(flag1==true){//ֻ�е�Ŀ��IP1��
				str="4267";	
			}else if(flag2==true){//ֻ�е�Ŀ��IP2��
				str="5567";
			}
		}
		int lengthAck=LengthDao.getInstance().LengthDes1(msg);//�ж������һ�µļ���С����
		switch (lengthAck) {
		case 1://Q4,S4�������Ŀ��IP1��D4�����Ŀ��IP2
			str=str+"7386";
			break;
		case 2://Q4,S4�������Ŀ��IP2��D4����Ŀ��IP1
			str=str+"7386";
			break;
		case 4:	//Q4,S4�µ����أ�D4��Ŀ��IP1
			str=str+"73";
			break;
		case 5:	//Q4,S4�µ����أ�D4��Ŀ��IP2
			str=str+"86";
			break;
		case 6:	//Q4,S4�µ����أ�D4��Ŀ��IP2
			str=str+"73";
			break;
		case 7:	//Q4,S4�µ����أ�D4��Ŀ��IP2
			str=str+"86";
			break;
		default:
			break;
		}
		//�������һ�²����������Ĺ⿪��Ӧ��״̬
		ackMsg.setBandAck(str);
	}
	/**
	 * ��NOLOCALʱ,���ɲ�����8*8�ķ�����
	 **/
	public void lengthChoose1(Msg msg,AckMsg ackMsg){
		String str="";
		int lengthAck=LengthDao.getInstance().LengthDes1(msg);//�ж������һ�µļ���С����
		switch (lengthAck) {
		case 1://Q4,S4�������Ŀ��IP1��D4�����Ŀ��IP2
			str="112234";
			break;
		case 2://Q4,S4�������Ŀ��IP2��D4����Ŀ��IP1
			str="142533";
			break;
		case 3://������ȥ����
			str="162738";
			break;
		case 4:	//Q4,S4�µ����أ�D4��Ŀ��IP1
			str="162733";
			break;
		case 5:	//Q4,S4�µ����أ�D4��Ŀ��IP2
			str="162734";
			break;
		case 6:	//Q4,S4ȥ��IP1��D4���ﱾ��
			str="112238";
			break;
		case 7:	//Q4,S4ȥ��IP2��D4���ﱾ��
			str="142538";
			break;
		default:
			break;
		}
		//�������һ�²����������Ĺ⿪��Ӧ��״̬
		ackMsg.setLengthAck(str);
	}
	/**
	 * ��ONLYLOCALʱ,���ɲ�����8*8�ķ�����
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
			if(flag1&&flag2){//��·����
				str="1124";
			}else if(flag1==true){//ֻ�е�Ŀ��IP1��
				str="11";	
			}else if(flag2==true){//ֻ�е�Ŀ��IP2��
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
			if(flag1&&flag2){//��·����
				str="4255";
			}else if(flag1==true){//ֻ�е�Ŀ��IP1��
				str="42";	
			}else if(flag2==true){//ֻ�е�Ŀ��IP2��
				str="55";
			}
		}
		int lengthAck=LengthDao.getInstance().LengthDes2(msg);//�ж������һ�µļ���С����
		switch (lengthAck) {
		case 1://�����û��뱾�ؽ����û����н���
			break;
		case 2://�����û�Q4,S4ȥ��IP1,D4ȥ��IP2
			str=str+"7386";
			break;
		case 3:	//�����û�Q4,S4ȥ��IP1,D4ȥ������
			str=str+"73";
			break;
		case 4:	//�����û�Q4,S4ȥ��IP2,D4ȥ��IP1
			str=str+"7386";
			break;
		case 5:	 //�����û�Q4,S4ȥ��IP2,D4ȥ������
			str=str+"86";
			break;
		case 6:	//�����û�Q4,S4ȥ������,D4ȥ��IP1
			str=str+"73";
			break;
		case 7:	//�����û�Q4,S4ȥ������,D4ȥ��IP2
			str=str+"86";
			break;
		case 8:	//�����û�Q4,S4,D4��ȥ��IP1
			str=str+"73";
			break;
		default:
			break;
		}
		//�������һ�²����������Ĺ⿪��Ӧ��״̬
		ackMsg.setBandAck(str);
		
	}
	/**
	 * ��ONLYLOCALʱ,���ɲ�����8*8�ķ�����
	 **/
	public void lengthChoose2(Msg msg,AckMsg ackMsg){
		String str="";
		int lengthAck=LengthDao.getInstance().LengthDes2(msg);//�ж������һ�µļ���С����
		switch (lengthAck) {

		case 1://�����û��뱾�ؽ����û����н���
			str="667788";
			break;
		case 2://�����û�Q4,S4ȥ��IP1,D4ȥ��IP2
			str="617284";
			break;
		case 3:	//�����û�Q4,S4ȥ��IP1,D4ȥ������
			str="617288";
			break;
		case 4:	//�����û�Q4,S4ȥ��IP2,D4ȥ��IP1
			str="647583";
			break;
		case 5:	 //�����û�Q4,S4ȥ��IP2,D4ȥ������
			str="647588";
			break;
		case 6:	//�����û�Q4,S4ȥ������,D4ȥ��IP1
			str="647588";
			break;
		case 7:	//�����û�Q4,S4ȥ������,D4ȥ��IP2
			str="667784";
			break;
		case 8:	//�����û�Q4,S4,D4��ȥ��IP1
			str="647588";
			break;
		default:
			break;
		}
		//�������һ�²����������Ĺ⿪��Ӧ��״̬
		ackMsg.setLengthAck(str);
	}
}
