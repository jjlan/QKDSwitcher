package com.qkdversion.domain;

import android.R.integer;

public class UserMsg {
  //用户源IP
  private String IpSource;
  //同步光波长
  private double synWave;
  //量子光波长
  private double quanWave;
  //经典光波长
  private double classWave;
  //目的IP1
  private String IpDes1;
  //目的IP2
  private String IpDes2;
public String getIpSource() {
	return IpSource;
}
public void setIpSource(String ipSource) {
	IpSource = ipSource;
}
public double getSynWave() {
	return synWave;
}
public void setSynWave(Double synWave) {
	this.synWave = synWave; 
}
public double getQuanWave() {
	return quanWave;
}
public void setQuanWave(Double quanWave) {
	this.quanWave = quanWave;
}
public double getClassWave() {
	return classWave;
}
public void setClassWave(Double classWave) {
	this.classWave = classWave;
} 
public String getIpDes1() {
	return IpDes1;
}
public void setIpDes1(String ipDes1) {
	IpDes1 = ipDes1;
}
public String getIpDes2() {
	return IpDes2;
}
public void setIpDes2(String ipDes2) {
	IpDes2 = ipDes2;
}
  
}
