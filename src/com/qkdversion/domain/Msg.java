package com.qkdversion.domain;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
/**
 * 
 * @author hjj
 * 
 */
public class Msg {
	private int switchType;//0x00: ���˼�������0x01��������������0x02������������
	private int flag;//����ѡ���Ǵӵ�һ·�������Ǵӵڶ�·����
	private String fiberIp;//��Ϊ���˼�����������Ŀ��IP�����Ƿǹ��˼���������Ϊ0.0.0.0
	private int UserNum;//���ڴ˴δ���һ���ж��ٸ��û���
	private List<UserMsg> userMsg=new ArrayList<UserMsg>();//�û���
	private int id=0;//������־
	public int getSwitchType() {
		return switchType;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public void setSwitchType(int switchType) {
		this.switchType = switchType;
	}
	public String getFiberIp() {
		return fiberIp;
	}
	public void setFiberIp(String fiberIp) {
		this.fiberIp = fiberIp;
	}
	public int getUserNum() {
		return UserNum;
	}
	public void setUserNum(int userNum) {
		UserNum = userNum;
	}
	public List<UserMsg> getUserMsg() {
		return userMsg;
	}
	public void setUserMsg(List<UserMsg> userMsg) {
		this.userMsg = userMsg;
	}
	public int getId() { 
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
