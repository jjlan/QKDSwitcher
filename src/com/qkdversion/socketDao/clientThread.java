package com.qkdversion.socketDao;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.text.InputFilter.LengthFilter;
import android.util.Log;
import android.widget.Switch;

import com.qkdversion.OXCDomian.LengthType;
import com.qkdversion.OXCDomian.OXCAckPara;
import com.qkdversion.OXCDomian.OXCSetupPara;
import com.qkdversion.msgDao.BusyDao;
import com.qkdversion.msgDao.MsgDao;
import com.qkdversion.msgDao.WaveDupl;
import com.qkdversion.msgDomain.Ack;
import com.qkdversion.msgDomain.AckMsg;
import com.qkdversion.msgDomain.Msg;
import com.qkdversion.msgDomain.MsgConstant;
import com.qkdversion.msgDomain.UserMsg;
import com.qkdversion.oxcDao.OXCSetupCl;
import com.qkdversion.oxcService.LengthDao;
import com.qkdversion.oxcService.OXCService;
import com.qkdversion.socketDomain.ServerPara;
import com.qkdversion.utils.MsgUtil;
import com.qkdversion.wssDao.WSSSetupCl;
import com.topeet.serialtest.Com;
import com.topeet.serialtest.serial;
/**
 * ����:��Ҫ�߼������߳���ִ��
 * @author Ljj 2015-04-24
 */
public class clientThread implements Runnable {
	static {
		System.loadLibrary("serialtest"); 
	}
	private static final String TAG = "clientThread"; 
	private serial com1;//��һ��WSS
	private serial com2;//�ڶ���WSS
	private Socket s;
	private boolean isFinish = false;// �����ж�һ�η����Ƿ����
	private ArrayList<Msg> msgList;//���ڱ����Ѿ�����ɹ���δ�ͷŵ����ݰ���Ϣ
	private ArrayList<AckMsg> linkList;	//linklist���ڱ�����ռ�õ���·��Ϣ
	private Msg msg;// ���ڱ�����Ϣ
	private byte[] bytes;// ���ڽ�����Ϣ	
	private OXCSetupCl oxc_1;//telnet������ oxc 4*4(����)
	private OXCSetupCl oxc_2;//telnet������ oxc 8*8(����)
	private OXCSetupCl oxc_3;//telnet������ oxc 8*8(����)
	@Override
	public void run() {   
		try {
			init();//��ʼ��
			Log.i(TAG, "Thread" + Thread.currentThread().getName());
	        int initState= checkDevice();// ����豸״��
	        Log.i(TAG, "�豸�������"+initState);
			s = new Socket(ServerPara.Server_IP, ServerPara.Server_Port);//���ӷ�����
			byte[] byteStatus=new byte[1];
			byteStatus[0]=(byte)initState;
			writeToServer(byteStatus);	// ���豸״�������������
			while (true) {
				bytes = readFromServer();// �ȴ����������ݰ�,�������ݣ���һֱ����
				if (bytes != null) {
					Log.i(TAG, "get str from server" + new String(bytes));
					if(MsgDao.getInstance().isDisconnect(bytes)){//�ж���������,�Ƿ��ǶϿ����� 
						
						byte[] msgOkByte=new byte[5];
						msgOkByte[0]=(byte)Ack.MESSAGE_OK;
						for(int i=1;i<msgOkByte.length;i++){
							msgOkByte[i]=bytes[i-1];
						}
						writeToServer(msgOkByte);//����ǶϿ������Ҳ������Ϣ��ȷ
						int id=MsgUtil.ByteToInt(bytes);
						Log.i(TAG,"��ǰ��Ϣ�б�ĳ��ȣ�"+linkList.size());
						for(AckMsg ackMsg :linkList){
							if(ackMsg.getId()==id){//�ҵ���Ӧ�����ݰ�,����Ϣ�б���ɾ��
								linkList.remove(ackMsg);
							}
						}
						Log.i(TAG,"ɾ����Ӧ����Ϣ�б�ĳ��ȣ�"+linkList.size());
						Log.i(TAG,"��ǰMsg��Ϣ�б�ĳ��ȣ�"+msgList.size());
						for(Msg msg1 :msgList){
							if(msg1.getId()==id){//�ҵ���Ӧ�����ݰ�,����Ϣ�б���ɾ��
								msgList.remove(msg1);
							}
							resetWave(msg1);//����ɾ�����ݰ���Ӧ�����ı�ʶ���и�λ
						}
						Log.i(TAG,"ɾ����Msg��Ϣ�б�ĳ��ȣ�"+msgList.size());
						writeToServer(bytes);//������ɾ������Id��
						bytes=null;
						continue;
						
					}else{
						msg = MsgDao.getInstance().getMSg(bytes);// �����յ����ַ�����Ϣ��װ��Message
						
						if(bytes!=null){
							byte mAck=(byte)Ack.MESSAGE_OK;
							byte[] b=makeMsgIsOk(mAck,bytes);
							writeToServer(b);
							Log.i("TAG", "��Ϣ����Ӧ��"+mAck);
						}else{
							byte mAck=(byte)Ack.MESSAGE_ERR;
							byte[] b=makeMsgIsOk(mAck,bytes);
							writeToServer(b);
							Log.i("TAG", "��ϢӦ���쳣��"+mAck);
							continue;
						}
					}
					isFinish = true;
				}
				if (isFinish) {// ������Ϣ���������豸
					isFinish = false;
					if(BusyDao.getInstance().isLinkBusy(msgList,linkList,msg)){ //�����·״���������Ƿ���Ҫ����
						byte mAck=(byte)Ack.LINKISBUSY;
						byte[] b=makeMsgIsOk(mAck,bytes);
						writeToServer(b);
						continue;
					}
					AckMsg resAck = messageDispatcher(msg);	// �������ݰ����������ݷ���,�������װ��ackMsg����
					byte[] bytes=MsgDao.getInstance().getAckStr(resAck);//��ackMsg����ת����Ӧ������
					Log.i(TAG,"������Ӧ������:"+new String(bytes));
					writeToServer(bytes);
					saveWave(msg);//����ס��Ӧ�Ĳ���
					msgList.add(msg);//�����·���ɹ�����Ϣ���ڼ����·�Ƿ����
					bytes = null;// �������ַ��������ÿ�
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace(); 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ʼ������
	 **/
	 private void init(){
	    com1= new serial();
		com1.Open(3, 115200,Com.WSS1);
		com2 = new serial();
	    com2.Open(4, 115200,Com.WSS2);
	    oxc_1=new OXCSetupCl();
		oxc_2=new OXCSetupCl();
		oxc_3=new OXCSetupCl();
		//��ʼ��linklist��
		linkList=new ArrayList<AckMsg>();
		//��ʼ��Msg��
		msgList=new ArrayList<Msg>();
		//�����жϲ����Ƿ��ظ���һ���ظ���ֱ�ӷ�����·������
		for(int i=1;i<97;i++){
			WaveDupl.duplWave[i]=false;
		}
	 }
	/**
	 * ����豸״��
	 **/
	private int checkDevice() {
		int device_flag=0;
		// �����˼�OXC����
		int isConnect_1=oxc_1.connectToTelnet(OXCSetupPara.OXC_1_IP);
		if(OXCAckPara.CONNECT_FAIL==isConnect_1){
			device_flag=Ack.DEVICE_1_ERR;
			return device_flag;
		}
		// ��鲨�����⿪��
		int isConnect_2=oxc_2.connectToTelnet(OXCSetupPara.OXC_2_IP);
		if(OXCAckPara.CONNECT_FAIL==isConnect_2){
			device_flag=Ack.DEVICE_2_ERR;
			return device_flag;
		}
		// ��鲨�����⿪��
		int isConnect_3=oxc_3.connectToTelnet(OXCSetupPara.OXC_3_IP);
		if(OXCAckPara.CONNECT_FAIL==isConnect_3){
			device_flag=Ack.DEVICE_4_ERR;
			return device_flag;
		}
		// ���WSS1
		if (!WSSSetupCl.getInstance().checkWSS(com1,Com.WSS1)) {
			device_flag=Ack.DEVICE_3_ERR;
			return device_flag;
		}
		// ���WSS2
		if (!WSSSetupCl.getInstance().checkWSS(com2,Com.WSS2)) { 
			device_flag=Ack.DEVICE_5_ERR;
			return device_flag;
		}
		device_flag=Ack.DEVICE_OK;
		return device_flag;
	}

	
	/**
	 * ������Ϣ���� 
	 **/
	private AckMsg messageDispatcher(Msg msg) {
		int resFlag =0;	// ����ָʾ״̬��һ��ĳ�����ڳ����������ض�Ӧ����Ϣ
		int switchType=msg.getSwitchType();
		AckMsg ackMsg=null;;//����һ����Ϣ��
		switch(switchType){
		case MsgConstant.FIBER://���˼�����
			 resFlag=fiberSwitch(msg,oxc_1);//���й��˼�����
			 ackMsg=MsgDao.getInstance().makeFiberAck(resFlag,msg,linkList);//���ɹ��˼�Ӧ����Ϣ��������
			break;
		case MsgConstant.WAVEBAND://����������
			resFlag=bandSwitch(msg,oxc_1,oxc_2);
			ackMsg=MsgDao.getInstance().makeBandAck(resFlag,msg,linkList);//���ɲ�����Ӧ����Ϣ��������
			break;
		case MsgConstant.WAVELENGTH://����������
			int lengthType=LengthDao.getInstance().judgeLengthType(msg);
			Log.i(TAG, "�������������ѡ����:"+lengthType);
			resFlag=LengthSwitch(lengthType); 
			ackMsg=MsgDao.getInstance().makeLengthAck(lengthType,resFlag,msg,linkList);//���ɲ�����Ӧ����Ϣ��������
			break;
		}
		return ackMsg;
	}


	/**
	 * ���˼�����
	 **/
	private int fiberSwitch(Msg msg,OXCSetupCl oxcCl) {
		int fiberAck=0;   
			// ȡ��Ŀ��IP���ǵ�һͨ������
			if (MsgConstant.IP1.equals(msg.getFiberIp())) {
				if(msg.getFlag()==MsgConstant.ONE){
					String command =OXCSetupPara.FIBER_1_IP1;// Ŀ��IP��IP1��ȣ�OXC4*4��ͨ��1-----ͨ��3,ָ��Ϊset 3 x x x
					fiberAck=OXCService.getOXCService().oxcSwitch(oxcCl,OXCSetupPara.OXC_1_IP,command);
					Log.i(TAG,"���˼�����Ŀ�ĵ�ΪIP1(��1·)"+fiberAck);
					if(fiberAck!=Ack.OXCSETUP_1_OK){
						return fiberAck;
					}
				}else if(msg.getFlag()==MsgConstant.TWO){
					String command =OXCSetupPara.FIBER_2_IP1;// Ŀ��IP��IP1��ȣ�OXC4*4��ͨ��1-----ͨ��3,ָ��Ϊset 3 x x x
					fiberAck=OXCService.getOXCService().oxcSwitch(oxcCl,OXCSetupPara.OXC_1_IP,command);
					Log.i(TAG,"���˼�����Ŀ�ĵ�ΪIP1(��2·)"+fiberAck);
					if(fiberAck!=Ack.OXCSETUP_1_OK){
						return fiberAck;
					}
				}
			} else if (MsgConstant.IP2.equals(msg.getFiberIp())) {
				if(msg.getFlag()==MsgConstant.ONE){
					String command =OXCSetupPara.FIBER_1_IP2;// Ŀ��IP��IP1��ȣ�OXC4*4��ͨ��1-----ͨ��4,ָ��Ϊset 4 x x x
					fiberAck=OXCService.getOXCService().oxcSwitch(oxcCl,OXCSetupPara.OXC_1_IP,command);
					Log.i(TAG,"���˼�����Ŀ�ĵ�ΪIP2(��1·)"+fiberAck);
					if(fiberAck!=Ack.OXCSETUP_1_OK){
						return fiberAck;
					}
				}else if(msg.getFlag()==MsgConstant.TWO){
					String command =OXCSetupPara.FIBER_2_IP2;// Ŀ��IP��IP1��ȣ�OXC4*4��ͨ��1-----ͨ��4,ָ��Ϊset 4 x x x
					fiberAck=OXCService.getOXCService().oxcSwitch(oxcCl,OXCSetupPara.OXC_1_IP,command);
					Log.i(TAG,"���˼�����Ŀ�ĵ�ΪIP2(��2·)"+fiberAck);
					if(fiberAck!=Ack.OXCSETUP_1_OK){
						return fiberAck;
					}
				}
			}
		fiberAck=Ack.FIBER_OK;
		return fiberAck;
	}
	/**
	 * ����������
	 */
	private int bandSwitch(Msg msg, OXCSetupCl oxc_1,OXCSetupCl oxc_2) {
		int bandAck=0;
		//����4*4
		String command =OXCSetupPara.BAND_44_IN;//ÿ��ͨ����ÿ��ͨ�����Ӧ
		bandAck=OXCService.getOXCService().oxcSwitch(oxc_1,OXCSetupPara.OXC_1_IP,command);
		if(Ack.OXCSETUP_1_OK!=bandAck)
			return bandAck;
		Log.i(TAG,"����������4*4���ý��"+bandAck);
		//����WSS
		if(MsgConstant.ONE==msg.getFlag()){	
			bandAck= WSSSetupCl.getInstance().setWssPort(msg, com1,Com.WSS1);
			if(Ack.WSS1SENDDATA_OK!=bandAck){//����ʧ�ܣ�ֱ�ӽ���Ϣ����ȥ 
				return bandAck;
			}
		}else if(MsgConstant.TWO==msg.getFlag()){ 
			bandAck= WSSSetupCl.getInstance().setWssPort(msg, com2,Com.WSS2); 
			if(Ack.WSS2SENDDATA_OK!=bandAck){//����ʧ�ܣ�ֱ�ӽ���Ϣ����ȥ 
				return bandAck;
			}
		}
		Log.i(TAG,"Wss���ý��"+bandAck);
		//���ò�����8*8
		command=OXCSetupPara.BAND_88_IN;
		bandAck=OXCService.getOXCService().oxcSwitch(oxc_2,OXCSetupPara.OXC_2_IP,command);
		if(Ack.OXCSETUP_2_OK!=bandAck)
			return bandAck;
		Log.i(TAG,"����������8*8���ý��"+bandAck);
		bandAck=Ack.BAND_OK;
		return bandAck;
	}
    /**
     * ����������
     */
	private int LengthSwitch(int type) {
		String command1=null;//4*4
		String command2=null;//8*8
		String command3=null;//8*8
		int ack=-1;
		int lengthAck=-1;
		switch(type){
			case LengthType.NOLOCAL://�ޱ��ط��Ͷ��û�
				command1=OXCSetupPara.LENGTH1_F_IN;
				if(msg.getFlag()==MsgConstant.ONE){//ͨ���ж��û��Ǵ���һ·����ѡ�񲨴����������
					command2=OXCSetupPara.LENGTH1_B_1_IN;
				}else{
					command2=OXCSetupPara.LENGTH1_B_2_IN;
				}
				//ͨ���ж�Ŀ�ĵ�ַ�������֣�Ŀ��IP������IP1��IP2��IP4
				lengthAck=LengthDao.getInstance().LengthDes1(msg);//�����1����������ж�
				Log.i(TAG, "�ޱ��ط��Ͷ��û���������:"+lengthAck);
				 command3=LengthDao.getInstance().getCommandFrom1(lengthAck);
				 ack=lengthConfig(command1,command2,command3);
				 break;
			case LengthType.ONLYLOCAL://ֻ�б��ط��Ͷ��û�
				command1=OXCSetupPara.LENGTH1_F_IN;
				if(msg.getFlag()==MsgConstant.ONE){//ͨ���ж��û��Ǵ���һ·����ѡ�񲨴����������
					command2=OXCSetupPara.LENGTH1_B_1_IN;
				}else{
					command2=OXCSetupPara.LENGTH1_B_2_IN;
				}
				 lengthAck=LengthDao.getInstance().LengthDes2(msg);//�����2����������ж�
				 Log.i(TAG, "ֻ�б��ط��Ͷ��û���������:"+lengthAck);
				 command3=LengthDao.getInstance().getCommandFrom2(lengthAck);
				 ack=lengthConfig(command1,command2,command3);
			 break;
				
			default:
				break;	
		}
		return ack;
		
	}
	/**
	 * �������������ú���
	 */
	private int lengthConfig(String command1,String command2,String command3){
		int lengthAck=0;
	    //����4*4
	    lengthAck=OXCService.getOXCService().oxcSwitch(oxc_1,OXCSetupPara.OXC_1_IP, command1);
	    if(Ack.OXCSETUP_1_OK!=lengthAck)
	    	return lengthAck;
	    Log.i(TAG,"����������4*4���ý��"+lengthAck);
	    //����WSS
	    if(MsgConstant.ONE==msg.getFlag()){	
	    	lengthAck= WSSSetupCl.getInstance().setWssPort(msg, com1,Com.WSS1);
			if(Ack.WSS1SENDDATA_OK!=lengthAck){//����ʧ�ܣ�ֱ�ӽ���Ϣ����ȥ 
				return lengthAck;
			}
		}else if(MsgConstant.TWO==msg.getFlag()){
			lengthAck= WSSSetupCl.getInstance().setWssPort(msg, com2,Com.WSS2);
			if(Ack.WSS2SENDDATA_OK!=lengthAck){//����ʧ�ܣ�ֱ�ӽ���Ϣ����ȥ 
				return lengthAck;
			}
		}
	    //���ò�����8*8
	  	lengthAck=OXCService.getOXCService().oxcSwitch(oxc_2,OXCSetupPara.OXC_2_IP,command2);
	  	if(Ack.OXCSETUP_2_OK!=lengthAck)
	  	 return lengthAck;
	  	Log.i(TAG,"����������8*8���ý��"+lengthAck);
	   //���ò�����8*8
	  	lengthAck=OXCService.getOXCService().oxcSwitch(oxc_3,OXCSetupPara.OXC_3_IP,command3);
	  	if(Ack.OXCSETUP_3_OK!=lengthAck)
		  	return lengthAck;
		Log.i(TAG,"����������8*8���ý��"+lengthAck);
		lengthAck=Ack.LEGNTH_OK;
		return lengthAck;
	}
	/**
	 * �ӷ�������ȡ��Ϣ
	 **/
//	private String readFromServer() {
//		String str = "";
//		InputStream is = null;
//		try {
//			if (s != null) {
//				is = s.getInputStream();
//				//��ȡ������
//				byte[] bytes = new byte[4];
//				is.read(bytes);
//				int msgLen=MsgUtil.ByteToInt(bytes);
//				Log.i(TAG,"�����ȣ�"+msgLen);
//				byte[] msgBytes=new byte[msgLen];
//				int len= is.read(msgBytes);
//				if (len != -1) {
//					str = new String(msgBytes, 0, len, "utf-8");
//				}
//				Log.i(TAG,"��Ϣ���ȣ�"+len+"��Ϣ���ݣ�"+str);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return str;
//	}
	/**
	 * �ӷ�������ȡ��Ϣ
	 **/
	private byte[] readFromServer() {
		InputStream is = null;
		byte[] msgBytes=null;
		try {
			if (s != null) {
				is = s.getInputStream();
				//��ȡ������
				byte[] bytes = new byte[4];
				is.read(bytes);
				int msgLen=MsgUtil.ByteToInt(bytes);
				Log.i(TAG,"�����ȣ�"+msgLen);
				msgBytes=new byte[msgLen];
				int len= is.read(msgBytes);
				Log.i(TAG,"��Ϣ���ȣ�"+len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return msgBytes;
	}
	/**
	 * �������������Ϣ 
	 **/
	private void writeToServer(byte[] ack) {
		OutputStream os = null;
		try {
			if (s != null) {
				os = s.getOutputStream();
				byte[] bytes=MsgUtil.IntToByte(ack.length);
				os.write(bytes);
				Log.i(TAG, "Ӧ���źŵ�һ���ֽ�Ϊ��"+ack[0]);
				os.write(ack);
			}
		} catch (IOException e) {
			e.printStackTrace(); 
		}
	}
	/**
	 * �������������Ϣ 
	 **/
	private void writeToServer(String ack) {
		OutputStream os = null;
		try {
			if (s != null) {
				os = s.getOutputStream();
				byte[] bytes=MsgUtil.IntToByte(ack.getBytes("utf-8").length);
				os.write(bytes);
				os.write(ack.getBytes("utf-8"));
			}
		} catch (IOException e) {
			e.printStackTrace(); 
		}
	}
	/**
	 * ���ɹ�������δ�ͷŵĲ������б�ʶ
	 **/
	public void saveWave(Msg msg){
		List<UserMsg> userMsgs=msg.getUserMsg();
		int index=0;
		for(UserMsg userMsg:userMsgs){
			index=WSSSetupCl.getInstance().getChannel(userMsg.getSynWave());
			WaveDupl.duplWave[index]=true;
			index=WSSSetupCl.getInstance().getChannel(userMsg.getQuanWave());
			WaveDupl.duplWave[index]=true;
			index=WSSSetupCl.getInstance().getChannel(userMsg.getClassWave());
			WaveDupl.duplWave[index]=true;
		}
	}
	/**
	 * ��ɾ�������ݰ��Ĳ������и�λ
	 **/
	public void resetWave(Msg msg){
		List<UserMsg> userMsgs=msg.getUserMsg();
		int index=0;
		for(UserMsg userMsg:userMsgs){
			index=WSSSetupCl.getInstance().getChannel(userMsg.getSynWave());
			WaveDupl.duplWave[index]=false;
			index=WSSSetupCl.getInstance().getChannel(userMsg.getQuanWave());
			WaveDupl.duplWave[index]=false;
			index=WSSSetupCl.getInstance().getChannel(userMsg.getClassWave());
			WaveDupl.duplWave[index]=false;
		}
	}
	public byte[] makeMsgIsOk(byte ack,byte[] id){
		byte[] ackBytes=new byte[5];
		int len=id.length;
		ackBytes[0]=ack;
	    ackBytes[1]=id[len-4];
	    ackBytes[2]=id[len-3];
	    ackBytes[3]=id[len-2]; 
	    ackBytes[4]=id[len-1];
	    return ackBytes;
	}
}
