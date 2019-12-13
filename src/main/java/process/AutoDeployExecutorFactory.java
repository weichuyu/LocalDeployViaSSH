package process;

public class AutoDeployExecutorFactory {
	/**
	 *
	 * @return
	 */
	public AutoDeployExecutor getInstanceTest(){
		AutoDeployExecutor executor = new AutoDeployExecutor();
		{
			//风控平台
			executor.host = "XXX.XXX.XXX.XXX";
			executor.username = "root";
			executor.password = "XXXXXXXX";
			executor.fileSeparator = "/";
			executor.logsPath = "/root/logs/CronExecute/spring.log";
			executor.deletePath = "/root/cronExecuteApp/CronExecute-1.0-SNAPSHOT.jar";
			//executor.deletePath = "";
			executor.appPath = "/root/cronExecuteApp/CronExecute-1.0-SNAPSHOT.jar";
			executor.appBackUpPath = "/root/backup";
			executor.processUniqueWord = "ps -ef | grep CronExecute | grep -v 'grep CronExecute' | awk '{print $2}'";
			executor.localPath = "F:/mvnrepo/cn/yu/tools/CronExecute/1.0-SNAPSHOT/CronExecute-1.0-SNAPSHOT.jar";
			//executor.localPath = "";
			executor.remotePath = "/root/cronExecuteApp";
			executor.startCommandPath = "cd /root/cronExecuteApp/";
			executor.startCommand = "nohup java -server -Djava.compiler=NONE -Xms256M -Xmx512M -jar CronExecute-1.0-SNAPSHOT.jar&";
		}
		return executor;
	}
}
