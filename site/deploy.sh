#!/bin/bash

HOST=gmock.org
ENV=production

function remote {
 ssh $HOST $1
}

mkdir -p war
mv *.war war/

grails -Dgrails.env=$ENV war

DATE=`date +'%Y%m%d-%H%M'`
GMOCK_PATH=gmock-${DATE}
mv gmock.war ${GMOCK_PATH}.war




remote "mkdir /www/${GMOCK_PATH}"
scp ${GMOCK_PATH}.war $HOST:/www/${GMOCK_PATH}
remote "unzip /www/${GMOCK_PATH}/${GMOCK_PATH}.war -d /www/${GMOCK_PATH}"
remote "rm /www/gmock"
remote "ln -s /www/${GMOCK_PATH} /www/gmock"
remote "sudo /etc/init.d/tomcat5 restart" &







