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
 * ����������WSS���Ʋ�
 * @author Ljj ���ͣ�WSS���Ʋ�
 */

public class WSSSetupCl {
	private static final String TAG = "WSSSetupCl";
	private static WSSSetupCl instance = new WSSSetupCl();
	private WSSSetupCl() {
	}
	public static WSSSetupCl getInstance() {
		return instance;
	}
	private String resetStr=null;//���ڻָ�����
	/**
	 * ����ָ���ȡ���
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
	 * ���ڷ��������
	 **/
	public void writeToSerial(String tx, serial com,int flag) {
		
		int[] text = new int[tx.length()];
		for (int i = 0; i < tx.length(); i++) {
			text[i] = tx.charAt(i);
		}
		com.Write(text, tx.length(),flag);
	}
	/**
	 * ���ڶ�ȡ�������
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
	 * ���WSS ͨ���򴮿ڷ���SN0����ȷ�� ���ظ��а���SN0�ַ�������Ϊ��ȷ �����ٴη��ͽ���ȷ�ϣ��Ĵζ�ʧ�ܣ�����Ϊ������
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
			Log.i(TAG, "���кţ�"+resStr+"WSS device is ok");
			return true;
		}
		return false; 
	}
	/**
	 * ���ú���(��ΪRSW����ָ��ܺ�ʱ��������ʱ��ʹ�ü�¼�ָ�Ĭ�ϲ���)
	 * ��ʹ�ã���򿪺�����ע�Ͳ���
	 **/
	public int  setWssPort(Msg msg, serial com,int flag) {
		int ack=0;
		String[] res=makeCommand(msg,com);
      /*if(resetStr!=null){//���и�λ
		Log.i(TAG,"resetStr��ֵ"+resetStr);
		ack=sendWssCmd(resetStr,com);
		    if(!Ack.WSSSENDDATA_OK.equals(ack)) 
		    	return ack;
		}
		resetStr=res[1];*/
		ack=sendWssCmd(res[0], com,flag);
		if(flag==Com.WSS1){
		 Log.i(TAG, "wss1��������"+res[0]);
		}else{
		 Log.i(TAG,"wss2��������"+res[0]);
		}
		//Log.i(TAG, res[0]); 
		return ack;//��������
	}
	/**
	 * ��������:���ݸ��������򲨳���Χ�ж����ĸ�ͨ�������� ����ֵ��ͨ����ֵ ���оͽ�ȡ��
	 **/
	public  int getChannel(double wave) {
		Double temp = 1 + (WSSCmdPara.SPEED_CONSTANT / wave - 191.35) * 20;
		return (int) (temp + 0.5);
	}
	/**
	 * ������Χ������������ ��������:��message�е�ÿ���û����������������жϲ���ÿ���û��Ĳ����ϲ���һ�𣨶���û��ò��úϲ���һ�����������
	 * ����:����wss����������ߣ�1���ߴ���Ŀ�ĵ�ַ1������Ϊ10.109.0.1��,2���ߴ���Ŀ�ĵ�ַ2��10.109.0.2����3
	 * ���ߴ���Ҫ���в����������û� �������:message ���ز�������Ӧ������WSSͨ����
	 */
	public Map<String, List<Integer>> channelToPort(Msg msg) {
		List<UserMsg> users = msg.getUserMsg();
		// ���ڱ���������
		Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();
		List<Integer> list_Ip1 = new ArrayList<Integer>();
		List<Integer> list_Ip2 = new ArrayList<Integer>();
		List<Integer> list_IpNone = new ArrayList<Integer>();
		// �˴���Ҫ�ж�һ��Ŀ�ĵ�ַ�Ƿ�������
		for (UserMsg user : users) {
			//ֻҪĿ�ĵ�ַ���б��ط��ͻ���նˣ�ͨͨ���뵽list_IpNone
			if(MsgConstant.IPLocalIn.equals(user.getIpSource())||MsgConstant.IPLocalOut.equals(user.getIpDes1())||MsgConstant.IPLocalOut.equals(user.getIpDes2())){
				list_IpNone.add(getChannel(user.getSynWave()));
				list_IpNone.add(getChannel(user.getQuanWave()));
				list_IpNone.add(getChannel(user.getClassWave()));
				continue;
			}
			// �����ⶼ��ͬһ��IP
			if ("0".equals(user.getIpDes2())) {
				// ����Ӧ�û���Ŀ��IP��ַ
				if (MsgConstant.IP1.equals(user.getIpDes1())) {
					// ��ÿ���û���������ͨ����ӵ�list_IP1
					list_Ip1.add(getChannel(user.getSynWave()));
					list_Ip1.add(getChannel(user.getQuanWave()));
					list_Ip1.add(getChannel(user.getClassWave()));
				} else if (MsgConstant.IP2.equals(user.getIpDes1())) {
					// ��ÿ���û���������ͨ����ӵ�list_IP2
					list_Ip2.add(getChannel(user.getSynWave()));
					list_Ip2.add(getChannel(user.getQuanWave()));
					list_Ip2.add(getChannel(user.getClassWave()));
				}
			} else {// ������ȥ�Ĳ���һ���ط�
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
	 * ����ͨ���Ͷ˿ڵ�ӳ���ϵ����WSS ������������Ŀ�������ʱ������Ҫ�Ӵ�sleep��ʱ��
	 * ����ֵ:Ϊһ��String���飬String[0]λ��ΪҪ���õ�ָ�String[1]λ��Ϊ���ڸ�λ�õ�ָ��
	 **/
	public String[] makeCommand(Msg msg, serial com) {
		String[] res=new String[2];
		Map<String, List<Integer>> map = channelToPort(msg);
		StringBuilder sb = new StringBuilder();
		StringBuilder strLast=new StringBuilder();//���汾�����õĽ���������´�����ǰ���и�λ
		sb.append(WSSCmdPara.COMMEND_URA);
		strLast.append(WSSCmdPara.COMMEND_URA);
		List<Integer> list1 = map.get("IP1");
		Log.i(TAG,"����������IP1���û�������"+list1.size()/3);
		for (Integer channel : list1) {
			sb.append(String.valueOf(channel) + ",1," + "0.0;");
			strLast.append(String.valueOf(channel) + ",99," + "99.9;");
		}
		List<Integer> list2 = map.get("IP2");
		Log.i(TAG,"����������IP2���û�������"+list2.size()/3);
		for (Integer channel : list2) {
			sb.append(String.valueOf(channel) + ",2," + "0.0;");
			strLast.append(String.valueOf(channel) + ",99," + "99.9;");
		}
		List<Integer> list3 = map.get("IPNone");
		Log.i(TAG,"���������е����������û�������"+list3.size()/3);
		for (Integer channel : list3) {
			sb.append(String.valueOf(channel) + ",3," + "0.0;");
			strLast.append(String.valueOf(channel) + ",99," + "99.9;");
		}
		String cmd = sb.toString();
		String last =strLast.toString();
		// ȥ������cmd����;��
		cmd = cmd.substring(0, cmd.length() - 1);
		last= last.substring(0, last.length() - 1);
		cmd = cmd + "\n";
		last=last+ "\n";
		res[0]=cmd;
		res[1]=last;
		return res;
		
	}
    /**
     * ����ָ�����Ƿ��ͳɹ�
     **/
	public int sendWssCmd(String cmd,serial com,int flag){
		int ack=0;
		String res = sendCmd(cmd, com,flag);
		if (res == null) {
			res = sendCmd(cmd, com,flag);
			if(res==null){
				if(Com.WSS1==flag)
				ack=Ack.WSS1SENDDATA_ERR;//WSSд������ʧ��
				else 
				ack=Ack.WSS2SENDDATA_ERR;
				return ack;
			}
		}
		if (res.contains("OK")) {
			cmd = WSSCmdPara.COMMEND_RSW;
			res = sendCmd(cmd, com,flag);
			if (!res.contains("OK")) {
				Log.i(TAG, "RSWʧ��");
				if(Com.WSS1==flag)
				ack=Ack.WSS1UPDATE_ERR;//WSS��������ʧ��
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
