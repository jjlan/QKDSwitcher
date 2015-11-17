package com.qkdversion.constant;


public final class Ack {
	/**
	 *  检测设备阶段应答值
	 **/
	public static final int DEVICE_OK=1;
	//4*4光开关连接异常
	public static final int DEVICE_1_ERR=2; 
	//8*8光开关连接异常(波带级)
	public static final int DEVICE_2_ERR=3;
	//8*8光开关连接异常(波长级)
	public static final int DEVICE_4_ERR=27;
	//wss1连接异常
	public static final int DEVICE_3_ERR=4;
	//wss2连接异常
	public static final int DEVICE_5_ERR=30;
	
	public static final int MESSAGE_OK=5;
	//message is error
	public static final int MESSAGE_ERR=6;
	//messageHandler
	public static final String DISPATCHER_OK="dispatcher_ok";
	public static final String DISPATCHER_ERR="dispatcher_fail";
	
	/**
	 * 4*4 OXC应答列表
	 **/
	public static final int OXCSETUP_1_OK=10;
	public static final int OXCSETUP_1_ERR=11;
	public static final int FIBER_OK=12;
	/**
	 * WSS应答参数列表
	 **/ 
	public static final int WSS1SENDDATA_OK=20;
	public static final int WSS1SENDDATA_ERR=21;
	public static final int WSS1UPDATE_OK=22;
	public static final int WSS1UPDATE_ERR=23;
	
	public static final int WSS2SENDDATA_OK=31;
	public static final int WSS2SENDDATA_ERR=32;
	public static final int WSS2UPDATE_OK=33;
	public static final int WSS2UPDATE_ERR=34;
	/**
	 * 波带级8*8 OXC应答列表
	 **/
	public static final int OXCSETUP_2_OK=24; 
	public static final int OXCSETUP_2_ERR=25; 
	public static final int BAND_OK=26; 
	
	/**
	 * 波长级8*8 OXC应答列表 
	 **/
	public static final int OXCSETUP_3_OK=27; 
	public static final int OXCSETUP_3_ERR=28; 
	public static final int LEGNTH_OK=29; 
    
	
	/**
	 * 链路不可用
	 **/
	public static final int LINKISBUSY=35;

	 

}
