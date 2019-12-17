#!/bin/sh
echo "========================================================================="
echo ""
echo " Auto CheckOut Project"
echo ""
echo " Auto Maven Build War"
echo ""
echo " Auto Upload War"
echo ""
echo " Auto Run Jboss"
echo ""
echo "========================================================================="
#log="/dev/null"
log="/home/ods/autoDeploy.log"
#remote 
remote_user="XXXXXX"
remote_pwd="XXXXXX"
remote_ip="XXX.XXX.XXX.XXX"
remote_war_path="/XXX/deployments/"
remote_jboss_standalone="/XXX/jboss-as-7.1.1.Final/bin/standalone.sh"
query_process="pgrep -n jboss"
#Your Project
workspace="/home/XXX/svncheckout/"
projectfoldername="XXX"
projecttargetname="XXX-web/target"
warname="XXX.war"
svn_ods="http://XXX.XXX.XXX.XXX/XXX/code/trunk/XXX"
mavenbuildphase="simulation"
#base
svn_base="http://XXX.XXX.XXX.XXX/XXX/code/trunk/base"
base_warname="XXX.jar"
base_projectfoldername="base"
base_projecttargetname="base-web/target"
echo "remoteip=${remote_ip}"
echo "remoteuser=${remote_user},remotepassword=${remote_pwd}"
if [ ! -x "${workspace}" ]; then
	mkdir "${workspace}"
else
	rm -rf "${workspace}"
	mkdir "${workspace}"
fi
#check base
cd "${workspace}"
echo "Checking Out From ${svn_base} Please Wait"
svn checkout "${svn_base}" > "${log}"
if [ ! -x "${workspace}${base_projectfoldername}" ]; then
        echo -e "Check Project Base From SVN [FAIL]"
        exit
else
        echo -e "Check Project Base From SVN [SUCCESS]"
fi
cd "${workspace}"${base_projectfoldername}
echo "Maven Building Project Please Wait"
mvn clean install -DskipTests -P"${mavenbuildphase}" > "${log}"
if [ ! -f "${workspace}${base_projectfoldername}/${base_projecttargetname}/${base_warname}" ]; then
        echo -e "Maven Build Project Base [FAIL]"
        exit
else
        echo -e "Maven Build Project Base [SUCCESS]"
fi
#check XXX
cd "${workspace}"
echo "Checking Out From ${svn_ods} Please Wait"
svn checkout "${svn_ods}" > "${log}"
if [ ! -x "${workspace}${projectfoldername}" ]; then
	echo -e "Check Project From SVN [FAIL]"
        exit
else
	echo -e "Check Project From SVN [SUCCESS]"
fi
cd "${workspace}"${projectfoldername}
echo "Maven Building Project Please Wait"
mvn clean install -DskipTests -P"${mavenbuildphase}" > "${log}"
if [ ! -f "${workspace}${projectfoldername}/${projecttargetname}/${warname}" ]; then
        echo -e "Maven Build Project [FAIL]${workspace}${projectfoldername}/${projecttargetname}/${warname}"
        exit
else
        echo -e "Maven Build Project [SUCCESS]"
fi
echo "Please ensure Remote Jboss is Closed!!!!!"
#remote deploy
#shutdown remote jboss
#echo "kill remote jboss"
#echo "ssh {remote_user}@${remote_ip}" "kill -9 ps -ef | grep jboss | grep -v grep | awk '{print " "$""2}'" 
#ssh "${remote_user}@${remote_ip}" "kill -9 ps -ef | grep jboss | grep -v grep | awk '{print " "$""2}'"
echo "Enter the remote Host password to query process..."
process_id=`ssh ${remote_user}@${remote_ip} ${query_process}`
if [  -n "${process_id}" ]; then
        echo "Enter the remote host password to kill process..."
        ssh ${remote_user}@${remote_ip} kill -9 ${process_id}
        echo "Process has been killed"
else
        echo "Process is not running"
fi
#ssh "${remote_user}@${remote_ip}""  ps -ef|grep /u05/jboss/jboss-as-7.1.1.Final/standalone/|grep -v grep|awk '{print " "$""2}'|xargs kill -9"
#remove remote war
#copy war to remote
echo "copy war"
scp -P 22 "${workspace}${projectfoldername}/${projecttargetname}/${warname}" "${remote_user}@${remote_ip}:${remote_war_path}"
#start remote jboss
echo "start jboss"
ssh "${remote_user}"@"${remote_ip}" " nohup ${remote_jboss_standalone}" "&"
echo "finished"
