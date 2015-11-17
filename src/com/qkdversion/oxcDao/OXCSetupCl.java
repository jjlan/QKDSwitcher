package com.qkdversion.oxcdao;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.SocketException;
import org.apache.commons.net.telnet.TelnetClient;



import com.qkdversion.oxc.constant.OXCAckPara;
/**
 * ���ܣ�OXC���Ʋ�
 * @author��ljj
 **/

public class OXCSetupCl {
	private TelnetClient telClient;// telnet�ͻ���
	private InputStream is;// telnet������
	private OutputStream os;// telnet�����

	public OXCSetupCl() {
		telClient = new TelnetClient("ANSI");
	}

	/**
	 * ����telnet���� �����ӳɹ�������OXCAckPara.CONNECT_OK �������쳣������OXCAckPara.CONNECT_FAIL
	 **/
	public int connectToTelnet(String OXC_IP) {
		try {
			telClient.connect(OXC_IP, 23);// ��������
			is = telClient.getInputStream();
			os = telClient.getOutputStream();
		} catch (SocketException e) {
			e.printStackTrace();
			return OXCAckPara.CONNECT_FAIL;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return OXCAckPara.CONNECT_FAIL;
		}
		return OXCAckPara.CONNECT_OK;
	}

	/**
	 * ��OXC��������ָ����ò��� ��ȡOXC��Ӧ���ַ���
	 */
	public void sendCommend(String comm_Type) {
		String sendStr = comm_Type + "\n";
		try {
			os.write(sendStr.getBytes());
			os.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ��ȡOXC��Ӧ��Ϣ(��Ϊ������Ϣ��ֻ��һ�У�����ֻ��һ�ξͿ���ʵ��) ���ػ�ȡ�����ַ���
	 */
	public String getOXCAckMsg() {
		String str = null;
		try {
			byte[] bytes = new byte[1024];
			int len = 0;
			len = is.read(bytes);
			str = new String(bytes, 0, len);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * �ر���
	 */
	public void close() {
		try {

			is.close();
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * �Ͽ�telnet����
	 */
	public void disConnectToTelnet() {
		try {
			telClient.disconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
