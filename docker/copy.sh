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

echo "begin copy zhao-modules-system "
cp ../zhao-modules/zhao-system/target/zhao-modules-system.jar ./zhao/modules/system/jar

