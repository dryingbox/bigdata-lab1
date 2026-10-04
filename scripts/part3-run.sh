#!/usr/bin/env bash
# 运行第三部分 3 个 MapReduce 程序，输出写入 /tmp/lab1-out/part3.log
set -u
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
export HADOOP_HOME="${HADOOP_HOME:-/home/hadoop/hadoop-3.3.6}"
export PATH="$PATH:$HADOOP_HOME/bin"
export HADOOP_CONF_DIR="${HADOOP_CONF_DIR:-$HADOOP_HOME/etc/hadoop}"

LOG=/tmp/lab1-out/part3.log
mkdir -p /tmp/lab1-out
exec > >(tee -a "$LOG") 2>&1

JAR="$ROOT/part3/lab1-part3.jar"
[ -f "$JAR" ] || { echo "缺少 $JAR，请先运行 scripts/part3-build.sh"; exit 1; }

set -x
hdfs dfs -rm -r -f /lab1
hdfs dfs -mkdir -p /lab1/dedup /lab1/sort /lab1/gp
hdfs dfs -put -f "$ROOT"/part3/data/dedup/*.txt /lab1/dedup/
hdfs dfs -put -f "$ROOT"/part3/data/sort/*.txt /lab1/sort/
hdfs dfs -put -f "$ROOT"/part3/data/grandparent/*.txt /lab1/gp/
hdfs dfs -ls -R /lab1

echo "### 1) 合并去重 Dedup"
hadoop jar "$JAR" Dedup /lab1/dedup /lab1/out/dedup
echo "----- Dedup 输出 -----"
hdfs dfs -cat /lab1/out/dedup/part-r-*

echo "### 2) 整数排序 Sort"
hadoop jar "$JAR" Sort /lab1/sort /lab1/out/sort
echo "----- Sort 输出 -----"
hdfs dfs -cat /lab1/out/sort/part-r-*

echo "### 3) 祖孙关系挖掘 GrandParent"
hadoop jar "$JAR" GrandParent /lab1/gp /lab1/out/gp
echo "----- GrandParent 输出 -----"
hdfs dfs -cat /lab1/out/gp/part-r-*

echo "第三部分运行结束"
