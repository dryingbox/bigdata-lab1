#!/usr/bin/env bash
# 编译第三部分 MapReduce 程序并打包
set -e
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
export HADOOP_HOME="${HADOOP_HOME:-/home/hadoop/hadoop-3.3.6}"
export PATH="$PATH:$HADOOP_HOME/bin"
export HADOOP_CONF_DIR="${HADOOP_CONF_DIR:-$HADOOP_HOME/etc/hadoop}"

rm -rf "$ROOT/part3/build"
mkdir -p "$ROOT/part3/build"
javac -classpath "$(hadoop classpath)" -d "$ROOT/part3/build" "$ROOT"/part3/*.java
jar cf "$ROOT/part3/lab1-part3.jar" -C "$ROOT/part3/build" .
echo "打包完成: $ROOT/part3/lab1-part3.jar"
