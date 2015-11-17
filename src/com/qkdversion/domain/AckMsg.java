package com.qkdversion.domain;
public class AckMsg {
	private byte ack; //链路分配结果反馈
	private int id;//记录相应Msg中的id
	private String fiberAck;//4*4光开关占用链路情况
	private String bandAck;//8*8(波带级)光开关占用链路情况
	private  String lengthAck;//8*8(波长级)光开关占用链路情况
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
