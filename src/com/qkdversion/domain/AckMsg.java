package com.qkdversion.domain;
public class AckMsg {
	private byte ack; //��·����������
	private int id;//��¼��ӦMsg�е�id
	private String fiberAck;//4*4�⿪��ռ����·���
	private String bandAck;//8*8(������)�⿪��ռ����·���
	private  String lengthAck;//8*8(������)�⿪��ռ����·���
	public byte getAck() {
		return ack;
	}
	public void setAck(byte ack) {
		this.ack = ack;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFiberAck() {
		return fiberAck;
	}
	public void setFiberAck(String fiberAck) {
		this.fiberAck = fiberAck;
	}
	public String getBandAck() {
		return bandAck;
	}
	public void setBandAck(String bandAck) {
		this.bandAck = bandAck;
	}
	public String getLengthAck() {
		return lengthAck;
	}
	public void setLengthAck(String lengthAck) {
		this.lengthAck = lengthAck;
	}
}
