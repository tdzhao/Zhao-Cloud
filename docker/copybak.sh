#!/bin/sh

# 复制项目的文件到对应docker路径，便于一键生成镜像。
usage() {
	echo "Usage: sh copy.sh"
	exit 1
}


# copy sql
echo "begin copy sql "
cp ../sql/zhao.sql ./mysql/db
cp ../sql/zhao-config.sql ./mysql/db

# copy html
echo "begin copy html "
cp -r ../zhao-ui/dist/** ./nginx/html/dist


# copy jar
echo "begin copy zhao-gateway "
cp ../zhao-gateway/target/zhao-gateway.jar ./zhao/gateway/jar

echo "begin copy zhao-auth "
cp ../zhao-auth/target/zhao-auth.jar ./zhao/auth/jar

echo "begin copy zhao-visual "
cp ../zhao-visual/zhao-monitor/target/zhao-visual-monitor.jar  ./zhao/visual/monitor/jar

echo "begin copy zhao-modules-system "
cp ../zhao-modules/zhao-system/target/zhao-modules-system.jar ./zhao/modules/system/jar

echo "begin copy zhao-modules-file "
cp ../zhao-modules/zhao-file/target/zhao-modules-file.jar ./zhao/modules/file/jar

echo "begin copy zhao-modules-job "
cp ../zhao-modules/zhao-job/target/zhao-modules-job.jar ./zhao/modules/job/jar

echo "begin copy zhao-modules-gen "
cp ../zhao-modules/zhao-gen/target/zhao-modules-gen.jar ./zhao/modules/gen/jar

