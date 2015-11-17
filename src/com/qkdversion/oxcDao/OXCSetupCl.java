package com.qkdversion.oxcdao;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.SocketException;
import org.apache.commons.net.telnet.TelnetClient;



import com.qkdversion.oxc.constant.OXCAckPara;
/**
 * 功能：OXC控制层
 * @author：ljj
 **/

public class OXCSetupCl {
	private TelnetClient telClient;// telnet客户端
	private InputStream is;// telnet输入流
	private OutputStream os;// telnet输出流

	public OXCSetupCl() {
		telClient = new TelnetClient("ANSI");
	}

	/**
	 * 创建telnet连接 若连接成功，返回OXCAckPara.CONNECT_OK 若出现异常，返回OXCAckPara.CONNECT_FAIL
	 **/
	public int connectToTelnet(String OXC_IP) {
		try {
			telClient.connect(OXC_IP, 23);// 进行连接
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
	 * 向OXC发送配置指令及配置参数 获取OXC响应的字符串
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
	 * 获取OXC响应信息(因为返回信息均只有一行，所以只读一次就可以实现) 返回获取到的字符串
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
	 * 关闭流
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
	 * 断开telnet连接
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
