package com.qkdversion.WSSDomian;

public class WSSCmdPara {
	/**
	 * ָ�����ƣ�SNO?
	 * ���͸�ʽ��SNO?  
	 * ���ܣ��鿴�������ú�
	 * ���ز�����ʽ��SNO39277
	 *            OK
	 *    ������Ϣ��
	 **/
	public final  static String  COMMEND_SN0="SNO?"+"\n";
	/**
	 * ָ�����ƣ�URA 
	 * ���͸�ʽ��URA 1,1,1.0;2,2,2.0
	 * ���ܣ���channel 1 to port 1 with 1 dB attenuation
	 * ���ز�����ʽ��URA
	 *            ok
	 **/
	public final static String COMMEND_URA="URA ";
	/**
	 * ָ�����ƣ�RSW
	 * ���͸�ʽ��RSW
	 * ���ܣ�update WSS configuration,һ�����URAָ���ʹ��
	 * ���ز�����ʽ:RSW 
	 *           OK
	 **/
	public final static String COMMEND_RSW="RSW"+"\n";
	/**
	 * ָ�����ƣ�RRA?
	 * ���͸�ʽ��RRA?
	 * ���ܣ�Read Back the new channel plan from the WSS 
	 * ���ز�����ʽ:RRA?
	 *           xxxxxxxxxxxxxxxxxxxxxxx
	 *           xxxxxxxxxxxxxxxxxxxxxxx
	 *           OK
	 **/
	public final static String COMMEND_RRA="RRA?"+"\n";
	/**
	 * ���ù��ٻ�����ֵ299792.50
	 */
    public final static double SPEED_CONSTANT=299792.5;
}
