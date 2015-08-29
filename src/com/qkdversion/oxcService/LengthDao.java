package com.qkdversion.oxcService;

import java.util.List;

import com.qkdversion.OXCDomian.LengthType;
import com.qkdversion.OXCDomian.OXCSetupPara;
import com.qkdversion.msgDomain.Msg;
import com.qkdversion.msgDomain.MsgConstant;
import com.qkdversion.msgDomain.UserMsg;

/**
 * 波长级处理类
 * 处理流程:
 * 1.利用judgeLengthType函数判断波长级分配的几种情况 NOLOCAL ONLYLOCAL or BOTHHAVE
 * 2.利用LengthSwitch函数中根据judgeLengthType返回值进行分类，利用LengthDesx细分每种大情况下的小情况。
 * 3.根据LengthDesx的返回值调用getCommandFromx生成对用的command3（即波长级OXC配置参数）
 * 4.调用lengthConfig进行光开关状态。返回此次分配的状态码
 * 5.调用makeLengthAck函数生成对应的配置AckMsg数据包
 * @author hjj
 *
 */
public class LengthDao {
	private LengthDao(){};
	private static LengthDao lengthDao=new LengthDao();
	public static LengthDao getInstance(){
		return lengthDao;
	}
	
	/**
	 * 判断判断波长级交换应答结果中4*4中是否需要返回（3344）（判断依据为目的Ip中是否有IP1和Ip2）
	 */
	public int isContains34(Msg msg){
		int ack=-1;
		boolean flag3=false;
		boolean flag4=false;
		List<UserMsg> list=msg.getUserMsg();
		for(UserMsg user: list){
			if(MsgConstant.IP1.equals(user.getIpDes1())||MsgConstant.IP1.equals(user.getIpDes2())){
				flag3=true;
			}
			if(MsgConstant.IP2.equals(user.getIpDes1())||MsgConstant.IP2.equals(user.getIpDes2())){
				flag4=true;
			}
		}
		if(flag3==true&&flag4==false){
			ack=0;//只需返回33
		}else if(flag3==false&&flag4==true){
			ack=1;//只需返回44
		}else if(flag3==true&&flag4==true){
			ack=2;//同时返回3344
		}else{
			ack=3;//不需要设置
		}
		return ack;
	}
	/**
	 * 判断波长级交换类型
	 **/
	public int judgeLengthType(Msg msg) {
		if(!isContainsTransLocal(msg)){//如果不包含本地用户
		    return LengthType.NOLOCAL;
		}else if(isOnlyLocal(msg)){//只有本地用户
			return LengthType.ONLYLOCAL;  
		}
		return -1;
	}
    /**
     * 判断波长级交换中是否包含本地发送端用户
     **/
	private boolean isContainsTransLocal(Msg msg) {
		List<UserMsg> userList = msg.getUserMsg();
		for (UserMsg user :userList) { //如果来源IP为本地用户返回true
			if(MsgConstant.IPLocalIn.equals(user.getIpSource())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断波长级交换是否只包含本地用户
	 **/
	private boolean isOnlyLocal(Msg msg){
		List<UserMsg> userList = msg.getUserMsg();
		Boolean flag1=false;
		Boolean flag2=false;
		for (UserMsg user :userList) { 
			if(MsgConstant.IPLocalIn.equals(user.getIpSource())){//存在本地发送端
	            flag1=true; 
				continue;
			}
			if(!user.getIpDes2().equals("0")){//不存在非本地发送端（与波长级过来用户要与本地接收端通信冲突）
				flag2=true;
			}
		}
		if(flag1==true&&flag2==false){  //只有一个用户参与本地交互
		  return true;
		} 
		  return false;
	}
/*****************情况一*************/
	/**
	 * 一个波长级交换用户的目的地址区分(无本地用户时)(针对情况一)
	 **/
	public  int LengthDes1(Msg msg){
		List<UserMsg> userList = msg.getUserMsg();
		int ack=0;
		for (UserMsg user :userList){
			
			if(user.getIpDes1().equals(MsgConstant.IP1)&&user.getIpDes2().equals(MsgConstant.IP2)){//Q4,S4到达的是目的IP1，D4到达的目的IP2
				ack=1;
				break;
			}else if(user.getIpDes1().equals(MsgConstant.IP2)&&user.getIpDes2().equals(MsgConstant.IP1)){//Q4,S4到达的是目的IP2，D4到达目的IP1
				ack=2;
				break;
			}else if(user.getIpDes1().equals(MsgConstant.IPLocalOut)&&user.getIpDes2().equals("0")){//Q4,S4,D4都下到本地
				ack=3;
				break;
			}else if(user.getIpDes1().equals(MsgConstant.IPLocalOut)&&user.getIpDes2().equals(MsgConstant.IP1)){//Q4,S4下到本地，D4到目的IP1
				ack=4;
				break;
			}else if(user.getIpDes1().equals(MsgConstant.IPLocalOut)&&user.getIpDes2().equals(MsgConstant.IP2)){//Q4,S4下到本地，D4到目的IP2
				ack=5;
				break;
			}else if(user.getIpDes1().equals(MsgConstant.IP1)&&user.getIpDes2().equals(MsgConstant.IPLocalOut)){//Q4,S4去往IP1，D4到达本地
				ack=6;
				break;
			}else if(user.getIpDes1().equals(MsgConstant.IP2)&&user.getIpDes2().equals(MsgConstant.IPLocalOut)){//Q4,S4去往IP2，D4到达本地
				ack=7;
                break;				
			}
		}
		return ack;
	}
	/**
	 * 根据情况一的LengthDes1函数返回值生成对应的command3
	 **/
	 public  String getCommandFrom1(int ack){
		 String command3="";
		 switch (ack) {
		case 1://Q4,S4到达的是目的IP1，D4到达的目的IP2
			command3=OXCSetupPara.LENGTH1_L_1_IN;
			break;
        case 2://Q4,S4到达的是目的IP2，D4到达目的IP1
        	command3=OXCSetupPara.LENGTH1_L_2_IN;
			break;
        case 3:	//Q4,S4,D4都下到本地
        	command3=OXCSetupPara.LENGTH1_L_3_IN;
        	break; 
        case 4:	//Q4,S4下到本地，D4到目的IP1
        	command3=OXCSetupPara.LENGTH1_L_4_IN;
		    break;
        case 5: //Q4,S4下到本地，D4到目的IP2
        	command3=OXCSetupPara.LENGTH1_L_5_IN;
		    break;  
        case 6://Q4,S4去往IP1，D4到达本地
        	command3=OXCSetupPara.LENGTH1_L_6_IN;
        	break;
        case 7://Q4,S4去往IP2，D4到达本地
        	command3=OXCSetupPara.LENGTH1_L_7_IN;
        	break;
        default:
			break;
		}
		return command3;
	 }
	 
	 
 
     /**
	 * 一个波长级交换用户的目的地址区分(只有本地用户时)(针对情况2)
	 **/
	public  int LengthDes2(Msg msg){
		List<UserMsg> userList = msg.getUserMsg();
		int ack=0;
		for (UserMsg user :userList){
			if(MsgConstant.IPLocalIn.equals(user.getIpSource())){
				if(user.getIpDes1().equals(MsgConstant.IPLocalOut)&&user.getIpDes2().equals("0")){//地用户与本地接收用户进行交互
					ack=1;
					break;
				}else if(user.getIpDes1().equals(MsgConstant.IP1)&&user.getIpDes2().equals(MsgConstant.IP2)){//本地用户Q4,S4去往IP1,D4去往IP2
					ack=2;
					break;
				}else if(user.getIpDes1().equals(MsgConstant.IP1)&&user.getIpDes2().equals(MsgConstant.IPLocalOut)){//本地用户Q4,S4去往IP1,D4去往本地
					ack=3;
					break;
				}else if(user.getIpDes1().equals(MsgConstant.IP2)&&user.getIpDes2().equals(MsgConstant.IP1)){//本地用户Q4,S4去往IP2,D4去往IP1
					ack=4;
					break;
				}else if(user.getIpDes1().equals(MsgConstant.IP2)&&user.getIpDes2().equals(MsgConstant.IPLocalOut)){//本地用户Q4,S4去往IP2,D4去往本地
					ack=5;
					break;
				}else if(user.getIpDes1().equals(MsgConstant.IPLocalOut)&&user.getIpDes2().equals(MsgConstant.IP1)){//本地用户Q4,S4去往本地,D4去往IP1
					ack=6;
					break;
				}else if(user.getIpDes1().equals(MsgConstant.IPLocalOut)&&user.getIpDes2().equals(MsgConstant.IP2)){//本地用户Q4,S4去往本地,D4去往IP2
					ack=7;
	                break;				
				}else if(user.getIpDes1().equals(MsgConstant.IP1)&&user.getIpDes2().equals("0")){//本地用户Q4,S4,D4都去往IP1
					ack=8;
					break;
				}
			}			
		}
		return ack;
	}
	/**
	 * 根据情况2的LengthDes1函数返回值生成对应的command3
	 **/
	 public  String getCommandFrom2(int ack){
		 String command3="";
		 switch (ack) {
		case 1://本地用户与本地接收用户进行交互
			command3=OXCSetupPara.LENGTH2_L_1_IN;
			break;
        case 2://本地用户Q4,S4去往IP1,D4去往IP2
        	command3=OXCSetupPara.LENGTH2_L_2_IN;
			break;
        case 3://本地用户Q4,S4去往IP1,D4去往本地
        	command3=OXCSetupPara.LENGTH2_L_3_IN;
        	break; 
        case 4:	//本地用户Q4,S4去往IP2,D4去往IP1
        	command3=OXCSetupPara.LENGTH2_L_4_IN;
		    break;
        case 5: //本地用户Q4,S4去往IP2,D4去往本地
        	command3=OXCSetupPara.LENGTH2_L_5_IN;
		    break;  
        case 6://本地用户Q4,S4去往本地,D4去往IP1
        	command3=OXCSetupPara.LENGTH2_L_6_IN;
        	break;
        case 7://本地用户Q4,S4去往本地,D4去往IP2
        	command3=OXCSetupPara.LENGTH2_L_7_IN;
        	break;
        case 8://本地用户Q4,S4,D4都去往IP1
        	command3=OXCSetupPara.LENGTH2_L_8_IN;
        	break;
        default:
			break;
		}
		return command3;
	 }
	 

}
