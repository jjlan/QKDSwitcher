package com.qkdversion.domain;

import android.R.integer;

public class UserMsg {
  //�û�ԴIP
  private String IpSource;
  //ͬ���Ⲩ��
  private double synWave;
  //���ӹⲨ��
  private double quanWave;
  //����Ⲩ��
  private double classWave;
  //Ŀ��IP1
  private String IpDes1;
  //Ŀ��IP2
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
