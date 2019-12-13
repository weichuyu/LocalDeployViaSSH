package process;

import com.jcraft.jsch.SftpProgressMonitor;

public class MyProgressMonitor implements SftpProgressMonitor {
	private long transfered;
	private String fileIdentity;
	public MyProgressMonitor(String name){
		fileIdentity = name;
	}
	@Override
	public boolean count(long  count) {
		// TODO Auto-generated method stub
//		transfered = transfered + count;
//        if(transfered<1024)
//        {
//               System.out.println("Currently transferred total size: " + transfered + " bytes");
//        }
//        if ((transfered> 1024) && (transfered<1048576))
//        {
//               System.out.println("Currently transferred total size: " + (transfered/1024) + "K bytes");
//        }
//        else
//        {
//               System.out.println("Currently transferred total size: " +( transfered/1024/1024) + "M bytes");
//        }
        return true;
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub
		System.out.println(fileIdentity+"--Transferring done.");
	}

	@Override
	public void init(int arg0, String arg1, String arg2, long arg3) {
		// TODO Auto-generated method stub
		System.out.println(fileIdentity+"--Transferring begin.");
	}

}
