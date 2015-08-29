package com.qkdversion.oxcService;

import java.util.List;

import com.qkdversion.OXCDomian.LengthType;
import com.qkdversion.OXCDomian.OXCSetupPara;
import com.qkdversion.msgDomain.Msg;
import com.qkdversion.msgDomain.MsgConstant;
import com.qkdversion.msgDomain.UserMsg;

/**
 * ������������
 * ��������:
 * 1.����judgeLengthType�����жϲ���������ļ������ NOLOCAL ONLYLOCAL or BOTHHAVE
 * 2.����LengthSwitch�����и���judgeLengthType����ֵ���з��࣬����LengthDesxϸ��ÿ�ִ�����µ�С�����
 * 3.����LengthDesx�ķ���ֵ����getCommandFromx���ɶ��õ�command3����������OXC���ò�����
 * 4.����lengthConfig���й⿪��״̬�����ش˴η����״̬��
 * 5.����makeLengthAck�������ɶ�Ӧ������AckMsg���ݰ�
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
	 * �ж��жϲ���������Ӧ������4*4���Ƿ���Ҫ���أ�3344�����ж�����ΪĿ��Ip���Ƿ���IP1��Ip2��
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
			ack=0;//ֻ�践��33
		}else if(flag3==false&&flag4==true){
			ack=1;//ֻ�践��44
		}else if(flag3==true&&flag4==true){
			ack=2;//ͬʱ����3344
		}else{
			ack=3;//����Ҫ����
		}
		return ack;
	}
	/**
	 * �жϲ�������������
	 **/
	public int judgeLengthType(Msg msg) {
		if(!isContainsTransLocal(msg)){//��������������û�
		    return LengthType.NOLOCAL;
		}else if(isOnlyLocal(msg)){//ֻ�б����û�
			return LengthType.ONLYLOCAL;  
		}
		return -1;
	}
    /**
     * �жϲ������������Ƿ�������ط��Ͷ��û�
     **/
	private boolean isContainsTransLocal(Msg msg) {
		List<UserMsg> userList = msg.getUserMsg();
		for (UserMsg user :userList) { //�����ԴIPΪ�����û�����true
			if(MsgConstant.IPLocalIn.equals(user.getIpSource())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * �жϲ����������Ƿ�ֻ���������û�
	 **/
	private boolean isOnlyLocal(Msg msg){
		List<UserMsg> userList = msg.getUserMsg();
		Boolean flag1=false;
		Boolean flag2=false;
		for (UserMsg user :userList) { 
			if(MsgConstant.IPLocalIn.equals(user.getIpSource())){//���ڱ��ط��Ͷ�
	            flag1=true; 
				continue;
			}
			if(!user.getIpDes2().equals("0")){//�����ڷǱ��ط��Ͷˣ��벨���������û�Ҫ�뱾�ؽ��ն�ͨ�ų�ͻ��
				flag2=true;
			}
		}
		if(flag1==true&&flag2==false){  //ֻ��һ���û����뱾�ؽ���
		  return true;
		} 
		  return false;
	}
/*****************���һ*************/
	/**
	 * һ�������������û���Ŀ�ĵ�ַ����(�ޱ����û�ʱ)(������һ)
	 **/
	public  int LengthDes1(Msg msg){
		List<UserMsg> userList = msg.getUserMsg();
		int ack=0;
		for (UserMsg user :userList){
			
			if(user.getIpDes1().equals(MsgConstant.IP1)&&user.getIpDes2().equals(MsgConstant.IP2)){//Q4,S4�������Ŀ��IP1��D4�����Ŀ��IP2
				ack=1;
				break;
			}else if(user.getIpDes1().equals(MsgConstant.IP2)&&user.getIpDes2().equals(MsgConstant.IP1)){//Q4,S4�������Ŀ��IP2��D4����Ŀ��IP1
				ack=2;
				break;
			}else if(user.getIpDes1().equals(MsgConstant.IPLocalOut)&&user.getIpDes2().equals("0")){//Q4,S4,D4���µ�����
				ack=3;
				break;
			}else if(user.getIpDes1().equals(MsgConstant.IPLocalOut)&&user.getIpDes2().equals(MsgConstant.IP1)){//Q4,S4�µ����أ�D4��Ŀ��IP1
				ack=4;
				break;
			}else if(user.getIpDes1().equals(MsgConstant.IPLocalOut)&&user.getIpDes2().equals(MsgConstant.IP2)){//Q4,S4�µ����أ�D4��Ŀ��IP2
				ack=5;
				break;
			}else if(user.getIpDes1().equals(MsgConstant.IP1)&&user.getIpDes2().equals(MsgConstant.IPLocalOut)){//Q4,S4ȥ��IP1��D4���ﱾ��
				ack=6;
				break;
			}else if(user.getIpDes1().equals(MsgConstant.IP2)&&user.getIpDes2().equals(MsgConstant.IPLocalOut)){//Q4,S4ȥ��IP2��D4���ﱾ��
				ack=7;
                break;				
			}
		}
		return ack;
	}
	/**
	 * �������һ��LengthDes1��������ֵ���ɶ�Ӧ��command3
	 **/
	 public  String getCommandFrom1(int ack){
		 String command3="";
		 switch (ack) {
		case 1://Q4,S4�������Ŀ��IP1��D4�����Ŀ��IP2
			command3=OXCSetupPara.LENGTH1_L_1_IN;
			break;
        case 2://Q4,S4�������Ŀ��IP2��D4����Ŀ��IP1
        	command3=OXCSetupPara.LENGTH1_L_2_IN;
			break;
        case 3:	//Q4,S4,D4���µ�����
        	command3=OXCSetupPara.LENGTH1_L_3_IN;
        	break; 
        case 4:	//Q4,S4�µ����أ�D4��Ŀ��IP1
        	command3=OXCSetupPara.LENGTH1_L_4_IN;
		    break;
        case 5: //Q4,S4�µ����أ�D4��Ŀ��IP2
        	command3=OXCSetupPara.LENGTH1_L_5_IN;
		    break;  
        case 6://Q4,S4ȥ��IP1��D4���ﱾ��
        	command3=OXCSetupPara.LENGTH1_L_6_IN;
        	break;
        case 7://Q4,S4ȥ��IP2��D4���ﱾ��
        	command3=OXCSetupPara.LENGTH1_L_7_IN;
        	break;
        default:
			break;
		}
		return command3;
	 }
	 
	 
 
     /**
	 * һ�������������û���Ŀ�ĵ�ַ����(ֻ�б����û�ʱ)(������2)
	 **/
	public  int LengthDes2(Msg msg){
		List<UserMsg> userList = msg.getUserMsg();
		int ack=0;
		for (UserMsg user :userList){
			if(MsgConstant.IPLocalIn.equals(user.getIpSource())){
				if(user.getIpDes1().equals(MsgConstant.IPLocalOut)&&user.getIpDes2().equals("0")){//���û��뱾�ؽ����û����н���
					ack=1;
					break;
				}else if(user.getIpDes1().equals(MsgConstant.IP1)&&user.getIpDes2().equals(MsgConstant.IP2)){//�����û�Q4,S4ȥ��IP1,D4ȥ��IP2
					ack=2;
					break;
				}else if(user.getIpDes1().equals(MsgConstant.IP1)&&user.getIpDes2().equals(MsgConstant.IPLocalOut)){//�����û�Q4,S4ȥ��IP1,D4ȥ������
					ack=3;
					break;
				}else if(user.getIpDes1().equals(MsgConstant.IP2)&&user.getIpDes2().equals(MsgConstant.IP1)){//�����û�Q4,S4ȥ��IP2,D4ȥ��IP1
					ack=4;
					break;
				}else if(user.getIpDes1().equals(MsgConstant.IP2)&&user.getIpDes2().equals(MsgConstant.IPLocalOut)){//�����û�Q4,S4ȥ��IP2,D4ȥ������
					ack=5;
					break;
				}else if(user.getIpDes1().equals(MsgConstant.IPLocalOut)&&user.getIpDes2().equals(MsgConstant.IP1)){//�����û�Q4,S4ȥ������,D4ȥ��IP1
					ack=6;
					break;
				}else if(user.getIpDes1().equals(MsgConstant.IPLocalOut)&&user.getIpDes2().equals(MsgConstant.IP2)){//�����û�Q4,S4ȥ������,D4ȥ��IP2
					ack=7;
	                break;				
				}else if(user.getIpDes1().equals(MsgConstant.IP1)&&user.getIpDes2().equals("0")){//�����û�Q4,S4,D4��ȥ��IP1
					ack=8;
					break;
				}
			}			
		}
		return ack;
	}
	/**
	 * �������2��LengthDes1��������ֵ���ɶ�Ӧ��command3
	 **/
	 public  String getCommandFrom2(int ack){
		 String command3="";
		 switch (ack) {
		case 1://�����û��뱾�ؽ����û����н���
			command3=OXCSetupPara.LENGTH2_L_1_IN;
			break;
        case 2://�����û�Q4,S4ȥ��IP1,D4ȥ��IP2
        	command3=OXCSetupPara.LENGTH2_L_2_IN;
			break;
        case 3://�����û�Q4,S4ȥ��IP1,D4ȥ������
        	command3=OXCSetupPara.LENGTH2_L_3_IN;
        	break; 
        case 4:	//�����û�Q4,S4ȥ��IP2,D4ȥ��IP1
        	command3=OXCSetupPara.LENGTH2_L_4_IN;
		    break;
        case 5: //�����û�Q4,S4ȥ��IP2,D4ȥ������
        	command3=OXCSetupPara.LENGTH2_L_5_IN;
		    break;  
        case 6://�����û�Q4,S4ȥ������,D4ȥ��IP1
        	command3=OXCSetupPara.LENGTH2_L_6_IN;
        	break;
        case 7://�����û�Q4,S4ȥ������,D4ȥ��IP2
        	command3=OXCSetupPara.LENGTH2_L_7_IN;
        	break;
        case 8://�����û�Q4,S4,D4��ȥ��IP1
        	command3=OXCSetupPara.LENGTH2_L_8_IN;
        	break;
        default:
			break;
		}
		return command3;
	 }
	 

}
