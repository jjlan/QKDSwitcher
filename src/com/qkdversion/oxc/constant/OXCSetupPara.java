package com.qkdversion.oxc.constant;

import android.text.InputFilter.LengthFilter;

/**
 * 定义OXC的配置参数
 * @author Ljj
 *
 */
public final class OXCSetupPara {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    
	//4*4OXC的IP地址
	public static final String OXC_1_IP="192.168.1.9";
	//波带级8*8OXC的IP地址
	public static final String OXC_2_IP="192.168.1.10"; 
	//波带级8*8OXC1的IP地址
	public static final String OXC_3_IP="192.168.1.13";
	/**
	 * 指令名称：POS
	 * 发送格式：POS  
	 * 功能：查询A->B通道配置情况，
	 * 返回参数格式：POS 1 2 3 4 5 6 7 8 
	 **/
	public final  static String  COMMEND_POS="POS";
	/**
	 * 指令名称：SET
	 * 发送格式：SET x x x x x x x x
	 * 功能：配置A->B通道连接情况
	 * 返回参数格式：SET x x x x x x x x
	 **/
	public final static String COMMEND_SET="SET";
	/**
	 * 指令名称：
	 * 发送格式：SET x x x x x x x x
	 * 功能：配置A->B通道连接情况
	 * 返回参数格式：SET x x x x x x x x
	 */
	/**
	 * 指令名称：IP
	 * 发送格式：IP 无参数用于查询IP配置情况   IP 有参数 用于配置IP
	 * 功能：查询IP或配置IP
	 * 返回参数格式：IP 192.168.10.100/24
	 **/
	public final static String COMMEND_IP="IP";
	/**
	 * 指令名称：GW
	 * 发送格式：GW 无参数用于查询网关配置情况   GW 有参数 用于配置GW 
	 * 功能：查询网关或配置网关
	 * 返回参数格式：GW 192.168.1.1
	 **/
	public final static String COMMEND_GW="GW";

/**********************************************************************************************************/
	/**
	 * 若为光纤级交换从第1路输入且目的地为IP1(第一输入通道和第三输出通道对应)
	 * (保存(1--3))
	 **/
	public final static String FIBER_1_IP1="SET 3 4 2 1";
	/**
	 * 若为光纤级交换从第1路输入且目的地为IP2(第一输入通道和第四输出通道对应)
	 * (保存1--4)
	 **/
	public final static String FIBER_1_IP2="SET 4 3 2 1";
	/**
	 * 若为光纤级交换从第2路输入且目的地为IP1(第二输入通道和第三输出通道对应)
	 * (保存2--3)
	 **/
	public final static String FIBER_2_IP1="SET 4 3 2 1";
	/**
	 * 若为光纤级交换从第2路输入且目的地为IP2(第二输入通道和第四输出通道对应)
	 * (保存2--4)
	 **/
	public final static String FIBER_2_IP2="SET 3 4 2 1";
/**********************************************************************************************************/
	/**
	 * 若为波带级交换从第1路输入(第一输入通道和第一输出通道相对应);若为波带级交换从第2路输入(第二输入通道和第二输出通道相对应)
	 * 若从第一路进来(保存1--1 3---3 4---4);若从第二路进来(保存2--2 3--3 4---4)
	 *  4*4
	 **/
	public final static String BAND_44_IN="SET 1 2 3 4";
	/**
	 * 若为波带级交换从第1路输入/第二路输入
	 * 若从第一路进来(保存1--1 2--4)/若从第二路进来(保存4--2，5---5)
	 * 8*8
	 **/
	public final static String BAND_88_IN="SET 1 4 7 2 5 8 3 6";
	/**********************************************************************************************************/	
	/**
	 * 情况一: 发送端没有本地用户的波长级交换
	 */
	/******************************************************************/
	/** 
	 * 波长级交换从第1路输入/第2路输入
	 * 若从第一路中进来(保存1--1 )/若从第二路进来(保存2--2 )
	 * 4*4
	 **/
	public final static String LENGTH1_F_IN="SET 1 2 3 4";
	/**
	 * 波长级交换从第1路输入
	 * 8*8(波带级配置)
	 * (保存信息：3--7 7--3,8--6)(根据用户是否同时进行波带级交换，进行保存相应的波带级信息1--1 2--4)
	 **/
	public final static String LENGTH1_B_1_IN="SET 1 4 7 2 5 8 3 6";
	/**
	 * 波长级交换从第2路输入
	 * 8*8(波带级配置)
	 * (保存信息：6--7 7--3 8--6)(根据用户是否同时进行波带级交换，进行保存相应的波带级信息4--2 5--5)
	 **/
	public final static String LENGTH1_B_2_IN="SET 1 4 8 2 5 7 3 6";
	 /** 
	  * 波长级交换(若Q4,S4到达的是目的IP1，D4到达的目的IP2)(01)
	  * 8*8(波长级配置)
	  * (保存信息：1--1 2--2 3--4)
	  **/
	public final static String LENGTH1_L_1_IN="SET 1 2 4 3 5 6 7 8";
	 /** 
	  * 波长级交换(若Q4,S4到达的是目的IP2，D4到达的目的IP1)(02)
	  * 8*8(波长级配置)
	  * (保存信息：1--4,2--5,3--3)
	  **/
	public final static String LENGTH1_L_2_IN="SET 4 5 3 1 2 6 7 8";
	 /** 
	  * 波长级交换(Q4,S4,D4都下到本地)(此时波带级的7--3,8--6都不用保存)(03)
	  * 8*8(波长级配置)
	  * (保存信息：1--6,2--7,3--8)
	  **/
	public final static String LENGTH1_L_3_IN="SET 6 7 8 4 5 1 2 3";
	 /** 
	  * 波长级交换(Q4,S4下到本地，D4到目的IP1)(此时波带级保存7--3)(04)
	  * 8*8(波长级配置)
	  * (保存信息：1--6,2--7,3--3)
	  **/
	public final static String LENGTH1_L_4_IN="SET 6 7 3 4 5 8 2 1";
	 /** 
	  * 波长级交换(Q4,S4下到本地，D4到目的IP2)(此时波带级的8--6)(05)
	  * 8*8(波长级配置)
	  * (保存信息：1--6,2--7,3--4)
	  **/
	public final static String LENGTH1_L_5_IN="SET 6 7 4 3 5 8 2 1";
	 /** 
	  * 波长级交换(Q4,S4去往IP1，D4到达本地)(此时波带级的3--7)(06)
	  * 8*8(波长级配置)
	  * (保存信息：1--1,2--2,3--8)
	  **/
	public final static String LENGTH1_L_6_IN="SET 1 2 8 3 5 4 6 7";
	 /** 
	  * 波长级交换(Q4,S4去往IP2，D4到达本地)(此时波带级的8--6)(07)
	  * 8*8(波长级配置)
	  * (保存信息：1--4,2--5,3--8)
	  **/
	public final static String LENGTH1_L_7_IN="SET 4 5 8 1 2 3 6 7";
	/******************************************************************/
	/**
	 * 情况二: 只有本地用户发送端参与的情况
	 */
	/** 
	 * 波长级交换从第1路输入/第2路输入
	 * 若从第一路中进来(保存1--1)/若从第二路进来(保存2--2)
	 * 4*4
	 **/
	 public final static String LENGTH2_F_IN="SET 1 2 3 4";
	/**
	 * 波长级交换从第1路输入
	 * 8*8(波带级配置)
	 * (根据用户是否同时进行波带级交换，进行保存相应的波带级信息1--1 2--4)
	 **/
	public final static String LENGTH2_B_1_IN="SET 1 4 7 2 5 8 3 6";
	/**
	 * 波长级交换从第2路输入
	 * 8*8(波带级配置)
	 * 根据用户是否同时进行波带级交换，进行保存相应的波带级信息4--2 5--5)
	 **/
	public final static String LENGTH2_B_2_IN="SET 1 4 8 2 5 7 3 6";
	/**
	 * 波长级交换(本地用户与本地接收用户进行交互)(01)
	 * 8*8(波长级配置)
	 * (保存信息：6--6,7--7,8--8)
	 **/
	public final static String LENGTH2_L_1_IN="SET 1 2 3 4 5 6 7 8";
	/**
	 * 波长级交换(本地用户Q4,S4去往IP1,D4去往IP2)(保存波带级的7--3 8--6)(02)
	 * 8*8(波长级配置)
	 * (保存信息：6--1,7--2,8--4)
	 **/
	public final static String LENGTH2_L_2_IN="SET 3 5 6 7 8 1 2 4";
	/**
	 * 波长级交换(本地用户Q4,S4去往IP1,D4去往本地)(保存波带级的7--3)(03)
	 * 8*8(波长级配置)
	 * (保存信息：6--1,7--2,8--8)
	 **/
	public final static String LENGTH2_L_3_IN="SET 3 5 6 7 4 1 2 8";
	/**
	 * 波长级交换(本地用户Q4,S4去往IP2,D4去往IP1)(保存波带级的7--3 8--6)(04)
	 * 8*8(波长级配置)
	 * (保存信息：6--4,7--5,8--3)
	 **/
	public final static String LENGTH2_L_4_IN="SET 1 2 6 7 8 4 5 3";
	/**
	 * 波长级交换(本地用户Q4,S4去往IP2,D4去往本地)(保存波带级的8--6)(05)
	 * 8*8(波长级配置)
	 * (保存信息：6--4,7--5,8--8)
	 **/
	public final static String LENGTH2_L_5_IN="SET 1 2 6 7 3 4 5 8";
	/**
	 * 波长级交换(本地用户Q4,S4去往本地,D4去往IP1)(保存波带级的7--3)(06)
	 * 8*8(波长级配置)
	 * (保存信息：6--4,7--5,8--8)
	 **/
	public final static String LENGTH2_L_6_IN="SET 1 2 6 7 3 4 5 8";
	/**
	 * 波长级交换(本地用户Q4,S4去往本地,D4去往IP2)(保存波带级的8--6)(07)
	 * 8*8(波长级配置)
	 * (保存信息：6--6,7--7,8--4)
	 **/
	public final static String LENGTH2_L_7_IN="SET 1 2 3 5 8 6 7 4";
	/**
	 * 波长级交换(本地用户Q4,S4,D4都去往IP1)(保存波带级的7--3)(08)
	 * 8*8(波长级配置)
	 * (保存信息：6--4,7--5,8--8)
	 **/
	public final static String LENGTH2_L_8_IN="SET 1 2 6 7 3 4 5 8";
	/**条件限制不能都到IP2***********************/
	
	
	
	
	
	
	
	
	
	
	
	 
	
}
