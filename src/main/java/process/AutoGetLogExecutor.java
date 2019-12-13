package process;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.log4j.Logger;

import util.SFTPUtil;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class AutoGetLogExecutor implements Runnable {
	static Logger logger = Logger.getLogger(AutoGetLogExecutor.class);
	private int port = 22;
	public String host = "XXX.XXX.XXX.XXX";
	public String username = "root";
	public String password = "XXXXXXXXXXXX";
	public String fileSeparator = "/";
	public String localPath = "";
	public String downloadFilePath = "";
	public String downloadFileName = "";

	@Override
	public void run() {
		Session session = null;
		Channel channel = null;  
        ChannelSftp sftp = null;
		try {
			// 连接
			session = SFTPUtil.connect(host, port, username, password);
			// 下载文件
			try {  
	            // 创建sftp通信通道  
	            channel = (Channel) session.openChannel("sftp");  
	            channel.connect(100000);  
	            logger.info("Channel created to " + host + ".");  
	            sftp = (ChannelSftp) channel;  
	            download(sftp, downloadFilePath, localPath, downloadFileName);
	            logger.info(localPath+File.separator+downloadFileName+"下载完成");
	        } catch (JSchException e) {  
	            logger.error("exception when channel create.", e);  
	        }  
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {  
	            if (sftp != null) {  
	                if (sftp.isConnected()) {  
	                    sftp.disconnect();  
	                } else if (sftp.isClosed()) {  
	                    logger.info("sftp is closed already");  
	                }  
	                if (null != sftp.getSession()) {  
	                    sftp.getSession().disconnect();  
	                }  
	            }  
	        } catch (JSchException e) {  
	            // Ignore  
	        }  
//			if (session != null)
//				session.disconnect();
		}
	}
	public static void download(ChannelSftp sftp, String srcPath, String saveFile, String srcfile) {  
        try {  
            sftp.cd(srcPath);  
            File file = new File(saveFile);  
            if (file.isDirectory()) {  
                sftp.get(srcfile, new FileOutputStream(file + File.separator + srcfile));
            } else {  
                sftp.get(srcfile, new FileOutputStream(file));  
            }  
        } catch (Exception e) {  
            logger.error("download file: {} error "+ srcPath + File.separator + srcfile, e);  
        }  
    } 
}
