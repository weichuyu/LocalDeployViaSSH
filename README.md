# LocalDeployViaSSH
一、前言：  
现在已经有jenkis，不过当你因某些原因无法使用jenkis，却又要操作十几台服务器应用的更新上线，本代码片段可以实现自动化上线。  
虽然jenkis的图像化界面很友好，但毕竟你还是要坐在电脑前去点它的图像化界面，但如果使用我这段代码，事先将上线文件准备好（sql脚本你还是自己执行吧）
然后用定时任务，启动本代码编译后的jar包，之后你只需要估计下时间，到点去检查上线结果和看下启动日志就好了。  
非常适用于测试期间频繁修改uat环境时的部署工作。  

二、这个代码片段的主要通过ssh连接远程服务器实现以下功能：  
（一）杀指定进程  
（二）删除指定日志文件（不会检查该路径是否是日志）  
（三）备份指定路径到备份路径，备份文件名为“日期时分.tar”（需要远程服务器安装了tar命令）  
（四）删除指定代码文件（可以是文件夹，也可以是war或者jar）  
（五）上传本地文件到指定路径（增量的class文件或者完整的war或者jar，反正是本次上线要上的东西）  
（六）运行指定脚本（启动脚本，目前只支持一句话，如果启动脚本很长，在远处服务器上写个sh脚本）  
（七）检查进程号是否存在  

三、使用方法  
（一）在process.AutoDeployExecutorFactory新写一个方法 模仿getInstanceTest  
（1）host：远程服务器ip地址；username：远程服务器用户名；password：远程服务器密码。  
（2）fileSeparator，不用改，不过必须写。  
（3）logsPath：要删除的日志路径，可以是单个文件也可以是文件夹（因为删除命令是rm -rf），  
如果不需要就填空字符串，但必须填。  
（4）deletePath：要删除的代码路径，可以是单个文件也可以是文件夹  
（如果不需要就填空字符串，但必须填）。  
（5）appPath，appBackUpPath：要备份的代码路径，备份后文件的路径，  
如果不需要备份，两个值都设置为空字符串，必须设置  
（备份用的命令是"tar -cvf "+appBackUpPath2+this.getTimeStr1()+".tar"+" "+appPath;）  
（6）localPath，remotePath：将本地文件用sftp传到远程指定路径上  
（或文件夹，增量上传class文件时让本地的文件夹和远程文件夹结构保持一致）  
（7）processUniqueWord：确认进程唯一pid的脚本，必须只返回一个唯一的pid  
（代码没有检查这句话是否返回正确的结果，需要自行保证）  
比如ps -ef | grep java | grep -v 'grep java' | awk '{print $2}'  
（8）startCommandPath，startCommand：启动脚本所在路径，启动脚本命令  
（二）在process.Processor中添加类似factory.getInstanceTest().run();的语句，并执行。  
（三）获得结果。  

五、注意事项  
（一）请先在测试环境测试过再用于生产。  
（二）建议不要用root（为你找想）。  
（三）应用是否启动，还是要看具体日志，AutoGetLogExecutorFactory是下载远程日志到本地的模板。  
