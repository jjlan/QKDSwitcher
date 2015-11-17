package com.qkdversion.wssdao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.qkdversion.constant.Ack;
import com.qkdversion.constant.MsgConstant;
import com.qkdversion.domain.Msg;
import com.qkdversion.domain.UserMsg;
import com.qkdversion.wss.constant.WSSCmdPara;
import com.topeet.serialtest.Com;
import com.topeet.serialtest.serial;

/**
 * 功能描述：WSS控制层
 * @author Ljj 类型：WSS控制层
 */

public class WSSSetupCl {
	private static final String TAG = "WSSSetupCl";
	private static WSSSetupCl instance = new WSSSetupCl();
	private WSSSetupCl() {
	}
	public static WSSSetupCl getInstance() {
		return instance;
	}
	private String resetStr=null;//用于恢复配置
	/**
	 * 发送指令，读取结果
	 **/
	public String sendCmd(String cmd, serial com,int flag) {
		writeToSerial(cmd, com,flag);
		try {
			if (cmd.contains("RRA?")||cmd.contains("URA")) {
				Thread.currentThread().sleep(100);
			} else if(cmd.contains("RSW")){
				Thread.currentThread().sleep(1000);
			}else{
				Thread.currentThread().sleep(70);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String res = readFromSerial(cmd, com,flag);
		return res;
	}
	/**
	 * 串口发送命令函数
	 **/
	public void writeToSerial(String tx, serial com,int flag) {
		
		int[] text = new int[tx.length()];
		for (int i = 0; i < tx.length(); i++) {
			text[i] = tx.charAt(i);
		}
		com.Write(text, tx.length(),flag);
	}
	/**
	 * 串口读取结果函数
	 **/
	public String readFromSerial(String tx, serial com,int flag) {
		int[] RX = null;
		// int len=0;
		String strRx = "";
		RX = com.Read(flag);
		if (RX == null) {
			RX = com.Read(flag);
			if (RX == null)
				return null;
		}
		strRx = new String(RX, 0, RX.length);
		return strRx;
	}
	/**
	 * 检查WSS 通过向串口发送SN0进行确认 若回复中包含SN0字符，则认为正确 否则再次发送进行确认，四次都失败，则认为有问题
	 **/
	public boolean checkWSS(serial com,int flag) {
		String cmd = null;
		String resStr = null;
		cmd = WSSCmdPara.COMMEND_SN0;
		resStr = WSSSetupCl.getInstance().sendCmd(cmd, com,flag);
		if (resStr == null) {
			resStr = WSSSetupCl.getInstance().sendCmd(cmd, com,flag);
			if (resStr == null) {
				return false;
			}
		}
		if (resStr.contains("SN127049")||resStr.contains("SN126726")) {
			Log.i(TAG, "序列号："+resStr+"WSS device is ok");
			return true;
		}
		return false; 
	}
	/**
	 * 设置函数(因为RSW更新指令很耗时，所以暂时不使用记录恢复默认操作)
	 * 若使用，则打开函数中注释部分
	 **/
	public int  setWssPort(Msg msg, serial com,int flag) {
		int ack=0;
		String[] res=makeCommand(msg,com);
      /*if(resetStr!=null){//进行复位
		Log.i(TAG,"resetStr的值"+resetStr);
		ack=sendWssCmd(resetStr,com);
		    if(!Ack.WSSSENDDATA_OK.equals(ack)) 
		    	return ack;
		}
		resetStr=res[1];*/
		ack=sendWssCmd(res[0], com,flag);
		if(flag==Com.WSS1){
		 Log.i(TAG, "wss1分配结果："+res[0]);
		}else{
		 Log.i(TAG,"wss2分配结果："+res[0]);
		}
		//Log.i(TAG, res[0]); 
		return ack;//进行配置
	}
	/**
	 * 功能描述:根据给定波长或波长范围判定是哪个通道的数据 返回值：通道的值 进行就近取整
	 **/
	public  int getChannel(double wave) {
		Double temp = 1 + (WSSCmdPara.SPEED_CONSTANT / wave - 191.35) * 20;
		return (int) (temp + 0.5);
	}
	/**
	 * 所属范围：波带级交换 功能描述:将message中的每个用户的三个波长进行判断并将每个用户的波长合并到一起（多个用户用不用合并到一根输出待定）
	 * 设置:利用wss的三根输出线，1号线代表到目的地址1（假设为10.109.0.1）,2号线代表到目的地址2（10.109.0.2），3
	 * 号线代表要进行波带级交换用户 传入参数:message 返回参数：对应待设置WSS通道数
	 */
	public Map<String, List<Integer>> channelToPort(Msg msg) {
		List<UserMsg> users = msg.getUserMsg();
		// 用于保存解析结果
		Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();
		List<Integer> list_Ip1 = new ArrayList<Integer>();
		List<Integer> list_Ip2 = new ArrayList<Integer>();
		List<Integer> list_IpNone = new ArrayList<Integer>();
		// 此处需要判断一下目的地址是否有两个
		for (UserMsg user : users) {
			//只要目的地址中有本地发送或接收端，通通加入到list_IpNone
			if(MsgConstant.IPLocalIn.equals(user.getIpSource())||MsgConstant.IPLocalOut.equals(user.getIpDes1())||MsgConstant.IPLocalOut.equals(user.getIpDes2())){
				list_IpNone.add(getChannel(user.getSynWave()));
				list_IpNone.add(getChannel(user.getQuanWave()));
				list_IpNone.add(getChannel(user.getClassWave()));
				continue;
			}
			// 三个光都到同一个IP
			if ("0".equals(user.getIpDes2())) {
				// 检查对应用户的目的IP地址
				if (MsgConstant.IP1.equals(user.getIpDes1())) {
					// 将每个用户的三个光通道添加到list_IP1
					list_Ip1.add(getChannel(user.getSynWave()));
					list_Ip1.add(getChannel(user.getQuanWave()));
					list_Ip1.add(getChannel(user.getClassWave()));
				} else if (MsgConstant.IP2.equals(user.getIpDes1())) {
					// 将每个用户的三个光通道添加到list_IP2
					list_Ip2.add(getChannel(user.getSynWave()));
					list_Ip2.add(getChannel(user.getQuanWave()));
					list_Ip2.add(getChannel(user.getClassWave()));
				}
			} else {// 三个光去的不是一个地方
				list_IpNone.add(getChannel(user.getSynWave()));
				list_IpNone.add(getChannel(user.getQuanWave()));
				list_IpNone.add(getChannel(user.getClassWave()));
			}
		}
		map.put("IP1", list_Ip1);
		map.put("IP2", list_Ip2);
		map.put("IPNone", list_IpNone);
		return map;
	}
	/**
	 * 根据通道和端口的映射关系设置WSS ：如果出错，最大的可能是延时不够，要加大sleep的时间
	 * 返回值:为一个String数组，String[0]位置为要配置的指令，String[1]位置为用于复位用的指令
	 **/
	public String[] makeCommand(Msg msg, serial com) {
		String[] res=new String[2];
		Map<String, List<Integer>> map = channelToPort(msg);
		StringBuilder sb = new StringBuilder();
		StringBuilder strLast=new StringBuilder();//保存本次配置的结果，用于下次配置前进行复位
		sb.append(WSSCmdPara.COMMEND_URA);
		strLast.append(WSSCmdPara.COMMEND_URA);
		List<Integer> list1 = map.get("IP1");
		Log.i(TAG,"波带级到达IP1的用户个数："+list1.size()/3);
		for (Integer channel : list1) {
			sb.append(String.valueOf(channel) + ",1," + "0.0;");
			strLast.append(String.valueOf(channel) + ",99," + "99.9;");
		}
		List<Integer> list2 = map.get("IP2");
		Log.i(TAG,"波带级到达IP2的用户个数："+list2.size()/3);
		for (Integer channel : list2) {
			sb.append(String.valueOf(channel) + ",2," + "0.0;");
			strLast.append(String.valueOf(channel) + ",99," + "99.9;");
		}
		List<Integer> list3 = map.get("IPNone");
		Log.i(TAG,"波带级下行到波长级的用户个数："+list3.size()/3);
		for (Integer channel : list3) {
			sb.append(String.valueOf(channel) + ",3," + "0.0;");
			strLast.append(String.valueOf(channel) + ",99," + "99.9;");
		}
		String cmd = sb.toString();
		String last =strLast.toString();
		// 去掉发送cmd最后的;号
		cmd = cmd.substring(0, cmd.length() - 1);
		last= last.substring(0, last.length() - 1);
		cmd = cmd + "\n";
		last=last+ "\n";
		res[0]=cmd;
		res[1]=last;
		return res;
		
	}
    /**
     * 发送指令并检查是否发送成功
     **/
	public int sendWssCmd(String cmd,serial com,int flag){
		int ack=0;
		String res = sendCmd(cmd, com,flag);
		if (res == null) {
			res = sendCmd(cmd, com,flag);
			if(res==null){
				if(Com.WSS1==flag)
				ack=Ack.WSS1SENDDATA_ERR;//WSS写入数据失败
				else 
				ack=Ack.WSS2SENDDATA_ERR;
				return ack;
			}
		}
		if (res.contains("OK")) {
			cmd = WSSCmdPara.COMMEND_RSW;
			res = sendCmd(cmd, com,flag);
			if (!res.contains("OK")) {
				Log.i(TAG, "RSW失败");
				if(Com.WSS1==flag)
				ack=Ack.WSS1UPDATE_ERR;//WSS更新数据失败
				else 
                ack=Ack.WSS2UPDATE_ERR;
				return ack;
			} 
		}
		if(Com.WSS1==flag)
		ack=Ack.WSS1SENDDATA_OK;
		else 
		ack=Ack.WSS2SENDDATA_OK;	
		return ack;
	}
}
