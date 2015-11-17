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
	private int switchType;//0x00: 光纤级交换，0x01：波带级交换，0x02：波长级交换
	private int flag;//用于选择是从第一路进来还是从第二路进来
	private String fiberIp;//若为光纤级交换，则是目的IP，若是非光纤级交换，则为0.0.0.0
	private int UserNum;//用于此次传送一共有多少个用户包
	private List<UserMsg> userMsg=new ArrayList<UserMsg>();//用户包
	private int id=0;//结束标志
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
