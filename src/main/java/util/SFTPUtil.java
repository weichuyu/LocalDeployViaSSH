package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SFTPUtil {
private static Log log = LogFactory.getLog(SFTPUtil.class);
	
	/**
	 * 连接sftp服务器
	 * @param host 远程主机ip地址
	 * @param port sftp连接端口，null 时为默认端口
	 * @param user 用户名
	 * @param password 密码
	 * @return
	 * @throws JSchException 
	 */
	public static Session connect(String host, Integer port, String user, String password) throws JSchException{
		Session session = null;
		try {
			JSch jsch = new JSch();
			if(port != null){
				session = jsch.getSession(user, host, port.intValue());
			}else{
				session = jsch.getSession(user, host);
			}
			session.setPassword(password);
			//设置第一次登陆的时候提示，可选值:(ask | yes | no)
			session.setConfig("StrictHostKeyChecking", "no");
			//30秒连接超时
			session.connect(30000);
		} catch (JSchException e) {
			e.printStackTrace();
			System.out.println("SFTPUitl 获取连接发生错误");
			throw e;
		}
		return session;
	}
	
	/** 
     * 执行相关的命令 
     */  
    public static void execCmd(Session session,String command) {  
        BufferedReader reader = null;
        Channel channel = null;
		try {
			channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);

			channel.connect();
			InputStream in = channel.getInputStream();
			reader = new BufferedReader(new InputStreamReader(in));
			String buf = null;
			while ((buf = reader.readLine()) != null) {
				System.out.println(buf);
			}
        } catch (IOException e) {  
            e.printStackTrace();  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                reader.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }
            channel.disconnect();
        }  
    }
}
