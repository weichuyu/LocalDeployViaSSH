package process;


public class AutoGetLogExecutorFactory{

	public AutoGetLogExecutor getInstanceTestLog(){
		AutoGetLogExecutor executor = new AutoGetLogExecutor();
		{
			executor.host = "XXX.XXX.XXX.XXX";
			executor.username = "root";
			executor.password = "XXXXXXXX";
			executor.fileSeparator = "/";
			executor.downloadFileName = "spring.log";
			executor.downloadFilePath = "/root/logs/CronExecute";
			executor.localPath = "F:\\DEVOUTPUT";
		}
		return executor;
	}
}
