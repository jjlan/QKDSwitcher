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
 * 描述:主要逻辑在子线程中执行
 * @author Ljj 2015-04-24
 */
public class clientThread implements Runnable {
	static {
		System.loadLibrary("serialtest"); 
	}
	private static final String TAG = "clientThread"; 
	private serial com1;//第一个WSS
	private serial com2;//第二个WSS
	private Socket s;
	private boolean isFinish = false;// 用于判断一次分配是否完成
	private ArrayList<Msg> msgList;//用于保存已经分配成功还未释放的数据包信息
	private ArrayList<AckMsg> linkList;	//linklist用于保存已占用的链路信息
	private Msg msg;// 用于保存消息
	private byte[] bytes;// 用于接收消息	
	private OXCSetupCl oxc_1;//telnet控制器 oxc 4*4(光纤)
	private OXCSetupCl oxc_2;//telnet控制器 oxc 8*8(波带)
	private OXCSetupCl oxc_3;//telnet控制器 oxc 8*8(波长)
	@Override
	public void run() {   
		try {
			init();//初始化
			Log.i(TAG, "Thread" + Thread.currentThread().getName());
	        int initState= checkDevice();// 检查设备状况
	        Log.i(TAG, "设备检测结果："+initState);
			s = new Socket(ServerPara.Server_IP, ServerPara.Server_Port);//连接服务器
			byte[] byteStatus=new byte[1];
			byteStatus[0]=(byte)initState;
			writeToServer(byteStatus);	// 将设备状况向服务器反馈
			while (true) {
				bytes = readFromServer();// 等待服务器数据包,若无数据，会一直阻塞
				if (bytes != null) {
					Log.i(TAG, "get str from server" + new String(bytes));
					if(MsgDao.getInstance().isDisconnect(bytes)){//判断信令类型,是否是断开信令 
						
						byte[] msgOkByte=new byte[5];
						msgOkByte[0]=(byte)Ack.MESSAGE_OK;
						for(int i=1;i<msgOkByte.length;i++){
							msgOkByte[i]=bytes[i-1];
						}
						writeToServer(msgOkByte);//如果是断开的信令，也返回消息正确
						int id=MsgUtil.ByteToInt(bytes);
						Log.i(TAG,"当前信息列表的长度："+linkList.size());
						for(AckMsg ackMsg :linkList){
							if(ackMsg.getId()==id){//找到对应的数据包,从信息列表中删除
								linkList.remove(ackMsg);
							}
						}
						Log.i(TAG,"删除后应答信息列表的长度："+linkList.size());
						Log.i(TAG,"当前Msg信息列表的长度："+msgList.size());
						for(Msg msg1 :msgList){
							if(msg1.getId()==id){//找到对应的数据包,从信息列表中删除
								msgList.remove(msg1);
							}
							resetWave(msg1);//将已删除数据包对应波长的标识进行复位
						}
						Log.i(TAG,"删除后Msg信息列表的长度："+msgList.size());
						writeToServer(bytes);//反馈下删除包的Id号
						bytes=null;
						continue;
						
					}else{
						msg = MsgDao.getInstance().getMSg(bytes);// 将接收到的字符串信息封装成Message
						
						if(bytes!=null){
							byte mAck=(byte)Ack.MESSAGE_OK;
							byte[] b=makeMsgIsOk(mAck,bytes);
							writeToServer(b);
							Log.i("TAG", "消息正常应答："+mAck);
						}else{
							byte mAck=(byte)Ack.MESSAGE_ERR;
							byte[] b=makeMsgIsOk(mAck,bytes);
							writeToServer(b);
							Log.i("TAG", "消息应答异常："+mAck);
							continue;
						}
					}
					isFinish = true;
				}
				if (isFinish) {// 利用消息配置整个设备
					isFinish = false;
					if(BusyDao.getInstance().isLinkBusy(msgList,linkList,msg)){ //检测线路状况，决定是否需要分配
						byte mAck=(byte)Ack.LINKISBUSY;
						byte[] b=makeMsgIsOk(mAck,bytes);
						writeToServer(b);
						continue;
					}
					AckMsg resAck = messageDispatcher(msg);	// 进行数据包解析和数据分配,将结果封装成ackMsg对象
					byte[] bytes=MsgDao.getInstance().getAckStr(resAck);//将ackMsg对象转化成应答信令
					Log.i(TAG,"分配结果应答信令:"+new String(bytes));
					writeToServer(bytes);
					saveWave(msg);//保存住对应的波长
					msgList.add(msg);//保存下分配成功的信息用于检测链路是否可用
					bytes = null;// 将接收字符串数据置空
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace(); 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化函数
	 **/
	 private void init(){
	    com1= new serial();
		com1.Open(3, 115200,Com.WSS1);
		com2 = new serial();
	    com2.Open(4, 115200,Com.WSS2);
	    oxc_1=new OXCSetupCl();
		oxc_2=new OXCSetupCl();
		oxc_3=new OXCSetupCl();
		//初始化linklist表
		linkList=new ArrayList<AckMsg>();
		//初始化Msg表
		msgList=new ArrayList<Msg>();
		//用于判断波长是否重复，一旦重复，直接返回链路不可用
		for(int i=1;i<97;i++){
			WaveDupl.duplWave[i]=false;
		}
	 }
	/**
	 * 检查设备状况
	 **/
	private int checkDevice() {
		int device_flag=0;
		// 检查光纤级OXC开关
		int isConnect_1=oxc_1.connectToTelnet(OXCSetupPara.OXC_1_IP);
		if(OXCAckPara.CONNECT_FAIL==isConnect_1){
			device_flag=Ack.DEVICE_1_ERR;
			return device_flag;
		}
		// 检查波带级光开关
		int isConnect_2=oxc_2.connectToTelnet(OXCSetupPara.OXC_2_IP);
		if(OXCAckPara.CONNECT_FAIL==isConnect_2){
			device_flag=Ack.DEVICE_2_ERR;
			return device_flag;
		}
		// 检查波长级光开关
		int isConnect_3=oxc_3.connectToTelnet(OXCSetupPara.OXC_3_IP);
		if(OXCAckPara.CONNECT_FAIL==isConnect_3){
			device_flag=Ack.DEVICE_4_ERR;
			return device_flag;
		}
		// 检查WSS1
		if (!WSSSetupCl.getInstance().checkWSS(com1,Com.WSS1)) {
			device_flag=Ack.DEVICE_3_ERR;
			return device_flag;
		}
		// 检查WSS2
		if (!WSSSetupCl.getInstance().checkWSS(com2,Com.WSS2)) { 
			device_flag=Ack.DEVICE_5_ERR;
			return device_flag;
		}
		device_flag=Ack.DEVICE_OK;
		return device_flag;
	}

	
	/**
	 * 进行消息处理 
	 **/
	private AckMsg messageDispatcher(Msg msg) {
		int resFlag =0;	// 用于指示状态，一旦某个环节出错，立即返回对应的信息
		int switchType=msg.getSwitchType();
		AckMsg ackMsg=null;;//定义一个消息体
		switch(switchType){
		case MsgConstant.FIBER://光纤级交换
			 resFlag=fiberSwitch(msg,oxc_1);//进行光纤级交换
			 ackMsg=MsgDao.getInstance().makeFiberAck(resFlag,msg,linkList);//生成光纤级应答信息，并返回
			break;
		case MsgConstant.WAVEBAND://波带级交换
			resFlag=bandSwitch(msg,oxc_1,oxc_2);
			ackMsg=MsgDao.getInstance().makeBandAck(resFlag,msg,linkList);//生成波带级应答信息，并返回
			break;
		case MsgConstant.WAVELENGTH://波长级交换
			int lengthType=LengthDao.getInstance().judgeLengthType(msg);
			Log.i(TAG, "波长级分配情况选择结果:"+lengthType);
			resFlag=LengthSwitch(lengthType); 
			ackMsg=MsgDao.getInstance().makeLengthAck(lengthType,resFlag,msg,linkList);//生成波长级应答信息，并返回
			break;
		}
		return ackMsg;
	}


	/**
	 * 光纤级交换
	 **/
	private int fiberSwitch(Msg msg,OXCSetupCl oxcCl) {
		int fiberAck=0;   
			// 取出目的IP且是第一通道进来
			if (MsgConstant.IP1.equals(msg.getFiberIp())) {
				if(msg.getFlag()==MsgConstant.ONE){
					String command =OXCSetupPara.FIBER_1_IP1;// 目的IP和IP1相等，OXC4*4中通道1-----通道3,指令为set 3 x x x
					fiberAck=OXCService.getOXCService().oxcSwitch(oxcCl,OXCSetupPara.OXC_1_IP,command);
					Log.i(TAG,"光纤级交换目的地为IP1(第1路)"+fiberAck);
					if(fiberAck!=Ack.OXCSETUP_1_OK){
						return fiberAck;
					}
				}else if(msg.getFlag()==MsgConstant.TWO){
					String command =OXCSetupPara.FIBER_2_IP1;// 目的IP和IP1相等，OXC4*4中通道1-----通道3,指令为set 3 x x x
					fiberAck=OXCService.getOXCService().oxcSwitch(oxcCl,OXCSetupPara.OXC_1_IP,command);
					Log.i(TAG,"光纤级交换目的地为IP1(第2路)"+fiberAck);
					if(fiberAck!=Ack.OXCSETUP_1_OK){
						return fiberAck;
					}
				}
			} else if (MsgConstant.IP2.equals(msg.getFiberIp())) {
				if(msg.getFlag()==MsgConstant.ONE){
					String command =OXCSetupPara.FIBER_1_IP2;// 目的IP和IP1相等，OXC4*4中通道1-----通道4,指令为set 4 x x x
					fiberAck=OXCService.getOXCService().oxcSwitch(oxcCl,OXCSetupPara.OXC_1_IP,command);
					Log.i(TAG,"光纤级交换目的地为IP2(第1路)"+fiberAck);
					if(fiberAck!=Ack.OXCSETUP_1_OK){
						return fiberAck;
					}
				}else if(msg.getFlag()==MsgConstant.TWO){
					String command =OXCSetupPara.FIBER_2_IP2;// 目的IP和IP1相等，OXC4*4中通道1-----通道4,指令为set 4 x x x
					fiberAck=OXCService.getOXCService().oxcSwitch(oxcCl,OXCSetupPara.OXC_1_IP,command);
					Log.i(TAG,"光纤级交换目的地为IP2(第2路)"+fiberAck);
					if(fiberAck!=Ack.OXCSETUP_1_OK){
						return fiberAck;
					}
				}
			}
		fiberAck=Ack.FIBER_OK;
		return fiberAck;
	}
	/**
	 * 波带级交换
	 */
	private int bandSwitch(Msg msg, OXCSetupCl oxc_1,OXCSetupCl oxc_2) {
		int bandAck=0;
		//配置4*4
		String command =OXCSetupPara.BAND_44_IN;//每个通道和每个通道相对应
		bandAck=OXCService.getOXCService().oxcSwitch(oxc_1,OXCSetupPara.OXC_1_IP,command);
		if(Ack.OXCSETUP_1_OK!=bandAck)
			return bandAck;
		Log.i(TAG,"波带级交换4*4配置结果"+bandAck);
		//配置WSS
		if(MsgConstant.ONE==msg.getFlag()){	
			bandAck= WSSSetupCl.getInstance().setWssPort(msg, com1,Com.WSS1);
			if(Ack.WSS1SENDDATA_OK!=bandAck){//配置失败，直接将信息返回去 
				return bandAck;
			}
		}else if(MsgConstant.TWO==msg.getFlag()){ 
			bandAck= WSSSetupCl.getInstance().setWssPort(msg, com2,Com.WSS2); 
			if(Ack.WSS2SENDDATA_OK!=bandAck){//配置失败，直接将信息返回去 
				return bandAck;
			}
		}
		Log.i(TAG,"Wss配置结果"+bandAck);
		//配置波带级8*8
		command=OXCSetupPara.BAND_88_IN;
		bandAck=OXCService.getOXCService().oxcSwitch(oxc_2,OXCSetupPara.OXC_2_IP,command);
		if(Ack.OXCSETUP_2_OK!=bandAck)
			return bandAck;
		Log.i(TAG,"波带级交换8*8配置结果"+bandAck);
		bandAck=Ack.BAND_OK;
		return bandAck;
	}
    /**
     * 波长级交换
     */
	private int LengthSwitch(int type) {
		String command1=null;//4*4
		String command2=null;//8*8
		String command3=null;//8*8
		int ack=-1;
		int lengthAck=-1;
		switch(type){
			case LengthType.NOLOCAL://无本地发送端用户
				command1=OXCSetupPara.LENGTH1_F_IN;
				if(msg.getFlag()==MsgConstant.ONE){//通过判断用户是从哪一路来来选择波带级配置情况
					command2=OXCSetupPara.LENGTH1_B_1_IN;
				}else{
					command2=OXCSetupPara.LENGTH1_B_2_IN;
				}
				//通过判断目的地址进行区分，目的IP可能是IP1，IP2和IP4
				lengthAck=LengthDao.getInstance().LengthDes1(msg);//对情况1的种类进行判断
				Log.i(TAG, "无本地发送端用户参与的情况:"+lengthAck);
				 command3=LengthDao.getInstance().getCommandFrom1(lengthAck);
				 ack=lengthConfig(command1,command2,command3);
				 break;
			case LengthType.ONLYLOCAL://只有本地发送端用户
				command1=OXCSetupPara.LENGTH1_F_IN;
				if(msg.getFlag()==MsgConstant.ONE){//通过判断用户是从哪一路来来选择波带级配置情况
					command2=OXCSetupPara.LENGTH1_B_1_IN;
				}else{
					command2=OXCSetupPara.LENGTH1_B_2_IN;
				}
				 lengthAck=LengthDao.getInstance().LengthDes2(msg);//对情况2的种类进行判断
				 Log.i(TAG, "只有本地发送端用户参与的情况:"+lengthAck);
				 command3=LengthDao.getInstance().getCommandFrom2(lengthAck);
				 ack=lengthConfig(command1,command2,command3);
			 break;
				
			default:
				break;	
		}
		return ack;
		
	}
	/**
	 * 波长级交换配置函数
	 */
	private int lengthConfig(String command1,String command2,String command3){
		int lengthAck=0;
	    //配置4*4
	    lengthAck=OXCService.getOXCService().oxcSwitch(oxc_1,OXCSetupPara.OXC_1_IP, command1);
	    if(Ack.OXCSETUP_1_OK!=lengthAck)
	    	return lengthAck;
	    Log.i(TAG,"波长级交换4*4配置结果"+lengthAck);
	    //配置WSS
	    if(MsgConstant.ONE==msg.getFlag()){	
	    	lengthAck= WSSSetupCl.getInstance().setWssPort(msg, com1,Com.WSS1);
			if(Ack.WSS1SENDDATA_OK!=lengthAck){//配置失败，直接将信息返回去 
				return lengthAck;
			}
		}else if(MsgConstant.TWO==msg.getFlag()){
			lengthAck= WSSSetupCl.getInstance().setWssPort(msg, com2,Com.WSS2);
			if(Ack.WSS2SENDDATA_OK!=lengthAck){//配置失败，直接将信息返回去 
				return lengthAck;
			}
		}
	    //配置波带级8*8
	  	lengthAck=OXCService.getOXCService().oxcSwitch(oxc_2,OXCSetupPara.OXC_2_IP,command2);
	  	if(Ack.OXCSETUP_2_OK!=lengthAck)
	  	 return lengthAck;
	  	Log.i(TAG,"波带级交换8*8配置结果"+lengthAck);
	   //配置波长级8*8
	  	lengthAck=OXCService.getOXCService().oxcSwitch(oxc_3,OXCSetupPara.OXC_3_IP,command3);
	  	if(Ack.OXCSETUP_3_OK!=lengthAck)
		  	return lengthAck;
		Log.i(TAG,"波长级交换8*8配置结果"+lengthAck);
		lengthAck=Ack.LEGNTH_OK;
		return lengthAck;
	}
	/**
	 * 从服务器读取信息
	 **/
//	private String readFromServer() {
//		String str = "";
//		InputStream is = null;
//		try {
//			if (s != null) {
//				is = s.getInputStream();
//				//读取包长度
//				byte[] bytes = new byte[4];
//				is.read(bytes);
//				int msgLen=MsgUtil.ByteToInt(bytes);
//				Log.i(TAG,"包长度："+msgLen);
//				byte[] msgBytes=new byte[msgLen];
//				int len= is.read(msgBytes);
//				if (len != -1) {
//					str = new String(msgBytes, 0, len, "utf-8");
//				}
//				Log.i(TAG,"消息长度："+len+"消息内容："+str);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return str;
//	}
	/**
	 * 从服务器读取信息
	 **/
	private byte[] readFromServer() {
		InputStream is = null;
		byte[] msgBytes=null;
		try {
			if (s != null) {
				is = s.getInputStream();
				//读取包长度
				byte[] bytes = new byte[4];
				is.read(bytes);
				int msgLen=MsgUtil.ByteToInt(bytes);
				Log.i(TAG,"包长度："+msgLen);
				msgBytes=new byte[msgLen];
				int len= is.read(msgBytes);
				Log.i(TAG,"消息长度："+len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return msgBytes;
	}
	/**
	 * 向服务器返回信息 
	 **/
	private void writeToServer(byte[] ack) {
		OutputStream os = null;
		try {
			if (s != null) {
				os = s.getOutputStream();
				byte[] bytes=MsgUtil.IntToByte(ack.length);
				os.write(bytes);
				Log.i(TAG, "应带信号的一个字节为："+ack[0]);
				os.write(ack);
			}
		} catch (IOException e) {
			e.printStackTrace(); 
		}
	}
	/**
	 * 向服务器返回信息 
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
	 * 将成功分配且未释放的波长进行标识
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
	 * 将删除的数据包的波长进行复位
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
