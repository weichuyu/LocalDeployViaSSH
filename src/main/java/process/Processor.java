package process;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.net.URL;

public class Processor {
	static Logger logger  =  Logger.getLogger(Processor. class );
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		initParam();
		doSth(args);
		logger.info("FIN");
	}
	private static void initParam(){
		URL url = Processor.class.getResource("/log4j.properties");
		PropertyConfigurator.configure(url);
	}
	
	private static void doSth(String[] args) throws Exception{
		AutoDeployExecutorFactory factory = new AutoDeployExecutorFactory();
		AutoGetLogExecutorFactory factorylog = new AutoGetLogExecutorFactory();
		//
		factory.getInstanceTest().run();
		Thread.sleep(10000);
		factorylog.getInstanceTestLog().run();
	}
}
