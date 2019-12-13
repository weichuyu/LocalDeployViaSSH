package process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import util.SFTPUtil;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class AutoDeployExecutor implements Runnable{
	static Logger logger  =  Logger.getLogger(AutoDeployExecutor. class );
	private int port = 22;
	public String host = "XXX.XXX.XXX.XXX";
	public String username = "root";
	public String password = "XXXXXXXXXX";
	public String fileSeparator = "/";
	public String logsPath = "";
	public String appPath = "";
	public String appBackUpPath = "";
	public String deletePath = "";
	public String processUniqueWord = "";
	public String localPath = "";
	public String remotePath = "";
	public String startCommandPath = "";
	public String startCommand = "";
	
	@Override
	public void run() {
		Session session = null;
		try{
			//连接
			session = SFTPUtil.connect(host, port, username, password);
			//停服务
			if(processUniqueWord!=null && processUniqueWord.length()>0){
				String command = processUniqueWord;
				List<String> pids = execCommand(session,command,true);
				if(pids == null){
					throw new Exception("进程号查询为null");
				}else if(pids.size()<=0){
					logger.info("进程号查询到0个，跳过杀进程操作，继续执行");
				}else if(pids.size()==1){
					logger.info("进程号查询到1个，开始杀进程操作");
					String pid = pids.get(0);
					if(pid!=null && pid.length()>0){
						String command2 = "kill -9 "+ pids.get(0).trim();
						logger.info("执行shell:"+command2);
						execCommand(session,command2,false);
					}else{
						logger.info("进程号为:"+pid+":跳过杀进程操作");
					}
				}else{
					throw new Exception("进程号查询到多个，请手动操作");
				}
			}
			//删日志
			if(logsPath != null && logsPath.length()>0){
				String command = "rm -rf "+logsPath;
				execCommand(session,command,false);
				logger.info("日志删除完成");
			}
			//备份文件
			if(appPath!=null && appPath.length()>0 && appBackUpPath!=null && appBackUpPath.length()>0){
				String appBackUpPath2 = appBackUpPath;
				if(!appBackUpPath.endsWith(fileSeparator)){
					appBackUpPath2 = appBackUpPath+fileSeparator;
				}
				String command = "tar -cvf "+appBackUpPath2+this.getTimeStr1()+".tar"+" "+appPath;
				execCommand(session,command,false);
				logger.info("备份完成");
			}
			//删文件
			if(deletePath != null && deletePath.length()>0){
				String command = "rm -rf "+deletePath;
				execCommand(session,command,false);
				logger.info("文件删除完成");
			}
			//上传文件
			if(localPath!=null && localPath.length()>0 && remotePath!=null && remotePath.length()>0){
				logger.info("上传文件开始");
				upload(session,localPath,remotePath);
				logger.info("上传文件结束");
			}
			//启服务
			if(startCommand!=null && startCommand.length()>0){
				String command = startCommandPath + "\n"+ startCommand+"\nexit";
				logger.info("重启服务");				
				//execCommand(session,command,true);
				execShell(session,command,false);
			}
			//检查服务是否开启
			if(processUniqueWord!=null && processUniqueWord.length()>0){
				String command = processUniqueWord;
				List<String> pids = execCommand(session,command,true);
				if(pids == null){
					logger.info("进程号查询为null，请手动确认系统是否重启");
				}else if(pids.size()<=0){
					logger.info("进程号查询为0，请手动确认系统是否重启");
				}else if(pids.size()==1){
					logger.info("进程号检查为:"+pids.get(0));
				}else{
					logger.info("进程号查询超过1，请手动确认系统是否需要重启");
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			if(session != null)session.disconnect();
		}
	}
	private List<String> execCommand(Session session,String command,boolean flag) throws Exception{
		if(command==null || command.length()<=0){
			return null;
		}
		List<String> result = new ArrayList<String>();
		Channel channel = null;
		BufferedReader reader = null;
		try{
			channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			channel.connect();
			InputStream in = channel.getInputStream();
			reader = new BufferedReader(new InputStreamReader(in));
			String buf = null;
			while ((buf = reader.readLine()) != null) {
				if(flag){
					logger.info(buf);
				}
				result.add(buf);
			}
		}catch(Exception e){
			throw e;
		}finally{
			try {
                reader.close();
            } catch (IOException e) {  
                logger.error(e.getMessage(),e);
            }
			if(channel != null)channel.disconnect();
		}
		return result;
	}
	private void upload(Session session,String src,String dest){
		File file = new File(src);
		if(!file.exists()){
			return;
		}
		Channel channel = null;
		try{
			channel = (Channel) session.openChannel("sftp");
			channel.connect(10000000);
			ChannelSftp sftp = (ChannelSftp) channel;
			sftp.cd(dest);
			
			copyFile(sftp, file, sftp.pwd());
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			channel.disconnect();
		}
	}
	private void copyFile(ChannelSftp sftp, File file, String pwd){
		if (file.isDirectory()) {
            File[] list = file.listFiles();
            try {
                try {
                    String fileName = file.getName();
                    sftp.cd(pwd);
                    logger.info("正在创建目录:" + sftp.pwd() + fileSeparator + fileName);
                    sftp.mkdir(fileName);
                    logger.info("目录创建成功:" + sftp.pwd() + fileSeparator + fileName);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                pwd = pwd + fileSeparator + file.getName();
                try {

                    sftp.cd(file.getName());
                } catch (SftpException e) {
                    // TODO: handle exception
                	logger.error(e.getMessage(),e);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
            	logger.error(e.getMessage(),e);
            }
            for (int i = 0; i < list.length; i++) {
                copyFile(sftp, list[i], pwd);
            }
        } else {

            try {
                sftp.cd(pwd);

            } catch (SftpException e1) {
                // TODO Auto-generated catch block
            	logger.error(e1.getMessage(),e1);
            }
            logger.info("正在复制文件:" + file.getAbsolutePath());
            InputStream instream = null;
            OutputStream outstream = null;
            try {
                outstream = sftp.put(file.getName());
                instream = new FileInputStream(file);

                byte b[] = new byte[1024];
                int n;
                try {
                    while ((n = instream.read(b)) != -1) {
                        outstream.write(b, 0, n);
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                	logger.error(e.getMessage(),e);
                }

            } catch (SftpException e) {
                // TODO Auto-generated catch block
            	logger.error(e.getMessage(),e);
            } catch (IOException e) {
                // TODO Auto-generated catch block
            	logger.error(e.getMessage(),e);
            } finally {
                try {
                    outstream.flush();
                    outstream.close();
                    instream.close();

                } catch (Exception e2) {
                    // TODO: handle exception
                	logger.error(e2.getMessage(),e2);
                }
            }
        }
    }
	private List<String> execShell(Session session,String command,boolean flag) throws Exception{
		if(command==null || command.length()<=0){
			return null;
		}
		List<String> result = new ArrayList<String>();
		Channel channel = null;
		OutputStream toServer = null;
		BufferedReader reader = null;
		try{
			channel = session.openChannel("shell");
			
			channel.connect();
 
			toServer = channel.getOutputStream();
			toServer.write((command + "\r\n").getBytes());
			toServer.flush();
			
			InputStream in = channel.getInputStream();
			reader = new BufferedReader(new InputStreamReader(in));
			String buf = null;
			while ((buf = reader.readLine()) != null) {
				if(flag){
					logger.info(buf);
				}
				result.add(buf);
			}
		}catch(Exception e){
			throw e;
		}finally{
			try {
                reader.close();  
            } catch (IOException e) {  
                logger.error(e.getMessage(),e);
            }
			try {
				toServer.close();
            } catch (IOException e) {  
                logger.error(e.getMessage(),e);
            }
			
			if(channel != null)channel.disconnect();
		}
		return result;
	}

	public static String getTimeStr1() {
		Calendar instance = Calendar.getInstance();
		int year = instance.get(instance.YEAR);
		int month = instance.get(instance.MARCH);
		int date = instance.get(instance.DAY_OF_MONTH);
		int hour = instance.get(instance.HOUR_OF_DAY);
		int minute = instance.get(instance.MINUTE);
		int secord = instance.get(instance.SECOND);
		String timeStr1 = year + "" + month + "" + date + "" + hour + "" + minute;
		return timeStr1;
	}
}
